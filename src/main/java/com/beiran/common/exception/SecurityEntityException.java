package com.beiran.common.exception;

/**
 * 通过 /auth/info 接口获取用户信息时若没找到抛出的异常
 */
public class SecurityEntityException extends RuntimeException {
    public SecurityEntityException(String msg) {
        super(msg);
    }
}
