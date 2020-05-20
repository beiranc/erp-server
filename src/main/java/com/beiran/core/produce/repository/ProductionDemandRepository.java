package com.beiran.core.produce.repository;

import com.beiran.core.produce.entity.ProductionDemand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * 生产需求计划 Repository
 */

public interface ProductionDemandRepository extends JpaRepository<ProductionDemand, String>, JpaSpecificationExecutor<ProductionDemand> {

    /**
     * 根据生产需求计划编号修改状态
     * @param productionId 生产需求计划编号
     * @param productionState 生产需求计划状态
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_production_demand SET production_state = ?2 WHERE production_id = ?1", nativeQuery = true)
    int updateState(String productionId, ProductionDemand.ProductionDemandState productionState);

    /**
     * 根据生产需求计划创建人的用户名查询
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<ProductionDemand>
     */
    List<ProductionDemand> findByProductionApplicant_UserName(String userName, Pageable pageable);

    /**
     * 根据生产需求计划创建人的用户名与状态查询
     * @param userName 用户名
     * @param productionState 生产需求计划状态
     * @param pageable 分页参数
     * @return List<ProductionDemand>
     */
    List<ProductionDemand> findByProductionApplicant_UserNameAndProductionState(String userName, ProductionDemand.ProductionDemandState productionState, Pageable pageable);

    /**
     * 根据生产需求计划状态查询
     * @param productionState 生产需求计划状态
     * @param pageable 分页参数
     * @return List<ProductionDemand>
     */
    List<ProductionDemand> findByProductionState(ProductionDemand.ProductionDemandState productionState, Pageable pageable);

    /**
     * 根据生产需求计划主题模糊查询
     * @param productionSubject 生产需求计划主题
     * @param pageable 分页参数
     * @return List<ProductionDemand>
     */
    List<ProductionDemand> findByProductionSubjectContaining(String productionSubject, Pageable pageable);

    /**
     * 根据生产需求计划创建时间查询
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<ProductionDemand>
     */
    List<ProductionDemand> findByProductionCreateTimeBetween(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 根据生产需求计划上一次修改时间查询
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<ProductionDemand>
     */
    List<ProductionDemand> findByLastModifiedTimeBetween(Date leftTime, Date rightTime, Pageable pageable);
}
