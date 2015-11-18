package com.slife.gopapa.service;

import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.http.MyHttpClient;

/**
 * @ClassName: UpdateSportTagService
 * @Description:  当接受到约赛消息时会触发这个服务,重新加载用户的运动项目
 * @author 肖邦
 * @date 2015-3-4 下午2:15:14
 */
public class UpdateSportTagService extends Service {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		AppManager.addService(UpdateSportTagService.this);
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		try {
			new PostTask().execute();
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * @ClassName: PostTask
	 * @Description: 修改用户资料(updateInfoAPP2)资料的异步任务
	 * @author 肖邦
	 * @date 2015-1-19 上午9:12:25
	 * 
	 */
	class PostTask extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... params) {
			String[] result = MyHttpClient.getJsonFromService(getApplicationContext(), APPConstants.URL_USER_SPORT_TAG + MyApplication.app2Token + "&user_account="
					+ MyApplication.preferences.getString("user_account", ""));
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			Set<String> nameSet = new TreeSet<String>();// 运动标签集合
			if (result != null && Integer.parseInt(result[0]) == 200) {
				// 运动标签
				// 运动标签存到手机里
				for (int i = 1; i < 100; i++) {
					try {
						JSONObject jsonObject = new JSONObject(result[1]);// 运动标签数据
						if (jsonObject.optJSONObject(String.valueOf(i)) != null) {// 获取从0到100的键取值
							nameSet.add(jsonObject.optJSONObject(String.valueOf(i)).optString("tag_name"));// 在集合中加入元素
						}
					} catch (JSONException e) {
					}
				}
				// 如果遍历之后集合内的数据还是为空就在集合头部加上"none",以便后面程序判断是否有数据
				if (nameSet.isEmpty()) {
					nameSet.add("none");
				}
				MyApplication.preferences.edit().putStringSet("sport_tag", nameSet).commit();// 保存到手机
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
