package com.beiran.core.sale.service.impl;

import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.DateTimeUtils;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.sale.dto.SaleDetailDto;
import com.beiran.core.sale.dto.SaleDto;
import com.beiran.core.sale.entity.SaleOrder;
import com.beiran.core.sale.entity.SaleOrderDetail;
import com.beiran.core.sale.repository.SaleOrderDetailRepository;
import com.beiran.core.sale.repository.SaleOrderRepository;
import com.beiran.core.sale.service.SaleService;
import com.beiran.core.sale.vo.SaleDetailVo;
import com.beiran.core.sale.vo.SaleVo;
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
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SaleService 接口实现类
 */

@Service("saleService")
public class SaleServiceImpl implements SaleService {

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private SaleOrderDetailRepository saleOrderDetailRepository;

    @Autowired
    private StockService stockService;

    private SaleDto transferSale(SaleOrder saleOrder) {
        SaleDto saleDto = new SaleDto();
        if (!Objects.equals(saleOrder, null)) {
            BeanUtils.copyProperties(saleOrder, saleDto);
        }
        UserSmallDto applicant = new UserSmallDto();
        if (!Objects.equals(saleOrder.getSaleUser(), null)) {
            applicant.setUserId(saleOrder.getSaleUser().getUserId());
            applicant.setUserName(saleOrder.getSaleUser().getUserName());
        }
        UserSmallDto operator = new UserSmallDto();
        if (!Objects.equals(saleOrder.getLastModifiedOperator(), null)) {
            operator.setUserId(saleOrder.getLastModifiedOperator().getUserId());
            operator.setUserName(saleOrder.getLastModifiedOperator().getUserName());
        }

        saleDto.setApplicant(applicant);
        saleDto.setOperator(operator);
        return saleDto;
    }

    private SaleDetailDto transferDetail(SaleOrderDetail saleOrderDetail) {
        SaleDetailDto saleDetailDto = new SaleDetailDto();
        if (!Objects.equals(saleOrderDetail, null)) {
            BeanUtils.copyProperties(saleOrderDetail, saleDetailDto);
        }
        return saleDetailDto;
    }

    private SaleOrder transferVo(SaleVo saleVo) {
        SaleOrder saleOrder = new SaleOrder();
        if (!Objects.equals(saleVo, null)) {
            BeanUtils.copyProperties(saleVo, saleOrder);
        }
        User applicant = new User();
        if (!Objects.equals(saleVo.getApplicant(), null)) {
            applicant.setUserId(saleVo.getApplicant().getUserId());
            applicant.setUserName(saleVo.getApplicant().getUserName());
        }
        return saleOrder;
    }

    private SaleOrderDetail transferDetailVo(SaleDetailVo saleDetailVo) {
        SaleOrderDetail saleOrderDetail = new SaleOrderDetail();
        if (!Objects.equals(saleDetailVo, null)) {
            BeanUtils.copyProperties(saleDetailVo, saleOrderDetail);
        }
        return saleOrderDetail;
    }

    /* ------------------------------ 销售订单相关 ----------------------------------- */

    /**
     * 创建销售订单
     *
     * @param saleVo 创建销售订单所需数据
     * @return SaleDto
     */
    @Override
    public SaleDto createSale(SaleVo saleVo) {
        if (Objects.equals(saleVo, null)) {
            throw new ParameterException("需要保存的销售订单不能为空");
        }
        if (Objects.equals(saleVo.getSaleDetails(), null) || saleVo.getSaleDetails().isEmpty()) {
            throw new ParameterException("需要保存的销售订单子项不能为空");
        }
        // FIXME 获取当前用户可能会出错
        SaleOrder saleOrder = transferVo(saleVo);
        saleOrder.setLastModifiedTime(new Date());
        User operator = new User();
        operator.setUserId(SecurityUtil.getUserId());
        operator.setUserName(SecurityUtil.getUserName());
        saleOrder.setLastModifiedOperator(operator);

        SaleOrder save = save(saleOrder);
        // 同时保存销售订单详细项
        List<SaleOrderDetail> saleOrderDetails = saleVo.getSaleDetails().stream().map(this::transferDetailVo).collect(Collectors.toList());
        saleOrderDetails.stream().forEach(saleOrderDetail -> {
            saleOrderDetail.setBelongOrder(save);
        });
        List<SaleOrderDetail> saveAll = saleOrderDetailRepository.saveAll(saleOrderDetails);
        List<SaleDetailDto> saleDetailDtos = saveAll.stream().map(this::transferDetail).collect(Collectors.toList());

        SaleDto saleDto = transferSale(save);
        saleDto.setSaleDetails(saleDetailDtos);
        return saleDto;
    }

