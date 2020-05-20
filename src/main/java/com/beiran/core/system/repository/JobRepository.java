package com.beiran.core.system.repository;

import com.beiran.core.system.entity.Job;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 岗位 Repository
 */
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {

    /**
     * 根据岗位编号修改岗位状态
     * @param jobId 岗位编号
     * @param jobState 岗位状态
     * @return 是否修改成功
     */
//    @Modifying
//    @Query(value = "UPDATE erp_job SET job_state = ?2 WHERE job_id = ?1", nativeQuery = true)
//    int updateState(String jobId, Job.JobState jobState);

    /**
     * 根据岗位名查询岗位信息
     * @param jobName 岗位名
     * @return Optional<Job>
     */
    Optional<Job> findByJobName(String jobName);

    /**
     * 根据岗位名模糊查询岗位信息
     * @param jobName 岗位名
     * @return List<Job>
     */
    List<Job> findByJobNameContaining(String jobName);

    /**
     * 根据岗位状态查询岗位信息
     * @param jobState 岗位状态
     * @param pageable 分页参数
     * @return List<Job>
     */
    List<Job> findByJobState(Job.JobState jobState, Pageable pageable);

    /**
     * 根据部门编号查询岗位信息
     * @param deptId 部门编号
     * @param pageable 分页参数
     * @return List<Job>
     */
    List<Job> findByJobDept_DeptId(String deptId, Pageable pageable);

    /**
     * 根据岗位创建时间范围查询岗位信息
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<Job>
     */
    List<Job> findByJobCreateTimeBetween(Date leftTime, Date rightTime, Pageable pageable);
}
