package com.slife.gopapa.activity.mine.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.task.UpdatePasswordTask;

/**
* @ClassName: UpdatePasswordActivity 
* @Description: 修改密码界面
* @author 菲尔普斯
* @date 2015-1-9 上午10:28:04 
*
 */
@ContentView(R.layout.activity_update_password)
public class UpdatePasswordActivity extends BaseActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack; //返回
	@ViewInject(R.id.common_title_name)
	private TextView tvTitleName; //标题栏名字
	@ViewInject(R.id.common_title_right)
	private TextView tvTitleRight; //标题栏的完成
	@ViewInject(R.id.update_password_password)
	private EditText etPassword;//密码
	@ViewInject(R.id.update_password_new_password1)
	private EditText etNewPassword1;//新密码
	@ViewInject(R.id.update_password_new_password2)
	private EditText etNewPassword2;//确认新密码
	@ViewInject(R.id.update_password_show_password)
	private ImageView imgShowPassword; //是否显示密码
	private boolean isShowPassword=true;//当前密码显示未***代表false
	
	private MyReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		tvTitleName.setText(R.string.setting_update_password);
		tvTitleRight.setText(R.string.complete);
		receiver = new MyReceiver();
	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("finish");
		registerReceiver(receiver, intentFilter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	@OnClick({ R.id.common_title_back, R.id.common_title_right })
	public  void imgBackOnclick(View v) {
		int id = v.getId();
		if (id == R.id.common_title_back) {	//返回按钮
			UpdatePasswordActivity.this.finish();
		} else if (id == R.id.common_title_right) { //下一步按钮(提交修改密码)
			String pwd = etPassword.getText().toString();
			String newPwd1 = etNewPassword1.getText().toString();
			String newPwd2 = etNewPassword2.getText().toString();
			if(newPwd1!=null&&!"".equals(newPwd1)&&newPwd1.length()>=6&&newPwd1.length()<21&&newPwd1.equals(newPwd2)){
				if (MyApplication.myLoginState&&NetWorkState.checkNet(UpdatePasswordActivity.this)) {
				new UpdatePasswordTask(UpdatePasswordActivity.this,pwd, newPwd1).execute();
				}else{
					Toast.makeText(UpdatePasswordActivity.this, "网络断开或连接服务器不成功", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(UpdatePasswordActivity.this, "请检查密码", Toast.LENGTH_SHORT).show();
			}
		}
	}
	/***
	* @Title: showPasswordOnclick
	* @Description: 显示密码与不显示密码的开关监听器
	* @param @param v
	* @return void
	* @throws
	 */
	@OnClick({ R.id.update_password_show_password })
	public  void showPasswordOnclick(View v) {
		if(!isShowPassword){
			imgShowPassword.setImageResource(R.drawable.regist_read_select);
			etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);    
			etNewPassword1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);    
			etNewPassword2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);    
			isShowPassword=true;
		}else{
			imgShowPassword.setImageResource(R.drawable.regist_read_unselect);
			etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); 
			etNewPassword1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); 
			etNewPassword2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); 
			isShowPassword=false;
		}
	}
	
	private class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("finish")){
				finish();
			}
		}
	}
}
