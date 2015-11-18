package com.slife.gopapa.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

/**
 * @ClassName: DataUtils
 * @Description: 格式化时间助手类
 * @author 菲尔普斯
 * @date 2014-11-25 下午3:04:14
 * 
 */
@SuppressLint("SimpleDateFormat")
public class DataUtils {
	/**
	* @Title: getLocalTime
	* @Description: 得到本地时间
	* @param @param timeType 要转换成的时间类型
	* @param @return 
	* @return String 本地时间根据类型转换后的字符串
	* @throws
	 */
	public static String getLocalTime(String timeType) {
		SimpleDateFormat formatter = new SimpleDateFormat(timeType);
		Date curDate = new Date();
		return formatter.format(curDate);
	}
	/**
	* @Title: getGotyTime
	* @Description:  根据亲加服务器的时间转换成yyyy-MM-dd HH:mm:ss格式的时间
	* @param @param curSecends 亲加服务器的时间
	* @param @return
	* @return String 转换后的时间
	* @throws
	 */
	public static String getGotyTime(long curSecends) {
		// 服务器是gmt时间，需要加上所在时区的时差，才是当前时间
		curSecends = curSecends * 1000;
		Date date = new Date(curSecends);
		DateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = dfs.format(date);
		return time;
	}
	/**
	* 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	*
	*/
	public static String getStringDate(Long date) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		return dateString;
	}
	
	/**
	* @Title: getCurrenTime
	* @Description: 得到当前系统时间
	* @param @return
	* @return String 返回以yyyyMMddHHmmss格式的时间
	* @throws
	 */
		public static String getCurrenTime(){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date curDate = new Date(System.currentTimeMillis());
			String str = formatter.format(curDate);
			return str;
		}
}
