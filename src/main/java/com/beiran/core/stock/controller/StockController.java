package com.beiran.core.stock.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.stock.dto.StockDto;
import com.beiran.core.stock.entity.MaterialStock;
import com.beiran.core.stock.entity.ProductStock;
import com.beiran.core.stock.entity.Stock;
import com.beiran.core.stock.service.StockService;
import com.beiran.core.stock.vo.MaterialNumberVo;
import com.beiran.core.stock.vo.ProductNumberVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 库存接口<br>
 * 仓库创建权限: stock:add<br>
 * 仓库修改权限: stock:edit<br>
 * 仓库删除权限: stock:del<br>
 * 仓库查询权限: stock:view<br>
 * 仓库导出权限: stock:export<br>
 *
 * 物料-仓库查询权限: stock:mater:view<br>
 * 产品-仓库查询权限: stock:prod:view<br>
 *
 * 暂时不考虑角色，只使用权限进行判断
 */
@RestController
@RequestMapping("/api/v1/stocks")
@Api(tags = "库存管理")
public class StockController {

    @Autowired
    private StockService stockService;

    /* ------------------------------- 仓库相关 -------------------------------- */

    /**
     * 创建仓库
     * @param stock
     * @return
     */
    @PostMapping
    @LogRecord("创建仓库")
    @PreAuthorize("@erp.check('stock:add')")
    @ApiOperation("创建仓库")
    public ResponseModel saveStock(@RequestBody @Valid Stock stock) {
        return ResponseModel.ok(stockService.save(stock));
    }

    /**
     * 修改仓库
     * @param stock
     * @return
     */
    @PutMapping
    @LogRecord("修改仓库")
    @PreAuthorize("@erp.check('stock:edit')")
    @ApiOperation("修改仓库")
    public ResponseModel updateStock(@RequestBody @Valid Stock stock) {
        return ResponseModel.ok(stockService.update(stock));
    }

    /**
     * 修改仓库管理员
     * @param userId 用户编号
     * @param stockId 仓库编号
     * @return
     */
    @PutMapping("/edit_manager")
    @LogRecord("修改仓库管理员")
    @PreAuthorize("@erp.check('stock:edit')")
    @ApiOperation("修改仓库管理员")
    public ResponseModel updateStockManager(@RequestParam("userId") String userId,
                                            @RequestParam("stockId") String stockId) {
        return ResponseModel.ok(stockService.updateStockManager(userId, stockId));
    }

