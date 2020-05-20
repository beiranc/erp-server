package com.beiran.security.utils;

import com.beiran.common.exception.SecurityEntityException;
import com.beiran.security.entity.SecurityUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    /**
     * 获取当前用户信息
     * @return SecurityUserDetails
     */
    public static SecurityUserDetails getUserInfo() {
        SecurityUserDetails securityUserDetails = null;

        try {
            securityUserDetails = (SecurityUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            log.info(" { 登录状态过期 } " + e.getLocalizedMessage());
            throw new SecurityEntityException("登录状态过期");
        }

        return securityUserDetails;
    }

    /**
     * 获取当前用户 ID
     * @return String
     */
    public static String getUserId() {
        return getUserInfo().getUserId();
    }

    /**
     * 获取当前用户登录名
     * @return String
     */
    public static String getUserName() {
        return getUserInfo().getUsername();
    }
}
