package com.beiran.core.sale.dto;

import com.beiran.core.sale.entity.SaleOrder;
import com.beiran.core.system.dto.UserSmallDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SaleDto {

    /**
     * 销售订单编号
     */
    private String saleId;

    /**
     * 销售订单主题
     */
    private String saleSubject;

    /**
     * 销售员
     */
    private UserSmallDto applicant;

    /**
     * 销售订单创建时间
     */
    private Date saleCreateTime;

    /**
     * 结算方式
     */
    private SaleOrder.SalePayWay salePayWay;

    /**
     * 销售订单状态
     */
    private SaleOrder.SaleOrderState saleState;

    /**
     * 上一次修改时间
     */
    private Date lastModifiedTime;

    /**
     * 上一次修改操作者
     */
    private UserSmallDto operator;

    /**
     * 销售总金额（即所有销售订单详细项的金额总和）
     */
    private Double totalAmount;

    /**
     * 销售订单详细项
     */
    private List<SaleDetailDto> saleDetails;

    /* --------------------------------- 客户相关属性 ------------------------------------- */

    private String customerId;

    private String customerName;

    private String customerAddress;

    private String customerPhone;

    private String customerEmail;
}
