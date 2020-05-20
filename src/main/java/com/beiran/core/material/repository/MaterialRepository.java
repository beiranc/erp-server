package com.beiran.core.material.repository;

import com.beiran.core.material.entity.Material;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 物料 Repository
 */
public interface MaterialRepository extends JpaRepository<Material, String>, JpaSpecificationExecutor<Material> {

    /**
     * 根据物料分类查询物料信息
     * @param categoryId 物料分类编号
     * @param pageable 分页参数
     * @return List<Material>
     */
    List<Material> findByMaterialCategory_CategoryId(String categoryId, Pageable pageable);
}
