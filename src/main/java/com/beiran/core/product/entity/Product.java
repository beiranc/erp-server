package com.beiran.core.product.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 产品实体
 */

@Getter
@Setter
@Table(name = "erp_product")
@Entity
public class Product {

    /**
     * 产品编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String productId;

    /**
     * 产品名
     */
    @Column(length = 50)
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
    @Column(scale = 6)
    @Digits(integer = 10, fraction = 6, message = "产品成本价格式不正确")
    private Double productInPrice;

    /**
     * 产品出售价
     */
    @Column(scale = 6)
    @Digits(integer = 10, fraction = 6, message = "产品出售价格式不正确")
    private Double productOutPrice;

    /**
     * 产品规格
     */
    @Column(length = 50)
    @NotBlank(message = "产品规格不能为空")
    private String productSpecification;

    /**
     * 产品制造商
     */
    @Column(length = 50)
    @NotBlank(message = "产品制造商不能为空")
    private String productManufacturer;

    /**
     * 产品产地
     */
    @Column(length = 50)
    @NotBlank(message = "产品产地不能为空")
    private String productOrigin;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        return Objects.equals(productId, product.productId) && Objects.equals(productName, product.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName);
    }
}
