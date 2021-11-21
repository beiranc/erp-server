package com.beiran.core.quotation.repository;

import com.beiran.core.quotation.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 报价单实体
 */
public interface QuotationRepository extends JpaRepository<Quotation, String>, JpaSpecificationExecutor<Quotation> {
}