package com.beiran.core.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 简略的岗位信息
 */

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobSmallDto {

    /**
     * 岗位编号
     */
    private String jobId;

    /**
     * 岗位名
     */
    private String jobName;

    /**
     * 岗位所属部门编号
     */
    private String deptId;

    /**
     * 岗位所属部门名
     */
    private String deptName;
}
