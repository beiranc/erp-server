package com.beiran.core.system.dto;

import com.beiran.core.system.entity.Job;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobDto {

    /**
     * 岗位编号
     */
    private String jobId;

    /**
     * 岗位名
     */
    private String jobName;

    /**
     * 岗位状态
     */
    private Job.JobState jobState;

    /**
     * 岗位创建时间
     */
    private String createTime;

    /**
     * 岗位所属部门
     */
    private DeptSmallDto dept;
}
