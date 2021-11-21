package com.beiran.core.quotation.service.impl;

import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.core.quotation.entity.Quotation;
import com.beiran.core.quotation.entity.Salesman;
import com.beiran.core.quotation.repository.QuotationRepository;
import com.beiran.core.quotation.repository.SalesmanRepository;
import com.beiran.core.quotation.service.SalesmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * SalesmanService 实现类
 */
@Service("salesmanService")
public class SalesmanServiceImpl implements SalesmanService {

    @Autowired
    private SalesmanRepository salesmanRepository;

    @Autowired
    private QuotationRepository quotationRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Salesman save(Salesman entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的业务员信息不能为空");
        }
        return salesmanRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Salesman> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的业务员不能为空");
        }
        List<Quotation> quotationList = quotationRepository.findAll();
        // FIXME 可能会出问题
        if (!CollectionUtils.isEmpty(quotationList)) {
            quotationList.forEach(quotation -> {
                Long salesmanId = quotation.getSalesman().getSalesmanId();
                entities.forEach(e -> {
                    if (salesmanId.compareTo(e.getSalesmanId()) == 0) {
                        entities.remove(e);
                    }
                });
            });
        }
        salesmanRepository.deleteAll(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Salesman update(Salesman entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的业务员不能为空");
        }
        return salesmanRepository.saveAndFlush(entity);
    }

    @Override
    public Salesman findById(Long id) {
        if (Objects.equals(id, null) || !StringUtils.hasText(id.toString())) {
            throw new ParameterException("业务员编号不能为空");
        }
        return salesmanRepository.findById(id).orElseThrow(() -> new EntityNotExistException("业务员不存在"));
    }

    @Override
    public Page<Salesman> findAll(Pageable pageable) {
        return salesmanRepository.findAll(pageable);
    }
}
