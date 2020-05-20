package com.beiran.security.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 用于登录
 */

@Getter
@Setter
public class AuthUser {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    private String code;

    private String codeKey;

    @Override
    public String toString() {
        return "{username=}" + username + ", password= ******";
    }
}
