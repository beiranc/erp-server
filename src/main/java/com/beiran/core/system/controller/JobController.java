package com.beiran.core.system.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.system.dto.JobDto;
import com.beiran.core.system.entity.Job;
import com.beiran.core.system.service.JobService;
import com.beiran.core.system.vo.JobVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 岗位接口<br>
 * 创建岗位权限: system:job:add<br>
 * 修改岗位权限: system:job:edit<br>
 * 删除岗位权限: system:job:del<br>
 * 查询岗位权限: system:job:view<br>
 * 导出岗位权限: system:job:export<br>
 */
@RestController
@RequestMapping("/api/v1/jobs")
@Api(tags = "系统管理：岗位管理")
public class JobController {

    @Autowired
    private JobService jobService;

    /**
     * 创建岗位
     * @param jobVo 需要的数据
     * @return
     */
    @PostMapping
    @LogRecord("创建岗位")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:job:add')")
    @ApiOperation("创建岗位")
    public ResponseModel save(@RequestBody @Valid JobVo jobVo) {
        return ResponseModel.ok(jobService.createJob(jobVo));
    }

    /**
     * 修改岗位
     * @param job 待修改的岗位信息
     * @return
     */
    @PutMapping
    @LogRecord("修改岗位")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:job:edit')")
    @ApiOperation("修改岗位")
    public ResponseModel update(@RequestBody Job job) {
        return ResponseModel.ok(jobService.update(job));
    }

    /**
     * 修改岗位状态
     * @param jobId 岗位编号
     * @param jobState 岗位状态
     * @return
     */
    @PutMapping("/state")
    @LogRecord("修改岗位状态")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:job:edit')")
    @ApiOperation("修改岗位状态")
    public ResponseModel updateState(@RequestParam("jobId") String jobId,
                                     @RequestParam("jobState") Job.JobState jobState) {
        boolean result = jobService.updateState(jobId, jobState);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 删除岗位
     * @param jobIds 待删除的岗位编号集合
     * @return
     */
    @DeleteMapping
    @LogRecord("删除岗位")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:job:del')")
    @ApiOperation("删除岗位")
    public ResponseModel delete(@RequestBody List<String> jobIds) {
        if (Objects.equals(jobIds, null) || jobIds.isEmpty()) {
            return ResponseModel.error("需要删除的岗位不能为空");
        } else {
            List<Job> jobs = jobIds.stream().map(jobId -> {
                Job job = new Job();
                job.setJobId(jobId);
                return job;
            }).collect(Collectors.toList());
            jobService.deleteAll(jobs);
            return ResponseModel.ok("删除成功");
        }
    }

    /**
     * 分页查询岗位
     * @param pageable
     * @return
     */
    @GetMapping
    @LogRecord("分页查询岗位")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:job:view')")
    @ApiOperation("分页查询岗位")
    public ResponseModel getJobs(@PageableDefault Pageable pageable) {
        return ResponseModel.ok(jobService.findAll(pageable));
    }

    /**
     * 通过岗位名查询岗位
     * @param jobName 岗位名
     * @return
     */
    @GetMapping("/name")
    @LogRecord("通过岗位名查询岗位")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:job:view')")
    @ApiOperation("通过岗位名查询岗位")
    public ResponseModel getJobByName(@RequestParam("jobName") String jobName) {
        JobDto jobDto = jobService.getJobByName(jobName);
        if (!Objects.equals(jobDto, null)) {
            return ResponseModel.ok(jobDto);
        } else {
            return ResponseModel.error("岗位不存在");
        }
    }

    /**
     * 通过岗位名模糊查询岗位
     * @param jobName 岗位名
     * @return
     */
    @GetMapping("/search")
    @LogRecord("通过岗位名模糊查询岗位")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:job:view')")
    @ApiOperation("通过岗位名模糊查询岗位")
    public ResponseModel getJobsByName(@RequestParam("jobName") String jobName) {
        return ResponseModel.ok(jobService.getJobsByName(jobName));
    }

    /**
     * 通过岗位状态查询岗位
     * @param jobState 岗位状态
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/state")
    @LogRecord("通过岗位状态查询岗位")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:job:view')")
    @ApiOperation("通过岗位状态查询岗位")
    public ResponseModel getJobsByState(@RequestParam("jobState") Job.JobState jobState,
                                        @PageableDefault Pageable pageable) {
        return ResponseModel.ok(jobService.getJobsByState(jobState, pageable));
    }

    /**
     * 通过部门查询岗位
     * @param deptId 部门编号
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/dept")
    @LogRecord("通过部门查询岗位")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:job:view')")
    @ApiOperation("通过部门查询岗位")
    public ResponseModel getJobsByDeptId(@RequestParam("deptId") String deptId,
                                         @PageableDefault Pageable pageable) {
        return ResponseModel.ok(jobService.getJobsByDeptId(deptId, pageable));
    }

    /**
     * 通过创建时间查询岗位
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/time")
    @LogRecord("通过创建时间查询岗位")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:job:view')")
    @ApiOperation("通过创建时间查询岗位")
    public ResponseModel getJobsByTime(@RequestParam("leftTime") Date leftTime,
                                       @RequestParam("rightTime") Date rightTime,
                                       @PageableDefault Pageable pageable) {
        return ResponseModel.ok(jobService.getJobsByTime(leftTime, rightTime, pageable));
    }

    /**
     * 导出岗位
     * @param pageable 分页参数
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出岗位")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:job:export')")
    @ApiOperation("导出岗位")
    public void export(@PageableDefault Pageable pageable,
                       HttpServletResponse response) throws Exception {
        File file = jobService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }
}
