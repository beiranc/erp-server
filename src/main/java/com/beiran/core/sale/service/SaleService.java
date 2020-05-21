package com.beiran.core.sale.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.sale.dto.SaleDto;
import com.beiran.core.sale.entity.SaleOrder;
import com.beiran.core.sale.vo.SaleVo;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * SaleService 接口
 */

public interface SaleService extends GenericService<SaleOrder, String> {

    /* ------------------------------ 销售订单相关 ----------------------------------- */

    /**
     * 创建销售订单
     * @param saleVo 创建销售订单所需数据
     * @return SaleDto
     */
    SaleDto createSale(SaleVo saleVo);

    /**
     * 根据销售订单编号修改销售订单状态
     * @param saleId 销售订单编号
     * @param saleState 销售订单状态
     * @return 是否修改成功
     */
    Boolean updateState(String saleId, SaleOrder.SaleOrderState saleState);

    /**
     * 修改销售订单状态为 PAYING
     * @param saleId 销售订单编号
     * @return 是否修改成功
     */
    Boolean paySale(String saleId);

    /**
     * 修改销售订单状态为 COMPLETED
     * @param saleId 销售订单编号
     * @return 是否修改成功
     */
    Boolean completeSale(String saleId);

    /**
     * 修改销售订单状态为 CANCELED
     * @param saleId 销售订单编号
     * @return 是否修改成功
     */
    Boolean cancelSale(String saleId);

    /**
     * 修改销售订单结算方式
     * @param saleId 销售订单编号
     * @param salePayWay 结算方式
     * @return 是否修改成功
     */
    Boolean updatePayWay(String saleId, SaleOrder.SalePayWay salePayWay);

    /**
     * 根据用户名查询
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<SaleDto>
     */
    List<SaleDto> getSalesByUserName(String userName, Pageable pageable);

    /**
     * 根据用户名与状态查询
     * @param userName 用户名
     * @param saleState 销售订单状态
     * @param pageable 分页参数
     * @return List<SaleDto>
     */
    List<SaleDto> getSalesByUserNameAndState(String userName, SaleOrder.SaleOrderState saleState, Pageable pageable);

    /**
     * 根据销售订单主题查询
     * @param saleSubject 销售订单主题
     * @param pageable 分页参数
     * @return List<SaleDto>
     */
    List<SaleDto> getSalesBySubject(String saleSubject, Pageable pageable);

    /**
     * 根据销售订单状态查询
     * @param saleState 销售订单状态
     * @param pageable 分页参数
     * @return List<SaleDto>
     */
    List<SaleDto> getSalesByState(SaleOrder.SaleOrderState saleState, Pageable pageable);

    /**
     * 根据销售订单创建时间查询
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<SaleDto>
     */
    List<SaleDto> getSalesByCreateTime(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 根据销售订单上一次修改时间查询
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<SaleDto>
     */
    List<SaleDto> getSalesByModifiedTime(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 获取所有的销售订单
     * @param pageable
     * @return
     */
    List<SaleDto> getAllSales(Pageable pageable);

    /**
     * 导出指定用户的销售订单
     * @param userName 用户名
     * @param pageable 分页参数
     * @return File
     */
    File createSpecExcelFile(String userName, Pageable pageable);

    /**
     * 导出销售订单
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);

    /* ------------------------------ 销售订单详细项相关 ------------------------------- */

    /**
     * 修改销售订单子项的销售数量与金额
     * @param saleDetailId 销售订单详细项编号
     * @param saleNumber 销售数量
     * @param saleMoney 销售金额
     * @return 是否修改成功
     */
    Boolean updateNumberAndMoney(String saleDetailId, Long saleNumber, Double saleMoney);

    /**
     * 根据销售订单编号删除销售订单子项
     * @param saleId 销售订单编号
     */
    void deleteSaleDetail(String saleId);

    /**
     * 导出指定销售订单的销售订单子项
     * @param saleId 销售订单编号
     * @param pageable 分页参数
     * @return File
     */
    File createDetailExcelFile(String saleId, Pageable pageable);
}
