package com.slife.gopapa.activity.news;

import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.activity.ranking.PersonInformationActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.ChatUserInfo;
import com.slife.gopapa.model.SearchUserInfo;
import com.slife.gopapa.task.AddFriendsDisponseTask;
import com.slife.gopapa.view.MyProgressDialog;
import com.slife.gopapa.view.XCRoundRectImageView;

/**
 * @ClassName: AddFriendsActivity
 * @Description: 搜索内容-添加好友界面
 * @author 菲尔普斯
 * @date 2015-1-7 下午2:13:42
 * 
 */
@ContentView(R.layout.activity_search_friends)
public class SearchFriendsActivity extends BaseActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack; // 返回按钮
	@ViewInject(R.id.common_title_name)
	private TextView txTitleName; // 标题名字
	@ViewInject(R.id.search_friends_layout)
	private RelativeLayout layout; // 搜索结果
	@ViewInject(R.id.search_friends_icon)
	private XCRoundRectImageView imgIcon; // 用户头像
	@ViewInject(R.id.search_friends_name)
	private TextView tvName; // 用户姓名
	@ViewInject(R.id.search_friends_signature)
	private TextView tvSignature; // 用户个性签名
	@ViewInject(R.id.search_friends_search)
	private ImageView imgSearch; // 搜索按钮
	@ViewInject(R.id.search_friends_content)
	private EditText etContent; // 要搜索的文本内容
	@ViewInject(R.id.search_friends_sendmsg)
	private Button btnSendMsg;// 发送消息
	@ViewInject(R.id.search_friends_add)
	private Button btnAddFriends;//添加好友
	private SearchUserInfo info;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(SearchFriendsActivity.this);
		txTitleName.setText(R.string.news_add_friends);
		info = new SearchUserInfo();
	}

	/**
	 * @Title: onclick
	 * @Description: 搜索，标题栏返回键 监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.common_title_back, R.id.search_friends_search })
	public  void onclick(View v) {
		if (v.getId() == R.id.common_title_back) {
			SearchFriendsActivity.this.finish();
		} else if (v.getId() == R.id.search_friends_search) {// 搜索
			String message = etContent.getText().toString();
			if (message != null && !"".equals(message)) {
				if (MyApplication.myLoginState) {// 判断当前是否已经登陆服务器
					new SearchFriendsTask().execute();
				}
			} else {
				Toast.makeText(SearchFriendsActivity.this, "搜索内容不能为空",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * @Title: btnOnclick
	 * @Description:添加好友，发送消息的监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.search_friends_sendmsg, R.id.search_friends_add })
	public  void btnOnclick(View v) {
		if (v.getId() == R.id.search_friends_sendmsg) {	//发送消息
			ChatUserInfo userInfo = new ChatUserInfo();
			userInfo.setUser_account(info.getUser_account());
			userInfo.setExtend_user_account(info.getUser_extends_account());
			userInfo.setUser_logo(info.getUser_logo());
			userInfo.setUser_nickname(info.getUser_nickname());
			Intent intent = new Intent();
			intent.setClass(SearchFriendsActivity.this, ChatActivity.class);
			intent.putExtra("users_info",userInfo);
			startActivity(intent);
		} else if (v.getId() == R.id.search_friends_add) { //添加为好友
			new AddFriendsDisponseTask(SearchFriendsActivity.this,MyApplication.senderUser.getUser_account(),info.getUser_account()).execute();
		}
	}

	/****
	 * @Title: iconOnclick
	 * @Description: 好友头像
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.search_friends_icon, R.id.search_friends_layout })
	public  void iconOnclick(View v) {
		if (info.getUser_account() != null
				&& !"".equals(info.getUser_account())) {
			Intent intent = new Intent();
			intent.setClass(SearchFriendsActivity.this,
					PersonInformationActivity.class);
			intent.putExtra("by_user_account", info.getUser_account());
			startActivity(intent);
		}
	}

	/***
	 * @ClassName: SearchFriendsTask
	 * @Description: 搜索好友异步任务
	 * @author 菲尔普斯
	 * @date 2015-1-19 下午6:06:34
	 * 
	 */
	private class SearchFriendsTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			MyProgressDialog.showDialog(SearchFriendsActivity.this, "搜索好友",
					"正在搜索");
		}

		@Override
		protected String[] doInBackground(Void... params) {
			return MyHttpClient.postDataToService(SearchFriendsActivity.this,
					APPConstants.URL_APP2_HOST_NAME
							+ APPConstants.URL_FRIENDS_SERACHAPP2,
					MyApplication.app2Token,
					new String[] { "user_account", "user_name" },
					new String[] {
							MyApplication.preferences.getString(
									DBConstants.USER_LOGIN_NAME, null),
							etContent.getText().toString() }, null, null);
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			MyProgressDialog.closeDialog();
			JsonObjectErrorDaoImpl.resolveJson(SearchFriendsActivity.this,
					result, new JsonObjectErrorDao() {

						@Override
						public void disposeJsonObj(JSONObject obj) {
							imgIcon.setVisibility(View.VISIBLE);
							btnSendMsg.setVisibility(View.VISIBLE);
							info.setUser_nickname(obj.optString("user_nickname"));
							info.setUser_account(obj.optString("user_account"));
							info.setUser_logo(obj.optString("user_logo_200"));
							info.setUser_sign(obj.optString("user_sign"));
							info.setIs_friend(obj.optBoolean("is_friend"));
							info.setUser_extends_account(obj.optString("extend_user_account"));
							tvName.setText(info.getUser_nickname());
							tvSignature.setText(info.getUser_sign());
							String iconUrl = info.getUser_logo();
							if (iconUrl != null && !"".equals(iconUrl)&& !"null".equals(iconUrl)) {
								MyApplication.bitmapUtils.display(imgIcon, iconUrl);
							}
							if(!info.isIs_friend()){
								btnAddFriends.setVisibility(View.VISIBLE);
							}
							if(obj.optString("user_nickname")==null||"".equals(obj.optString("user_nickname"))){
								Toast.makeText(SearchFriendsActivity.this, "未找到用户", Toast.LENGTH_SHORT).show();
							}
						}
					});
		}
	}
}
