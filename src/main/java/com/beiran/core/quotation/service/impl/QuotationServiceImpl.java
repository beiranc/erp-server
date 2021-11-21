package com.beiran.core.quotation.service.impl;

import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.quotation.entity.Quotation;
import com.beiran.core.quotation.repository.QuotationRepository;
import com.beiran.core.quotation.service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * QuotationService 实现类
 */
@Service("quotationService")
public class QuotationServiceImpl implements QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation save(Quotation entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的报价单不能为空");
        }
        return quotationRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Quotation> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的报价单不能为空");
        }
        quotationRepository.deleteAll(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation update(Quotation entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的报价单不能为空");
        }
        return quotationRepository.saveAndFlush(entity);
    }

    @Override
    public Quotation findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("报价单编号不能为空");
        }
        return quotationRepository.findById(id).orElseThrow(() -> new EntityNotExistException("报价单不存在"));
    }

    @Override
    public Page<Quotation> findAll(Pageable pageable) {
        return quotationRepository.findAll(pageable);
    }

    @Override
    public File createQuotationFile(String quotationId) {
        Quotation quotation = findById(quotationId);
        return createExcelFile(quotation);
    }

    /**
     * 根据给定的 Quotation 创建 Excel 文件
     * @param quotation Quotation
     * @return File
     */
    private File createExcelFile(Quotation quotation) {
        return FileUtils.createQuotationFile(quotation);
    }
}
