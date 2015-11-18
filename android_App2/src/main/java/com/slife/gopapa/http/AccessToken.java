package com.slife.gopapa.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.database.DBConstants;

/***
 * @ClassName: AccessToken
 * @Description: 打开APP的时候会先去得到token的值
 * @author 菲尔普斯
 * @date 2015-1-13 下午4:05:50
 * 
 */
public class AccessToken {
	private static String common_token_url = APPConstants.URL_COMMON_HOST_NAME
			+ APPConstants.URL_COMMON_TOKEN;
	private static String app2_token_url = APPConstants.URL_APP2_HOST_NAME
			+ APPConstants.URL_APP2_TOKEN;

	/**
	 * @Title: getToken
	 * @Description: 得到公用的token的值
	 * @param @param dao 接口回调
	 * @return void
	 * @throws
	 */
	public static void getCommonToken(final Context context) {
		MyApplication.httpUtils.send(HttpMethod.GET, common_token_url,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						getCommonToken(context);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Editor editor=MyApplication.preferences.edit();
						try {
							JSONObject obj = new JSONObject(arg0.result);
							if (verificationToken(obj.getString("access_token"))) {
								MyApplication.commonToken = obj
										.getString("access_token");
								// 保存到数据库
								editor.putString(DBConstants.TOKEN_COMMON,
										MyApplication.commonToken);
								editor.putLong(DBConstants.TOKEN_COMMON_TIME,
										System.currentTimeMillis());
							}
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
						editor.commit();
					}
				});
	}

	/***
	 * @Title: getToken2
	 * @Description:取得APP2的token值
	 * @param
	 * @return void
	 * @throws
	 */
	public static void getAPP2Token() {
		MyApplication.httpUtils.send(HttpMethod.GET, app2_token_url,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						getAPP2Token();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Editor editor=MyApplication.preferences.edit();
						try {
							JSONObject obj = new JSONObject(arg0.result);
							if (verificationToken(obj.getString("access_token"))) {
								MyApplication.app2Token = obj
										.getString("access_token");
								// 保存到数据库
								editor.putString(DBConstants.TOKEN_APP2,
										MyApplication.app2Token);
								editor.putLong(DBConstants.TOKEN_APP2_TIME,
										System.currentTimeMillis());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						editor.commit();
					}
				});
	}

	/**
	 * @Title: verificationToken
	 * @Description: 验证token的合法性
	 * @param @param token
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	private static boolean verificationToken(String token) {
		if (token != null && !"".equals(token) && token.length() == 32) {
			StringBuffer buffer = new StringBuffer();
			char tokenChar[] = token.toCharArray();
			for (int i = 0; i < tokenChar.length; i++) {
				if (Character.isLetterOrDigit(tokenChar[i])) {
					buffer.append(tokenChar[i]);
				}
			}
			if (buffer.length() == 32) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
