package com.slife.gopapa.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.view.schoolpicker.SchoolPicker;

/**
 * 
* @ClassName: SchoolDialog
* @Description: TODO(这里用一句话描述这个类的作用)
* @author 肖邦
* @date 2015-3-4 上午11:19:46
*
 */
public class SchoolDialog extends Dialog implements android.view.View.OnClickListener,android.content.DialogInterface.OnDismissListener {
	public OnDialogSelectListener2 listener;// 当Dialog中的选项被选中后的监听器
	private SchoolPicker schoolPicker;//学校选择器
	private Button btn;//确认按钮

	public SchoolDialog(Context context) {
		super(context);
	}

	public SchoolDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select_school);
		initView();
	}
	/**
	 * @Title: initView
	 * @Description: 初始化控件
	 * @param
	 * @return void
	 * @throws
	 */
	private void initView() {
		schoolPicker = (SchoolPicker) findViewById(R.id.citypicker);
		btn = (Button) findViewById(R.id.citypicker_btn);
		btn.setOnClickListener(this);
		this.setOnDismissListener(this);
	}
	/**
	 * 
	 * @Title: setOnDialogDismissListener
	 * @Description: 设置监听器的方法
	 * @param @param listener
	 * @return void
	 * @throws
	 */
	public void setOnDialogDismissListener(OnDialogSelectListener2 listener) {
		this.listener = listener;
	}

	/**
	 * 
	 * @ClassName: OnDialogDismissListener
	 * @Description: 回调接口
	 * @author 肖邦
	 * @date 2015-1-27 上午10:33:58
	 * 
	 */
	public interface OnDialogSelectListener2 {
		/**
		 * 
		 * @Title: selectedFeeType
		 * @Description:
		 * @param @param province_code
		 * @param @param city_code_
		 * @param @param district_code
		 * @param @param province
		 * @param @param city
		 * @param @param district
		 * @return void
		 * @throws
		 */
		void onSelected(int province_code, int city_code_, String province, String city);
	}

	/**
	 * (非 Javadoc) Title: onClick Description:
	 * 
	 * @param v
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (listener != null) {
			//若监听器不为空,
			Editor editor = MyApplication.preferences.edit();// 获取编辑器
			//将 省份ID 学校ID 城市 学校 传到回调函数里面
			listener.onSelected(schoolPicker.getProvincePosition(), Integer.parseInt(schoolPicker.getSchoolId()), "",
					schoolPicker.getSchool());
			//并且记录当前的  省份/学校在选择器里的位置;
			editor.putInt("SchoolProvincePosition", schoolPicker.getProvincePosition());
			editor.putInt("SchoolPosition", schoolPicker.getCityPosition());
			editor.commit();// 提交修改
			dismiss();
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		MyApplication.isModifyTextSize=1;
	}
}
