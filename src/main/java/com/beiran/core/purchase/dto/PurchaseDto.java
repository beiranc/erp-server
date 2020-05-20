package com.beiran.core.purchase.dto;

import com.beiran.core.purchase.entity.PurchaseOrder;
import com.beiran.core.system.dto.UserSmallDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PurchaseDto {

    /**
     * 采购计划编号
     */
    private String purchaseId;

    /**
     * 采购计划主题
     */
    private String purchaseSubject;

    /**
     * 采购计划创建时间
     */
    private Date purchaseCreateTime;

    /**
     * 创建人
     */
    private UserSmallDto applicant;

    /**
     * 采购计划状态
     */
    private PurchaseOrder.PurchaseOrderState purchaseState;

    /**
     * 上一次修改时间
     */
    private Date lastModifiedTime;

    /**
     * 上一次修改的操作者
     */
    private UserSmallDto operator;

    /**
     * 采购计划详细项
     */
    private List<PurchaseDetailDto> purchaseOrderDetails;
}
