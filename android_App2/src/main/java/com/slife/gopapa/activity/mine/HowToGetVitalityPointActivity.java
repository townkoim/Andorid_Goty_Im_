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
import com.slife.gopapa.adapter.HowToGetVitalityPointAdapter;
import com.slife.gopapa.model.VitalityItem;

/**
 * 
 * @ClassName: HowToGetVitalityPointActivity
 * @Description: 如何获得积分,原型图"活力积分-01";
 * 				点击listview中项目可以跳转到获得积分信息的界面
 * 				1.每日登陆 2.发布邀请 3.接受邀请 4.分享战绩 5.成功提交战绩6.推荐给好友
 * @author 肖邦
 * @date 2015-1-7 下午5:08:02
 * 
 */
@ContentView(R.layout.activity_mine_vitality_point2)
public class HowToGetVitalityPointActivity extends BaseActivity {
	@ViewInject(R.id.activity_vitality_point2_list1)
	private ListView lvAssigment;// 今日任务
	@ViewInject(R.id.activity_vitality_point2_list2)
	private ListView lvOtherAward;// 其他奖励
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;//返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;//标题
	private HowToGetVitalityPointActivity activity;//本类对象
	private HowToGetVitalityPointAdapter adapter;//listview适配器
	private HowToGetVitalityPointAdapter adapter2;//listview适配器
	private ArrayList<VitalityItem> list;// 今日任务列表
	private ArrayList<VitalityItem> list2;// 其他奖励列表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		initView();
	}

	private void initView() {
		tvTitle.setText("活力积分");
		list = new ArrayList<VitalityItem>();
		// 在任务listview中加入数据
		list.add(new VitalityItem("每日登录", "1", true));
		list.add(new VitalityItem("发布邀请", "3", false));
		adapter = new HowToGetVitalityPointAdapter(getApplicationContext(), list);
		lvAssigment.setAdapter(adapter);
		lvAssigment.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 1) {
					myStartActicity(HowToGetVitalityPointDetailActivity.TYPE_PUBLISH);
				}
			}
		});
		list2 = new ArrayList<VitalityItem>();
		// 在奖励listview中加入数据
		list2.add(new VitalityItem("接受邀请", "3", true));
		list2.add(new VitalityItem("分享战绩", "3", true));
		list2.add(new VitalityItem("成功提交战绩", "5", true));
		list2.add(new VitalityItem("推荐给好友", "5", false));
		adapter2 = new HowToGetVitalityPointAdapter(getApplicationContext(), list2);
		lvOtherAward.setAdapter(adapter2);
		//设置item点击事件跳转至相应的界面
		lvOtherAward.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					myStartActicity(HowToGetVitalityPointDetailActivity.TYPE_ACCEPT);
				}
				if (position == 1) {
					myStartActicity(HowToGetVitalityPointDetailActivity.TYPE_SHARE);
				}
				if (position == 2) {
					myStartActicity(HowToGetVitalityPointDetailActivity.TYPE_SUBMIT);
				}
				if (position == 3) {
//					startActivity(new Intent(activity, ShareActivity.class));
				}
			}
		});
	}

	/**
	 * @Title: startActicity
	 * @Description: 跳转到如何获得积分详情界面
	 * @param @param type
	 * @return void
	 * @throws
	 */
	private void myStartActicity(int type) {
		Intent intent = new Intent(activity, HowToGetVitalityPointDetailActivity.class);
		intent.putExtra("type", type);
		startActivity(intent);
	}

	@OnClick(R.id.common_title_back)
	public void onclick(View v) {
		finish();
	}
}
