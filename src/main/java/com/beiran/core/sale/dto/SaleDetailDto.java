package com.beiran.core.sale.dto;

import com.beiran.core.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SaleDetailDto {
    /**
     * 销售订单详细项编号
     */
    private String saleDetailId;

    /**
     * 销售数量
     */
    private Long saleNumber;

    /**
     * 此产品的销售总金额
     */
    private Double saleMoney;

    /**
     * 销售的产品
     */
    private Product saleProduct;
}
