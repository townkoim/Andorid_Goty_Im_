package com.slife.gopapa.view;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.slife.gopapa.view.DateTimePicker.OnDateTimeChangedListener;


/**
 * @ClassName: DateTimePickerDialog
 * @Description: 时间(年月日时分秒)选择器的Dialog,供约赛是使用
 * @author 肖邦
 * @date 2015-1-7 下午4:32:36
 */
public class DateTimePickerDialog extends AlertDialog implements OnClickListener {
	private DateTimePicker mDateTimePicker;// 时间选择器控件
	private Calendar mDate = Calendar.getInstance();// 系统日期数据操作类
	private OnDateTimeSetListener mOnDateTimeSetListener;// 时间选择监听器
	private long date;
	@SuppressWarnings("deprecation")
	public DateTimePickerDialog(Context context, long date) {
		super(context);
		this.date = date;
		mDateTimePicker = new DateTimePicker(context);
		setView(mDateTimePicker);
		mDateTimePicker.setOnDateTimeChangedListener(new OnDateTimeChangedListener() {
			@Override
			public void onDateTimeChanged(DateTimePicker view, int year, int month, int day, int hour, int minute) {
				//当时间滚轮中是数据改变,dialog中的title也随之改变
				mDate.set(Calendar.YEAR, year);
				mDate.set(Calendar.MONTH, month);
				mDate.set(Calendar.DAY_OF_MONTH, day);
				mDate.set(Calendar.HOUR_OF_DAY, hour);
				mDate.set(Calendar.MINUTE, minute);
				mDate.set(Calendar.SECOND, 0);
				updateTitle(mDate.getTimeInMillis());
			}
		});

		setButton("设置", this);
		setButton2("取消", (OnClickListener) null);
		mDate.setTimeInMillis(date);
		updateTitle(mDate.getTimeInMillis());
	}

	public interface OnDateTimeSetListener {
		void OnDateTimeSet(AlertDialog dialog, long date);
	}

	private void updateTitle(long date) {
		//设置format的格式
		int flag = DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_TIME;
		setTitle(DateUtils.formatDateTime(this.getContext(), date, flag));
	}

	public void setOnDateTimeSetListener(OnDateTimeSetListener callBack) {
		mOnDateTimeSetListener = callBack;
	}

	public void onClick(DialogInterface arg0, int arg1) {
		if (mOnDateTimeSetListener != null) {
			if(date> mDate.getTimeInMillis()){
				Toast.makeText(getContext(), "约赛时间必须大于当前时间", Toast.LENGTH_SHORT).show();
				mOnDateTimeSetListener.OnDateTimeSet(this, 0);
			}else{
				mOnDateTimeSetListener.OnDateTimeSet(this, mDate.getTimeInMillis());
			}
		}
	}
}
