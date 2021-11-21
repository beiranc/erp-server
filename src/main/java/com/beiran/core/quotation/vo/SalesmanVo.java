package com.beiran.core.quotation.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 创建 Salesman 所需字段
 */
@Data
public class SalesmanVo {

    @NotBlank(message = "业务员姓名不能为空")
    private String name;

    @NotBlank(message = "电话不能为空")
    private String tel;

    @NotBlank(message = "传真不能为空")
    private String fax;

    @Email(message = "邮箱格式不正确")
    private String email;
}
