package com.beiran.core.product.repository;

import com.beiran.core.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 产品 Repository
 */
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    /**
     * 根据产品分类编号查询产品
     * @param categoryId 产品分类编号
     * @param pageable 分页参数
     * @return List<Product>
     */
    List<Product> findByProductCategory_CategoryId(String categoryId, Pageable pageable);
}
