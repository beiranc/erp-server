package com.beiran.core.product.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * 产品分类
 */

@Data
@Table(name = "erp_product_category")
@Entity
public class ProductCategory {

    /**
     * 产品分类编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String categoryId;

    /**
     * 产品分类名
     */
    @Column(length = 50)
    @NotBlank(message = "产品分类名不能为空")
    private String categoryName;
}
