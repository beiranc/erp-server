package com.beiran.core.system.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改密码时需要用到
 */

@Data
public class UserPasswordVo {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String userName;

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
