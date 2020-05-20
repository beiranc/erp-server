package com.beiran.common.exception;

/**
 * 实体不存在异常
 */
public class EntityNotExistException extends RuntimeException {

    public EntityNotExistException(String message) {
        super(message);
    }
}
