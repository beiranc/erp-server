package com.beiran.core.quotation.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.transfer.BasicInfoTransferUtils;
import com.beiran.core.quotation.entity.BasicInfo;
import com.beiran.core.quotation.service.BasicInfoService;
import com.beiran.core.quotation.vo.BasicInfoSearchVo;
import com.beiran.core.quotation.vo.BasicInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 报价单的基本信息项相关接口<br>
 */
@Log
@RestController
@RequestMapping("/api/v1/basic_infos")
@Api(tags = "报价单管理：基本信息管理")
public class BasicInfoController {

    @Autowired
    private BasicInfoService basicInfoService;

    /**
     * 保存文件时直接存储绝对路径
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @LogRecord("上传主图")
    @ApiOperation("上传主图")
    public synchronized ResponseModel uploadImg(@RequestPart("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseModel.error(400, "上传的文件为空", null, null);
            }
            // 获取文件原本的名称
            String originFileName = file.getOriginalFilename();

            // 设置文件前缀路径(即程序同目录路径下的 /upload 目录)
            String destDir = new File("").getAbsolutePath();

            // 以当前日期为分类存储图片
            String dateDir = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());

            String addDestDir = File.separator + "upload" + File.separator + dateDir;

            // 生成图片保存的绝对路径
            destDir += addDestDir;

            // 如果待保存路径不存在则创建文件夹
            File destFile = new File(destDir);
            if (!destFile.exists()) {
                boolean isCreateSuccess = destFile.mkdirs();
                if (!isCreateSuccess) {
                    return ResponseModel.error("创建图片保存文件夹失败");
                }
            }

            // 获取文件后缀名
            int begin = originFileName.indexOf(".");
            int last = originFileName.length();
            String fileSuffixName = originFileName.substring(begin, last);

            // 新的文件名
            String dateFileName = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());

            String newFileName = dateFileName + fileSuffixName;

            // 完整路径的文件创建以及写入
            File finalFile = new File(destFile.getAbsolutePath() + File.separator + newFileName);
            file.transferTo(finalFile);
            String directory = finalFile.getCanonicalPath();

            return ResponseModel.ok("上传文件成功", directory);
        } catch (Exception e) {
            return ResponseModel.error("上传文件失败", e.getLocalizedMessage());
        }
    }

    /**
     * 预览图片时返回一个 Base64 编码格式的图片
     * @param path
     * @return
     */
    @GetMapping("/preview")
    @LogRecord("预览图片")
    @ApiOperation("预览图片")
    public ResponseModel previewImg(@RequestParam("path") String path, HttpServletResponse response) {
        if (StringUtils.isEmpty(path)) {
            return ResponseModel.error(400, "图片路径为空", null, null);
        }

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        // 输出 Base64 字符串
        String base64String = "";
        File file = new File(path);

        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            Base64.Encoder encoder = Base64.getEncoder();
            base64String = encoder.encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Note: url 为 Base64 编码后的图片
        return ResponseModel.ok(HttpStatus.OK.value(), null, null, "data:image/jpeg;base64," + base64String);
    }

    @PostMapping
    @LogRecord("创建基本信息")
    @ApiOperation("创建基本信息")
    public ResponseModel saveBasicInfo(@RequestBody @Valid BasicInfoVo basicInfoVo) {
        return ResponseModel.ok(basicInfoService.save(BasicInfoTransferUtils.voToBasicInfo(basicInfoVo)));
    }

    // 修改
    @PutMapping
    @LogRecord("修改基本信息")
    @ApiOperation("修改基本信息")
    public ResponseModel updateBasicInfo(@RequestBody @Valid BasicInfo basicInfo) {
        return ResponseModel.ok(basicInfoService.update(basicInfo));
    }

    // 删除
    @DeleteMapping
    @LogRecord("删除基本信息")
    @ApiOperation("删除基本信息")
    public ResponseModel deleteBasicInfo(@RequestBody List<String> basicInfoIds) {
        if (Objects.equals(basicInfoIds, null) || basicInfoIds.isEmpty()) {
            return ResponseModel.error("需要删除的基本信息不能为空");
        }
        List<BasicInfo> basicInfos = basicInfoIds.stream().map(basicInfoId -> {
            BasicInfo basicInfo = new BasicInfo();
            basicInfo.setBasicInfoId(basicInfoId);
            return basicInfo;
        }).collect(Collectors.toList());
        basicInfoService.deleteAll(basicInfos);
        return ResponseModel.ok("删除成功");
    }

    // 查询单个
    @GetMapping("/{id}")
    @LogRecord("查询一个基本信息")
    @ApiOperation("查询一个基本信息")
    public ResponseModel getBasicInfoById(@PathVariable("id") String basicInfoId) {
        return ResponseModel.ok(basicInfoService.findById(basicInfoId));
    }

    // 查询全部
    @GetMapping
    @LogRecord("查询所有基本信息")
    @ApiOperation("查询所有基本信息")
    public ResponseModel getBasicInfos(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseModel.ok(basicInfoService.findAll(pageable));
    }

    // 根据搜索条件查询
    @GetMapping("/condition")
    @LogRecord("根据条件查询基本信息")
    @ApiOperation("根据条件查询基本信息")
    public ResponseModel getBasicInfoByCondition(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable,
                                                 BasicInfoSearchVo basicInfoSearchVo) {
        log.info("条件: " + basicInfoSearchVo);
        return ResponseModel.ok(basicInfoService.findByCondition(pageable, basicInfoSearchVo));
    }
}
