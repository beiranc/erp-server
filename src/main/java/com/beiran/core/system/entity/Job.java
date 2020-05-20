package com.beiran.core.system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 岗位实体
 */

@Getter
@Setter
@Table(name = "erp_job")
@Entity
public class Job {

    /**
     * 岗位编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String jobId;

    /**
     * 岗位名<br>
     * TODO: 同一个部门下不能有相同的岗位名
     */
    @Column(length = 100)
    private String jobName;

    /**
     * 岗位状态
     */
    private JobState jobState;

    /**
     * 岗位创建时间，由 Hibernate 自动赋值
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date jobCreateTime;

    /**
     * 岗位所属部门，使用外键进行关联
     */
    @JoinColumn(name = "deptId")
    @OneToOne(fetch = FetchType.EAGER)
    private Dept jobDept;

    /**
     * 岗位状态枚举类
     */
    public enum JobState {

        ACTIVE("启用"),

        DISABLED("停用");

        private String value;

        private JobState(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
}
