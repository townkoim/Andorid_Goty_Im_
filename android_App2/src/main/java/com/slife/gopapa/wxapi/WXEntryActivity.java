package com.slife.gopapa.wxapi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.utils.ParseErrorJsonUtils;
import com.slife.gopapa.view.MyProgressDialog;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * 
* @ClassName: WXEntryActivity
* @Description: 这是一个微信分享的回调类,执行微信分享后在onResp方法里接受返回的结果,根据结果匹配相关的操作
* @author 肖邦
* @date 2015-3-3 下午5:23:09
*
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private WXEntryActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//去除标题栏
		setContentView(R.layout.activity_share_result);
		activity = this;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);	//隐藏键盘
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	//设置只能竖屏
		MyApplication.api.handleIntent(getIntent(), this);
	}


	@Override
	public void onReq(BaseReq arg0) {

	}

	class ShareTask extends AsyncTask<Integer, Void, String[]> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			MyProgressDialog.showDialog(activity, "分享赚活力", "请稍等...");
		}

		@Override
		protected String[] doInBackground(Integer... params) {
			return MyHttpClient.postDataToService(activity, APPConstants.URL_ADDSHAREVITALITYAPP2, MyApplication.app2Token, new String[] { "user_account", "type" }, new String[] {
					MyApplication.preferences.getString("user_account", ""), String.valueOf(params[0]) }, null, null);
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			MyProgressDialog.closeDialog();
			if (result != null) {
				if ("200".equals(result[0])) {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(result[1]);
						int shareResult = jsonObject.optInt("is_sussess");
						if (shareResult == -2) {
							Toast.makeText(activity, "已经在该平台分享过", Toast.LENGTH_LONG).show();
						}
						if (shareResult == -1) {
							Toast.makeText(activity, "分享失败", Toast.LENGTH_LONG).show();
						}
						if (shareResult == 1) {
							Toast.makeText(activity, "分享成功", Toast.LENGTH_LONG).show();
							activity.finish();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(activity, ParseErrorJsonUtils.getErrorMsg(result).getError(), Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(activity, "加分失败", Toast.LENGTH_LONG).show();
			}
			activity.finish();
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK://分享成功
			result = R.string.errcode_success;
			new ShareTask().execute(7);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL://取消分享
			result = R.string.errcode_cancel;
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			activity.finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED://发送被拒绝
			result = R.string.errcode_deny;
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			activity.finish();
			break;
		default:
			result = R.string.errcode_unknown;//发送返回
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			activity.finish();
			break;
		}
	}
}
