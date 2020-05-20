package com.beiran.core.system.repository;

import com.beiran.core.system.entity.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 权限 Repository
 */
public interface PermissionRepository extends JpaRepository<Permission, String>, JpaSpecificationExecutor<Permission> {

    /**
     * 根据权限名查询一个权限
     * @param permissionName 权限名
     * @return Optional<Permission>
     */
    Optional<Permission> findByPermissionName(String permissionName);

    /**
     * 根据权限名模糊查询
     * @param permissionName 权限名
     * @return List<Permission>
     */
    List<Permission> findByPermissionNameContaining(String permissionName, Pageable pageable);

    /**
     * 根据权限描述模糊查询
     * @param permissionDesc 权限描述
     * @return List<Permission>
     */
    List<Permission> findByPermissionDescContaining(String permissionDesc, Pageable pageable);

    /**
     * 根据权限创建时间查询
     * @param leftTime 创建时间的左区间
     * @param rightTime 创建时间的右区间
     * @return List<Permission>
     */
    List<Permission> findByPermissionCreateTimeBetween(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 根据角色编号查询权限
     * @param roleId 角色编号
     * @return Set<Permission>
     */
    Set<Permission> findByPermissionRoles_RoleId(String roleId);
}
