package com.beiran.common.respone;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * 响应体中的提示信息，可根据 message()、detail()、both() 等静态方法创建
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseStatusInfo {
    /**
     * 提示信息
     */
    private String message;

    /**
     * 错误的详细信息
     */
    private String detail;

    private ResponseStatusInfo(String message, String detail) {
        super();
        this.message = message;
        this.detail = detail;
    }

    /**
     * @param message 提示信息
     * @return 返回只包含提示信息的 StatusInfo
     */
    public static ResponseStatusInfo message(String message) {
        return new ResponseStatusInfo(message, null);
    }

    /**
     * @param message 提示信息
     * @param detail 错误详细信息
     * @return 返回错误详细及提示信息都包含的 StatusInfo
     */
    public static ResponseStatusInfo both(String message, String detail) {
        return new ResponseStatusInfo(message, detail);
    }

    /**
     * @param detail 错误详细信息
     * @return 仅返回包含错误详细信息的 StatusInfo
     */
    public static ResponseStatusInfo detail(String detail) {
        return new ResponseStatusInfo(null, detail);
    }
}
