package com.beiran.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间相关工具
 */
public class DateTimeUtils {
	
	/**
	 * 带时间的日期格式
	 */
	public static final String DATE_FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 不带时间的日期格式
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
	/**
	 * 获取当前标准格式化日期时间
	 * @return 格式化的日期时间
	 */
	public static String getDateTime() {
		return getDateTime(new Date());
	}
	
	/**
	 * 获取给定日期的标准格式化日期时间
	 * @param date 给定日期
	 * @return 格式化的日期时间
	 */
	public static String getDateTime(Date date) {
		return new SimpleDateFormat(DATE_FORMAT_TIMESTAMP).format(date);
	}
	
	/**
	 * 获取当前标准化格式日期
	 * @return 格式化的日期
	 */
	public static String getDate() {
		return getDateTime(new Date());
	}
	
	/**
	 * 获取给定日期的标准格式化日期
	 * @param date 给定日期
	 * @return 格式化的日期
	 */
	public static String getDate(Date date) {
		return new SimpleDateFormat(DATE_FORMAT).format(date);
	}
}
