package com.beiran.core.sale.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.sale.entity.SaleOrder;
import com.beiran.core.sale.service.SaleService;
import com.beiran.core.sale.vo.SaleVo;
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
 * 销售订单接口<br>
 * 创建销售订单权限: sale:add<br>
 * 修改销售订单权限: sale:edit<br>
 * 删除销售订单权限: sale:del<br>
 * 查看销售订单权限: sale:view<br>
 * 导出销售订单权限: sale:export<br>
 */

@RestController
@RequestMapping("/api/v1/sales")
@Api(tags = "销售管理：销售订单")
public class SaleController {

    @Autowired
    private SaleService saleService;

    /* ---------------------------------------- 销售订单相关 --------------------------------------- */

    /**
     * 创建销售订单
     * @param saleVo
     * @return
     */
    @PostMapping
    @LogRecord("创建销售订单")
    @PreAuthorize("@erp.check('sale:add')")
    @ApiOperation("创建销售订单")
    public ResponseModel saveSale(@RequestBody @Valid SaleVo saleVo) {
        return ResponseModel.ok(saleService.createSale(saleVo));
    }

    /**
     * 修改销售订单
     * @param saleOrder
     * @return
     */
    @PutMapping
    @LogRecord("修改销售订单")
    @PreAuthorize("@erp.check('sale:edit')")
    @ApiOperation("修改销售订单")
    public ResponseModel updateSale(@RequestBody SaleOrder saleOrder) {
        return ResponseModel.ok(saleService.update(saleOrder));
    }

