package com.beiran.core.purchase.repository;

import com.beiran.core.purchase.entity.PurchaseOrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 采购计划详细 Repository
 */

public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, String>, JpaSpecificationExecutor<PurchaseOrderDetail> {

    /**
     * 根据采购计划详细编号修改采购数量
     * @param purchaseDetailId 采购计划详细项编号
     * @param purchaseNumber 采购数量
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_purchase_order_detail SET purchase_number = ?2 WHERE purchase_detail_id = ?1", nativeQuery = true)
    int updateNumber(String purchaseDetailId, Long purchaseNumber);

    /**
     * 根据采购计划编号删除相关的采购计划详细项
     * @param purchaseId 采购计划编号
     */
    @Modifying
    @Query(value = "DELETE FROM erp_purchase_order_detail WHERE purchase_id = ?1", nativeQuery = true)
    void deleteByPurchaseId(String purchaseId);

    /**
     * 根据采购计划编号查询采购计划详细
     * @param purchaseId 采购计划编号
     * @param pageable 分页参数
     * @return List<PurchaseOrderDetail>
     */
    List<PurchaseOrderDetail> findByBelongOrder_PurchaseId(String purchaseId, Pageable pageable);
}
