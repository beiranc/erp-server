package com.beiran.core.produce.service.impl;

import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.DateTimeUtils;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.produce.dto.ProductionDetailDto;
import com.beiran.core.produce.dto.ProductionDto;
import com.beiran.core.produce.entity.ProductionDemand;
import com.beiran.core.produce.entity.ProductionDemandDetail;
import com.beiran.core.produce.repository.ProductionDemandDetailRepository;
import com.beiran.core.produce.repository.ProductionDemandRepository;
import com.beiran.core.produce.service.ProductionService;
import com.beiran.core.produce.vo.ProductionDetailVo;
import com.beiran.core.produce.vo.ProductionVo;
import com.beiran.core.product.entity.Product;
import com.beiran.core.product.service.ProductService;
import com.beiran.core.stock.dto.StockSmallDto;
import com.beiran.core.stock.entity.Stock;
import com.beiran.core.stock.service.StockService;
import com.beiran.core.stock.vo.ProductNumberVo;
import com.beiran.core.system.dto.UserSmallDto;
import com.beiran.core.system.entity.User;
import com.beiran.security.utils.SecurityUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 生产需求计划 Service 实现类
 */

@Service("productionService")
public class ProductionServiceImpl implements ProductionService {

    @Autowired
    private ProductionDemandRepository productionDemandRepository;

    @Autowired
    private ProductionDemandDetailRepository productionDemandDetailRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private StockService stockService;

    private ProductionDto transferProduction(ProductionDemand productionDemand) {
        ProductionDto productionDto = new ProductionDto();

        if (!Objects.equals(productionDemand, null)) {
            BeanUtils.copyProperties(productionDemand, productionDto);
        }

        UserSmallDto applicant = new UserSmallDto();
        if (!Objects.equals(productionDemand.getProductionApplicant(), null)) {
            applicant.setUserId(productionDemand.getProductionApplicant().getUserId());
            applicant.setUserName(productionDemand.getProductionApplicant().getUserName());
        }

        UserSmallDto operator = new UserSmallDto();
        if (!Objects.equals(productionDemand.getLastModifiedOperator(), null)) {
            operator.setUserId(productionDemand.getLastModifiedOperator().getUserId());
            operator.setUserName(productionDemand.getLastModifiedOperator().getUserName());
        }

        productionDto.setApplicant(applicant);
        productionDto.setOperator(operator);

        return productionDto;
    }

    private ProductionDetailDto transferDetail(ProductionDemandDetail productionDemandDetail) {
        ProductionDetailDto productionDetailDto = new ProductionDetailDto();

        if (!Objects.equals(productionDemandDetail, null)) {
            BeanUtils.copyProperties(productionDemandDetail, productionDetailDto);
        }

        StockSmallDto stock = new StockSmallDto();

        if (!Objects.equals(productionDemandDetail.getProductionStock(), null)) {
            stock.setStockId(productionDemandDetail.getProductionStock().getStockId());
            stock.setStockName(productionDemandDetail.getProductionStock().getStockName());
        }

        productionDetailDto.setStock(stock);
        productionDetailDto.setNumber(productionDemandDetail.getProductionNumber().toString());
        return productionDetailDto;
    }

    private ProductionDemand transferProductionVo(ProductionVo productionVo) {
        ProductionDemand productionDemand = new ProductionDemand();

        if (!Objects.equals(productionVo, null)) {
            BeanUtils.copyProperties(productionVo, productionDemand);
        }

        User user = new User();

        if (!Objects.equals(productionVo.getApplicant(), null)) {
            user.setUserId(productionVo.getApplicant().getUserId());
            user.setUserName(productionVo.getApplicant().getUserName());
        }

        productionDemand.setProductionApplicant(user);

        return productionDemand;
    }

