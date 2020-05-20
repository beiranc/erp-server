package com.beiran.core.system.repository;

import com.beiran.core.system.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 操作日志 Repository
 */
public interface LogRepository extends JpaRepository<Log, String>, JpaSpecificationExecutor<Log> {

    /**
     * 根据用户名查询该用户的操作日志
     * @param userName 用户名（登录名）
     * @return Page<Log>
     */
    Page<Log> findByUserName(String userName, Pageable pageable);

    /**
     * 根据用户名清空该用户的操作日志
     * @param userName 用户名
     * @return 是否删除成功
     */
    @Modifying
    @Query(value = "DELETE FROM erp_log WHERE user_name = ?1", nativeQuery = true)
    int deleteByName(String userName);
}
