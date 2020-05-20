package com.beiran.core.system.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.system.dto.PermissionDto;
import com.beiran.core.system.entity.Permission;
import com.beiran.core.system.vo.PermissionVo;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * PermissionService 接口
 */
public interface PermissionService extends GenericService<Permission, String> {

    /**
     * 创建一个权限
     * @param permissionVo PermissionVo
     * @return PermissionDto
     */
    PermissionDto createPermission(PermissionVo permissionVo);

    /**
     * 根据权限名查找一个权限
     * @param permissionName 权限名
     * @return PermissionDto
     */
    PermissionDto getPermissionByName(String permissionName);

    /**
     * 根据权限名模糊查询权限
     * @param permissionName 权限名
     * @param pageable 分页参数
     * @return List<PermissionDto>
     */
    List<PermissionDto> getPermissionsByName(String permissionName, Pageable pageable);

    /**
     * 根据权限描述模糊查询权限
     * @param permissionDesc 权限描述
     * @param pageable 分页参数
     * @return List<PermissionDto>
     */
    List<PermissionDto> getPermissionsByDesc(String permissionDesc, Pageable pageable);

    /**
     * 根据权限创建时间查询权限
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<PermissionDto>
     */
    List<PermissionDto> getPermissionsByTime(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 根据角色编号查询权限
     * @param roleId 角色编号
     * @return Set<PermissionDto>
     */
    Set<PermissionDto> getPermissionsByRoleId(String roleId);

    /**
     * 导出权限信息
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);
}
