package com.beiran.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 凤凰定制信息
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "phoenix.custom")
public class PhoenixCustomInfoConfig {

    /**
     * Logo 定制, 默认支持
     */
    private Boolean logo = true;

    /**
     * 外包装定制, 默认支持
     */
    private Boolean outerPacking = true;

    /**
     * 图案定制, 默认支持
     */
    private Boolean design = true;
}