    private ProductionDemandDetail transferDetailVo(ProductionDetailVo productionDetailVo) {
        ProductionDemandDetail productionDemandDetail = new ProductionDemandDetail();

        if (!Objects.equals(productionDetailVo, null)) {
            BeanUtils.copyProperties(productionDetailVo, productionDemandDetail);
        }

        Stock stock = new Stock();
        if (!Objects.equals(productionDetailVo.getStock(), null)) {
            stock.setStockId(productionDetailVo.getStock().getStockId());
            stock.setStockName(productionDetailVo.getStock().getStockName());
        }
        productionDemandDetail.setProductionStock(stock);

        return productionDemandDetail;
    }

    /* ------------------------------------- 生产需求计划相关 ----------------------------------------- */

    /**
     * 创建生产需求计划，默认为 CREATED 状态
     *
     * @param productionVo 创建生产需求计划所需数据
     * @return ProductionDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductionDto createProductionDemand(ProductionVo productionVo) {
        // Step 1. 保存生产需求计划
        // Step 2. 保存生产需求计划详细项
        if (Objects.equals(productionVo, null)) {
            throw new ParameterException("需要保存的生产需求计划不能为空");
        }
        ProductionDemand productionDemand = transferProductionVo(productionVo);

        // FIXME 获取用户信息时可能会出错
        User lastModifiedOperator = new User();
        lastModifiedOperator.setUserId(SecurityUtil.getUserId());
        lastModifiedOperator.setUserName(SecurityUtil.getUserName());
        productionDemand.setLastModifiedTime(new Date());
        productionDemand.setLastModifiedOperator(lastModifiedOperator);

        ProductionDemand save = productionDemandRepository.save(productionDemand);

        if (Objects.equals(productionVo.getProductionDetails(), null) || productionVo.getProductionDetails().isEmpty()) {
            throw new ParameterException("生产需求计划子项不能为空");
        }
        List<ProductionDemandDetail> productionDemandDetails = productionVo.getProductionDetails().stream().map(this::transferDetailVo).collect(Collectors.toList());

        // 设置所属生产需求计划
        productionDemandDetails.forEach(productionDemandDetail -> {
            productionDemandDetail.setBelongDemand(save);
        });

        List<ProductionDemandDetail> saveAll = productionDemandDetailRepository.saveAll(productionDemandDetails);

        List<ProductionDetailDto> productionDetailDtos = saveAll.stream().map(this::transferDetail).collect(Collectors.toList());

        ProductionDto productionDto = transferProduction(save);

        productionDto.setProductionDetails(productionDetailDtos);

        return productionDto;
    }

    /**
     * 修改生产需求计划状态
     *
     * @param productionId    生产需求计划编号
     * @param productionState 生产需求计划状态
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateState(String productionId, ProductionDemand.ProductionDemandState productionState) {
        if (!StringUtils.hasText(productionId)) {
            throw new ParameterException("生产需求计划编号不能为空");
        }
        // 同时修改上一次操作者和上一次修改时间
        ProductionDemand productionDemand = findById(productionId);
        productionDemand.setLastModifiedTime(new Date());
        User operator = new User();
        operator.setUserId(SecurityUtil.getUserId());
        operator.setUserName(SecurityUtil.getUserName());
        productionDemand.setLastModifiedOperator(operator);
        productionDemand.setProductionState(productionState);
        ProductionDemand update = update(productionDemand);
        return !Objects.equals(productionDemand,null);
    }

    /**
     * 修改生产需求计划状态为 CONFIRMED
     *
     * @param productionId 生产需求计划编号
     * @return 是否修改成功
     */
    @Override
    public Boolean confirmProduction(String productionId) {
        ProductionDemand productionDemand = findById(productionId);
        // 若不是 CREATED 状态，则无法被修改为 CONFIRMED 状态
        if (!Objects.equals(productionDemand.getProductionState(), ProductionDemand.ProductionDemandState.CREATED)) {
            throw new ParameterException("无法被修改");
        }
        return updateState(productionId, ProductionDemand.ProductionDemandState.CONFIRMED);
    }

