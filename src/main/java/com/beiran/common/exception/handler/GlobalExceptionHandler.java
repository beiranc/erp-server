package com.beiran.common.exception.handler;

import com.beiran.common.exception.*;
import com.beiran.common.respone.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获并处理<br>
 *     200 OK<br>
 *     201 Created<br>
 *     400 Bad Request<br>
 *     401 Unauthorized<br>
 *     403 Forbidden<br>
 *     404 Not Found<br>
 *     405 Method Not Allowed<br>
 *     500 Internal Server Error<br>
 *     502 Bad Gateway<br>
 *
 * 注意: 如果 @RestControllerAdvice 全局捕获异常无效，需要首先考虑所有的切面中是否捕获了异常并且没有抛出
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义的 ParameterException
     */
    @ExceptionHandler(value = ParameterException.class)
    public ResponseModel handleParameterException(ParameterException exception) {
        // 400 状态码
        // 是否需要添加跳转 URL 后面再判断
        return ResponseModel.error(HttpStatus.BAD_REQUEST.value(), exception.getLocalizedMessage(), exception.getStackTrace().toString(), null);
    }

    /**
     * 处理库存不足异常
     */
    @ExceptionHandler(value = StockShortageException.class)
    public ResponseModel handleStockShortageException(StockShortageException exception) {
        // 500 状态码
        return ResponseModel.error(exception.getLocalizedMessage());
    }

    /**
     * 处理 Assert.notNull() 抛出的 IllegalArgumentException
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseModel handleIllegalArgumentException(IllegalArgumentException exception) {
        // 400 状态码
        // 是否需要添加跳转 URL 后面再判断
        return ResponseModel.error(HttpStatus.BAD_REQUEST.value(), exception.getLocalizedMessage(), exception.getStackTrace().toString(), null);
    }

    /**
     * 处理 IllegalStateException
     * @param exception
     * @return
     */
    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseModel handleIllegalStateException(IllegalStateException exception) {
        // 400 状态码
        return ResponseModel.error(HttpStatus.BAD_REQUEST.value(), exception.getLocalizedMessage(), exception.getStackTrace().toString(), null);
    }

    /**
     * 处理实体不存在异常
     */
    @ExceptionHandler(value = EntityNotExistException.class)
    public ResponseModel handleEntityNotExistException(EntityNotExistException exception) {
        // 500 状态码
        // 是否需要添加跳转 URL 后面再进行判断
        return ResponseModel.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getLocalizedMessage(), exception.getStackTrace().toString(), null);
    }

    /**
     * 处理实体已存在异常
     */
    @ExceptionHandler(value = EntityExistException.class)
    public ResponseModel handleEntityExistException(EntityExistException exception) {
        // 500 状态码
        return ResponseModel.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getLocalizedMessage(), exception.getStackTrace().toString(), null);
    }

    /**
     * 处理文件下载异常
     */
    @ExceptionHandler(value = FileDownloadException.class)
    public ResponseModel handleFileDownloadException(FileDownloadException exception) {
        // 500 状态码
        return ResponseModel.error(exception.getLocalizedMessage());
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(value = Throwable.class)
    public ResponseModel handleAllException(Throwable throwable) {
        // 500 状态码
        log.info(" { 其他异常 } " + throwable.getLocalizedMessage() + "  " + throwable.getStackTrace().toString());
        throwable.printStackTrace();
        return ResponseModel.error("其他异常", throwable.getLocalizedMessage());
    }

    /**
     * 处理没有权限异常（因为 SecurityConfig 中配置自定义的 AccessDeniedHandler 无法捕获，原因不明）
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseModel handleAccessDeniedException(AccessDeniedException exception) {
        // 403 状态码
        return ResponseModel.error(HttpStatus.FORBIDDEN.value(), "没有权限", exception.getLocalizedMessage(), null);
    }

    /**
     * 处理 JSR-303 数据校验异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseModel handleValidException(MethodArgumentNotValidException e) {
        // 400 状态码
        return ResponseModel.error(HttpStatus.BAD_REQUEST.value(), e.getBindingResult().getFieldError().getDefaultMessage(), e.getStackTrace().toString(), null);
    }

    /**
     * 处理 JSR-303 数据校验异常
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseModel handleBindException(BindException e) {
        // 400 状态码
        return ResponseModel.error(HttpStatus.BAD_REQUEST.value(), e.getBindingResult().getFieldError().getDefaultMessage(), e.getStackTrace().toString(), null);
    }

    /**
     * 处理 getInfo 没有找到的异常
     */
    @ExceptionHandler(SecurityEntityException.class)
    public ResponseModel handleSecurityEntityException(SecurityEntityException e) {
        // 401 异常
        return ResponseModel.error(HttpStatus.UNAUTHORIZED.value(), e.getLocalizedMessage(), null, null);
    }

    /**
     * 处理认证中出现的异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseModel handleAuthenticationException(AuthenticationException exception) {
        // 如果是用户名未查找到实体的异常
        if (exception instanceof UsernameNotFoundException) {
            log.info(" { 登录失败 } " + exception.getLocalizedMessage());
            return ResponseModel.error("用户名不存在");
        }

        // 如果是账户锁定异常
        if (exception instanceof LockedException) {
            log.info(" { 登录失败 } " + exception.getLocalizedMessage());
            return ResponseModel.error("用户已停用，请联系管理员");
        }

        // 如果是凭证不合法异常
        if (exception instanceof BadCredentialsException) {
            log.info(" { 登录失败 } " + exception.getLocalizedMessage());
            return ResponseModel.error("用户名或密码不正确");
        }

        // 默认返回
        log.info(" { 默认：登录失败 } " + exception.getLocalizedMessage());
        return ResponseModel.error("登录失败");
    }
}
