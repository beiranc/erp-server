package com.beiran.core.material.service.impl;

import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.material.entity.Material;
import com.beiran.core.material.repository.MaterialRepository;
import com.beiran.core.material.service.MaterialService;
import com.beiran.core.system.entity.Dept;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * MaterialService 接口的实现类
 */
@Service("materialService")
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    @Override
    public List<Material> getMaterialsByCategory(String categoryId, Pageable pageable) {
        if (!StringUtils.hasText(categoryId)) {
            throw new ParameterException("物料分类编号不能为空");
        }
        return materialRepository.findByMaterialCategory_CategoryId(categoryId, pageable);
    }

    @Override
    public File createExcelFile(Pageable pageable) {
        Page<Material> materialPage = findAll(pageable);
        List<Material> materials = materialPage.getContent();
        if (Objects.equals(materials, null)) {
            materials = new ArrayList<>();
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("物料编号");
        rowInfo.createCell(++columnIndex).setCellValue("物料名称");
        rowInfo.createCell(++columnIndex).setCellValue("物料分类");
        rowInfo.createCell(++columnIndex).setCellValue("物料进货价");
        rowInfo.createCell(++columnIndex).setCellValue("物料规格");
        rowInfo.createCell(++columnIndex).setCellValue("物料制造商");
        rowInfo.createCell(++columnIndex).setCellValue("物料产地");

        for (int i = 0; i < materials.size(); i++) {
            Material material = materials.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(material.getMaterialId());
            row.getCell(++columnIndex).setCellValue(material.getMaterialName());
            row.getCell(++columnIndex).setCellValue(material.getMaterialCategory() == null ? "-" : material.getMaterialCategory().getCategoryName());
            row.getCell(++columnIndex).setCellValue(material.getMaterialInPrice().toString());
            row.getCell(++columnIndex).setCellValue(material.getMaterialSpecification());
            row.getCell(++columnIndex).setCellValue(material.getMaterialManufacturer());
            row.getCell(++columnIndex).setCellValue(material.getMaterialOrigin());
        }
        return FileUtils.createExcelFile(workbook, "erp_materials");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Material save(Material entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的物料不能为空");
        }
        // 无需判断是否已存在
        return materialRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Material> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的物料不能为空");
        }
        materialRepository.deleteAll(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Material update(Material entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的物料不能为空");
        }
        return materialRepository.saveAndFlush(entity);
    }

    @Override
    public Material findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("物料编号不能为空");
        }
        return materialRepository.findById(id).orElseThrow(() -> new EntityNotExistException("物料不存在"));
    }

    @Override
    public Page<Material> findAll(Pageable pageable) {
        return materialRepository.findAll(pageable);
    }
}