    /**
     * 修改生产需求计划状态为 VERIFYING
     *
     * @param productionId 生产需求计划编号
     * @return 是否修改成功
     */
    @Override
    public Boolean verifyProduction(String productionId) {
        ProductionDemand productionDemand = findById(productionId);
        // 若不是 CONFIRMED/REPRODUCED 状态，则无法被修改为 VERIFYING 状态
        if (Objects.equals(productionDemand.getProductionState(), ProductionDemand.ProductionDemandState.CONFIRMED) || Objects.equals(productionDemand.getProductionState(), ProductionDemand.ProductionDemandState.REPRODUCED)) {
            return updateState(productionId, ProductionDemand.ProductionDemandState.VERIFYING);
        } else {
            throw new ParameterException("无法被修改");
        }
    }

    /**
     * 修改生产需求计划状态为 IMPORTED
     *
     * @param productionId 生产需求计划编号
     * @return 是否修改成功
     */
    @Override
    public Boolean importProduction(String productionId) {
        // 已入库状态需要将产品存入仓库
        ProductionDemand productionDemand = findById(productionId);
        // 若不是 VERIFYING 状态，则无法被修改为 IMPORTED 状态
        if (!Objects.equals(productionDemand.getProductionState(), ProductionDemand.ProductionDemandState.VERIFYING)) {
            throw new ParameterException("无法被修改");
        }
        boolean result = updateState(productionId, ProductionDemand.ProductionDemandState.IMPORTED);
        List<ProductionDemandDetail> productionDemandDetails = productionDemandDetailRepository.findByBelongDemand_ProductionId(productionId, PageRequest.of(0, 200));
        productionDemandDetails.stream().forEach(productionDemandDetail -> {
            if (productionDemandDetail.getNewProduct()) {
                // 新产品则直接添加
                Product product = new Product();
                // FIXME BeanUtils 复制数据可能会出错
                BeanUtils.copyProperties(productionDemandDetail, product);
                Product save = productService.save(product);
                ProductNumberVo productNumberVo = new ProductNumberVo(save.getProductId(), productionDemandDetail.getProductionStock().getStockId(), productionDemandDetail.getProductionNumber());
                stockService.saveProductRecord(productNumberVo);
            } else {
                // 否则直接在原有库存中添加
                ProductNumberVo productNumberVo = new ProductNumberVo(productionDemandDetail.getProductId(), productionDemandDetail.getProductionStock().getStockId(), productionDemandDetail.getProductionNumber());
                stockService.saveProductRecord(productNumberVo);
            }
        });
        return result;
    }

    /**
     * 修改生产需求计划状态为 REPRODUCED
     *
     * @param productionId 生产需求计划编号
     * @return 是否修改成功
     */
    @Override
    public Boolean reproduceProduction(String productionId) {
        ProductionDemand productionDemand = findById(productionId);
        // 若不是 VERIFYING 状态，则无法被修改为 REPRODUCED 状态
        if (!Objects.equals(productionDemand.getProductionState(), ProductionDemand.ProductionDemandState.VERIFYING)) {
            throw new ParameterException("无法被修改");
        }
        return updateState(productionId, ProductionDemand.ProductionDemandState.REPRODUCED);
    }

    /**
     * 修改生产需求计划状态为 CLOSED
     *
     * @param productionId 生产需求计划编号
     * @return 是否修改成功
     */
    @Override
    public Boolean closeProduction(String productionId) {
        ProductionDemand productionDemand = findById(productionId);
        // 若不是 CREATED/IMPORTED 状态，则无法被修改为 CLOSED 状态
        if (Objects.equals(productionDemand.getProductionState(), ProductionDemand.ProductionDemandState.CREATED) || Objects.equals(productionDemand.getProductionState(), ProductionDemand.ProductionDemandState.IMPORTED)) {
            return updateState(productionId, ProductionDemand.ProductionDemandState.CLOSED);
        } else {
            throw new ParameterException("无法被修改");
        }
    }

