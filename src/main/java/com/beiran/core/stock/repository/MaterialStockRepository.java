package com.beiran.core.stock.repository;

import com.beiran.core.stock.entity.MaterialStock;
import com.beiran.core.stock.vo.MaterialNumberVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 物料-仓库 Repository
 */
public interface MaterialStockRepository extends JpaRepository<MaterialStock, String>, JpaSpecificationExecutor<MaterialStock> {

    /**
     * 查找仓库中特定物料的数量
     * @param materialId 物料编号
     * @return MaterialNumberVo
     */
    @Query("SELECT new com.beiran.core.stock.vo.MaterialNumberVo(m.material.materialId, sum(m.materNumber)) FROM MaterialStock m WHERE m.material.materialId = :materialId")
    MaterialNumberVo findMaterialNumber(@Param("materialId") String materialId);

    /**
     * 查找特定仓库中特定物料的数量
     * @param materialId 物料编号
     * @param stockId 仓库编号
     * @return MaterialNumberVo
     */
    @Query("SELECT new com.beiran.core.stock.vo.MaterialNumberVo(m.material.materialId, m.stock.stockId, m.materNumber) FROM MaterialStock m WHERE m.material.materialId = :materialId AND m.stock.stockId = :stockId")
    MaterialNumberVo findMaterialStockNumber(@Param("materialId") String materialId, @Param("stockId") String stockId);

    /**
     * 统计物料-仓库中物料数量（根据 stockId 分类）
     * @return List<MaterialNumberVo>
     */
    @Query("SELECT new com.beiran.core.stock.vo.MaterialNumberVo(sum(m.materNumber), m.stock.stockId) FROM MaterialStock m GROUP BY m.stock.stockId")
    List<MaterialNumberVo> findAllMaterialStockNumber();

    /**
     * 修改特定仓库中特定物料的数量
     * @param materialId 物料编号
     * @param stockId 仓库编号
     * @param number 数量
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_material_stock SET mater_number = ?3 WHERE material_id = ?1 AND stock_id = ?2", nativeQuery = true)
    int updateNumber(String materialId, String stockId, Long number);
}
