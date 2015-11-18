package com.slife.gopapa.activity.competition;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.FromUserInfo;
import com.slife.gopapa.model.GetRaceInfoAPP2;
import com.slife.gopapa.model.ToUserInfo;
import com.slife.gopapa.utils.ParseErrorJsonUtils;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * @ClassName: MyJoinOrInvitationDetailActivity
 * @Description: 我参与的.我发起的详情(包含赛事主题、名称、活力名、项目、地址、时间、费用、说明),实现了原型图中"我发起的-02~05";
 *               此页面有三种状态1.无状态 2.接受约赛 3.提交战绩(这几个状态是根据服务器返回的数据判断的)
 * @author 肖邦
 * @date 2015-1-22 上午9:16:28
 * 
 */
@ContentView(R.layout.activity_myjoin_or_invitation_detail)
public class MyJoinOrInvitationDetailActivity extends BaseActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;// 返回键
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_head)
	private ImageView imgHead;// 头像
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_sex)
	private ImageView imgSex;// 性别
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;// 标题
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_theme)
	private TextView tvTheme;// 赛事主题
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_name)
	private TextView tvName;// 姓名
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_vitality_name)
	private TextView tvVitality;// 活力名称
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_item)
	private TextView tvSportTag;// 运动项目
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_position)
	private TextView tvAddress;// 地址
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_time)
	private TextView tvTime;// 时间
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_fee)
	private TextView tvFee;// 费用
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_illustration)
	private TextView tvIllustration;// 说明
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_state)
	private TextView tvState;// 状态
	@ViewInject(R.id.activity_myjoin_or_invitation_detail_commit_or_accept)
	private TextView tvCommitOrAccept;// 提交战绩或接受邀请
	private MyJoinOrInvitationDetailActivity activity;
	private int type = 0;// 类型
	private int raceId = 0;// 赛事ID
	private GetRaceInfoAPP2 infoAPP2;// 约赛详情实体类
	private FromUserInfo fromUserInfo;// 约赛详情的发起者实体类
	private ToUserInfo toUserInfo;// 约赛详情的接受者实体类
	private boolean is_submit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		int type = getIntent().getIntExtra("type", 0);
		int raceId = getIntent().getIntExtra("race_id", 0);
		if (type != 0 && raceId != 0) {
			if (type == 2) {
				tvTitle.setText("我发起的");
			} else if (type == 3) {
				tvTitle.setText("邀请我的");
			}
			this.type = type;
			this.raceId = raceId;
			new Task().execute();
		}
	}

	/**
	 * @ClassName: Task
	 * @Description: 加载约赛详情数据; 入参:1.user_account 用户啪啪号; 2.race_id 约赛ID
	 * @author 肖邦
	 * @date 2015-1-27 下午2:04:19
	 */
	class Task extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPreExecute() {
			MyProgressDialog.showDialog(activity, "约赛详情", "请稍等...");
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(Void... params) {
			// 请求数据
			String[] json = MyHttpClient.getJsonFromService(MyJoinOrInvitationDetailActivity.this,
					new StringBuffer(APPConstants.URL_GETRACEINFOAPP2).append(MyApplication.app2Token).append("&user_account=").append(MyApplication.senderUser.getUser_account())
							.append("&race_id=").append(raceId).toString());
			return json;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			if (result != null) {
				if ("200".equals(result[0])) {
					// 资源文件内的费用类型数组
					String[] feeArr = getResources().getStringArray(R.array.competition_fee);
					ArrayList<String> arr = new ArrayList<String>();
					arr.add("");// 将集合下标为0的位置占据
					for (String fee : feeArr) {
						arr.add(fee);
					}
					infoAPP2 = JSON.parseObject(result[1], GetRaceInfoAPP2.class);
					if (infoAPP2 != null) {
						tvSportTag.setText(infoAPP2.getSport_tag_name());
						tvAddress.setText(infoAPP2.getRace_address());
						tvTime.setText(infoAPP2.getRace_time());
						tvTheme.setText(infoAPP2.getRace_title());
						tvFee.setText(arr.get(Integer.parseInt(infoAPP2.getRace_expenses())));
						tvIllustration.setText(infoAPP2.getRace_desc());
						tvState.setTextColor(getResources().getColor(R.color.text_green));
						is_submit = infoAPP2.isIs_submit();
						infoAPP2.getRace_time().split(" ");
						// Date date = new
						// Date(Integer.parseInt(infoAPP2.getRace_time().split(" ")[0].split("-")[0]),
						// Integer.parseInt(infoAPP2.getRace_time().split(" ")[0].split("-")[1]),
						// Integer.parseInt(infoAPP2.getRace_time().split(" ")[0].split("-")[2]),
						// Integer.parseInt(infoAPP2.getRace_time().split(" ")[1].split(":")[0]),
						// Integer.parseInt(infoAPP2.getRace_time().split(" ")[1].split(":")[1]),
						// Integer.parseInt(infoAPP2.getRace_time().split(" ")[1].split(":")[2]));
						// date.compareTo(new Date());

						// 赛事状态（-1：已发送邀请；1：已接受邀请；2：发起人已上报结果；3：接收人已上报结果；4 :
						// 双方都已上报结果）
						if (infoAPP2.getRace_status() == -1) {
							// 已发送邀请
							if (type == 2) {// 我发起的
								tvState.setText("等待对方接受");
								tvTitle.setText("等待对方接受");
							} else if (type == 3) {// 我参与的
								tvState.setText("等待您接受");
								tvTitle.setText("等待您接受");
								tvCommitOrAccept.setText("接受邀请");
								tvCommitOrAccept.setVisibility(View.VISIBLE);
							}
						} else if (infoAPP2.getRace_status() == 1) {
							// 已接受邀请
							if (infoAPP2.getRace_result() == null) {
								tvState.setText("赛后请提交战绩");
								tvTitle.setText("赛后请提交战绩");
								tvCommitOrAccept.setText("提交战绩");
								tvCommitOrAccept.setVisibility(View.VISIBLE);
							}
						} else if (infoAPP2.getRace_status() == 2) {
							// 发起人已上报结果
							tvTitle.setText("等待结果");
							if (type == 2) {
								tvState.setText("等待对方提交结果");
								tvTitle.setText("等待对方提交结果");
								tvCommitOrAccept.setVisibility(View.INVISIBLE);
							} else if (type == 3) {
								tvState.setText("请提交战绩");
								tvTitle.setText("请提交战绩");
								tvCommitOrAccept.setText("提交战绩");
								tvCommitOrAccept.setVisibility(View.VISIBLE);
							}
						} else if (infoAPP2.getRace_status() == 3) {
							// 接收人已上报结果
							tvTitle.setText("等待结果");
							if (type == 3) {
								tvState.setText("等待对方提交结果");
								tvTitle.setText("等待对方提交结果");
								tvCommitOrAccept.setVisibility(View.INVISIBLE);
							} else if (type == 2) {
								tvState.setText("请提交战绩");
								tvTitle.setText("请提交战绩");
								tvCommitOrAccept.setText("提交战绩");
								tvCommitOrAccept.setVisibility(View.VISIBLE);
							}
						} else if (infoAPP2.getRace_status() == 4) {
							// 双方都已上报结果
							if (infoAPP2.getRace_result() != null) {
								tvState.setText(infoAPP2.getRace_result());
							}
						}
						// 根据类型设置控件中的信息.如果是我发起的就用To_user_info.如果是我参与的就用From_user_info()
						if (infoAPP2.isIs_sponsor()) {
							type = 2;
						} else {
							type = 3;
						}
						if (type == 2) {// 我发起的
							toUserInfo = infoAPP2.getTo_user_info();
							MyApplication.bitmapUtils.display(imgHead, toUserInfo.getUser_logo_200());
							tvName.setText(toUserInfo.getUser_nickname());
							tvVitality.setText(toUserInfo.getVitality_name());
							if (toUserInfo.getUser_gender() == 2) {
								imgSex.setImageResource(R.drawable.woman);
							}

						} else if (type == 3) {// 我参与的
							fromUserInfo = infoAPP2.getFrom_user_info();
							MyApplication.bitmapUtils.display(imgHead, fromUserInfo.getUser_logo_200());
							tvName.setText(fromUserInfo.getUser_nickname());
							tvVitality.setText(fromUserInfo.getVitality_name());
							if (fromUserInfo.getUser_gender() == 2) {
								imgSex.setImageResource(R.drawable.woman);
							}
						}
					}
				} else {
					Toast.makeText(activity, ParseErrorJsonUtils.getErrorMsg(result).getError(), Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(activity, "请求错误", Toast.LENGTH_LONG).show();
			}
			MyProgressDialog.closeDialog();
		}
	}

	@OnClick({ R.id.common_title_back, R.id.activity_myjoin_or_invitation_detail_commit_or_accept })
	public void onclick(View v) {
		if (v.getId() == R.id.common_title_back) {
			activity.finish();
		}
		if (v.getId() == R.id.activity_myjoin_or_invitation_detail_commit_or_accept) {
			if ("提交战绩".equals(tvCommitOrAccept.getText().toString())) {
				 if (is_submit) {
				 // 挑战到提交战绩的界面
				Intent intent = new Intent(activity, SubmitResultActivity.class);
				intent.putExtra("infoAPP2", infoAPP2);
				intent.putExtra("type", type);
				intent.putExtra("race_id", getIntent().getIntExtra("race_id", 0));
				startActivity(intent);
				activity.finish();
				 } else {
				 Toast.makeText(activity, "请在约赛后两小时提交战绩",
				 Toast.LENGTH_LONG).show();
				 }
			} else if ("接受邀请".equals(tvCommitOrAccept.getText().toString())) {// 我参与的
				new PostTask().execute();
			}
		}
	}

	/**
	 * @ClassName: PostTask
	 * @Description: 接受约赛; 入参:1.user_account 用户啪啪号 ; 2.race_id 约赛ID;3.is_ios
	 *               是否IOS端 1表示ios 其他则为android(-1)
	 * @author 肖邦
	 * @date 2015-1-27 下午2:11:34
	 */
	class PostTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPreExecute() {
			MyProgressDialog.showDialog(activity, "接受约赛", "提交申请中...");
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(Void... params) {
			return MyHttpClient.postDataToService(MyJoinOrInvitationDetailActivity.this, APPConstants.URL_DISPOSERACEAPP2, MyApplication.app2Token, new String[] { "user_account",
					"race_id", "is_ios" }, new String[] { MyApplication.preferences.getString("user_account", ""), String.valueOf(raceId), APPConstants.IS_IOS }, null, null);
		}

		@Override
		protected void onPostExecute(String[] json) {
			super.onPostExecute(json);
			MyProgressDialog.closeDialog();
			if (json != null) {
				if ("200".equals(json[0])) {
					try {
						JSONObject jsonObject = new JSONObject(json[1]);
						boolean is_sussess = jsonObject.getBoolean("is_sussess");
						if (is_sussess) {
							Toast.makeText(activity, "接受成功", Toast.LENGTH_LONG).show();
							activity.finish();
						} else {
							Toast.makeText(activity, "接受失败", Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(activity, ParseErrorJsonUtils.getErrorMsg(json).getError(), Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(activity, "接受失败", Toast.LENGTH_LONG).show();
			}
		}
	}
}
