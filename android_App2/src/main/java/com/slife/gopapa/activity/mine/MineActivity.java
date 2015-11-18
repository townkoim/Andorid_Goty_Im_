package com.slife.gopapa.activity.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.activity.competition.CompetitionPointActivity;
import com.slife.gopapa.activity.competition.MyJoinOrInvitationActivity;
import com.slife.gopapa.activity.competition.VitalityPointActivity;
import com.slife.gopapa.activity.mine.setting.SettingActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.utils.FileUtils;
import com.slife.gopapa.view.XCRoundRectImageView;

/**
 * 
 * @ClassName: MineActivity
 * @Description: 我的界面,原型图"我的";
 * 				(包含个人信息/活力积分/战绩积分/我参与的/我发布的/设置)
 * 				点击条目跳转到对应的界面;
 * @author 肖邦
 * @date 2015-1-7 下午5:09:20
 * 
 */
@ContentView(R.layout.activity_mine)
public class MineActivity extends BaseActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;//返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;//标题
	@ViewInject(R.id.activity_mine_img)
	private XCRoundRectImageView imgHead;// 用户头像
	@ViewInject(R.id.activity_mine_sex)
	private ImageView imgSex;// 性别
	@ViewInject(R.id.activity_mine_name)
	private TextView tvName;// 用户名
	@ViewInject(R.id.activity_mine_papa_vitality_name)
	private TextView tvVitalityName;// 活力名
	@ViewInject(R.id.activity_mine_age)
	private TextView tvAge;// 年龄
	@ViewInject(R.id.activity_mine_papa_number)
	private TextView tvPapaNum;

	private MineActivity activity;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ViewUtils.inject(MineActivity.this);
		activity = this;
		MyApplication.isRegister = 0;
		initView();
	}

	/**
	 * 
	* @Title: initView
	* @Description: 从preferences中取出加载过后的信息并填充到控件当中
	* @param 
	* @return void
	* @throws
	 */
	private void initView() {
		tvTitle.setText("我的");
		imgBack.setVisibility(View.GONE);
		SharedPreferences preferences = MyApplication.preferences;
		//获取用户的个人头像
		String path = FileUtils.getUserHeadImgPath();
		if (path!=null) {
			MyApplication.bitmapUtils.display(imgHead, path);
		}else{
			if (!"".equals(preferences.getString("user_logo_200", ""))) {
				MyApplication.bitmapUtils.display(imgHead, preferences.getString("user_logo_200", ""));
			}else{
				imgHead.setImageResource(R.drawable.common_users_icon_default);
			}
		}
		//从sharepreference中取得数据填充到控件中
		tvName.setText(preferences.getString("user_nickname", ""));
		tvAge.setText(preferences.getString("user_age", ""));
		tvPapaNum.setText(preferences.getString("user_account", ""));
		tvVitalityName.setText(preferences.getString("vitality_name", ""));
		//设置用户性别的头像.
		if ("1".equals(preferences.getString("user_gender", ""))) {// 男
			imgSex.setImageResource(R.drawable.man);
		} else {
			imgSex.setImageResource(R.drawable.woman);
		}
	}

	@OnClick({ R.id.activity_mine_rl_information, R.id.activity_mine_rl_invatation, R.id.activity_mine_rl_join, R.id.activity_mine_rl_military, R.id.activity_mine_rl_setting,
			R.id.activity_mine_rl_vitality })
	public void onclick(View v) {
		//完成跳转页面的动作
		Intent intent = null;
		if (v.getId() == R.id.activity_mine_rl_information) {
			intent = new Intent(activity, PersonalInformationActivity.class);// 个人信息 
			startActivity(intent);
			activity.finish();
			return;
		} else if (v.getId() == R.id.activity_mine_rl_vitality) {
			intent = new Intent(activity, VitalityPointActivity.class);// 活力积分
		} else if (v.getId() == R.id.activity_mine_rl_military) {
			intent = new Intent(activity, CompetitionPointActivity.class);// 战绩积分
		} else if (v.getId() == R.id.activity_mine_rl_invatation) {
			intent = new Intent(activity, MyJoinOrInvitationActivity.class);// 我发起的
			intent.putExtra("type", 2);
		} else if (v.getId() == R.id.activity_mine_rl_join) {
			intent = new Intent(activity, MyJoinOrInvitationActivity.class);// 我参与的
			intent.putExtra("type", 3);
		} else if (v.getId() == R.id.activity_mine_rl_setting) {
			intent = new Intent(activity, SettingActivity.class);// 我参与的
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	private long mExitTime;// 计算按两次返回键的间隔

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();

			} else {
				AppManager.existAPP();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
