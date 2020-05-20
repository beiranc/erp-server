package com.beiran.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 封装基本的 CRUD 操作
 * @param <T> 给定实体类型
 * @param <ID> 给定实体 ID 类型
 */

public interface GenericService<T, ID> {

    /**
     * 保存操作
     * @param entity 需要保存的实体
     * @return T 返回保存的实体
     */
    T save(T entity);

    /**
     * 根据给定一批实体进行批量删除
     * @param entities
     */
    void deleteAll(List<T> entities);

    /**
     * 更新操作
     * @param entity 需要更新的实体
     * @return 更新后的实体
     */
    T update(T entity);

    /**
     * 根据给定 ID 查询一个实体
     * @param id 需要查询的实体 ID
     * @return 查询出来的实体
     */
    T findById(ID id);

    /**
     * 分页查询
     * @param pageable 常用 Pageable 接口的实现类 PageRequest
     * @return 返回一个 Page 对象
     */
    Page<T> findAll(Pageable pageable);
}

