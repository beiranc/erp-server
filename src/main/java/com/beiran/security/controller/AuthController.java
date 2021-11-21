package com.beiran.security.controller;

import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.RedisUtils;
import com.beiran.core.system.dto.UserDto;
import com.beiran.core.system.entity.User;
import com.beiran.core.system.service.UserService;
import com.beiran.security.config.JWTConfig;
import com.beiran.security.entity.AuthUser;
import com.beiran.security.entity.SecurityUserDetails;
import com.beiran.security.utils.JWTTokenUtil;
import com.beiran.security.utils.SecurityUtil;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 系统认证接口
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Api(tags = "系统认证")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RedisUtils redisUtils;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // 创建验证码时用到的第一段 Key
    private static final String CAPTCHA_KEY = "ERP_CAPTCHA_KEY";

    /**
     * EasyCaptcha验证码，使用 base64 编码进行输出
     * @return ResponseModel
     */
    @GetMapping("/code")
    @ApiOperation("获取验证码")
    public ResponseModel easyValidCode(HttpServletResponse response) {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        // 创建算术类型验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(111, 36);
        // 几位数运算
        captcha.setLen(2);
        // 设置验证码字体
        try {
            captcha.setFont(Captcha.FONT_1);
        } catch (IOException | FontFormatException e) {
            log.error("{ 设置验证码字体失败 }" + e);
        }
        // 运算的公式: 32+2=?
        String arithmeticString = captcha.getArithmeticString();
        // 运算结果
        String text = captcha.text();

        // 输出 Base64 字符串
        String base64String = captcha.toBase64();

        // 将 CAPTCHA_KEY 作为第一段
        String keyUUID = UUID.nameUUIDFromBytes(CAPTCHA_KEY.getBytes()).toString().toLowerCase().replaceAll("-", "");
        // 将 Base64 编码后的验证码作为第二段
        String imgUUID = UUID.nameUUIDFromBytes(base64String.getBytes()).toString().toLowerCase().replaceAll("-", "");
        // 结合 keyUUID 与 imgUUID 生成当前验证码的 key
        String imgKey = UUID.nameUUIDFromBytes((keyUUID + imgUUID).getBytes()).toString().toLowerCase().replaceAll("-", "");

        log.info(" { codeKey } " + imgKey);
        log.info(" { 验证码公式 } " + arithmeticString);
        log.info(" { 运算结果 } " + text);

        // 保存到 Redis, 设置 2 分钟过期
        redisUtils.set(imgKey, text, 2, TimeUnit.MINUTES);

        // Note: data 为 imgKey, url 为 Base64 编码后的验证码图片
        return ResponseModel.ok(HttpStatus.OK.value(), null, imgKey, base64String);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    @ApiOperation("获取用户信息")
    public ResponseModel getUserInfo() {
        String userName = SecurityUtil.getUserName();
        SecurityUserDetails securityUserDetails = (SecurityUserDetails) userDetailsService.loadUserByUsername(userName);
        return ResponseModel.ok(securityUserDetails);
    }

    /**
     * 登录
     * @param authUser 用户信息
     * @return ResponseModel
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public ResponseModel login(@RequestBody @Valid AuthUser authUser) {
        // 获取服务端保存的验证码
        String code = (String) redisUtils.get(authUser.getCodeKey());

        // 清除验证码
        redisUtils.del(authUser.getCodeKey());

        // 判断验证码是否正确
        if (!StringUtils.hasText(code)) {
            return ResponseModel.error(HttpStatus.BAD_REQUEST.value(), "验证码不存在或已过期", null, null);
        }

        if (!StringUtils.hasText(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
            return ResponseModel.error(HttpStatus.BAD_REQUEST.value(), "验证码错误", null, null);
        }

        UserDto user = userService.getUserByName(authUser.getUsername());
        if (!Objects.equals(user,null)) {
            if (Objects.equals(user.getState(), User.UserState.DISABLED.getValue())) {
                return ResponseModel.error("当前用户已停用，请联系管理员", null);
            }
        }

        // 执行登录
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authUser.getUsername(), authUser.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成 JWT Token
        SecurityUserDetails securityUserDetails = (SecurityUserDetails) authentication.getPrincipal();
        String token = JWTTokenUtil.createAccessToken(securityUserDetails);

        // 加上前缀
        token = JWTConfig.tokenPrefix + token;

        log.info(" { 登录成功 } ");

        // 将 Token 返回
        return ResponseModel.ok("登录成功", token);
    }

    /**
     * 登出
     * @return ResponseModel
     */
    @DeleteMapping("/logout")
    @ApiOperation("用户登出")
    public ResponseModel logout() {
        SecurityContextHolder.clearContext();
        return ResponseModel.ok("登出成功");
    }
}
