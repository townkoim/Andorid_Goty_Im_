package com.slife.gopapa.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.news.AboutMatchActivity;
import com.slife.gopapa.activity.news.ChatActivity;
import com.slife.gopapa.activity.news.NewsFriendsActivity;
import com.slife.gopapa.activity.news.OffcialAssistantActivity;
import com.slife.gopapa.adapter.NewsAdapter;
import com.slife.gopapa.adapter.NewsAdapter.ViewHolder;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.database.DBHelperOperation;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.ChatUserInfo;
import com.slife.gopapa.model.RecentNews;
import com.slife.gopapa.utils.ListUtils;

/**
 * @ClassName: NewsFragment
 * @Description: 消息界面
 * @author 菲尔普斯
 * @date 2015-1-6 下午3:46:55
 * 
 */
public class NewsFragment extends Fragment implements OnItemClickListener,OnClickListener {
	@ViewInject(R.id.news_relayout_offcial_assistant)
	private RelativeLayout offcialAssistantLayout;
	@ViewInject(R.id.news_offcial_assistant_trackpoint)
	private Button offcialAssistant; // 官方消息的小红点
	@ViewInject(R.id.news_tv_offcial_assistant_message)
	private TextView offcialAssistantMessage;// 官方消息内容

	@ViewInject(R.id.news_relayout_about_match)
	private RelativeLayout aboutMatchLayout;
	@ViewInject(R.id.news_about_match_trackpoint)
	private Button aboutMatch;// 约赛小红点
	@ViewInject(R.id.news_tv_about_match_message)
	private TextView aboutMatchMessage;// 约赛消息

	@ViewInject(R.id.news_relayout_new_friends)
	private RelativeLayout newFriendsLayout;
	@ViewInject(R.id.news_new_friends_trackpoint)
	private Button newFriends;// 新的朋友小红点
	@ViewInject(R.id.news_tv_about_match_message)
	private TextView newFriendsMessage;// 新的朋友消息
	@ViewInject(R.id.news_listview)
	private ListView newsListView; // 消息列表
	@ViewInject(R.id.news_img_circle)
	private ImageView imgCircle;
	private Animation animation;

