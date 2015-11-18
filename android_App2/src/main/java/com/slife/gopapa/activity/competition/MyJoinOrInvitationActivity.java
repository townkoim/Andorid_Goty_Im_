package com.slife.gopapa.activity.competition;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.adapter.MyJoinAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.MyJoinOrInvitation;
import com.slife.gopapa.utils.ParseErrorJsonUtils;

/**
 * 
 * @ClassName: MyJoinOrInvitationActivity
 * @Description: 我发起的,我参与的一级界面,实现了原型中"我参与的" "我发起的" 页面.
 * 展示我发起的，我参与的相应的赛事主题、发起人（被邀请人）、项目、时间，
 * 点击对应的条目可以查看约赛的详情
 * @author 肖邦
 * @date 2015-1-22 上午9:14:56
 * 
 */

@ContentView(R.layout.activity_mine_my_invivation)
public class MyJoinOrInvitationActivity extends BaseActivity implements OnRefreshListener2<ListView> {
	@ViewInject(R.id.activity_mine_my_invatation_list)
	private PullToRefreshListView ptrListView;// 下拉刷新控件
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;// 返回
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;// 标题
	private MyJoinOrInvitationActivity activity;
	private MyJoinAdapter adapter;// 对应的适配器
	private ArrayList<MyJoinOrInvitation> list;
	private ListView listView;
	private int start = 0;// 开始位置
	private int limit = 10;// 每页显示数量
	private int type = 2;// 类型2为我发起的/类型3为我参与的
	private String clear = "";// 是否清空listview的标记

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		list = new ArrayList<MyJoinOrInvitation>();
		initView(getIntent().getIntExtra("type", 0));// 根据传过来的type初始化界面
														// (类型2为我发起的/类型3为我参与的)
		initListView();
	}

	private void initListView() {
		ptrListView.setMode(Mode.BOTH);
		// 下拉刷新时的提示文本设置
		ptrListView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新");
		ptrListView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
		ptrListView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新");
		// 上拉加载更多时的提示文本设置
		ptrListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
		ptrListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		ptrListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开加载");
		listView = ptrListView.getRefreshableView();// 获得可刷新的ListView,用于设置适配器
		ptrListView.setOnRefreshListener(this);// 刷新监听器
		ptrListView.setRefreshing(false);// 第一次进入页面是刷新
		ptrListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 跳转到我参与的或者是我发布的详情页面
				Intent intent = new Intent(activity, MyJoinOrInvitationDetailActivity.class);
				// 将列表内容的类型还有赛事的ID传到下一个界面，因为约赛详情界面请求数据需要这个两个参数
				intent.putExtra("type", type);
				intent.putExtra("race_id", Integer.parseInt(list.get(position - 1).getRace_id()));
				startActivity(intent);
			}
		});
		adapter = new MyJoinAdapter(activity, list, type);
		listView.setAdapter(adapter);
	}

	/**
	 * @Title: initView
	 * @Description: 根据上一个界面传来的值设置标题
	 * @param @param type
	 * @return void
	 * @throws
	 */
	private void initView(int type) {
		if (type != 0) {
			if (type == 2) {
				tvTitle.setText("我发起的");
				this.type = 2;
			} else {
				tvTitle.setText("邀请我的");
				this.type = 3;
			}
		}
	}

	/**
	 * @ClassName: Task
	 * @Description: 加载我参与的/我发布的数据 入参 
	 * 1.user_account 用户啪啪号 
	 * 2.type 列表类型 (2我发布的；3我参与的) 
	 * 3.start 列表开始位置 
	 * 4.limit 获取条数
	 * @author 肖邦
	 * @date 2015-1-27 下午2:02:21
	 */
	class Task extends AsyncTask<String, Void, String[]> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(String... params) {
			clear = params[2];
			//请求数据
			String[] json = MyHttpClient.getJsonFromService(MyJoinOrInvitationActivity.this,new StringBuffer(APPConstants.URL_RACE + MyApplication.app2Token).append("&user_account=")
					.append(MyApplication.preferences.getString("user_account", "")).append("&type=").append(type).append("&start=").append(params[0]).append("&limit=")
					.append(params[1]).toString());
			return json;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			if ("clear".equals(clear)) {
				list.clear();
			}
			if (result != null) {//结果不为空
				if ("200".equals(result[0])) {//结果有数据
					list.addAll(JSON.parseArray(result[1], MyJoinOrInvitation.class));
				} else {
					Toast.makeText(activity, ParseErrorJsonUtils.getErrorMsg(result).getError(), Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(activity, "请求错误", Toast.LENGTH_LONG).show();
			}
			adapter.notifyDataSetChanged();
			ptrListView.onRefreshComplete();
		}
	}

	@OnClick(R.id.common_title_back)
	public void onclick(View v) {
		finish();
	}

	// 下拉刷新.并清空listview中的数据
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		start = 0;
		limit = 10;
		new Task().execute("0", "10", "clear");
	}

	// 上拉加载.
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		start += limit;
		new Task().execute(start + "", "10", "");
	}
}
