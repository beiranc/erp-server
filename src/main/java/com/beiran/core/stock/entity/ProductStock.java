package com.beiran.core.stock.entity;

import com.beiran.core.product.entity.Product;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

/**
 * 产品-仓库中间表
 */

@Getter
@Setter
@Table(name = "erp_product_stock")
@Entity
public class ProductStock {

    /**
     * 产品-仓库中间表编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String prodStockId;

    /**
     * 产品编号
     */
    @JoinColumn(name = "productId")
    @ManyToOne
    @NotNull(message = "产品编号不能为空")
    private Product product;

    /**
     * 仓库编号
     */
    @JoinColumn(name = "stockId")
    @ManyToOne
    @NotNull(message = "仓库编号不能为空")
    private Stock stock;

    /**
     * 存储的产品数量
     */
    @PositiveOrZero(message = "存储数量不能为空")
    private Long prodNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductStock productRepo = (ProductStock) o;
        return Objects.equals(prodStockId, productRepo.prodStockId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prodStockId);
    }
}
