package com.beiran.core.system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * 部门实体
 */

@Getter
@Setter
@Table(name = "erp_dept")
@Entity
public class Dept {

    /**
     * 部门编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String deptId;

    /**
     * 部门名称
     */
    @Column(length = 100)
    private String deptName;

    /**
     * 上一级部门，为空说明没有上一级，自身即为最上级
     */
    @JoinColumn(name = "parentId")
    @OneToOne(fetch = FetchType.EAGER)
    private Dept deptParent;

    /**
     * 部门状态
     */
    private DeptState deptState;

    /**
     * 部门创建时间，由 Hibernate 自动赋值
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date deptCreateTime;

    /**
     * 部门状态枚举类
     */
    public enum DeptState {

        ACTIVE("启用"),

        DISABLED("停用");

        private String value;

        private DeptState(String value) {
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
        Dept dept = (Dept) o;
        return Objects.equals(deptId, dept.deptId) && Objects.equals(deptName, dept.deptName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deptId, deptName);
    }
}
