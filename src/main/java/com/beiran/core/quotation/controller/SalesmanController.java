package com.beiran.core.quotation.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.transfer.SalesmanTransferUtils;
import com.beiran.core.quotation.entity.Salesman;
import com.beiran.core.quotation.service.SalesmanService;
import com.beiran.core.quotation.vo.SalesmanVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 业务员相关接口<br>
 */
@RestController
@RequestMapping("/api/v1/salesman")
@Api(tags = "报价单管理：业务员管理")
public class SalesmanController {

    @Autowired
    private SalesmanService salesmanService;

    @PostMapping
    @LogRecord("创建业务员")
    @ApiOperation("创建业务员")
    public ResponseModel saveSalesman(@RequestBody @Valid SalesmanVo salesmanVo) {
        return ResponseModel.ok(salesmanService.save(SalesmanTransferUtils.voToSalesman(salesmanVo)));
    }

    @PutMapping
    @LogRecord("修改业务员")
    @ApiOperation("修改业务员")
    public ResponseModel updateSalesman(@RequestBody @Valid Salesman salesman) {
        return ResponseModel.ok(salesmanService.update(salesman));
    }

    @DeleteMapping
    @LogRecord("删除业务员")
    @ApiOperation("删除业务员")
    public ResponseModel deleteSalesman(@RequestBody List<String> salesmanIds) {
        if (Objects.equals(salesmanIds, null) || salesmanIds.isEmpty()) {
            return ResponseModel.error("需要删除的业务员不能为空");
        }
        List<Salesman> salesmanList = salesmanIds.stream().map(salesmanId -> {
            Salesman salesman = new Salesman();
            salesman.setSalesmanId(Long.valueOf(salesmanId));
            return salesman;
        }).collect(Collectors.toList());
        salesmanService.deleteAll(salesmanList);
        return ResponseModel.ok("删除成功");
    }

    @GetMapping("/{id}")
    @LogRecord("查询一个业务员")
    @ApiOperation("查询一个业务员")
    public ResponseModel getSalesmanById(@PathVariable("id") String salesmanId) {
        return ResponseModel.ok(salesmanService.findById(Long.valueOf(salesmanId)));
    }

    @GetMapping
    @LogRecord("查询所有业务员")
    @ApiOperation("查询所有业务员")
    public ResponseModel getSalesmanList(@PageableDefault(sort = "salesmanCreateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseModel.ok(salesmanService.findAll(pageable));
    }
}
