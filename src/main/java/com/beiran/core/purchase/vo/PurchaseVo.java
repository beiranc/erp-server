package com.beiran.core.purchase.vo;

import com.beiran.core.purchase.entity.PurchaseOrder;
import com.beiran.core.system.dto.UserSmallDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PurchaseVo {

    /**
     * 采购计划主题
     */
    @NotBlank(message = "采购计划主题不能为空")
    private String purchaseSubject;

    /**
     * 创建人
     */
    @NotNull(message = "采购计划创建者不能为空")
    private UserSmallDto applicant;

    /**
     * 采购计划状态，默认为 CREATED
     */
    private PurchaseOrder.PurchaseOrderState purchaseState = PurchaseOrder.PurchaseOrderState.CREATED;

    /**
     * 采购计划详细项
     */
    @NotNull(message = "采购计划子项不能为空")
    private List<PurchaseDetailVo> purchaseOrderDetails;
}
