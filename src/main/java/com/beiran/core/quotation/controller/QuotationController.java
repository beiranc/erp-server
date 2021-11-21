package com.beiran.core.quotation.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.common.utils.RateUtils;
import com.beiran.common.utils.transfer.QuotationTransferUtils;
import com.beiran.core.quotation.entity.Quotation;
import com.beiran.core.quotation.service.QuotationService;
import com.beiran.core.quotation.vo.QuotationVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 报价单接口
 */
@RestController
@RequestMapping("/api/v1/quotations")
@Api(tags = "报价单管理：报价单管理")
@RequiredArgsConstructor
public class QuotationController {

    @Value("${phoenix.chinese-name}")
    private String chineseName;

    @Value("${phoenix.english-name}")
    private String englishName;

    @Value("${phoenix.address}")
    private String address;

    @Value("${phoenix.official-website}")
    private String officialWebsite;

    private final RateUtils rateUtils;

    private final QuotationService quotationService;

    // 返回公共信息
    @GetMapping("/phoenix")
    @LogRecord("获取凤凰基本信息")
    @ApiOperation("获取凤凰基本信息")
    public ResponseModel getPhoenixInfo() {
        Map<String, String> infoMap = new HashMap<>();
        infoMap.put("chineseName", chineseName);
        infoMap.put("englishName", englishName);
        infoMap.put("address", address);
        infoMap.put("officialWebsite", officialWebsite);
        return ResponseModel.ok("获取成功", infoMap);
    }

    // 创建
    @PostMapping
    @LogRecord("创建报价单")
    @ApiOperation("创建报价单")
    public ResponseModel saveQuotation(@RequestBody @Valid QuotationVo quotationVo) {
        return ResponseModel.ok(quotationService.save(QuotationTransferUtils.voToQuotation(quotationVo, rateUtils.getCurrentRate())));
    }

    // 暂不提供修改

    // 删除
    @DeleteMapping
    @LogRecord("删除报价单")
    @ApiOperation("删除报价单")
    public ResponseModel deleteQuotation(@RequestBody List<String> quotationIds) {
        if (Objects.equals(quotationIds, null) || quotationIds.isEmpty()) {
            return ResponseModel.error("需要删除的报价单不能为空");
        }
        List<Quotation> quotations = quotationIds.stream().map(quotationId -> {
            Quotation quotation = new Quotation();
            quotation.setQuotationId(quotationId);
            return quotation;
        }).collect(Collectors.toList());
        quotationService.deleteAll(quotations);
        return ResponseModel.ok("删除成功");
    }

    // 查询一个
    @GetMapping("/{id}")
    @LogRecord("查询一个报价单")
    @ApiOperation("查询一个报价单")
    public ResponseModel getQuotationById(@PathVariable("id") String quotationId) {
        return ResponseModel.ok(quotationService.findById(quotationId));
    }

    // 查询所有 generateDate
    @GetMapping
    @LogRecord("查询所有报价单")
    @ApiOperation("查询所有报价单")
    public ResponseModel getQuotations(@PageableDefault(sort = "generateDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseModel.ok(quotationService.findAll(pageable));
    }

    // 导出一个
    @GetMapping("/export/{id}")
    @LogRecord("导出一个报价单")
    @ApiOperation("导出一个报价单")
    public void exportQuotationById(@PathVariable("id") String quotationId,
                           HttpServletResponse response) throws Exception {
        File file = quotationService.createQuotationFile(quotationId);
        FileUtils.downloadFile(response, file, file.getName());
    }
}
