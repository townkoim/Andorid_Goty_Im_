package com.slife.gopapa.activity.competition;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.GetRaceInfoAPP2;
import com.slife.gopapa.utils.ParseErrorJsonUtils;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * 
 * @ClassName: CommitResultActivity
 * @Description: 提交战绩界面,实现了原型图中"提交战绩";
 * 用户可以在Radiobutton选择胜/平/负中的其中一个;
 * 
 * @author 肖邦
 * @date 2015-1-22 下午1:59:35
 * 
 */
@ContentView(R.layout.activity_competition_submit_result)
public class SubmitResultActivity extends BaseActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;//返回键
	@ViewInject(R.id.activity_submit_result_head)
	private ImageView imgHead;// 用户头像
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;//标题
	@ViewInject(R.id.activity_competition_submit_result_title)
	private TextView tvTheme;// 主题
	@ViewInject(R.id.activity_submit_result_user)
	private TextView tvFromUser;// 发起者
	@ViewInject(R.id.activity_submit_result_combater)
	private TextView tvToUser;// 接受者
	@ViewInject(R.id.activity_submit_result_item)
	private TextView tvItem;// 项目
	@ViewInject(R.id.activity_submit_result_position)
	private TextView tvAddress;// 地址
	@ViewInject(R.id.activity_submit_result_time)
	private TextView tvTime;// 时间
	@ViewInject(R.id.activity_submit_result_illustration)
	private TextView tvIllustration;// 说明
	// -----------------RadioButton-----------------
	@ViewInject(R.id.btn_0)
	private RadioButton rbtn1;// 胜
	@ViewInject(R.id.btn_1)
	private RadioButton rbtn2;// 平
	@ViewInject(R.id.btn_2)
	private RadioButton rbtn3;// 负
	// -----------------RadioButton-----------------
	@ViewInject(R.id.activity_person_information_commit)
	private TextView tvCommit;// 提交
	private SubmitResultActivity activity;
	private GetRaceInfoAPP2 infoAPP2;
	private int type;// 我参与的或我发起的类型
	private int result;// 比赛结果

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		if (getIntent() != null && getIntent().getSerializableExtra("infoAPP2") != null) {
			// 一级页面传来的实体类
			infoAPP2 = (GetRaceInfoAPP2) getIntent().getSerializableExtra("infoAPP2");
			initView();
		}
	}
	/**
	 * 
	* @Title: initView
	* @Description: 获得从上一级界面数据(发起人/赴约人/项目/地点/时间/说明)加载到这个界面
	* @param 
	* @return void
	* @throws
	 */
	private void initView() {
		tvTheme.setText(infoAPP2.getRace_title());
		tvFromUser.setText("发起:" + infoAPP2.getFrom_user_info().getUser_nickname());
		tvToUser.setText("赴约:" + infoAPP2.getTo_user_info().getUser_nickname());
		tvItem.setText("项目:" + infoAPP2.getSport_tag_name());
		tvAddress.setText("地点:" + infoAPP2.getRace_address());
		tvTime.setText("时间:" + infoAPP2.getRace_time());
		tvIllustration.setText("说明:" + infoAPP2.getRace_desc());
		type = getIntent().getIntExtra("type", 0);
		MyApplication.bitmapUtils.configDefaultLoadFailedImage(R.drawable.common_users_icon_default);
		if (type == 2) {
			MyApplication.bitmapUtils.display(imgHead, infoAPP2.getTo_user_info().getUser_logo_200());
		} else {
			MyApplication.bitmapUtils.display(imgHead, infoAPP2.getFrom_user_info().getUser_logo_200());
		}
	}

	@OnClick({ R.id.common_title_back, R.id.activity_person_information_commit })
	public  void onclick(View v) {
		if (v.getId() == R.id.common_title_back) {
			this.finish();
		}
		if (v.getId() == R.id.activity_person_information_commit) {
			if (rbtn1.isChecked()) {// 胜
				result = 1;
			}
			if (rbtn2.isChecked()) {// 平
				result = 2;
			}
			if (rbtn3.isChecked()) {// 负
				result = 3;
			}
			new Task().execute();
		}
	}

	/**
	 * @ClassName: Task
	 * @Description: 提交战绩
	 * 
	 * @author 肖邦
	 * @date 2015-1-27 下午2:16:51
	 */
	class Task extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			MyProgressDialog.showDialog(activity, "提交战绩", "提交中...");
		}

		@Override
		protected String[] doInBackground(Void... params) {
			String[] json = MyHttpClient.postDataToService(SubmitResultActivity.this,APPConstants.URL_SUBMITRACEAPP2, MyApplication.app2Token, new String[] { "user_account", "race_result", "race_id","is_ios" },
					new String[] { MyApplication.preferences.getString("user_account", ""), result + "", getIntent().getIntExtra("race_id", 0) + "" ,APPConstants.IS_IOS}, null, null);
			return json;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			MyProgressDialog.closeDialog();
			if (result != null) {
				if ("200".equals(result[0])) {
					Toast.makeText(activity, "提交成功", Toast.LENGTH_LONG).show();
					activity.finish();
				} else {
					Toast.makeText(activity, ParseErrorJsonUtils.getErrorMsg(result).getError(), Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
