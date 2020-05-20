package com.beiran.core.system.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.system.dto.DeptDto;
import com.beiran.core.system.dto.DeptTree;
import com.beiran.core.system.entity.Dept;
import com.beiran.core.system.vo.DeptVo;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;

/**
 * DeptService 接口
 */
public interface DeptService extends GenericService<Dept, String> {

    /**
     * 创建部门
     * @param deptVo 创建部门所需数据
     * @return DeptDto
     */
    DeptDto createDept(DeptVo deptVo);

    /**
     * 根据部门编号修改部门状态
     * @param deptId 部门编号
     * @param deptState 部门状态
     * @return 是否修改成功
     */
    Boolean updateState(String deptId, Dept.DeptState deptState);

    /**
     * 根据部门名称获取部门信息
     * @param deptName 部门名称
     * @return DeptDto
     */
    DeptDto getDeptByName(String deptName);

    /**
     * 根据部门名称搜索部门
     * @param deptName 部门名称
     * @return List<DeptDto>
     */
    List<DeptDto> getDeptsByName(String deptName);

    /**
     * 根据部门状态查询部门
     * @param deptState 部门状态
     * @param pageable 分页参数
     * @return List<DeptDto>
     */
    List<DeptDto> getDeptsByState(Dept.DeptState deptState, Pageable pageable);

    /**
     * 获取部门关系树
     * @return List<DeptDto>
     */
    List<DeptTree> getDeptTree();

    /**
     * 导出部门信息
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);
}
