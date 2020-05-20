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
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
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
public class AuthController {

    @Autowired
    private Producer producer;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    /**
     * 验证码，使用 base64 编码进行输出
     * @param response
     * @return
     */
    @GetMapping("/code")
    @ApiOperation("获取验证码")
    public ResponseModel validCode(HttpServletResponse response) {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        // 生成文字验证码
        String text = producer.createText();

        // 生成图片验证码
        BufferedImage bufferedImage = producer.createImage(text);

        // 输出 Base64 字符串
        String base64String = "";
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

            byte[] bytes = byteArrayOutputStream.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            base64String = encoder.encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 将 KAPTCHA_SESSION_KEY 作为第一段
        String keyUUID = UUID.nameUUIDFromBytes(Constants.KAPTCHA_SESSION_KEY.getBytes()).toString().toLowerCase().replaceAll("-", "");
        // 将 Base64 编码后的验证码作为第二段
        String imgUUID = UUID.nameUUIDFromBytes(base64String.getBytes()).toString().toLowerCase().replaceAll("-", "");
        // 结合 keyUUID 与 imgUUID 生成当前验证码的 key
        String imgKey = UUID.nameUUIDFromBytes((keyUUID + imgUUID).getBytes()).toString().toLowerCase().replaceAll("-", "");

        log.info(" { codeKey } " + imgKey);
        log.info(" { 保存的验证码 } " + text);

        // 保存到 Redis, 设置 2 分钟过期
        redisUtils.set(imgKey, text, 2, TimeUnit.MINUTES);

        // Note: data 为 imgKey, url 为 Base64 编码后的验证码图片
        return ResponseModel.ok(HttpStatus.OK.value(), null, imgKey, "data:image/jpeg;base64," + base64String);
    }

    /**
     * 获取用户信息
     * @return
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
     * @param authUser
     * @return
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

//        UserDto user = userService.getUserByName(authUser.getUsername());
//        if (!Objects.equals(user,null)) {
//            if (Objects.equals(user.getState(), User.UserState.DISABLED.getValue())) {
//                return ResponseModel.error("当前用户已停用，请联系管理员", null);
//            }
//        }

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
     * @return
     */
    @DeleteMapping("/logout")
    @ApiOperation("用户登出")
    public ResponseModel logout() {
        SecurityContextHolder.clearContext();
        return ResponseModel.ok("登出成功");
    }
}
