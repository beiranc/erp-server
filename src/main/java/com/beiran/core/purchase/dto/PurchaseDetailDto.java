package com.beiran.core.purchase.dto;

import com.beiran.core.material.entity.MaterialCategory;
import com.beiran.core.stock.dto.StockSmallDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PurchaseDetailDto {

    /**
     * 采购计划详细编号
     */
    private String purchaseDetailId;

    /**
     * 是否为新物料
     */
    private Boolean newMaterial;

    /**
     * 物料存储至哪个仓库
     */
    private StockSmallDto stock;

    /**
     * 采购数量
     */
    private String number;

    /* ----------------------------- 物料相关属性 -------------------------------- */

    private String materialId;

    private String materialName;

    private MaterialCategory materialCategory;

    private Double materialInPrice;

    private String materialSpecification;

    private String materialManufacturer;

    private String materialOrigin;
}
