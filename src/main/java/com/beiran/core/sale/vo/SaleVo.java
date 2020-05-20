package com.beiran.core.sale.vo;

import com.beiran.core.sale.entity.SaleOrder;
import com.beiran.core.system.dto.UserSmallDto;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SaleVo {

    /**
     * 销售订单主题
     */
    @NotBlank(message = "销售订单主题不能为空")
    private String saleSubject;

    /**
     * 销售员
     */
    @NotNull(message = "销售员不能为空")
    private UserSmallDto applicant;

    /**
     * 结算方式，默认银行卡方式
     */
    private SaleOrder.SalePayWay salePayWay = SaleOrder.SalePayWay.BANKCARD;

    /**
     * 销售订单状态，默认 CREATED
     */
    private SaleOrder.SaleOrderState saleState = SaleOrder.SaleOrderState.CREATED;

    /**
     * 销售总金额（即所有销售订单详细项的金额总和）
     */
    @Digits(integer = 10, fraction = 6, message = "销售总金额不能为空")
    private Double totalAmount;

    /**
     * 销售订单详细项
     */
    @NotNull(message = "销售订单详细项不能为空")
    private List<SaleDetailVo> saleDetails;

    /* --------------------------------- 客户相关属性 ------------------------------------- */

    @NotBlank(message = "客户名不能为空")
    private String customerName;

    @NotBlank(message = "客户地址不能为空")
    private String customerAddress;

    @NotBlank(message = "客户联系方式不能为空")
    private String customerPhone;

    @NotBlank(message = "客户邮箱地址不能为空")
    private String customerEmail;
}
