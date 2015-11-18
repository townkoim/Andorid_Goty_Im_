package com.slife.gopapa.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.gotye.api.Gotye;
import com.gotye.api.GotyeLoginListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.slife.gopapa.activity.login.LoginActivity;
import com.slife.gopapa.activity.login.RegisterSuccessActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.http.AccessToken;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.ChatUserInfo;

/***
 * @ClassName: VerifyUserService
 * @Description: 登陆服务器的后台服务
 * @author 菲尔普斯
 * @date 2015-1-15 下午3:59:22
 * 
 */
public class VerifyUserService extends Service implements GotyeLoginListener {
	private String userAccount; // 用户登录名
	private String userPassword; // 用户密码
	private VerifyUserTask task; // 验证用户名与密码的异步任务
	private int responseCout = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		AppManager.addService(VerifyUserService.this);
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		MyApplication.verifyUserServiceRuning = true;
		userAccount = MyApplication.preferences.getString(
				DBConstants.USER_LOGIN_NAME, null); // 从数据库取得登陆名
		userPassword = MyApplication.preferences.getString(
				DBConstants.USER_PASSWORD, null); // 从数据库取得登陆密码
		if (userAccount != null && !"".equals(userAccount)
				&& !"".equals(userPassword) && userPassword != null
				&& NetWorkState.checkNet(this)) {
			// 执行异步任务
			responseCout=0;
			task = new VerifyUserTask();
			task.execute();
		} else {
			MyApplication.verifyUserServiceRuning = false;
			VerifyUserService.this.stopSelf();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * @Title: loginGotye
	 * @Description: 登陆亲加聊天账号
	 * @param
	 * @return void
	 * @throws
	 */

	private void loginGotye() {
		if(MyApplication.senderUser!=null&&
				MyApplication.senderUser.getExtend_user_account()!=null&&
				!"".equals(MyApplication.senderUser.getExtend_user_account())){
		MyApplication.httpUtils.send(
				HttpMethod.GET,
				APPConstants.URL_GOTYEREGIST1
						+ MyApplication.senderUser.getExtend_user_account()
						+ APPConstants.URL_GOTYEREGIST2,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						MyApplication.verifyUserServiceRuning = false;
						VerifyUserService.this.stopSelf();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						MyApplication.gotyeApi = Gotye.getInstance().makeGotyeAPIForUser(MyApplication.senderUser.getExtend_user_account());// 创建用户api，api实例是对同一个username单例，不会重复创建新的实例
						MyApplication.gotyeApi.addLoginListener(VerifyUserService.this); // 添加登陆回调监听事件
						MyApplication.gotyeApi.login(null); // 发起登陆

					}
				});
		}

	}

	@Override
	public void onLogin(String arg0, String arg1, int arg2) {
		if (arg2 == 0) {
			MyApplication.gotyeState = true;
			MyApplication.gotyeApi.removeLoginListener(VerifyUserService.this);
			startService(new Intent(getApplicationContext(),ChatService.class));
			VerifyUserService.this.stopSelf();
			MyApplication.verifyUserServiceRuning = false;
		} else {
			loginGotye();
		}
		MyApplication.verifyUserServiceRuning = false;
		VerifyUserService.this.stopSelf();
	}

	@Override
	public void onLogout(String arg0, String arg1, int arg2) {

	}

	/***
	 * @ClassName: VerifyUserTask
	 * @Description: 验证用户登陆状态的异步任务
	 * @author 菲尔普斯
	 * @date 2015-1-15 下午4:20:34
	 * 
	 */
	public class VerifyUserTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected String[] doInBackground(Void... params) {
			responseCout += 1;
			String[] result = MyHttpClient.postDataToService(
					getApplicationContext(), APPConstants.URL_APP2_HOST_NAME
							+ APPConstants.URL_USER_LOGIN,
					MyApplication.commonToken, new String[] { "user_name",
							"user_pwd", "facility", "client_id", "is_ios" },
					new String[] { userAccount, userPassword,
							MyApplication.IMEI, MyApplication.clientID, "2" },
					null, null);

			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			String errorMsg = null; // 错误信息
			String errorCode = null; // 错误码
			String error = null;// 请求接口成功，但是json返回的是正确的还是错误
			if (result != null && result.length > 0) {
				try {
					JSONObject obj = new JSONObject(result[1]);
					error = obj.optString("error_code");
					errorMsg = obj.optString("error");
					errorCode = obj.optString("api_code");
					if (Integer.valueOf(result[0]) == 200) { // 代表获取Json成功
						if (error == null || "".equals(error)) {// 代表登陆成功，取不到错误信息
							obj.optString("is_sign_vitality");// 此处暂时保留。。。。。等待活力功能完善
							MyApplication.myLoginState = true; // 设置我的当前状态为登录状态
							MyApplication.senderUser = new ChatUserInfo();
							MyApplication.senderUser.setUser_account(obj
									.optString("user_account"));
							MyApplication.senderUser.setExtend_user_account(obj
									.optString("extend_user_account"));
							MyApplication.senderUser.setUser_logo(obj.optString("user_logo_200"));
							MyApplication.senderUser.setUser_nickname(obj.optString("user_nickname"));
							Editor editor = MyApplication.preferences.edit();
							editor.putString(DBConstants.USER_ACCOUNT,obj.optString("user_account"));
							editor.putString(DBConstants.USER_EXTEND_ACCOUNT,obj.optString("extend_user_account"));
							editor.commit();
							MyApplication.pushState=obj.optBoolean("is_bind");//取得是否绑定了clidentID
							loginGotye();
							// showToast("登陆成功");
							if (obj.optString("user_nickname") == null
									|| "".equals(obj.optString("user_nickname"))) {
								Intent intent = new Intent(
										getApplicationContext(),
										RegisterSuccessActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}
						}
					} else {
						// 如果返回的错误消息，提示用户
						if (error != null && !"".equals(errorMsg)) {
							if (error.equals("401")|| errorCode.equals("40101")) {
								AccessToken.getCommonToken(getApplicationContext());
								AccessToken.getAPP2Token();
								doTaskAgain();
							} else if (Integer.valueOf(errorCode) == 4000120) { // 说明clientID未设定,需要重新登录服务器
								doTaskAgain();
							} else {
								if (NetWorkState.checkNet(getApplicationContext())) { // 检测是否有网络
									Editor editor = MyApplication.preferences.edit(); // 修改数据库里面保存的用户信息，全部为null
									editor.putInt(DBConstants.NEW_FRIENDS_COUNT, 0);
									editor.putInt(DBConstants.NEW_ABOUT_MATCH_COUNT, 0);
									editor.putInt(DBConstants.NEW_OFFCIAL_ASSISTANT_COUNT, 0);
									editor.commit();
									Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(intent);
									AppManager.existAPP();
								}
								showToast(errorMsg);
								MyApplication.verifyUserServiceRuning = false;
								VerifyUserService.this.stopSelf();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				MyApplication.verifyUserServiceRuning = false;
				VerifyUserService.this.stopSelf();
				showToast("请检查网络连接是否可用");

			}
		}
	}

	/**
	 * @Title: doTaskAgain
	 * @Description: 再次执行异步任务
	 * @param
	 * @return void
	 * @throws
	 */
	private void doTaskAgain() {
		if (responseCout >= 5) { // 如果请求了三次了，就弹出网络不可用
			if(MyApplication.isInitWelcomeActivity){
				showToast("请检查网络连接是否可用");
			}
			responseCout=0;
		} else {
			new VerifyUserTask().execute();
		}
	}

	/***
	 * @Title: showToast
	 * @Description: 在Service中需要Toast则需要handler来实现
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void showToast(final String message) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {

			@Override
			public void run() {
				if(MyApplication.isInitWelcomeActivity){
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
