package com.slife.gopapa.activity;

import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.AppManager;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;

/***
* @ClassName: BaseNoServiceActivity 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author 菲尔普斯
* @date 2015-2-3 下午4:11:48 
*
 */
public class BaseNoServiceActivity extends Activity{
	public Activity activity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//去除标题栏
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);	//隐藏键盘
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	//设置只能竖屏
		activity = this;
		AppManager.addActivity(this);
		if(MyApplication.IMEI==null||"".equals(MyApplication.IMEI)){
			MyApplication.IMEI=((TelephonyManager)this.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
		}
	}

}
