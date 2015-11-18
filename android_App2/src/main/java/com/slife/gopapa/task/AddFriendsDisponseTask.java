package com.slife.gopapa.task;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.view.MyProgressDialog;

/***
* @ClassName: AddFriendsDisponseTask 
* @Description: 处理添加好友请求的异步任务（请求添加好友）
* @author 菲尔普斯
* @date 2015-1-28 上午11:26:40 
*
 */
public class AddFriendsDisponseTask extends AsyncTask<Void, Void, String[]> {
	private Context context;
	private String sendAccount; // 发送者啪啪号
	private String targetAccount; // 发送目标啪啪号
	private boolean isSuccess=false;;

	public boolean isSuccess() {
		return isSuccess;
	}
	public AddFriendsDisponseTask(Context context, String sendAccount,
			String targetAccount) {
		super();
		this.context = context;
		this.sendAccount = sendAccount;
		this.targetAccount = targetAccount;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		MyProgressDialog.showDialog(context, "好友请求处理", "正在处理中");
	}
	@Override
	protected String[] doInBackground(Void... params) {
		return MyHttpClient.postDataToService(context,APPConstants.URL_APP2_HOST_NAME+APPConstants.URL_FRIENDS_SENDAPPLYAPP2, MyApplication.app2Token,
				new String[] { "send_user_account", "to_user_account","is_ios" },
				new String[] { sendAccount, targetAccount,"-1" }, null, null);
	}
	@Override
	protected void onPostExecute(String[] result) {
		super.onPostExecute(result);
		MyProgressDialog.closeDialog();
		JsonObjectErrorDaoImpl.resolveJson(context, result,
				new JsonObjectErrorDao() {

					@Override
					public void disposeJsonObj(JSONObject obj) {
						isSuccess = obj.optBoolean("is_success");
						Toast.makeText(context, "好友请求发送成功，等待对方接受请求", Toast.LENGTH_SHORT).show();
					}
				});
	}
}