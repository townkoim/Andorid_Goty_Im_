/**   
 * @Title: SelectSexDialog.java
 * @Package com.slife.gopapa.view
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 肖邦
 * @date 2015-2-27 下午12:02:19
 * @version V1.0
 */
package com.slife.gopapa.view;

import com.slife.gopapa.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * @ClassName: SelectSexDialog
 * @Description: 此类暂时不用,供修改性别使用
 * @author 肖邦
 * @date 2015-2-27 下午12:02:19
 * 
 */
public class SelectSexDialog extends Dialog implements android.view.View.OnClickListener {
	private OnSelectedSexListener listener;
	private TextView tvMan;
	private TextView tvWoman;

	/**
	 * Title: Description:
	 * 
	 * @param context
	 */
	public SelectSexDialog(Context context) {
		super(context);
	}
	public SelectSexDialog(Context context,int theme) {
		super(context,theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	/**
	 * @Title: initView
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param
	 * @return void
	 * @throws
	 */
	private void initView() {
		tvMan = (TextView) findViewById(R.id.select_sex_dialog_man);
		tvWoman = (TextView) findViewById(R.id.select_sex_dialog_woman);
		tvMan.setOnClickListener(this);
		tvWoman.setOnClickListener(this);
	}

	/**
	 * 
	 * @Title: setOnDialogDismissListener
	 * @Description: 设置监听器的方法
	 * @param @param listener
	 * @return void
	 * @throws
	 */
	public void setOnSelectedSexListener(OnSelectedSexListener listener) {
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
	public interface OnSelectedSexListener {
		/**
		 * 
		 * @Title: selectedFeeType
		 * @Description:
		 * @param @param type 约赛费用的类型
		 * @param @param raceExpense 约赛费用的类型所在的位置(默然从1开始)
		 * @return void
		 * @throws
		 */
		void selectedSex(String sex, String gender);
	}

	/**
	 * (非 Javadoc) Title: onClick Description:
	 * 
	 * @param v
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.select_sex_dialog_man) {
			listener.selectedSex("男", "1");
		}
		if (v.getId() == R.id.select_sex_dialog_woman) {
			listener.selectedSex("女", "2");
		}
	}
}
