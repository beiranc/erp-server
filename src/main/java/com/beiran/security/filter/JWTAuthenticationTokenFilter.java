package com.beiran.security.filter;

import com.alibaba.fastjson.JSON;
import com.beiran.security.config.JWTConfig;
import com.beiran.security.entity.SecurityUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JWT 接口请求校验拦截器<br>
 * 请求接口会进入这里验证 token 是否合法和过期
 */
@Slf4j
public class JWTAuthenticationTokenFilter extends BasicAuthenticationFilter {

    public JWTAuthenticationTokenFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 获取请求头中 JWT 的 Token
        String tokenHeader = request.getHeader(JWTConfig.tokenHeader);

        if (!Objects.equals(tokenHeader, null) && tokenHeader.startsWith(JWTConfig.tokenPrefix)) {
            try {
                // 截取 JWT 前缀
                String token = tokenHeader.replace(JWTConfig.tokenPrefix, "");

                // 解析 JWT
                Claims claims = Jwts.parser()
                        .setSigningKey(JWTConfig.secret)
                        .parseClaimsJws(token)
                        .getBody();

                // 获取用户名
                String userName = claims.getSubject();
                // 获取用户 ID
                String userId = claims.getId();

                if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(userId)) {
                    // 获取角色
                    List<GrantedAuthority> authorities = new ArrayList<>();

                    // 将之前生成 Token 时放入的权限字符串取出
                    String authority = claims.get("authorities").toString();

                    log.info(" { authority 字符串 } " + authority);

                    // 如果它不为空
                    if (!StringUtils.isEmpty(authority)) {
                        // 解析 JSON 格式的权限字符串
                        List<Map<String, String>> maps = (List<Map<String, String>>) JSON.parse(authority);
                        // 遍历权限
                        maps.stream().forEach(grantedAuthority -> {
                            // 如果不为空则取出其中的每一个 GrantedAuthority 存入 authorities
                            if (!StringUtils.isEmpty(grantedAuthority)) {
                                authorities.add(new SimpleGrantedAuthority("" + grantedAuthority.get("authority")));
                            }
                        });
                        log.info( " { GrantedAuthority 数据 } " + maps.toString());
                    }

                    // 放置数据到 Spring Security 安全实体中
                    SecurityUserDetails securityUserDetails = new SecurityUserDetails();
                    securityUserDetails.setUserName(claims.getSubject());
                    securityUserDetails.setUserId(claims.getId());
                    securityUserDetails.setAuthorities(authorities);

                    // 将当前登录用户放入到 Spring Security 的上下文中
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(securityUserDetails, userName, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                log.info(" { Token 过期 } " + e.getLocalizedMessage());
                // FIXME 理论上过期与无效都需要抛异常给前端
            } catch (Exception e) {
                log.info(" { Token 无效 } " + e.getLocalizedMessage());
            }
        }

        chain.doFilter(request, response);
        return;
    }
}
