package com.slife.gopapa.activity.login;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gotye.api.Gotye;
import com.gotye.api.GotyeLoginListener;
import com.igexin.sdk.PushManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.http.AccessToken;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.ChatUserInfo;
import com.slife.gopapa.service.ChatService;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * @ClassName: LoginActivity
 * @Description: 用户登陆界面
 * @author 菲尔普斯
 * @date 2015-1-4 下午2:09:27
 * 
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends Activity implements GotyeLoginListener {
	@ViewInject(R.id.login_username)
	private EditText etUserName;// 用户名
	@ViewInject(R.id.login_pswd)
	private EditText etPassword; // 密码
	@ViewInject(R.id.login_conceal)
	private ImageView imgConceal; // 显示密码开关
	@ViewInject(R.id.login_land)
	private Button btnLogin;// 登陆
	@ViewInject(R.id.login_findpsw)
	private TextView tvFindPassword; // 找回密码
	@ViewInject(R.id.login_newusers)
	private TextView txNewUser; // 新用户
	private boolean isConceal = false; // 设置是否显示密码。
	private long mExitTime;// 计算按两次返回键的间隔
	private String name; // 用户名
	private String password; // 密码
	private SharedPreferences.Editor editor;
	private LoginTask loginTask;
	private String pushUserAccount = null;// 接收强制退出发送过来的啪啪号
	private int taskCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 去除标题栏
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // 隐藏键盘
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 设置只能竖屏
		AppManager.addActivity(LoginActivity.this);
		ViewUtils.inject(LoginActivity.this);
		try {
			pushUserAccount = getIntent().getStringExtra("push_user_account");
		} catch (Exception e) {
		}
		if (pushUserAccount != null && !"".equals(pushUserAccount)) {
			etUserName.setText(pushUserAccount);
		}
	}

	/**
	 * @Title: concealOnclick
	 * @Description: 显示与不显示密码监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.login_conceal })
	public void concealOnclick(View v) {
		if (isConceal) { // 如果当前显示密码的开关是打开的，则就关闭它，并设置相对应的图片进行改变。以及文本改为*****
			imgConceal.setImageResource(R.drawable.common_unconceal);
			etPassword.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			isConceal = false;
		} else {// 如果当前显示密码的开关是关闭的，则就开启它，并设置相对应的图片进行改变。以及文本改为可见的
			imgConceal.setImageResource(R.drawable.common_conceal);
			etPassword
					.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			isConceal = true;
		}
		// 设置EditText的光标在最后一个
		etPassword.setSelection(etPassword.getText().toString().length());
	}

	/**
	 * @Title: loginOnclick
	 * @Description: 登陆监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.login_land })
	public void loginOnclick(View v) {
		name = etUserName.getText().toString();
		password = etPassword.getText().toString();
		if (etUserName != null && !"".equals(name) && password != null
				&& !"".equals(password)) { // 登陆的时候先要判断用户所填写的账号和密码是否为空
			MyProgressDialog.showDialog(LoginActivity.this, "登陆", "正在登陆...请稍等");
			loginTask = new LoginTask(); // 执行登陆异步任务
			loginTask.execute();
		}
	}

	/**
	 * @Title: txSkip
	 * @Description: 新用户和忘记密码监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.login_findpsw, R.id.login_newusers })
	public void txSkip(View v) {
		Intent intent = new Intent();
		if (v.getId() == R.id.login_findpsw) { // 找回密码的按钮监听器
			intent.setClass(LoginActivity.this, FindPasswordActivity.class);
		} else if (v.getId() == R.id.login_newusers) { // 注册用户，新用户的按钮
			intent.setClass(LoginActivity.this, RegisterActivity.class);
		}
		startActivity(intent);
	}

	/**
	 * Title: onKeyDown Description: 重写返回键，两次返回键退出程序
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				AppManager.existAPP();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * @Title: loginGotye
	 * @Description:先根据登陆成功之后的返回的聊天账号在亲加服务器进行注册。 注册成功之后则直接登陆秦加，如果注册失败，则继续注册
	 * @param
	 * @return void
	 * @throws
	 */
	private void loginGotye() {
		MyApplication.httpUtils.send(
				HttpMethod.GET,
				APPConstants.URL_GOTYEREGIST1
						+ MyApplication.preferences.getString(
								DBConstants.USER_EXTEND_ACCOUNT, null)
						+ APPConstants.URL_GOTYEREGIST2,
				new RequestCallBack<String>() {
					
					//这个重写方法说明访问成功
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						MyApplication.gotyeApi = Gotye.getInstance()
								.makeGotyeAPIForUser(MyApplication.senderUser.getExtend_user_account());// 创建用户api，api实例是对同一个username单例，不会重复创建新的实例
						MyApplication.gotyeApi.addLoginListener(LoginActivity.this); // 添加登陆回调监听事件
						MyApplication.gotyeApi.login(null); // 发起登陆
					}

					@Override
					public void onFailure(com.lidroid.xutils.exception.HttpException arg0,String arg1) {
						loginGotye();
					}
				});
	}

	/**
	 * Title: onLogin Description: 登陆亲加的回调
	 * @param arg0
	 * @param arg1
	 * @param arg2   返回的code 如果code为0，表示登陆亲加成功，登陆成功则启动ChatService服务
	 */
	@Override
	public void onLogin(String arg0, String arg1, int arg2) {
		if (arg2 == 0) {
			MyApplication.gotyeState = true;
			startService(new Intent(LoginActivity.this, ChatService.class));
		} else {
			loginGotye();
		}
	}

	@Override
	public void onLogout(String arg0, String arg1, int arg2) {
		System.out.println();
	}

	/***
	 * @ClassName: LoginTask
	 * @Description: 
	 *               根据用户所填写的账号和密码，发起登陆，请求服务器接口（在onPostExecute通过JsonObjectErrorDao接口进行回调
	 *               ）
	 *               在回调里面（表示登陆成功），修改数据库的资料（登录名，啪啪号，密码等）。修改全局变量MyApplication.senderUser的属性的值
	 * @author 菲尔普斯
	 * @date 2015-1-14 下午5:29:04
	 * 
	 */
	private class LoginTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			taskCount += 1;
				MyApplication.clientID=PushManager.getInstance().getClientid(getApplicationContext());
				if(MyApplication.clientID==null){
					MyApplication.clientID="";
				}
		}

		@Override
		protected String[] doInBackground(Void... params) {
			String[] result = MyHttpClient.postDataToService(
					LoginActivity.this, APPConstants.URL_APP2_HOST_NAME+ APPConstants.URL_USER_LOGIN,
					MyApplication.app2Token, new String[] { "user_name","user_pwd", "facility", "client_id", "is_ios" },
					new String[] { name, password, MyApplication.IMEI,MyApplication.clientID, "-1" }, null, null);
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
							loginSuccess(obj);
							MyApplication.pushState=obj.optBoolean("is_bind");//取得是否绑定了clidentID
						} else {
							Toast.makeText(LoginActivity.this, errorMsg,
									Toast.LENGTH_SHORT).show();
						}
						MyProgressDialog.closeDialog();
					} else {
						// 如果返回的错误消息，提示用户
						if (error != null && !"".equals(errorMsg)) {
							if (error.equals("401")|| errorCode.equals("40101")) {
								AccessToken.getCommonToken(LoginActivity.this);
								AccessToken.getAPP2Token();
								doTaskAgain();
							} else if (Integer.valueOf(errorCode) == 4000120) { // 说明clientID未设定,需要重新登录服务器
								doTaskAgain();
							} else {
								Toast.makeText(LoginActivity.this, errorMsg,Toast.LENGTH_SHORT).show();
								MyProgressDialog.closeDialog();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				MyProgressDialog.closeDialog();
				Toast.makeText(LoginActivity.this, "请检查网络连接是否可用",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/***
	 * @Title: loginSuccess
	 * @Description: 登陆自己的服务器成功之后，初始化全局变量以及修改数据库的值
	 * @param @param obj 登陆成功之后而却返回正确的结果的json对象
	 * @return void
	 * @throws
	 */
	private void loginSuccess(JSONObject obj) {
		editor = MyApplication.preferences.edit();
		// 登陆成功之后把服务器返回的数据（啪啪号），以及登陆名，密码都保存到数据库（SharedPreferences）
		editor.putString(DBConstants.USER_LOGIN_NAME, name);
		editor.putString(DBConstants.USER_PASSWORD, etPassword.getText()
				.toString());
		editor.putString(DBConstants.USER_ACCOUNT,
				obj.optString("user_account"));
		editor.putString(DBConstants.USER_EXTEND_ACCOUNT,
				obj.optString("extend_user_account"));
		editor.commit();
		// 此处取得活力，根据活力的判断跳转到活力界面还是？
		obj.optString("is_sign_vitality");
		MyApplication.myLoginState = true;
		// 让全局变量senderUser我的个人资料给填充完毕
		if (MyApplication.senderUser == null
				|| "".equals(MyApplication.senderUser)) {
			MyApplication.senderUser = new ChatUserInfo();
		}
		MyApplication.senderUser.setUser_account(obj.optString("user_account"));
		MyApplication.senderUser.setExtend_user_account(obj
				.optString("extend_user_account"));
		MyApplication.senderUser.setUser_logo(obj.optString("user_logo_200"));
		MyApplication.senderUser.setUser_nickname(obj
				.optString("user_nickname"));
		// 登陆自己服务器成之后，还需要登陆秦加聊天服务器
		loginGotye();
		// 跳转到TabActivity
		Intent intent = new Intent();
		if (obj.optString("user_nickname") == null
				|| "".equals(obj.optString("user_nickname"))) {
			intent.setClass(LoginActivity.this, RegisterSuccessActivity.class);
		} else {
			intent.setClass(LoginActivity.this, LoginedActivity.class);
		}
		intent.putExtra("isLogin", true);
		startActivity(intent);
		LoginActivity.this.finish();
	}

	/**
	 * @Title: doTaskAgain
	 * @Description: 再次执行异步任务
	 * @param
	 * @return void
	 * @throws
	 */
	private void doTaskAgain() {
		if (taskCount >= 5) { // 如果请求了三次了，就弹出网络不可用
			Toast.makeText(LoginActivity.this, "请检查网络连接是否可用",
					Toast.LENGTH_SHORT).show();
			MyProgressDialog.closeDialog();
			taskCount=0;
		} else {
			handler.sendEmptyMessage(0);
		}
	}
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			new LoginTask().execute();
		};
	};

}
