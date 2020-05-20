package com.beiran.core.material.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.material.entity.Material;
import com.beiran.core.material.entity.MaterialCategory;
import com.beiran.core.material.service.MaterialCategoryService;
import com.beiran.core.material.service.MaterialService;
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
 * 物料接口<br>
 * 创建物料权限: material:add （创建物料需要 purchaser 或 purchaseManager）<br>
 * 修改物料权限: material:edit<br>
 * 查询物料权限: material:view （purchaser 和 purchaseManager 也有权限查看物料）<br>
 * 删除物料权限: material:del<br>
 * 导出物料权限: material:export<br>
 *
 * 注: 物料分类接口与物料使用相同的权限<br>
 */

@RestController
@RequestMapping("/api/v1/materials")
@Api(tags = "物料管理")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialCategoryService materialCategoryService;

    /* -------------------------------------- 物料相关 --------------------------------------- */

    /**
     * 创建物料
     * @param material
     * @return
     */
    @PostMapping
    @LogRecord("创建物料")
    @PreAuthorize("@erp.check('purchaser') or @erp.check('purchaseManager') or @erp.check('material:add')")
    @ApiOperation("创建物料")
    public ResponseModel saveMaterial(@RequestBody @Valid Material material) {
        return ResponseModel.ok(materialService.save(material));
    }

    /**
     * 修改物料
     * @param material
     * @return
     */
    @PutMapping
    @LogRecord("修改物料")
    @PreAuthorize("@erp.check('material:edit')")
    @ApiOperation("修改物料")
    public ResponseModel updateMaterial(@RequestBody @Valid Material material) {
        return ResponseModel.ok(materialService.update(material));
    }

    /**
     * 删除物料
     * @param materialIds
     * @return
     */
    @DeleteMapping
    @LogRecord("删除物料")
    @PreAuthorize("@erp.check('material:del')")
    @ApiOperation("删除物料")
    public ResponseModel deleteMaterial(@RequestBody List<String> materialIds) {
        if (Objects.equals(materialIds, null) || materialIds.isEmpty()) {
            return ResponseModel.error("需要删除的物料不能为空");
        }
        List<Material> materials = materialIds.stream().map(materialId -> {
            Material material = new Material();
            material.setMaterialId(materialId);
            return material;
        }).collect(Collectors.toList());
        materialService.deleteAll(materials);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 根据物料编号查询一个物料
     * @param materialId
     * @return
     */
    @GetMapping("/{id}")
    @LogRecord("查询一个物料信息")
    @PreAuthorize("@erp.check('material:view')")
    @ApiOperation("查询一个物料信息")
    public ResponseModel getMaterialById(@PathVariable("id") String materialId) {
        return ResponseModel.ok(materialService.findById(materialId));
    }

    /**
     * 查询所有物料
     * @param pageable
     * @return
     */
    @GetMapping
    @LogRecord("查询所有物料")
    @PreAuthorize("@erp.check('material:view')")
    @ApiOperation("查询所有物料")
    public ResponseModel getMaterials(@PageableDefault(sort = "materialCategory") Pageable pageable) {
        return ResponseModel.ok(materialService.findAll(pageable));
    }

    /**
     * 根据物料分类查询物料
     * @param categoryId
     * @param pageable
     * @return
     */
    @GetMapping("/category")
    @LogRecord("根据物料分类查询物料")
    @PreAuthorize("@erp.check('material:view')")
    @ApiOperation("根据物料分类查询物料")
    public ResponseModel getMaterialsByCategory(@RequestParam("categoryId") String categoryId,
                                                @PageableDefault Pageable pageable) {
        return ResponseModel.ok(materialService.getMaterialsByCategory(categoryId, pageable));
    }

    /**
     * 导出物料
     * @param pageable
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出物料")
    @PreAuthorize("@erp.check('material:export')")
    @ApiOperation("导出物料")
    public void export(@PageableDefault(size = 200, sort = "materialCategory") Pageable pageable,
                       HttpServletResponse response) throws Exception {
        File file = materialService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /* -------------------------------------- 物料分类相关 ------------------------------------- */

    /**
     * 创建物料分类
     * @param materialCategory
     * @return
     */
    @PostMapping("/categories")
    @LogRecord("创建物料分类")
    @PreAuthorize("@erp.check('material:add')")
    @ApiOperation("创建物料分类")
    public ResponseModel saveMaterialCategory(@RequestBody @Valid MaterialCategory materialCategory) {
        return ResponseModel.ok(materialCategoryService.save(materialCategory));
    }

    /**
     * 修改物料分类
     * @param materialCategory
     * @return
     */
    @PutMapping("/categories")
    @LogRecord("修改物料分类")
    @PreAuthorize("@erp.check('material:edit')")
    @ApiOperation("修改物料分类")
    public ResponseModel updateMaterialCategory(@RequestBody @Valid MaterialCategory materialCategory) {
        return ResponseModel.ok(materialCategoryService.update(materialCategory));
    }

    /**
     * 删除物料分类
     * @param categoryIds
     * @return
     */
    @DeleteMapping("/categories")
    @LogRecord("删除物料分类")
    @PreAuthorize("@erp.check('material:del')")
    @ApiOperation("删除物料分类")
    public ResponseModel deleteMaterialCategory(@RequestBody List<String> categoryIds) {
        if (Objects.equals(categoryIds, null) || categoryIds.isEmpty()) {
            return ResponseModel.error("需要删除的物料分类不能为空");
        }
        List<MaterialCategory> materialCategories = categoryIds.stream().map(categoryId -> {
            MaterialCategory materialCategory = new MaterialCategory();
            materialCategory.setCategoryId(categoryId);
            return materialCategory;
        }).collect(Collectors.toList());
        materialCategoryService.deleteAll(materialCategories);
        return ResponseModel.ok("删除成功");
    }

    /**
     * 查询物料分类
     * @param pageable
     * @return
     */
    @GetMapping("/categories")
    @LogRecord("查询物料分类")
    @PreAuthorize("@erp.check('material:view')")
    @ApiOperation("查询物料分类")
    public ResponseModel getMaterialCategories(@PageableDefault Pageable pageable) {
        return ResponseModel.ok(materialCategoryService.findAll(pageable));
    }
}
