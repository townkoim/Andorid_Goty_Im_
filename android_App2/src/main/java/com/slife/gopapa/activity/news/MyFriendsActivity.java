package com.slife.gopapa.activity.news;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.adapter.MyfriendsAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.dao.JsonArrayErrorDao;
import com.slife.gopapa.dao.impl.JsonArrayErrorDaoImpl;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.ChatUserInfo;

/***
 * @ClassName: MyFriendsActivity
 * @Description: TODO我的好友列表 1、加载好友之前先开启动画效果，让ListView不可见
 *               2、等待数据加载完成，关闭动画效果，让ListView显示
 * @author 菲尔普斯
 * @date 2015-1-16 上午10:15:04
 * 
 */
@ContentView(R.layout.activity_myfriends)
public class MyFriendsActivity extends BaseActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;
	@ViewInject(R.id.myfriends_listview)
	private ListView listView; // 列表
	@ViewInject(R.id.myfriends_img_circle)
	private ImageView imgCircle;// 动画
	private MyfriendsAdapter adapter; // 适配器
	private List<ChatUserInfo> list;// 好友数据集合
	private Animation animation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		tvTitle.setText(R.string.news_my_friends);
		list = new ArrayList<>(); // 适配器的数据集合
		adapter = new MyfriendsAdapter(MyFriendsActivity.this, list); // 适配器
		listView.setAdapter(adapter);
		if(MyApplication.myLoginState){//判断当前是否已经登陆服务器
				new MyFriendsTask().execute();// 执行加载好友的异步任务
		}else{
			Toast.makeText(MyFriendsActivity.this, "正在尝试登陆服务器......", Toast.LENGTH_SHORT).show();
			
		}
		// 列表监听器
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MyFriendsActivity.this,
						ChatActivity.class);
				intent.putExtra("users_info", list.get(position));//将好友的用户信息返回
				startActivity(intent);
			}
		});
	}

	@OnClick({ R.id.common_title_back })
	public  void imgBackOnclick(View v) {
		MyFriendsActivity.this.finish();
	}

	/**
	 * @Title: startAnimation
	 * @Description:开启动画
	 * @param
	 * @return void
	 * @throws
	 */
	private void startAnimation() {
		animation = AnimationUtils.loadAnimation(MyFriendsActivity.this,
				R.anim.update_new_version_animation); // 加载动画
		LinearInterpolator lin = new LinearInterpolator();
		animation.setInterpolator(lin);
		imgCircle.startAnimation(animation); // 为控件设置动画
	}

	/**
	 * @ClassName: MyFriendsTask
	 * @Description: 开启异步任务,加载还有列表
	 * @author 菲尔普斯
	 * @date 2015-1-19 下午2:36:44
	 * 
	 */
	private class MyFriendsTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startAnimation();
		}

		@Override
		protected String[] doInBackground(Void... params) {
			String[] result = MyHttpClient.postDataToService(MyFriendsActivity.this,
					APPConstants.URL_APP2_HOST_NAME
							+ APPConstants.URL_FRIENDS_LISTSAPP2,
					MyApplication.app2Token, new String[] { "user_account" },
					new String[] { MyApplication.preferences.getString(
							DBConstants.USER_ACCOUNT, null) }, null, null);
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			//加载完毕之后让动画关闭，而且让动画不显示，再让ListView显示
			imgCircle.clearAnimation();
			imgCircle.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			JsonArrayErrorDaoImpl.resolveJson(MyFriendsActivity.this, result,
					new JsonArrayErrorDao() {

						@Override
						public void disposeJsonArray(JSONArray array) {
							if (array != null && array.length() > 0) {
								List<ChatUserInfo> listFriends = new ArrayList<>();
								for (int i = 0; i < array.length(); i++) {
									try {
										JSONObject obj = array.getJSONObject(i);
										ChatUserInfo friends = new ChatUserInfo();
										friends.setUser_account(obj.optString("friend_user_account"));
										friends.setExtend_user_account(obj.optString("friend_extend_user_account"));
										if (obj.getString("user_logo_200") != null&& !"".equals(obj.getString("user_logo_200"))) {
											friends.setUser_logo(obj.getString("user_logo_200"));
										}
										friends.setUser_nickname(obj.optString("user_nickname"));
										listFriends.add(friends);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								list.addAll(listFriends);
								adapter.notifyDataSetChanged();
							}
						}
					});
		}
	}

}
