package com.beiran.core.system.dto;

import com.beiran.core.system.entity.Dept;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeptDto {

    /**
     * 部门编号
     */
    private String deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 上一级部门
     */
    private DeptSmallDto parent;

    /**
     * 部门状态
     */
    private Dept.DeptState deptState;

    /**
     * 部门创建时间
     */
    private String createTime;
}
