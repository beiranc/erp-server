package com.beiran.core.stock.repository;

import com.beiran.core.stock.entity.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 仓库 Repository
 */

public interface StockRepository extends JpaRepository<Stock, String>, JpaSpecificationExecutor<Stock> {

    /**
     * 根据仓库名查询仓库信息
     * @param stockName 仓库名
     * @return Optional<Stock>
     */
    Optional<Stock> findByStockName(String stockName);

    /**
     * 根据仓库名模糊查询仓库信息
     * @param stockName 仓库名
     * @param pageable 分页参数
     * @return List<Stock>
     */
    List<Stock> findByStockNameContaining(String stockName, Pageable pageable);

    /**
     * 根据仓库管理员用户名查询仓库信息
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<Stock>
     */
    List<Stock> findByStockManager_UserName(String userName, Pageable pageable);

    /**
     * 更换仓库管理员
     * @param userId 用户编号
     * @param stockId 仓库编号
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_stock SET stock_manager_id = ?1 WHERE stock_id = ?2", nativeQuery = true)
    int updateStockManager(String userId, String stockId);
}
