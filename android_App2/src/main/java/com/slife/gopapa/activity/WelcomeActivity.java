package com.slife.gopapa.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.login.LoginActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.database.DBConstants;

/***
 * @ClassName: WelcomeActivity
 * @Description: Logo欢迎界面
 * @author 菲尔普斯
 * @date 2015-1-14 下午5:51:45
 * 
 */
@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends Activity {
	private SharedPreferences.Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//去除标题栏
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);	//隐藏键盘
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	//设置只能竖屏
		AppManager.addActivity(WelcomeActivity.this);//将所有集成了父类的Activity添加到集合
		if(MyApplication.IMEI==null||"".equals(MyApplication.IMEI)){	//得到设备的IMEI号
			MyApplication.IMEI=((TelephonyManager)this.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
		}
		ViewUtils.inject(this);
		MyApplication.isInitWelcomeActivity=true;
		isFirstAccess(); // 判断是否第一次登陆
	}


	/***
	 * @Title: isFirstAccess
	 * @Description: 用来判断是否第一次登陆，如果是第一次登陆就进入引导界面
	 * 				 如果不是第一次进入程序，则去数据库上次登陆保存起来的登陆账号和密码。如果账号和密码不为空则跳转到TabMainActivity，此时开启后台服务，去请求网络验证账号和密码的准确性
	 * 				 如果从数据库取出来的账号密码为空则跳转到登陆界面
	 * @param
	 * @return void
	 * @throws
	 */
	private void isFirstAccess() {
		final Intent intent = new Intent();
		boolean isFirstAccess = MyApplication.preferences.getBoolean(DBConstants.ISFIRSTACCESS, true); //得到数据库存放的当前是否第一次进入程序
		if (isFirstAccess) { 					//如果当前是第一次进入程序，就设置跳转对象是GuideActivity，并且修改数据库的ISFIRSTACCESS
			intent.setClass(WelcomeActivity.this, GuideActivity.class);
			editor = MyApplication.preferences.edit();
			editor.putBoolean(DBConstants.ISFIRSTACCESS, false);
			editor.commit();
		} else {								//表示不是第一次进入程序，但是此时要判断数据库存的是否有账号和密码。
			String loginName=MyApplication.preferences.getString(DBConstants.USER_LOGIN_NAME, null);
			if(loginName==null||"".equals(loginName)){			//说明登陆的用户名是空的，则表示没有登陆过，就设置跳转对象为LoginActivity
				intent.setClass(WelcomeActivity.this, LoginActivity.class);
			}else{							//说明是有账号和密码，然后启动后台服务。去验证用户名和密码是否正确。设置跳转对象为TabMainActivity
				intent.setClass(WelcomeActivity.this, TabMainActivity.class);
			}
		}
		
		// 在此处延迟三秒钟再去跳转到Activity界面
				new CountDownTimer(2000, 10) {		

					@Override
					public void onTick(long millisUntilFinished) {
						
					}

					@Override
					public void onFinish() {
						startActivity(intent);
						WelcomeActivity.this.finish();
					}
				}.start();
	}

}
