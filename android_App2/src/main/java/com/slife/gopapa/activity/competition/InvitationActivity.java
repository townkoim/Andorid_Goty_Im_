package com.slife.gopapa.activity.competition;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.activity.mine.ModifyPersonInformationActivity;
import com.slife.gopapa.activity.mine.SelectActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.utils.DataUtils;
import com.slife.gopapa.view.CompetitionFeeDialog;
import com.slife.gopapa.view.CompetitionFeeDialog.OnDialogDismissListener;
import com.slife.gopapa.view.DateTimePickerDialog;
import com.slife.gopapa.view.DateTimePickerDialog.OnDateTimeSetListener;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * @ClassName: InvitationActivity
 * @Description: 邀请比赛的第一步(填写项目 费用 地点 主题 说明 时间,第二步押积分暂时不做),实现了原型中"邀请比赛"页;
 *               1.点击"约赛项目"访问公共接口的运动项目; 2.点击"费用"有AA、我请、求请客、谁输谁出、免费五个选项;
 *               3.点击“时间”选择约赛时间，精确到分钟 ;4.点击“地点”跳到地图的界面; 5.“赛事主题”、“说明”填写相应的内容
 *               6.点击“邀请”向服务器提交POST请求
 * @author 肖邦
 * @date 2015-1-7 下午4:50:14
 */
