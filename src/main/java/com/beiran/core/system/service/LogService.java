package com.beiran.core.system.service;

import com.beiran.core.system.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;

/**
 * 日志业务操作
 */
public interface LogService {

    /**
     * 新增日志
     * @param log 日志数据
     * @return 保存后的 Log
     */
    Log save(Log log);

    /**
     * 清空所有日志
     */
    void flushAll();

    /**
     * 根据用户名清空该用户所有的日志
     * @param userName 用户名
     * @return 是否清空成功
     */
    Boolean flushByName(String userName);

    /**
     * 根据用户名获取日志
     * @param userName 用户名
     * @param pageable 分页参数
     * @return Page<Log>
     */
    Page<Log> getLogsByName(String userName, Pageable pageable);

    /**
     * 查询所有用户的日志
     * @param pageable 分页参数
     * @return Page<Log>
     */
    Page<Log> getLogs(Pageable pageable);

    /**
     * 导出指定用户的日志
     * @param userName 用户名
     * @param pageable 分页参数
     * @return File
     */
    File createSpecLogsExcelFileByName(String userName, Pageable pageable);

    /**
     * 导出所有用户的日志
     * @param pageable 分页参数
     * @return File
     */
    File createLogsExcelFile(Pageable pageable);
}
