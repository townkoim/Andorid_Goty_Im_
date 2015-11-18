
package com.slife.gopapa.service;

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
 * 
* @ClassName: PostPersonInfoService
* @Description: TODO(这里用一句话描述这个类的作用)
* @author 肖邦
* @date 2015-2-27 上午10:02:43
*
 */
public class PostPersonInfoService extends Service{
	private Intent intent;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		AppManager.addService(PostPersonInfoService.this);
	}
	
	/** (非 Javadoc) 
	* Title: onStart
	* Description:
	* @param intent
	* @param startId
	* @deprecated
	* @see android.app.Service#onStart(android.content.Intent, int)
	*/ 
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		this.intent = intent;
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
			String[] result = MyHttpClient.postDataToService(getApplicationContext(), APPConstants.URL_UPDATEINFOAPP2, MyApplication.app2Token, new String[] {
					"user_account", "user_info" }, new String[] { MyApplication.preferences.getString("user_account", ""), intent.getStringExtra("user_info") }, null, null);
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			if (result != null && Integer.parseInt(result[0]) == 200) {
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result[1]);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
