package com.beiran.core.sale.repository;

import com.beiran.core.sale.entity.SaleOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * 销售订单 Repository
 */

public interface SaleOrderRepository extends JpaRepository<SaleOrder, String>, JpaSpecificationExecutor<SaleOrder> {

    /**
     * 根据销售订单编号修改销售订单状态
     * @param saleId 销售订单编号
     * @param saleState 销售订单状态
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_sale_order SET sale_state = ?2 WHERE sale_id = ?1", nativeQuery = true)
    int updateState(String saleId, SaleOrder.SaleOrderState saleState);

    /**
     * 根据销售订单编号修改销售订单结算方式
     * @param saleId 销售订单编号
     * @param salePayWay 结算方式
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_sale_order SET sale_pay_way = ?2 WHERE sale_id = ?1", nativeQuery = true)
    int updatePayWay(String saleId, SaleOrder.SalePayWay salePayWay);

    /**
     * 根据销售订单创建者的用户名查询
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<SaleOrder>
     */
    List<SaleOrder> findBySaleUser_UserName(String userName, Pageable pageable);

    /**
     * 根据销售订单创建者的用户名与销售订单状态查询
     * @param userName 用户名
     * @param saleState 销售订单状态
     * @param pageable 分页参数
     * @return List<SaleOrder>
     */
    List<SaleOrder> findBySaleUser_UserNameAndSaleState(String userName, SaleOrder.SaleOrderState saleState, Pageable pageable);

    /**
     * 根据销售订单主题模糊查询
     * @param saleSubject 销售订单主题
     * @param pageable 分页参数
     * @return List<SaleOrder>
     */
    List<SaleOrder> findBySaleSubjectContaining(String saleSubject, Pageable pageable);

    /**
     * 根据销售订单状态查询
     * @param saleState 销售订单状态
     * @param pageable 分页参数
     * @return List<SaleOrder>
     */
    List<SaleOrder> findBySaleState(SaleOrder.SaleOrderState saleState, Pageable pageable);

    /**
     * 根据销售订单创建时间查询
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<SaleOrder>
     */
    List<SaleOrder> findBySaleCreateTimeBetween(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 根据销售订单上一次修改时间查询
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<SaleOrder>
     */
    List<SaleOrder> findByLastModifiedTimeBetween(Date leftTime, Date rightTime, Pageable pageable);
}
