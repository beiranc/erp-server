package com.beiran.core.system.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.system.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 日志相关接口<br>
 * 查询所有日志的权限: system:log:view<br>
 * 查询特定用户的权限: system:log:spec<br>
 * 导出日志的权限: system:log:export<br>
 * 清空日志的权限: system:log:del<br>
 *
 */

@RestController
@RequestMapping("/api/v1/logs")
@Api(tags = "系统管理：日志管理")
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 查询所有的日志
     * @param pageable 分页参数
     * @return 返回带分页属性的数据
     */
    @GetMapping
    @PreAuthorize("@erp.check('admin') and @erp.check('system:log:view')")
    @ApiOperation("查询所有操作日志")
    public ResponseModel queryAll(@PageableDefault(sort = { "createTime" }, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseModel.ok(logService.getLogs(pageable));
    }

    /**
     * 查询特定用户操作日志<br>
     * Note: 此接口可开放给用户
     * @param userName 用户名
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/user_name")
    @PreAuthorize("@erp.check('system:log:spec')")
    @ApiOperation("查询特定用户操作日志")
    public ResponseModel querySpec(@RequestParam("userName") String userName,
                                   @PageableDefault(sort = { "createTime" }, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseModel.ok(logService.getLogsByName(userName, pageable));
    }

    /**
     * 导出所有日志
     * @param pageable 分页参数
     * @param response
     * @throws Exception
     */
    @GetMapping("/export_all")
    @LogRecord("导出所有操作日志")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:log:export')")
    @ApiOperation("导出所有操作日志")
    public void exportAll(@PageableDefault(size = 200, sort = { "createTime" }, direction = Sort.Direction.DESC) Pageable pageable,
                                   HttpServletResponse response) throws Exception {
        File file = logService.createLogsExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /**
     * 导出特定用户操作日志
     * @param userName 用户名
     * @param pageable 分页参数
     * @param response
     * @throws Exception
     */
    @GetMapping("/export_spec")
    @LogRecord("导出特定用户操作日志")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:log:export')")
    @ApiOperation("导出特定用户操作日志")
    public void exportSpec(@RequestParam("userName") String userName,
                           @PageableDefault(size = 200, sort = { "createTime" }, direction = Sort.Direction.DESC) Pageable pageable,
                           HttpServletResponse response) throws Exception {
        File file = logService.createSpecLogsExcelFileByName(userName, pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /**
     * 清空所有操作日志
     * @return
     */
    @DeleteMapping
    @LogRecord("清空所有操作日志")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:log:del')")
    @ApiOperation("清空所有操作日志")
    public ResponseModel flushAll() {
        logService.flushAll();
        return ResponseModel.ok("清空成功");
    }

    /**
     * 清空特定用户操作日志
     * @param userName 用户名
     * @return
     */
    @DeleteMapping("/flush_spec")
    @LogRecord("清空特定用户操作日志")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:log:del')")
    @ApiOperation("清空特定用户操作日志")
    public ResponseModel flushSpec(@RequestParam("userName") String userName) {
        boolean result = logService.flushByName(userName);
        if (result) {
            return ResponseModel.ok("清空成功");
        } else {
            return ResponseModel.error("清空失败");
        }
    }
}
