package com.beiran.common.exception;

/**
 * 用于参数错误时抛出的异常
 */
public class ParameterException extends RuntimeException {

    public ParameterException(String message) {
        super(message);
    }
}
