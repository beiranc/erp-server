package com.beiran.core.produce.vo;

import com.beiran.core.produce.entity.ProductionDemand;
import com.beiran.core.system.dto.UserSmallDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ProductionVo {

    /**
     * 生产需求计划主题
     */
    @NotBlank(message = "生产需求计划主题不能为空")
    private String productionSubject;

    /**
     * 创建人
     */
    @NotNull(message = "生产需求计划创建者不能为空")
    private UserSmallDto applicant;

    /**
     * 生产需求计划状态，默认为 CREATED
     */
    private ProductionDemand.ProductionDemandState productionState = ProductionDemand.ProductionDemandState.CREATED;

    /**
     * 生产需求计划详细项
     */
    @NotNull(message = "生产需求计划子项不能为空")
    private List<ProductionDetailVo> productionDetails;
}
