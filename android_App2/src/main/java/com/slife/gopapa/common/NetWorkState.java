package com.slife.gopapa.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @ClassName: NetWorkState
 * @Description:相关的网络状态类的工具方法建议放在本文件。
 * 				需要的权限：android.permission.INTERNET
 * 						android.permission.ACCESS_NETWORK_STATE
 * 						android.permission.CHANGE_NETWORK_STATE
 * @author 菲尔普斯
 * @date 2014-12-8 下午2:45:53
 * 
 */
public class NetWorkState {

	
	/**
	 * @Description: 使用ConnectivityManager判断网络连接是否存在。
	 * @param context 上下文
	 * @return boolean 只要连接上wifi或者运营商网络就会返回true。所以本函数并无法保证肯定能上网，因为
	 * 					存在已经连上wifi但是这个wifi无法访问internet的情况。
	 * @throws
	 */
	public static boolean checkNet(Context context) {
		try {
			// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivityManager != null) {
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				// 获取网络连接管理的对象
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/***
	* @Title: isWIFI
	* @Description: 判断 当前网络是否处于WIFI网络状态下，只有在WIFI网络才会返回true
	* @param @param context
	* @param @return
	* @return boolean
	* @throws
	 */
	public static boolean isWIFI(Context context){
		 ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) { //当前有网络
//			int netType = info.getType(); 
//			if (netType == ConnectivityManager.TYPE_WIFI) 
//			{ 
//				return info.isConnected(); 
//			}
			return true;
		}
		return false;
	}
	
	
	
	
}