@ContentView(R.layout.activity_competition_invitation)
public class InvitationActivity extends BaseActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;// 返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;// 界面标题
	@ViewInject(R.id.common_title_right)
	private TextView tvRight;// 下一步
	@ViewInject(R.id.activity_invitation_competition_item_tv)
	private TextView tvItem;// 约赛项目
	@ViewInject(R.id.activity_invitation_competition_fee_tv)
	private TextView tvFee;// 约赛费用
	@ViewInject(R.id.activity_invitation_publish)
	private TextView tvInvitation;// 约赛时间
	@ViewInject(R.id.activity_invitation_competition_position_tv)
	private TextView tvAddress;// 约赛地点
	@ViewInject(R.id.activity_invitation_competition_theme)
	private EditText etTheme;// 约赛主题
	@ViewInject(R.id.activity_invitation_competition_illustration)
	private EditText etIllustration;// 约赛说明
	@ViewInject(R.id.activity_invitation_competition_time_tv)
	private TextView tvTime;// 约赛时间
	private InvitationActivity activity;
	private int tag_id = 0;// 运动标签对应的ID
	private int raceExpenses = 0;// 费用对应的ID
	private double lat = 0;// 纬度
	private double lon = 0;// 经度
	private String name = "";// 地址
	private String address = "";// 详细地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		initView();
	}

	/**
	 * @Title: initView
	 * @Description: 初始化界面顶部的文字
	 * @param
	 * @return void
	 * @throws
	 */
	private void initView() {
		tvTitle.setText("邀请比赛");// 界面标题
		tvRight.setText("下一步");// 顶部右边的文字
		tvRight.setVisibility(View.GONE);// 押付活力的功能暂时不做
	}

	@OnClick({ R.id.common_title_back, R.id.common_title_right, R.id.activity_invitation_competition_item, R.id.activity_invitation_competition_fee,
			R.id.activity_invitation_competition_position, R.id.activity_invitation_competition_time, R.id.activity_invitation_publish })
	public void onclick(View v) {
		Intent intent = null;
		if (v.getId() == R.id.common_title_back) {
			finish();// 接受当前界面
		}
		if (v.getId() == R.id.activity_invitation_publish) {
			if ("".equals(etIllustration.getText()) || "".equals(etTheme.getText()) || "".equals(tvAddress.getText()) || "".equals(tvFee.getText()) || "".equals(tvTime.getText())
					|| "".equals(tvItem.getText())) {
				Toast.makeText(activity, "请输入完整信息", Toast.LENGTH_LONG).show();
				return;
			}
			// 邀请
			try {
				// 将填写的数据打包成JSON格式
				JSONObject object = new JSONObject();
				object.put("race_title", etTheme.getText().toString());// 赛事主题
				object.put("sport_tag_id", tag_id);// 运动标签对应的ID
				object.put("race_expenses", raceExpenses);// 费用类型对应的ID
				object.put("race_time", tvTime.getText().toString());// 运动时间
				object.put("race_address", address);// 详细地址
				object.put("longitude", lon);// 经度
				object.put("latitude", lat);// 纬度
				object.put("race_desc", tvInvitation.getText().toString());// 说明
				new InvitationTask().execute(object.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (v.getId() == R.id.common_title_right) {// 此页面暂时不做
			// 点击跳到愿押活力的界面
			// intent = new Intent(activity, InvitationPointActivity.class);
			// Bundle bundle = new Bundle();
			// bundle.putStringArray("invitation_detail", new String[] {
			// etTheme.getText().toString(), tvItem.getText().toString(),
			// tvFee.getText().toString(),
			// tvTime.getText().toString(), tvAddress.getText().toString(),
			// etIllustration.getText().toString() });
			// intent.putExtras(bundle);
			// startActivity(intent);
		}
		if (v.getId() == R.id.activity_invitation_competition_item) {
			// 点击跳到选择项目的界面
			intent = new Intent(activity, ModifyPersonInformationActivity.class);
			intent.putExtra("modifyType", ModifyPersonInformationActivity.SPORT);// 将修改的类型传到下一个界面
			startActivityForResult(intent, SelectActivity.RESULTCODE);
		}
		if (v.getId() == R.id.activity_invitation_competition_fee) {
			// 弹出自定义的费用选择Dialog
			CompetitionFeeDialog dialog = new CompetitionFeeDialog(activity, R.style.dialog);// 实例化
			dialog.setCanceledOnTouchOutside(true);// 设置外面点击可令Dialog消失
			Window dialogWindow = dialog.getWindow();// 设置Dialog的位置
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			dialogWindow.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);// 垂直居中和水平居中
			dialogWindow.setAttributes(lp);
			// 设置Dialog消失时的监听器
			dialog.setOnDialogDismissListener(new OnDialogDismissListener() {
				@Override
				public void selectedFeeType(String type, int raceExpense) {
					tvFee.setText(type);// 回调传回来的费用类型.
					raceExpenses = raceExpense;// 费用类型对应的ID
				}
			});
			dialog.show();
		}
		if (v.getId() == R.id.activity_invitation_competition_time) {
			// 弹出自定义时间选择器
			DateTimePickerDialog dialog = new DateTimePickerDialog(this, System.currentTimeMillis());
			dialog.setOnDateTimeSetListener(new OnDateTimeSetListener() {
				public void OnDateTimeSet(AlertDialog dialog, long date) {
					// 回调后在TextVIew中显示日期
					if(date==0){
						tvTime.setText("");
					}else{
						tvTime.setText(DataUtils.getStringDate(date));
					}
				}
			});
			dialog.show();
		}
		if (v.getId() == R.id.activity_invitation_competition_position) {
			// 跳转到地图界面
			startActivityForResult(new Intent(activity, MapActivity.class), MapActivity.RESULT_CODE);
		}
	}

	/**
	 * @ClassName: InvitationTask
	 * @Description: 约赛异步任务,需要入参( 1.from_user_account 发起人用户啪啪号
	 *               2.to_user_account赴约人用户啪啪号 3.race_info 约赛信息 4.is_ios 是否IOS端
	 *               1表示ios其他则为android(-1))
	 * @author 肖邦
	 * @date 2015-1-21 下午4:53:22
	 */
	class InvitationTask extends AsyncTask<String, Void, String[]> {
		@Override
		protected void onPreExecute() {
			MyProgressDialog.showDialog(activity, "约赛", "提交详情中...");
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(String... params) {
			// 提交POST请求
			String[] result = MyHttpClient.postDataToService(InvitationActivity.this, APPConstants.URL_SENDRACEAPP2, MyApplication.app2Token, new String[] { "from_user_account",
					"to_user_account", "race_info", "is_ios" }, new String[] { MyApplication.preferences.getString("user_account", ""), getIntent().getStringExtra("user_account"),
					params[0], APPConstants.IS_IOS }, null, null);
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			MyProgressDialog.closeDialog();
			JsonObjectErrorDaoImpl.resolveJson(InvitationActivity.this, result,new JsonObjectErrorDao() {
				
				@Override
				public void disposeJsonObj(JSONObject obj) {
					Toast.makeText(activity, "请求成功", Toast.LENGTH_LONG).show();
					activity.finish();
				}
			});
		}
	}

	/**
	 * (非 Javadoc) Title: onActivityResult Description:从上一个界面返回数据(运动项目/约赛地址)显示到当前的activity
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @see android.app.Activity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			if (resultCode == SelectActivity.RESULTCODE) {
				//运动项目
				if (data.getStringExtra("tag_name") != null) {
					// 接收从运动标签界面传回来的诗句
					tvItem.setText(data.getStringExtra("tag_name"));
					tag_id = Integer.parseInt(data.getStringExtra("tag_id"));
				}
			} else if (resultCode == MapActivity.RESULT_CODE) {
				if((data.getStringExtra("lat") != null || !"".equals(data.getStringExtra("lat"))) && (data.getStringExtra("long") != null || !"".equals(data.getStringExtra("lon")))){				
					// 接收从地图界面传回来的地址.经纬度
					name = data.getStringExtra("name");
					address = data.getStringExtra("address");
					lat = Double.parseDouble(data.getStringExtra("lat").substring(0, data.getStringExtra("lat").lastIndexOf(".") + 5));
					lon = Double.parseDouble(data.getStringExtra("lon").substring(0, data.getStringExtra("lon").lastIndexOf(".") + 5));
					tvAddress.setText(name);
				}
			}
		}
	}
}
