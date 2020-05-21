package com.beiran.core.purchase.service.impl;

import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.DateTimeUtils;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.material.entity.Material;
import com.beiran.core.material.service.MaterialService;
import com.beiran.core.purchase.dto.PurchaseDetailDto;
import com.beiran.core.purchase.dto.PurchaseDto;
import com.beiran.core.purchase.entity.PurchaseOrder;
import com.beiran.core.purchase.entity.PurchaseOrderDetail;
import com.beiran.core.purchase.repository.PurchaseOrderDetailRepository;
import com.beiran.core.purchase.repository.PurchaseOrderRepository;
import com.beiran.core.purchase.service.PurchaseService;
import com.beiran.core.purchase.vo.PurchaseVo;
import com.beiran.core.stock.dto.StockSmallDto;
import com.beiran.core.stock.entity.Stock;
import com.beiran.core.stock.service.StockService;
import com.beiran.core.stock.vo.MaterialNumberVo;
import com.beiran.core.system.dto.UserSmallDto;
import com.beiran.core.system.entity.Dept;
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
 * PurchaseService 接口实现类
 */

@Service("purchaseService")
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private StockService stockService;

    /* --------------------------------- 采购计划相关 -------------------------------------- */

    /**
     * 创建采购计划，默认为 CREATED 状态
     *
     * @param purchaseVo 创建采购计划所需数据
     * @return PurchaseDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseDto createPurchaseOrder(PurchaseVo purchaseVo) {
        if (Objects.equals(purchaseVo, null)) {
            throw new ParameterException("需要保存的采购计划不能为空");
        }
        // 返回的采购计划数据
        PurchaseDto purchaseDto = new PurchaseDto();

        // 需要保存的采购计划数据
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseSubject(purchaseVo.getPurchaseSubject());
        purchaseOrder.setPurchaseState(purchaseVo.getPurchaseState());
        User user = new User();
        user.setUserId(purchaseVo.getApplicant().getUserId());
        user.setUserName(purchaseVo.getApplicant().getUserName());
        purchaseOrder.setPurchaseApplicant(user);
        purchaseOrder.setLastModifiedOperator(user);
        purchaseOrder.setLastModifiedTime(new Date());
        // 执行保存
        PurchaseOrder save = save(purchaseOrder);
        // 转换并保存每个采购计划详细项
        List<PurchaseOrderDetail> purchaseOrderDetails = purchaseVo.getPurchaseOrderDetails().stream().map(purchaseDetailVo -> {
            // FIXME 如果一个采购计划详细项都没有，会抛异常
            PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail();
            BeanUtils.copyProperties(purchaseDetailVo, purchaseOrderDetail);
            purchaseOrderDetail.setBelongOrder(save);
            purchaseOrderDetail.setNewMaterial(purchaseDetailVo.getNewMaterial());
            purchaseOrderDetail.setPurchaseNumber(purchaseDetailVo.getNumber());
            Stock stock = new Stock();
            stock.setStockId(purchaseDetailVo.getStock().getStockId());
            stock.setStockName(purchaseDetailVo.getStock().getStockName());
            purchaseOrderDetail.setPurchaseStock(stock);
            return purchaseOrderDetail;
        }).collect(Collectors.toList());
        List<PurchaseDetailDto> purchaseDetailDtos = purchaseOrderDetails.stream().map(purchaseOrderDetail -> {
            PurchaseDetailDto purchaseDetailDto = new PurchaseDetailDto();
            PurchaseOrderDetail detail = purchaseOrderDetailRepository.save(purchaseOrderDetail);
            BeanUtils.copyProperties(detail, purchaseDetailDto);
            purchaseDetailDto.setNumber(detail.getPurchaseNumber().toString());
            StockSmallDto stockSmallDto = new StockSmallDto();
            stockSmallDto.setStockId(detail.getPurchaseStock().getStockId());
            stockSmallDto.setStockName(detail.getPurchaseStock().getStockName());
            purchaseDetailDto.setStock(stockSmallDto);
            return purchaseDetailDto;
        }).collect(Collectors.toList());
        // 复制 purchaseOrder 的属性到 purchaseDto 中，然后将其返回
        BeanUtils.copyProperties(save, purchaseDto);
        purchaseDto.setPurchaseOrderDetails(purchaseDetailDtos);
        UserSmallDto applicant = new UserSmallDto();
        applicant.setUserId(save.getPurchaseApplicant().getUserId());
        applicant.setUserName(save.getPurchaseApplicant().getUserName());
        purchaseDto.setApplicant(applicant);
        UserSmallDto operator = new UserSmallDto();
        operator.setUserId(save.getLastModifiedOperator().getUserId());
        operator.setUserName(save.getLastModifiedOperator().getUserName());
        purchaseDto.setOperator(operator);
        return purchaseDto;
    }

    /**
     * 修改采购计划状态（此方法不提供接口）
     *
     * @param purchaseId         采购计划编号
     * @param purchaseOrderState 采购计划状态
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateState(String purchaseId, PurchaseOrder.PurchaseOrderState purchaseOrderState) {
        if (!StringUtils.hasText(purchaseId)) {
            throw new ParameterException("采购计划编号不能为空");
        }
        // 不对当前状态进行判断，直接修改
        // 同时需要修改上一次修改时间和操作者
        PurchaseOrder purchaseOrder = findById(purchaseId);
        purchaseOrder.setLastModifiedTime(new Date());
        User user = new User();
        user.setUserId(SecurityUtil.getUserId());
        user.setUserName(SecurityUtil.getUserName());
        purchaseOrder.setLastModifiedOperator(user);
        purchaseOrder.setPurchaseState(purchaseOrderState);
        PurchaseOrder update = update(purchaseOrder);
        return !Objects.equals(update,null);
    }

    /**
     * 修改采购计划状态为 CONFIRMED
     *
     * @param purchaseId 采购计划编号
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmPurchase(String purchaseId) {
        if (!StringUtils.hasText(purchaseId)) {
            throw new ParameterException("采购计划编号不能为空");
        }
        // 如果状态不是 CREATED 的采购计划，则无法修改为 CONFIRMED 状态
        PurchaseOrder purchaseOrder = findById(purchaseId);
        if (!Objects.equals(purchaseOrder.getPurchaseState(), PurchaseOrder.PurchaseOrderState.CREATED)) {
            throw new ParameterException("无法被修改");
        }
        return updateState(purchaseId, PurchaseOrder.PurchaseOrderState.CONFIRMED);
    }

    /**
     * 修改采购计划状态为 VERIFYING
     *
     * @param purchaseId 采购计划编号
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean verifyPurchase(String purchaseId) {
        if (!StringUtils.hasText(purchaseId)) {
            throw new ParameterException("采购计划编号不能为空");
        }
        // 如果状态不是 CONFIRMED/DISCUSSING 的采购计划，则无法修改为 VERIFYING 状态
        PurchaseOrder purchaseOrder = findById(purchaseId);
        if (!Objects.equals(purchaseOrder.getPurchaseState(), PurchaseOrder.PurchaseOrderState.CONFIRMED) || !Objects.equals(purchaseOrder.getPurchaseState(), PurchaseOrder.PurchaseOrderState.DISCUSSING)) {
            throw new ParameterException("无法被修改");
        }
        return updateState(purchaseId, PurchaseOrder.PurchaseOrderState.VERIFYING);
    }

    /**
     * 修改采购计划状态为 IMPORTED
     *
     * @param purchaseId 采购计划编号
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean importPurchase(String purchaseId) {
        // 已入库状态需要将物料存入仓库
        if (!StringUtils.hasText(purchaseId)) {
            throw new ParameterException("采购计划编号不能为空");
        }
        // 如果状态不是 VERIFYING 的采购计划，则无法修改为 IMPORTED 状态
        PurchaseOrder purchaseOrder = findById(purchaseId);
        if (!Objects.equals(purchaseOrder.getPurchaseState(), PurchaseOrder.PurchaseOrderState.VERIFYING)) {
            throw new ParameterException("无法被修改");
        }
        boolean result = updateState(purchaseId, PurchaseOrder.PurchaseOrderState.IMPORTED);
        List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailRepository.findByBelongOrder_PurchaseId(purchaseId, PageRequest.of(0, 200));
        purchaseOrderDetails.stream().forEach(purchaseOrderDetail -> {
            // 若为新物料则添加，否则直接在原有库存中加数量
            if (purchaseOrderDetail.getNewMaterial()) {
                Material material = new Material();
                // FIXME BeanUtils 复制数据可能会出错
                BeanUtils.copyProperties(purchaseOrderDetail, material);
                Material save = materialService.save(material);
                MaterialNumberVo materialNumberVo = new MaterialNumberVo(save.getMaterialId(), purchaseOrderDetail.getPurchaseStock().getStockId(), purchaseOrderDetail.getPurchaseNumber());
                stockService.saveMaterialRecord(materialNumberVo);
            } else {
                MaterialNumberVo materialNumberVo = new MaterialNumberVo(purchaseOrderDetail.getMaterialId(), purchaseOrderDetail.getPurchaseStock().getStockId(), purchaseOrderDetail.getPurchaseNumber());
                stockService.saveMaterialRecord(materialNumberVo);
            }
        });
        return result;
    }

    /**
     * 修改采购计划状态为 DISCUSSING
     *
     * @param purchaseId 采购计划编号
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean discussPurchase(String purchaseId) {
        if (!StringUtils.hasText(purchaseId)) {
            throw new ParameterException("采购计划编号不能为空");
        }
        // 如果状态不是 VERIFYING 的采购计划，则无法修改为 DISCUSSING 状态
        PurchaseOrder purchaseOrder = findById(purchaseId);
        if (!Objects.equals(purchaseOrder.getPurchaseState(), PurchaseOrder.PurchaseOrderState.VERIFYING)) {
            throw new ParameterException("无法被修改");
        }
        return updateState(purchaseId, PurchaseOrder.PurchaseOrderState.DISCUSSING);
    }

    /**
     * 修改采购计划状态为 CLOSED
     *
     * @param purchaseId 采购计划编号
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean closePurchase(String purchaseId) {
        if (!StringUtils.hasText(purchaseId)) {
            throw new ParameterException("采购计划编号不能为空");
        }
        // 如果状态不是 CREATED/IMPORTED/DISCUSSING 的采购计划，则无法修改为 CLOSED 状态
        PurchaseOrder purchaseOrder = findById(purchaseId);
        if (Objects.equals(purchaseOrder.getPurchaseState(), PurchaseOrder.PurchaseOrderState.CREATED) || Objects.equals(purchaseOrder.getPurchaseState(), PurchaseOrder.PurchaseOrderState.IMPORTED) || Objects.equals(purchaseOrder.getPurchaseState(), PurchaseOrder.PurchaseOrderState.DISCUSSING)) {
            return updateState(purchaseId, PurchaseOrder.PurchaseOrderState.CLOSED);
        } else {
            throw new ParameterException("无法被修改");
        }
    }

    /**
     * 根据用户名查询采购计划
     *
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<PurchaseDto>
     */
    @Override
    public List<PurchaseDto> getPurchasesByUserName(String userName, Pageable pageable) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("用户名不能为空");
        }
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByPurchaseApplicant_UserName(userName, pageable);
        if (purchaseOrders.isEmpty()) {
            throw new EntityNotExistException("用户暂无任何采购计划");
        }
        List<PurchaseDto> purchaseDtos = purchaseOrders.stream().map(purchaseOrder -> {
            PurchaseDto purchaseDto = transferPurchase(purchaseOrder);
            return purchaseDto;
        }).collect(Collectors.toList());
        return purchaseDtos;
    }

    /**
     * 根据用户名以及采购计划状态查询采购计划
     *
     * @param userName      用户名
     * @param purchaseState 采购计划状态
     * @param pageable      分页参数
     * @return List<PurchaseDto>
     */
    @Override
    public List<PurchaseDto> getPurchasesByUserNameAndState(String userName, PurchaseOrder.PurchaseOrderState purchaseState, Pageable pageable) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("用户名不能为空");
        }
        if (Objects.equals(purchaseState, null)) {
            throw new ParameterException("采购计划状态不能为空");
        }
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByPurchaseApplicant_UserNameAndPurchaseState(userName, purchaseState, pageable);
        List<PurchaseDto> purchaseDtos =
                purchaseOrders.stream()
                        .map(this::transferPurchase)
                        .collect(Collectors.toList());
        return purchaseDtos;
    }

    /**
     * 根据采购计划创建时间查询采购计划
     *
     * @param leftTime  左区间
     * @param rightTime 右区间
     * @param pageable  分页参数
     * @return List<PurchaseDto>
     */
    @Override
    public List<PurchaseDto> getPurchasesByCreateTime(Date leftTime, Date rightTime, Pageable pageable) {
        if (Objects.equals(leftTime, null) || Objects.equals(rightTime, null)) {
            throw new ParameterException("采购计划创建时间不能为空");
        }
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByPurchaseCreateTimeBetween(leftTime, rightTime, pageable);
        List<PurchaseDto> purchaseDtos =
                purchaseOrders.stream()
                        .map(this::transferPurchase)
                        .collect(Collectors.toList());
        return purchaseDtos;
    }

    /**
     * 根据采购计划状态查询采购计划
     *
     * @param purchaseState 采购计划状态
     * @param pageable      分页参数
     * @return List<PurchaseDto>
     */
    @Override
    public List<PurchaseDto> getPurchasesByState(PurchaseOrder.PurchaseOrderState purchaseState, Pageable pageable) {
        if (Objects.equals(purchaseState, null)) {
            throw new ParameterException("采购计划状态不能为空");
        }
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByPurchaseState(purchaseState, pageable);
        List<PurchaseDto> purchaseDtos =
                purchaseOrders.stream()
                        .map(this::transferPurchase)
                        .collect(Collectors.toList());
        return purchaseDtos;
    }

    /**
     * 根据上一次修改时间查询采购计划
     *
     * @param leftTime  左区间
     * @param rightTime 右区间
     * @param pageable  分页参数
     * @return List<PurchaseDto>
     */
    @Override
    public List<PurchaseDto> getPurchasesByModifiedTime(Date leftTime, Date rightTime, Pageable pageable) {
        if (Objects.equals(leftTime, null) || Objects.equals(rightTime, null)) {
            throw new ParameterException("采购计划上一次修改时间不能为空");
        }
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByLastModifiedTimeBetween(leftTime, rightTime, pageable);
        List<PurchaseDto> purchaseDtos =
                purchaseOrders.stream()
                        .map(this::transferPurchase)
                        .collect(Collectors.toList());
        return purchaseDtos;
    }

    @Override
    public List<PurchaseDto> getAllPurchases(Pageable pageable) {
        Page<PurchaseOrder> purchaseOrderPage = findAll(pageable);
        List<PurchaseOrder> purchaseOrders = purchaseOrderPage.getContent();
        List<PurchaseDto> purchaseDtos =
                purchaseOrders.stream()
                        .map(this::transferPurchase)
                        .collect(Collectors.toList());
        return purchaseDtos;
    }

    /**
     * 导出指定用户的采购计划
     *
     * @param userName 用户名
     * @param pageable 分页参数
     * @return File
     */
    @Override
    public File createSpecExcelFile(String userName, Pageable pageable) {
        // 采购计划与采购计划详细分开导出
        List<PurchaseDto> purchaseDtos = getPurchasesByUserName(userName, pageable);
        // 根据分页信息获取数据
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("编号");
        rowInfo.createCell(++columnIndex).setCellValue("主题");
        rowInfo.createCell(++columnIndex).setCellValue("创建时间");
        rowInfo.createCell(++columnIndex).setCellValue("创建人");
        rowInfo.createCell(++columnIndex).setCellValue("状态");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改时间");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改操作者");
        rowInfo.createCell(++columnIndex).setCellValue("采购计划子项数量");

        for (int i = 0; i < purchaseDtos.size(); i++) {
            PurchaseDto purchaseDto = purchaseDtos.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(purchaseDto.getPurchaseId());
            row.getCell(++columnIndex).setCellValue(purchaseDto.getPurchaseSubject());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(purchaseDto.getPurchaseCreateTime()));
            row.getCell(++columnIndex).setCellValue(Objects.equals(purchaseDto.getApplicant(), null) ? "-" : purchaseDto.getApplicant().getUserName());
            row.getCell(++columnIndex).setCellValue(purchaseDto.getPurchaseState().getValue());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(purchaseDto.getLastModifiedTime()));
            row.getCell(++columnIndex).setCellValue(Objects.equals(purchaseDto.getOperator(), null) ? "-" : purchaseDto.getOperator().getUserName());
            row.getCell(++columnIndex).setCellValue(Objects.equals(purchaseDto.getPurchaseOrderDetails(), null) ? "-" : String.valueOf(purchaseDto.getPurchaseOrderDetails().size()));
        }
        return FileUtils.createExcelFile(workbook, "erp_purchases");
    }

    /**
     * 导出所有的采购计划
     *
     * @param pageable 分页参数
     * @return File
     */
    @Override
    public File createExcelFile(Pageable pageable) {
        // 根据分页信息获取数据
        Page<PurchaseOrder> purchaseOrderPage = findAll(pageable);
        List<PurchaseOrder> purchaseOrders = purchaseOrderPage.getContent();
        if (Objects.equals(purchaseOrders, null)) {
            purchaseOrders = new ArrayList<>();
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
        rowInfo.createCell(++columnIndex).setCellValue("创建人");
        rowInfo.createCell(++columnIndex).setCellValue("状态");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改时间");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改操作者");

        for (int i = 0; i < purchaseOrders.size(); i++) {
            PurchaseOrder purchaseOrder = purchaseOrders.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(purchaseOrder.getPurchaseId());
            row.getCell(++columnIndex).setCellValue(purchaseOrder.getPurchaseSubject());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(purchaseOrder.getPurchaseCreateTime()));
            row.getCell(++columnIndex).setCellValue(Objects.equals(purchaseOrder.getPurchaseApplicant(), null) ? "-" : purchaseOrder.getPurchaseApplicant().getUserName());
            row.getCell(++columnIndex).setCellValue(purchaseOrder.getPurchaseState().getValue());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(purchaseOrder.getLastModifiedTime()));
            row.getCell(++columnIndex).setCellValue(Objects.equals(purchaseOrder.getLastModifiedOperator(), null) ? "-" : purchaseOrder.getLastModifiedOperator().getUserName());
        }
        return FileUtils.createExcelFile(workbook, "erp_purchases");
    }

    /**
     * 保存操作
     *
     * @param entity 需要保存的实体
     * @return T 返回保存的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder save(PurchaseOrder entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的采购计划不能为空");
        }
        return purchaseOrderRepository.save(entity);
    }

    /**
     * 根据给定一批实体进行批量删除
     *
     * @param entities
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<PurchaseOrder> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的采购计划不能为空");
        }
        // 需要同时删除采购计划详细项
        List<String> purchaseIds = entities.stream().map(PurchaseOrder::getPurchaseId).collect(Collectors.toList());
        purchaseIds.stream().forEach(purchaseId -> {
            deletePurchaseDetail(purchaseId);
        });
        purchaseOrderRepository.deleteAll(entities);
    }

    /**
     * 更新操作
     *
     * @param entity 需要更新的实体
     * @return 更新后的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder update(PurchaseOrder entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的采购计划不能为空");
        }
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(entity.getPurchaseId()).orElse(null);
        if (Objects.equals(purchaseOrder, null)) {
            throw new EntityNotExistException("采购计划不存在");
        }
        return purchaseOrderRepository.saveAndFlush(entity);
    }

    /**
     * 根据给定 ID 查询一个实体
     *
     * @param id 需要查询的实体 ID
     * @return 查询出来的实体
     */
    @Override
    public PurchaseOrder findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("采购计划编号不能为空");
        }
        return purchaseOrderRepository.findById(id).orElseThrow(() -> new EntityNotExistException("采购计划不存在"));
    }

    /**
     * 分页查询
     *
     * @param pageable 常用 Pageable 接口的实现类 PageRequest
     * @return 返回一个 Page 对象
     */
    @Override
    public Page<PurchaseOrder> findAll(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable);
    }

    private PurchaseDto transferPurchase(PurchaseOrder purchaseOrder) {
        if (Objects.equals(purchaseOrder, null)) {
            throw new ParameterException("需要转换的采购计划数据异常");
        }
        PurchaseDto purchaseDto = new PurchaseDto();
        BeanUtils.copyProperties(purchaseOrder, purchaseDto);

        UserSmallDto applicant = new UserSmallDto();
        applicant.setUserId(purchaseOrder.getPurchaseApplicant().getUserId());
        applicant.setUserName(purchaseOrder.getPurchaseApplicant().getUserName());

        UserSmallDto operator = new UserSmallDto();
        operator.setUserId(purchaseOrder.getPurchaseApplicant().getUserId());
        operator.setUserName(purchaseOrder.getPurchaseApplicant().getUserName());

        List<PurchaseDetailDto> purchaseDetailDtos = new ArrayList<>();
        List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailRepository.findByBelongOrder_PurchaseId(purchaseOrder.getPurchaseId(), PageRequest.of(0, 200));
        if (!purchaseOrderDetails.isEmpty()) {
            purchaseDetailDtos = purchaseOrderDetails
                    .stream()
                    .map(purchaseOrderDetail -> transferDetail(purchaseOrderDetail))
                    .collect(Collectors.toList());
        }

        purchaseDto.setApplicant(applicant);
        purchaseDto.setOperator(operator);
        purchaseDto.setPurchaseOrderDetails(purchaseDetailDtos);
        return purchaseDto;
    }

    private PurchaseDetailDto transferDetail(PurchaseOrderDetail purchaseOrderDetail) {
        if (Objects.equals(purchaseOrderDetail, null)) {
            throw new ParameterException("需要转换的采购计划子项数据异常");
        }
        PurchaseDetailDto purchaseDetailDto = new PurchaseDetailDto();
        BeanUtils.copyProperties(purchaseOrderDetail, purchaseDetailDto);
        purchaseDetailDto.setNumber(purchaseOrderDetail.getPurchaseNumber().toString());
        StockSmallDto stock = new StockSmallDto();
        stock.setStockId(purchaseOrderDetail.getPurchaseStock().getStockId());
        stock.setStockName(purchaseOrderDetail.getPurchaseStock().getStockName());
        purchaseDetailDto.setStock(stock);
        return purchaseDetailDto;
    }

    /* --------------------------------- 采购计划详细项相关 --------------------------------- */

    /**
     * 修改采购计划详细项的采购数量
     *
     * @param purchaseDetailId 采购计划详细项编号
     * @param purchaseNumber   采购数量
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePurchaseDetailNumber(String purchaseDetailId, Long purchaseNumber) {
        if (!StringUtils.hasText(purchaseDetailId)) {
            throw new ParameterException("采购计划子项编号不能为空");
        }
        if (Objects.equals(purchaseNumber, null) || purchaseNumber < 0) {
            throw new ParameterException("采购数量不能小于零");
        }
        return purchaseOrderDetailRepository.updateNumber(purchaseDetailId, purchaseNumber) > 0;
    }

    /**
     * 删除采购计划详细项
     *
     * @param purchaseId 采购计划编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePurchaseDetail(String purchaseId) {
        if (!StringUtils.hasText(purchaseId)) {
            throw new ParameterException("采购计划编号不能为空");
        }
        purchaseOrderDetailRepository.deleteByPurchaseId(purchaseId);
    }

    /**
     * 导出指定采购计划的采购计划详细项
     *
     * @param purchaseId 采购计划编号
     * @param pageable   分页参数
     * @return File
     */
    @Override
    public File createDetailExcelFile(String purchaseId, Pageable pageable) {
        List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailRepository.findByBelongOrder_PurchaseId(purchaseId, pageable);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("编号");
        rowInfo.createCell(++columnIndex).setCellValue("是否为新物料");
        rowInfo.createCell(++columnIndex).setCellValue("仓库");
        rowInfo.createCell(++columnIndex).setCellValue("采购数量");
        rowInfo.createCell(++columnIndex).setCellValue("物料编号");
        rowInfo.createCell(++columnIndex).setCellValue("物料名");
        rowInfo.createCell(++columnIndex).setCellValue("物料分类");
        rowInfo.createCell(++columnIndex).setCellValue("物料价格");
        rowInfo.createCell(++columnIndex).setCellValue("物料规格");
        rowInfo.createCell(++columnIndex).setCellValue("物料制造商");
        rowInfo.createCell(++columnIndex).setCellValue("物料产地");

        for (int i = 0; i < purchaseOrderDetails.size(); i++) {
            PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetails.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(purchaseOrderDetail.getPurchaseDetailId());
            row.getCell(++columnIndex).setCellValue(purchaseOrderDetail.getNewMaterial() ? "是" : "否");
            row.getCell(++columnIndex).setCellValue(Objects.equals(purchaseOrderDetail.getPurchaseStock(), null) ? "-" : purchaseOrderDetail.getPurchaseStock().getStockName());
            row.getCell(++columnIndex).setCellValue(purchaseOrderDetail.getPurchaseNumber().toString());
            row.getCell(++columnIndex).setCellValue(Objects.equals(purchaseOrderDetail.getMaterialId(), null) ? "-" : purchaseOrderDetail.getMaterialId());
            row.getCell(++columnIndex).setCellValue(purchaseOrderDetail.getMaterialName());
            row.getCell(++columnIndex).setCellValue(Objects.equals(purchaseOrderDetail.getMaterialCategory(), null) ? "-" : purchaseOrderDetail.getMaterialCategory().getCategoryName());
            row.getCell(++columnIndex).setCellValue(purchaseOrderDetail.getMaterialInPrice().toString());
            row.getCell(++columnIndex).setCellValue(purchaseOrderDetail.getMaterialSpecification());
            row.getCell(++columnIndex).setCellValue(purchaseOrderDetail.getMaterialManufacturer());
            row.getCell(++columnIndex).setCellValue(purchaseOrderDetail.getMaterialOrigin());
        }
        return FileUtils.createExcelFile(workbook, "erp_purchase_details");
    }
}
