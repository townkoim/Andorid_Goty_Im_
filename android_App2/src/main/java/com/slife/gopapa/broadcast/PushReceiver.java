package com.slife.gopapa.broadcast;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;
import com.slife.gopapa.R;

import com.igexin.sdk.PushConsts;
import com.slife.gopapa.activity.TabMainActivity;
import com.slife.gopapa.activity.WelcomeActivity;
import com.slife.gopapa.activity.login.LoginActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.database.DBHelperOperation;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.service.UpdateSportTagService;

/***
 * @ClassName: PushReceiver
 * @Description: 个推的广播接受者。用来接收服务器推送过来的消息
 * @author 菲尔普斯
 * @date 2015-2-2 上午9:37:40
 * 
 */
public class PushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		case PushConsts.GET_MSG_DATA:
			byte[] payload = bundle.getByteArray("payload");
			String data = new String(payload);
			final String newData = data.replaceAll("&quot;", "\"");// 去除json中的&quot;替代为"
			new CountDownTimer(1000, 10) {

				@Override
				public void onTick(long millisUntilFinished) {

				}

				@Override
				public void onFinish() {
					parseJson(context, newData);
				}
			}.start();
			break;
		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			String cid = bundle.getString("clientid");
			if (!"".equals(cid) && cid != null) {
				Editor editor = MyApplication.preferences.edit();
				editor.putString(DBConstants.CLIENTID, cid);
				editor.commit();
				MyApplication.clientID = cid;
			}
			if (MyApplication.myLoginState) {
				if (!MyApplication.pushState) {
					loginBind(context);
				}
			}
			break;
		// 添加其他case
		// .........
		default:
			break;
		}

	}

	/***
	 * @Title: loginBind
	 * @Description: 绑定推送与用户关系
	 * @param
	 * @return void
	 * @throws
	 */
	private void loginBind(final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				MyHttpClient.postDataToService(
						context,
						APPConstants.URL_LOGIN_BIND,
						MyApplication.app2Token,
						new String[] { "user_account", "facility", "client_id","is_ios" },
						new String[] {
								MyApplication.senderUser.getUser_account(),
								MyApplication.IMEI, MyApplication.clientID,"-1" }, null, null);
			}
		}).start();
	}

	/***
	 * @Title: parseJson
	 * @Description: 解析推送过来的json，根据推送的内容不同来处理不同的业务逻辑
	 * @param @param context
	 * @param @param jsonStr
	 * @return void
	 * @throws
	 */
	private void parseJson(Context context, String jsonStr) {
		try {
			JSONObject obj = new JSONObject(jsonStr);
			String account = obj.optString("push_user_account");
			if (MyApplication.preferences.getString(DBConstants.USER_ACCOUNT,
					"").equals(account)) { // 得到啪啪号进行判断
				if (obj.optString("push_type").equals("4")) { // 说明上次登陆最后的设备号和此次的不一样
					if (MyApplication.isTabActivity) {
						Editor editor = MyApplication.preferences.edit(); // 修改数据库里面保存的用户信息，全部为null
						editor.putString(DBConstants.USER_LOGIN_NAME, null);
						editor.putString(DBConstants.USER_PASSWORD, null);
						editor.putString(DBConstants.USER_EXTEND_ACCOUNT, null);
						editor.putString(DBConstants.USER_ACCOUNT, null);
						editor.putString(DBConstants.USER_NICKNAME, null);
						editor.putInt(DBConstants.NEW_FRIENDS_COUNT, 0);
						editor.putInt(DBConstants.NEW_ABOUT_MATCH_COUNT, 0);
						editor.putInt(DBConstants.NEW_OFFCIAL_ASSISTANT_COUNT,
								0);
						editor.commit();
						Toast.makeText(context, obj.optString("push_content"),
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(context, LoginActivity.class);
						intent.putExtra("push_user_account", account);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
						AppManager.existAPP();
					}
				} else {
					showNotification(context, obj.optString("push_content"),
							obj.optString("push_type"));// 显示通知栏
					insertToSharePreference(context, account, obj);
					if (MyApplication.isTabActivity) {
						// 控制底部菜单的小红点
						MyApplication.trackPointMsgCount = MyApplication.trackPointMsgCount + 1;
						Intent intent1 = new Intent();
						intent1.setAction(APPConstants.ACTION_TRACKPOINT_CHANGE);
						context.sendBroadcast(intent1);
						// 控制NewsFragment界面的小红点
						if (MyApplication.isInitNewsFragment) { // 如果已经初始化了NewsFragment界面，那就发送广播到NewsFragment界面
							Intent intent = new Intent();
							if (obj.optString("push_type").equals("1")) { // 好友消息
								intent.setAction(APPConstants.ACTION_NEW_FRIENDS);
							} else if (obj.optString("push_type").equals("2")) { // 约赛消息
								intent.setAction(APPConstants.ACTION_NEW_ABOUT_MATCH);
								DBHelperOperation.insertAboutMatch(account,
										obj.optString("push_content"),
										obj.optString("race_id"));
							} else if (obj.optString("push_type").equals("3")) {
								intent.setAction(APPConstants.ACTION_NEW_OFFICIAL_ASSISTANT);
							}
							context.sendBroadcast(intent);
						}
					}
				}
			} else {
				if (MyApplication.preferences.getString(
						DBConstants.USER_ACCOUNT, null) != null
						&& MyApplication.myLoginState) {
					// 没有匹配上此用户。所以将数据上传给服务器
					new UploadPushTask(context, account,
							obj.optString("push_content"),
							obj.optString("push_type"),
							obj.optString("race_id"), "-1").execute();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void insertToSharePreference(Context context, String account,
			JSONObject obj) {
		if (MyApplication.preferences == null) {
			MyApplication.preferences = context.getSharedPreferences(
					DBConstants.SHAREDPREFERENCES_NAME,
					DBConstants.SHAREDPREFERENCES_MODEL);
		}
		Editor editor = MyApplication.preferences.edit();
		if (obj.optString("push_type").equals("1")) { // 好友消息
			editor.putInt(DBConstants.NEW_FRIENDS_COUNT,
					MyApplication.preferences.getInt(
							DBConstants.NEW_FRIENDS_COUNT, 0) + 1);
		} else if (obj.optString("push_type").equals("2")) { // 约赛消息
			editor.putInt(DBConstants.NEW_ABOUT_MATCH_COUNT,
					MyApplication.preferences.getInt(
							DBConstants.NEW_ABOUT_MATCH_COUNT, 0) + 1);
			DBHelperOperation.insertAboutMatch(account,
					obj.optString("push_content"), obj.optString("race_id"));
		} else if (obj.optString("push_type").equals("3")) { // 官方助手消息
			editor.putInt(DBConstants.NEW_OFFCIAL_ASSISTANT_COUNT,
					MyApplication.preferences.getInt(
							DBConstants.NEW_OFFCIAL_ASSISTANT_COUNT, 0) + 1);
		}
		editor.commit();
	}

	/**
	 * @Title: showNotification
	 * @Description: 显示通知栏
	 * @param @param context 上下文对象
	 * @param @param json 推送的json格式
	 * @return void
	 * @throws
	 */
	@SuppressLint("NewApi")
	private void showNotification(Context context, String content, String type) {
		if (MyApplication.voiceUtils != null) {
			MyApplication.voiceUtils.playSound();
		}
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = null;
		if (MyApplication.myLoginState == true) { // 表名未初始化消息界面,则intent对象则为启动应用程序
			intent = new Intent(context, TabMainActivity.class);
			intent.putExtra("push", true);
		} else {
			intent = new Intent(context, WelcomeActivity.class);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		String title = "";
		if (type.equals("1")) {
			title = "好友请求";
		} else if (type.equals("2")) {
			title = "约赛消息";
			Intent intent2 = new Intent(context, UpdateSportTagService.class);
			context.startService(intent2);
		} else if (type.equals("3")) {
			title = "官方助手";
		}
		Notification notification = new Notification(R.drawable.ic_launcher,
				content, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context, title, content, contentIntent);
		notificationManager.notify(1, notification);
	}

	/***
	 * @ClassName: UploadPushTask
	 * @Description: 当推送的内容和此用户的啪啪号不符合的时候就调用此接口将推送过来的数据上传到服务器
	 * @author 菲尔普斯
	 * @date 2015-2-4 下午2:32:35
	 * 
	 */
	public class UploadPushTask extends AsyncTask<Void, Void, String[]> {
		private Context context;
		private String account;
		private String content;
		private String pushType;
		private String raceId;
		private String isIos;

		public UploadPushTask(Context context, String account, String content,
				String pushType, String raceId, String isIos) {
			super();
			this.context = context;
			this.account = account;
			this.content = content;
			this.pushType = pushType;
			this.raceId = raceId;
			this.isIos = isIos;
		}

		@Override
		protected String[] doInBackground(Void... params) {

			return MyHttpClient.postDataToService(context,
					APPConstants.URL_APP2_HOST_NAME
							+ APPConstants.URL_UPLOAD_PUSH_CONTENT,
					MyApplication.preferences.getString(DBConstants.TOKEN_APP2,
							null), new String[] { "push_user_account",
							"push_content", "push_type", "race_id", "is_ios" },
					new String[] { account, content, pushType, raceId, isIos },
					null, null);
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			if (result != null) {
				if (Integer.valueOf(result[0]) == 200) {
					try {
						JSONObject obj = new JSONObject(result[1]);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
