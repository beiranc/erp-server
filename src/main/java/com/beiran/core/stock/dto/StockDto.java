package com.beiran.core.stock.dto;

import com.beiran.core.system.dto.UserSmallDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StockDto {

    /**
     * 仓库编号
     */
    private String stockId;

    /**
     * 仓库名
     */
    @NotBlank(message = "仓库名不能为空")
    private String stockName;

    /**
     * 仓库位置
     */
    @NotBlank(message = "仓库位置不能为空")
    private String stockPosition;

    /**
     * 仓库管理员
     */
    @NotNull(message = "仓库管理员不能为空")
    private UserSmallDto manager;
}
