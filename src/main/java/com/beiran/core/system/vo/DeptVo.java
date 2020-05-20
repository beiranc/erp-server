package com.beiran.core.system.vo;

import com.beiran.core.system.dto.DeptSmallDto;
import com.beiran.core.system.entity.Dept;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeptVo {

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /**
     * 上一级部门
     */
    private DeptSmallDto parent;

    /**
     * 部门状态，默认启用
     */
    private Dept.DeptState deptState = Dept.DeptState.ACTIVE;
}
