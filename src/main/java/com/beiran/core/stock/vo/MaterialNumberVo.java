package com.beiran.core.stock.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * 用于查询仓库中存储的物料数量
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MaterialNumberVo {

    @NotBlank(message = "物料编号不能为空")
    private String materialId;

    @NotBlank(message = "仓库编号不能为空")
    private String stockId;

    @PositiveOrZero(message = "存储数量不能小于零")
    private Long materialNumber;

    public MaterialNumberVo(Long materialNumber, String stockId) {
        this.materialNumber = materialNumber;
        this.stockId = stockId;
    }

    public MaterialNumberVo(String materialId, Long materialNumber) {
        this.materialId = materialId;
        this.materialNumber = materialNumber;
    }

    public MaterialNumberVo(String materialId, String stockId, Long materialNumber) {
        this.materialId = materialId;
        this.stockId = stockId;
        this.materialNumber = materialNumber;
    }
}
