package com.slife.gopapa.activity.ranking;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.R.color;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.activity.competition.InvitationActivity;
import com.slife.gopapa.activity.news.ChatActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.ChatUserInfo;
import com.slife.gopapa.model.PersonalInfo;
import com.slife.gopapa.model.Race;
import com.slife.gopapa.model.UserInfo;
import com.slife.gopapa.task.AddFriendsDisponseTask;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * @ClassName: PersonInformationActivity
 * @Description: 个人资料界面
 * @author 肖邦
 * @date 2015-1-20 下午1:08:03
 */
@ContentView(R.layout.activity_ranking_person_information)
public class PersonInformationActivity extends BaseActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;// 返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;// 标题
	@ViewInject(R.id.activity_ranking_person_information_ll_invatation)
	private RelativeLayout rlInvitation;// 邀请好友
	@ViewInject(R.id.activity_ranking_person_information_ll_chating)
	private RelativeLayout rlChating;// 聊天
	@ViewInject(R.id.activity_ranking_person_information_ll_friend)
	private RelativeLayout rlAddFriends;// 添加好友
	@ViewInject(R.id.activity_rangking_person_information_head)
	private ImageView imgHead;// 用户头像
	@ViewInject(R.id.activity_ranking_person_information_combat1)
	private ImageView imgHead1;// 邀请者头像 1
	@ViewInject(R.id.activity_ranking_person_information_combat3)
	private ImageView imgHead2;// 邀请者头像 2
	@ViewInject(R.id.activity_ranking_person_information_combat2)
	private ImageView imgToHead1;// 接受者头像1
	@ViewInject(R.id.activity_ranking_person_information_combat4)
	private ImageView imgToHead2;// 接受者头像2
	@ViewInject(R.id.activity_ranking_person_information_age_img)
	private ImageView imgHeadSex;// 用户你年龄背景
	@ViewInject(R.id.activity_ranking_person_information_sex)
	private ImageView imgSex;// 用户性别
	@ViewInject(R.id.activity_ranking_person_information_name)
	private TextView tvName;// 用户名称
	@ViewInject(R.id.activity_ranking_person_information_age)
	private TextView tvAge;// 用户年龄
	@ViewInject(R.id.activity_ranking_person_information_vitality_num)
	private TextView tvVitality;// 活力
	@ViewInject(R.id.activity_ranking_person_information_vitality_name)
	private TextView tvVitalityName;// 活力名称
	@ViewInject(R.id.activity_ranking_person_information_area)
	private TextView tvArea;// 地区
	@ViewInject(R.id.activity_ranking_person_information_signture)
	private TextView tvSign;// 个性签名
	@ViewInject(R.id.activity_ranking_person_information_result1)
	private TextView tvResult1;// 比赛结果1
	@ViewInject(R.id.activity_ranking_person_information_result2)
	private TextView tvResult2;// 比赛结果2
	@ViewInject(R.id.activity_ranking_person_information_current1)
	private RelativeLayout rl1;// 最近比赛结果layout1
	@ViewInject(R.id.activity_ranking_person_information_current2)
	private RelativeLayout rl2;// 最近比赛结果layout2
	@ViewInject(R.id.activity_ranking_person_information_no_current)
	private TextView tvNoCurrent;// 没有最近战绩提示
	@ViewInject(R.id.activity_ranking_person_information_ll)
	private LinearLayout llBottom;
	private PersonInformationActivity activity;// 本类对象
	private String by_user_account = "";
	private UserInfo userInfo;// 用户信息
	private String self_user_account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		self_user_account = MyApplication.preferences.getString("user_account", "");
		if (getIntent() != null && getIntent().getStringExtra("by_user_account") != null) {
			by_user_account = getIntent().getStringExtra("by_user_account");
		}
		if(by_user_account.equals(self_user_account)){
			rlChating.setVisibility(View.GONE);
			rlInvitation.setVisibility(View.GONE);
			llBottom.setBackgroundColor(Color.WHITE);
		}
		initView();
		new Task().execute();
	}

	private void initView() {
		tvTitle.setText("个人资料");
	}

	/**
	 * 
	 * @ClassName: Task
	 * @Description: 加载用户个人信息
	 * @author 肖邦
	 * @date 2015-2-26 下午5:29:01
	 * 
	 */
	public class Task extends AsyncTask<String, Void, String[]> {

		@Override
		protected void onPreExecute() {
			MyProgressDialog.showDialog(activity, "排名个人信息", "请稍等...");
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(String... params) {
			String[] json = MyHttpClient.getJsonFromService(PersonInformationActivity.this, APPConstants.URL_GETPERSONALINFOAPP2 + MyApplication.app2Token + "&user_account="
					+ MyApplication.preferences.getString("user_account", "44723") + "&by_user_account=" + by_user_account);
			return json;
		}

		/**
		 * (非 Javadoc) Title: onPostExecute Description: 将获取到的信息填充到控件当中
		 * 
		 * @param result
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			MyProgressDialog.closeDialog();
			JsonObjectErrorDaoImpl.resolveJson(PersonInformationActivity.this, result, new JsonObjectErrorDao() {
				
				@Override
				public void disposeJsonObj(JSONObject obj) {
					PersonalInfo info = JSON.parseObject(obj.toString(), PersonalInfo.class);
					userInfo = info.getUser_info();
					tvName.setText(userInfo.getUser_nickname());
					tvArea.setText(userInfo.getNow_name());
					tvSign.setText(userInfo.getUser_sign());
					tvVitality.setText("战绩积分" + info.getPower_num());
					tvVitalityName.setText(info.getVitality_name());
					tvAge.setText(userInfo.getUser_age());
					setImg(imgHead, userInfo.getUser_logo_200());
					if (!info.isIs_friend() && !by_user_account.equals(self_user_account)) {
						rlAddFriends.setVisibility(View.VISIBLE);
					}
					if ("2".equals(userInfo.getUser_gender())) {
						imgSex.setImageResource(R.drawable.woman);
						imgHeadSex.setImageResource(R.drawable.age_woman);
					}
					if (info.getRace().size() == 2) {
						Race race1 = info.getRace().get(0);
						Race race2 = info.getRace().get(1);
						tvResult1.setText(race1.getRace_result());
						tvResult2.setText(race2.getRace_result());
						setImg(imgHead1, race1.getFrom_user_logo_200());
						setImg(imgHead2, race2.getFrom_user_logo_200());
						setImg(imgToHead1, race1.getTo_user_logo_200());
						setImg(imgToHead2, race2.getTo_user_logo_200());
					} else if (info.getRace().size() == 1) {
						Race race1 = info.getRace().get(0);
						tvResult1.setText(race1.getRace_result());
						setImg(imgHead1, race1.getFrom_user_logo_200());
						setImg(imgToHead1, race1.getTo_user_logo_200());
						rl2.setVisibility(View.GONE);
					} else if (info.getRace().size() == 0) {
						tvNoCurrent.setVisibility(View.VISIBLE);
						rl1.setVisibility(View.GONE);
						rl2.setVisibility(View.GONE);
					}
				}
			});
		}
	}

	/**
	 * @Title: setImg
	 * @Description: 设置图片的方法
	 * @param @param view
	 * @param @param url
	 * @return void
	 * @throws
	 */
	public void setImg(ImageView view, String url) {
		if (url != null && !"".equals(url)) {
			MyApplication.bitmapUtils.display(view, url);
		} else {
			view.setImageResource(R.drawable.common_users_icon_default);
		}
	}

	@OnClick({ R.id.common_title_back, R.id.activity_ranking_person_information_ll_invatation, R.id.activity_ranking_person_information_ll_chating,
			R.id.activity_ranking_person_information_ll_friend })
	public void onclick(View v) {
		if (v.getId() == R.id.common_title_back) {
			finish();
		}
		if (v.getId() == R.id.activity_ranking_person_information_ll_invatation) {// 邀请比赛
			Intent intent = new Intent(activity, InvitationActivity.class);
			intent.putExtra("user_account", by_user_account);
			startActivity(intent);
		}
		if (v.getId() == R.id.activity_ranking_person_information_ll_chating) {// 聊天
			if (userInfo != null && !"".equals(userInfo) && !"".equals(by_user_account)) {
				Intent intent = new Intent(PersonInformationActivity.this, ChatActivity.class);
				ChatUserInfo cUser = new ChatUserInfo();
				cUser.setUser_account(by_user_account);
				cUser.setExtend_user_account(userInfo.getExtend_user_account());
				String nickName = userInfo.getUser_nickname();
				if (nickName != null && !"".equals(nickName)) {
					cUser.setUser_nickname(nickName);
				}
				String logo = userInfo.getUser_logo_200();
				if (logo != null && !"".equals(logo)) {
					cUser.setUser_logo(logo);
				}
				intent.putExtra("users_info", cUser);
				startActivity(intent);
			}
		}
		if (v.getId() == R.id.activity_ranking_person_information_ll_friend) {// 添加好友
			if (MyApplication.senderUser != null && !"".equals(by_user_account)) {
				new AddFriendsDisponseTask(PersonInformationActivity.this, MyApplication.senderUser.getUser_account(), by_user_account).execute();
			}
		}
	}
}
