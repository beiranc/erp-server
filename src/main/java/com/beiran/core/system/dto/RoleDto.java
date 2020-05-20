package com.beiran.core.system.dto;

import lombok.Data;

import java.util.Set;

@Data
public class RoleDto {

    /**
     * 角色编号
     */
    private String roleId;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String roleDesc;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 角色所拥有权限
     */
    private Set<PermissionSmallDto> permissions;
}
