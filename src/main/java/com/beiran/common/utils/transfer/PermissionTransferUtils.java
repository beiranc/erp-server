package com.beiran.common.utils.transfer;

import com.beiran.common.utils.DateTimeUtils;
import com.beiran.core.system.dto.PermissionDto;
import com.beiran.core.system.entity.Permission;
import com.beiran.core.system.vo.PermissionVo;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 提供 Permission-PermissionDto-PermissionVo 相互转换的工具
 */
@Log
public class PermissionTransferUtils {

    /**
     * 将 PermissionDto 转换为 Permission
     * @param permissionDto 需要转换的 PermissionDto
     * @return Permission
     */
    public static Permission dtoToPermission(PermissionDto permissionDto) {
        Permission permission = new Permission();
        if (!Objects.equals(permissionDto, null)) {
            BeanUtils.copyProperties(permissionDto, permission);
        }
        Date createTime = null;
        if (StringUtils.hasText(permissionDto.getCreateTime())) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_TIMESTAMP);
            try {
                createTime = simpleDateFormat.parse(permissionDto.getCreateTime());
            } catch (ParseException e) {
                log.info(" { 日期转换异常 } " + e.getLocalizedMessage());
            }
        }
        permission.setPermissionCreateTime(createTime);
        return permission;
    }

    /**
     * 将 Permission 转换为 PermissionDto
     * @param permission 需要转换的 Permission
     * @return PermissionDto
     */
    public static PermissionDto permissionToDto(Permission permission) {
        PermissionDto permissionDto = new PermissionDto();
        if (!Objects.equals(permission, null)) {
            BeanUtils.copyProperties(permission, permissionDto);
        }
        if (!Objects.equals(permission.getPermissionCreateTime(), null)) {
            permissionDto.setCreateTime(DateTimeUtils.getDateTime(permission.getPermissionCreateTime()));
        }
        return permissionDto;
    }

    /**
     * 将 PermissionVo 转换为 Permission
     * @param permissionVo 需要转换的 PermissionVo
     * @return Permission
     */
    public static Permission voToPermission(PermissionVo permissionVo) {
        Permission permission = new Permission();
        if (!Objects.equals(permissionVo, null)) {
            BeanUtils.copyProperties(permissionVo, permission);
        }
        return permission;
    }
}
