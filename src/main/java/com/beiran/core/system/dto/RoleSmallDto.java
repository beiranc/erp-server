package com.beiran.core.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 角色缩略信息
 */

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RoleSmallDto {

    /**
     * 角色编号
     */
    private String roleId;

    /**
     * 角色名
     */
    private String roleName;
}
