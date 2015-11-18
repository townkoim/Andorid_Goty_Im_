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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.activity.ranking.PersonInformationActivity;
import com.slife.gopapa.adapter.NewsNewFriendsAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.dao.JsonArrayErrorDao;
import com.slife.gopapa.dao.impl.JsonArrayErrorDaoImpl;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.ContactsPerson;
import com.slife.gopapa.view.MyProgressDialog;

/**
* @ClassName: NewsFriendsActivity 
* @Description:新的朋友消息界面(处理好友请求会在此界面展示)
* @author 菲尔普斯
* @date 2015-1-7 下午2:14:25 
*
 */
@ContentView(R.layout.activity_news_friends)
public class NewsFriendsActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.news_friends_listview)
	private ListView listView;
	@ViewInject(R.id.common_title_back)
	private ImageView imgTitleBack;
	@ViewInject(R.id.common_title_name)
	private TextView tvTitleName;

	private NewsNewFriendsAdapter adapter; //适配器
	private List<ContactsPerson>lists;   //数据源

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(NewsFriendsActivity.this);
		tvTitleName.setText(R.string.news_new_friends);
		imgTitleBack.setOnClickListener(this);
		lists=new ArrayList<>();
		adapter = new NewsNewFriendsAdapter(NewsFriendsActivity.this,lists);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent=new Intent(NewsFriendsActivity.this,PersonInformationActivity.class);
				intent.putExtra("by_user_account", lists.get(position).getUser_account());
				startActivity(intent);
			}
		});
		if(MyApplication.myLoginState){	//判断当前是否已经登陆服务器
			new NewsFriendsTask().execute();
		}else{
			Toast.makeText(NewsFriendsActivity.this, "正在尝试登陆服务器......", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		NewsFriendsActivity.this.finish();
	}
	
	/***
	* @ClassName: NewsFriendsTask 
	* @Description: 新朋友的异步任务去加载数据
	* @author 菲尔普斯
	* @date 2015-1-30 下午4:43:17 
	*
	 */
	private class NewsFriendsTask extends AsyncTask<Void, Void, String[]>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			MyProgressDialog.showDialog(NewsFriendsActivity.this, "新的朋友", "正在加载......");
		}
		@Override
		protected String[] doInBackground(Void... params) {
			
			return MyHttpClient.postDataToService(NewsFriendsActivity.this,APPConstants.URL_APP2_HOST_NAME+APPConstants.URL_FRIENDS_LISTAPPLYAPP2, MyApplication.app2Token,
					new String[]{"user_account"}, new String[]{MyApplication.preferences.getString(DBConstants.USER_ACCOUNT, null)}, null, null);
		}
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			MyProgressDialog.closeDialog();
			JsonArrayErrorDaoImpl.resolveJson(NewsFriendsActivity.this, result, new JsonArrayErrorDao() {
				
				@Override
				public void disposeJsonArray(JSONArray array) {
					if(array!=null&&!"".equals(array)){
						List<ContactsPerson>listPerson = new ArrayList<>();
					for(int i=0;i<array.length();i++){
						try {
							JSONObject obj = array.getJSONObject(i);
							ContactsPerson friend = new ContactsPerson();
							friend.setUser_account(obj.optString("send_user_account"));
							friend.setUser_nickname(obj.optString("user_nickname"));
							friend.setExtend_user_account(obj.optString("extend_user_account"));
							friend.setStatus(obj.optString("status"));
							friend.setUser_logo(obj.optString("user_logo_200"));
							listPerson.add(friend);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					lists.addAll(listPerson);
					adapter.notifyDataSetChanged();
					}
				}
			});
		}
		
	}

}
