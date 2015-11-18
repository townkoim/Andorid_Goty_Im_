package com.slife.gopapa.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TimePicker.OnTimeChangedListener;

import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;

/**
 * @ClassName: DateTimePickDialog
 * @Description: 年月日选择器
 * @author 肖邦
 * @date 2015-1-9 下午3:34:33
 */
public class DateTimePickDialog implements OnDateChangedListener, OnTimeChangedListener {
	private DatePicker datePicker;// 日期选择器
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;
	private Calendar calendar;
	private Calendar calendarNow;
	private SimpleDateFormat preYear;
	private SimpleDateFormat nowYear;
	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialog(Activity activity, String initDateTime) {
		this.activity = activity;
		this.initDateTime = initDateTime;
	}

	/**
	 * 初始化时间选择器的日期
	 */
	public void init(DatePicker datePicker) {
		Calendar calendar = Calendar.getInstance();
		if (!(null == initDateTime || "".equals(initDateTime))) {
			calendar = this.getCalendarByInintData(initDateTime);
		} else {
			initDateTime = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
		}
		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
	}

	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间TextView
	 * @return
	 */
	@SuppressLint("InflateParams")
	public AlertDialog dateTimePicKDialog(final TextView inputDate) {
		LinearLayout dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		init(datePicker);

		ad = new AlertDialog.Builder(activity).setTitle(initDateTime).setView(dateTimeLayout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//点击确定则返回选择后的日期
				if(calendar.compareTo(calendarNow)>0){
					Toast.makeText(activity, "日期选择有误", Toast.LENGTH_SHORT).show();
					return;
				}
				int now = Integer.parseInt(nowYear.format(calendarNow.getTime()));//现在的年份
				int pre = Integer.parseInt(preYear.format(calendar.getTime()));//选择的年份
				MyApplication.preferences.edit().putString("user_age", String.valueOf(now-pre)).commit();
				inputDate.setText(dateTime);
				MyApplication.preferences.edit().putString("user_birth", dateTime).commit();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//点击取消则返回原本的日期
				inputDate.setText(initDateTime);
			}
		}).show();

		onDateChanged(null, 0, 0, 0);
		return ad;
	}

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	@SuppressLint("SimpleDateFormat")
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		// 获得日历实例
		calendar = Calendar.getInstance();
		calendarNow = Calendar.getInstance();
		calendarNow.setTime(new Date(System.currentTimeMillis()));
		calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日");
		preYear = new SimpleDateFormat("yyyy");
		nowYear = new SimpleDateFormat("yyyy");
		dateTime = sdf.format(calendar.getTime());
		ad.setTitle(sdf2.format(calendar.getTime()));
	}

	/**
	 * 实现将初始日期时间XXXX-XX-XX拆分成年 月 日,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();
		String[] arr = initDateTime.split("-");
		int currentYear = Integer.valueOf(arr[0]).intValue();
		int currentMonth = Integer.valueOf(arr[1]).intValue() - 1;
		int currentDay = Integer.valueOf(arr[2]).intValue();
		calendar.set(currentYear, currentMonth, currentDay);
//		Log.i("", currentYear + "-" + currentMonth + "-" + currentDay);
		return calendar;
	}
}
