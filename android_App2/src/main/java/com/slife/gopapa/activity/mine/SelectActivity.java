package com.slife.gopapa.activity.mine;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.adapter.SelectAdapter;

/**
 * 
 * @ClassName: SelectActivity
 * @Description: 选择项目的界面,原型图"个人信息-选择职业";
 * 				展示学校或职业的listview,选中后返回个人信息界面(这界面已废弃,由CityDialog/SchoolDialog取代)
 * @author 肖邦
 * @date 2015-1-7 下午5:10:38
 * 
 */
@ContentView(R.layout.activity_select)
public class SelectActivity extends BaseActivity {
	private SelectActivity activity;//本类对象
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;//返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;//标题
	@ViewInject(R.id.activity_select_list)
	private ListView lv;//列表
	private SelectAdapter adapter;//学校或职业适配器
	public static final int TYPE_OCCUPATION = 0;//类型(职业,需要这个标记来判断访问地址)
	public static final int TYPE_SPORTS = 1;//类型(学校)
	public static final int RESULTCODE = 1002;
	private ArrayList<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		if (getIntent() != null) {
			initView(getIntent());
		}
	}
	/**
	 * 
	* @Title: initView
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param intent
	* @return void
	* @throws
	 */
	private void initView(Intent intent) {
		int list_type = -1;
		String[] listItem = null;
		if ((list_type = intent.getIntExtra("list_type", -1)) != -1) {
			if (list_type == TYPE_OCCUPATION) {
				listItem = getResources().getStringArray(R.array.occupation);
			}
			if (list_type == TYPE_SPORTS) {
				listItem = getResources().getStringArray(R.array.sports);
			}
			list = new ArrayList<String>();
			for (String item : listItem) {
				list.add(item);
			}
			adapter = new SelectAdapter(getApplicationContext(), list, 3);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent data = new Intent();
					data.putExtra("select_result", list.get(position));
					setResult(RESULTCODE, data);
					finish();
				}
			});
		}
	}

	@OnClick(R.id.common_title_back)
	public void onClick(View v) {
		finish();
	}
}
