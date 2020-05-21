package com.beiran.core.purchase.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.purchase.dto.PurchaseDetailDto;
import com.beiran.core.purchase.dto.PurchaseDto;
import com.beiran.core.purchase.entity.PurchaseOrder;
import com.beiran.core.purchase.vo.PurchaseVo;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * PurchaseService 接口
 */

public interface PurchaseService extends GenericService<PurchaseOrder, String> {

    /* --------------------------------- 采购计划相关 -------------------------------------- */

    /**
     * 创建采购计划，默认为 CREATED 状态
     * @param purchaseVo 创建采购计划所需数据
     * @return PurchaseDto
     */
    PurchaseDto createPurchaseOrder(PurchaseVo purchaseVo);

    /**
     * 修改采购计划状态（此方法不提供接口）
     * @param purchaseId 采购计划编号
     * @param purchaseOrderState 采购计划状态
     * @return 是否修改成功
     */
    Boolean updateState(String purchaseId, PurchaseOrder.PurchaseOrderState purchaseOrderState);

    /**
     * 修改采购计划状态为 CONFIRMED
     * @param purchaseId 采购计划编号
     * @return 是否修改成功
     */
    Boolean confirmPurchase(String purchaseId);

    /**
     * 修改采购计划状态为 VERIFYING
     * @param purchaseId 采购计划编号
     * @return 是否修改成功
     */
    Boolean verifyPurchase(String purchaseId);

    /**
     * 修改采购计划状态为 IMPORTED
     * @param purchaseId 采购计划编号
     * @return 是否修改成功
     */
    Boolean importPurchase(String purchaseId);

    /**
     * 修改采购计划状态为 DISCUSSING
     * @param purchaseId 采购计划编号
     * @return 是否修改成功
     */
    Boolean discussPurchase(String purchaseId);

    /**
     * 修改采购计划状态为 CLOSED
     * @param purchaseId 采购计划编号
     * @return 是否修改成功
     */
    Boolean closePurchase(String purchaseId);

    /**
     * 根据用户名查询采购计划
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<PurchaseDto>
     */
    List<PurchaseDto> getPurchasesByUserName(String userName, Pageable pageable);

    /**
     * 根据用户名以及采购计划状态查询采购计划
     * @param userName 用户名
     * @param purchaseState 采购计划状态
     * @param pageable 分页参数
     * @return List<PurchaseDto>
     */
    List<PurchaseDto> getPurchasesByUserNameAndState(String userName, PurchaseOrder.PurchaseOrderState purchaseState, Pageable pageable);

    /**
     * 根据采购计划创建时间查询采购计划
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<PurchaseDto>
     */
    List<PurchaseDto> getPurchasesByCreateTime(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 根据采购计划状态查询采购计划
     * @param purchaseState 采购计划状态
     * @param pageable 分页参数
     * @return List<PurchaseDto>
     */
    List<PurchaseDto> getPurchasesByState(PurchaseOrder.PurchaseOrderState purchaseState, Pageable pageable);

    /**
     * 根据上一次修改时间查询采购计划
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<PurchaseDto>
     */
    List<PurchaseDto> getPurchasesByModifiedTime(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 获取所有采购计划
     * @param pageable
     * @return
     */
    List<PurchaseDto> getAllPurchases(Pageable pageable);

    /**
     * 导出指定用户的采购计划
     * @param userName 用户名
     * @param pageable 分页参数
     * @return File
     */
    File createSpecExcelFile(String userName, Pageable pageable);

    /**
     * 导出所有的采购计划
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);

    /* --------------------------------- 采购计划详细项相关 --------------------------------- */

    /**
     * 修改采购计划详细项的采购数量
     * @param purchaseDetailId 采购计划详细项编号
     * @param purchaseNumber 采购数量
     * @return 是否修改成功
     */
    Boolean updatePurchaseDetailNumber(String purchaseDetailId, Long purchaseNumber);

    /**
     * 删除采购计划详细项
     * @param purchaseId 采购计划编号
     */
    void deletePurchaseDetail(String purchaseId);

    /**
     * 导出指定采购计划的采购计划详细项
     * @param purchaseId 采购计划编号
     * @param pageable 分页参数
     * @return File
     */
    File createDetailExcelFile(String purchaseId, Pageable pageable);
}
