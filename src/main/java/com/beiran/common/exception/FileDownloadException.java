package com.beiran.common.exception;

/**
 * 文件下载异常
 */
public class FileDownloadException extends RuntimeException {

    public FileDownloadException(String message) {
        super(message);
    }
}