    /**
     * 根据销售订单编号修改销售订单状态
     *
     * @param saleId    销售订单编号
     * @param saleState 销售订单状态
     * @return 是否修改成功
     */
    @Override
    public Boolean updateState(String saleId, SaleOrder.SaleOrderState saleState) {
        // 修改状态的同时需要修改上一次修改时间与操作者
        if (!StringUtils.hasText(saleId)) {
            throw new ParameterException("销售订单编号不能为空");
        }
        SaleOrder saleOrder = findById(saleId);
        saleOrder.setLastModifiedTime(new Date());
        User operator = new User();
        operator.setUserId(SecurityUtil.getUserId());
        operator.setUserName(SecurityUtil.getUserName());
        saleOrder.setLastModifiedOperator(operator);
        update(saleOrder);
        return saleOrderRepository.updateState(saleId, saleState) > 0;
    }

    /* 创建 -> 取消/结算
    结算 -> 完成/取消
     */

    /**
     * 修改销售订单状态为 PAYING
     *
     * @param saleId 销售订单编号
     * @return 是否修改成功
     */
    @Override
    public Boolean paySale(String saleId) {
        SaleOrder saleOrder = findById(saleId);
        if (!Objects.equals(saleOrder.getSaleState(), SaleOrder.SaleOrderState.CREATED)) {
            throw new ParameterException("无法被修改");
        }
        return updateState(saleId, SaleOrder.SaleOrderState.PAYING);
    }

    /**
     * 修改销售订单状态为 COMPLETED
     *
     * @param saleId 销售订单编号
     * @return 是否修改成功
     */
    @Override
    public Boolean completeSale(String saleId) {
        // 完成时需要扣除相应库存。由于实际的出货流程过于复杂，故在此简单进行此操作
        // 实际出货时，应寻找与目标地址最近的仓库进行出货流程
        SaleOrder saleOrder = findById(saleId);
        if (!Objects.equals(saleOrder.getSaleState(), SaleOrder.SaleOrderState.PAYING)) {
            throw new ParameterException("无法被修改");
        }
        boolean result = updateState(saleId, SaleOrder.SaleOrderState.COMPLETED);
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findByBelongOrder_SaleId(saleId, PageRequest.of(0, 200));
        if (result) {
            saleOrderDetails.forEach(saleOrderDetail -> {
                // 订单中的数量
                Long saleNumber = saleOrderDetail.getSaleNumber();
                Page<Stock> stockPage = stockService.findAll(PageRequest.of(0, 200));
                List<Stock> stocks = stockPage.getContent();
                for (Stock stock : stocks) {
                    ProductNumberVo productStockNumber = stockService.getProductStockNumber(saleOrderDetail.getSaleProduct().getProductId(), stock.getStockId());
                    // 库存数量
                    Long stockNumber = productStockNumber.getProductNumber();
                    // 如果销售数量为 0 则说明已经出货完成了
                    if (saleNumber > 0) {
                        if (stockNumber >= 0) {
                            if (stockNumber >= saleNumber) {
                                stockService.updateProductNumber(saleOrderDetail.getSaleProduct().getProductId(), stock.getStockId(), Long.valueOf(stockNumber - saleNumber));
                            } else {
                                stockService.updateProductNumber(saleOrderDetail.getSaleProduct().getProductId(), stock.getStockId(), Long.valueOf(0));
                                saleNumber -= stockNumber;
                            }
                        }
                    }
                }
            });
        }
        return result;
    }

    /**
     * 修改销售订单状态为 CANCELED
     *
     * @param saleId 销售订单编号
     * @return 是否修改成功
     */
    @Override
    public Boolean cancelSale(String saleId) {
        SaleOrder saleOrder = findById(saleId);
        if (!Objects.equals(saleOrder.getSaleState(), SaleOrder.SaleOrderState.CREATED) || !Objects.equals(saleOrder.getSaleState(), SaleOrder.SaleOrderState.PAYING)) {
            throw new ParameterException("无法被修改");
        }
        return updateState(saleId, SaleOrder.SaleOrderState.CANCELED);
    }

