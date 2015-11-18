package com.slife.gopapa.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.service.VerifyUserService;

/**
* @ClassName: BaseActivity 
* @Description: 所有需要继承了Activity的子类(需要启动后台登陆服务的)必须继承BaseActivity，用来初始化一些全局对象、变量等
* 				初始化的内容：  1、去除标题栏
* 						  2、隐藏键盘（防止界面EditText获取焦点强制弹出键盘）
* 						  3、设置只能竖屏（多屏幕切换回切换生命周期，导致程序不稳定）
* 						  4、只要继承了该父类BaseActivity的子类，都将会吧自己添加到AllActivityManager的activityList的集合里面
* 						  5、判断当前有没有登录，如果没有登录则开启服务VerifyUserService去登录服务器
* @author 菲尔普斯
* @date 2015-1-28 下午3:47:22 
*
 */
public class BaseActivity extends BaseNoServiceActivity{
	private String loginName; // 用户登陆的名
	private String password; // 用户密码
	public Activity activity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//判断登陆状态
		if(!MyApplication.myLoginState||!MyApplication.gotyeState){
				startService();
			}
	}

	/**
	 * @Title: startService
	 * @Description: 根据数据库的登录名和密码去服务器验证正确性，和加活力值
	 * @param
	 * @return void
	 * @throws
	 */
	private void startService() {
		loginName = MyApplication.preferences.getString(
				DBConstants.USER_LOGIN_NAME, null); // 从数据库取得登陆名
		password = MyApplication.preferences.getString(
				DBConstants.USER_PASSWORD, null); // 从数据库取得登陆密码
		if (loginName != null && !"".equals(loginName) && password != null&& !"".equals(password)) {
			if(!MyApplication.myLoginState||!MyApplication.gotyeState){
				if(!MyApplication.verifyUserServiceRuning){
					if(NetWorkState.checkNet(getApplicationContext())){
						startService(new Intent(BaseActivity.this,VerifyUserService.class));
					}
				}
			}
		}
	}
}
