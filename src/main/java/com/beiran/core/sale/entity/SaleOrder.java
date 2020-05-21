package com.beiran.core.sale.entity;

import com.beiran.core.system.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * 销售订单实体
 */

@Getter
@Setter
@Table(name = "erp_sale_order")
@Entity
public class SaleOrder {

    /**
     * 销售订单编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String saleId;

    /**
     * 销售订单主题
     */
    @Column(length = 100)
    private String saleSubject;

    /**
     * 销售员
     */
    @JoinColumn(name = "saleUserId")
    @ManyToOne
    private User saleUser;

    /**
     * 销售订单创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date saleCreateTime;

    /**
     * 结算方式
     */
    private SalePayWay salePayWay;

    /**
     * 销售订单状态
     */
    private SaleOrderState saleState;

    /**
     * 上一次修改时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedTime;

    /**
     * 上一次修改操作者
     */
    @JoinColumn(name = "lastModifiedOperator")
    @ManyToOne
    private User lastModifiedOperator;

    /**
     * 销售总金额（即所有销售订单详细项的金额总和）
     */
    private Double totalAmount;

    /* --------------------------------- 客户相关属性 ------------------------------------- */

    private String customerId;

    private String customerName;

    private String customerAddress;

    private String customerPhone;

    private String customerEmail;

    /**
     * 结算方式枚举类
     */
    public enum SalePayWay {
        ALIPAY("支付宝"),

        BANKCARD("银行卡"),

        OTHER("其他");

        private String value;

        private SalePayWay(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * 销售订单状态枚举类
     */
    public enum SaleOrderState {
        CREATED("已创建"),

        PAYING("结算中"),

        COMPLETED("已完成"),

        CANCELED("已取消");

        private String value;

        private SaleOrderState(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SaleOrder saleOrder = (SaleOrder) o;
        return Objects.equals(saleId, saleOrder.saleId) && Objects.equals(saleSubject, saleOrder.saleSubject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleId, saleSubject);
    }
}
