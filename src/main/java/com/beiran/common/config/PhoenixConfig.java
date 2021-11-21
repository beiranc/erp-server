package com.beiran.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 凤凰信息配置
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "phoenix")
public class PhoenixConfig {

    /**
     * 公司中文名
     */
    private String chineseName;

    /**
     * 公司英文名
     */
    private String englishName;

    /**
     * 地址
     */
    private String address;

    /**
     * 官网
     */
    private String officialWebsite;
}
