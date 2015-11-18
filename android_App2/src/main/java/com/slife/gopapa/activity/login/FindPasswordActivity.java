package com.slife.gopapa.activity.login;

import org.json.JSONObject;

import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseNoServiceActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.http.AccessToken;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.task.VerifyCodeTask;
import com.slife.gopapa.view.MyProgressDialog;
import com.slife.gopapa.view.TimeCountDown;

/**
 * @ClassName: FindPasswordActivity
 * @Description: 找回密码界面(忘记密码则进入此界面找回密码，因为内测，目前短信下发到手机功能不做，只做简单的获取，直接填充验证码)
 * @author 菲尔普斯
 * @date 2015-1-4 下午5:03:17
 * 
 */
@ContentView(R.layout.activity_find_password)
public class FindPasswordActivity extends BaseNoServiceActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;// 返回按钮
	@ViewInject(R.id.common_title_name)
	private TextView txTitleName; // 标题名字
	@ViewInject(R.id.find_pwd_phonenumber)
	private EditText etPhone; // 电话号码
	@ViewInject(R.id.find_pwd_autocode)
	private EditText etAuthCode;// 验证码
	@ViewInject(R.id.find_pwd_password1)
	private EditText etPassword1;// 密码
	@ViewInject(R.id.find_pwd_password2)
	private EditText etPassword2;// 重复密码
	@ViewInject(R.id.find_pwd_get_autocode)
	private TextView btnSendAutoCode;// 发送验证码
	@ViewInject(R.id.find_pwd_submit)
	private Button btnSubmit; // 提交
	private TimeCountDown timeCountDown;// 倒计时器

	private VerifyCodeTask verifyCodeTask;
	private String mobile;// 手机号码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(FindPasswordActivity.this);
		txTitleName.setText(R.string.find_password);
	}

	/**
	 * @Title: titleOnclick
	 * @Description: 标题栏的监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.common_title_back })
	public  void titleOnclick(View v) {
		if (v.getId() == R.id.common_title_back) { // 返回按钮
			if (timeCountDown != null) {
				timeCountDown.onFinish();
			}
			FindPasswordActivity.this.finish();
		}
	}

	/**
	 * @Title: sendAutoCodeOnclick
	 * @Description: 发送验证码监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.find_pwd_get_autocode })
	public  void sendAutoCodeOnclick(View v) {
		// 判断公用的token是否为空，为空则加载一次token
		if (MyApplication.commonToken == null|| "".equals(MyApplication.commonToken)) {
			AccessToken.getCommonToken(FindPasswordActivity.this);
		}
		timeCountDown = new TimeCountDown(90000, 1000, btnSendAutoCode);
		mobile = etPhone.getText().toString();
		if (mobile != null && !"".equals(mobile) && mobile.length() == 11) {
			if(NetWorkState.checkNet(FindPasswordActivity.this)){	//检测是否有网络
				timeCountDown.start(); // 开启计时器
				verifyCodeTask = new VerifyCodeTask(FindPasswordActivity.this,
						etPhone.getText().toString(), "2",etAuthCode);
				verifyCodeTask.execute();
			} else {
				Toast.makeText(FindPasswordActivity.this, "检查网络连接是否可用",Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(FindPasswordActivity.this, "手机号码不正确",
					Toast.LENGTH_SHORT).show();
		}
		
	}

	/***
	 * @Title: submitOnclick
	 * @Description:提交数据到服务器（找回密码）
	 * @param @param v
	 * @return void
	 * @throws  	
	 */
	@OnClick({ R.id.find_pwd_submit })
	public  void submitOnclick(View v) {
		if (timeCountDown != null) {
			timeCountDown.onFinish();
		}
		if(checkDataValidity()){
		if(NetWorkState.checkNet(FindPasswordActivity.this)){
			new ResertPasswordTask().execute(); // 开启异步任务去提交数据到服务器
		}else{
			Toast.makeText(FindPasswordActivity.this, "请检查网络连接",Toast.LENGTH_SHORT).show();
		}
		}
		
	}
	
	private boolean checkDataValidity() {
		String pwd1 = etPassword1.getText().toString();
		String pwd2 = etPassword2.getText().toString();
		String auth_code=etAuthCode.getText().toString();
		if ( auth_code!= null&& !"".equals(auth_code)&&auth_code.length()==6) {
			if (pwd1.length() < 23 && pwd1.length() > 5 ) {
				if (pwd1.equals(pwd2)) {
					return true;
				} else {
					Toast.makeText(FindPasswordActivity.this, "两次密码不一致...",Toast.LENGTH_SHORT).show();
					return false;
				}
			} else {
				Toast.makeText(FindPasswordActivity.this, "密码必须为6-22位",Toast.LENGTH_SHORT).show();
				return false;
			}
		} else {
			Toast.makeText(FindPasswordActivity.this, "验证码不正确!", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
	}

	/***
	 * @ClassName: ResertPasswordTask
	 * @Description:找回密码的异步任务(线程)
	 * @author 菲尔普斯
	 * @date 2015-1-28 下午4:18:56
	 * 
	 */
	class ResertPasswordTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			MyProgressDialog.showDialog(FindPasswordActivity.this, "找回密码",
					"密码验证中......");
		}

		@Override
		protected String[] doInBackground(Void... params) {
			String result[] = MyHttpClient.postDataToService(FindPasswordActivity.this,
					APPConstants.URL_COMMON_HOST_NAME
							+ APPConstants.URL_RESETPASSWORD,
					MyApplication.commonToken, new String[] { "user_mobile",
							"verify_code", "new_pwd" }, new String[] { mobile,
							etAuthCode.getText().toString(),
							etPassword1.getText().toString() }, null, null);
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			MyProgressDialog.closeDialog();// 关闭对话框
			JsonObjectErrorDaoImpl.resolveJson(FindPasswordActivity.this,
					result, new JsonObjectErrorDao() {
						@Override
						public void disposeJsonObj(JSONObject obj) {
							Toast.makeText(FindPasswordActivity.this, "修改密码成功!",
									Toast.LENGTH_SHORT).show();
							Editor editor = MyApplication.preferences.edit();
							editor.putString(DBConstants.USER_LOGIN_NAME,etPhone.getText().toString());
							editor.putString(DBConstants.USER_PASSWORD,etPassword1.getText().toString());
							editor.commit();
//							startActivity(new Intent(FindPasswordActivity.this,TabMainActivity.class));
							FindPasswordActivity.this.finish();
						}
					});
		}
	}
}
