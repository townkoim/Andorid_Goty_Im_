package com.slife.gopapa.activity.mine;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseNoServiceActivity;
import com.slife.gopapa.adapter.ChildAdapter;

/**
 * 
 * @ClassName: ShowImageStep1Activity
 * @Description: 无原型图;
 * 				从上级文件夹中跳转过来选择图片当头像,获取到图片地址后再传到下一个界面去
 * @author 肖邦
 * @date 2015-1-20 上午10:15:51
 * 
 */
@ContentView(R.layout.activity_show_img_step1)
public class ShowImageStep1Activity extends BaseNoServiceActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;//返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;//标题
	private GridView mGridView;
	private List<String> list;
	private ChildAdapter adapter;
	private ShowImageStep1Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		tvTitle.setText("选择头像");
		mGridView = (GridView) findViewById(R.id.child_grid);
		//接收上级界面传来的路径集合
		list = getIntent().getStringArrayListExtra("data");
		adapter = new ChildAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(activity, ShowImageStep2Activity.class);
				intent.putExtra("img_path", list.get(position));
				startActivity(intent);
				activity.finish();
			}
		});
	}

	@OnClick(R.id.common_title_back)
	public void onclick(View v) {
		if (v.getId() == R.id.common_title_back) {
			activity.finish();
		}
	}
}
