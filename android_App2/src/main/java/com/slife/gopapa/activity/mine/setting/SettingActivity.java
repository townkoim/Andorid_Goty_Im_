package com.slife.gopapa.activity.mine.setting;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.activity.login.LoginActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.feedback.bug.FeedBackBugActivity;
import com.slife.gopapa.task.CheckVersionTask;
import com.slife.gopapa.utils.FileUtils;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * @ClassName: SettingActivity
 * @Description: 设置界面
 * @author 菲尔普斯
 * @date 2015-1-8 下午5:27:04
 * 
 */
@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity implements OnClickListener, OnLongClickListener {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;
	@ViewInject(R.id.common_title_name)
	private TextView tvTitleName; //标题栏名字
	@ViewInject(R.id.setting_update_password)
	private RelativeLayout updataPassword; //修改密码
	@ViewInject(R.id.setting_remind_setting)
	private RelativeLayout remindSetting; //提醒设置
	@ViewInject(R.id.setting_update_new_version)
	private RelativeLayout updateNewVersion; //版本更新
	@ViewInject(R.id.setting_opinion_feedback)
	private RelativeLayout opinion_feedback; //意见反馈
	@ViewInject(R.id.setting_user_agreement)
	private RelativeLayout userAgreement; //用户协议
	@ViewInject(R.id.setting_clear_cache_layout)
	private RelativeLayout settingClearCache; //清理缓存
	@ViewInject(R.id.setting_about_papa)
	private RelativeLayout settingAboutPapa; //关于啪啪
	@ViewInject(R.id.setting_logout)
	private RelativeLayout settingLogout; //退出
	@ViewInject(R.id.setting_update_news_version_image)
	private ImageView imgUpdateVersion; // 图片旋转控件
	private Animation animation = null;	//更新版本的时候旋转的动画

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(SettingActivity.this);
		tvTitleName.setText(R.string.setting);
		updataPassword.setOnClickListener(this);
		remindSetting.setOnClickListener(this);
		updateNewVersion.setOnClickListener(this);
		opinion_feedback.setOnClickListener(this);
		userAgreement.setOnClickListener(this);
		settingAboutPapa.setOnClickListener(this);
		settingAboutPapa.setOnLongClickListener(this);
	}

	@OnClick({ R.id.common_title_back })
	public  void imgTitleBackOnclick(View v) {
		if (animation != null) {
			animation.cancel();
		}
		SettingActivity.this.finish();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = null;
		if (id == R.id.setting_update_password) {	//修改密码
			intent = new Intent(SettingActivity.this,
					UpdatePasswordActivity.class);
		} else if (id == R.id.setting_remind_setting){ //提醒设置
			intent = new Intent(SettingActivity.this,
					RemindSettingActivity.class);
		} else if (id == R.id.setting_update_new_version) { //版本更新
			new CheckVersionTask(SettingActivity.this, imgUpdateVersion).execute();
		} else if (id == R.id.setting_opinion_feedback) { //意见反馈
			intent = new Intent(SettingActivity.this,
					OpinionFeddBackActivity.class);
		} else if (id == R.id.setting_user_agreement) {  //用户协议
			intent = new Intent(SettingActivity.this,
					AgreenmentActivity.class);
		} else if (id == R.id.setting_about_papa) {  //关于啪啪
			intent = new Intent(SettingActivity.this,
					AboutPapaActivity.class);
		}

		if (intent != null) {
			startActivity(intent);
		}
	}

	@OnClick({ R.id.setting_logout, R.id.setting_clear_cache_layout })
	public  void cacheExitOnClick(View v) {
		int id = v.getId();
		if (id == R.id.setting_clear_cache_layout) {
			showDialog("清理缓存", "确定要清理缓存？", 0);
		} else if (id == R.id.setting_logout) {
			showDialog("退出账号", "确定退出当前账号？", 1);
		}
	}

	/**
	 * @Title: showDialog
	 * @Description: 清除缓存和退出对话框
	 * @param @param title 对话框标题
	 * @param @param message 对话框内容
	 * @param @param tag 标签（0代表清除缓存，1代表退出）
	 * @return void
	 * @throws
	 */
	private void showDialog(String title, String message, int tag) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SettingActivity.this).setIcon(R.drawable.ic_launcher)
				.setTitle(title).setMessage(message);
		setPositiveButton(tag, builder);
		setNegativeButton(tag, builder);
		builder.show();
	}

	/**
	 * @Title: setPositiveButton
	 * @Description: 确定对话框
	 * @param @param buildr
	 * @param @return
	 * @return AlertDialog.Builder
	 * @throws
	 */
	private AlertDialog.Builder setPositiveButton(final int tag,
			AlertDialog.Builder builder) {
		return builder.setPositiveButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
	}

	private AlertDialog.Builder setNegativeButton(final int tag,
			AlertDialog.Builder builder) {
		return builder.setNegativeButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (tag == 0) { // 清理缓存
							MyProgressDialog.showDialog(SettingActivity.this,
									"清理缓存", "正在清理");
							String imgPath=FileUtils.getStorageDirectory();
							File file = new File(imgPath);
							if(file.exists()){
								FileUtils.delAllFile(imgPath);
								MyApplication.preferences.edit().putString(MyApplication.preferences.getString("user_account","")+"_head_file", "").commit();
							}
							// 此处做清理缓存的操作
							new CountDownTimer(2000, 10) {

								@Override
								public void onTick(long millisUntilFinished) {

								}

								@Override
								public void onFinish() {
									MyProgressDialog.closeDialog();
								}
							}.start();
						} else if (tag == 1) {	//退出操作
								Editor editor = MyApplication.preferences.edit(); // 修改数据库里面保存的用户信息，全部为null
								editor.putString(DBConstants.USER_LOGIN_NAME, null);
								editor.putString(DBConstants.USER_PASSWORD, null);
								editor.putString(DBConstants.USER_EXTEND_ACCOUNT, null);
								editor.putString(DBConstants.USER_ACCOUNT, null);
								editor.putString(DBConstants.USER_NICKNAME, null);
								editor.putInt(DBConstants.NEW_FRIENDS_COUNT, 0);
								editor.putInt(DBConstants.NEW_ABOUT_MATCH_COUNT, 0);
								editor.putInt(DBConstants.NEW_OFFCIAL_ASSISTANT_COUNT, 0);
								editor.commit();
								startActivity(new Intent(SettingActivity.this,LoginActivity.class)); // 跳转到登陆界面
								AppManager.existAPP();
						}
					}
				});
	}
	
	@Override
	public boolean onLongClick(View v) {
		startActivity(new Intent(this, FeedBackBugActivity.class));
		return false;
	}
}