	private NewsAdapter adapter; // 消息列表适配器
	private List<RecentNews> list;
	private NewsReceiver receiver;
	private GetUserTask task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		receiver = new NewsReceiver();
		MyApplication.isInitNewsFragment = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_news, container, false);
		ViewUtils.inject(this, view);
		View headView = inflater.inflate(R.layout.listview_head_news_fragment,null);
		ViewUtils.inject(this, headView);
		offcialAssistantLayout.setOnClickListener(this);
		aboutMatchLayout.setOnClickListener(this);
		newFriendsLayout.setOnClickListener(this);
		newsListView.addHeaderView(headView);
		list = new ArrayList<>();
		startAnimation();
		// 延时加载...为了不让ListView卡顿
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
		}, 500);
		return view;
	}
	@Override
	public void onResume() {
		super.onResume();
		initParams();
	}
	

	/**
	 * @Title: initParams
	 * @Description:初始化参数(官方助手、约赛、新的朋友小红点)
	 * @param
	 * @return void
	 * @throws
	 */
	private void initParams() {
		int newOfficialAssitantCount = MyApplication.preferences.getInt(
				DBConstants.NEW_OFFCIAL_ASSISTANT_COUNT, 0);
		if (newOfficialAssitantCount > 0) {
			offcialAssistant.setVisibility(View.VISIBLE);
			offcialAssistant.setText(String.valueOf(newOfficialAssitantCount)); 
		}
		int newFriendsCount = MyApplication.preferences.getInt(
				DBConstants.NEW_FRIENDS_COUNT, 0);
		if (newFriendsCount > 0) {
			newFriends.setVisibility(View.VISIBLE);
			newFriends.setText(String.valueOf(newFriendsCount));
		}
		int newAboutMatchCount = MyApplication.preferences.getInt(
				DBConstants.NEW_ABOUT_MATCH_COUNT, 0);
		if (newAboutMatchCount > 0) {
			aboutMatch.setVisibility(View.VISIBLE);
			aboutMatch.setText(String.valueOf(newAboutMatchCount));
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(APPConstants.ACTION_NEWS_DBCHANGE);
		filter.addAction(APPConstants.ACTION_ONLINE_MESSAGE);
		filter.addAction(APPConstants.ACTION_OFFLINE_MESSAGE);
		filter.addAction(APPConstants.ACTION_NEW_FRIENDS);
		filter.addAction(APPConstants.ACTION_NEW_ABOUT_MATCH);
		filter.addAction(APPConstants.ACTION_NEW_OFFICIAL_ASSISTANT);
		getActivity().registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
	}

	/**
	 * @Title: startAnimation
	 * @Description:开启动画(在去加载数据库的操作之前进行动画操作)
	 * @param
	 * @return void
	 * @throws
	 */
	private void startAnimation() {
		animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.update_new_version_animation);
		LinearInterpolator lin = new LinearInterpolator();
		animation.setInterpolator(lin);
		imgCircle.startAnimation(animation);
	}

	// 延时加载...为了不让ListView卡顿
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (animation != null) {
				animation.cancel();
			}
			imgCircle.clearAnimation();
			imgCircle.setVisibility(View.GONE);
			newsListView.setVisibility(View.VISIBLE);
			adapter = new NewsAdapter(getActivity(), list);
			newsListView.setAdapter(adapter);
			newsListView.setOnItemClickListener(NewsFragment.this);
			getNewsData();
		};
	};

	/***
	 * @Title: relayoutOnclick
	 * @Description:listView上面三个列表的点击事件
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@Override
	public void onClick(View v) {
		Editor editor = MyApplication.preferences.edit();
		int id = v.getId();
		Intent intent = new Intent();
		if(id==R.id.news_relayout_offcial_assistant){
			MyApplication.trackPointMsgCount -= Integer.valueOf(offcialAssistant.getText().toString());
			editor.putInt(DBConstants.NEW_OFFCIAL_ASSISTANT_COUNT, 0);
			editor.commit();
			offcialAssistant.setText("0");
			offcialAssistant.setVisibility(View.GONE);
			intent.setClass(getActivity(), OffcialAssistantActivity.class);
		}else if (id == R.id.news_relayout_about_match) {
			MyApplication.trackPointMsgCount -= Integer.valueOf(aboutMatch.getText().toString());
			editor.putInt(DBConstants.NEW_ABOUT_MATCH_COUNT, 0);
			editor.commit();
			aboutMatch.setText("0");
			aboutMatch.setVisibility(View.GONE);
			intent.setClass(getActivity(), AboutMatchActivity.class);
		} else if (id == R.id.news_relayout_new_friends) {
			MyApplication.trackPointMsgCount -= Integer.valueOf(newFriends.getText().toString());
			editor.putInt(DBConstants.NEW_FRIENDS_COUNT, 0);
			editor.commit();
			newFriends.setText("0");
			newFriends.setVisibility(View.GONE);
			intent.setClass(getActivity(), NewsFriendsActivity.class);
		}
		Intent intent1 = new Intent();
		intent1.setAction(APPConstants.ACTION_TRACKPOINT_CHANGE);
		getActivity().sendBroadcast(intent1);
		getActivity().startActivity(intent);
	}

	/**
	 * Title: onItemClick Description: ListView的点击事件
	 * 
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(position>0){
		RecentNews news = list.get(position - 1);
		// 改变数据库这个itmen对象的消息数量为0了。并且通知tabActivity的小红点未读消息发生了改变
		int count = DBHelperOperation
				.queryRecentContactMsgCountByExtendAccount(news
						.getUser_extend_account());
		Intent intentTrack = new Intent();
		MyApplication.trackPointMsgCount = MyApplication.trackPointMsgCount
				- count;
		intentTrack.setAction(APPConstants.ACTION_TRACKPOINT_CHANGE);
		getActivity().sendBroadcast(intentTrack);

		// 让小红点消失
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.trackPoint.setVisibility(View.GONE);
		// 先将小红点撤销,1.删除数据库的未读消息条数，二。让小红点dimiss
		DBHelperOperation.updataRecentContactMsgCount(0,
				news.getUser_extend_account());
		list.get(position - 1).setMsgCount(0);
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		ChatUserInfo userInfo = new ChatUserInfo(news.getUser_extend_account(),
				news.getUser_nick_name(), news.getUser_logo());
		intent.putExtra("users_info", userInfo);
		getActivity().startActivity(intent);
		}
	}

	/**
	 * @Title: getNewsData
	 * @Description: 得到数据库最近联系人的信息
	 * @param
	 * @return void
	 * @throws
	 */
	private void getNewsData() {
		list.addAll(DBHelperOperation.getRecentNewsData());
		adapter.notifyDataSetChanged();
		if (NetWorkState.checkNet(getActivity())) {
			if (MyApplication.myLoginState && list.size() > 0) {
				task = new GetUserTask(list);
				task.execute();
			}
		}
	}

	/***
	 * @Title: listDataChange
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param news
	 * @return void
	 * @throws
	 */
	private void listDataChange(RecentNews news) {
		if (ListUtils.hasMessage(list, news)) { // 如果当前集合里面存在这个news对象(通过聊天账号来确定)
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getUser_extend_account()
						.equals(news.getUser_extend_account())) {
					list.get(i).setTime(news.getTime());
					list.get(i).setUser_last_message(
							news.getUser_last_message());
					list.get(i).setMsgCount(
							news.getMsgCount() + list.get(i).getMsgCount());
					break;
				}
			}
		} else {
			list.add(news);
		}
		Collections.sort(list);
		adapter.notifyDataSetChanged();
	}

	/**
	 * @ClassName: NewsReceiver
	 * @Description: 广播接收器
	 * @author 菲尔普斯
	 * @date 2015-1-22 下午4:40:02
	 * 
	 */
	public class NewsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(APPConstants.ACTION_NEWS_DBCHANGE)
					|| intent.getAction().equals(
							APPConstants.ACTION_ONLINE_MESSAGE)) { // 聊天界面按返回键的时候更新消息列表（按时间进行）
				RecentNews dbNews = (RecentNews) intent
						.getSerializableExtra("recent_news");
				if (dbNews != null && !"".equals(dbNews)) {
					listDataChange(dbNews);
					List<RecentNews> lists = new ArrayList<>();
					lists.add(dbNews);
					new GetUserTask(lists).execute();
				}
			} else if (intent.getAction().equals(
					APPConstants.ACTION_OFFLINE_MESSAGE)) {
				List<RecentNews> listOffLine = (List<RecentNews>) intent
						.getSerializableExtra("recent_news_list");
				if (listOffLine != null && listOffLine.size() > 0) {
					for (int i = 0; i < listOffLine.size(); i++) {
						listDataChange(listOffLine.get(i));
					}
					new GetUserTask(listOffLine).execute();
				}
			} else if (intent.getAction().equals(
					APPConstants.ACTION_NEW_FRIENDS)) { // 新的朋友的推送消息
				int count = MyApplication.preferences.getInt(DBConstants.NEW_FRIENDS_COUNT, 0);
				if(count>0){
				newFriends.setVisibility(View.VISIBLE);
				newFriends.setText(String.valueOf(count));
				}
			} else if (intent.getAction().equals(
					APPConstants.ACTION_NEW_ABOUT_MATCH)) { // 约赛消息
				int count = MyApplication.preferences.getInt(DBConstants.NEW_ABOUT_MATCH_COUNT, 0);
				if(count>0){
					aboutMatch.setVisibility(View.VISIBLE);
					aboutMatch.setText(String.valueOf(count));
				}
			}else if(intent.getAction().equals(APPConstants.ACTION_NEW_OFFICIAL_ASSISTANT)){//官方消息
				int count = MyApplication.preferences.getInt(DBConstants.NEW_OFFCIAL_ASSISTANT_COUNT, 0);
				if(count>0){
					offcialAssistant.setVisibility(View.VISIBLE);
					offcialAssistant.setText(String.valueOf(count));
				}
			}
		}

	}

	/***
	 * @ClassName: GetUserTask
	 * @Description: 根据用户的啪啪号得到这个用户的信息
	 * @author 菲尔普斯
	 * @date 2015-1-28 上午11:40:28
	 * 
	 */
	public class GetUserTask extends AsyncTask<Void, Void, String[]> {
		private List<RecentNews> listNews; // 最近消息的数据源
		private String[] extendAccount; // 用来保存消息列表中所有人的聊天 账号

		public GetUserTask(List<RecentNews> listNews) {
			super();
			this.listNews = listNews;
		}

		@Override
		protected String[] doInBackground(Void... params) {
			// 将集合当中的所有聊天账号循环加载到extendAccount数组当中
			extendAccount = new String[listNews.size()];
			for (int i = 0; i < extendAccount.length; i++) {
				extendAccount[i] = list.get(i).getUser_extend_account();
			}
			return MyHttpClient.postDataToService(getActivity(),
					APPConstants.URL_APP2_HOST_NAME
							+ APPConstants.URL_GETEXTEND_INFOAPP2,
					MyApplication.app2Token,
					new String[] { "extend_user_account" },
					new String[] { com.alibaba.fastjson.JSONArray
							.toJSONString(extendAccount) }, null, null);
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			JsonObjectErrorDaoImpl.resolveJson(getActivity(), result,
					new JsonObjectErrorDao() {

						@Override
						public void disposeJsonObj(JSONObject obj) {
							for (int i = 0; i < extendAccount.length; i++) {
								JSONObject obj1 = null;
								try {
									obj1 = obj.getJSONObject(extendAccount[i]);
								} catch (JSONException e) {
									e.printStackTrace();
								}
								if (obj1 != null) {
									for (int j = 0; j < list.size(); j++) {
										// 根据数据中的下标和数据源的下标来确定返回的这个人的信息，然后修改此人的信息（头像，昵称），然后修改数据库当中的数据
										if (extendAccount[i].equals(list.get(j)
												.getUser_extend_account())) {
											list.get(j)
													.setUser_target_account(
															obj1.optString("user_account"));
											list.get(j)
													.setUser_logo(
															obj1.optString("user_logo_200"));
											list.get(j)
													.setUser_nick_name(
															obj1.optString("user_nickname"));
											DBHelperOperation
													.updataRecentContactUserInfo(
															list.get(j)
																	.getUser_nick_name(),
															list.get(j)
																	.getUser_logo(),
															list.get(j)
																	.getUser_extend_account());

										}
									}
								}
							}
							adapter.notifyDataSetChanged();
						}
					});
		}
	}

}
