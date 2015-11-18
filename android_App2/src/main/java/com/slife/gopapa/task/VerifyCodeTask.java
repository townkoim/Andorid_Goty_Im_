package com.slife.gopapa.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.AccessToken;
import com.slife.gopapa.http.MyHttpClient;

/***
 * @ClassName: VerifyCodeTask
 * @Description: 获取验证码的异步任务
 * @author 菲尔普斯
 * @date 2015-1-14 下午1:55:17
 * 
 */
public class VerifyCodeTask extends AsyncTask<Void, Void, String> {
	private Context context; // 上下文对象
	private String mobile; // 手机号码
	private String type; // 验证码类型
	private String responseMessage = null;// 验证码
	private String errorMessage = null;// 错误的信息
	private EditText etAuCode;//验证码

	/**
	 * Title: Description:
	 * 
	 * @param context
	 *            上下文对象
	 * @param mobile
	 *            手机号码
	 * @param type
	 *            类型 1代表注册，其他代表找回密码
	 */
	public VerifyCodeTask(Context context, String mobile, String type,EditText etAuCode) {
		super();
		this.context = context;
		this.mobile = mobile;
		this.type = type;
		this.etAuCode=etAuCode;
	}

	@Override
	protected String doInBackground(Void... params) {
		String errorCode = null; // 错误码
		String error = null;// 请求接口成功，但是json返回的是正确的还是错误
		// 请求网络，得到包含返回码和Json的数组
		String[] result = MyHttpClient.postDataToService(context, APPConstants.URL_COMMON_HOST_NAME + APPConstants.URL_VERIFYCODE, MyApplication.commonToken, new String[] {
				"user_mobile", "type" }, new String[] { mobile, type }, null, null);
		// 判断返回结果并解析Json
		if (result != null && result.length > 0) {
			try {
				JSONObject obj = new JSONObject(result[1]);
				error = obj.optString("error_code");
				errorMessage = obj.optString("error");
				errorCode = obj.optString("api_code");
				if (Integer.valueOf(result[0]) == 200) {
					responseMessage = obj.optString("VerifyCode");
				} else {
					errorMessage = obj.optString("error");
					// 如果返回的错误消息，提示用户
					if (error != null && !"".equals(errorMessage)) {
						if (error.equals("401") || errorCode.equals("40101")) {
							AccessToken.getCommonToken(context);
							AccessToken.getAPP2Token();
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if (errorMessage != null && !"".equals(errorMessage)) { // 返回的错误消息
			Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
		}
//		if (responseMessage != null && !"".equals(responseMessage)) { // 返回正确的验证码
//			Toast.makeText(context, responseMessage, Toast.LENGTH_SHORT).show();
//			etAuCode.setText(String.valueOf(responseMessage));
//		}
	}


}