    /**
     * 根据生产员的用户名查询
     *
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<ProductionDto>
     */
    @Override
    public List<ProductionDto> getProductionsByUserName(String userName, Pageable pageable) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("用户名不能为空");
        }
        List<ProductionDemand> productionDemands = productionDemandRepository.findByProductionApplicant_UserName(userName, pageable);
        List<ProductionDto> productionDtos =
                productionDemands.stream()
                        .map(this::transferProduction)
                        .collect(Collectors.toList());
        productionDtos.stream().forEach(productionDto -> {
            List<ProductionDemandDetail> productionDemandDetails = productionDemandDetailRepository.findByBelongDemand_ProductionId(productionDto.getProductionId(), PageRequest.of(0, 200));
            List<ProductionDetailDto> productionDetailDtos = productionDemandDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            productionDto.setProductionDetails(productionDetailDtos);
        });
        return productionDtos;
    }

    /**
     * 根据用户名与生产需求计划状态查询
     *
     * @param userName        用户名
     * @param productionState 生产需求计划状态
     * @param pageable        分页参数
     * @return List<ProductionDto>
     */
    @Override
    public List<ProductionDto> getProductionsByUserNameAndState(String userName, ProductionDemand.ProductionDemandState productionState, Pageable pageable) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("用户名不能为空");
        }
        if (Objects.equals(productionState, null)) {
            throw new ParameterException("生产需求计划状态不能为空");
        }
        List<ProductionDemand> productionDemands = productionDemandRepository.findByProductionApplicant_UserNameAndProductionState(userName, productionState, pageable);
        List<ProductionDto> productionDtos =
                productionDemands.stream()
                        .map(this::transferProduction)
                        .collect(Collectors.toList());
        productionDtos.stream().forEach(productionDto -> {
            List<ProductionDemandDetail> productionDemandDetails = productionDemandDetailRepository.findByBelongDemand_ProductionId(productionDto.getProductionId(), PageRequest.of(0, 200));
            List<ProductionDetailDto> productionDetailDtos = productionDemandDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            productionDto.setProductionDetails(productionDetailDtos);
        });
        return productionDtos;
    }

    /**
     * 根据生产需求计划状态查询
     *
     * @param productionState 生产需求计划状态
     * @param pageable        分页参数
     * @return List<ProductionDto>
     */
    @Override
    public List<ProductionDto> getProductionsByState(ProductionDemand.ProductionDemandState productionState, Pageable pageable) {
        if (Objects.equals(productionState, null)) {
            throw new ParameterException("生产需求计划状态不能为空");
        }
        List<ProductionDemand> productionDemands = productionDemandRepository.findByProductionState(productionState, pageable);
        List<ProductionDto> productionDtos =
                productionDemands.stream()
                        .map(this::transferProduction)
                        .collect(Collectors.toList());
        productionDtos.stream().forEach(productionDto -> {
            List<ProductionDemandDetail> productionDemandDetails = productionDemandDetailRepository.findByBelongDemand_ProductionId(productionDto.getProductionId(), PageRequest.of(0, 200));
            List<ProductionDetailDto> productionDetailDtos = productionDemandDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            productionDto.setProductionDetails(productionDetailDtos);
        });
        return productionDtos;
    }

    /**
     * 根据生产需求计划主题模糊查询
     *
     * @param productionSubject 生产需求计划主题
     * @param pageable          分页参数
     * @return List<ProductionDto>
     */
    @Override
    public List<ProductionDto> getProductionsBySubject(String productionSubject, Pageable pageable) {
        if (!StringUtils.hasText(productionSubject)) {
            throw new ParameterException("生产需求计划主题不能为空");
        }
        List<ProductionDemand> productionDemands = productionDemandRepository.findByProductionSubjectContaining(productionSubject, pageable);
        List<ProductionDto> productionDtos =
                productionDemands.stream()
                        .map(this::transferProduction)
                        .collect(Collectors.toList());
        productionDtos.stream().forEach(productionDto -> {
            List<ProductionDemandDetail> productionDemandDetails = productionDemandDetailRepository.findByBelongDemand_ProductionId(productionDto.getProductionId(), PageRequest.of(0, 200));
            List<ProductionDetailDto> productionDetailDtos = productionDemandDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            productionDto.setProductionDetails(productionDetailDtos);
        });
        return productionDtos;
    }

    /**
     * 根据生产需求计划创建时间查询
     *
     * @param leftTime  左区间
     * @param rightTime 右区间
     * @param pageable  分页参数
     * @return List<ProductionDto>
     */
    @Override
    public List<ProductionDto> getProductionsByCreateTime(Date leftTime, Date rightTime, Pageable pageable) {
        if (Objects.equals(leftTime, null) || Objects.equals(rightTime, null)) {
            throw new ParameterException("生产需求计划创建时间不能为空");
        }
        List<ProductionDemand> productionDemands = productionDemandRepository.findByProductionCreateTimeBetween(leftTime, rightTime, pageable);
        List<ProductionDto> productionDtos =
                productionDemands.stream()
                        .map(this::transferProduction)
                        .collect(Collectors.toList());
        productionDtos.stream().forEach(productionDto -> {
            List<ProductionDemandDetail> productionDemandDetails = productionDemandDetailRepository.findByBelongDemand_ProductionId(productionDto.getProductionId(), PageRequest.of(0, 200));
            List<ProductionDetailDto> productionDetailDtos = productionDemandDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            productionDto.setProductionDetails(productionDetailDtos);
        });
        return productionDtos;
    }

    /**
     * 根据生产需求计划上一次修改时间查询
     *
     * @param leftTime  左区间
     * @param rightTime 右区间
     * @param pageable  分页参数
     * @return List<ProductionDto>
     */
    @Override
    public List<ProductionDto> getProductionsByModifiedTime(Date leftTime, Date rightTime, Pageable pageable) {
        if (Objects.equals(leftTime, null) || Objects.equals(rightTime, null)) {
            throw new ParameterException("生产需求计划上一次修改时间不能为空");
        }
        List<ProductionDemand> productionDemands = productionDemandRepository.findByLastModifiedTimeBetween(leftTime, rightTime, pageable);
        List<ProductionDto> productionDtos =
                productionDemands.stream()
                        .map(this::transferProduction)
                        .collect(Collectors.toList());
        productionDtos.stream().forEach(productionDto -> {
            List<ProductionDemandDetail> productionDemandDetails = productionDemandDetailRepository.findByBelongDemand_ProductionId(productionDto.getProductionId(), PageRequest.of(0, 200));
            List<ProductionDetailDto> productionDetailDtos = productionDemandDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            productionDto.setProductionDetails(productionDetailDtos);
        });
        return productionDtos;
    }

    @Override
    public List<ProductionDto> getAllProductions(Pageable pageable) {
        Page<ProductionDemand> productionDemandPage = findAll(pageable);
        List<ProductionDemand> productionDemands = productionDemandPage.getContent();
        List<ProductionDto> productionDtos =
                productionDemands.stream()
                        .map(this::transferProduction)
                        .collect(Collectors.toList());
        productionDtos.stream().forEach(productionDto -> {
            List<ProductionDemandDetail> productionDemandDetails = productionDemandDetailRepository.findByBelongDemand_ProductionId(productionDto.getProductionId(), PageRequest.of(0, 200));
            List<ProductionDetailDto> productionDetailDtos = productionDemandDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            productionDto.setProductionDetails(productionDetailDtos);
        });
        return productionDtos;
    }

    /**
     * 导出指定用户的生产需求计划
     *
     * @param userName 用户名
     * @param pageable 分页参数
     * @return File
     */
    @Override
    public File createSpecExcelFile(String userName, Pageable pageable) {
        List<ProductionDto> productionDtos = getProductionsByUserName(userName, pageable);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("编号");
        rowInfo.createCell(++columnIndex).setCellValue("主题");
        rowInfo.createCell(++columnIndex).setCellValue("创建时间");
        rowInfo.createCell(++columnIndex).setCellValue("创建者");
        rowInfo.createCell(++columnIndex).setCellValue("状态");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改时间");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改操作者");
        rowInfo.createCell(++columnIndex).setCellValue("生产需求计划子项数量");

        for (int i = 0; i < productionDtos.size(); i++) {
            ProductionDto productionDto = productionDtos.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(productionDto.getProductionId());
            row.getCell(++columnIndex).setCellValue(productionDto.getProductionSubject());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(productionDto.getProductionCreateTime()));
            row.getCell(++columnIndex).setCellValue(Objects.equals(productionDto.getApplicant(), null) ? "-" : productionDto.getApplicant().getUserName());
            row.getCell(++columnIndex).setCellValue(productionDto.getProductionState().getValue());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(productionDto.getLastModifiedTime()));
            row.getCell(++columnIndex).setCellValue(Objects.equals(productionDto.getOperator(), null) ? "-" : productionDto.getOperator().getUserName());
            row.getCell(++columnIndex).setCellValue(Objects.equals(productionDto.getProductionDetails(), null) ? "-" : String.valueOf(productionDto.getProductionDetails().size()));
        }
        return FileUtils.createExcelFile(workbook, "erp_productions");
    }

    /**
     * 导出生产需求计划
     *
     * @param pageable 分页参数
     * @return File
     */
    @Override
    public File createExcelFile(Pageable pageable) {
        Page<ProductionDemand> productionDemandPage = findAll(pageable);
        List<ProductionDemand> productionDemands = productionDemandPage.getContent();
        if (Objects.equals(productionDemands, null)) {
            productionDemands = new ArrayList<>();
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("编号");
        rowInfo.createCell(++columnIndex).setCellValue("主题");
        rowInfo.createCell(++columnIndex).setCellValue("创建时间");
        rowInfo.createCell(++columnIndex).setCellValue("创建者");
        rowInfo.createCell(++columnIndex).setCellValue("状态");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改时间");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改操作者");

        for (int i = 0; i < productionDemands.size(); i++) {
            ProductionDemand productionDemand = productionDemands.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(productionDemand.getProductionId());
            row.getCell(++columnIndex).setCellValue(productionDemand.getProductionSubject());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(productionDemand.getProductionCreateTime()));
            row.getCell(++columnIndex).setCellValue(Objects.equals(productionDemand.getProductionApplicant(), null) ? "-" : productionDemand.getProductionApplicant().getUserName());
            row.getCell(++columnIndex).setCellValue(productionDemand.getProductionState().getValue());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(productionDemand.getLastModifiedTime()));
            row.getCell(++columnIndex).setCellValue(Objects.equals(productionDemand.getLastModifiedOperator(), null) ? "-" : productionDemand.getLastModifiedOperator().getUserName());
        }
        return FileUtils.createExcelFile(workbook, "erp_productions");
    }

    /**
     * 保存操作
     *
     * @param entity 需要保存的实体
     * @return T 返回保存的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductionDemand save(ProductionDemand entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的生产需求计划不能为空");
        }
        return productionDemandRepository.save(entity);
    }

    /**
     * 根据给定一批实体进行批量删除
     *
     * @param entities
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<ProductionDemand> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需求删除的生产需求计划不能为空");
        }
        // 同时删除对应的生产需求计划详细项
        List<String> productionIds = entities.stream().map(ProductionDemand::getProductionId).collect(Collectors.toList());
        productionIds.stream().forEach(productionId -> deleteProductionDetail(productionId));
        productionDemandRepository.deleteAll(entities);
    }

    /**
     * 更新操作
     *
     * @param entity 需要更新的实体
     * @return 更新后的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductionDemand update(ProductionDemand entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的生产需求计划不能为空");
        }
        ProductionDemand productionDemand = productionDemandRepository.findById(entity.getProductionId()).orElse(null);
        if (Objects.equals(productionDemand, null)) {
            throw new EntityNotExistException("生产需求计划不存在");
        }
        return productionDemandRepository.saveAndFlush(entity);
    }

    /**
     * 根据给定 ID 查询一个实体
     *
     * @param id 需要查询的实体 ID
     * @return 查询出来的实体
     */
    @Override
    public ProductionDemand findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("生产需求计划编号不能为空");
        }
        return productionDemandRepository.findById(id).orElseThrow(() -> new EntityNotExistException("生产需求计划不存在"));
    }

    /**
     * 分页查询
     *
     * @param pageable 常用 Pageable 接口的实现类 PageRequest
     * @return 返回一个 Page 对象
     */
    @Override
    public Page<ProductionDemand> findAll(Pageable pageable) {
        return productionDemandRepository.findAll(pageable);
    }

    /* ------------------------------------- 生产需求计划相关 ----------------------------------------- */

    /**
     * 根据生产需求计划详细项编号修改生产数量
     *
     * @param productionDetailId 生产需求计划详细项编号
     * @param productionNumber   生产数量
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionDetailNumber(String productionDetailId, Long productionNumber) {
        if (!StringUtils.hasText(productionDetailId)) {
            throw new ParameterException("生产需求计划编号不能为空");
        }
        if (Objects.equals(productionNumber, null) || productionNumber < 0) {
            throw new ParameterException("生产数量不能小于零");
        }
        return productionDemandDetailRepository.updateNumber(productionDetailId, productionNumber) > 0;
    }

    /**
     * 根据生产需求计划编号删除其下所有的生产需求详细项
     *
     * @param productionId 生产需求计划编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductionDetail(String productionId) {
        if (!StringUtils.hasText(productionId)) {
            throw new ParameterException("生产需求计划编号不能为空");
        }
        productionDemandDetailRepository.deleteByProductionId(productionId);
    }

    /**
     * 导出指定的生产需求计划下的所有的生产需求计划详细项
     *
     * @param productionId 生产需求计划编号
     * @param pageable     分页参数
     * @return File
     */
    @Override
    public File createDetailExcelFile(String productionId, Pageable pageable) {
        List<ProductionDemandDetail> productionDemandDetails = productionDemandDetailRepository.findByBelongDemand_ProductionId(productionId, pageable);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("编号");
        rowInfo.createCell(++columnIndex).setCellValue("是否为新产品");
        rowInfo.createCell(++columnIndex).setCellValue("仓库");
        rowInfo.createCell(++columnIndex).setCellValue("生产数量");
        rowInfo.createCell(++columnIndex).setCellValue("产品编号");
        rowInfo.createCell(++columnIndex).setCellValue("产品名");
        rowInfo.createCell(++columnIndex).setCellValue("成本价");
        rowInfo.createCell(++columnIndex).setCellValue("出售价");
        rowInfo.createCell(++columnIndex).setCellValue("规格");
        rowInfo.createCell(++columnIndex).setCellValue("制造商");
        rowInfo.createCell(++columnIndex).setCellValue("产地");

        for (int i = 0; i < productionDemandDetails.size(); i++) {
            ProductionDemandDetail productionDemandDetail = productionDemandDetails.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(productionDemandDetail.getProductionDetailId());
            row.getCell(++columnIndex).setCellValue(productionDemandDetail.getNewProduct() ? "是" : "否");
            row.getCell(++columnIndex).setCellValue(Objects.equals(productionDemandDetail.getProductionStock(), null) ? "-" : productionDemandDetail.getProductionStock().getStockName());
            row.getCell(++columnIndex).setCellValue(productionDemandDetail.getProductionNumber().toString());
            row.getCell(++columnIndex).setCellValue(productionDemandDetail.getProductId());
            row.getCell(++columnIndex).setCellValue(productionDemandDetail.getProductName());
            row.getCell(++columnIndex).setCellValue(productionDemandDetail.getProductInPrice().toString());
            row.getCell(++columnIndex).setCellValue(productionDemandDetail.getProductOutPrice().toString());
            row.getCell(++columnIndex).setCellValue(productionDemandDetail.getProductSpecification());
            row.getCell(++columnIndex).setCellValue(productionDemandDetail.getProductManufacturer());
            row.getCell(++columnIndex).setCellValue(productionDemandDetail.getProductOrigin());
        }
        return FileUtils.createExcelFile(workbook, "erp_production_details");
    }
}
