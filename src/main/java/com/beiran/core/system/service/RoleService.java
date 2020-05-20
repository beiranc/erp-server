package com.beiran.core.system.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.system.dto.RoleDto;
import com.beiran.core.system.entity.Role;
import com.beiran.core.system.vo.RoleVo;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * RoleService 接口
 */
public interface RoleService extends GenericService<Role, String> {

    /**
     * 根据 RoleVo 创建角色
     * @param roleVo 给定的 RoleVo
     * @return RoleDto
     */
    RoleDto createRole(RoleVo roleVo);

    /**
     * 根据角色名查询角色
     * @param roleName 角色名
     * @return RoleDto
     */
    RoleDto getRoleByName(String roleName);

    /**
     * 根据角色名模糊匹配
     * @param roleName 角色名
     * @return List<RoleDto>
     */
    List<RoleDto> getRolesByName(String roleName);

    /**
     * 根据角色描述模糊匹配
     * @param roleDesc 角色描述
     * @return List<RoleDto>
     */
    List<RoleDto> getRolesByDesc(String roleDesc);

    /**
     * 根据用户编号查询
     * @param userId 用户编号
     * @return Set<RoleDto>
     */
    Set<RoleDto> getRolesByUserId(String userId);

    /**
     * 根据用户创建时间查询用户
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @return List<RoleDto>
     */
    List<RoleDto> getRolesByTime(Date leftTime, Date rightTime);

    /**
     * 导出角色信息
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);
}
