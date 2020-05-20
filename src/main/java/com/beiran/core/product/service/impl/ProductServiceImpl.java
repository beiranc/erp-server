package com.beiran.core.product.service.impl;

import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.product.entity.Product;
import com.beiran.core.product.repository.ProductRepository;
import com.beiran.core.product.service.ProductService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ProductService 实现类
 */

@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getProductsByCategory(String categoryId, Pageable pageable) {
        if (!StringUtils.hasText(categoryId)) {
            throw new ParameterException("产品分类编号不能为空");
        }
        return productRepository.findByProductCategory_CategoryId(categoryId, pageable);
    }

    @Override
    public File createExcelFile(Pageable pageable) {
        Page<Product> productPage = findAll(pageable);
        List<Product> products = productPage.getContent();
        if (Objects.equals(products, null)) {
            products = new ArrayList<>();
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("产品编号");
        rowInfo.createCell(++columnIndex).setCellValue("产品名称");
        rowInfo.createCell(++columnIndex).setCellValue("产品分类");
        rowInfo.createCell(++columnIndex).setCellValue("产品进货价");
        rowInfo.createCell(++columnIndex).setCellValue("产品出售价");
        rowInfo.createCell(++columnIndex).setCellValue("产品规格");
        rowInfo.createCell(++columnIndex).setCellValue("产品制造商");
        rowInfo.createCell(++columnIndex).setCellValue("产品产地");

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(product.getProductId());
            row.getCell(++columnIndex).setCellValue(product.getProductName());
            row.getCell(++columnIndex).setCellValue(product.getProductCategory() == null ? "-" : product.getProductCategory().getCategoryName());
            row.getCell(++columnIndex).setCellValue(product.getProductInPrice().toString());
            row.getCell(++columnIndex).setCellValue(product.getProductOutPrice().toString());
            row.getCell(++columnIndex).setCellValue(product.getProductSpecification());
            row.getCell(++columnIndex).setCellValue(product.getProductManufacturer());
            row.getCell(++columnIndex).setCellValue(product.getProductOrigin());
        }
        return FileUtils.createExcelFile(workbook, "erp_products");
    }

    @Override
    public Product save(Product entity) {
        // 产品新增无需判断是否已存在
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要保存的产品不能为空");
        }
        return productRepository.save(entity);
    }

    @Override
    public void deleteAll(List<Product> entities) {
        if (Objects.equals(entities, null) || entities.isEmpty()) {
            throw new ParameterException("需要删除的产品不能为空");
        }
        productRepository.deleteAll(entities);
    }

    @Override
    public Product update(Product entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("需要修改的产品不能为空");
        }
        return productRepository.saveAndFlush(entity);
    }

    @Override
    public Product findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("产品编号不能为空");
        }
        return productRepository.findById(id).orElseThrow(() -> new EntityNotExistException("产品不存在"));
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}