    /**
     * 修改销售订单结算方式
     *
     * @param saleId     销售订单编号
     * @param salePayWay 结算方式
     * @return 是否修改成功
     */
    @Override
    public Boolean updatePayWay(String saleId, SaleOrder.SalePayWay salePayWay) {
        // 修改结算方式的同时需要修改上一次修改时间与操作者
        if (!StringUtils.hasText(saleId)) {
            throw new ParameterException("销售订单编号不能为空");
        }
        SaleOrder saleOrder = findById(saleId);
        saleOrder.setLastModifiedTime(new Date());
        User operator = new User();
        operator.setUserId(SecurityUtil.getUserId());
        operator.setUserName(SecurityUtil.getUserName());
        saleOrder.setLastModifiedOperator(operator);
        update(saleOrder);
        return saleOrderRepository.updatePayWay(saleId, salePayWay) > 0;
    }

    /**
     * 根据用户名查询
     *
     * @param userName 用户名
     * @param pageable 分页参数
     * @return List<SaleDto>
     */
    @Override
    public List<SaleDto> getSalesByUserName(String userName, Pageable pageable) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("用户名不能为空");
        }
        List<SaleOrder> saleOrders = saleOrderRepository.findBySaleUser_UserName(userName, pageable);
        List<SaleDto> saleDtos =
                saleOrders.stream()
                        .map(this::transferSale)
                        .collect(Collectors.toList());
        saleDtos.forEach(saleDto -> {
            List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findByBelongOrder_SaleId(saleDto.getSaleId(), PageRequest.of(0, 200));
            List<SaleDetailDto> saleDetailDtos = saleOrderDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            saleDto.setSaleDetails(saleDetailDtos);
        });
        return saleDtos;
    }

    /**
     * 根据用户名与状态查询
     *
     * @param userName  用户名
     * @param saleState 销售订单状态
     * @param pageable  分页参数
     * @return List<SaleDto>
     */
    @Override
    public List<SaleDto> getSalesByUserNameAndState(String userName, SaleOrder.SaleOrderState saleState, Pageable pageable) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("用户名不能为空");
        }
        if (Objects.equals(saleState, null)) {
            throw new ParameterException("销售订单状态不能为空");
        }
        List<SaleOrder> saleOrders = saleOrderRepository.findBySaleUser_UserNameAndSaleState(userName, saleState, pageable);
        List<SaleDto> saleDtos =
                saleOrders.stream()
                        .map(this::transferSale)
                        .collect(Collectors.toList());
        saleDtos.forEach(saleDto -> {
            List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findByBelongOrder_SaleId(saleDto.getSaleId(), PageRequest.of(0, 200));
            List<SaleDetailDto> saleDetailDtos = saleOrderDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            saleDto.setSaleDetails(saleDetailDtos);
        });
        return saleDtos;
    }

    /**
     * 根据销售订单主题查询
     *
     * @param saleSubject 销售订单主题
     * @param pageable    分页参数
     * @return List<SaleDto>
     */
    @Override
    public List<SaleDto> getSalesBySubject(String saleSubject, Pageable pageable) {
        if (!StringUtils.hasText(saleSubject)) {
            throw new ParameterException("销售订单主题不能为空");
        }
        List<SaleOrder> saleOrders = saleOrderRepository.findBySaleSubjectContaining(saleSubject, pageable);
        List<SaleDto> saleDtos =
                saleOrders.stream()
                        .map(this::transferSale)
                        .collect(Collectors.toList());
        saleDtos.forEach(saleDto -> {
            List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findByBelongOrder_SaleId(saleDto.getSaleId(), PageRequest.of(0, 200));
            List<SaleDetailDto> saleDetailDtos = saleOrderDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            saleDto.setSaleDetails(saleDetailDtos);
        });
        return saleDtos;
    }

    /**
     * 根据销售订单状态查询
     *
     * @param saleState 销售订单状态
     * @param pageable  分页参数
     * @return List<SaleDto>
     */
    @Override
    public List<SaleDto> getSalesByState(SaleOrder.SaleOrderState saleState, Pageable pageable) {
        if (Objects.equals(saleState, null)) {
            throw new ParameterException("销售订单状态不能为空");
        }
        List<SaleOrder> saleOrders = saleOrderRepository.findBySaleState(saleState, pageable);
        List<SaleDto> saleDtos =
                saleOrders.stream()
                        .map(this::transferSale)
                        .collect(Collectors.toList());
        saleDtos.forEach(saleDto -> {
            List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findByBelongOrder_SaleId(saleDto.getSaleId(), PageRequest.of(0, 200));
            List<SaleDetailDto> saleDetailDtos = saleOrderDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            saleDto.setSaleDetails(saleDetailDtos);
        });
        return saleDtos;
    }

    /**
     * 根据销售订单创建时间查询
     *
     * @param leftTime  左区间
     * @param rightTime 右区间
     * @param pageable  分页参数
     * @return List<SaleDto>
     */
    @Override
    public List<SaleDto> getSalesByCreateTime(Date leftTime, Date rightTime, Pageable pageable) {
        if (Objects.equals(leftTime, null) || Objects.equals(rightTime, null)) {
            throw new ParameterException("销售订单创建时间不能为空");
        }
        List<SaleOrder> saleOrders = saleOrderRepository.findBySaleCreateTimeBetween(leftTime, rightTime, pageable);
        List<SaleDto> saleDtos =
                saleOrders.stream()
                        .map(this::transferSale)
                        .collect(Collectors.toList());
        saleDtos.forEach(saleDto -> {
            List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findByBelongOrder_SaleId(saleDto.getSaleId(), PageRequest.of(0, 200));
            List<SaleDetailDto> saleDetailDtos = saleOrderDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            saleDto.setSaleDetails(saleDetailDtos);
        });
        return saleDtos;
    }

    /**
     * 根据销售订单上一次修改时间查询
     *
     * @param leftTime  左区间
     * @param rightTime 右区间
     * @param pageable  分页参数
     * @return List<SaleDto>
     */
    @Override
    public List<SaleDto> getSalesByModifiedTime(Date leftTime, Date rightTime, Pageable pageable) {
        if (Objects.equals(leftTime, null) || Objects.equals(rightTime, null)) {
            throw new ParameterException("销售订单上一次修改时间不能为空");
        }
        List<SaleOrder> saleOrders = saleOrderRepository.findByLastModifiedTimeBetween(leftTime, rightTime, pageable);
        List<SaleDto> saleDtos =
                saleOrders.stream()
                        .map(this::transferSale)
                        .collect(Collectors.toList());
        saleDtos.forEach(saleDto -> {
            List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findByBelongOrder_SaleId(saleDto.getSaleId(), PageRequest.of(0, 200));
            List<SaleDetailDto> saleDetailDtos = saleOrderDetails.stream().map(this::transferDetail).collect(Collectors.toList());
            saleDto.setSaleDetails(saleDetailDtos);
        });
        return saleDtos;
    }

    /**
     * 导出指定用户的销售订单
     *
     * @param userName 用户名
     * @param pageable 分页参数
     * @return File
     */
    @Override
    public File createSpecExcelFile(String userName, Pageable pageable) {
        List<SaleDto> saleDtos = getSalesByUserName(userName, pageable);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("编号");
        rowInfo.createCell(++columnIndex).setCellValue("主题");
        rowInfo.createCell(++columnIndex).setCellValue("销售员");
        rowInfo.createCell(++columnIndex).setCellValue("创建时间");
        rowInfo.createCell(++columnIndex).setCellValue("结算方式");
        rowInfo.createCell(++columnIndex).setCellValue("状态");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改时间");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改操作者");
        rowInfo.createCell(++columnIndex).setCellValue("总金额");
        rowInfo.createCell(++columnIndex).setCellValue("销售订单子项数量");
        rowInfo.createCell(++columnIndex).setCellValue("客户编号");
        rowInfo.createCell(++columnIndex).setCellValue("客户名");
        rowInfo.createCell(++columnIndex).setCellValue("客户地址");
        rowInfo.createCell(++columnIndex).setCellValue("客户联系方式");
        rowInfo.createCell(++columnIndex).setCellValue("客户邮箱");

        for (int i = 0; i < saleDtos.size(); i++) {
            SaleDto saleDto = saleDtos.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(saleDto.getSaleId());
            row.getCell(++columnIndex).setCellValue(saleDto.getSaleSubject());
            row.getCell(++columnIndex).setCellValue(Objects.equals(saleDto.getApplicant(), null) ? "-" : saleDto.getApplicant().getUserName());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(saleDto.getSaleCreateTime()));
            row.getCell(++columnIndex).setCellValue(saleDto.getSalePayWay().getValue());
            row.getCell(++columnIndex).setCellValue(saleDto.getSaleState().getValue());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(saleDto.getLastModifiedTime()));
            row.getCell(++columnIndex).setCellValue(Objects.equals(saleDto.getOperator(), null) ? "-" : saleDto.getOperator().getUserName());
            row.getCell(++columnIndex).setCellValue(saleDto.getTotalAmount().toString());
            row.getCell(++columnIndex).setCellValue(Objects.equals(saleDto.getSaleDetails(), null) ? "-" : String.valueOf(saleDto.getSaleDetails().size()));
            row.getCell(++columnIndex).setCellValue(StringUtils.hasText(saleDto.getCustomerId()) ? saleDto.getCustomerId() : "-");
            row.getCell(++columnIndex).setCellValue(saleDto.getCustomerName());
            row.getCell(++columnIndex).setCellValue(saleDto.getCustomerAddress());
            row.getCell(++columnIndex).setCellValue(saleDto.getCustomerPhone());
            row.getCell(++columnIndex).setCellValue(saleDto.getCustomerEmail());
        }
        return FileUtils.createExcelFile(workbook, "erp_sales");
    }

    /**
     * 导出销售订单
     *
     * @param pageable 分页参数
     * @return File
     */
    @Override
    public File createExcelFile(Pageable pageable) {
        Page<SaleOrder> saleOrderPage = findAll(pageable);
        List<SaleOrder> saleOrders = saleOrderPage.getContent();
        if (Objects.equals(saleOrders, null)) {
            saleOrders = new ArrayList<>();
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("编号");
        rowInfo.createCell(++columnIndex).setCellValue("主题");
        rowInfo.createCell(++columnIndex).setCellValue("销售员");
        rowInfo.createCell(++columnIndex).setCellValue("创建时间");
        rowInfo.createCell(++columnIndex).setCellValue("结算方式");
        rowInfo.createCell(++columnIndex).setCellValue("状态");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改时间");
        rowInfo.createCell(++columnIndex).setCellValue("上一次修改操作者");
        rowInfo.createCell(++columnIndex).setCellValue("总金额");
        rowInfo.createCell(++columnIndex).setCellValue("客户编号");
        rowInfo.createCell(++columnIndex).setCellValue("客户名");
        rowInfo.createCell(++columnIndex).setCellValue("客户地址");
        rowInfo.createCell(++columnIndex).setCellValue("客户联系方式");
        rowInfo.createCell(++columnIndex).setCellValue("客户邮箱");

        for (int i = 0; i < saleOrders.size(); i++) {
            SaleOrder saleOrder = saleOrders.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(saleOrder.getSaleId());
            row.getCell(++columnIndex).setCellValue(saleOrder.getSaleSubject());
            row.getCell(++columnIndex).setCellValue(Objects.equals(saleOrder.getSaleUser(), null) ? "-" : saleOrder.getSaleUser().getUserName());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(saleOrder.getSaleCreateTime()));
            row.getCell(++columnIndex).setCellValue(saleOrder.getSalePayWay().getValue());
            row.getCell(++columnIndex).setCellValue(saleOrder.getSaleState().getValue());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(saleOrder.getLastModifiedTime()));
            row.getCell(++columnIndex).setCellValue(Objects.equals(saleOrder.getLastModifiedOperator(), null) ? "-" : saleOrder.getLastModifiedOperator().getUserName());
            row.getCell(++columnIndex).setCellValue(saleOrder.getTotalAmount().toString());
            row.getCell(++columnIndex).setCellValue(StringUtils.hasText(saleOrder.getCustomerId()) ? saleOrder.getCustomerId() : "-");
            row.getCell(++columnIndex).setCellValue(saleOrder.getCustomerName());
            row.getCell(++columnIndex).setCellValue(saleOrder.getCustomerAddress());
            row.getCell(++columnIndex).setCellValue(saleOrder.getCustomerPhone());
            row.getCell(++columnIndex).setCellValue(saleOrder.getCustomerEmail());
        }
        return FileUtils.createExcelFile(workbook, "erp_sales");
    }

    /**
     * 保存操作
     *
     * @param entity 需要保存的实体
     * @return T 返回保存的实体
     */
    @Override
    public SaleOrder save(SaleOrder entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的销售订单不能为空");
        }
        return saleOrderRepository.save(entity);
    }

    /**
     * 根据给定一批实体进行批量删除
     *
     * @param entities
     */
    @Override
    public void deleteAll(List<SaleOrder> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的销售订单不能为空");
        }
        // 需要同时删除销售订单子项
        entities.stream().map(SaleOrder::getSaleId).forEach(saleId -> deleteSaleDetail(saleId));
        saleOrderRepository.deleteAll(entities);
    }

    /**
     * 更新操作
     *
     * @param entity 需要更新的实体
     * @return 更新后的实体
     */
    @Override
    public SaleOrder update(SaleOrder entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的销售订单不能为空");
        }
        return saleOrderRepository.saveAndFlush(entity);
    }

    /**
     * 根据给定 ID 查询一个实体
     *
     * @param id 需要查询的实体 ID
     * @return 查询出来的实体
     */
    @Override
    public SaleOrder findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("销售订单编号不能为空");
        }
        return saleOrderRepository.findById(id).orElseThrow(() -> new EntityNotExistException("销售订单不存在"));
    }

    /**
     * 分页查询
     *
     * @param pageable 常用 Pageable 接口的实现类 PageRequest
     * @return 返回一个 Page 对象
     */
    @Override
    public Page<SaleOrder> findAll(Pageable pageable) {
        return saleOrderRepository.findAll(pageable);
    }

    /* ------------------------------ 销售订单详细项相关 ------------------------------- */

    /**
     * 修改销售订单子项的销售数量与金额
     *
     * @param saleDetailId 销售订单详细项编号
     * @param saleNumber   销售数量
     * @param saleMoney    销售金额
     * @return 是否修改成功
     */
    @Override
    public Boolean updateNumberAndMoney(String saleDetailId, Long saleNumber, Double saleMoney) {
        if (!StringUtils.hasText(saleDetailId)) {
            throw new ParameterException("销售订单子项编号不能为空");
        }
        if (Objects.equals(saleNumber, null) || saleNumber < 0) {
            throw new ParameterException("销售数量不能小于零");
        }
        if (Objects.equals(saleMoney, null) || saleMoney < 0) {
            throw new ParameterException("销售金额不能小于零");
        }
        return saleOrderDetailRepository.updateNumberAndMoney(saleDetailId, saleNumber, saleMoney) > 0;
    }

    /**
     * 根据销售订单编号删除销售订单子项
     *
     * @param saleId 销售订单编号
     */
    @Override
    public void deleteSaleDetail(String saleId) {
        if (!StringUtils.hasText(saleId)) {
            throw new ParameterException("销售订单编号不能为空");
        }
        saleOrderDetailRepository.deleteBySaleId(saleId);
    }

    /**
     * 导出指定销售订单的销售订单子项
     *
     * @param saleId   销售订单编号
     * @param pageable 分页参数
     * @return File
     */
    @Override
    public File createDetailExcelFile(String saleId, Pageable pageable) {
        List<SaleOrderDetail> saleOrderDetails = saleOrderDetailRepository.findByBelongOrder_SaleId(saleId, pageable);
        if (Objects.equals(saleOrderDetails, null)) {
            saleOrderDetails = new ArrayList<>();
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("编号");
        rowInfo.createCell(++columnIndex).setCellValue("销售数量");
        rowInfo.createCell(++columnIndex).setCellValue("金额");
        rowInfo.createCell(++columnIndex).setCellValue("产品名");

        for (int i = 0; i < saleOrderDetails.size(); i++) {
            SaleOrderDetail saleOrderDetail = saleOrderDetails.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(saleOrderDetail.getSaleDetailId());
            row.getCell(++columnIndex).setCellValue(saleOrderDetail.getSaleNumber().toString());
            row.getCell(++columnIndex).setCellValue(saleOrderDetail.getSaleMoney().toString());
            row.getCell(++columnIndex).setCellValue(Objects.equals(saleOrderDetail.getSaleProduct(), null) ? "-" : saleOrderDetail.getSaleProduct().getProductName());
        }
        return FileUtils.createExcelFile(workbook, "erp_sale_details");
    }
}
