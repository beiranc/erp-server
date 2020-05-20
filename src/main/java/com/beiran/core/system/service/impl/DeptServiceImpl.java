package com.beiran.core.system.service.impl;

import com.beiran.common.exception.EntityExistException;
import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.DateTimeUtils;
import com.beiran.common.utils.FileUtils;
import com.beiran.common.utils.transfer.DeptTransferUtils;
import com.beiran.core.system.dto.DeptDto;
import com.beiran.core.system.dto.DeptSmallDto;
import com.beiran.core.system.dto.DeptTree;
import com.beiran.core.system.entity.Dept;
import com.beiran.core.system.repository.DeptRepository;
import com.beiran.core.system.service.DeptService;
import com.beiran.core.system.vo.DeptVo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service("deptService")
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptRepository deptRepository;

    @Override
    public DeptDto createDept(DeptVo deptVo) {
        if (Objects.equals(deptVo, null)) {
            throw new ParameterException("需要保存的部门不能为空");
        }
        Dept dept = DeptTransferUtils.voToDept(deptVo);
        Dept save = save(dept);
        if (!Objects.equals(save, null)) {
            return DeptTransferUtils.deptToDto(save);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateState(String deptId, Dept.DeptState deptState) {
        if (!StringUtils.hasText(deptId)) {
            throw new ParameterException("部门编号不能为空");
        }
        Dept dept = findById(deptId);
        dept.setDeptState(deptState);
        Dept update = update(dept);
        return !Objects.equals(update, null);
    }

    @Override
    public DeptDto getDeptByName(String deptName) {
        if (!StringUtils.hasText(deptName)) {
            throw new ParameterException("部门名不能为空");
        }
        // 由调用方自行判断
        Dept dept = deptRepository.findByDeptName(deptName).orElse(null);
        if (Objects.equals(dept, null)) {
            return null;
        } else {
            return DeptTransferUtils.deptToDto(dept);
        }
    }

    @Override
    public List<DeptDto> getDeptsByName(String deptName) {
        if (!StringUtils.hasText(deptName)) {
            throw new ParameterException("部门名不能为空");
        }
        List<Dept> depts = deptRepository.findByDeptNameContaining(deptName);
        List<DeptDto> deptDtos =
                depts.stream()
                        .map(dept -> DeptTransferUtils.deptToDto(dept))
                        .collect(Collectors.toList());
        return deptDtos;
    }

    @Override
    public List<DeptDto> getDeptsByState(Dept.DeptState deptState, Pageable pageable) {
        if (Objects.equals(deptState, null)) {
            throw new ParameterException("部门状态不能为空");
        }
        List<Dept> depts = deptRepository.findByDeptState(deptState, pageable);
        List<DeptDto> deptDtos =
                depts.stream()
                        .map(dept -> DeptTransferUtils.deptToDto(dept))
                        .collect(Collectors.toList());
        return deptDtos;
    }

    @Override
    public List<DeptTree> getDeptTree() {
        // FIXME 目前只支持两层
        List<Dept> parent = deptRepository.findByDeptParentIsNull();
        List<Dept> others = deptRepository.findByDeptParentIsNotNull();
        List<DeptTree> trees = new ArrayList<>();

        if ((!Objects.equals(parent, null) && !parent.isEmpty()) || (!Objects.equals(others, null) && !others.isEmpty())) {
            parent.stream().forEach(dept -> {
                // 顶级部门
                DeptTree parentNode = new DeptTree();
                BeanUtils.copyProperties(dept, parentNode);
                parentNode.setCreateTime(DateTimeUtils.getDateTime(dept.getDeptCreateTime()));
                Set<DeptSmallDto> children = new HashSet<>();
                // 遍历所有的子部门，若有父级 id 等于当前的顶级部门 id 的，则加入 children 里
                others.stream().forEach(child -> {
                    if (Objects.equals(child.getDeptParent().getDeptId(), dept.getDeptId())) {
                        DeptSmallDto deptSmallDto = new DeptSmallDto();
                        deptSmallDto.setDeptId(child.getDeptId());
                        deptSmallDto.setDeptName(child.getDeptName());
                        children.add(deptSmallDto);
                    }
                });
                parentNode.setChildren(children);
                trees.add(parentNode);
            });
        }
        return trees;
    }

    @Override
    public File createExcelFile(Pageable pageable) {
        // 根据分页信息获取数据
        Page<Dept> deptPage = findAll(pageable);
        List<Dept> depts = deptPage.getContent();
        if (Objects.equals(depts, null)) {
            depts = new ArrayList<>();
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("部门编号");
        rowInfo.createCell(++columnIndex).setCellValue("部门名称");
        rowInfo.createCell(++columnIndex).setCellValue("部门状态");
        rowInfo.createCell(++columnIndex).setCellValue("上级部门");

        for (int i = 0; i < depts.size(); i++) {
            Dept dept = depts.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(dept.getDeptId());
            row.getCell(++columnIndex).setCellValue(dept.getDeptName());
            row.getCell(++columnIndex).setCellValue(dept.getDeptState().getValue());
            if (!Objects.equals(dept.getDeptParent(), null)) {
                row.getCell(++columnIndex).setCellValue(dept.getDeptParent().getDeptName());
            } else {
                row.getCell(++columnIndex).setCellValue("无");
            }
        }
        return FileUtils.createExcelFile(workbook, "erp_dept");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Dept save(Dept entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的部门不能为空");
        }
        DeptDto deptDto = getDeptByName(entity.getDeptName());
        if (Objects.equals(deptDto, null)) {
            return deptRepository.save(entity);
        } else {
            throw new EntityExistException("部门已存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Dept> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的部门不能为空");
        }
        deptRepository.deleteAll(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Dept update(Dept entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的部门不能为空");
        }
        return deptRepository.saveAndFlush(entity);
    }

    @Override
    public Dept findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("部门编号不能为空");
        }
        return deptRepository.findById(id).orElseThrow(() -> new EntityNotExistException("部门不存在"));
    }

    @Override
    public Page<Dept> findAll(Pageable pageable) {
        return deptRepository.findAll(pageable);
    }
}
