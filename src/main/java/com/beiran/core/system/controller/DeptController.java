package com.beiran.core.system.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.system.dto.DeptDto;
import com.beiran.core.system.entity.Dept;
import com.beiran.core.system.repository.DeptRepository;
import com.beiran.core.system.service.DeptService;
import com.beiran.core.system.vo.DeptVo;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 部门接口<br>
 * 部门创建权限: system:dept:add<br>
 * 部门修改权限: system:dept:edit<br>
 * 部门查询权限: system:dept:view<br>
 * 部门删除权限: system:dept:del<br>
 * 部门导出权限: system:dept:export<br>
 */

@RestController
@RequestMapping("/api/v1/depts")
@Api(tags = "系统管理：部门管理")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @Autowired
    private DeptRepository deptRepository;

    /**
     * 创建部门
     * @param deptVo 待创建的部门数据
     * @return
     */
    @PostMapping
    @LogRecord("创建部门")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:dept:add')")
    @ApiOperation("创建部门")
    public ResponseModel save(@RequestBody @Valid DeptVo deptVo) {
        return ResponseModel.ok(deptService.createDept(deptVo));
    }

    /**
     * 修改部门
     * @param dept 待修改的部门
     * @return
     */
    @PutMapping
    @LogRecord("修改部门")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:dept:edit')")
    @ApiOperation("修改部门")
    public ResponseModel update(@RequestBody Dept dept) {
        return ResponseModel.ok(deptService.update(dept));
    }

    /**
     * 修改部门状态
     * @param deptId 部门编号
     * @param deptState 部门状态
     * @return
     */
    @PutMapping("/state")
    @LogRecord("修改部门状态")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:dept:edit')")
    @ApiOperation("修改部门状态")
    public ResponseModel updateState(@RequestParam("deptId") String deptId,
                                     @RequestParam("deptState") Dept.DeptState deptState) {
        boolean result = deptService.updateState(deptId, deptState);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 删除部门
     * @param deptIds 待删除的部门编号集合
     * @return
     */
    @DeleteMapping
    @LogRecord("删除部门")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:dept:del')")
    @ApiOperation("删除部门")
    public ResponseModel delete(@RequestBody List<String> deptIds) {
        if (Objects.equals(deptIds, null) || deptIds.isEmpty()) {
            return ResponseModel.error("需要删除的部门不能为空");
        } else {
            List<Dept> depts = deptIds.stream().map(deptId -> {
                Dept dept = new Dept();
                dept.setDeptId(deptId);
                return dept;
            }).collect(Collectors.toList());
            deptService.deleteAll(depts);
            return ResponseModel.ok("删除成功");
        }
    }

    /**
     * 根据部门名查询一个部门
     * @param deptName 部门名
     * @return
     */
    @GetMapping("/name")
    @LogRecord("查询一个部门")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:dept:view')")
    @ApiOperation("查询一个部门")
    public ResponseModel getDeptByName(@RequestParam("deptName") String deptName) {
        DeptDto deptDto = deptService.getDeptByName(deptName);
        if (!Objects.equals(deptDto, null)) {
            return ResponseModel.ok(deptDto);
        } else {
            return ResponseModel.error("部门不存在");
        }
    }

    /**
     * 根据部门名模糊查询部门信息
     * @param deptName 部门名称
     * @return
     */
    @GetMapping("/search")
    @LogRecord("模糊查询部门")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:dept:view')")
    @ApiOperation("模糊查询部门")
    public ResponseModel getDeptsByName(@RequestParam("deptName") String deptName) {
        return ResponseModel.ok(deptService.getDeptsByName(deptName));
    }

    /**
     * 通过部门状态查询部门
     * @param deptState 部门状态
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/state")
    @LogRecord("通过部门状态查询部门")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:dept:view')")
    @ApiOperation("通过部门状态查询部门")
    public ResponseModel getDeptsByState(@RequestParam("deptState") Dept.DeptState deptState,
                                         @PageableDefault Pageable pageable) {
        return ResponseModel.ok(deptService.getDeptsByState(deptState, pageable));
    }

    /**
     * 获取部门关系树（前端界面显示可用，目前只支持到两级）
     * @return
     */
    @GetMapping("/tree")
    @LogRecord("获取部门关系树")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:dept:view')")
    @ApiOperation("获取部门关系树")
    public ResponseModel getDeptTree() {
        return ResponseModel.ok(deptService.getDeptTree());
    }

    /**
     * 获取所有父级部门
     * @return
     */
    @GetMapping("/parent")
    @LogRecord("获取所有父级部门")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:dept:view')")
    @ApiOperation("获取所有父级部门")
    public ResponseModel getParentDept() {
        return ResponseModel.ok(deptRepository.findByDeptParentIsNull());
    }

    /**
     * 查询所有部门
     * @param pageable
     * @return
     */
    @GetMapping
    @LogRecord("查询所有部门")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:dept:view')")
    @ApiOperation("查询所有部门")
    public ResponseModel getDepts(@PageableDefault Pageable pageable) {
        return ResponseModel.ok(deptService.findAll(pageable));
    }

    /**
     * 导出部门信息
     * @param pageable 分页参数
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出部门信息")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:dept:export')")
    @ApiOperation("导出部门信息")
    public void export(@PageableDefault(size = 200) Pageable pageable,
                       HttpServletResponse response) throws Exception {
        File file = deptService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }
}
