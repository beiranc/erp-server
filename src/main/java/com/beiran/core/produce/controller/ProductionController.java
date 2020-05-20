package com.beiran.core.produce.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.produce.entity.ProductionDemand;
import com.beiran.core.produce.service.ProductionService;
import com.beiran.core.produce.vo.ProductionVo;
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
 * 生产需求计划接口<br>
 * 创建生产需求计划权限: produce:add<br>
 * 修改生产需求计划权限: produce:edit<br>
 * 删除生产需求计划权限: produce:del<br>
 * 查询生产需求计划权限: produce:view<br>
 * 导出生产需求计划权限: produce:export<br>
 */

@RestController
@RequestMapping("/api/v1/produce")
@Api(tags = "生产管理")
public class ProductionController {

    @Autowired
    private ProductionService productionService;

    /* ------------------------------------ 生产需求计划相关 ------------------------------------ */

    /**
     * 创建生产需求计划
     * @param productionVo
     * @return
     */
    @PostMapping
    @LogRecord("创建生产需求计划")
    @PreAuthorize("@erp.check('produce:add')")
    @ApiOperation("创建生产需求计划")
    public ResponseModel saveProduction(@RequestBody @Valid ProductionVo productionVo) {
        return ResponseModel.ok(productionService.createProductionDemand(productionVo));
    }

    /**
     * 修改生产需求计划
     * @param productionDemand
     * @return
     */
    @PutMapping
    @LogRecord("修改生产需求计划")
    @PreAuthorize("@erp.check('produce:edit')")
    @ApiOperation("修改生产需求计划")
    public ResponseModel updateProduction(@RequestBody ProductionDemand productionDemand) {
        return ResponseModel.ok(productionService.update(productionDemand));
    }

