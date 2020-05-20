package com.beiran.core.product.service.impl;

import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.core.product.entity.ProductCategory;
import com.beiran.core.product.repository.ProductCategoryRepository;
import com.beiran.core.product.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * ProductCategoryService 实现类
 */

@Service("productCategoryService")
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public ProductCategory save(ProductCategory entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的产品分类不能为空");
        }
        return productCategoryRepository.save(entity);
    }

    @Override
    public void deleteAll(List<ProductCategory> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的产品分类不能为空");
        }
        productCategoryRepository.deleteAll(entities);
    }

    @Override
    public ProductCategory update(ProductCategory entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的产品分类不能为空");
        }
        return productCategoryRepository.saveAndFlush(entity);
    }

    @Override
    public ProductCategory findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("产品分类编号不能为空");
        }
        return productCategoryRepository.findById(id).orElseThrow(() -> new EntityNotExistException("产品分类不存在"));
    }

    @Override
    public Page<ProductCategory> findAll(Pageable pageable) {
        return productCategoryRepository.findAll(pageable);
    }
}
