package com.beiran.common.utils.transfer;

import com.beiran.common.utils.DateTimeUtils;
import com.beiran.core.system.dto.DeptSmallDto;
import com.beiran.core.system.dto.JobDto;
import com.beiran.core.system.entity.Dept;
import com.beiran.core.system.entity.Job;
import com.beiran.core.system.vo.JobVo;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 提供 JobDto-Job-JobVo 相互转换的工具
 */

@Log
public class JobTransferUtils {

    /**
     * 将 JobDto 转换为 Job
     * @param jobDto 需要转换的 JobDto
     * @return Job
     */
    public static Job dtoToJob(JobDto jobDto) {
        Job job = new Job();
        if (!Objects.equals(jobDto, null)) {
            BeanUtils.copyProperties(jobDto, job);
        }
        Date createTime = null;
        if (StringUtils.hasText(jobDto.getCreateTime())) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_TIMESTAMP);
            try {
                createTime = simpleDateFormat.parse(jobDto.getCreateTime());
            } catch (ParseException e) {
                log.info(" { 日期转换异常 } " + e.getLocalizedMessage());
            }
        }
        job.setJobCreateTime(createTime);
        Dept dept = new Dept();
        if (!Objects.equals(jobDto.getDept(), null)) {
            BeanUtils.copyProperties(jobDto.getDept(), dept);
        }
        job.setJobDept(dept);
        return job;
    }

    /**
     * 将 Job 转换为 JobDto
     * @param job 需要转换的 Job
     * @return JobDto
     */
    public static JobDto jobToDto(Job job) {
        JobDto jobDto = new JobDto();
        if (!Objects.equals(job, null)) {
            BeanUtils.copyProperties(job, jobDto);
        }
        if (!Objects.equals(job.getJobCreateTime(), null)) {
            jobDto.setCreateTime(DateTimeUtils.getDateTime(job.getJobCreateTime()));
        }
        DeptSmallDto deptSmallDto = new DeptSmallDto();
        if (!Objects.equals(job.getJobDept(), null)) {
            BeanUtils.copyProperties(job.getJobDept(), deptSmallDto);
        }
        jobDto.setDept(deptSmallDto);
        return jobDto;
    }

    /**
     * 将 JobVo 转换为 Job
     * @param jobVo 需要转换的 JobVo
     * @return Job
     */
    public static Job voToJob(JobVo jobVo) {
        Job job = new Job();
        if (!Objects.equals(jobVo, null)) {
            BeanUtils.copyProperties(jobVo, job);
        }
        Dept dept = new Dept();
        if (!Objects.equals(jobVo.getDept(), null)) {
            BeanUtils.copyProperties(jobVo.getDept(), dept);
        }
        job.setJobDept(dept);
        return job;
    }
}
