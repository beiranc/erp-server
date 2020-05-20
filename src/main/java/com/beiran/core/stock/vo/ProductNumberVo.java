package com.beiran.core.stock.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

/**
 * 用于查询产品数量
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductNumberVo {

    @NotBlank(message = "产品编号不能为空")
    private String productId;

    @NotBlank(message = "仓库编号不能为空")
    private String stockId;

    @PositiveOrZero(message = "存储数量不能小于零")
    private Long productNumber;

    public ProductNumberVo(Long productNumber, String stockId) {
        this.productNumber = productNumber;
        this.stockId = stockId;
    }

    public ProductNumberVo(String productId, Long productNumber) {
        this.productId = productId;
        this.productNumber = productNumber;
    }

    public ProductNumberVo(String productId, String stockId, Long productNumber) {
        this.productId = productId;
        this.stockId = stockId;
        this.productNumber = productNumber;
    }
}
