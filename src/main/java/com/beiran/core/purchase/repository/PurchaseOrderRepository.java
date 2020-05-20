package com.beiran.core.purchase.repository;

import com.beiran.core.purchase.entity.PurchaseOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * 采购计划 Repository
 */

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String>, JpaSpecificationExecutor<PurchaseOrder> {

    /**
     * 根据采购计划编号修改采购计划状态
     * @param purchaseId 采购计划编号
     * @param purchaseState 采购计划状态
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_purchase_order SET purchase_state = ?2 WHERE purchaseId = ?1", nativeQuery = true)
    int updateState(String purchaseId, PurchaseOrder.PurchaseOrderState purchaseState);

    /**
     * 根据用户名查询采购计划
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<PurchaseOrder>
     */
    List<PurchaseOrder> findByPurchaseApplicant_UserName(String userName, Pageable pageable);

    /**
     * 根据用户名和采购计划状态查询采购计划
     * @param userName 用户名
     * @param purchaseState 采购状态
     * @param pageable 分页参数
     * @return List<PurchaseOrder>
     */
    List<PurchaseOrder> findByPurchaseApplicant_UserNameAndPurchaseState(String userName, PurchaseOrder.PurchaseOrderState purchaseState, Pageable pageable);

    /**
     * 根据采购计划创建时间查询采购计划
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<PurchaseOrder>
     */
    List<PurchaseOrder> findByPurchaseCreateTimeBetween(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 根据采购计划状态查询采购计划
     * @param purchaseState 采购计划状态
     * @param pageable 分页参数
     * @return List<PurchaseOrder>
     */
    List<PurchaseOrder> findByPurchaseState(PurchaseOrder.PurchaseOrderState purchaseState, Pageable pageable);

    /**
     * 根据上一次修改时间查询采购计划
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<PurchaseOrder>
     */
    List<PurchaseOrder> findByLastModifiedTimeBetween(Date leftTime, Date rightTime, Pageable pageable);
}
