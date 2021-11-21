package com.beiran.core.quotation.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * 报价单实体
 */

@Getter
@Setter
@NoArgsConstructor
@Table(name = "erp_phoenix_quotation")
@Entity
public class Quotation {

    /**
     * 报价单编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String quotationId;

    /*-------------------------------- 报价单抬头 ----------------------------------*/

    /**
     * 公司中文名
     */
    @Column(length = 100)
    private String chineseName;

    /**
     * 公司英文名
     */
    @Column(length = 100)
    private String englishName;

    /**
     * 地址
     */
    @Column(length = 100)
    private String address;

    /**
     * 官网
     */
    @Column(length = 100)
    private String officialWebsite;

    /**
     * 业务员
     */
    @JoinColumn(name = "salesmanId")
    @ManyToOne(fetch = FetchType.EAGER)
    private Salesman salesman;

    /**
     * 生成日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date generateDate;

    /**
     * 实时汇率
     */
    @Column(length = 10)
    private String realTimeRate;

    /**
     * 有效期, 一般为生成这张报价单后一个月
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date validPeriod;

    /*-------------------------------- 基本信息 ----------------------------------*/

    /**
     * 报价单中每一项货物
     */
    @JoinTable(name = "erp_phoenix_quotation_basic_info",
            joinColumns = @JoinColumn(referencedColumnName = "quotationId", name = "quotationId"),
            inverseJoinColumns = { @JoinColumn(referencedColumnName = "basicInfoId", name = "basicInfoId") })
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<BasicInfo> basicInfos;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quotation quotation = (Quotation) o;
        return Objects.equals(quotationId, quotation.quotationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quotationId);
    }
}
