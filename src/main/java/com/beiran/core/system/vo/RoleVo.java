package com.beiran.core.system.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 创建角色时用得到
 */

@Data
public class RoleVo {

    /**
     * 角色名
     */
    @NotBlank(message = "角色名不能为空")
    private String roleName;

    /**
     * 角色描述
     */
    @NotBlank(message = "角色描述不能为空")
    private String roleDesc;

    /**
     * 角色权限
     */
    private List<String> permissionIds;
}
