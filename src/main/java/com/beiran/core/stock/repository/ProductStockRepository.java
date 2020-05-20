package com.beiran.core.stock.repository;

import com.beiran.core.stock.entity.ProductStock;
import com.beiran.core.stock.vo.ProductNumberVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 产品-仓库 Repository
 */
public interface ProductStockRepository extends JpaRepository<ProductStock, String>, JpaSpecificationExecutor<ProductStock> {

    /**
     * 查找仓库中特定产品的数量
     * @param productId 产品编号
     * @return ProductNumberVo
     */
    @Query("SELECT new com.beiran.core.stock.vo.ProductNumberVo(p.product.productId, sum(p.prodNumber)) FROM ProductStock p WHERE p.product.productId = :productId")
    ProductNumberVo findProductNumber(@Param("productId") String productId);

    /**
     * 查找特定仓库中特定产品的数量
     * @param productId 产品编号
     * @param stockId 仓库编号
     * @return ProductNumberVo
     */
    @Query("SELECT new com.beiran.core.stock.vo.ProductNumberVo(p.product.productId, p.stock.stockId, p.prodNumber) FROM ProductStock p WHERE p.product.productId = :productId AND p.stock.stockId = :stockId")
    ProductNumberVo findProductStockNumber(@Param("productId") String productId, @Param("stockId") String stockId);

    /**
     * 统计产品-仓库中产品数量（根据 stockId 分类）
     * @return List<ProductNumberVo>
     */
    @Query("SELECT new com.beiran.core.stock.vo.ProductNumberVo(sum(p.prodNumber), p.stock.stockId) FROM ProductStock p GROUP BY p.stock.stockId")
    List<ProductNumberVo> findAllProductStockNumber();

    /**
     * 修改特定仓库中特定产品的数量
     * @param productId 产品编号
     * @param stockId 仓库编号
     * @param number 数量
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_product_stock SET prod_number = ?3 WHERE product_id = ?1 AND stock_id = ?2", nativeQuery = true)
    int updateNumber(String productId, String stockId, Long number);
}
