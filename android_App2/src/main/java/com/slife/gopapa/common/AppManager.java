package com.slife.gopapa.common;

import java.util.ArrayList;
import java.util.List;

import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.dao.ChatListener;

import android.app.Activity;
import android.app.Service;

/**
 * @ClassName: AppManager
 * @Description: 用来管理所有的Activity.每一个Activity都会加载到activityList集合当中。
 *               每一个Service都会加载到serviceList当中 当程序需要退出的时候关闭所有的Activity和所有的service
 * @author 菲尔普斯
 * @date 2015-1-12 上午11:19:59
 * 
 */
public class AppManager {
	private static List<Activity> activityList = new ArrayList<>();
	private static List<Service> serviceList = new ArrayList<>();

	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public static void addService(Service service) {
		serviceList.add(service);
	}

	public static void existAPP() {
		if (MyApplication.mLocationClient != null) {
			MyApplication.mLocationClient.stop();
		}
		if (MyApplication.gotyeApi != null) {
			MyApplication.gotyeApi.logout();
			MyApplication.gotyeApi.removeAllChatListener();
			MyApplication.gotyeApi.removeAllLoginListener();
			MyApplication.gotyeApi.removeAllUserListener();
			MyApplication.gotyeApi.removeAllServerListener();
			MyApplication.gotyeApi = null;
		}
		ChatListener.mVoiceSendTime=0;
		// 全部变量全部变为false
		MyApplication.senderUser = null;
		MyApplication.targetUser = null;
		MyApplication.myLoginState = false;
		MyApplication.isTabActivity = false;
		MyApplication.isInitNewsFragment = false;
		MyApplication.gotyeState = false;
		MyApplication.verifyUserServiceRuning = false;
		MyApplication.trackPointMsgCount = 0;
		for (int i = 0; i < activityList.size(); i++) {
			activityList.get(i).finish();
		}
		for (int j = 0; j < serviceList.size(); j++) {
			serviceList.get(j).stopSelf();
		}
	}
}
