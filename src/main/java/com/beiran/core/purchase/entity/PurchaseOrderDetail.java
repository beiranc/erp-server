package com.beiran.core.purchase.entity;

import com.beiran.core.material.entity.MaterialCategory;
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
 * 采购计划详细实体
 */

@Getter
@Setter
@Table(name = "erp_purchase_order_detail")
@Entity
public class PurchaseOrderDetail {

    /**
     * 采购计划详细编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String purchaseDetailId;

    /**
     * 是否为新物料
     */
    @NotNull(message = "是否为新物料不能为空")
    private Boolean newMaterial;

    /**
     * 物料存储至哪个仓库
     */
    @JoinColumn(name = "stockId")
    @ManyToOne
    @NotNull(message = "需要指定物料存储的仓库")
    private Stock purchaseStock;

    /**
     * 采购数量
     */
    @PositiveOrZero(message = "采购数量不能小于零")
    private Long purchaseNumber;

    /**
     * 采购计划编号
     */
    @JoinColumn(name = "purchaseId")
    @ManyToOne
    @NotNull(message = "需要指定所属采购计划")
    private PurchaseOrder belongOrder;

    /* ----------------------------- 物料相关属性 -------------------------------- */

    private String materialId;

    @NotBlank(message = "物料名不能为空")
    private String materialName;

    @JoinColumn(name = "materialCategoryId")
    @ManyToOne
    @NotNull(message = "物料分类不能为空")
    private MaterialCategory materialCategory;

    @Column(scale = 6)
    @Digits(integer = 10, fraction = 6, message = "物料价格格式不正确")
    private Double materialInPrice;

    @NotBlank(message = "物料规格不能为空")
    private String materialSpecification;

    @NotBlank(message = "物料制造商不能为空")
    private String materialManufacturer;

    @NotBlank(message = "物料产地不能为空")
    private String materialOrigin;
}
