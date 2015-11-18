package com.slife.gopapa.task;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.view.MyProgressDialog;

/***
* @ClassName: FeedBackTask 
* @Description: 意见反馈的异步任务 
* @author 菲尔普斯
* @date 2015-1-28 下午4:48:45 
*
 */
public class FeedBackTask extends AsyncTask<Void, Void, String[]>{
	private Context context;
	private EditText etTitle;
	private EditText etContent;
	private String title;
	private String content;
	
	public FeedBackTask(Context context,EditText etTitle,EditText etContent) {
		super();
		this.context=context;
		this.etTitle = etTitle;
		this.etContent = etContent;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		title=etTitle.getText().toString();
		content=etContent.getText().toString();
		MyProgressDialog.showDialog(context, "意见反馈", "正在提交......");
	}
	
	@Override
	protected String[] doInBackground(Void... params) {
		return MyHttpClient.postDataToService(context,APPConstants.URL_COMMON_HOST_NAME+APPConstants.URL_FEEDBACK, MyApplication.commonToken,
				new String[]{"user_account","feedback_title","feedback_content","feedback_type"}, new String[]{MyApplication.preferences.getString(DBConstants.USER_ACCOUNT, null)
				,title,content,"2"}, null, null);
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
					etTitle.setText("");
					etContent.setText("");
					Toast.makeText(context, "反馈成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(context, "反馈失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
}