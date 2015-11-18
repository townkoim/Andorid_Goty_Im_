package com.slife.gopapa.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.ranking.PersonInformationActivity;
import com.slife.gopapa.activity.ranking.RankingActivity;
import com.slife.gopapa.activity.ranking.RankingActivity.onFragmentContentRefresh;
import com.slife.gopapa.adapter.RankingAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.AccessToken;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.RankingList;
import com.slife.gopapa.utils.ParseErrorJsonUtils;

/**
 * 
 * @ClassName: RankingFragment
 * @Description: 排名Fragment
 * @author 肖邦
 * @date 2015-1-26 下午5:18:31
 * 
 */
public class RankingFragment extends Fragment implements OnRefreshListener2<ListView>, OnScrollListener {
	@ViewInject(R.id.fragment_ranking_list)
	private PullToRefreshListView ptrListView;//刷新列表
	public static RankingAdapter adapter;//排名适配器
	private ListView listView;
	private boolean scrollFlag = false;// 标记是否滑动
	private int lastVisibleItemPosition;// 标记上次滑动位置
	private onListViewScrollListener listener;//listview滑动监听器
	private onRefreshingListener refreshingListener;//列表刷新监听器
	private boolean isFirstUpScroll = true;//是否上滑
	private boolean isFirstDownScroll = true;//是否下滑
	private int school_id;//学校ID
	private int mArea;//城市码
	private int mTag;//运动项目ID
	private String mStringTag;//运动项目
	private int mType;//用于区别
	private int mPosition;
	private int user_account;
	private String url = "";
	private int start = 0;
	private ArrayList<RankingList> list;
	private RankingActivity activity;
	public int user_power_num = 0;
	public int user_ranking = 0;
	public boolean isRefreshComplete = false;
	private boolean isDownRefresh = true;
	public RankingFragment() {
		super();
	}

