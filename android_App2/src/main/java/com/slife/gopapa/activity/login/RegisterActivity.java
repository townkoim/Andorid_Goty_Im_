package com.slife.gopapa.activity.login;

import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.slife.gopapa.activity.mine.setting.AgreenmentActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.task.VerifyCodeTask;
import com.slife.gopapa.view.MyProgressDialog;
import com.slife.gopapa.view.TimeCountDown;

/**
 * @ClassName: RegisterActivity
 * @Description: 新用户注册界面
 * @author 菲尔普斯
 * @date 2015-1-4 下午2:10:09
 * 
 */
@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseNoServiceActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;// 返回按钮
	@ViewInject(R.id.common_title_name)
	private TextView txTitleName; // 标题名字
	@ViewInject(R.id.common_title_right)
	private TextView txTitleRight; // 标题栏右侧文字
	@ViewInject(R.id.register_phonenumber)
	private EditText etPhone; // 电话号码
	@ViewInject(R.id.register_autocode)
	private EditText etAuthCode;// 验证码
	@ViewInject(R.id.register_password1)
	private EditText etPassword1;// 密码
	@ViewInject(R.id.register_password2)
	private EditText etPassword2;// 重复密码
	@ViewInject(R.id.register_get_autocode)
	private TextView btnSendAutoCode;// 发送验证码
	@ViewInject(R.id.register_reader)
	private ImageView imgReader;
	@ViewInject(R.id.register_reader_text)
	private TextView tvAgreement;

	private boolean isReader = false;// 判断是否已经阅读协议，默认为false
	private TimeCountDown timeCountDown;// 倒计时器
	private String phoneNumber;// 电话号码
	private VerifyCodeTask verifycodeTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(RegisterActivity.this);
		txTitleName.setText(R.string.mobile_register);
		txTitleRight.setText(R.string.common_next_step);
		tvAgreement.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =  new Intent(RegisterActivity.this,AgreenmentActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * @Title: imgReaderOnclick
	 * @Description:是否已经阅读协议的监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.register_reader })
	public  void imgReaderOnclick(View v) {
		if (!isReader) {
			imgReader.setImageResource(R.drawable.regist_read_select);
			isReader = true;
		} else {
			imgReader.setImageResource(R.drawable.regist_read_unselect);
			isReader = false;
		}
	}

	/**
	 * @Title: titleOnclick
	 * @Description: 标题栏的监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.common_title_back, R.id.common_title_right })
	public  void titleOnclick(View v) {
		if (timeCountDown != null) {	//停止倒计时
			timeCountDown.onFinish();
		}
		if (v.getId() == R.id.common_title_back) { // 返回按钮
			RegisterActivity.this.finish();
		} else if (v.getId() == R.id.common_title_right) { // 下一步按钮
			if (timeCountDown != null) {
				timeCountDown.onFinish();
				if (checkDataValidity()) { // 提交数据到服务器
					if(isReader){
						if(NetWorkState.checkNet(RegisterActivity.this)){
							new RegisterTask().execute();
						}else{
							Toast.makeText(RegisterActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(RegisterActivity.this, "还没同意用户协议", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}

	/**
	 * @Title: checkData
	 * @Description: 检查数据的合法性
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	private boolean checkDataValidity() {
		String pwd1 = etPassword1.getText().toString();
		String pwd2 = etPassword2.getText().toString();
		String auth_code=etAuthCode.getText().toString();
		if ( auth_code!= null&& !"".equals(auth_code)&&auth_code.length()==6) {
			if (pwd1.length() < 23 && pwd1.length() > 5) {
				if (pwd1.equals(pwd2)) {
					return true;
				} else {
					Toast.makeText(RegisterActivity.this, "两次密码不一致...",Toast.LENGTH_SHORT).show();
					return false;
				}
			} else {
				Toast.makeText(RegisterActivity.this, "密码必须为6-22位",Toast.LENGTH_SHORT).show();
				return false;
			}
		} else {
			Toast.makeText(RegisterActivity.this, "验证码不正确!", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	/**
	 * @Title: sendAutoCodeOnclick
	 * @Description: 发送验证码监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.register_get_autocode })
	public  void sendAutoCodeOnclick(View v) {
		phoneNumber = etPhone.getText().toString();
		if (phoneNumber.length() == 11) {
			timeCountDown = new TimeCountDown(90000, 1000, btnSendAutoCode);
			if (NetWorkState.checkNet(RegisterActivity.this)) { // 判断是否有网络，在去执行异步任务
				timeCountDown.start();
				// 启动异步任务去提交数据到服务器
				verifycodeTask = new VerifyCodeTask(RegisterActivity.this,
						phoneNumber, "1",etAuthCode); // 1代表当前是注册用户
				verifycodeTask.execute();
			} else {
				Toast.makeText(RegisterActivity.this, "检查网络连接是否可用",Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(RegisterActivity.this, "手机号码不正确", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/***
	 * @ClassName: RegisterTask
	 * @Description:提交注册信息到服务器的异步任务
	 * @author 菲尔普斯
	 * @date 2015-1-14 下午2:51:03
	 * 
	 */
	private class RegisterTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			MyProgressDialog.showDialog(RegisterActivity.this, "注册用户",
					"正在注册...请稍等");
		}

		@Override
		protected String[] doInBackground(Void... params) {
			String[] result = MyHttpClient.postDataToService(RegisterActivity.this,
					APPConstants.URL_COMMON_HOST_NAME
							+ APPConstants.URL_USER_REGISTER,
					MyApplication.commonToken, new String[] { "user_mobile",
							"verify_code", "user_pwd" }, new String[] {
							etPhone.getText().toString(),
							etAuthCode.getText().toString(),
							etPassword1.getText().toString() }, null, null);
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			MyProgressDialog.closeDialog();
			JsonObjectErrorDaoImpl.resolveJson(RegisterActivity.this, result, new JsonObjectErrorDao() {
				
				@Override
				public void disposeJsonObj(JSONObject obj) {
					// 跳转到注册成功界面，并将服务器返回来的数据传递给下个界面
					Intent intent = new Intent(RegisterActivity.this,RegisterSuccessActivity.class);
					intent.putExtra("user_login_number", etPhone.getText().toString());//用户注册的手机号码
					intent.putExtra("user_account",obj.optString("user_account"));//返回的啪啪号
					intent.putExtra("extend_user_account",obj.optString("extend_user_account")); //聊天号
					intent.putExtra("user_nickname",obj.optString("user_nickname")); //昵称
					intent.putExtra("user_password", etPassword1.getText().toString()); //密码
					intent.putExtra("is_first_registered", true); //是否第一次进入注册成功
					startActivity(intent);
					RegisterActivity.this.finish();	
				}
			});
		}
	}
}
