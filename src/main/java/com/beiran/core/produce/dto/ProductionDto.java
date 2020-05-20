package com.beiran.core.produce.dto;

import com.beiran.core.produce.entity.ProductionDemand;
import com.beiran.core.system.dto.UserSmallDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductionDto {

    /**
     * 生产需求计划编号
     */
    private String productionId;

    /**
     * 生产需求计划主题
     */
    private String productionSubject;

    /**
     * 生产需求计划创建时间
     */
    private Date productionCreateTime;

    /**
     * 生产需求计划创建者
     */
    private UserSmallDto applicant;

    /**
     * 生产需求计划状态
     */
    private ProductionDemand.ProductionDemandState productionState;

    /**
     * 上一次修改时间
     */
    private Date lastModifiedTime;

    /**
     * 上一次修改操作者
     */
    private UserSmallDto operator;

    /**
     * 生产需求计划详细项
     */
    private List<ProductionDetailDto> productionDetails;
}
