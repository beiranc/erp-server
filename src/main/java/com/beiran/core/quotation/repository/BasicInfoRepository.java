package com.beiran.core.quotation.repository;

import com.beiran.core.quotation.entity.BasicInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 基本信息 Repository
 */
public interface BasicInfoRepository extends JpaRepository<BasicInfo, String>, JpaSpecificationExecutor<BasicInfo> {
}