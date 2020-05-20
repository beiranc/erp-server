package com.beiran.security.handler;

import com.beiran.common.respone.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 暂无权限处理器（403）
 */

@Component
public class UserAuthAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 403 处理结果
     * @param request HttServletRequest
     * @param response HttpServletResponse
     * @param accessDeniedException AccessDeniedException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseModel.response(response, ResponseModel.error(HttpStatus.FORBIDDEN.value(), "没有权限", accessDeniedException.getLocalizedMessage(), null));
    }
}
