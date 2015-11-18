package com.slife.gopapa.view;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.slife.gopapa.R;
import com.slife.gopapa.adapter.SelectAdapter;

/**
 * 
 * @ClassName: CompetitionFeeDialog
 * @Description: 自定义费用选择的Dialog
 * @author 肖邦
 * @date 2015-1-7 下午4:27:47
 * 
 */
public class CompetitionFeeDialog extends Dialog {
	private ListView lv;// 选项列表
	private ArrayList<String> list;// 费用选择的List
	private Context context;// 调用dialog的上下文
	private SelectAdapter adapter;// ListView的适配器
	public OnDialogDismissListener listener;// 当Dialog中的选项被选中后的监听器

	public CompetitionFeeDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CompetitionFeeDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_competition_fee);
		initView();
	}

	/**
	 * 
	 * @Title: initView
	 * @Description: 初始化布局,将数据加载到ListView中
	 * @param
	 * @return void
	 * @throws
	 */
	private void initView() {
		lv = (ListView) findViewById(R.id.dialog_competition_fee_list);
		// 获取资源文件中的费用类型的数组
		String[] fee_type = context.getResources().getStringArray(R.array.competition_fee);
		list = new ArrayList<>();
		for (String item : fee_type) {
			list.add(item);
		}
		adapter = new SelectAdapter(context, list, SelectAdapter.TYPE_COMPETITION_FEE);
		// 设置适配器
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (listener != null) {
					//点击后让dialog消失,并且把数据传回Activity,第一个参数为费用类型的字段,第二个参数为对应ID
					listener.selectedFeeType(list.get(position), position + 1);
					dismiss();
				}
			}
		});
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
		 * @param @param type 约赛费用的类型
		 * @param @param raceExpense 约赛费用的类型所在的位置(默然从1开始)
		 * @return void
		 * @throws
		 */
		void selectedFeeType(String type, int raceExpenseID);
	}
}
