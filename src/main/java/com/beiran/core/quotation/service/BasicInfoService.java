package com.beiran.core.quotation.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.quotation.entity.BasicInfo;
import com.beiran.core.quotation.vo.BasicInfoSearchVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * BasicInfoService 接口
 */
public interface BasicInfoService extends GenericService<BasicInfo, String> {
    /**
     * 根据查询条件查询
     * @param pageable 分页条件
     * @param basicInfoSearchVo 查询条件
     * @return Page<BasicInfo>
     */
    Page<BasicInfo> findByCondition(Pageable pageable, BasicInfoSearchVo basicInfoSearchVo);
}
