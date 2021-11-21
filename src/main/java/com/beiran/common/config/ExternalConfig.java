package com.beiran.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 极速 API 相关配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "external")
public class ExternalConfig {

    /**
     * APP_KEY
     */
    private String appKey;

    /**
     * 汇率接口 URL
     */
    private String rateUrl;
}
