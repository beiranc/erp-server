package com.beiran.core.sale.repository;

import com.beiran.core.sale.entity.SaleOrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 销售订单详细项 Repository
 */

public interface SaleOrderDetailRepository extends JpaRepository<SaleOrderDetail, String>, JpaSpecificationExecutor<SaleOrderDetail> {

    /**
     * 修改销售订单子项的销售数量与销售金额
     * @param saleDetailId 销售订单子项编号
     * @param saleNumber 销售数量
     * @param saleMoney 销售金额
     * @return 是否修改成功
     */
    @Query(value = "UPDATE erp_sale_order_detail SET sale_number = ?2, sale_money = ?3 WHERE sale_detail_id = ?1", nativeQuery = true)
    int updateNumberAndMoney(String saleDetailId, Long saleNumber, Double saleMoney);

    /**
     * 根据销售订单编号删除其下的所有销售订单子项
     * @param saleId 销售订单编号
     */
    @Query(value = "DELETE FROM erp_sale_order_detail WHERE sale_id = ?1", nativeQuery = true)
    void deleteBySaleId(String saleId);

    /**
     * 根据销售订单编号查询
     * @param saleId 销售订单编号
     * @return List<SaleOrderDetail>
     */
    List<SaleOrderDetail> findByBelongOrder_SaleId(String saleId, Pageable pageable);
}
