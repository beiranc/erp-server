package com.beiran.core.system.dto;

import com.beiran.core.system.entity.Dept;
import lombok.Data;

import java.util.Set;

@Data
public class DeptTree {

    private String deptId;

    private String deptName;

    private Dept.DeptState deptState;

    private String createTime;

    private Set<DeptSmallDto> children;
}
