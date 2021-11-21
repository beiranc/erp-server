package com.beiran.core.quotation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Objects;

/**
 * 业务员信息
 */

@Getter
@Setter
@NoArgsConstructor
@Table(name = "erp_phoenix_salesman")
@Entity
public class Salesman {

    /**
     * 业务员编号, 自动生成
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long salesmanId;

    /**
     * 业务员姓名
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "业务员姓名不能为空")
    private String name;

    /**
     * 电话
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "电话不能为空")
    private String tel;

    /**
     * 传真
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "传真不能为空")
    private String fax;

    /**
     * 邮箱
     */
    @Column(nullable = false, length = 100)
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date salesmanCreateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Salesman salesman = (Salesman) o;
        return Objects.equals(salesmanId, salesman.salesmanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salesmanId);
    }
}