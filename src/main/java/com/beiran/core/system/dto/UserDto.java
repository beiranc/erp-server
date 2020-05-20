package com.beiran.core.system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Set;

/**
 * 响应给前端的用户信息
 */

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto {

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户密码
     */
    @JsonIgnore
    private String userPassword;

    /**
     * 用户状态
     */
    private String state;

    /**
     * 用户联系方式
     */
    private String userPhone;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户创建时间
     */
    private String createTime;

    /**
     * 用户的岗位与部门信息
     */
    private JobSmallDto jobSmallDto;

    /**
     * 用户角色信息
     */
    private Set<RoleSmallDto> roles;
}
