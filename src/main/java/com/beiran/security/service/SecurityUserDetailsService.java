package com.beiran.security.service;

import com.beiran.core.system.entity.Permission;
import com.beiran.core.system.entity.Role;
import com.beiran.core.system.entity.User;
import com.beiran.core.system.repository.JobRepository;
import com.beiran.core.system.repository.PermissionRepository;
import com.beiran.core.system.repository.UserRepository;
import com.beiran.security.entity.SecurityUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Spring Security 用户的业务实现
 */

@Slf4j
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private JobRepository jobRepository;

    /**
     * 查询用户信息
     * @param username 用户名
     * @return UserDetails Spring Security 用户信息
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中查询用户
        User user = userRepository.findByUserName(username).orElse(null);
        if (Objects.equals(user, null)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        // 如果从数据库中查找出来的 user 不为空，则复制数据到 Spring Security 实体中
        SecurityUserDetails securityUserDetails = new SecurityUserDetails();
        BeanUtils.copyProperties(user, securityUserDetails);

        // 用户角色
        Set<String> roles = user.getUserRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());

        // 用户所拥有的所有权限
        Set<Permission> permissions = new HashSet<>();
        Set<String> roleIds = user.getUserRoles().stream().map(Role::getRoleId).collect(Collectors.toSet());
        roleIds.forEach(roleId -> {
            Set<Permission> rolePermissions = permissionRepository.findByPermissionRoles_RoleId(roleId);
            permissions.addAll(rolePermissions);
        });

        // 将用户角色与用户权限都放入 authorities 中
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        authorities.addAll(permissions.stream().map(Permission::getPermissionName).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        securityUserDetails.setAuthorities(authorities);
        return securityUserDetails;
    }
}
