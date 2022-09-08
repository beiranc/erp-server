package com.beiran.core.system.vo;

import com.beiran.core.system.entity.User;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * 用户注册所需信息
 */

@Data
public class UserVo {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String userName;

    /**
     * 昵称
     */
    private String nickName = "新用户";

    /**
     * 登录密码
     */
    @NotBlank(message = "登录密码不能为空")
    private String userPassword;

    /**
     * 用户状态
     */
    @NotNull(message = "用户状态不能为空")
    private User.UserState userState;

    /**
     * 用户联系方式
     */
    @NotBlank(message = "用户联系方式不能为空")
    private String userPhone;

    /**
     * 用户性别
     */
    @NotNull(message = "用户性别不能为空")
    private User.UserSex userSex;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String userEmail;

    /**
     * 用户岗位 ID
     */
    @NotBlank(message = "用户岗位不能为空")
    private String job;

    /**
     * 用户角色 ID 集合
     */
    @NotNull(message = "请至少选择一个角色")
    private Set<String> roles;
}
