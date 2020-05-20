package com.beiran.core.stock.entity;

import com.beiran.core.material.entity.Material;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

/**
 * 物料-仓库中间表
 */

@Getter
@Setter
@Table(name = "erp_material_stock")
@Entity
public class MaterialStock {

    /**
     * 物料-仓库中间表编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String materStockId;

    /**
     * 物料编号
     */
    @JoinColumn(name = "materialId")
    @ManyToOne
    @NotNull(message = "物料编号不能为空")
    private Material material;

    /**
     * 仓库编号
     */
    @JoinColumn(name = "stockId")
    @ManyToOne
    @NotNull(message = "仓库编号不能为空")
    private Stock stock;

    /**
     * 存储的物料数量
     */
    @PositiveOrZero(message = "存储数量不能为空")
    private Long materNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MaterialStock materialRepo = (MaterialStock) o;
        return Objects.equals(materStockId, materialRepo.materStockId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materStockId);
    }
}
