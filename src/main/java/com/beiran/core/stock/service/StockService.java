package com.beiran.core.stock.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.stock.dto.StockDto;
import com.beiran.core.stock.entity.MaterialStock;
import com.beiran.core.stock.entity.ProductStock;
import com.beiran.core.stock.entity.Stock;
import com.beiran.core.stock.vo.MaterialNumberVo;
import com.beiran.core.stock.vo.ProductNumberVo;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;

/**
 * StockService 接口
 */

public interface StockService extends GenericService<Stock, String> {

    /* ------------------------------- 仓库相关 -------------------------------- */

    /**
     * 根据仓库名查询仓库信息
     * @param stockName 仓库名
     * @return StockDto
     */
    StockDto getStockByName(String stockName);

    /**
     * 根据仓库名模糊查询仓库信息
     * @param stockName 仓库名
     * @param pageable 分页参数
     * @return List<StockDto>
     */
    List<StockDto> getStocksByName(String stockName, Pageable pageable);

    /**
     * 根据用户名查询仓库
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<StockDto>
     */
    List<StockDto> getStocksByUserName(String userName, Pageable pageable);

    /**
     * 更换仓库管理员
     * @param userId 用户编号
     * @param stockId 仓库编号
     * @return 是否更换成功
     */
    Boolean updateStockManager(String userId, String stockId);

    /**
     * 导出仓库信息
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);

    /* ------------------------------- 物料-仓库相关 ---------------------------- */

    /**
     * 查找仓库中特定物料的数量
     * @param materialId 物料编号
     * @return MaterialNumberVo
     */
    MaterialNumberVo getMaterialNumber(String materialId);

    /**
     * 查找特定仓库中特定物料的数量
     * @param materialId 物料编号
     * @param stockId 仓库编号
     * @return MaterialNumberVo
     */
    MaterialNumberVo getMaterialStockNumber(String materialId, String stockId);

    /**
     * 修改特定仓库中特定物料的数量
     * @param materialId 物料编号
     * @param stockId 仓库编号
     * @param number 数量
     * @return 是否修改成功
     */
    Boolean updateMaterialNumber(String materialId, String stockId, Long number);

    /**
     * 向特定仓库中存储一定数量的物料
     * @param materialNumberVo 物料与仓库的数据
     * @return MaterialStock
     */
    MaterialStock saveMaterialRecord(MaterialNumberVo materialNumberVo);

    /**
     * 更新物料-仓库中间表记录
     * @param materialStock 物料-仓库中间表
     * @return MaterialStock
     */
    MaterialStock updateMaterialRecord(MaterialStock materialStock);

    /**
     * 删除一些物料-仓库记录
     * @param materialStocks 物料-仓库记录集合
     */
    void deleteMaterialRecord(List<MaterialStock> materialStocks);

    /* ------------------------------- 产品-仓库相关 ---------------------------- */

    /**
     * 查找仓库中特定产品的数量
     * @param productId 产品数量
     * @return ProductNumberVo
     */
    ProductNumberVo getProductNumber(String productId);

    /**
     * 查找特定仓库中特定产品的数量
     * @param productId 产品编号
     * @param stockId 仓库编号
     * @return ProductNumberVo
     */
    ProductNumberVo getProductStockNumber(String productId, String stockId);

    /**
     * 修改特定仓库中特定产品的数量
     * @param productId 产品编号
     * @param stockId 仓库编号
     * @param number 数量
     * @return 是否修改成功
     */
    Boolean updateProductNumber(String productId, String stockId, Long number);

    /**
     * 向特定仓库中存储一定数量的产品
     * @param productNumberVo 产品与仓库的数据
     * @return ProductStock
     */
    ProductStock saveProductRecord(ProductNumberVo productNumberVo);

    /**
     * 更新产品-仓库中间表记录
     * @param productStock 产品-仓库中间表
     * @return ProductStock
     */
    ProductStock updateProductRecord(ProductStock productStock);

    /**
     * 删除一些产品-仓库中间表记录
     * @param productStocks 产品-仓库记录集合
     */
    void deleteProductRecord(List<ProductStock> productStocks);
}
