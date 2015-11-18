package com.slife.gopapa.view;

import android.app.ProgressDialog;
import android.content.Context;

/***
* @ClassName: MyProgressDialog 
* @Description: 自定义封装进度对话框
* @author 菲尔普斯
* @date 2015-1-15 下午4:05:26 
*
 */
public class MyProgressDialog {
	private static ProgressDialog progressDialog;
	/**
	* @Title: showDialog 
	* @Description: 显示进度对话框
	* @param @param context 上下文对象
	* @param @param title 弹框的标题
	* @param @param message 弹框的消息内容
	* @return void
	* @throws
	 */
	public static void showDialog(Context context, CharSequence title,
			CharSequence message) {
		progressDialog = ProgressDialog.show(context, title, message, true,
				false);
	}
	public static void showDialog(Context context, CharSequence title,
			CharSequence message,boolean isCancel) {
		progressDialog = ProgressDialog.show(context, title, message, true,
				isCancel);
	}
	public static void closeDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog=null;
		}
	}
}
