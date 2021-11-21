package com.beiran.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 */

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许跨域访问的路径
                .allowedOrigins("*") // 允许跨域访问的源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
                .maxAge(18000) // 预检间隔时间，单位秒，这里设置为 5 个小时
                .allowedHeaders("*") // 允许头部设置
                .allowCredentials(true); // 是否发送 Cookie
    }
}
