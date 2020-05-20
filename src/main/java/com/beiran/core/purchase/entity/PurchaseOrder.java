package com.beiran.core.purchase.entity;

import com.beiran.core.system.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * 采购计划实体，不考虑采购金额
 */

@Getter
@Setter
@Table(name = "erp_purchase_order")
@Entity
public class PurchaseOrder {

    /**
     * 采购计划编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String purchaseId;

    /**
     * 采购计划主题
     */
    @Column(length = 100)
    private String purchaseSubject;

    /**
     * 采购计划创建时间，由 Hibernate 自动创建
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date purchaseCreateTime;

    /**
     * 采购计划创建人
     */
    @JoinColumn(name = "purchaseApplicantId")
    @ManyToOne
    private User purchaseApplicant;

    /**
     * 采购计划状态
     */
    private PurchaseOrderState purchaseState;

    /**
     * 上一次修改时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedTime;

    /**
     * 上一次修改的操作者
     */
    @JoinColumn(name = "lastModifiedOperatorId")
    @ManyToOne
    private User lastModifiedOperator;

    /**
     * 采购计划状态枚举类
     */
    public enum PurchaseOrderState {
        CREATED("已创建"),

        CONFIRMED("已确认"),

        VERIFYING("校验中"),

        IMPORTED("已入库"),

        DISCUSSING("交涉中"),

        CLOSED("已关闭");

        private String value;

        private PurchaseOrderState(String value) {
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
        PurchaseOrder purchaseOrder = (PurchaseOrder) o;
        return Objects.equals(purchaseId, purchaseOrder.purchaseId) && Objects.equals(purchaseSubject, purchaseOrder.purchaseSubject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseId, purchaseSubject);
    }
}
