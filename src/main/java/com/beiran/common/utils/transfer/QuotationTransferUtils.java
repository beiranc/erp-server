package com.beiran.common.utils.transfer;

import com.beiran.core.quotation.entity.BasicInfo;
import com.beiran.core.quotation.entity.Quotation;
import com.beiran.core.quotation.entity.Salesman;
import com.beiran.core.quotation.vo.QuotationVo;
import lombok.extern.java.Log;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 提供 QuotationVo-Quotation 的转换工具
 */
@Log
public class QuotationTransferUtils {

    /**
     * 从 QuotationVo 转换为 Quotation
     * @param quotationVo 待转化的 QuotationVo
     * @param rate 当前汇率, 可通过 RateUtils 获取
     * @return Quotation
     */
    public static Quotation voToQuotation(QuotationVo quotationVo, String rate) {
        Quotation quotation = new Quotation();
        if (!Objects.equals(quotationVo, null)) {
            BeanUtils.copyProperties(quotationVo, quotation);
        }
        // 业务员信息
        Salesman salesman = new Salesman();
        salesman.setSalesmanId(Long.valueOf(quotationVo.getSalesmanId()));
        quotation.setSalesman(salesman);
        // 实时汇率
        quotation.setRealTimeRate(rate);
        // 有效期
        Date validPeriod = DateUtils.addDays(new Date(), 30);
        quotation.setValidPeriod(validPeriod);
        // 基本信息
        Set<BasicInfo> basicInfos = quotationVo.getBasicInfos().stream().map(basicInfoId -> {
            BasicInfo basicInfo = new BasicInfo();
            basicInfo.setBasicInfoId(basicInfoId);
            return basicInfo;
        }).collect(Collectors.toSet());
        quotation.setBasicInfos(basicInfos);
        return quotation;
    }
}
