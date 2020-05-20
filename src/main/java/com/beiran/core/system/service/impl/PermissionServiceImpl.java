package com.beiran.core.system.service.impl;

import com.beiran.common.exception.EntityExistException;
import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.DateTimeUtils;
import com.beiran.common.utils.FileUtils;
import com.beiran.common.utils.transfer.PermissionTransferUtils;
import com.beiran.core.system.dto.PermissionDto;
import com.beiran.core.system.entity.Permission;
import com.beiran.core.system.repository.PermissionRepository;
import com.beiran.core.system.service.PermissionService;
import com.beiran.core.system.vo.PermissionVo;
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
 * PermissionService 接口的实现类
 */

@Service("permissionService")
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public PermissionDto createPermission(PermissionVo permissionVo) {
        if (Objects.equals(permissionVo, null)) {
            throw new ParameterException("需要保存的权限不能为空");
        }
        Permission permission = PermissionTransferUtils.voToPermission(permissionVo);
        Permission save = save(permission);
        return PermissionTransferUtils.permissionToDto(save);
    }

    @Override
    public PermissionDto getPermissionByName(String permissionName) {
        if (!StringUtils.hasText(permissionName)) {
            throw new ParameterException("权限名不能为空");
        }
        // 若出错设为 null 由调用方判断
        Permission permission = permissionRepository.findByPermissionName(permissionName).orElse(null);
        if (Objects.equals(permission, null)) {
            return null;
        } else {
            return PermissionTransferUtils.permissionToDto(permission);
        }
    }

    @Override
    public List<PermissionDto> getPermissionsByName(String permissionName, Pageable pageable) {
        if (!StringUtils.hasText(permissionName)) {
            throw new ParameterException("权限名不能为空");
        }
        List<Permission> permissions = permissionRepository.findByPermissionNameContaining(permissionName, pageable);
        List<PermissionDto> permissionDtos =
                permissions.stream()
                        .map(permission -> PermissionTransferUtils.permissionToDto(permission))
                        .collect(Collectors.toList());
        return permissionDtos;
    }

    @Override
    public List<PermissionDto> getPermissionsByDesc(String permissionDesc, Pageable pageable) {
        if (!StringUtils.hasText(permissionDesc)) {
            throw new ParameterException("权限描述不能为空");
        }
        List<Permission> permissions = permissionRepository.findByPermissionDescContaining(permissionDesc, pageable);
        List<PermissionDto> permissionDtos =
                permissions.stream()
                        .map(permission -> PermissionTransferUtils.permissionToDto(permission))
                        .collect(Collectors.toList());
        return permissionDtos;
    }

    @Override
    public List<PermissionDto> getPermissionsByTime(Date leftTime, Date rightTime, Pageable pageable) {
        if (Objects.equals(leftTime, null) || Objects.equals(rightTime, null)) {
            throw new ParameterException("权限创建时间范围不能为空");
        }
        List<Permission> permissions = permissionRepository.findByPermissionCreateTimeBetween(leftTime, rightTime, pageable);
        List<PermissionDto> permissionDtos =
                permissions.stream()
                        .map(permission -> PermissionTransferUtils.permissionToDto(permission))
                        .collect(Collectors.toList());
        return permissionDtos;
    }

    @Override
    public Set<PermissionDto> getPermissionsByRoleId(String roleId) {
        if (!StringUtils.hasText(roleId)) {
            throw new ParameterException("角色编号不能为空");
        }
        Set<Permission> permissions = permissionRepository.findByPermissionRoles_RoleId(roleId);
        Set<PermissionDto> permissionDtos =
                permissions.stream()
                        .map(permission -> PermissionTransferUtils.permissionToDto(permission))
                        .collect(Collectors.toCollection(HashSet::new));
        return permissionDtos;
    }

    @Override
    public File createExcelFile(Pageable pageable) {
        // 根据分页信息获取数据
        Page<Permission> permissionPage = findAll(pageable);
        List<Permission> permissions = permissionPage.getContent();
        if (Objects.equals(permissions, null)) {
            permissions = new ArrayList<>();
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("权限编号");
        rowInfo.createCell(++columnIndex).setCellValue("权限名称");
        rowInfo.createCell(++columnIndex).setCellValue("权限描述");
        rowInfo.createCell(++columnIndex).setCellValue("权限创建时间");

        for (int i = 0; i < permissions.size(); i++) {
            Permission permission = permissions.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(permission.getPermissionId());
            row.getCell(++columnIndex).setCellValue(permission.getPermissionName());
            row.getCell(++columnIndex).setCellValue(permission.getPermissionDesc());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(permission.getPermissionCreateTime()));
        }
        return FileUtils.createExcelFile(workbook, "erp_permissions");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Permission save(Permission entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的权限不能为空");
        }
        Permission permission = permissionRepository.findByPermissionName(entity.getPermissionName()).orElse(null);
        if (Objects.equals(permission, null)) {
            return permissionRepository.save(entity);
        } else {
            throw new EntityExistException("权限已存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Permission> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的权限不能为空");
        }
        permissionRepository.deleteAll(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Permission update(Permission entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要更改的权限不能为空");
        }
        return permissionRepository.saveAndFlush(entity);
    }

    @Override
    public Permission findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("权限编号不能为空");
        }
        return permissionRepository.findById(id).orElseThrow(() -> new EntityNotExistException("权限不存在"));
    }

    @Override
    public Page<Permission> findAll(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }
}
