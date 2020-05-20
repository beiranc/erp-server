package com.beiran.security.utils;

import com.alibaba.fastjson.JSON;
import com.beiran.security.config.JWTConfig;
import com.beiran.security.entity.SecurityUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * JWT 工具类
 */

@Slf4j
public class JWTTokenUtil {
    /**
     * 私有化构造器
     */
    private JWTTokenUtil() {}

    /**
     * 生成 Token
     * @param securityUserDetails Spring Security 安全用户实体
     * @return Token
     */
    public static String createAccessToken(SecurityUserDetails securityUserDetails) {
        // 登录成功生成 Token
        String token = null;
        token = Jwts.builder()
                // 向 token 中放入用户 ID
                .setId(securityUserDetails.getUserId() + "")
                // 向 token 中放入用户名
                .setSubject(securityUserDetails.getUsername())
                // 签发时间
                .setIssuedAt(new Date())
                // 签发人
                .setIssuer("erp")
                // 自定义属性，这里将用户拥有的权限放入
                .claim("authorities", JSON.toJSONString(securityUserDetails.getAuthorities()))
                // 设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + JWTConfig.expiration))
                // 设置签名算法和密钥
                .signWith(SignatureAlgorithm.HS512, JWTConfig.secret)
                .compact();
        // 打印 token
        log.info(" { Token } = " + token);
        return token;
    }
}
