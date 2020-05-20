package com.beiran.core.material.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 物料实体
 */

@Getter
@Setter
@Table(name = "erp_material")
@Entity
public class Material {

    /**
     * 物料编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String materialId;

    /**
     * 物料名
     */
    @Column(length = 50)
    @NotBlank(message = "物料名不能为空")
    private String materialName;

    /**
     * 物料分类
     */
    @JoinColumn(name = "materialCategoryId")
    @ManyToOne
    @NotNull(message = "物料分类不能为空")
    private MaterialCategory materialCategory;

    /**
     * 物料进货价
     */
    @Column(scale = 6)
    @Digits(integer = 10, fraction = 6, message = "物料价格格式不正确")
    private Double materialInPrice;

    /**
     * 物料规格
     */
    @Column(length = 50)
    @NotBlank(message = "物料规格不能为空")
    private String materialSpecification;

    /**
     * 物料制造商
     */
    @Column(length = 50)
    @NotBlank(message = "物料制造商不能为空")
    private String materialManufacturer;

    /**
     * 物料产地
     */
    @Column(length = 50)
    @NotBlank(message = "物料产地不能为空")
    private String materialOrigin;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Material material = (Material) o;
        return Objects.equals(materialId, material.materialId) && Objects.equals(materialName, material.materialName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialId, materialName);
    }
}
