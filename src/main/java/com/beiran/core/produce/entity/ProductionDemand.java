package com.beiran.core.produce.entity;

import com.beiran.core.system.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * 生产需求计划
 */

@Getter
@Setter
@Table(name = "erp_production_demand")
@Entity
public class ProductionDemand {

    /**
     * 生产需求计划编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String productionId;

    /**
     * 生产需求计划主题
     */
    @Column(length = 100)
    private String productionSubject;

    /**
     * 生产需求计划创建时间
     */
    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    private Date productionCreateTime;

    /**
     * 生产需求计划创建者
     */
    @JoinColumn(name = "productionApplicantId")
    @ManyToOne
    private User productionApplicant;

    /**
     * 生产需求计划状态
     */
    private ProductionDemandState productionState;

    /**
     * 上一次修改时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedTime;

    /**
     * 上一次修改操作者
     */
    @JoinColumn(name = "lastModifiedOperatorId")
    @ManyToOne
    private User lastModifiedOperator;

    /**
     * 生产需求计划状态枚举类
     */
    public enum ProductionDemandState {
        CREATED("已创建"),

        CONFIRMED("已确认"),

        VERIFYING("校验中"),

        IMPORTED("已入库"),

        REPRODUCED("重生产"),

        CLOSED("已关闭");

        private String value;

        private ProductionDemandState(String value) {
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
        ProductionDemand productionDemand = (ProductionDemand) o;
        return Objects.equals(productionId, productionDemand.productionId) && Objects.equals(productionSubject, productionDemand.productionSubject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productionId, productionSubject);
    }
}
