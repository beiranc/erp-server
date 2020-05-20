package com.beiran.core.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * 权限实体
 */

@Getter
@Setter
@Table(name = "erp_permission")
@Entity
public class Permission {

    /**
     * 权限编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String permissionId;

    /**
     * 权限名
     */
    @Column(length = 100, nullable = false, unique = true)
    private String permissionName;

    /**
     * 权限描述
     */
    @Column(length = 100)
    private String permissionDesc;

    /**
     * 权限创建时间，由 Hibernate 自动赋值
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date permissionCreateTime;

    /**
     * 指定由 Role 来维护关联关系
     */
    @ManyToMany(mappedBy = "rolePermissions")
    @JsonIgnore
    private Set<Role> permissionRoles;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Permission permission = (Permission) o;
        return Objects.equals(permissionId, permission.permissionId) && Objects.equals(permissionName, permission.permissionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionId, permissionName);
    }
}
