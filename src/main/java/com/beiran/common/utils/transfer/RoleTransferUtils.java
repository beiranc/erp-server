package com.beiran.common.utils.transfer;

import com.beiran.common.utils.DateTimeUtils;
import com.beiran.core.system.dto.PermissionSmallDto;
import com.beiran.core.system.dto.RoleDto;
import com.beiran.core.system.entity.Permission;
import com.beiran.core.system.entity.Role;
import com.beiran.core.system.vo.RoleVo;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 提供 Role-RoleDto-RoleVo 转换的工具
 */

@Log
public class RoleTransferUtils {

    /**
     * 将 Role 转换为 RoleDto
     * @param role 需要转换的 Role
     * @return RoleDto
     */
    public static RoleDto roleToDto(Role role) {
        RoleDto roleDto = new RoleDto();
        // 复制属性
        if (!Objects.equals(role, null)) {
            BeanUtils.copyProperties(role, roleDto);
        }
        // 手动设置的属性 createTime permissions
        if (!Objects.equals(role.getRoleCreateTime(), null)) {
            roleDto.setCreateTime(DateTimeUtils.getDateTime(role.getRoleCreateTime()));
        }
        Set<PermissionSmallDto> permissions = new HashSet<>();
        if (!Objects.equals(role.getRolePermissions(), null) && !role.getRolePermissions().isEmpty()) {
            permissions =
                    role.getRolePermissions().stream()
                            .map(permission -> {
                                PermissionSmallDto permissionSmallDto = new PermissionSmallDto();
                                permissionSmallDto.setPermissionId(permission.getPermissionId());
                                permissionSmallDto.setPermissionName(permission.getPermissionName());
                                return permissionSmallDto;
                            })
                            .collect(Collectors.toCollection(HashSet::new));
        }
        roleDto.setPermissions(permissions);
        return roleDto;
    }

    /**
     * 将 RoleDto 转换为 Role
     * @param roleDto 需要转换的 RoleDto
     * @return Role
     */
    public static Role dtoToRole(RoleDto roleDto) {
        Role role = new Role();
        // 复制属性
        if (!Objects.equals(roleDto, null)) {
            BeanUtils.copyProperties(roleDto, role);
        }
        // 手动设置 createTime permissions
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_TIMESTAMP);
        if (StringUtils.hasText(roleDto.getCreateTime())) {
            try {
                role.setRoleCreateTime(simpleDateFormat.parse(roleDto.getCreateTime()));
            } catch (ParseException e) {
                log.info(" { 日期转换异常 } " + e.getLocalizedMessage());
                role.setRoleCreateTime(null);
            }
        }
        Set<Permission> permissions = new HashSet<>();
        if (!Objects.equals(roleDto.getPermissions(), null) && !roleDto.getPermissions().isEmpty()) {
            permissions =
                    roleDto.getPermissions()
                            .stream()
                            .map(permissionSmallDto -> {
                                Permission permission = new Permission();
                                permission.setPermissionId(permissionSmallDto.getPermissionId());
                                permission.setPermissionName(permissionSmallDto.getPermissionName());
                                return permission;
                            })
                            .collect(Collectors.toCollection(HashSet::new));
        }
        role.setRolePermissions(permissions);
        return role;
    }

    /**
     * 将 RoleVo 转换为 Role
     * @param roleVo 需要转换的 RoleVo
     * @return Role
     */
    public static Role voToRole(RoleVo roleVo) {
        Role role = new Role();
        // 复制属性
        if (!Objects.equals(roleVo, null)) {
            BeanUtils.copyProperties(roleVo, role);
        }
        Set<Permission> permissions = new HashSet<>();
        if (!roleVo.getPermissionIds().isEmpty() && !Objects.equals(roleVo.getPermissionIds(), null)) {
            roleVo.getPermissionIds().stream().forEach(permissionId -> {
                Permission permission = new Permission();
                permission.setPermissionId(permissionId);
                permissions.add(permission);
            });
        }
        role.setRolePermissions(permissions);
        return role;
    }
}
