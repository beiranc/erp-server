package com.beiran.core.material.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * 物料分类
 */

@Data
@Table(name = "erp_material_category")
@Entity
public class MaterialCategory {

    /**
     * 物料分类编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String categoryId;

    /**
     * 物料分类名
     */
    @Column(length = 50)
    @NotBlank(message = "物料分类名不能为空")
    private String categoryName;
}
