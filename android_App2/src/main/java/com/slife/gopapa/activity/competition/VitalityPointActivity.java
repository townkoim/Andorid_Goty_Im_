package com.slife.gopapa.activity.competition;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.activity.mine.HowToGetVitalityPointActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.UserVitalityAPP2;
import com.slife.gopapa.utils.ParseErrorJsonUtils;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * 
 * @ClassName: VitalityPointActivity
 * @Description: 活力积分界面,实现原型图"活力积分-02";
 * 1.这个界面显示用户的活力值,还有提示用户活力值的作用
 * 2.点击活力积分跳转到获取活力积分的界面
 * @author 肖邦
 * @date 2015-1-7 下午5:10:59
 * 
 */
@ContentView(R.layout.activity_mine_vitality_point)
public class VitalityPointActivity extends BaseActivity {
	@ViewInject(R.id.activity_vitality_point_howto)
	private RelativeLayout rlHowTo;//跳转到获取活力积分的界面的Layout
	@ViewInject(R.id.activity_vitality_point_level)
	private TextView tvLevel;//活力对应等级
	@ViewInject(R.id.activity_vitality_point_vitality)
	private TextView tvVitality;//活力值
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;//返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;//标题
	private VitalityPointActivity activity;//本类对象

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		initView();
		new Task().execute();
	}

	/**
	 * 
	* @ClassName: Task
	* @Description:  获取用户活力值和对应的活力名称
	* @author 肖邦
	* @date 2015-2-26 下午2:15:57
	*
	 */
	class Task extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			MyProgressDialog.showDialog(activity, "活力积分", "请稍等");
		}

		@Override
		protected String[] doInBackground(Void... params) {
			String json[] = MyHttpClient.getJsonFromService(VitalityPointActivity.this,new StringBuffer(APPConstants.URL_GETUSERVITALITYAPP2).append(MyApplication.app2Token).append("&user_account=")
					.append(MyApplication.preferences.getString("user_account", "44723")).toString());
			return json;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			MyProgressDialog.closeDialog();
			if (result != null) {
				if ("200".equals(result[0])) {
					UserVitalityAPP2 userVitalityAPP2 = JSON.parseObject(result[1], UserVitalityAPP2.class);
					tvVitality.setText("活力  " + userVitalityAPP2.getVitality_num());
					tvLevel.setText(userVitalityAPP2.getVitality_name());
				} else {
					Toast.makeText(activity, ParseErrorJsonUtils.getErrorMsg(result).getError(), Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void initView() {
		tvTitle.setText("活力积分");
	}

	@OnClick({ R.id.activity_vitality_point_howto, R.id.common_title_back })
	public void onclick(View v) {
		if (v.getId() == R.id.activity_vitality_point_howto) {
			//跳转到获取活力积分的界面
			startActivity(new Intent(activity, HowToGetVitalityPointActivity.class));
		}
		if (v.getId() == R.id.common_title_back) {
			finish();
		}
	}
}
