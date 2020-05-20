package com.beiran.core.sale.service.impl;

import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.DateTimeUtils;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.sale.entity.Customer;
import com.beiran.core.sale.repository.CustomerRepository;
import com.beiran.core.sale.service.CustomerService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * CustomerService 接口实现类
 */

@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * 根据客户名模糊查询
     *
     * @param customerName 客户名
     * @param pageable     分页参数
     * @return List<Customer>
     */
    @Override
    public List<Customer> getCustomersByName(String customerName, Pageable pageable) {
        if (!StringUtils.hasText(customerName)) {
            throw new ParameterException("客户名称不能为空");
        }
        return customerRepository.findByCustomerNameContaining(customerName, pageable);
    }

    /**
     * 根据创建时间查询
     *
     * @param leftTime  左区间
     * @param rightTime 右区间
     * @param pageable  分页参数
     * @return List<Customer>
     */
    @Override
    public List<Customer> getCustomersByCreateTime(Date leftTime, Date rightTime, Pageable pageable) {
        if (Objects.equals(leftTime, null) && Objects.equals(rightTime, null)) {
            throw new ParameterException("客户创建时间不能为空");
        }
        return customerRepository.findByCustomerCreateTimeBetween(leftTime, rightTime, pageable);
    }

    /**
     * 导出客户信息
     *
     * @param pageable 分页参数
     * @return File
     */
    @Override
    public File createExcelFile(Pageable pageable) {
        Page<Customer> customerPage = findAll(pageable);
        List<Customer> customers = customerPage.getContent();
        if (Objects.equals(customers, null)) {
            customers = new ArrayList<>();
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("客户编号");
        rowInfo.createCell(++columnIndex).setCellValue("客户名称");
        rowInfo.createCell(++columnIndex).setCellValue("客户地址");
        rowInfo.createCell(++columnIndex).setCellValue("联系方式");
        rowInfo.createCell(++columnIndex).setCellValue("客户邮箱");
        rowInfo.createCell(++columnIndex).setCellValue("创建时间");

        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(customer.getCustomerId());
            row.getCell(++columnIndex).setCellValue(customer.getCustomerName());
            row.getCell(++columnIndex).setCellValue(customer.getCustomerAddress());
            row.getCell(++columnIndex).setCellValue(customer.getCustomerPhone());
            row.getCell(++columnIndex).setCellValue(customer.getCustomerEmail());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(customer.getCustomerCreateTime()));
        }
        return FileUtils.createExcelFile(workbook, "erp_customers");
    }

    /**
     * 保存操作
     *
     * @param entity 需要保存的实体
     * @return T 返回保存的实体
     */
    @Override
    public Customer save(Customer entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的客户不能为空");
        }
        return customerRepository.save(entity);
    }

    /**
     * 根据给定一批实体进行批量删除
     *
     * @param entities
     */
    @Override
    public void deleteAll(List<Customer> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的客户不能为空");
        }
        customerRepository.deleteAll(entities);
    }

    /**
     * 更新操作
     *
     * @param entity 需要更新的实体
     * @return 更新后的实体
     */
    @Override
    public Customer update(Customer entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的客户不能为空");
        }
        return customerRepository.saveAndFlush(entity);
    }

    /**
     * 根据给定 ID 查询一个实体
     *
     * @param id 需要查询的实体 ID
     * @return 查询出来的实体
     */
    @Override
    public Customer findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("客户编号不能为空");
        }
        return customerRepository.findById(id).orElseThrow(() -> new EntityNotExistException("客户不存在"));
    }

    /**
     * 分页查询
     *
     * @param pageable 常用 Pageable 接口的实现类 PageRequest
     * @return 返回一个 Page 对象
     */
    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }
}