    /**
     * 确认生产需求计划
     * @param productionId
     * @return
     */
    @PutMapping("/confirm")
    @LogRecord("确认生产需求计划")
    @PreAuthorize("@erp.check('produce:edit') and @erp.check('produceManager')")
    @ApiOperation("确认生产需求计划")
    public ResponseModel confirmProduction(@RequestParam("productionId") String productionId) {
        boolean result = productionService.confirmProduction(productionId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 校验生产产品
     * @param productionId
     * @return
     */
    @PutMapping("/verify")
    @LogRecord("校验生产的产品")
    @PreAuthorize("@erp.check('produce:edit')")
    @ApiOperation("校验生产的产品")
    public ResponseModel verifyProduction(@RequestParam("productionId") String productionId) {
        boolean result = productionService.verifyProduction(productionId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 生产的产品入库
     * @param productionId
     * @return
     */
    @PutMapping("/import")
    @LogRecord("生产的产品入库")
    @PreAuthorize("@erp.check('produce:edit')")
    @ApiOperation("生产的产品入库")
    public ResponseModel importProduction(@RequestParam("productionId") String productionId) {
        boolean result = productionService.importProduction(productionId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 重生产产品
     * @param productionId
     * @return
     */
    @PutMapping("/reproduce")
    @LogRecord("重生产产品")
    @PreAuthorize("@erp.check('produce:edit')")
    @ApiOperation("重生产产品")
    public ResponseModel reproduceProduction(@RequestParam("productionId") String productionId) {
        boolean result = productionService.reproduceProduction(productionId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 关闭生产需求计划
     * @param productionId
     * @return
     */
    @PutMapping("/close")
    @LogRecord("关闭生产需求计划")
    @PreAuthorize("@erp.check('produce:edit') and @erp.check('produceManager')")
    @ApiOperation("关闭生产需求计划")
    public ResponseModel closeProduction(@RequestParam("productionId") String productionId) {
        boolean result = productionService.closeProduction(productionId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 删除生产需求计划
     * @param productionIds
     * @return
     */
    @DeleteMapping
    @LogRecord("删除生产需求计划")
    @PreAuthorize("@erp.check('produce:del') and @erp.check('produceManager')")
    @ApiOperation("删除生产需求计划")
    public ResponseModel deleteProduction(@RequestBody List<String> productionIds) {
        if (Objects.equals(productionIds, null) || productionIds.isEmpty()) {
            return ResponseModel.error("需要删除的生产需求计划不能为空");
        }
        List<ProductionDemand> productionDemands = productionIds.stream().map(productionId -> {
            ProductionDemand productionDemand = new ProductionDemand();
            productionDemand.setProductionId(productionId);
            return productionDemand;
        }).collect(Collectors.toList());
        productionService.deleteAll(productionDemands);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 通过用户名获取生产需求计划
     * @param userName
     * @param pageable
     * @return
     */
    @GetMapping("/user_name")
    @LogRecord("通过用户名获取生产需求计划")
    @PreAuthorize("@erp.check('produce:view')")
    @ApiOperation("通过用户名获取生产需求计划")
    public ResponseModel getProductionsByUserName(@RequestParam("userName") String userName,
                                                  @PageableDefault Pageable pageable) {
        return ResponseModel.ok(productionService.getProductionsByUserName(userName, pageable));
    }

    /**
     * 通过用户名与状态获取生产需求计划
     * @param userName
     * @param productionState
     * @param pageable
     * @return
     */
    @GetMapping("/name_state")
    @LogRecord("通过用户名与状态获取生产需求计划")
    @PreAuthorize("@erp.check('produce:view')")
    @ApiOperation("通过用户名与状态获取生产需求计划")
    public ResponseModel getProductionsByUserNameAndState(@RequestParam("userName") String userName,
                                                          @RequestParam("state") ProductionDemand.ProductionDemandState productionState,
                                                          @PageableDefault Pageable pageable) {
        return ResponseModel.ok(productionService.getProductionsByUserNameAndState(userName, productionState, pageable));
    }

    /**
     * 通过状态获取生产需求计划
     * @param productionState
     * @param pageable
     * @return
     */
    @GetMapping("/state")
    @LogRecord("通过状态获取生产需求计划")
    @PreAuthorize("@erp.check('produce:view') and @erp.check('produceManager')")
    @ApiOperation("通过状态获取生产需求计划")
    public ResponseModel getProductionsByState(@RequestParam("productionState") ProductionDemand.ProductionDemandState productionState,
                                               @PageableDefault Pageable pageable) {
        return ResponseModel.ok(productionService.getProductionsByState(productionState, pageable));
    }

    /**
     * 通过主题获取生产需求计划
     * @param productionSubject
     * @param pageable
     * @return
     */
    @GetMapping("/subject")
    @LogRecord("通过主题获取生产需求计划")
    @PreAuthorize("@erp.check('produce:view') and @erp.check('produceManager')")
    @ApiOperation("通过主题获取生产需求计划")
    public ResponseModel getProductionsBySubject(@RequestParam("productionSubject") String productionSubject,
                                                 @PageableDefault Pageable pageable) {
        return ResponseModel.ok(productionService.getProductionsBySubject(productionSubject, pageable));
    }

    /**
     * 通过创建时间获取生产需求计划
     * @param leftTime
     * @param rightTime
     * @param pageable
     * @return
     */
    @GetMapping("/create_time")
    @LogRecord("通过创建时间获取生产需求计划")
    @PreAuthorize("@erp.check('produce:view') and @erp.check('produceManager')")
    @ApiOperation("通过创建时间获取生产需求计划")
    public ResponseModel getProductionsByCreateTime(@RequestParam("leftTime") Date leftTime,
                                                    @RequestParam("rightTime") Date rightTime,
                                                    @PageableDefault Pageable pageable) {
        if (Objects.equals(leftTime, null) && Objects.equals(rightTime, null)) {
            return ResponseModel.ok(productionService.getProductionsByCreateTime(new Date(), new Date(), pageable));
        } else {
            return ResponseModel.ok(productionService.getProductionsByCreateTime(leftTime, rightTime, pageable));
        }
    }

    /**
     * 通过上一次修改时间获取生产需求计划
     * @param leftTime
     * @param rightTime
     * @param pageable
     * @return
     */
    @GetMapping("/modified_time")
    @LogRecord("通过上一次修改时间获取生产需求计划")
    @PreAuthorize("@erp.check('produce:view') and @erp.check('produceManager')")
    @ApiOperation("通过上一次修改时间获取生产需求计划")
    public ResponseModel getProductionsByModifiedTime(@RequestParam("leftTime") Date leftTime,
                                                    @RequestParam("rightTime") Date rightTime,
                                                    @PageableDefault Pageable pageable) {
        if (Objects.equals(leftTime, null) && Objects.equals(rightTime, null)) {
            return ResponseModel.ok(productionService.getProductionsByModifiedTime(new Date(), new Date(), pageable));
        } else {
            return ResponseModel.ok(productionService.getProductionsByModifiedTime(leftTime, rightTime, pageable));
        }
    }

    /**
     * 导出指定用户的生产需求计划
     * @param userName
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/export_spec")
    @LogRecord("导出指定用户的生产需求计划")
    @PreAuthorize("@erp.check('produce:export')")
    @ApiOperation("导出指定用户的生产需求计划")
    public void exportSpec(@RequestParam("userName") String userName,
                           @PageableDefault(size = 200) Pageable pageable,
                           HttpServletResponse response) throws Exception {
        File file = productionService.createSpecExcelFile(userName, pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /**
     * 导出生产需求计划
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出生产需求计划")
    @PreAuthorize("@erp.check('produce:export') and @erp.check('produceManager')")
    @ApiOperation("导出生产需求计划")
    public void export(@PageableDefault(size = 200) Pageable pageable,
                       HttpServletResponse response) throws Exception {
        File file = productionService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /* ------------------------------------ 生产需求计划详细项相关 ------------------------------- */

    /**
     * 修改生产数量
     * @param productionDetailId
     * @param productionNumber
     * @return
     */
    @PutMapping("/details")
    @LogRecord("修改生产数量")
    @PreAuthorize("@erp.check('produce:edit')")
    @ApiOperation("修改生产数量")
    public ResponseModel updateProductionDetailNumber(@RequestParam("detailId") String productionDetailId,
                                                      @RequestParam("number") Long productionNumber) {
        boolean result = productionService.updateProductionDetailNumber(productionDetailId, productionNumber);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 导出生产需求计划子项
     * @param productionId
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/details/export")
    @LogRecord("导出生产需求计划子项")
    @PreAuthorize("@erp.check('produce:export')")
    @ApiOperation("导出生产需求计划子项")
    public void exportDetails(@RequestParam("productionId") String productionId,
                              @PageableDefault(size = 200) Pageable pageable,
                              HttpServletResponse response) throws Exception {
        File file = productionService.createDetailExcelFile(productionId, pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }
}
