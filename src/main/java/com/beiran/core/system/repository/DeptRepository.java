package com.beiran.core.system.repository;

import com.beiran.core.system.entity.Dept;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 部门 Repository
 */
public interface DeptRepository extends JpaRepository<Dept, String>, JpaSpecificationExecutor<Dept> {

    /**
     * 根据部门编号修改部门状态
     * @param deptId 部门编号
     * @param deptState 部门状态
     * @return 是否修改成功
     */
//    @Modifying
//    @Query(value = "UPDATE erp_dept SET dept_state = ?2 WHERE dept_id = ?1", nativeQuery = true)
//    int updateState(String deptId, Dept.DeptState deptState);

    /**
     * 根据部门名称查询部门信息
     * @param deptName 部门名称
     * @return Optional<Dept>
     */
    Optional<Dept> findByDeptName(String deptName);

    /**
     * 根据部门名称模糊查询部门信息
     * @param deptName 部门名称
     * @return List<Dept>
     */
    List<Dept> findByDeptNameContaining(String deptName);

    /**
     * 根据部门状态查询部门信息
     * @param deptState 部门状态
     * @param pageable 分页参数
     * @return List<Dept>
     */
    List<Dept> findByDeptState(Dept.DeptState deptState, Pageable pageable);

    /**
     * 查找所有的最上级部门
     * @return List<Dept>
     */
    List<Dept> findByDeptParentIsNull();

    /**
     * 查找所有的部门（除最上级外）
     * @return List<Dept>
     */
    List<Dept> findByDeptParentIsNotNull();
}
