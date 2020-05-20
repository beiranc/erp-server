package com.beiran.core.product.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.product.entity.Product;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;

/**
 * ProductService 接口
 */

public interface ProductService extends GenericService<Product, String> {

    /**
     * 根据产品分类编号查询产品
     * @param categoryId 产品分类编号
     * @param pageable 分页参数
     * @return List<Product>
     */
    List<Product> getProductsByCategory(String categoryId, Pageable pageable);

    /**
     * 导出产品信息
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);
}
