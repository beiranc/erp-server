package com.beiran.core.system.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.system.dto.PermissionDto;
import com.beiran.core.system.entity.Permission;
import com.beiran.core.system.service.PermissionService;
import com.beiran.core.system.vo.PermissionVo;
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
 * 权限接口<br>
 * 创建权限: system:permission:add<br>
 * 修改权限: system:permission:edit<br>
 * 删除权限: system:permission:del<br>
 * 查看权限: system:permission:view<br>
 * 导出权限: system:permission:export<br>
 */

@RestController
@RequestMapping("/api/v1/permissions")
@Api(tags = "系统管理：权限管理")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 创建一个权限
     * @param permissionVo
     * @return
     */
    @PostMapping
    @LogRecord("创建一个权限")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:permission:add')")
    @ApiOperation("创建一个权限")
    public ResponseModel save(@RequestBody @Valid PermissionVo permissionVo) {
        return ResponseModel.ok(permissionService.createPermission(permissionVo));
    }

    /**
     * 修改一个权限
     * @param permission 待修改的权限
     * @return
     */
    @PutMapping
    @LogRecord("修改一个权限")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:permission:edit')")
    @ApiOperation("修改一个权限")
    public ResponseModel update(@RequestBody Permission permission) {
        return ResponseModel.ok(permissionService.update(permission));
    }

    /**
     * 删除权限
     * @param permissionIds 待删除的权限编号集合
     * @return
     */
    @DeleteMapping
    @LogRecord("删除权限")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:permission:del')")
    @ApiOperation("删除权限")
    public ResponseModel delete(@RequestBody List<String> permissionIds) {
        if (Objects.equals(permissionIds, null) && permissionIds.isEmpty()) {
            return ResponseModel.error("需要删除的权限不能为空");
        } else {
            List<Permission> permissions = permissionIds.stream().map(permissionId -> {
                Permission permission = new Permission();
                permission.setPermissionId(permissionId);
                return permission;
            }).collect(Collectors.toList());
            permissionService.deleteAll(permissions);
            return ResponseModel.ok("删除成功");
        }
    }

    /**
     * 分页查询权限
     * @param pageable
     * @return
     */
    @GetMapping
    @LogRecord("分页查询权限")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:permission:view')")
    @ApiOperation("分页查询权限")
    public ResponseModel getPermissions(@PageableDefault Pageable pageable) {
        return ResponseModel.ok(permissionService.findAll(pageable));
    }

    /**
     * 查询一个权限
     * @param permissionName 权限名
     * @return
     */
    @GetMapping("/name")
    @LogRecord("查询一个权限")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:permission:view')")
    @ApiOperation("查询一个权限")
    public ResponseModel getPermissionByName(@RequestParam("permissionName") String permissionName) {
        PermissionDto permissionDto = permissionService.getPermissionByName(permissionName);
        if (!Objects.equals(permissionDto, null)) {
            return ResponseModel.ok(permissionDto);
        } else {
            return ResponseModel.error("权限不存在");
        }
    }

    /**
     * 通过权限名搜索权限
     * @param permissionName 权限名
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/search")
    @LogRecord("通过权限名搜索权限")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:permission:view')")
    @ApiOperation("通过权限名搜索权限")
    public ResponseModel getPermissionsByName(@RequestParam("permissionName") String permissionName,
                                              @PageableDefault Pageable pageable) {
        return ResponseModel.ok(permissionService.getPermissionsByName(permissionName, pageable));
    }

    /**
     * 通过权限描述搜索权限
     * @param permissionDesc 权限描述
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/desc")
    @LogRecord("通过权限描述搜索权限")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:permission:view')")
    @ApiOperation("通过权限描述搜索权限")
    public ResponseModel getPermissionsByDesc(@RequestParam("permissionDesc") String permissionDesc,
                                              @PageableDefault Pageable pageable) {
        return ResponseModel.ok(permissionService.getPermissionsByDesc(permissionDesc, pageable));
    }

    /**
     * 通过权限创建时间查询权限
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/time")
    @LogRecord("通过权限创建时间查询权限")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:permission:view')")
    @ApiOperation("通过权限创建时间查询权限")
    public ResponseModel getPermissionsByTime(@RequestParam("leftTime") Date leftTime,
                                              @RequestParam("rightTime") Date rightTime,
                                              @PageableDefault Pageable pageable) {
        if (Objects.equals(leftTime, null) && Objects.equals(rightTime, null)) {
            return ResponseModel.ok(permissionService.findAll(pageable));
        } else if (Objects.equals(rightTime, null) && !Objects.equals(leftTime, null)) {
            return ResponseModel.ok(permissionService.getPermissionsByTime(leftTime, new Date(), pageable));
        } else if (!Objects.equals(rightTime, null) && Objects.equals(leftTime, null)) {
            return ResponseModel.ok(permissionService.findAll(pageable));
        } else {
            return ResponseModel.ok(permissionService.getPermissionsByTime(leftTime, rightTime, pageable));
        }
    }

    /**
     * 导出权限
     * @param pageable 分页参数
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出权限")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:permission:export')")
    @ApiOperation("导出权限")
    public void export(@PageableDefault Pageable pageable,
                       HttpServletResponse response) throws Exception {
        File file = permissionService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }
}
