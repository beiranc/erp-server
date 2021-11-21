package com.beiran.core.quotation.repository;

import com.beiran.core.quotation.entity.Salesman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 业务员 Repository
 */
public interface SalesmanRepository extends JpaRepository<Salesman, Long>, JpaSpecificationExecutor<Salesman> {
}