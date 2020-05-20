package com.beiran.core.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 部门缩略信息
 */

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeptSmallDto {

    /**
     * 部门编号
     */
    private String deptId;

    /**
     * 部门名
     */
    private String deptName;
}
