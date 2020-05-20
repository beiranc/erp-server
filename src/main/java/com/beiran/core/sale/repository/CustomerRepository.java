package com.beiran.core.sale.repository;

import com.beiran.core.sale.entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * 客户 Repository
 */
public interface CustomerRepository extends JpaRepository<Customer, String>, JpaSpecificationExecutor<Customer> {

    /**
     * 根据客户名模糊查询
     * @param customerName 客户名
     * @param pageable 分页参数
     * @return List<Customer>
     */
    List<Customer> findByCustomerNameContaining(String customerName, Pageable pageable);

    /**
     * 根据客户创建时间查询
     * @param leftTime 左区间
     * @param rightTime 右区间
     * @param pageable 分页参数
     * @return List<Customer>
     */
    List<Customer> findByCustomerCreateTimeBetween(Date leftTime, Date rightTime, Pageable pageable);
}
