package com.beiran.security.entity;

import com.beiran.common.utils.DateTimeUtils;
import com.beiran.core.system.entity.Job;
import com.beiran.core.system.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Spring Security 用户的实体，需要实现 UserDetails 接口
 */

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SecurityUserDetails implements UserDetails {

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 登录密码
     */
    @JsonIgnore
    private String userPassword;

    /**
     * 用户状态
     */
    @JsonIgnore
    private User.UserState userState;

    /**
     * 用户联系方式
     */
    private String userPhone;

    /**
     * 用户性别
     */
    @JsonIgnore
    private User.UserSex userSex;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户创建时间
     */
    @JsonIgnore
    private Date userCreateTime;

    /**
     * 用户岗位
     */
    @JsonIgnore
    private Job userJob;

    /**
     * 用户角色
     */
    @JsonIgnore
    private Collection<GrantedAuthority> authorities;

    /**
     * 账户是否过期
     */
    private boolean isAccountNonExpired = false;

    /**
     * 账户是否停用
     */
    private boolean isAccountNonLocked = false;

    /**
     * 证书是否过期
     */
    private boolean isCredentialsNonExpired = false;

    /**
     * 账户是否有效
     */
    private boolean isEnabled = true;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public Collection getRoles() {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    public String getDept() {
        return userJob.getJobDept().getDeptName();
    }

    public String getJob() {
        return userJob.getJobName();
    }

    public String getSex() {
        return userSex.getValue();
    }

    public String getState() {
        return userState.getValue();
    }

    public String getCreateTime() {
        if (Objects.equals(userCreateTime, null)) {
            return null;
        }
        return DateTimeUtils.getDateTime(userCreateTime);
    }
}
