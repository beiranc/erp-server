package com.beiran.core.system.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PermissionVo {

    /**
     * 权限名
     */
    @NotBlank(message = "权限名不能为空")
    private String permissionName;

    /**
     * 权限描述
     */
    @NotBlank(message = "权限描述不能为空")
    private String permissionDesc;
}
