package com.beiran.security.jwt;

import com.beiran.core.system.entity.User;
import com.beiran.security.entity.SecurityUserDetails;
import com.beiran.security.service.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

/**
 * 自定义登录验证
 */
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SecurityUserDetailsService securityUserDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // 获取用户名
        String userName = (String) authentication.getPrincipal();

        // 获取用户名
        String userPassword = (String) authentication.getCredentials();

        // 查询用户是否存在，若不存在会跑出 UsernameNotFoundException
        SecurityUserDetails securityUserDetails = (SecurityUserDetails) securityUserDetailsService.loadUserByUsername(userName);

        // 判断密码是否正确，密码使用 BCryptPasswordEncoder 进行加密
        if (!bCryptPasswordEncoder.matches(userPassword, securityUserDetails.getPassword())) {
            throw new BadCredentialsException("密码不正确");
        }

        // 可添加其他的判断

        // 判断用户状态
        if (Objects.equals(securityUserDetails.getState(), User.UserState.DISABLED.getValue())) {
            throw new LockedException("用户已停用，请联系管理员");
        }

        // 获取用户权限
        Collection<? extends GrantedAuthority> authorities = securityUserDetails.getAuthorities();

        // 登录
        return new UsernamePasswordAuthenticationToken(securityUserDetails, userPassword, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
