package com.beiran.core.stock.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class StockSmallDto {

    /**
     * 仓库编号
     */
    private String stockId;

    /**
     * 仓库名
     */
    @NotBlank(message = "仓库名不能为空")
    private String stockName;
}
