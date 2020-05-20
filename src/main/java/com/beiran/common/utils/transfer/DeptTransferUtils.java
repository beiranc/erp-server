package com.beiran.common.utils.transfer;

import com.beiran.common.utils.DateTimeUtils;
import com.beiran.core.system.dto.DeptDto;
import com.beiran.core.system.dto.DeptSmallDto;
import com.beiran.core.system.entity.Dept;
import com.beiran.core.system.vo.DeptVo;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 提供 DeptDto-Dept-DeptVo 相互转换的工具
 */

@Log
public class DeptTransferUtils {

    /**
     * 将 DeptDto 转换为 Dept
     * @param deptDto 需要转换的 DeptDto
     * @return Dept
     */
    public static Dept dtoToDept(DeptDto deptDto) {
        Dept dept = new Dept();
        if (!Objects.equals(deptDto, null)) {
            BeanUtils.copyProperties(deptDto, dept);
        }
        if (!Objects.equals(deptDto.getParent(), null)) {
            Dept parent = new Dept();
            BeanUtils.copyProperties(deptDto.getParent(), parent);
            dept.setDeptParent(parent);
        }
        Date createTime = null;
        if (StringUtils.hasText(deptDto.getCreateTime())) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_TIMESTAMP);
            try {
               createTime = simpleDateFormat.parse(deptDto.getCreateTime());
            } catch (ParseException e) {
                log.info(" { 日期转换异常 } " + e.getLocalizedMessage());
            }
        }
        dept.setDeptCreateTime(createTime);
        return dept;
    }

    /**
     * 将 Dept 转换为 DeptDto
     * @param dept 需要转换的 Dept
     * @return DeptDto
     */
    public static DeptDto deptToDto(Dept dept) {
        DeptDto deptDto = new DeptDto();
        if (!Objects.equals(dept, null)) {
            BeanUtils.copyProperties(dept, deptDto);
        }
        if (!Objects.equals(dept.getDeptParent(), null)) {
            DeptSmallDto parent = new DeptSmallDto();
            BeanUtils.copyProperties(dept.getDeptParent(), parent);
            deptDto.setParent(parent);
        }
        if (!Objects.equals(dept.getDeptCreateTime(), null)) {
            deptDto.setCreateTime(DateTimeUtils.getDateTime(dept.getDeptCreateTime()));
        }
        return deptDto;
    }

    /**
     * 将 DeptVo 转换为 Dept
     * @param deptVo 需要转换的 DeptVo
     * @return Dept
     */
    public static Dept voToDept(DeptVo deptVo) {
        Dept dept = new Dept();
        if (!Objects.equals(deptVo, null)) {
            BeanUtils.copyProperties(deptVo, dept);
        }
        if (!Objects.equals(deptVo.getParent(), null)) {
            Dept parent = new Dept();
            parent.setDeptId(deptVo.getParent().getDeptId());
            parent.setDeptName(deptVo.getParent().getDeptName());
            dept.setDeptParent(parent);
        } else {
            dept.setDeptParent(null);
        }
        return dept;
    }
}
