package com.beiran.security.jwt;

import com.beiran.security.entity.SecurityUserDetails;
import com.beiran.security.utils.SecurityUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义权限验证表达式（因为 SpEL 支持使用 @ 来引用 Bean）
 */

@Component("erp")
public class CustomPermissionCheck {

    /**
     * 使用 @PreAuthorize("@erp.check('permission')") 来调用
     * @param permissions 该接口所需权限
     * @return 是否具有权限
     */
    public Boolean check(String... permissions) {
        // 根据 Token 获取用户信息
        SecurityUserDetails securityUserDetails = SecurityUtil.getUserInfo();

        // 获取用户权限
        Set<String> roles = securityUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        // 只有有任何一个匹配的就返回 true
        return Arrays.stream(permissions).anyMatch(roles::contains);
    }
}
