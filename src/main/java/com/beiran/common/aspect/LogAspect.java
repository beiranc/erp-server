package com.beiran.common.aspect;

import com.beiran.common.annotation.LogRecord;
import com.beiran.core.system.entity.Log;
import com.beiran.core.system.service.LogService;
import com.beiran.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * 日志切面
 * FIXME: 日志信息可以抽象成一个方法
 */
@Slf4j
@Component
@Aspect
public class LogAspect {

    @Resource
    private LogService logService;

    /**
     * 记录操作所花费的时间
     */
    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    /**
     * 注解切入点
     */
    @Pointcut("@annotation(com.beiran.common.annotation.LogRecord)")
    public void logPointCut() { }

    /**
     * 配置环绕通知
     * @param joinPoint 连接点
     * @return Object
     */
    @Around("logPointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        currentTime.set(System.currentTimeMillis());
        Log userLog = new Log();
        Object result;
        common(joinPoint, userLog, false);
        // 记录日志
        result = joinPoint.proceed();
        userLog.setSpendTime(System.currentTimeMillis() - currentTime.get());
        logService.save(userLog);
        return result;
    }

    @AfterThrowing(pointcut = "logPointCut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        Log userLog = new Log();
        userLog.setSpendTime(System.currentTimeMillis() - currentTime.get());
        currentTime.remove();
        common(joinPoint, userLog, true);
        logService.save(userLog);
        log.error(" { 其他异常 } " + exception.getLocalizedMessage());
    }

    private void common(JoinPoint joinPoint, Log userLog, Boolean isThrowing) {
        // 获取 HttpServletRequest 中的信息
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        // 获取方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        // 获取方法上的注解
        LogRecord logRecord = method.getAnnotation(LogRecord.class);

        // 拼接日志所需的方法参数信息
        StringBuilder paramsInfo = new StringBuilder();
        paramsInfo.append("{");

        // 参数值
        Object[] argValues = joinPoint.getArgs();

        // 参数名
        String[] argNames = methodSignature.getParameterNames();

        // 拼接方法参数
        if (!Objects.equals(argValues, null)) {
            for (int i = 0; i < argValues.length; i++) {
                // 记录创建用户的日志时需要将密码隐藏掉
                if (Objects.equals(argNames[i], "loginPassword")) {
                    paramsInfo.append(" ").append(argNames[i]).append(": ").append("*********");
                } else {
                    paramsInfo.append(" ").append(argNames[i]).append(": ").append(argValues[i]);
                }
            }
        }

        // 从 Spring Security 的上下文中获取当前登录的用户名
        String userName = SecurityUtil.getUserName();

        userLog.setUserName(userName);
        userLog.setOperation(isThrowing ? "{ 异常操作 } : " + logRecord.value() : logRecord.value());
        userLog.setCreateTime(new Date());
        userLog.setIp(getIp(request));
        // 拼接日志所需的方法信息
        String methodInfo = joinPoint.getTarget().getClass().getName() +
                "." +
                methodSignature.getName() +
                "()";
        userLog.setMethod(methodInfo);
        userLog.setParams(paramsInfo + " }");
        userLog.setParams(Arrays.asList(joinPoint.getArgs()).toString());
    }

    /**
     * 获取 IP 地址
     */
    private static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (Objects.equals(ip, null) || Objects.equals(ip.length(), 0) || "unknown".equalsIgnoreCase(ip)) {
           ip = request.getHeader("Proxy-Client-IP");
        }
        if (Objects.equals(ip, null) || Objects.equals(ip.length(), 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (Objects.equals(ip, null) || Objects.equals(ip.length(), 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        if (Objects.equals("127.0.0.1", ip)) {
            // 获取本机真正的 IP
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }
}
