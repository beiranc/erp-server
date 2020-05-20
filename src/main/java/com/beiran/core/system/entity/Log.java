package com.beiran.core.system.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 操作日志实体
 */

@Data
@Table(name = "erp_log")
@Entity
public class Log {

    /**
     * 日志编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String logId;

    /**
     * 用户名
     */
    @Column(length = 50)
    private String userName;

    /**
     * 用户所做的操作
     */
    @Column(length = 50)
    private String operation;

    /**
     * 请求方法
     */
    @Column(length = 200)
    private String method;

    /**
     * 请求参数
     */
    @Column(columnDefinition = "TEXT")
    private String params;

    /**
     * 请求耗时，以毫秒为单位
     */
    private Long spendTime;

    /**
     * 所在 IP
     */
    private String ip;

    /**
     * 创建时间，由 Hibernate 自动赋值
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}
