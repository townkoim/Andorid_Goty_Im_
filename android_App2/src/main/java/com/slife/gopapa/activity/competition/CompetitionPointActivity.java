package com.slife.gopapa.activity.competition;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.slife.gopapa.activity.mine.ShareActivity;
import com.slife.gopapa.adapter.CompetitionPointAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.ListPowerAPP2;
import com.slife.gopapa.utils.ParseErrorJsonUtils;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * 
 * @ClassName: CompetitionPointActivity
 * @Description: 战绩积分界面,,实现了原型中"战绩积分"页;
 * 				展示参与过的比赛记录,长按比赛记录可以分享至对应的分享平台;
 * @author 肖邦
 * @date 2015-1-7 下午5:07:35
 * 
 */
@ContentView(R.layout.activity_mine_combat_point)
public class CompetitionPointActivity extends BaseActivity implements OnRefreshListener2<ListView>, OnItemClickListener {
	@ViewInject(R.id.activity_competiton_combat_point)
	private TextView tvMyPoint;// 我的战绩积分
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;// 返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;// 标题
	@ViewInject(R.id.activity_competiton_combat_point_list)
	private PullToRefreshListView ptrListView;// 下拉刷新
	private CompetitionPointActivity activity;//本类对象
	private ListView listView;// 积分列表
	private CompetitionPointAdapter adapter;// 列表适配器
	private ArrayList<ListPowerAPP2> list;// 战绩积分数组
	private int intPoint = 0;//战绩积分
	private int start = 0; //查询开始的位置
	private String url = "";// 请求地址
	private String point = "";// 积分

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		initURL();
		initView();
	}

	/**
	 * @Title: initURL
	 * @Description: 刷新数据后,重置请求地址
	 * @param
	 * @return void
	 * @throws
	 */
	private void initURL() {
		url = new StringBuffer(APPConstants.URL_LISTPOWERAPP2).append(MyApplication.app2Token).append("&user_account=")
				.append(MyApplication.preferences.getString("user_account", "")).append("&start=").append(start).append("&limit=15").toString();
	}

	private void initView() {
		tvTitle.setText("战绩积分");
		ptrListView.setMode(Mode.BOTH);// 上拉.下拉模式
		// 下拉刷新时的提示文本设置
		ptrListView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新");
		ptrListView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
		ptrListView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");
		// 上拉加载更多时的提示文本设置
		ptrListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
		ptrListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		ptrListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
		listView = ptrListView.getRefreshableView();
		list = new ArrayList<ListPowerAPP2>();
		ptrListView.setOnRefreshListener(this);// 设置刷新监听器
		ptrListView.setOnItemClickListener(this);// 设置条目点击的监听器
		ptrListView.setRefreshing(false);// 刚进界面就要加载数据
		adapter = new CompetitionPointAdapter(activity, list);
		listView.setAdapter(adapter);
	}

	@OnClick(R.id.common_title_back)
	public void onclick(View v) {
		finish();
	}

	/**
	 * 
	 * @ClassName: Task
	 * @Description:战绩积分异步任务,
	 * 入参1.user_account	用户啪啪号 ;
	 * 2.start	数据开始数
	 * 3.limit	获取条数
	 * @author 肖邦
	 * @date 2015-2-11 下午4:59:02
	 * 
	 */
	class Task extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPreExecute() {
			MyProgressDialog.showDialog(activity, "战绩积分", "请稍等...");
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(Void... params) {
			return MyHttpClient.getJsonFromService(CompetitionPointActivity.this, url);
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (result != null) {
				try {
					if ("200".equals(result[0])) {
						JSONObject object = new JSONObject(result[1]);
						point = "我的积分战绩:" + object.optInt("power_sum");
						intPoint = object.optInt("power_sum");
						JSONArray array = object.optJSONArray("power_list");
						List<ListPowerAPP2> List = JSON.parseArray(array.toString(), ListPowerAPP2.class);
						for (ListPowerAPP2 listPowerAPP2 : List) {
							list.add(listPowerAPP2);
						}
					} else {
						Toast.makeText(getApplicationContext(), ParseErrorJsonUtils.getErrorMsg(result).getError(), Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_LONG).show();
			}
			MyProgressDialog.closeDialog();
			tvMyPoint.setText("0");
			if (intPoint != 0) {
				tvMyPoint.setText(point);
			}
			adapter.notifyDataSetChanged();
			ptrListView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	/**
	 * (非 Javadoc) Title: onPullDownToRefresh Description:
	 * 下拉刷新
	 * @param refreshView
	 * @see com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2#onPullDownToRefresh(com.handmark.pulltorefresh.library.PullToRefreshBase)
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		start = 0;
		list.clear();
		initURL();
		new Task().execute();
	}

	/**
	 * (非 Javadoc) Title: onPullUpToRefresh Description:
	 * 上拉加载
	 * @param refreshView
	 * @see com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2#onPullUpToRefresh(com.handmark.pulltorefresh.library.PullToRefreshBase)
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		start += 15;
		initURL();
		new Task().execute();
	}

	/**
	 * (非 Javadoc) Title: onItemClick Description:
	 * 点击之后跳转到分享的界面
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ListPowerAPP2 listPowerAPP2 = list.get(position - 1);//获得对应战绩积分的实体类
		Intent intent = new Intent(activity, ShareActivity.class);
		intent.putExtra("arr", new String[] { "啪啪江湖战绩", "测试", "http://spapa.com.cn", "" });//参数:1.标题 2.内容3.超链接4.图片地址
		intent.putExtra("race_id", listPowerAPP2.getRace_id());//比赛ID
		startActivity(intent);
		activity.finish();
	}
}
