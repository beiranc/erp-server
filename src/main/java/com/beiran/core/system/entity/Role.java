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
 * 角色实体
 */

@Getter
@Setter
@Table(name = "erp_role")
@Entity
public class Role {

    /**
     * 角色编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String roleId;

    /**
     * 角色名，角色名不能使用中文，在角色描述中可以说明这个角色的作用
     */
    @Column(length = 50, unique = true, nullable = false)
    private String roleName;

    /**
     * 角色描述
     */
    @Column(length = 100)
    private String roleDesc;

    /**
     * 角色创建时间，由 Hibernate 自动赋值
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date roleCreateTime;

    /**
     * 指定由 User 来维护关联关系
     */
    @ManyToMany(mappedBy = "userRoles")
    @JsonIgnore
    private Set<User> roleUsers;

    /**
     * 角色所拥有的权限
     */
    @JoinTable(name = "erp_role_permission",
            joinColumns = @JoinColumn(referencedColumnName = "roleId", name = "roleId"),
            inverseJoinColumns = { @JoinColumn(referencedColumnName = "permissionId", name = "permissionId") })
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Permission> rolePermissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Role role = (Role) o;
        return Objects.equals(roleId, role.roleId) && Objects.equals(roleName, role.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, roleName);
    }
}
