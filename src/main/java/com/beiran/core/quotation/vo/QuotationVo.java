package com.beiran.core.quotation.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * 创建报价单所需数据
 */
@Data
public class QuotationVo {
    /**
     * 公司中文名
     */
    @NotBlank(message = "请填写公司中文名")
    private String chineseName;

    /**
     * 公司英文名
     */
    @NotBlank(message = "请填写公司英文名")
    private String englishName;

    /**
     * 地址
     */
    @NotBlank(message = "请填写公司地址")
    private String address;

    /**
     * 官网
     */
    @NotBlank(message = "请填写公司官网地址")
    private String officialWebsite;

    /**
     * 业务员
     */
    @NotBlank(message = "请选择业务员")
    private String salesmanId;

    /**
     * 报价单中每一项货物信息
     */
    @NotNull(message = "请选择基本信息")
    private Set<String> basicInfos;
}
