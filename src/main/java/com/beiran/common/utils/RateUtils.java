package com.beiran.common.utils;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 获取汇率工具
 */
@Component
@RequiredArgsConstructor
public class RateUtils {

    private final RestTemplate restTemplate;

    @Value("${external.app-key}")
    private String appKey;

    @Value("${external.rate-url}")
    private String rateUrl;

    private String from = "USD";

    private String to = "CNY";

    private String amount = "1";

    public String getCurrentRate() {
        String finalUrl = rateUrl + "?appkey=" + appKey + "&from=" + from + "&to=" + to + "&amount=" + amount;
        RateResultEntity result = restTemplate.getForObject(finalUrl, RateResultEntity.class);
        Double rate = Double.valueOf(result.getResult().getRate());
        return String.format("%.2f", rate);
    }
}

@Data
class RateResultEntity {
    Integer status;
    String msg;
    InnerResult result;

    @Data
    static class InnerResult {
        String from;
        String to;
        String fromname;
        String toname;
        String updatetime;
        String rate;
        Double camount;
    }
}