    /**
     * 结算销售订单
     * @param saleId
     * @return
     */
    @PutMapping("/pay")
    @LogRecord("结算销售订单")
    @PreAuthorize("@erp.check('sale:edit')")
    @ApiOperation("结算销售订单")
    public ResponseModel paySale(@RequestParam("saleId") String saleId) {
        boolean result = saleService.paySale(saleId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 完成销售订单
     * @param saleId
     * @return
     */
    @PutMapping("/complete")
    @LogRecord("完成销售订单")
    @PreAuthorize("@erp.check('sale:edit')")
    @ApiOperation("完成销售订单")
    public ResponseModel completeSale(@RequestParam("saleId") String saleId) {
        boolean result = saleService.completeSale(saleId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 取消销售订单
     * @param saleId
     * @return
     */
    @PutMapping("/cancel")
    @LogRecord("取消销售订单")
    @PreAuthorize("@erp.check('sale:edit')")
    @ApiOperation("取消销售订单")
    public ResponseModel cancelSale(@RequestParam("saleId") String saleId) {
        boolean result = saleService.cancelSale(saleId);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 修改结算方式
     * @param saleId
     * @param salePayWay
     * @return
     */
    @PutMapping("/pay_way")
    @LogRecord("修改结算方式")
    @PreAuthorize("@erp.check('sale:edit')")
    @ApiOperation("修改结算方式")
    public ResponseModel updatePayWay(@RequestParam("saleId") String saleId,
                                      @RequestParam("salePayWay") SaleOrder.SalePayWay salePayWay) {
        boolean result = saleService.updatePayWay(saleId, salePayWay);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 删除销售订单
     * @param saleIds
     * @return
     */
    @DeleteMapping
    @LogRecord("删除销售订单")
    @PreAuthorize("@erp.check('sale:del') and @erp.check('saleManager')")
    @ApiOperation("删除销售订单")
    public ResponseModel deleteSale(@RequestBody List<String> saleIds) {
        if (Objects.equals(saleIds, null) || saleIds.isEmpty()) {
            return ResponseModel.error("需要删除的销售订单不能为空");
        }
        List<SaleOrder> saleOrders = saleIds.stream().map(saleId -> {
            SaleOrder saleOrder = new SaleOrder();
            saleOrder.setSaleId(saleId);
            return saleOrder;
        }).collect(Collectors.toList());
        saleService.deleteAll(saleOrders);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 查询所有销售订单
     * @param pageable
     * @return
     */
    @GetMapping
    @LogRecord("查询所有销售订单")
    @PreAuthorize("@erp.check('sale:view') and @erp.check('saleManager')")
    @ApiOperation("查询所有销售订单")
    public ResponseModel getAllSales(@PageableDefault Pageable pageable) {
        return ResponseModel.ok(saleService.getAllSales(pageable));
    }

    /**
     * 通过用户名查询销售订单
     * @param userName
     * @param pageable
     * @return
     */
    @GetMapping("/user_name")
    @LogRecord("通过用户名查询销售订单")
    @PreAuthorize("@erp.check('sale:view')")
    @ApiOperation("通过用户名查询销售订单")
    public ResponseModel getSalesByUserName(@RequestParam("userName") String userName,
                                            @PageableDefault Pageable pageable) {
        return ResponseModel.ok(saleService.getSalesByUserName(userName, pageable));
    }

    /**
     * 通过用户名与销售订单状态查询
     * @param userName
     * @param saleState
     * @param pageable
     * @return
     */
    @GetMapping("/name_state")
    @LogRecord("通过用户名与销售订单状态查询")
    @PreAuthorize("@erp.check('sale:view')")
    @ApiOperation("通过用户名与销售订单状态查询")
    public ResponseModel getSalesByUserNameAndState(@RequestParam("userName") String userName,
                                                    @RequestParam("state") SaleOrder.SaleOrderState saleState,
                                                    @PageableDefault Pageable pageable) {
        return ResponseModel.ok(saleService.getSalesByUserNameAndState(userName, saleState, pageable));
    }

    /**
     * 通过销售订单主题查询
     * @param saleSubject
     * @param pageable
     * @return
     */
    @GetMapping("/subject")
    @LogRecord("通过销售订单主题查询")
    @PreAuthorize("@erp.check('sale:view') and @erp.check('saleManager')")
    @ApiOperation("通过销售订单主题查询")
    public ResponseModel getSalesBySubject(@RequestParam("saleSubject") String saleSubject,
                                           @PageableDefault Pageable pageable) {
        return ResponseModel.ok(saleService.getSalesBySubject(saleSubject, pageable));
    }

    /**
     * 通过销售订单状态查询
     * @param saleState
     * @param pageable
     * @return
     */
    @GetMapping("/state")
    @LogRecord("通过销售订单状态查询")
    @PreAuthorize("@erp.check('sale:view') and @erp.check('saleManager')")
    @ApiOperation("通过销售订单状态查询")
    public ResponseModel getSalesByState(@RequestParam("saleState") SaleOrder.SaleOrderState saleState,
                                         @PageableDefault Pageable pageable) {
        return ResponseModel.ok(saleService.getSalesByState(saleState, pageable));
    }

    /**
     * 通过销售订单创建时间查询
     * @param leftTime
     * @param rightTime
     * @param pageable
     * @return
     */
    @GetMapping("/create_time")
    @LogRecord("通过销售订单创建时间查询")
    @PreAuthorize("@erp.check('sale:view') and @erp.check('saleManager')")
    @ApiOperation("通过销售订单创建时间查询")
    public ResponseModel getSalesByCreateTime(@RequestParam("leftTime") Date leftTime,
                                              @RequestParam("rightTime") Date rightTime,
                                              @PageableDefault Pageable pageable) {
        if (Objects.equals(leftTime, null) && Objects.equals(rightTime, null)) {
            return ResponseModel.ok(saleService.getSalesByCreateTime(new Date(), new Date(), pageable));
        } else {
            return ResponseModel.ok(saleService.getSalesByCreateTime(leftTime, rightTime, pageable));
        }
    }

    /**
     * 通过销售订单上一次修改时间查询
     * @param leftTime
     * @param rightTime
     * @param pageable
     * @return
     */
    @GetMapping("/modified_time")
    @LogRecord("通过销售订单上一次修改时间查询")
    @PreAuthorize("@erp.check('sale:view') and @erp.check('saleManager')")
    @ApiOperation("通过销售订单上一次修改时间查询")
    public ResponseModel getSalesByModifiedTime(@RequestParam("leftTime") Date leftTime,
                                              @RequestParam("rightTime") Date rightTime,
                                              @PageableDefault Pageable pageable) {
        if (Objects.equals(leftTime, null) && Objects.equals(rightTime, null)) {
            return ResponseModel.ok(saleService.getSalesByModifiedTime(new Date(), new Date(), pageable));
        } else {
            return ResponseModel.ok(saleService.getSalesByModifiedTime(leftTime, rightTime, pageable));
        }
    }

    /**
     * 导出指定用户的销售订单
     * @param userName
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/export_spec")
    @LogRecord("导出指定用户的销售订单")
    @PreAuthorize("@erp.check('sale:export')")
    @ApiOperation("导出指定用户的销售订单")
    public void exportSpec(@RequestParam("userName") String userName,
                           @PageableDefault(size = 200) Pageable pageable,
                           HttpServletResponse response) throws Exception {
        File file = saleService.createSpecExcelFile(userName, pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /**
     * 导出销售订单
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出销售订单")
    @PreAuthorize("@erp.check('sale:export') and @erp.check('saleManager')")
    @ApiOperation("导出销售订单")
    public void export(@PageableDefault(size = 200) Pageable pageable,
                       HttpServletResponse response) throws Exception {
        File file = saleService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /* ---------------------------------------- 销售订单详细项相关 ---------------------------------- */

    /**
     * 修改销售数量
     * @param saleDetailId
     * @param saleNumber
     * @param saleMoney
     * @return
     */
    @PutMapping("/details")
    @LogRecord("修改销售数量")
    @PreAuthorize("@erp.check('sale:edit')")
    @ApiOperation("修改销售数量")
    public ResponseModel updateDetailNumberAndMoney(@RequestParam("saleDetailId") String saleDetailId,
                                                    @RequestParam("saleNumber") Long saleNumber,
                                                    @RequestParam("saleMoney") Double saleMoney) {
        boolean result = saleService.updateNumberAndMoney(saleDetailId, saleNumber, saleMoney);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 导出销售订单子项
     * @param saleId
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/details/export")
    @LogRecord("导出销售订单子项")
    @PreAuthorize("@erp.check('sale:export')")
    @ApiOperation("导出销售订单子项")
    public void exportDetail(@RequestParam("saleId") String saleId,
                             @PageableDefault(size = 200) Pageable pageable,
                             HttpServletResponse response) throws Exception {
        File file = saleService.createDetailExcelFile(saleId, pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }
}
