package com.beiran.core.purchase.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.purchase.entity.PurchaseOrder;
import com.beiran.core.purchase.service.PurchaseService;
import com.beiran.core.purchase.vo.PurchaseVo;
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
 * 采购管理接口<br>
 * 创建采购计划权限: purchase:add<br>
 * 修改采购计划权限: purchase:edit (部分修改状态功能需要 purchaseManager)<br>
 * 删除采购计划权限: purchase:del<br>
 * 查询采购计划权限: purchase:view<br>
 * 导出采购计划权限: purchase:export<br>
 */

@RestController
@RequestMapping("/api/v1/purchases")
@Api(tags = "采购管理")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    /* --------------------------------- 采购计划相关 -------------------------------------- */

    /**
     * 创建采购计划
     * @param purchaseVo
     * @return
     */
    @PostMapping
    @LogRecord("创建采购计划")
    @PreAuthorize("@erp.check('purchase:add')")
    @ApiOperation("创建采购计划")
    public ResponseModel savePurchase(@RequestBody @Valid PurchaseVo purchaseVo) {
        return ResponseModel.ok(purchaseService.createPurchaseOrder(purchaseVo));
    }

    /**
     * 修改采购计划
     * @param purchaseOrder
     * @return
     */
    @PutMapping
    @LogRecord("修改采购计划")
    @PreAuthorize("@erp.check('purchase:edit')")
    @ApiOperation("修改采购计划")
    public ResponseModel updatePurchase(@RequestBody PurchaseOrder purchaseOrder) {
        return ResponseModel.ok(purchaseService.update(purchaseOrder));
    }

    /**
     * 确认采购计划，需要 purchaseManager 角色
     * @param purchaseId
     * @return
     */
    @PutMapping("/confirm")
    @LogRecord("确认采购计划")
    @PreAuthorize("@erp.check('purchase:edit') and @erp.check('purchaseManager')")
    @ApiOperation("确认采购计划")
    public ResponseModel confirmPurchase(@RequestParam("purchaseId") String purchaseId) {
        boolean result = purchaseService.confirmPurchase(purchaseId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 校验采购物料，实际应当由库存部门的员工来修改
     * @param purchaseId
     * @return
     */
    @PutMapping("/verify")
    @LogRecord("校验采购物料")
    @PreAuthorize("@erp.check('purchase:edit')")
    @ApiOperation("校验采购物料")
    public ResponseModel verifyPurchase(@RequestParam("purchaseId") String purchaseId) {
        boolean result = purchaseService.verifyPurchase(purchaseId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 采购物料入库
     * @param purchaseId
     * @return
     */
    @PutMapping("/import")
    @LogRecord("采购物料入库")
    @PreAuthorize("@erp.check('purchase:edit')")
    @ApiOperation("采购物料入库")
    public ResponseModel importPurchase(@RequestParam("purchaseId") String purchaseId) {
        boolean result = purchaseService.importPurchase(purchaseId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 交涉采购物料
     * @param purchaseId
     * @return
     */
    @PutMapping("/discuss")
    @LogRecord("交涉采购物料")
    @PreAuthorize("@erp.check('purchase:edit')")
    @ApiOperation("交涉采购物料")
    public ResponseModel discussPurchase(@RequestParam("purchaseId") String purchaseId) {
        boolean result = purchaseService.discussPurchase(purchaseId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 关闭采购计划
     * @param purchaseId
     * @return
     */
    @PutMapping("/close")
    @LogRecord("关闭采购计划")
    @PreAuthorize("@erp.check('purchase:edit') and @erp.check('purchaseManager')")
    @ApiOperation("关闭采购计划")
    public ResponseModel closePurchase(@RequestParam("purchaseId") String purchaseId) {
        boolean result = purchaseService.closePurchase(purchaseId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 删除采购计划
     * @param purchaseOrderIds
     * @return
     */
    @DeleteMapping
    @LogRecord("删除采购计划")
    @PreAuthorize("@erp.check('purchase:edit') and @erp.check('purchaseManager')")
    @ApiOperation("删除采购计划")
    public ResponseModel deletePurchase(@RequestBody List<String> purchaseOrderIds) {
        if (Objects.equals(purchaseOrderIds, null) || purchaseOrderIds.isEmpty()) {
            return ResponseModel.error("需要删除的采购计划不能为空");
        }
        List<PurchaseOrder> purchaseOrders = purchaseOrderIds.stream().map(purchaseId -> {
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setPurchaseId(purchaseId);
            return purchaseOrder;
        }).collect(Collectors.toList());
        purchaseService.deleteAll(purchaseOrders);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 通过用户名查询采购计划
     * @param userName
     * @param pageable
     * @return
     */
    @GetMapping("/user_name")
    @LogRecord("通过用户名查询采购计划")
    @PreAuthorize("@erp.check('purchase:view')")
    @ApiOperation("通过用户名查询采购计划")
    public ResponseModel getPurchasesByUserName(@RequestParam("userName") String userName,
                                                @PageableDefault Pageable pageable) {
        return ResponseModel.ok(purchaseService.getPurchasesByUserName(userName, pageable));
    }

    /**
     * 通过用户名与状态查询采购计划
     * @param userName
     * @param purchaseState
     * @param pageable
     * @return
     */
    @GetMapping("/name_state")
    @LogRecord("通过用户名与状态查询采购计划")
    @PreAuthorize("@erp.check('purchase:view')")
    @ApiOperation("通过用户名与状态查询采购计划")
    public ResponseModel getPurchasesByNameAndState(@RequestParam("userName") String userName,
                                                    @RequestParam("state") PurchaseOrder.PurchaseOrderState purchaseState,
                                                    @PageableDefault Pageable pageable) {
        return ResponseModel.ok(purchaseService.getPurchasesByUserNameAndState(userName, purchaseState, pageable));
    }

    /**
     * 通过创建时间查询采购计划
     * @param leftTime
     * @param rightTime
     * @param pageable
     * @return
     */
    @GetMapping("/create_time")
    @LogRecord("通过创建时间查询采购计划")
    @PreAuthorize("@erp.check('purchase:view') and @erp.check('purchaseManager')")
    @ApiOperation("通过创建时间查询采购计划")
    public ResponseModel getPurchasesByCreateTime(@RequestParam("leftTime") Date leftTime,
                                                  @RequestParam("rightTime") Date rightTime,
                                                  @PageableDefault Pageable pageable) {
        if (Objects.equals(leftTime, null) && Objects.equals(rightTime, null)) {
            return ResponseModel.ok(purchaseService.getPurchasesByCreateTime(new Date(), new Date(), pageable));
        } else {
            return ResponseModel.ok(purchaseService.getPurchasesByCreateTime(leftTime, rightTime, pageable));
        }
    }

    /**
     * 通过状态查询采购计划
     * @param purchaseState
     * @param pageable
     * @return
     */
    @GetMapping("/state")
    @LogRecord("通过状态查询采购计划")
    @PreAuthorize("@erp.check('purchase:view') and @erp.check('purchaseManager')")
    @ApiOperation("通过状态查询采购计划")
    public ResponseModel getPurchasesByState(@RequestParam("purchaseState") PurchaseOrder.PurchaseOrderState purchaseState,
                                             @PageableDefault Pageable pageable) {
        return ResponseModel.ok(purchaseService.getPurchasesByState(purchaseState, pageable));
    }

    /**
     * 通过上一次修改时间查询采购计划
     * @param leftTime
     * @param rightTime
     * @param pageable
     * @return
     */
    @GetMapping("/modified_time")
    @LogRecord("通过上一次修改时间查询采购计划")
    @PreAuthorize("@erp.check('purchase:view') and @erp.check('purchaseManager')")
    @ApiOperation("通过上一次修改时间查询采购计划")
    public ResponseModel getPurchasesByModifiedTime(@RequestParam("leftTime") Date leftTime,
                                                    @RequestParam("rightTime") Date rightTime,
                                                    @PageableDefault Pageable pageable) {
        if (Objects.equals(leftTime, null) && Objects.equals(rightTime, null)) {
            return ResponseModel.ok(purchaseService.getPurchasesByModifiedTime(new Date(), new Date(), pageable));
        } else {
            return ResponseModel.ok(purchaseService.getPurchasesByModifiedTime(leftTime, rightTime, pageable));
        }
    }

    /**
     * 导出指定用户的采购计划
     * @param userName
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/export_spec")
    @LogRecord("导出指定用户的采购计划")
    @PreAuthorize("@erp.check('purchase:export')")
    @ApiOperation("导出指定用户的采购计划")
    public void exportSpec(@RequestParam("userName") String userName,
                           @PageableDefault(size = 200) Pageable pageable,
                           HttpServletResponse response) throws Exception {
        File file = purchaseService.createSpecExcelFile(userName, pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /**
     * 导出采购计划
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出采购计划")
    @PreAuthorize("@erp.check('purchase:export') and @erp.check('purchaseManager')")
    @ApiOperation("导出采购计划")
    public void export(@PageableDefault(size = 200) Pageable pageable,
                       HttpServletResponse response) throws Exception {
        File file = purchaseService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /* --------------------------------- 采购计划详细项相关 --------------------------------- */

    /**
     * 修改采购子项数量
     * @param purchaseDetailId
     * @param purchaseNumber
     * @return
     */
    @PutMapping("/details")
    @LogRecord("修改采购子项数量")
    @PreAuthorize("@erp.check('purchase:edit')")
    @ApiOperation("修改采购子项数量")
    public ResponseModel updatePurchaseDetailNumber(@RequestParam("purchaseDetailId") String purchaseDetailId,
                                                    @RequestParam("purchaseNumber") Long purchaseNumber) {
        boolean result = purchaseService.updatePurchaseDetailNumber(purchaseDetailId, purchaseNumber);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 导出采购计划子项
     * @param purchaseId
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/details/export")
    @LogRecord("导出采购计划子项")
    @PreAuthorize("@erp.check('purchase:export')")
    @ApiOperation("导出采购计划子项")
    public void exportDetails(@RequestParam("purchaseId") String purchaseId,
                              @PageableDefault(size = 200) Pageable pageable,
                              HttpServletResponse response) throws Exception {
        File file = purchaseService.createDetailExcelFile(purchaseId, pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }
}
