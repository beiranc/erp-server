package com.beiran.core.sale.entity;

import com.beiran.core.product.entity.Product;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 销售订单详细项实体
 */

@Getter
@Setter
@Table(name = "erp_sale_order_detail")
@Entity
public class SaleOrderDetail {

    /**
     * 销售订单详细项编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String saleDetailId;

    /**
     * 销售数量
     */
    private Long saleNumber;

    /**
     * 此产品的销售总金额
     */
    private Double saleMoney;

    /**
     * 所属的销售订单
     */
    @JoinColumn(name = "saleId")
    @ManyToOne
    private SaleOrder belongOrder;

    /**
     * 销售的产品
     */
    @JoinColumn(name = "productId")
    @ManyToOne(fetch = FetchType.EAGER)
    private Product saleProduct;
}
