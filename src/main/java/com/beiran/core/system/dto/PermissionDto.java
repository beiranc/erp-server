package com.beiran.core.system.dto;

import lombok.Data;

@Data
public class PermissionDto {

    /**
     * 权限编号
     */
    private String permissionId;

    /**
     * 权限名
     */
    private String permissionName;

    /**
     * 权限描述
     */
    private String permissionDesc;

    /**
     * 权限创建时间
     */
    private String createTime;
}
