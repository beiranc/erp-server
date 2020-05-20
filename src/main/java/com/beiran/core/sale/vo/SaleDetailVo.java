package com.beiran.core.sale.vo;

import com.beiran.core.product.entity.Product;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class SaleDetailVo {

    /**
     * 销售数量
     */
    @PositiveOrZero(message = "销售数量不能小于零")
    private Long saleNumber;

    /**
     * 此产品的销售总金额
     */
    @Digits(integer = 10, fraction = 6, message = "金额不能小于零")
    private Double saleMoney;

    /**
     * 销售的产品
     */
    @NotNull(message = "产品信息不能为空")
    private Product saleProduct;
}
