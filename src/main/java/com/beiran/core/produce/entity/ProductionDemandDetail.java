package com.beiran.core.produce.entity;

import com.beiran.core.product.entity.ProductCategory;
import com.beiran.core.stock.entity.Stock;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * 生产需求计划详细项
 */

@Getter
@Setter
@Table(name = "erp_production_demand_detail")
@Entity
public class ProductionDemandDetail {

    /**
     * 生产需求计划详细编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String productionDetailId;

    /**
     * 是否为新产品
     */
    @NotNull(message = "是否为新产品不能为空")
    private Boolean newProduct;

    /**
     * 产品存储至哪个仓库
     */
    @JoinColumn(name = "stockId")
    @ManyToOne
    @NotNull(message = "需要指定产品存储的仓库")
    private Stock productionStock;

    /**
     * 生产数量
     */
    @PositiveOrZero(message = "生产数量不能小于零")
    private Long productionNumber;

    /**
     * 生产需求计划编号
     */
    @JoinColumn(name = "productionId")
    @ManyToOne
    @NotNull(message = "需要指定所属生产需求计划编号")
    private ProductionDemand belongDemand;

    /* ----------------------------- 产品相关属性 -------------------------------- */

    /**
     * 产品编号
     */
    private String productId;

    /**
     * 产品名
     */
    @NotBlank(message = "产品名不能为空")
    private String productName;

    /**
     * 产品分类
     */
    @JoinColumn(name = "productCategoryId")
    @ManyToOne
    @NotNull(message = "产品分类不能为空")
    private ProductCategory productCategory;

    /**
     *  产品进货价/成本价
     */
    @Digits(integer = 10, fraction = 6, message = "产品成本价格式不正确")
    private Double productInPrice;

    /**
     * 产品出售价
     */
    @Digits(integer = 10, fraction = 6, message = "产品出售价格式不正确")
    private Double productOutPrice;

    /**
     * 产品规格
     */
    @NotBlank(message = "产品规格不能为空")
    private String productSpecification;

    /**
     * 产品制造商
     */
    @NotBlank(message = "产品制造商不能为空")
    private String productManufacturer;

    /**
     * 产品产地
     */
    @NotBlank(message = "产品产地不能为空")
    private String productOrigin;
}
