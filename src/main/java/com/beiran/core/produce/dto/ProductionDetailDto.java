package com.beiran.core.produce.dto;

import com.beiran.core.product.entity.ProductCategory;
import com.beiran.core.stock.dto.StockSmallDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductionDetailDto {

    /**
     * 生产需求计划详细编号
     */
    private String productionDetailId;

    /**
     * 是否为新产品
     */
    private Boolean newProduct;

    /**
     * 存储至哪个仓库
     */
    private StockSmallDto stock;

    /**
     * 生产数量
     */
    private String number;

    /* ----------------------------------------- 产品相关属性 ---------------------------------- */

    private String productId;

    private String productName;

    private ProductCategory productCategory;

    private Double productInPrice;

    private Double productOutPrice;

    private String productSpecification;

    private String productManufacturer;

    private String productOrigin;
}