	public RankingFragment(RankingActivity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contextView = inflater.inflate(R.layout.fragment_ranking, container, false);
		ViewUtils.inject(this, contextView);
		AccessToken.getAPP2Token();
		school_id = (int) getArguments().get("school_id");
		mArea = (int) getArguments().get("area");
		mTag = (int) getArguments().get("tag");
		mStringTag = (String) getArguments().get("StringTag");
		mType = (int) getArguments().get("type");
		mPosition = (int) getArguments().get("position");
		user_account = (int) getArguments().get("user_account");
		if(activity!=null)
		activity.setOnFragmentContentRefresh(mPosition, new onFragmentContentRefresh() {

			@Override
			public void onRefreshed(int area, int type, int schoolId, int tag) {
				mArea = area;
				mType = type;
				school_id = schoolId;
				onPullDownToRefresh(ptrListView);
			}
		});
		initURL();
		initListView();
		return contextView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * @Title: initURL
	 * @Description: 设置URL
	 * @param
	 * @return void
	 * @throws
	 */
	private void initURL() {
		if (mType != 0) {
			if (mType == 1) {
				url = new StringBuffer(APPConstants.URL_RANKING).append(MyApplication.app2Token).append("&user_account=").append(user_account).append("&sport_tag_id=")
						.append(mTag).append("&type=1").append("&sc_id=").append(mArea).append("&start=").append(start).append("&limit=15").toString();
			} else if (mType == 2) {//学校
				url = new StringBuffer(APPConstants.URL_RANKING).append(MyApplication.app2Token).append("&user_account=").append(user_account).append("&sport_tag_id=")
						.append(mTag).append("&type=2").append("&school_id=").append(school_id).append("&start=").append(start).append("&limit=15").toString();
			}
		}
	}

	private void initListView() {
		ptrListView.setMode(Mode.BOTH);
		// 下拉刷新时的提示文本设置
		ptrListView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新");
		ptrListView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
		ptrListView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");
		// 上拉加载更多时的提示文本设置
		ptrListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
		ptrListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		ptrListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
		listView = ptrListView.getRefreshableView();
		list = new ArrayList<RankingList>();
		ptrListView.setOnScrollListener(this);
		ptrListView.setOnRefreshListener(this);
		ptrListView.setRefreshing(false);
		ptrListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), PersonInformationActivity.class);
				if (list.get(position - 1) != null) {
					intent.putExtra("by_user_account", list.get(position - 1).getUser_account() + "");
				}
				startActivity(intent);
			}
		});
		adapter = new RankingAdapter(getActivity(), list);
		listView.setAdapter(adapter);
	}

	/**
	 * @ClassName: RankingTask
	 * @Description: 获取排名信息
	 * @author 肖邦
	 * @date 2015-1-27 下午4:07:33
	 */
	class RankingTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ptrListView.setVisibility(View.INVISIBLE);
		}
		@Override
		protected String[] doInBackground(Void... params) {
			MyApplication.map.put(mStringTag, new Object[] { 0, 0, false,0 });
			String[] json = MyHttpClient.getJsonFromService(getActivity(), url);
			return json;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			ptrListView.setVisibility(View.VISIBLE);
			if (result != null) {
				try {
					if ("200".equals(result[0])) {
						if (result[1] != null && !"".equals(result[1])) {
							JSONObject object = new JSONObject(result[1]);
							String s = object.optJSONArray("ranking_list").toString();
							user_power_num = object.optInt("user_power_num");
							user_ranking = object.optInt("user_ranking");
							List<RankingList> List = JSON.parseArray(s, RankingList.class);
							if(isDownRefresh){
								if(list.size()==0){
									list.addAll(List);
								}
							}else{
								list.addAll(List);
							}
						}
					} else {
						Toast.makeText(activity, ParseErrorJsonUtils.getErrorMsg(result).getError(), Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(activity, "加载失败", Toast.LENGTH_LONG).show();
				user_ranking = 0;
				user_power_num = 0;
			}
			adapter.notifyDataSetChanged();
			MyApplication.map.put(mStringTag, new Object[] { user_ranking, user_power_num, true,list.size() });
			ptrListView.onRefreshComplete();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		isDownRefresh = true;
		start = 0;
		list.clear();
		initURL();
		AccessToken.getAPP2Token();
		MyApplication.map.put(mStringTag, new Object[] { user_ranking, user_power_num, false,0 });
		if (refreshingListener != null) {
			refreshingListener.onRefreshing();
		}
		new RankingTask().execute();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		isDownRefresh = false;
		start += 15;
		initURL();
		AccessToken.getAPP2Token();
		MyApplication.map.put(mStringTag, new Object[] { user_ranking, user_power_num, false,0 });
		if (refreshingListener != null) {
			refreshingListener.onRefreshing();
		}
		new RankingTask().execute();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			scrollFlag = true;
		} else {
			scrollFlag = false;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (scrollFlag) {
			if (listener != null) {
				if (firstVisibleItem > lastVisibleItemPosition) {
					listener.onUpScrolled(isFirstUpScroll);
					isFirstUpScroll = false;
					isFirstDownScroll = true;
				}
				if (firstVisibleItem < lastVisibleItemPosition) {
					listener.onDownScrolled(isFirstDownScroll);
					isFirstUpScroll = true;
					isFirstDownScroll = false;
				}
				if (firstVisibleItem == lastVisibleItemPosition) {
					return;
				}
			}
			lastVisibleItemPosition = firstVisibleItem;
		}
	}

	public interface onListViewScrollListener {
		public void onUpScrolled(boolean isUpScroll);

		public void onDownScrolled(boolean isDownScroll);
	}

	public void setOnListViewScrollListener(onListViewScrollListener listener) {
		this.listener = listener;
	}

	public interface onRefreshingListener {
		public void onRefreshing();
	}

	public void setOnRefreshingListener(onRefreshingListener listener) {
		refreshingListener = listener;
	}
}
