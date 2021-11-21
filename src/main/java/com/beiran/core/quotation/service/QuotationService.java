package com.beiran.core.quotation.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.quotation.entity.Quotation;

import java.io.File;

/**
 * QuotationService 接口
 */
public interface QuotationService extends GenericService<Quotation, String> {
    /**
     * 导出一个报价单
     * @param quotationId 报价单 ID
     * @return File
     */
    File createQuotationFile(String quotationId);
}
