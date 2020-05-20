package com.beiran.security.config;

import com.beiran.security.filter.JWTAuthenticationTokenFilter;
import com.beiran.security.handler.UserAuthAccessDeniedHandler;
import com.beiran.security.handler.UserAuthenticationEntryPointHandler;
import com.beiran.security.jwt.UserAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Spring Security 配置类
 */

@Configuration
@EnableWebSecurity
// 开启权限注解，默认是关闭的
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 自定义的暂无权限处理器（403 Forbidden）
     */
    @Autowired
    private UserAuthAccessDeniedHandler userAuthAccessDeniedHandler;

    /**
     * 自定义的用户未登录处理器
     */
    @Autowired
    private UserAuthenticationEntryPointHandler userAuthenticationEntryPointHandler;

    /**
     * 自定义的登录逻辑验证器
     */
    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    /**
     * 加密方式
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置登录验证逻辑
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(userAuthenticationProvider);
    }

    /**
     * 去除默认的 'ROLE_' 前缀
     */
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    /**
     * 配置 Spring Security 的控制逻辑
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 放行配置文件中 antMatchers 配置的请求或资源
                .antMatchers(JWTConfig.antMatchers.split(",")).permitAll()
                // 允许跨域预检请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 其他所有请求都需要认证才能访问
                .anyRequest().authenticated()
                .and()
                // 没有权限时的处理器
                .exceptionHandling()
                .accessDeniedHandler(userAuthAccessDeniedHandler)
                .and()
                // 未认证时的处理器
                .httpBasic()
                .authenticationEntryPoint(userAuthenticationEntryPointHandler)
                .and()
                // 允许跨域
                .cors()
                .and()
                // 允许 X-Frame-Options
                .headers().frameOptions().disable()
                .and()
                // 禁用跨站请求伪造防护
                .csrf().disable();

        // 基于 Token 不需要 Session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 禁用缓存
        http.headers().cacheControl();

        // 添加 JWT 请求过滤器
        http.addFilter(new JWTAuthenticationTokenFilter(authenticationManager()));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // FIXME 放行静态资源，需要根据实际的静态资源文件夹修改
        web.ignoring().antMatchers("/global/**");
    }
}
