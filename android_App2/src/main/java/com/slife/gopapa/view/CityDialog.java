/**
 * 
 */
package com.slife.gopapa.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.view.citypicker.CityPicker;

/**
 * @ClassName: CityDialog
 * @Description: 城市选择器
 * @author 肖邦
 * @date 2015-3-4 下午1:19:13
 */
public class CityDialog extends Dialog implements android.view.View.OnClickListener {
	public OnDialogDismissListener listener;// 当Dialog中的选项被选中后的监听器
	private CityPicker cityPicker;//城市滚轮
	private Button btn;//确认按钮
	private boolean isModify;//这个标记用于是否储存选择后的城市信息(省市区码)

	public CityDialog(Context context) {
		super(context);
	}

	public CityDialog(Context context, int theme, boolean isModify) {
		super(context, theme);
		this.isModify = isModify;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select_city);
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
		cityPicker = (CityPicker) findViewById(R.id.citypicker);
		btn = (Button) findViewById(R.id.citypicker_btn);
		btn.setOnClickListener(this);
	}

	/**
	 * 
	 * @Title: setOnDialogDismissListener
	 * @Description: 设置监听器的方法
	 * @param @param listener
	 * @return void
	 * @throws
	 */
	public void setOnDialogDismissListener(OnDialogDismissListener listener) {
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
	public interface OnDialogDismissListener {
		/**
		 * 
		 * @Title: selectedFeeType
		 * @Description:
		 * @param @param province_code 省码
		 * @param @param city_code_  城市码
		 * @param @param district_code 区码
		 * @param @param province 省份名
		 * @param @param city  城市名
		 * @param @param district 地区名
		 * @return void
		 * @throws
		 */
		void onSelected(int province_code, int city_code_, int district_code, String province, String city, String district);
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
			Editor editor = MyApplication.preferences.edit();// 获取编辑器
			if (isModify) {
				//储存省市区码
				editor.putInt("province_code", Integer.parseInt(cityPicker.getCity_code_string().substring(0, 2)));// 省号
				editor.putInt("city_code", Integer.parseInt(cityPicker.getCity_code_string().substring(0, 4)));// 市号
				editor.putInt("district_code", Integer.parseInt(cityPicker.getCity_code_string()));// 区号
			}
			//将用户的城市信息保存起来
			listener.onSelected(Integer.parseInt(cityPicker.getCity_code_string().substring(0, 2)), Integer.parseInt(cityPicker.getCity_code_string().substring(0, 4)),
					Integer.parseInt(cityPicker.getCity_code_string()), cityPicker.getProvince_string(), cityPicker.getCity_string(), cityPicker.getDistrict_string());
			if (MyApplication.isRanking == 0) {
				//如果不是RankingActivity调用的则把位置信息储存到ProvincePosition/CityPosition/DistrictPosition中
				editor.putInt("ProvincePosition", cityPicker.getProvincePosition());
				editor.putInt("CityPosition", cityPicker.getCityPosition());
				editor.putInt("DistrictPosition", cityPicker.getDistrictPosition());
			} else {
				//如果是RankingActivity调用的则把位置信息储存到ProvincePositionRanking/CityPositionRanking/DistrictPositionRanking中
				editor.putInt("ProvincePositionRanking", cityPicker.getProvincePosition());
				editor.putInt("CityPositionRanking", cityPicker.getCityPosition());
				editor.putInt("DistrictPositionRanking", cityPicker.getDistrictPosition());
			}
			editor.commit();// 提交修改
			dismiss();
		}
	}
}
