package com.slife.gopapa.task;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.view.MyProgressDialog;

/***
* @ClassName: UpdatePasswordTask 
* @Description:修改密码的异步任务
* @author 菲尔普斯
* @date 2015-1-28 下午4:42:44 
*
 */
public class UpdatePasswordTask extends AsyncTask<Void, Void, String[]>{
	private Context context;
	private String pwd;
	private String newPwd;
	
	public UpdatePasswordTask(Context context,String pwd, String newPwd) {
		super();
		this.context=context;
		this.pwd = pwd;
		this.newPwd = newPwd;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		MyProgressDialog.showDialog(context, "修改密码", "正在修改......");
	}
	
	@Override
	protected String[] doInBackground(Void... params) {
		return MyHttpClient.postDataToService(context,APPConstants.URL_COMMON_HOST_NAME+APPConstants.URL_EDIT_PASSWORD, MyApplication.commonToken,
				new String[]{"user_account","old_pwd","new_pwd"}, new String[]{MyApplication.senderUser.getUser_account(),pwd,newPwd}, null, null);
	}
	@Override
	protected void onPostExecute(String[] result) {
		super.onPostExecute(result);
		MyProgressDialog.closeDialog();
		JsonObjectErrorDaoImpl.resolveJson(context, result, new JsonObjectErrorDao() {
			
			@Override
			public void disposeJsonObj(JSONObject obj) {
				boolean isSuccess = obj.optBoolean("is_sussess");
				if(isSuccess){
					Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
					Editor editor=MyApplication.preferences.edit();
					editor.putString(DBConstants.USER_PASSWORD, pwd);
					editor.commit();
					Intent intent = new  Intent();
					intent.setAction("finish");
					context.sendBroadcast(intent);
				}else{
					Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}