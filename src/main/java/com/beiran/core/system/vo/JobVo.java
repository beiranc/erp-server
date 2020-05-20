package com.beiran.core.system.vo;

import com.beiran.core.system.dto.DeptSmallDto;
import com.beiran.core.system.entity.Job;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class JobVo {

    /**
     * 岗位名
     */
    @NotBlank(message = "岗位名不能为空")
    private String jobName;

    /**
     * 岗位状态，默认为启用
     */
    private Job.JobState jobState = Job.JobState.ACTIVE;

    /**
     * 岗位所属部门
     */
    @NotNull(message = "岗位所属部门不能为空")
    private DeptSmallDto dept;
}
