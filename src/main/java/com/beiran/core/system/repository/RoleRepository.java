package com.beiran.core.system.repository;

import com.beiran.core.system.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 角色 Repository
 */
public interface RoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    /**
     * 根据角色名查询角色
     * @param roleName 角色名
     * @return Optional<Role>
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * 根据角色名模糊匹配
     * @param roleName
     * @return List<Role>
     */
    List<Role> findByRoleNameContaining(String roleName);

    /**
     * 根据角色描述模糊匹配
     * @param roleDesc 角色描述
     * @return List<Role>
     */
    List<Role> findByRoleDescContaining(String roleDesc);

    /**
     * 根据用户编号查询角色
     * @param userId 用户编号
     * @return Set<Role>
     */
    Set<Role> findByRoleUsers_UserId(String userId);

    /**
     * 根据用户创建时间查询用户
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @return List<Role>
     */
    List<Role> findByRoleCreateTimeBetween(Date leftTime, Date rightTime);
}
