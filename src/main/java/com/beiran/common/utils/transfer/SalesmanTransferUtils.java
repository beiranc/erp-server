package com.beiran.common.utils.transfer;

import com.beiran.core.quotation.entity.Salesman;
import com.beiran.core.quotation.vo.SalesmanVo;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

/**
 * 提供 SalesmanVo-Salesman 的转换工具
 */
@Log
public class SalesmanTransferUtils {

    /**
     * 将 SalesmanVo 转换为 Salesman
     * @param salesmanVo 待转换的 SalesmanVo
     * @return Salesman
     */
    public static Salesman voToSalesman(SalesmanVo salesmanVo) {
        Salesman salesman = new Salesman();
        if (!Objects.equals(salesmanVo, null)) {
            BeanUtils.copyProperties(salesmanVo, salesman);
        }
        return salesman;
    }
}
