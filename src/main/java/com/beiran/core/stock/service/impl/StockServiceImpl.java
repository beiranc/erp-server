package com.beiran.core.stock.service.impl;

import com.beiran.common.exception.EntityExistException;
import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.FileUtils;
import com.beiran.common.utils.transfer.StockTransferUtils;
import com.beiran.core.material.entity.Material;
import com.beiran.core.product.entity.Product;
import com.beiran.core.stock.dto.StockDto;
import com.beiran.core.stock.entity.MaterialStock;
import com.beiran.core.stock.entity.ProductStock;
import com.beiran.core.stock.entity.Stock;
import com.beiran.core.stock.repository.MaterialStockRepository;
import com.beiran.core.stock.repository.ProductStockRepository;
import com.beiran.core.stock.repository.StockRepository;
import com.beiran.core.stock.service.StockService;
import com.beiran.core.stock.vo.MaterialNumberVo;
import com.beiran.core.stock.vo.ProductNumberVo;
import com.beiran.core.system.entity.User;
import com.beiran.core.system.repository.UserRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * StockService 接口实现类
 */

@Service("stockService")
public class StockServiceImpl implements StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MaterialStockRepository materialStockRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private UserRepository userRepository;

    /* ------------------------------- 仓库相关 -------------------------------- */

    /**
     * 根据仓库名查询仓库信息
     *
     * @param stockName 仓库名
     * @return StockDto
     */
    @Override
    public StockDto getStockByName(String stockName) {
        if (!StringUtils.hasText(stockName)) {
            throw new ParameterException("仓库名不能为空");
        }
        // 留给调用方判断
        Stock stock = stockRepository.findByStockName(stockName).orElse(null);
        if (!Objects.equals(stock, null)) {
            return StockTransferUtils.stockToDto(stock);
        }
        return null;
    }

    /**
     * 根据仓库名模糊查询仓库信息
     *
     * @param stockName 仓库名
     * @param pageable  分页参数
     * @return List<StockDto>
     */
    @Override
    public List<StockDto> getStocksByName(String stockName, Pageable pageable) {
        if (!StringUtils.hasText(stockName)) {
            throw new ParameterException("仓库名不能为空");
        }
        List<Stock> stocks = stockRepository.findByStockNameContaining(stockName, pageable);
        List<StockDto> stockDtos =
                stocks.stream()
                        .map(stock -> StockTransferUtils.stockToDto(stock))
                        .collect(Collectors.toList());
        return stockDtos;
    }

    /**
     * 根据用户名查询仓库
     *
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<StockDto>
     */
    @Override
    public List<StockDto> getStocksByUserName(String userName, Pageable pageable) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("仓库管理员用户名不能为空");
        }
        List<Stock> stocks = stockRepository.findByStockManager_UserName(userName, pageable);
        List<StockDto> stockDtos =
                stocks.stream()
                        .map(stock -> StockTransferUtils.stockToDto(stock))
                        .collect(Collectors.toList());
        return stockDtos;
    }

    /**
     * 更换仓库管理员
     *
     * @param userId  用户编号
     * @param stockId 仓库编号
     * @return 是否更换成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStockManager(String userId, String stockId) {
        // 首先判空，然后查询 userId 与 stockId 对应的 User 和 Stock 是否存在
        // 存在就改，不存在抛异常
        if (!StringUtils.hasText(userId)) {
            throw new ParameterException("用户编号不能为空");
        }
        if (!StringUtils.hasText(stockId)) {
            throw new ParameterException("仓库编号不能为空");
        }
        User user = userRepository.findById(userId).orElse(null);
        if (Objects.equals(user, null)) {
            throw new EntityNotExistException("需要更换的用户不存在");
        }
        Stock stock = stockRepository.findById(stockId).orElse(null);
        if (Objects.equals(stock, null)) {
            throw new EntityNotExistException("需要更换仓库管理员的仓库不存在");
        }
        return stockRepository.updateStockManager(userId, stockId) > 0;
    }

    /**
     * 导出仓库信息
     *
     * @param pageable 分页参数
     * @return File
     */
    @Override
    public File createExcelFile(Pageable pageable) {
        // 仓库数据
        Page<Stock> stockPage = findAll(pageable);
        List<Stock> stocks = stockPage.getContent();
        if (Objects.equals(stocks, null)) {
            stocks = new ArrayList<>();
        }
        // 物料数据
        List<MaterialNumberVo> materialNumber = materialStockRepository.findAllMaterialStockNumber();
        // 产品数据
        List<ProductNumberVo> productNumber = productStockRepository.findAllProductStockNumber();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("仓库编号");
        rowInfo.createCell(++columnIndex).setCellValue("仓库名称");
        rowInfo.createCell(++columnIndex).setCellValue("仓库位置");
        rowInfo.createCell(++columnIndex).setCellValue("仓库管理员");
        rowInfo.createCell(++columnIndex).setCellValue("仓库存放物料总数");
        rowInfo.createCell(++columnIndex).setCellValue("仓库存放产品总数");

        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            // 仓库存放的物料总数数据
            // FIXME 可能会出问题
            Set<MaterialNumberVo> materialNumberVos = materialNumber.stream().filter(materialNumberVo -> Objects.equals(materialNumberVo.getStockId(), stock.getStockId())).collect(Collectors.toSet());
            MaterialNumberVo materialNumberVo = null;
            if (!materialNumberVos.isEmpty()) {
                materialNumberVo = materialNumberVos.stream().findFirst().orElse(null);
            }
            // 仓库存放的产品总数数据
            // FIXME 可能会出问题
            Set<ProductNumberVo> productNumberVos = productNumber.stream().filter(productNumberVo -> Objects.equals(productNumberVo.getStockId(), stock.getStockId())).collect(Collectors.toSet());
            ProductNumberVo productNumberVo = null;
            if (!productNumberVos.isEmpty()) {
                productNumberVo = productNumberVos.stream().findFirst().orElse(null);
            }
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(stock.getStockId());
            row.getCell(++columnIndex).setCellValue(stock.getStockName());
            row.getCell(++columnIndex).setCellValue(stock.getStockPosition());
            row.getCell(++columnIndex).setCellValue(Objects.equals(stock.getStockManager(), null) ? "-" : stock.getStockManager().getUserName());
            row.getCell(++columnIndex).setCellValue(Objects.equals(materialNumberVo, null) ? "-" : materialNumberVo.getMaterialNumber().toString());
            row.getCell(++columnIndex).setCellValue(Objects.equals(productNumberVo, null) ? "-" : productNumberVo.getProductNumber().toString());
        }
        return FileUtils.createExcelFile(workbook, "erp_stocks");
    }

    /**
     * 保存操作
     *
     * @param entity 需要保存的实体
     * @return T 返回保存的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Stock save(Stock entity) {
        // 由于仓库名是唯一的，所以需要查是否存在
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的仓库不能为空");
        }
        StockDto stockDto = getStockByName(entity.getStockName());
        if (!Objects.equals(stockDto, null)) {
            throw new EntityExistException("仓库已存在");
        }
        return stockRepository.save(entity);
    }

    /**
     * 根据给定一批实体进行批量删除
     *
     * @param entities
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Stock> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的仓库不能为空");
        }
        stockRepository.deleteAll(entities);
    }

    /**
     * 更新操作
     *
     * @param entity 需要更新的实体
     * @return 更新后的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Stock update(Stock entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要更新的仓库不能为空");
        }
        return stockRepository.saveAndFlush(entity);
    }

    /**
     * 根据给定 ID 查询一个实体
     *
     * @param id 需要查询的实体 ID
     * @return 查询出来的实体
     */
    @Override
    public Stock findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("仓库编号不能为空");
        }
        return stockRepository.findById(id).orElseThrow(() -> new EntityNotExistException("仓库不存在"));
    }

    /**
     * 分页查询
     *
     * @param pageable 常用 Pageable 接口的实现类 PageRequest
     * @return 返回一个 Page 对象
     */
    @Override
    public Page<Stock> findAll(Pageable pageable) {
        return stockRepository.findAll(pageable);
    }

    /* ------------------------------- 物料-仓库相关 ---------------------------- */

    /**
     * 查找仓库中特定物料的数量
     *
     * @param materialId 物料编号
     * @return MaterialNumberVo
     */
    @Override
    public MaterialNumberVo getMaterialNumber(String materialId) {
        if (!StringUtils.hasText(materialId)) {
            throw new ParameterException("物料编号不能为空");
        }
        return materialStockRepository.findMaterialNumber(materialId);
    }

    /**
     * 查找特定仓库中特定物料的数量
     *
     * @param materialId 物料编号
     * @param stockId    仓库编号
     * @return MaterialNumberVo
     */
    @Override
    public MaterialNumberVo getMaterialStockNumber(String materialId, String stockId) {
        if (!StringUtils.hasText(materialId)) {
            throw new ParameterException("物料编号不能为空");
        }
        if (!StringUtils.hasText(stockId)) {
            throw new ParameterException("仓库编号不能为空");
        }
        return materialStockRepository.findMaterialStockNumber(materialId, stockId);
    }

    /**
     * 修改特定仓库中特定物料的数量
     *
     * @param materialId 物料编号
     * @param stockId    仓库编号
     * @param number     数量
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateMaterialNumber(String materialId, String stockId, Long number) {
        // 不能修改为负数
        if (!StringUtils.hasText(materialId)) {
            throw new ParameterException("物料编号不能为空");
        }
        if (!StringUtils.hasText(stockId)) {
            throw new ParameterException("仓库编号不能为空");
        }
        if (Objects.equals(number, number) || number < 0) {
            throw new ParameterException("物料数量错误");
        }
        return materialStockRepository.updateNumber(materialId, stockId, number) > 0;
    }

    /**
     * 向特定仓库中存储一定数量的物料
     *
     * @param materialNumberVo 物料与仓库的数据
     * @return MaterialStock
     */
    @Override
