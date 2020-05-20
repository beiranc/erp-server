package com.beiran.core.material.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.material.entity.Material;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;

/**
 * MaterialService 接口
 */
public interface MaterialService extends GenericService<Material, String> {

    /**
     * 根据物料分类编号查询物料
     * @param categoryId 物料分类编号
     * @param pageable 分页参数
     * @return List<Material>
     */
    List<Material> getMaterialsByCategory(String categoryId, Pageable pageable);

    /**
     * 导出物料信息
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);
}
