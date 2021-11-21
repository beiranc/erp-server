package com.beiran.common.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "common")
public class CommonConfig {

    /**
     * 登录请求处理地址
     */
    public static String loginUrl;

    /**
     * 登出请求处理地址
     */
    public static String logoutUrl;

    /**
     * Api 版本前缀，用于统一所有的请求地址前缀
     */
    public static String apiVersion;

    /**
     * Redis 中验证码 key
     */
    public static String codeKey;

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public void setCodeKey(String codeKey) {
        this.codeKey = codeKey;
    }
}