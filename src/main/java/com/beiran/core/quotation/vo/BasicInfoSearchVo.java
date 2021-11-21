package com.beiran.core.quotation.vo;

import com.beiran.core.quotation.entity.BasicInfo;
import lombok.Data;

/**
 * 创建报价单时搜索基本信息的条件
 */
@Data
public class BasicInfoSearchVo {
    /**
     * 型号
     */
    private String model;

    /**
     * 前叉
     */
    private String frontFork;

    /**
     * 显示器
     */
    private BasicInfo.DisplayType display;

    /**
     * 油门转把
     */
    private BasicInfo.ThrottleType throttle;

    /**
     * 变速器
     */
    private String derailleur;

    /**
     * 功率
     */
    private String power;

    /**
     * 电压
     */
    private String voltage;

    /**
     * 轮胎尺寸
     */
    private String wheelSize;

    /**
     * 车架材质
     */
    private String frame;

    /**
     * 电池容量
     */
    private String batteryCapacity;

    /**
     * 制动系统/刹车
     */
    private BasicInfo.BrakeSystemType brakeSystem;
}
