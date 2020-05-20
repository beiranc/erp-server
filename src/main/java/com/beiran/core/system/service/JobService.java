package com.beiran.core.system.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.system.dto.JobDto;
import com.beiran.core.system.entity.Job;
import com.beiran.core.system.vo.JobVo;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * JobService 接口
 */

public interface JobService extends GenericService<Job, String> {

    /**
     * 创建岗位
     * @param jobVo 需要的数据
     * @return JobDto
     */
    JobDto createJob(JobVo jobVo);

    /**
     * 根据岗位编号修改岗位状态
     * @param jobId 岗位编号
     * @param jobState 岗位状态
     * @return 是否修改成功
     */
    Boolean updateState(String jobId, Job.JobState jobState);

    /**
     * 根据岗位名查询岗位信息
     * @param jobName 岗位名
     * @return JobDto
     */
    JobDto getJobByName(String jobName);

    /**
     * 根据岗位名模糊查询
     * @param jobName 岗位名
     * @return List<JobDto>
     */
    List<JobDto> getJobsByName(String jobName);

    /**
     * 根据岗位状态查询岗位
     * @param jobState 岗位状态
     * @param pageable 分页参数
     * @return List<JobDto>
     */
    List<JobDto> getJobsByState(Job.JobState jobState, Pageable pageable);

    /**
     * 根据部门编号查询岗位
     * @param deptId 部门编号
     * @param pageable 分页参数
     * @return List<JobDto>
     */
    List<JobDto> getJobsByDeptId(String deptId, Pageable pageable);

    /**
     * 根据岗位创建时间范围查询岗位
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<JobDto>
     */
    List<JobDto> getJobsByTime(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 导出岗位信息
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);
}
