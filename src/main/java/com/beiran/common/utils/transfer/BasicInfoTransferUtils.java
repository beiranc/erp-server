package com.beiran.common.utils.transfer;

import com.beiran.core.quotation.entity.BasicInfo;
import com.beiran.core.quotation.vo.BasicInfoVo;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

/**
 * 提供 BasicInfoVo-BasicInfo 的转换工具
 */
@Log
public class BasicInfoTransferUtils {

    /**
     * 将 BasicInfoVo 转换为 BasicInfo
     * @param basicInfoVo 待转换的 BasicInfoVo
     * @return BasicInfo
     */
    public static BasicInfo voToBasicInfo(BasicInfoVo basicInfoVo) {
        BasicInfo basicInfo = new BasicInfo();
        if (!Objects.equals(basicInfoVo, null)) {
            BeanUtils.copyProperties(basicInfoVo, basicInfo);
        }
        return basicInfo;
    }
}
