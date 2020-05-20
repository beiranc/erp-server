package com.beiran.core.product.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.product.entity.Product;
import com.beiran.core.product.entity.ProductCategory;
import com.beiran.core.product.service.ProductCategoryService;
import com.beiran.core.product.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 产品接口<br>
 * 添加产品权限: product:add （producer 和 produceManager 有权限添加产品）<br>
 * 修改产品权限: product:edit<br>
 * 删除产品权限: product:del<br>
 * 查询产品权限: product:view （producer 和 produceManager 也有权限查询产品）<br>
 * 导出产品权限: product:export<br>
 *
 * 注: 产品分类相关操作与产品权限一致<br>
 */

@RestController
@RequestMapping("/api/v1/products")
@Api(tags = "产品管理")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryService productCategoryService;

    /* -------------------------------------- 产品相关 --------------------------------------- */

    /**
     * 添加产品
     * @param product
     * @return
     */
    @PostMapping
    @LogRecord("添加产品")
    @PreAuthorize("@erp.check('producer') or @erp.check('produceManager') or @erp.check('product:add')")
    @ApiOperation("添加产品")
    public ResponseModel saveProduct(@RequestBody @Valid Product product) {
        return ResponseModel.ok(productService.save(product));
    }

    /**
     * 修改产品
     * @param product
     * @return
     */
    @PutMapping
    @LogRecord("修改产品")
    @PreAuthorize("@erp.check('product:edit')")
    @ApiOperation("修改产品")
    public ResponseModel updateProduct(@RequestBody @Valid Product product) {
        return ResponseModel.ok(productService.update(product));
    }

    /**
     * 删除产品
     * @param productIds
     * @return
     */
    @DeleteMapping
    @LogRecord("删除产品")
    @PreAuthorize("@erp.check('product:del')")
    @ApiOperation("删除产品")
    public ResponseModel deleteProduct(@RequestBody List<String> productIds) {
        if (Objects.equals(productIds, null) || productIds.isEmpty()) {
            return ResponseModel.error("需要删除的产品不能为空");
        }
        List<Product> products = productIds.stream().map(productId -> {
            Product product = new Product();
            product.setProductId(productId);
            return product;
        }).collect(Collectors.toList());
        productService.deleteAll(products);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 查询一个产品
     * @param productId
     * @return
     */
    @GetMapping("/{id}")
    @LogRecord("查询一个产品")
    @PreAuthorize("@erp.check('product:view')")
    @ApiOperation("查询一个产品")
    public ResponseModel getProductById(@PathVariable("id") String productId) {
        return ResponseModel.ok(productService.findById(productId));
    }

    /**
     * 查询所有产品
     * @param pageable
     * @return
     */
    @GetMapping
    @LogRecord("查询所有产品")
    @PreAuthorize("@erp.check('product:view')")
    @ApiOperation("查询所有产品")
    public ResponseModel getProducts(@PageableDefault(sort = "productCategory") Pageable pageable) {
        return ResponseModel.ok(productService.findAll(pageable));
    }

    /**
     * 根据产品分类查询产品
     * @param categoryId
     * @param pageable
     * @return
     */
    @GetMapping("/category")
    @LogRecord("根据产品分类查询产品")
    @PreAuthorize("@erp.check('product:view')")
    @ApiOperation("根据产品分类查询产品")
    public ResponseModel getProductsByCategory(@RequestParam("categoryId") String categoryId,
                                               @PageableDefault Pageable pageable) {
        return ResponseModel.ok(productService.getProductsByCategory(categoryId, pageable));
    }

    /**
     * 导出产品
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出产品")
    @PreAuthorize("@erp.check('product:export')")
    @ApiOperation("导出产品")
    public void export(@PageableDefault(sort = "productCategory", size = 200) Pageable pageable,
                       HttpServletResponse response) throws Exception {
        File file = productService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /* -------------------------------------- 产品分类相关 --------------------------------------- */

    /**
     * 添加产品分类
     * @param productCategory
     * @return
     */
    @PostMapping("/categories")
    @LogRecord("添加产品分类")
    @PreAuthorize("@erp.check('product:add')")
    @ApiOperation("添加产品分类")
    public ResponseModel saveProductCategory(@RequestBody @Valid ProductCategory productCategory) {
        return ResponseModel.ok(productCategoryService.save(productCategory));
    }

    /**
     * 修改产品分类
     * @param productCategory
     * @return
     */
    @PutMapping("/categories")
    @LogRecord("修改产品分类")
    @PreAuthorize("@erp.check('product:edit')")
    @ApiOperation("修改产品分类")
    public ResponseModel updateProductCategory(@RequestBody @Valid ProductCategory productCategory) {
        return ResponseModel.ok(productCategoryService.update(productCategory));
    }

    /**
     * 删除产品分类
     * @param categoryIds
     * @return
     */
    @DeleteMapping("/categories")
    @LogRecord("删除产品分类")
    @PreAuthorize("@erp.check('product:del')")
    @ApiOperation("删除产品分类")
    public ResponseModel deleteProductCategory(@RequestBody List<String> categoryIds) {
        if (Objects.equals(categoryIds, null) || categoryIds.isEmpty()) {
            return ResponseModel.error("需要删除的产品分类不能为空");
        }
        List<ProductCategory> categories = categoryIds.stream().map(categoryId -> {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setCategoryId(categoryId);
            return productCategory;
        }).collect(Collectors.toList());
        productCategoryService.deleteAll(categories);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 查询产品分类
     * @param pageable
     * @return
     */
    @GetMapping("/categories")
    @LogRecord("查询产品分类")
    @PreAuthorize("@erp.check('product:view')")
    @ApiOperation("查询产品分类")
    public ResponseModel getProductCategories(@PageableDefault Pageable pageable) {
        return ResponseModel.ok(productCategoryService.findAll(pageable));
    }
}
