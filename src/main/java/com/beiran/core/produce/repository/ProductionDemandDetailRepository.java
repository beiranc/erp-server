package com.beiran.core.produce.repository;

import com.beiran.core.produce.entity.ProductionDemandDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 生产需求计划详细 Repository
 */

public interface ProductionDemandDetailRepository extends JpaRepository<ProductionDemandDetail, String>, JpaSpecificationExecutor<ProductionDemandDetail> {

    /**
     * 根据生产需求计划详细项编号修改生产数量
     * @param productionDetailId 生产需求计划详细项编号
     * @param productionNumber 生产数量
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_production_demand_detail SET production_number = ?2 WHERE production_detail_id = ?1", nativeQuery = true)
    int updateNumber(String productionDetailId, Long productionNumber);

    /**
     * 根据生产需求计划编号删除其所拥有的子项
     * @param productionId 生产需求计划编号
     */
    @Modifying
    @Query(value = "DELETE FROM erp_production_demand_detail WHERE production_detail_id = ?1", nativeQuery = true)
    void deleteByProductionId(String productionId);

    /**
     * 根据生产需求计划编号查询生产需求计划子项
     * @param productionId 生产需求计划编号
     * @param pageable 分页参数
     * @return
     */
    List<ProductionDemandDetail> findByBelongDemand_ProductionId(String productionId, Pageable pageable);
}
