package com.beiran.core.sale.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.sale.entity.Customer;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * CustomerService 接口
 */

public interface CustomerService extends GenericService<Customer, String> {

    /**
     * 根据客户名模糊查询
     * @param customerName 客户名
     * @param pageable 分页参数
     * @return List<Customer>
     */
    List<Customer> getCustomersByName(String customerName, Pageable pageable);

    /**
     * 根据创建时间查询
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<Customer>
     */
    List<Customer> getCustomersByCreateTime(Date leftTime, Date rightTime, Pageable pageable);

    /**
     * 导出客户信息
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);
}
