package com.beiran.core.sale.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.sale.entity.Customer;
import com.beiran.core.sale.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 客户接口<br>
 * 创建客户权限: cust:add<br>
 * 修改客户权限: cust:edit<br>
 * 删除客户权限: cust:del<br>
 * 查询客户权限: cust:view<br>
 * 导出客户权限: cust:export<br>
 */

@RestController
@RequestMapping("/api/v1/customers")
@Api(tags = "销售管理：客户管理")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 创建客户
     * @param customer
     * @return
     */
    @PostMapping
    @LogRecord("创建客户")
    @PreAuthorize("@erp.check('cust:add')")
    @ApiOperation("创建客户")
    public ResponseModel saveCustomer(@RequestBody Customer customer) {
        return ResponseModel.ok(customerService.save(customer));
    }

    /**
     * 修改客户
     * @param customer
     * @return
     */
    @PutMapping
    @LogRecord("修改客户")
    @PreAuthorize("@erp.check('cust:edit')")
    @ApiOperation("修改客户")
    public ResponseModel updateCustomer(@RequestBody Customer customer) {
        return ResponseModel.ok(customerService.update(customer));
    }

    /**
     * 删除客户
     * @param customerIds
     * @return
     */
    @DeleteMapping
    @LogRecord("删除客户")
    @PreAuthorize("@erp.check('cust:del')")
    @ApiOperation("删除客户")
    public ResponseModel deleteCustomer(@RequestBody List<String> customerIds) {
        if (Objects.equals(customerIds, null) || customerIds.isEmpty()) {
            return ResponseModel.error("需要删除的客户不能为空");
        }
        List<Customer> customers = customerIds.stream().map(customerId -> {
            Customer customer = new Customer();
            customer.setCustomerId(customerId);
            return customer;
        }).collect(Collectors.toList());
        customerService.deleteAll(customers);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 分页查询客户
     * @param pageable
     * @return
     */
    @GetMapping
    @LogRecord("分页查询客户")
    @PreAuthorize("@erp.check('cust:view')")
    @ApiOperation("分页查询客户")
    public ResponseModel getCustomers(@PageableDefault Pageable pageable) {
        return ResponseModel.ok(customerService.findAll(pageable));
    }

    /**
     * 通过客户名查询客户
     * @param customerName
     * @param pageable
     * @return
     */
    @GetMapping("/name")
    @LogRecord("通过客户名查询客户")
    @PreAuthorize("@erp.check('cust:view')")
    @ApiOperation("通过客户名查询客户")
    public ResponseModel getCustomersByName(@RequestParam("customerName") String customerName,
                                            @PageableDefault Pageable pageable) {
        return ResponseModel.ok(customerService.getCustomersByName(customerName, pageable));
    }

    /**
     * 通过创建时间查询客户
     * @param leftTime
     * @param rightTime
     * @param pageable
     * @return
     */
    @GetMapping("/create_time")
    @LogRecord("通过创建时间查询客户")
    @PreAuthorize("@erp.check('cust:view')")
    @ApiOperation("通过创建时间查询客户")
    public ResponseModel getCustomersByCreateTime(@RequestParam("leftTime") Date leftTime,
                                                  @RequestParam("rightTime") Date rightTime,
                                                  @PageableDefault Pageable pageable) {
        return ResponseModel.ok(customerService.getCustomersByCreateTime(leftTime, rightTime, pageable));
    }

    /**
     * 导出客户
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出客户")
    @PreAuthorize("@erp.check('cust:export')")
    @ApiOperation("导出客户")
    public void export(@PageableDefault(size = 200) Pageable pageable,
                       HttpServletResponse response) throws Exception {
        File file = customerService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }
}
