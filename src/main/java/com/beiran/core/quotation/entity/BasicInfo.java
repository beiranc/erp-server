package com.beiran.core.quotation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * 报价单中每一项货物基本信息
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "erp_phoenix_basic_info")
@Entity
public class BasicInfo {

    /**
     * 基本信息编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String basicInfoId;

    /**
     * 主图, 存绝对路径
     */
    @Column(length = 180)
    @NotBlank(message = "主图路径不能为空")
    private String mainPicture;

    /**
     * 型号
     */
    @Column(length = 180)
    @NotBlank(message = "请填写型号")
    private String model;

    /**
     * 前叉
     */
    @Column(length = 180)
    @NotBlank(message = "请填写前叉信息")
    private String frontFork;

    /**
     * 显示器
     */
    @NotNull(message = "请选择显示器类型")
    private DisplayType display;

    /**
     * 油门转把
     */
    @NotNull(message = "请选择油门转把类型")
    private ThrottleType throttle;

    /**
     * 变速器
     */
    @Column(length = 180)
    @NotBlank(message = "请填写变速器信息")
    private String derailleur;

    /**
     * 功率
     */
    @Column(length = 10)
    @NotBlank(message = "请填写功率")
    private String power;

    /**
     * 电压
     */
    @Column(length = 10)
    @NotBlank(message = "请填写电压")
    private String voltage;

    /**
     * 轮胎尺寸
     */
    @Column(length = 180)
    @NotBlank(message = "请填写轮胎尺寸")
    private String wheelSize;

    /**
     * 车架材质
     */
    @Column(length = 180)
    @NotBlank(message = "请填写车架材质")
    private String frame;

    /**
     * 最大速度
     */
    @Column(length = 10)
    @NotBlank(message = "请填写最大速度")
    private String maxSpeed;

    /**
     * 单次充电里程
     */
    @Column(length = 10)
    @NotBlank(message = "请填写单次充电里程")
    private String mileagePerCharge;

    /**
     * 电池容量
     */
    @Column(length = 10)
    @NotBlank(message = "请填写电池容量")
    private String batteryCapacity;

    /**
     * 制动系统/刹车
     */
    @NotNull(message = "请选择制动系统/刹车类型")
    private BrakeSystemType brakeSystem;

    /**
     * 起订量
     */
    @Column(length = 10)
    @NotBlank(message = "请填写起订量")
    private String moq;

    /**
     * 单价
     */
    @Column(length = 10)
    @NotBlank(message = "请填写单价")
    private String fobPrice;

    /**
     * 纸箱大小
     */
    @Column(length = 30)
    @NotBlank(message = "请填写纸箱大小")
    private String cartonSize;

    /**
     * 海关编码
     */
    @Column(length = 50)
    @NotBlank(message = "请填写海关编码")
    private String hsCode;

    /**
     * 是否支持 LOGO 定制
     */
    @NotNull(message = "请选择是否支持是否支持 LOGO 定制")
    private Boolean logo;

    /**
     * 是否支持外包装定制
     */
    @NotNull(message = "请选择是否支持外包装定制")
    private Boolean outerPacking;

    /**
     * 是否支持图案定制
     */
    @NotNull(message = "请选择是否支持图案定制")
    private Boolean design;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createTime;

    /**
     * 指定由 Quotation 来维护关系
     */
    @ManyToMany(mappedBy = "basicInfos")
    @JsonIgnore
    private Set<Quotation> basicInfoQuotations;

    /**
     * 显示器类型, 若为 CAN_BE_ADDED, 则字体颜色为红色
     */
    public enum DisplayType {
        WITH_LED("With LED, such as LED470 etc."),

        WITH_LCD("With LCD"),

        CAN_BE_ADDED("LCD display or LED display can be added");

        private String value;

        DisplayType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 油门转把类型
     */
    public enum ThrottleType {
        WITH_THROTTLE("With Throttle"),

        CAN_BE_ADDED("Can be added");

        private String value;

        ThrottleType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 制动系统/刹车类型
     */
    public enum BrakeSystemType {
        HYDRAULIC_DISC("Hydraulic disc brake"),

        MECHANICAL_DISC("Mechanical disc brake");

        private String value;

        BrakeSystemType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}