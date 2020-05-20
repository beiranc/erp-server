package com.beiran.core.product.repository;

import com.beiran.core.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 产品分类 Repository
 */
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String>, JpaSpecificationExecutor<ProductCategory> {
}