//    @Transactional(rollbackFor = Exception.class)
    public MaterialStock saveMaterialRecord(MaterialNumberVo materialNumberVo) {
        // 注意如果这个仓库中已经存放过这种物料了，那就在这个基础上加
        // 也就是在保存前首先要查一下存不存在
        if (Objects.equals(materialNumberVo, null)) {
            throw new ParameterException("需要存储的物料信息不能为空");
        }
        if (Objects.equals(materialNumberVo.getMaterialNumber(), null) || materialNumberVo.getMaterialNumber() < 0) {
            throw new ParameterException("物料数量错误");
        }
        MaterialStock materialStock = new MaterialStock();
        materialStock.setMaterNumber(materialNumberVo.getMaterialNumber());
        Material material = new Material();
        material.setMaterialId(materialNumberVo.getMaterialId());
        materialStock.setMaterial(material);
        Stock stock = new Stock();
        stock.setStockId(materialNumberVo.getStockId());
        materialStock.setStock(stock);
        // 返回的 MaterialStock
        MaterialStock returnMaterialStock = new MaterialStock();
        MaterialNumberVo materialStockNumber = materialStockRepository.findMaterialStockNumber(materialNumberVo.getMaterialId(), materialNumberVo.getStockId());
        if (!Objects.equals(materialStockNumber, null)) {
            // 不为空说明库存中已经有这个物料的存储记录了，则在此基础上加库存量就行了
            int result = materialStockRepository.updateNumber(materialNumberVo.getMaterialId(), materialNumberVo.getStockId(), materialStockNumber.getMaterialNumber() + materialNumberVo.getMaterialNumber());
            if (result > 0) {
                materialStock.setMaterNumber(materialNumberVo.getMaterialNumber() + materialStockNumber.getMaterialNumber());
            } else {
                materialStock.setMaterNumber(materialStockNumber.getMaterialNumber());
            }
            // FIXME 可能会出现没有数据的问题
            BeanUtils.copyProperties(materialStock, returnMaterialStock);
        } else {
            // 为空则说明没有记录，则直接保存即可
            returnMaterialStock = materialStockRepository.save(materialStock);
        }
        // 调用方需要判断返回结果是否为 Null
        return returnMaterialStock;
    }

    /**
     * 更新物料-仓库中间表记录
     *
     * @param materialStock 物料-仓库中间表
     * @return MaterialStock
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterialStock updateMaterialRecord(MaterialStock materialStock) {
        // 无须先查询是否存在，直接修改即可
        if (Objects.equals(materialStock, null)) {
            throw new ParameterException("需要修改的物料存储记录不能为空");
        }
        return materialStockRepository.saveAndFlush(materialStock);
    }

    /**
     * 删除一些物料-仓库记录
     *
     * @param materialStocks 物料-仓库记录集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterialRecord(List<MaterialStock> materialStocks) {
        if (Objects.equals(materialStocks, null) || materialStocks.isEmpty()) {
            throw new ParameterException("需要删除的物料存储记录不能为空");
        }
        materialStockRepository.deleteAll(materialStocks);
    }

    /* ------------------------------- 产品-仓库相关 ---------------------------- */

    /**
     * 查找仓库中特定产品的数量
     *
     * @param productId 产品数量
     * @return ProductNumberVo
     */
    @Override
    public ProductNumberVo getProductNumber(String productId) {
        if (!StringUtils.hasText(productId)) {
            throw new ParameterException("产品编号不能为空");
        }
        return productStockRepository.findProductNumber(productId);
    }

    /**
     * 查找特定仓库中特定产品的数量
     *
     * @param productId 产品编号
     * @param stockId   仓库编号
     * @return ProductNumberVo
     */
    @Override
    public ProductNumberVo getProductStockNumber(String productId, String stockId) {
        if (!StringUtils.hasText(productId)) {
            throw new ParameterException("产品编号不能为空");
        }
        if (!StringUtils.hasText(stockId)) {
            throw new ParameterException("仓库编号不能为空");
        }
        return productStockRepository.findProductStockNumber(productId, stockId);
    }

    /**
     * 修改特定仓库中特定产品的数量
     *
     * @param productId 产品编号
     * @param stockId   仓库编号
     * @param number    数量
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductNumber(String productId, String stockId, Long number) {
        // 修改的数量不能为负数
        if (!StringUtils.hasText(productId)) {
            throw new ParameterException("产品编号不能为空");
        }
        if (!StringUtils.hasText(stockId)) {
            throw new ParameterException("仓库编号不能为空");
        }
        if (Objects.equals(number, number) || number < 0) {
            throw new ParameterException("产品数量错误");
        }
        return productStockRepository.updateNumber(productId, stockId, number) > 0;
    }

    /**
     * 向特定仓库中存储一定数量的产品
     *
     * @param productNumberVo 产品与仓库的数据
     * @return ProductStock
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductStock saveProductRecord(ProductNumberVo productNumberVo) {
        // 存储前需要先查是否存在，如存在则加数量，不存在则创一条新的记录
        if (Objects.equals(productNumberVo, null)) {
            throw new ParameterException("产品存储信息不能为空");
        }
        ProductStock productStock = new ProductStock();
        productStock.setProdNumber(productNumberVo.getProductNumber());
        Product product = new Product();
        product.setProductId(productNumberVo.getProductId());
        productStock.setProduct(product);
        Stock stock = new Stock();
        stock.setStockId(productNumberVo.getStockId());
        productStock.setStock(stock);
        // 返回的产品-仓库信息
        ProductStock returnProductStock = new ProductStock();
        ProductNumberVo productStockNumber = productStockRepository.findProductStockNumber(productNumberVo.getProductId(), productNumberVo.getStockId());
        if (!Objects.equals(productStockNumber, null)) {
            // 不为空则表示已经存在该记录，则直接在原有记录上添加数量即可
            int result = productStockRepository.updateNumber(productNumberVo.getProductId(), productNumberVo.getStockId(), productStockNumber.getProductNumber() + productNumberVo.getProductNumber());
            if (result > 0) {
                productStock.setProdNumber(productStockNumber.getProductNumber() + productNumberVo.getProductNumber());
            } else {
                productStock.setProdNumber(productStockNumber.getProductNumber());
            }
            // FIXME 复制数据可能会出现没值的情况
            BeanUtils.copyProperties(productStock, returnProductStock);
        } else {
            // 为空说明没有记录，直接保存即可
            returnProductStock = productStockRepository.save(productStock);
        }
        return returnProductStock;
    }

    /**
     * 更新产品-仓库中间表记录
     *
     * @param productStock 产品-仓库中间表
     * @return ProductStock
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductStock updateProductRecord(ProductStock productStock) {
        // 更新前无须查询是否存在
        if (Objects.equals(productStock, null)) {
            throw new ParameterException("需要修改的产品存储信息不能为空");
        }
        return productStockRepository.saveAndFlush(productStock);
    }

    /**
     * 删除一些产品-仓库中间表记录
     *
     * @param productStocks 产品-仓库记录集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductRecord(List<ProductStock> productStocks) {
        if (Objects.equals(productStocks, null) || productStocks.isEmpty()) {
            throw new ParameterException("需要删除的产品存储信息不能为空");
        }
        productStockRepository.deleteAll(productStocks);
    }
}
