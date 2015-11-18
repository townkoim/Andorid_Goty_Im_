/**   
 * @Title: LoginedActivty.java
 * @Package com.slife.gopapa.activity.login
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 肖邦
 * @date 2015-1-23 上午11:48:19
 * @version V1.0
 */
package com.slife.gopapa.activity.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.activity.TabMainActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.City;
import com.slife.gopapa.model.UserInfo;
import com.slife.gopapa.service.DownloadHeadImgService;

/**
 * @ClassName: LoginedActivty
 * @Description: 无界面,只用来加载信息.登录后在这个界面获取 1.运动标签 2.判断该用户是否有学校信息 3.用户个人信息 4.用户城市信息
 *               然后在跳到TabMainActivity
 * @author 肖邦
 * @date 2015-1-23 上午11:48:19
 * 
 */
@ContentView(R.layout.activity_logined)
public class LoginedActivity extends BaseActivity {
	private LoginedActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		new Task().execute();
	}

	/**
	 * @ClassName: Task
	 * @Description: 获取1.运动标签2.用户学校信息3.个人信息4.城市信息
	 * @author 肖邦
	 * @date 2015-1-27 下午2:17:37
	 */
	class Task extends AsyncTask<Void, Void, ArrayList<String[]>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String[]> doInBackground(Void... params) {
			ArrayList<String[]> list = new ArrayList<String[]>();
			// 运动标签
			list.add(MyHttpClient.getJsonFromService(LoginedActivity.this,
					APPConstants.URL_USER_SPORT_TAG + MyApplication.app2Token + "&user_account=" + MyApplication.preferences.getString("user_account", "")));
			// 是否有学校信息
			list.add(MyHttpClient.getJsonFromService(LoginedActivity.this, APPConstants.URL_ISSCHOOLINFOAPP2 + MyApplication.app2Token + "&user_account="
					+ MyApplication.preferences.getString("user_account", "")));
			// 个人信息
			list.add(MyHttpClient.getJsonFromService(LoginedActivity.this, APPConstants.URL_GETBASEINFOAPP2 + MyApplication.app2Token + "&user_account="
					+ (MyApplication.preferences.getString("user_account", ""))));
			// 用户城市信息
			list.add(MyHttpClient.getJsonFromService(LoginedActivity.this, APPConstants.URL_GETUSERCITYINFOAPP2 + MyApplication.app2Token + "&user_account="
					+ MyApplication.preferences.getString("user_account", "")));
			return list;
		}

		@Override
		protected void onPostExecute(ArrayList<String[]> result) {
			super.onPostExecute(result);
			Set<String> nameSet = new TreeSet<String>();// 运动标签集合
			if (result != null) {// 如果返回结果不为空
				if (result.get(0) != null) {// 如果运动标签集合返回结果不为空
					// 运动标签
					if ("200".equals(result.get(0)[0])) {// 请求成功
						// 运动标签存到手机里
						for (int i = 1; i < 100; i++) {
							try {
								JSONObject jsonObject = new JSONObject(result.get(0)[1]);// 运动标签数据
								if (jsonObject.optJSONObject(String.valueOf(i)) != null) {// 获取从0到100的键取值
									nameSet.add(jsonObject.optJSONObject(String.valueOf(i)).optString("tag_name"));// 在集合中加入元素
								}
							} catch (JSONException e) {
							}
						}
						// 如果遍历之后集合内的数据还是为空就在集合头部加上"none",以便后面程序判断是否有数据
						if (nameSet.isEmpty()) {
							nameSet.add("none");
						}
						MyApplication.preferences.edit().putStringSet("sport_tag", nameSet).commit();// 保存到手机
					} else {
						// 结果为空直接在集合头部加上"none"
						nameSet.add("none");
						Editor editor = MyApplication.preferences.edit().putStringSet("sport_tag", nameSet);
						editor.commit();
					}
				}
			}
			if (result.get(1) != null) {// 如果校园信息返回结果不为空
				// 校园信息
				if ("200".equals(result.get(1)[0])) {// 请求成功
					try {
						JSONObject jsonObject = new JSONObject(result.get(1)[1]);
						boolean is_school = jsonObject.optBoolean("is_school");// 返回布尔型结果
						MyApplication.preferences.edit().putBoolean("is_school", is_school).commit();// 保存到手机
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			if (result.get(2) != null) {
				// 将个人信息储存到手机里
				if ("200".equals(result.get(2)[0])) {
					UserInfo baseInfoAPP2 = JSON.parseObject(result.get(2)[1], UserInfo.class);
					Editor editor = MyApplication.preferences.edit();
					editor.putString("user_nickname", baseInfoAPP2.getUser_nickname());
					editor.putString("user_sign", baseInfoAPP2.getUser_sign());
					editor.putString("user_gender", baseInfoAPP2.getUser_gender());
					editor.putString("user_birth", baseInfoAPP2.getUser_birth());
					editor.putString("user_age", baseInfoAPP2.getUser_age());
					editor.putString("now_province_id", baseInfoAPP2.getNow_province_id());
					editor.putString("now_city_id", baseInfoAPP2.getNow_city_id());
					editor.putString("now_county_id", baseInfoAPP2.getNow_county_id());
					editor.putString("profession_id", baseInfoAPP2.getProfession_id());
					editor.putInt("basePro", Integer.valueOf(baseInfoAPP2.getPro_id()));
					editor.putString("school_id", baseInfoAPP2.getSchool_id());
					editor.putString("user_company", baseInfoAPP2.getUser_company());
					editor.putString("user_logo_200", baseInfoAPP2.getUser_logo_200());
					editor.putString("user_logo_500", baseInfoAPP2.getUser_logo_500());
					editor.putString("now_name", baseInfoAPP2.getNow_name());
					editor.putString("profession_name", baseInfoAPP2.getProfession_name());
					editor.putString("school_name", baseInfoAPP2.getSchool_name());
					editor.putString("extend_user_account", baseInfoAPP2.getExtend_user_account());
					editor.putString("vitality_name", baseInfoAPP2.getVitality_name());
					editor.commit();
				}
			}
			if (result.get(3) != null) {
				// 用户城市信息
				if ("200".equals(result.get(3)[0])) {
					List<City> cityList = JSON.parseArray(result.get(3)[1], City.class);
					Editor editor = MyApplication.preferences.edit();// 获取编辑器
					try {
						editor.putInt("province_code", Integer.parseInt(cityList.get(0).getSc_id()));// 省号
						editor.putInt("city_code", Integer.parseInt(cityList.get(1).getSc_id()));// 市号
						editor.putInt("district_code", Integer.parseInt(cityList.get(2).getSc_id()));// 区号
					} catch (Exception e) {
						//如果没有城市码则默认城市码为广东深圳南山
						editor.putInt("province_code", 44);// 省号
						editor.putInt("city_code", 4403);// 市号
						editor.putInt("district_code", 440305);// 区号
					}
					editor.putString("province_name", cityList.get(0).getSc_name());//省
					editor.putString("city_name", cityList.get(1).getSc_name());//市
					editor.putString("district_name", cityList.get(2).getSc_name());//区
					editor.commit();
				}
			}
			startService(new Intent(LoginedActivity.this,DownloadHeadImgService.class));
			//如果是从修改个人信息界面跳转过来的
			if (getIntent() != null && getIntent().getBooleanExtra("tag", false)) {
				Intent intent = new Intent(LoginedActivity.this, TabMainActivity.class);
				intent.putExtra("tag", true);
				startActivity(intent);
				LoginedActivity.this.finish();
			} else {
				// 跳转到TabActivity
				Intent intent = new Intent(LoginedActivity.this, TabMainActivity.class);
				intent.putExtra("isLogin", true);
				startActivity(intent);
				LoginedActivity.this.finish();
			}
		}
	}

}
