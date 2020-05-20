package com.beiran.core.material.repository;

import com.beiran.core.material.entity.MaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 物料分类 Repository
 */
public interface MaterialCategoryRepository extends JpaRepository<MaterialCategory, String>, JpaSpecificationExecutor<MaterialCategory> {
}
