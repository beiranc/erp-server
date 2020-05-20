package com.beiran.core.system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * 系统使用者
 */

@Getter
@Setter
@Table(name = "erp_user")
@Entity
public class User {

    /**
     * 用户编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String userId;

    /**
     * 用户名（用作登录）
     */
    @Column(length = 50, unique = true, nullable = false)
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户昵称
     */
    @Column(length = 100)
    private String nickName;

    /**
     * 登录密码，存入数据库前先加密
     */
    private String userPassword;

    /**
     * 用户状态
     */
    private UserState userState;

    /**
     * 用户联系方式
     */
    @Column(length = 32)
    private String userPhone;

    /**
     * 用户性别
     */
    private UserSex userSex;

    /**
     * 用户邮箱
     */
    @Column(length = 100)
    private String userEmail;

    /**
     * 用户创建时间，创建时间默认为入职时间，由 Hibernate 自动赋值
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date userCreateTime;

    /**
     * 用户岗位
     */
    @JoinColumn(name = "userJobId")
    @OneToOne
    private Job userJob;

    /**
     * 用户角色
     */
    @JoinTable(name = "erp_user_role",
            joinColumns = @JoinColumn(referencedColumnName = "userId", name = "userId") ,
            inverseJoinColumns = { @JoinColumn(referencedColumnName = "roleId", name = "roleId") })
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> userRoles;

    /**
     * 用户状态枚举类
     */
    public enum UserState {

        ACTIVE("启用"),

        DISABLED("停用");

        private String value;

        private UserState(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * 用户性别枚举类
     */
    public enum UserSex {

        MALE("男"),

        FEMALE("女"),

        SECRET("保密");

        private String value;

        private UserSex(String value) {
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
        if (o ==  null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(userName, user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName);
    }
}
