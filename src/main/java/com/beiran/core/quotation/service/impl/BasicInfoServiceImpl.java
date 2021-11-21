package com.beiran.core.quotation.service.impl;

import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.core.quotation.entity.BasicInfo;
import com.beiran.core.quotation.repository.BasicInfoRepository;
import com.beiran.core.quotation.service.BasicInfoService;
import com.beiran.core.quotation.vo.BasicInfoSearchVo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * BasicInfoService 实现类
 */
@Log
@Service("basicInfoService")
public class BasicInfoServiceImpl implements BasicInfoService {

    @Autowired
    private BasicInfoRepository basicInfoRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasicInfo save(BasicInfo entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的基本信息不能为空");
        }
        return basicInfoRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<BasicInfo> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的基本信息不能为空");
        }
        basicInfoRepository.deleteAll(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasicInfo update(BasicInfo entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的基本信息不能为空");
        }
        return basicInfoRepository.saveAndFlush(entity);
    }

    @Override
    public BasicInfo findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("基本信息编号不能为空");
        }
        return basicInfoRepository.findById(id).orElseThrow(() -> new EntityNotExistException("基本信息不存在"));
    }

    @Override
    public Page<BasicInfo> findAll(Pageable pageable) {
        return basicInfoRepository.findAll(pageable);
    }

    @Override
    public Page<BasicInfo> findByCondition(Pageable pageable, BasicInfoSearchVo basicInfoSearchVo) {
        if (!Objects.equals(basicInfoSearchVo, null)) {
            Specification<BasicInfo> specification = (root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = new LinkedList<>();
                // 型号
                if (StringUtils.hasText(basicInfoSearchVo.getModel())) {
                    predicates.add(criteriaBuilder.like(root.get("model"), basicInfoSearchVo.getModel()));
                }
                // 前叉
                if (StringUtils.hasText(basicInfoSearchVo.getFrontFork())) {
                    predicates.add(criteriaBuilder.like(root.get("frontFork"), basicInfoSearchVo.getFrontFork()));
                }
                // 显示器
                if (!Objects.equals(basicInfoSearchVo.getDisplay(), null)) {
                    predicates.add(criteriaBuilder.equal(root.get("display"), basicInfoSearchVo.getDisplay()));
                }
                // 油门转把
                if (!Objects.equals(basicInfoSearchVo.getThrottle(), null)) {
                    predicates.add(criteriaBuilder.equal(root.get("throttle"), basicInfoSearchVo.getThrottle()));
                }
                // 变速器
                if (StringUtils.hasText(basicInfoSearchVo.getDerailleur())) {
                    predicates.add(criteriaBuilder.like(root.get("derailleur"), basicInfoSearchVo.getDerailleur()));
                }
                // 功率
                if (StringUtils.hasText(basicInfoSearchVo.getPower())) {
                    predicates.add(criteriaBuilder.like(root.get("power"), basicInfoSearchVo.getPower()));
                }
                // 电压
                if (StringUtils.hasText(basicInfoSearchVo.getVoltage())) {
                    predicates.add(criteriaBuilder.like(root.get("voltage"), basicInfoSearchVo.getVoltage()));
                }
                // 轮胎尺寸
                if (StringUtils.hasText(basicInfoSearchVo.getWheelSize())) {
                    predicates.add(criteriaBuilder.like(root.get("wheelSize"), basicInfoSearchVo.getWheelSize()));
                }
                // 车架材质
                if (StringUtils.hasText(basicInfoSearchVo.getFrame())) {
                    predicates.add(criteriaBuilder.like(root.get("frame"), basicInfoSearchVo.getFrame()));
                }
                // 电池容量
                if (StringUtils.hasText(basicInfoSearchVo.getBatteryCapacity())) {
                    predicates.add(criteriaBuilder.like(root.get("batteryCapacity"), basicInfoSearchVo.getBatteryCapacity()));
                }
                // 制动系统/刹车
                if (!Objects.equals(basicInfoSearchVo.getBrakeSystem(), null)) {
                    predicates.add(criteriaBuilder.equal(root.get("brakeSystem"), basicInfoSearchVo.getBrakeSystem()));
                }

                return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
            };
            return basicInfoRepository.findAll(specification, pageable);
        }
        return basicInfoRepository.findAll(pageable);
    }
}
