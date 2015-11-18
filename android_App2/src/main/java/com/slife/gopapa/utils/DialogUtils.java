package com.slife.gopapa.utils;

import com.slife.gopapa.R;
import com.slife.gopapa.view.CityDialog;
import com.slife.gopapa.view.SchoolDialog;

import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * @ClassName: DialogUtils
 * @Description: 这是返回Dialog的工具类
 * @author 肖邦
 * @date 2015-3-5 上午9:19:49
 */
public class DialogUtils {
	/**
	 * @Title: createCityDialog
	 * @Description: 返回城市选择器的Dialog
	 * @param @param context
	 * @param @param theme
	 * @param @param isModify
	 * @param @return
	 * @return CityDialog
	 * @throws
	 */
	public static CityDialog createCityDialog(Context context, int theme, boolean isModify) {// isModify用于是否储存选择后的城市信息(省市区码)
		CityDialog dialog = new CityDialog(context, theme, isModify);// 实例化
		dialog.setCanceledOnTouchOutside(true);// 设置外面点击可令Dialog消失
		Window dialogWindow = dialog.getWindow();// 设置Dialog的位置
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);// 垂直居中和水平居中
		dialogWindow.setAttributes(lp);
		return dialog;
	}
	/**
	* @Title: createSchoolDialog
	* @Description: 返回学校选择器的Dialog
	* @param @param context
	* @param @param theme
	* @param @return
	* @return SchoolDialog
	* @throws
	 */
	public static SchoolDialog createSchoolDialog(Context context, int theme) {
		SchoolDialog dialog = new SchoolDialog(context, R.style.dialog);// 实例化
		dialog.setCanceledOnTouchOutside(true);// 设置外面点击可令Dialog消失
		Window dialogWindow = dialog.getWindow();// 设置Dialog的位置
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);// 垂直居中和水平居中
		dialogWindow.setAttributes(lp);
		return dialog;
	}
}
