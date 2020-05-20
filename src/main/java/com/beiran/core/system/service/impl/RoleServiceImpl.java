package com.beiran.core.system.service.impl;

import com.beiran.common.exception.EntityExistException;
import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.DateTimeUtils;
import com.beiran.common.utils.FileUtils;
import com.beiran.common.utils.transfer.RoleTransferUtils;
import com.beiran.core.system.dto.RoleDto;
import com.beiran.core.system.entity.Permission;
import com.beiran.core.system.entity.Role;
import com.beiran.core.system.repository.RoleRepository;
import com.beiran.core.system.service.RoleService;
import com.beiran.core.system.vo.RoleVo;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * RoleService 接口的实现类<br>
 * 若参数为空，则会抛出 ParameterException<br>
 * 若通过传入的 ID 未找到对应的实体，则会抛出 EntityNotExistException
 */

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleDto createRole(RoleVo roleVo) {
        Role role = RoleTransferUtils.voToRole(roleVo);
        Role save = save(role);
        return RoleTransferUtils.roleToDto(save);
    }

    @Override
    public RoleDto getRoleByName(String roleName) {
        if (!StringUtils.hasText(roleName)) {
            throw new ParameterException("角色名不能为空");
        }
        // 这边直接设为 null 让调用方判断
        Role role = roleRepository.findByRoleName(roleName).orElse(null);
        if (Objects.equals(role, null)) {
            return null;
        } else {
            return RoleTransferUtils.roleToDto(role);
        }
    }

    @Override
    public List<RoleDto> getRolesByName(String roleName) {
        if (!StringUtils.hasText(roleName)) {
            throw new ParameterException("角色名不能为空");
        }
        List<Role> roles = roleRepository.findByRoleNameContaining(roleName);
        List<RoleDto> roleDtos =
                roles.stream()
                        .map(role -> RoleTransferUtils.roleToDto(role))
                        .collect(Collectors.toList());
        return roleDtos;
    }

    @Override
    public List<RoleDto> getRolesByDesc(String roleDesc) {
        if (!StringUtils.hasText(roleDesc)) {
            throw new ParameterException("角色描述不能为空");
        }
        List<Role> roles = roleRepository.findByRoleDescContaining(roleDesc);
        List<RoleDto> roleDtos =
                roles.stream()
                        .map(role -> RoleTransferUtils.roleToDto(role))
                        .collect(Collectors.toList());
        return roleDtos;
    }

    @Override
    public Set<RoleDto> getRolesByUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new ParameterException("用户编号不能为空");
        }
        Set<Role> roles = roleRepository.findByRoleUsers_UserId(userId);
        Set<RoleDto> roleDtos =
                roles.stream()
                        .map(role -> RoleTransferUtils.roleToDto(role))
                        .collect(Collectors.toSet());
        return roleDtos;
    }

    @Override
    public List<RoleDto> getRolesByTime(Date leftTime, Date rightTime) {
        if (Objects.equals(leftTime, null) || Objects.equals(rightTime, null)) {
            throw new ParameterException("角色创建时间不能为空");
        }
        List<Role> roles = roleRepository.findByRoleCreateTimeBetween(leftTime, rightTime);
        List<RoleDto> roleDtos =
                roles.stream()
                        .map(role -> RoleTransferUtils.roleToDto(role))
                        .collect(Collectors.toList());
        return roleDtos;
    }

    @Override
    public File createExcelFile(Pageable pageable) {
        // 根据分页信息获取数据
        Page<Role> rolePage = roleRepository.findAll(pageable);
        List<Role> roles = rolePage.getContent();
        if (Objects.equals(roles, null)) {
            roles = new ArrayList<>();
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("角色编号");
        rowInfo.createCell(++columnIndex).setCellValue("角色名称");
        rowInfo.createCell(++columnIndex).setCellValue("角色描述");
        rowInfo.createCell(++columnIndex).setCellValue("角色创建时间");
        rowInfo.createCell(++columnIndex).setCellValue("角色权限");

        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(role.getRoleId());
            row.getCell(++columnIndex).setCellValue(role.getRoleName());
            row.getCell(++columnIndex).setCellValue(role.getRoleDesc());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(role.getRoleCreateTime()));
            String rolePermission = role.getRolePermissions().stream().map(Permission::getPermissionName).collect(Collectors.joining(", ", "[ ", " ]"));
            row.getCell(++columnIndex).setCellValue(rolePermission);
        }
        return FileUtils.createExcelFile(workbook, "erp_roles");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role save(Role entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("角色不能为空");
        }
        Role role = roleRepository.findByRoleName(entity.getRoleName()).orElse(null);
        if (Objects.equals(role, null)) {
            return roleRepository.save(entity);
        } else {
            throw new EntityExistException("角色已存在");
        }
    }

    @Override
    public void deleteAll(List<Role> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的角色不能为空");
        }
        roleRepository.deleteAll(entities);
    }

    @Override
    public Role update(Role entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的角色不能为空");
        }
        return roleRepository.saveAndFlush(entity);
    }

    @Override
    public Role findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("角色编号不能为空");
        }
        return roleRepository.findById(id).orElseThrow(() -> new EntityNotExistException("角色不存在"));
    }

    @Override
    public Page<Role> findAll(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }
}
