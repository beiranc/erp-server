package com.beiran.core.system.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.system.dto.RoleDto;
import com.beiran.core.system.entity.Role;
import com.beiran.core.system.service.RoleService;
import com.beiran.core.system.vo.RoleVo;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色接口<br>
 * 角色添加权限: system:role:add<br>
 * 角色修改权限: system:role:edit<br>
 * 角色删除权限: system:role:del<br>
 * 角色查询权限: system:role:view<br>
 * 角色导出权限: system:role:export<br>
 */

@RestController
@RequestMapping("/api/v1/roles")
@Api(tags = "系统管理：角色管理")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 创建角色
     * @param roleVo 待添加的角色
     * @return 保存后的角色
     */
    @PostMapping
    @LogRecord("创建角色")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:role:add')")
    @ApiOperation("创建角色")
    public ResponseModel save(@RequestBody @Valid RoleVo roleVo) {
        RoleDto admin = roleService.getRoleByName("admin");
        if (!Objects.equals(admin, null) && Objects.equals(admin.getRoleName(), roleVo.getRoleName())) {
            return ResponseModel.error("ADMIN 角色无法重复创建");
        } else {
            return ResponseModel.ok("创建成功", roleService.createRole(roleVo));
        }
    }

    /**
     * 修改角色
     * @param role 待修改的角色
     * @return 修改后的角色
     */
    @PutMapping
    @LogRecord("修改角色")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:role:edit')")
    @ApiOperation("修改角色")
    public ResponseModel update(@RequestBody Role role) {
        if (Objects.equals(role.getRoleName(), "admin")) {
            return ResponseModel.error("ADMIN 角色禁止修改");
        } else {
            return ResponseModel.ok("修改成功", roleService.update(role));
        }
    }

    /**
     * 批量删除角色
     * @param roleIds 待删除的角色编号集合
     * @return 提示信息
     */
    @DeleteMapping
    @LogRecord("批量删除角色")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:role:del')")
    @ApiOperation("批量删除角色")
    public ResponseModel delete(@RequestBody List<String> roleIds) {
        RoleDto admin = roleService.getRoleByName("admin");
        if (!Objects.equals(admin, null)) {
            // 如果要删除的角色中有 admin, 则将其移除
            roleIds.removeIf(roleId -> Objects.equals(roleId, admin.getRoleId()));
        }
        List<Role> roles = roleIds.stream().map(roleId -> {
            Role role = new Role();
            role.setRoleId(roleId);
            return role;
        }).collect(Collectors.toList());
        roleService.deleteAll(roles);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 分页查询
     * @param pageable 分页参数
     * @return 分页数据
     */
    @GetMapping
    @LogRecord("分页查询角色")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:role:view')")
    @ApiOperation("分页查询角色")
    public ResponseModel getRoles(@PageableDefault Pageable pageable) {
        return ResponseModel.ok(roleService.findAll(pageable));
    }

    /**
     * 根据角色名查询角色
     * @param roleName 角色名
     * @return
     */
    @GetMapping("/name")
    @LogRecord("通过角色名查询角色")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:role:view')")
    @ApiOperation("通过角色名查询角色")
    public ResponseModel getRoleByName(@RequestParam("roleName") String roleName) {
        RoleDto roleDto = roleService.getRoleByName(roleName);
        if (!Objects.equals(roleDto, null)) {
            return ResponseModel.ok(roleDto);
        } else {
            return ResponseModel.error("角色不存在");
        }
    }

    /**
     * 根据角色名模糊查询角色
     * @param roleName 角色名
     * @return
     */
    @GetMapping("/search")
    @LogRecord("通过角色名模糊查询角色")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:role:view')")
    @ApiOperation("通过角色名模糊查询角色")
    public ResponseModel getRolesByName(@RequestParam("roleName") String roleName) {
        return ResponseModel.ok(roleService.getRolesByName(roleName));
    }

    /**
     * 根据角色描述查询角色
     * @param roleDesc 角色描述
     * @return
     */
    @GetMapping("/desc")
    @LogRecord("通过角色描述模糊查询角色")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:role:view')")
    @ApiOperation("通过角色描述模糊查询角色")
    public ResponseModel getRolesByDesc(@RequestParam("roleDesc") String roleDesc) {
        return ResponseModel.ok(roleService.getRolesByDesc(roleDesc));
    }

    /**
     * 根据创建时间查询角色
     * @param leftTime 创建时间左区间
     * @param rightTime 创建时间右区间
     * @return
     */
    @GetMapping("/time")
    @LogRecord("通过创建时间查询角色")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:role:view')")
    @ApiOperation("根据创建时间查询角色")
    public ResponseModel getRolesByTime(@RequestParam("leftTime") Date leftTime,
                                              @RequestParam("rightTime") Date rightTime) {
        return ResponseModel.ok(roleService.getRolesByTime(leftTime, rightTime));
    }

    /**
     * 导出角色
     * @param pageable 分页参数
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出角色")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:role:export')")
    @ApiOperation("导出角色信息")
    public void exportRoles(@PageableDefault(size = 200) Pageable pageable,
                            HttpServletResponse response) throws Exception {
        File file = roleService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }
}
