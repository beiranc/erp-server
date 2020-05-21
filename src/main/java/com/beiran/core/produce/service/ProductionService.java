package com.beiran.core.produce.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.produce.dto.ProductionDto;
import com.beiran.core.produce.entity.ProductionDemand;
import com.beiran.core.produce.vo.ProductionVo;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * ProductionService 接口
 */

public interface ProductionService extends GenericService<ProductionDemand, String> {

    /* ------------------------------------- 生产需求计划相关 ----------------------------------------- */

    /**
     * 创建生产需求计划，默认为 CREATED 状态
     * @param productionVo 创建生产需求计划所需数据
     * @return ProductionDto
     */
    ProductionDto createProductionDemand(ProductionVo productionVo);

    /**
     * 修改生产需求计划状态
     * @param productionId 生产需求计划编号
     * @param productionState 生产需求计划状态
     * @return 是否修改成功
     */
    Boolean updateState(String productionId, ProductionDemand.ProductionDemandState productionState);

    /**
     * 修改生产需求计划状态为 CONFIRMED
     * @param productionId 生产需求计划编号
     * @return 是否修改成功
     */
    Boolean confirmProduction(String productionId);

    /**
     * 修改生产需求计划状态为 VERIFYING
     * @param productionId 生产需求计划编号
     * @return 是否修改成功
     */
    Boolean verifyProduction(String productionId);

    /**
     * 修改生产需求计划状态为 IMPORTED
     * @param productionId 生产需求计划编号
     * @return 是否修改成功
     */
    Boolean importProduction(String productionId);

    /**
     * 修改生产需求计划状态为 REPRODUCED
     * @param productionId 生产需求计划编号
     * @return 是否修改成功
     */
    Boolean reproduceProduction(String productionId);

    /**
     * 修改生产需求计划状态为 CLOSED
     * @param productionId 生产需求计划编号
     * @return 是否修改成功
     */
    Boolean closeProduction(String productionId);

    /**
     * 根据生产员的用户名查询
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<ProductionDto>
     */
    List<ProductionDto> getProductionsByUserName(String userName, Pageable pageable);

    /**
     * 根据用户名与生产需求计划状态查询
     * @param userName 用户名
     * @param productionState 生产需求计划状态
     * @param pageable 分页参数
     * @return List<ProductionDto>
     */
    List<ProductionDto> getProductionsByUserNameAndState(String userName, ProductionDemand.ProductionDemandState productionState, Pageable pageable);

    /**
     * 根据生产需求计划状态查询
     * @param productionState 生产需求计划状态
     * @param pageable 分页参数
     * @return List<ProductionDto>
     */
    List<ProductionDto> getProductionsByState(ProductionDemand.ProductionDemandState productionState, Pageable pageable);

    /**
     * 根据生产需求计划主题模糊查询
     * @param productionSubject 生产需求计划主题
     * @param pageable 分页参数
     * @return List<ProductionDto>
     */
    List<ProductionDto> getProductionsBySubject(String productionSubject, Pageable pageable);

    /**
     * 根据生产需求计划创建时间查询
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<ProductionDto>
     */
    List<ProductionDto> getProductionsByCreateTime(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 根据生产需求计划上一次修改时间查询
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<ProductionDto>
     */
    List<ProductionDto> getProductionsByModifiedTime(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 获取所有的生产需求计划
     * @param pageable 分页参数
     * @return List<ProductionDto>
     */
    List<ProductionDto> getAllProductions(Pageable pageable);

    /**
     * 导出指定用户的生产需求计划
     * @param userName 用户名
     * @param pageable 分页参数
     * @return File
     */
    File createSpecExcelFile(String userName, Pageable pageable);

    /**
     * 导出生产需求计划
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);

    /* ------------------------------------- 生产需求计划相关 ----------------------------------------- */

    /**
     * 根据生产需求计划详细项编号修改生产数量
     * @param productionDetailId 生产需求计划详细项编号
     * @param productionNumber 生产数量
     * @return 是否修改成功
     */
    Boolean updateProductionDetailNumber(String productionDetailId, Long productionNumber);

    /**
     * 根据生产需求计划编号删除其下所有的生产需求详细项
     * @param productionId 生产需求计划编号
     */
    void deleteProductionDetail(String productionId);

    /**
     * 导出指定的生产需求计划下的所有的生产需求计划详细项
     * @param productionId 生产需求计划编号
     * @param pageable 分页参数
     * @return File
     */
    File createDetailExcelFile(String productionId, Pageable pageable);
}
