package com.beiran.core.system.service.impl;

import com.beiran.common.exception.EntityExistException;
import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.DateTimeUtils;
import com.beiran.common.utils.FileUtils;
import com.beiran.common.utils.transfer.JobTransferUtils;
import com.beiran.core.system.dto.JobDto;
import com.beiran.core.system.entity.Job;
import com.beiran.core.system.repository.JobRepository;
import com.beiran.core.system.service.JobService;
import com.beiran.core.system.vo.JobVo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * JobService 接口实现类
 */
@Service("jobService")
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Override
    public JobDto createJob(JobVo jobVo) {
        if (Objects.equals(jobVo, null)) {
            throw new ParameterException("需要保存的岗位不能为空");
        }
        Job job = JobTransferUtils.voToJob(jobVo);
        Job save = save(job);
        if (!Objects.equals(save, null)) {
            return JobTransferUtils.jobToDto(save);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateState(String jobId, Job.JobState jobState) {
        if (!StringUtils.hasText(jobId)) {
            throw new ParameterException("岗位编号不能为空");
        }
        Job job = findById(jobId);
        job.setJobState(jobState);
        Job update = update(job);
        return !Objects.equals(update, null);
    }

    @Override
    public JobDto getJobByName(String jobName) {
        if (!StringUtils.hasText(jobName)) {
            throw new ParameterException("岗位名不能为空");
        }
        // 由调用方判断
        Job job = jobRepository.findByJobName(jobName).orElse(null);
        if (!Objects.equals(job, null)) {
            return JobTransferUtils.jobToDto(job);
        }
        return null;
    }

    @Override
    public List<JobDto> getJobsByName(String jobName) {
        if (!StringUtils.hasText(jobName)) {
            throw new ParameterException("岗位名不能为空");
        }
        List<Job> jobs = jobRepository.findByJobNameContaining(jobName);
        List<JobDto> jobDtos =
                jobs.stream()
                        .map(job -> JobTransferUtils.jobToDto(job))
                        .collect(Collectors.toList());
        return jobDtos;
    }

    @Override
    public List<JobDto> getJobsByState(Job.JobState jobState, Pageable pageable) {
        if (Objects.equals(jobState, null)) {
            throw new ParameterException("岗位状态不能为空");
        }
        List<Job> jobs = jobRepository.findByJobState(jobState, pageable);
        List<JobDto> jobDtos =
                jobs.stream()
                        .map(job -> JobTransferUtils.jobToDto(job))
                        .collect(Collectors.toList());
        return jobDtos;
    }

    @Override
    public List<JobDto> getJobsByDeptId(String deptId, Pageable pageable) {
        if (!StringUtils.hasText(deptId)) {
            throw new ParameterException("部门编号不能为空");
        }
        List<Job> jobs = jobRepository.findByJobDept_DeptId(deptId, pageable);
        List<JobDto> jobDtos =
                jobs.stream()
                        .map(job -> JobTransferUtils.jobToDto(job))
                        .collect(Collectors.toList());
        return jobDtos;
    }

    @Override
    public List<JobDto> getJobsByTime(Date leftTime, Date rightTime, Pageable pageable) {
        if (Objects.equals(leftTime, null) || Objects.equals(rightTime, null)) {
            throw new ParameterException("岗位创建时间不能为空");
        }
        List<Job> jobs = jobRepository.findByJobCreateTimeBetween(leftTime, rightTime, pageable);
        List<JobDto> jobDtos =
                jobs.stream()
                        .map(job -> JobTransferUtils.jobToDto(job))
                        .collect(Collectors.toList());
        return jobDtos;
    }

    @Override
    public File createExcelFile(Pageable pageable) {
        // 根据分页信息获取数据
        Page<Job> jobPage = findAll(pageable);
        List<Job> jobs = jobPage.getContent();
        if (Objects.equals(jobs, null)) {
            jobs = new ArrayList<>();
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("岗位编号");
        rowInfo.createCell(++columnIndex).setCellValue("岗位名称");
        rowInfo.createCell(++columnIndex).setCellValue("岗位状态");
        rowInfo.createCell(++columnIndex).setCellValue("岗位创建时间");
        rowInfo.createCell(++columnIndex).setCellValue("所属部门");

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(job.getJobId());
            row.getCell(++columnIndex).setCellValue(job.getJobName());
            row.getCell(++columnIndex).setCellValue(job.getJobState().getValue());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(job.getJobCreateTime()));
            row.getCell(++columnIndex).setCellValue(job.getJobDept().getDeptName());
        }
        return FileUtils.createExcelFile(workbook, "erp_jobs");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Job save(Job entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的岗位不能为空");
        }
        Job job = jobRepository.findByJobName(entity.getJobName()).orElse(null);
        if (!Objects.equals(job, null)) {
            throw new EntityExistException("岗位已存在");
        }
        return jobRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Job> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的岗位不能为空");
        }
        jobRepository.deleteAll(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Job update(Job entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的岗位不能为空");
        }
        return jobRepository.saveAndFlush(entity);
    }

    @Override
    public Job findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("岗位编号不能为空");
        }
        return jobRepository.findById(id).orElseThrow(() -> new EntityNotExistException("岗位不存在"));
    }

    @Override
    public Page<Job> findAll(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }
}
