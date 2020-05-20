package com.beiran.core.purchase.vo;

import com.beiran.core.material.entity.MaterialCategory;
import com.beiran.core.stock.dto.StockSmallDto;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class PurchaseDetailVo {

    /**
     * 是否为新物料
     */
    @NotNull(message = "请选择是否为新物料")
    private Boolean newMaterial;

    /**
     * 物料存储至哪个仓库
     */
    @NotNull(message = "请选择存储的仓库")
    private StockSmallDto stock;

    /**
     * 采购数量
     */
    @PositiveOrZero(message = "采购数量不能小于零")
    private Long number;

    /* ----------------------------- 物料相关属性 -------------------------------- */

    private String materialId;

    @NotBlank(message = "物料名不能为空")
    private String materialName;

    @NotNull(message = "物料分类不能为空")
    private MaterialCategory materialCategory;

    @Digits(integer = 10, fraction = 6, message = "物料价格格式不正确")
    private Double materialInPrice;

    @NotBlank(message = "物料规格不能为空")
    private String materialSpecification;

    @NotBlank(message = "物料制造商不能为空")
    private String materialManufacturer;

    @NotBlank(message = "物料产地不能为空")
    private String materialOrigin;
}
