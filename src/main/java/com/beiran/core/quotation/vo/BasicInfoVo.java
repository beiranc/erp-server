package com.beiran.core.quotation.vo;

import com.beiran.core.quotation.entity.BasicInfo;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建货物信息时所需字段
 */
@Data
public class BasicInfoVo {

    /**
     * 主图, 存绝对路径
     */
    @NotBlank(message = "主图路径不能为空")
    private String mainPicture;

    /**
     * 型号
     */
    @NotBlank(message = "请填写型号")
    private String model;

    /**
     * 前叉
     */
    @NotBlank(message = "请填写前叉信息")
    private String frontFork;

    /**
     * 显示器
     */
    @NotNull(message = "请选择显示器类型")
    private BasicInfo.DisplayType display;

    /**
     * 油门转把
     */
    @NotNull(message = "请选择油门转把类型")
    private BasicInfo.ThrottleType throttle;

    /**
     * 变速器
     */
    @NotBlank(message = "请填写变速器信息")
    private String derailleur;

    /**
     * 功率
     */
    @NotBlank(message = "请填写功率")
    private String power;

    /**
     * 电压
     */
    @NotBlank(message = "请填写电压")
    private String voltage;

    /**
     * 轮胎尺寸
     */
    @NotBlank(message = "请填写轮胎尺寸")
    private String wheelSize;

    /**
     * 车架材质
     */
    @NotBlank(message = "请填写车架材质")
    private String frame;

    /**
     * 最大速度
     */
    @NotBlank(message = "请填写最大速度")
    private String maxSpeed;

    /**
     * 单次充电里程
     */
    @NotBlank(message = "请填写单次充电里程")
    private String mileagePerCharge;

    /**
     * 电池容量
     */
    @NotBlank(message = "请填写电池容量")
    private String batteryCapacity;

    /**
     * 制动系统/刹车
     */
    @NotNull(message = "请选择制动系统/刹车类型")
    private BasicInfo.BrakeSystemType brakeSystem;

    /**
     * 起订量
     */
    @NotBlank(message = "请填写起订量")
    private String moq;

    /**
     * 单价
     */
    @NotBlank(message = "请填写单价")
    private String fobPrice;

    /**
     * 纸箱大小
     */
    @NotBlank(message = "请填写纸箱大小")
    private String cartonSize;

    /**
     * 海关编码
     */
    @NotBlank(message = "请填写海关编码")
    private String hsCode;

    /**
     * 是否支持 LOGO 定制
     */
    @NotNull(message = "请选择是否支持是否支持 LOGO 定制")
    private Boolean logo = true;

    /**
     * 是否支持外包装定制
     */
    @NotNull(message = "请选择是否支持外包装定制")
    private Boolean outerPacking = true;

    /**
     * 是否支持图案定制
     */
    @NotNull(message = "请选择是否支持图案定制")
    private Boolean design = true;
}
