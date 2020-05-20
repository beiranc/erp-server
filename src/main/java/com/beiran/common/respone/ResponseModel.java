package com.beiran.common.respone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.ServletResponse;
import java.io.PrintWriter;

/**
 * HTTP 请求响应模型
 */
@Getter
@Setter
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel {
    /**
     * 状态码
     */
    private int status;

    /**
     * 状态信息
     */
    private ResponseStatusInfo statusInfo;

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 跳转链接
     */
    private String url;

    /**
     * 禁止外部直接 new
     */
    private ResponseModel() {}

    /**
     * @return 返回 500 错误，并且错误信息为未知异常
     */
    public static ResponseModel error() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未知异常！", null, null);
    }

    /**
     * @param message 需要提示的错误信息
     * @return 返回 500 错误，并且错误信息为自定义错误信息
     */
    public static ResponseModel error(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null, null);
    }

    /**
     * @param message 需要提示的错误信息
     * @param detail 错误的详细信息
     * @return 返回 500 错误，自定义提示的错误信息以及详细信息
     */
    public static ResponseModel error(String message, String detail) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, detail, null);
    }

    /**
     * @param message 需要提示的错误信息
     * @param detail 错误的详细信息
     * @param url 跳转的 URL 地址
     * @return 返回 500 错误，自定义的提示信息、错误详细信息、可跳转的 URL
     */
    public static ResponseModel error(String message, String detail, String url) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, detail, url);
    }

    /**
     * @param status 状态码，可通过 HttpStatus 获取
     * @param message 提示信息
     * @param detail 错误详细信息
     * @param url 可跳转的 URL 地址
     * @return 全字段自定义
     */
    public static ResponseModel error(int status, String message, String detail, String url) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setStatus(status);
        responseModel.setUrl(url);
        responseModel.setStatusInfo(ResponseStatusInfo.both(message, detail));
        return responseModel;
    }

    /**
     * @param message 操作成功时的提示信息
     * @return 返回只包含提示信息的响应体
     */
    public static ResponseModel ok(String message) {
        return ok(HttpStatus.OK.value(), ResponseStatusInfo.message(message), null, null);
    }

    /**
     * @param data 数据
     * @return 返回不带提示信息的数据
     */
    public static ResponseModel ok(Object data) {
        return ok(HttpStatus.OK.value(), null, data, null);
    }

    /**
     * @param message 提示信息
     * @param data 数据
     * @return 返回带提示信息的数据
     */
    public static ResponseModel ok(String message, Object data) {
        return ok(HttpStatus.OK.value(), ResponseStatusInfo.message(message), data, null);
    }

    /**
     * 可自定义成功时返回的数据字段
     * @param status 状态码
     * @param statusInfo 状态信息
     * @param data 数据
     * @param url 跳转链接
     * @return 全字段自定义的 ResponseModel
     */
    public static ResponseModel ok(int status, ResponseStatusInfo statusInfo, Object data, String url) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setStatus(status);
        responseModel.setStatusInfo(statusInfo);
        responseModel.setData(data);
        responseModel.setUrl(url);
        return responseModel;
    }

    /**
     * @return 返回 200 OK 且不带任何数据及提示信息
     */
    public static ResponseModel ok() {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setStatus(HttpStatus.OK.value());
        return responseModel;
    }

    /**
     * 通过 ServletResponse 输出 ResponseModel 数据
     * @param response ServletResponse
     * @param responseModel 自定义数据返回类型
     */
    public static void response(ServletResponse response, ResponseModel responseModel) {
        // 设置编码为 UTF-8
        response.setCharacterEncoding("UTF-8");
        // 设置返回类型为 application/json
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try (PrintWriter out = response.getWriter()) {
            out.println(new ObjectMapper().writeValueAsString(responseModel));
            out.flush();
        } catch (Exception e) {
            log.error(" { JSON 输出异常 } " + e);
        }
    }
}
