package com.beiran.core.produce.vo;

import com.beiran.core.product.entity.ProductCategory;
import com.beiran.core.stock.dto.StockSmallDto;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class ProductionDetailVo {

    /**
     * 是否为新产品
     */
    @NotNull(message = "请选择是否为新物料")
    private Boolean newProduct;

    /**
     * 产品存储至哪个仓库
     */
    @NotNull(message = "请选择存储的仓库")
    private StockSmallDto stock;

    /**
     * 生产数量
     */
    @PositiveOrZero(message = "生产数量不能小于零")
    private Long productionNumber;

    /* ----------------------------------------- 产品相关属性 ---------------------------------- */

    private String productId;

    @NotBlank(message = "产品名不能为空")
    private String productName;

    @NotNull(message = "产品分类不能为空")
    private ProductCategory productCategory;

    @Digits(integer = 10, fraction = 6, message = "产品成本价格式不正确")
    private Double productInPrice;

    @Digits(integer = 10, fraction = 6, message = "产品出售价格式不正确")
    private Double productOutPrice;

    @NotBlank(message = "产品规格不能为空")
    private String productSpecification;

    @NotBlank(message = "产品制造商不能为空")
    private String productManufacturer;

    @NotBlank(message = "产品产地不能为空")
    private String productOrigin;
}
