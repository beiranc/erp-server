package com.beiran.core.material.service.impl;

import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.core.material.entity.MaterialCategory;
import com.beiran.core.material.repository.MaterialCategoryRepository;
import com.beiran.core.material.service.MaterialCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * MaterialCategoryService 接口的实现类
 */
@Service("materialCategoryService")
public class MaterialCategoryServiceImpl implements MaterialCategoryService {

    @Autowired
    private MaterialCategoryRepository materialCategoryRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterialCategory save(MaterialCategory entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的物料分类不能为空");
        }
        return materialCategoryRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<MaterialCategory> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的物料分类不能为空");
        }
        materialCategoryRepository.deleteAll(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterialCategory update(MaterialCategory entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的物料分类不能为空");
        }
        return materialCategoryRepository.saveAndFlush(entity);
    }

    @Override
    public MaterialCategory findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("物料分类编号不能为空");
        }
        return materialCategoryRepository.findById(id).orElseThrow(() -> new EntityNotExistException("物料分类不存在"));
    }

    @Override
    public Page<MaterialCategory> findAll(Pageable pageable) {
        return materialCategoryRepository.findAll(pageable);
    }
}
