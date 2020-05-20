package com.beiran.core.sale.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * 客户实体
 */

@Getter
@Setter
@Table(name = "erp_customer")
@Entity
public class Customer {

    /**
     * 客户编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String customerId;

    /**
     * 客户名称
     */
    @Column(length = 100)
    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    /**
     * 客户地址
     */
    @Column(length = 100)
    @NotBlank(message = "客户地址不能为空")
    private String customerAddress;

    /**
     * 客户联系方式
     */
    @Column(length = 100)
    @NotBlank(message = "客户联系方式不能为空")
    private String customerPhone;

    /**
     * 客户邮箱
     */
    @Column(length = 100)
    @Email(message = "客户邮箱地址不能为空")
    private String customerEmail;

    /**
     * 客户创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date customerCreateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId) && Objects.equals(customerName, customer.customerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerName);
    }
}
