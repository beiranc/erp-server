package com.beiran.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解（用 AOP 实现）
 * 用于方法
 * 新增: value 属性用作 logInfo 属性的别名
 */

@Target(value = { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecord {

    /**
     * @return 记录的操作信息，例如用户登录，默认为空字符串
     */
    String value() default "";
}