    /**
     * 删除仓库
     * @param stockIds
     * @return
     */
    @DeleteMapping
    @LogRecord("删除仓库")
    @PreAuthorize("@erp.check('stock:del')")
    @ApiOperation("删除仓库")
    public ResponseModel deleteStock(@RequestBody List<String> stockIds) {
        if (Objects.equals(stockIds, null) || stockIds.isEmpty()) {
            return ResponseModel.error("需要删除的仓库不能为空");
        }
        List<Stock> stocks = stockIds.stream().map(stockId -> {
            Stock stock = new Stock();
            stock.setStockId(stockId);
            return stock;
        }).collect(Collectors.toList());
        stockService.deleteAll(stocks);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 查询所有仓库
     * @param pageable 分页参数
     * @return
     */
    @GetMapping
    @LogRecord("查询所有仓库")
    @PreAuthorize("@erp.check('stock:view')")
    @ApiOperation("查询所有仓库")
    public ResponseModel getStocks(@PageableDefault Pageable pageable) {
        return ResponseModel.ok(stockService.findAll(pageable));
    }

    /**
     * 通过仓库名查询仓库信息
     * @param stockName 仓库名
     * @return
     */
    @GetMapping("/name")
    @LogRecord("通过仓库名查询仓库信息")
    @PreAuthorize("@erp.check('stock:view')")
    @ApiOperation("通过仓库名查询仓库信息")
    public ResponseModel getStockByName(@RequestParam("stockName") String stockName) {
        StockDto stock = stockService.getStockByName(stockName);
        if (Objects.equals(stock, null)) {
            return ResponseModel.error("仓库不存在");
        }
        return ResponseModel.ok(stock);
    }

    /**
     * 通过仓库名模糊查询仓库信息
     * @param stockName 仓库名
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/search")
    @LogRecord("通过仓库名模糊查询仓库信息")
    @PreAuthorize("@erp.check('stock:view')")
    @ApiOperation("通过仓库名模糊查询仓库信息")
    public ResponseModel getStocksByName(@RequestParam("stockName") String stockName,
                                         @PageableDefault Pageable pageable) {
        return ResponseModel.ok(stockService.getStocksByName(stockName, pageable));
    }

    /**
     * 通过仓库管理员用户名查询仓库信息
     * @param userName 用户名
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/username")
    @LogRecord("通过用户名查询仓库信息")
    @PreAuthorize("@erp.check('stock:view')")
    @ApiOperation("通过用户名查询仓库信息")
    public ResponseModel getStocksByUserName(@RequestParam("userName") String userName,
                                             @PageableDefault Pageable pageable) {
        return ResponseModel.ok(stockService.getStocksByUserName(userName, pageable));
    }

    /**
     * 导出仓库信息
     * @param pageable 分页参数
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出仓库信息")
    @PreAuthorize("@erp.check('stock:export')")
    @ApiOperation("导出仓库信息")
    public void export(@PageableDefault Pageable pageable,
                       HttpServletResponse response) throws Exception {
        File file = stockService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /* ------------------------------- 物料-仓库相关 ---------------------------- */

    /**
     * 存储物料
     * @param materialNumberVo
     * @return
     */
    @PostMapping("/mater")
    @LogRecord("存储物料")
    @PreAuthorize("@erp.check('stock:add')")
    @ApiOperation("存储物料")
    public ResponseModel saveMaterialRecord(@RequestBody @Valid MaterialNumberVo materialNumberVo) {
        return ResponseModel.ok(stockService.saveMaterialRecord(materialNumberVo));
    }

    /**
     * 修改存储的物料
     * @param materialStock
     * @return
     */
    @PutMapping("/mater")
    @LogRecord("修改存储的物料")
    @PreAuthorize("@erp.check('stock:edit')")
    @ApiOperation("修改存储的物料")
    public ResponseModel updateMaterialRecord(@RequestBody @Valid MaterialStock materialStock) {
        return ResponseModel.ok(stockService.updateMaterialRecord(materialStock));
    }

    /**
     * 修改存储的物料数量
     * @param materialId 物料编号
     * @param stockId 仓库编号
     * @param number 数量
     * @return
     */
    @PutMapping("/mater/edit_number")
    @LogRecord("修改存储的物料数量")
    @PreAuthorize("@erp.check('stock:edit')")
    @ApiOperation("修改存储的物料数量")
    public ResponseModel updateMaterialNumber(@RequestParam("materialId") String materialId,
                                              @RequestParam("stockId") String stockId,
                                              @RequestParam("number") Long number) {
        boolean result = stockService.updateMaterialNumber(materialId, stockId, number);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 删除存储的物料
     * @param materialStockIds
     * @return
     */
    @DeleteMapping("/mater")
    @LogRecord("删除存储的物料")
    @PreAuthorize("@erp.check('stock:del')")
    @ApiOperation("删除存储的物料")
    public ResponseModel deleteMaterialRecord(@RequestBody List<String> materialStockIds) {
        if (Objects.equals(materialStockIds, null) || materialStockIds.isEmpty()) {
            return ResponseModel.error("需要删除的物料存储信息不能为空");
        }
        List<MaterialStock> materialStocks = materialStockIds.stream().map(materialStockId -> {
            MaterialStock materialStock = new MaterialStock();
            materialStock.setMaterStockId(materialStockId);
            return materialStock;
        }).collect(Collectors.toList());
        stockService.deleteMaterialRecord(materialStocks);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 查询物料数量
     * @param materialId
     * @return
     */
    @GetMapping("/mater")
    @LogRecord("查询物料数量")
    @PreAuthorize("@erp.check('stock:mater:view')")
    @ApiOperation("查询物料数量")
    public ResponseModel getMaterialNumber(@RequestParam("materialId") String materialId) {
        return ResponseModel.ok(stockService.getMaterialNumber(materialId));
    }

    /**
     * 查询特定仓库的物料数量
     * @param materialId
     * @param stockId
     * @return
     */
    @GetMapping("/mater/spec")
    @LogRecord("查询特定仓库的物料数量")
    @PreAuthorize("@erp.check('stock:mater:view')")
    @ApiOperation("查询特定仓库的物料数量")
    public ResponseModel getMaterialStockNumber(@RequestParam("materialId") String materialId,
                                                @RequestParam("stockId") String stockId) {
        return ResponseModel.ok(stockService.getMaterialStockNumber(materialId, stockId));
    }

    /* ------------------------------- 产品-仓库相关 ---------------------------- */

    /**
     * 存储产品
     * @param productNumberVo
     * @return
     */
    @PostMapping("/prod")
    @LogRecord("存储产品")
    @PreAuthorize("@erp.check('stock:add')")
    @ApiOperation("存储产品")
    public ResponseModel saveProductRecord(@RequestBody @Valid ProductNumberVo productNumberVo) {
        return ResponseModel.ok(stockService.saveProductRecord(productNumberVo));
    }

    /**
     * 修改存储的产品
     * @param productStock
     * @return
     */
    @PutMapping("/prod")
    @LogRecord("修改存储的产品")
    @PreAuthorize("@erp.check('stock:edit')")
    @ApiOperation("修改存储的产品")
    public ResponseModel updateProductRecord(@RequestBody @Valid ProductStock productStock) {
        return ResponseModel.ok(stockService.updateProductRecord(productStock));
    }

    /**
     * 修改存储的产品数量
     * @param productId 产品编号
     * @param stockId 仓库编号
     * @param number 数量
     * @return
     */
    @PutMapping("/prod/edit_number")
    @LogRecord("修改存储的产品数量")
    @PreAuthorize("@erp.check('stock:edit')")
    @ApiOperation("修改存储的产品数量")
    public ResponseModel updateProductNumber(@RequestParam("productId") String productId,
                                             @RequestParam("stockId") String stockId,
                                             @RequestParam("number") Long number) {
        return ResponseModel.ok(stockService.updateProductNumber(productId, stockId, number));
    }

    /**
     * 删除存储的产品
     * @param productStockIds
     * @return
     */
    @DeleteMapping("/prod")
    @LogRecord("删除存储的产品")
    @PreAuthorize("@erp.check('stock:del')")
    @ApiOperation("删除存储的产品")
    public ResponseModel deleteProductRecord(@RequestBody List<String> productStockIds) {
        if (Objects.equals(productStockIds, null) || productStockIds.isEmpty()) {
            return ResponseModel.error("需要删除的产品存储信息不能为空");
        }
        List<ProductStock> productStocks = productStockIds.stream().map(productStockId -> {
            ProductStock productStock = new ProductStock();
            productStock.setProdStockId(productStockId);
            return productStock;
        }).collect(Collectors.toList());
        stockService.deleteProductRecord(productStocks);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 查询产品数量
     * @param productId
     * @return
     */
    @GetMapping("/prod")
    @LogRecord("查询产品数量")
    @PreAuthorize("@erp.check('stock:prod:view')")
    @ApiOperation("查询产品数量")
    public ResponseModel getProductNumber(@RequestParam("productId") String productId) {
        return ResponseModel.ok(stockService.getProductNumber(productId));
    }

    /**
     * 查询特定仓库中产品数量
     * @param productId
     * @param stockId
     * @return
     */
    @GetMapping("/prod/spec")
    @LogRecord("查询特定仓库中产品数量")
    @PreAuthorize("@erp.check('stock:prod:view')")
    @ApiOperation("查询特定仓库中产品数量")
    public ResponseModel getProductStockNumber(@RequestParam("productId") String productId,
                                               @RequestParam("stockId") String stockId) {
        return ResponseModel.ok(stockService.getProductStockNumber(productId, stockId));
    }
}
