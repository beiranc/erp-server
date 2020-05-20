package com.beiran.common.exception;

/**
 * 库存不足异常
 */
public class StockShortageException extends RuntimeException {

    public StockShortageException() {
        super("库存不足");
    }
}
