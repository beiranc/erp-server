package com.beiran.common.exception;

/**
 * 实体已存在异常
 */
public class EntityExistException extends RuntimeException {

    public EntityExistException(String message) {
        super(message);
    }
}
