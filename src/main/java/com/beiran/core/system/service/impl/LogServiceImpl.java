package com.beiran.core.system.service.impl;

import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.DateTimeUtils;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.system.entity.Log;
import com.beiran.core.system.repository.LogRepository;
import com.beiran.core.system.service.LogService;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * LogService 接口的实现类<br>
 * 若参数为空则会抛出 ParameterException 或 IllegalArgumentException
 */
@Service("logService")
public class LogServiceImpl implements LogService {

    @Resource
    private LogRepository logRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Log save(Log log) {
        Assert.notNull(log, "需要保存的日志不能为空");
        return logRepository.save(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void flushAll() {
        logRepository.deleteAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean flushByName(String userName) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("日志用户名不能为空");
        }
        return logRepository.deleteByName(userName) > 0;
    }

    @Override
    public Page<Log> getLogsByName(String userName, Pageable pageable) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("日志用户名不能为空");
        }
        return logRepository.findByUserName(userName, pageable);
    }

    @Override
    public Page<Log> getLogs(Pageable pageable) {
        return logRepository.findAll(pageable);
    }

    @Override
    public File createSpecLogsExcelFileByName(String userName, Pageable pageable) {
        Page<Log> logPage = getLogsByName(userName, pageable);
        List<Log> logs = logPage.getContent();
        if (CollectionUtils.isEmpty(logs)) {
            logs = new ArrayList<>();
        }
        return createExcelFile(logs);
    }

    @Override
    public File createLogsExcelFile(Pageable pageable) {
        Page<Log> logPage = getLogs(pageable);
        List<Log> logs = logPage.getContent();
        if (CollectionUtils.isEmpty(logs)) {
            logs = new ArrayList<>();
        }
        return createExcelFile(logs);
    }

    /**
     * 根据给定 List<Log> 创建 Excel 文件
     * @param logs List<Log>
     * @return File
     */
    private File createExcelFile(List<Log> logs) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = workbook.createSheet();
        // 创建头信息
        SXSSFRow rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("日志编号");
        rowInfo.createCell(++columnIndex).setCellValue("用户名");
        rowInfo.createCell(++columnIndex).setCellValue("用户操作");
        rowInfo.createCell(++columnIndex).setCellValue("请求方法");
        rowInfo.createCell(++columnIndex).setCellValue("请求参数");
        rowInfo.createCell(++columnIndex).setCellValue("请求耗时(ms)");
        rowInfo.createCell(++columnIndex).setCellValue("IP");
        rowInfo.createCell(++columnIndex).setCellValue("创建时间");

        for (int i = 0; i < logs.size(); i++) {
            Log log = logs.get(i);
            SXSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(log.getLogId());
            row.getCell(++columnIndex).setCellValue(log.getUserName());
            row.getCell(++columnIndex).setCellValue(log.getOperation());
            row.getCell(++columnIndex).setCellValue(log.getMethod());
            row.getCell(++columnIndex).setCellValue(log.getParams());
            row.getCell(++columnIndex).setCellValue(log.getSpendTime());
            row.getCell(++columnIndex).setCellValue(log.getIp());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(log.getCreateTime()));
        }
        return FileUtils.createExcelFile(workbook, "erp_logs");
    }
}
