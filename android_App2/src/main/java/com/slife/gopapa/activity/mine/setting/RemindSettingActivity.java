package com.slife.gopapa.activity.mine.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.database.DBConstants;


/**
* @ClassName: RemindSettingActivity 
* @Description: 提醒设置界面
* @author 菲尔普斯
* @date 2015-1-9 下午2:19:29 
*
 */
@ContentView(R.layout.activity_remind_setting)
public class RemindSettingActivity extends BaseActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;
	@ViewInject(R.id.common_title_name)
	private TextView tvTitleName;
	@ViewInject(R.id.reming_setting_voice)
	private ImageView imgVoice;
	@ViewInject(R.id.reming_setting_vibration)
	private ImageView imgVibration;
	@ViewInject(R.id.reming_setting_disturbing)
	private ImageView imgDisturbing;
	private boolean isVoice=false; // 是否开启声音
	private boolean isVibration=false; // 是否开启震动
	private boolean isDisturbing=false; // 是否开启免打扰

	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(RemindSettingActivity.this);
		preferences = MyApplication.preferences;
		tvTitleName.setText(R.string.setting_remind_setting);
		initImageSwitch();
	}
	/**
	* @Title: initImageSwitch
	* @Description:初始化图片开关,从数据库去取得上次保存的开关的状态
	* @param 
	* @return void
	* @throws
	 */
	private void initImageSwitch() {
		isVoice=preferences.getBoolean(DBConstants.REMIND_VOICE, true);
		isVibration=preferences.getBoolean(DBConstants.REMIND_VIBRATION, true);
		isDisturbing=preferences.getBoolean(DBConstants.REMIND_DISTURBING, true);
		if(!isVoice){
			imgVoice.setImageResource(R.drawable.control_open);
		}else{
			imgVoice.setImageResource(R.drawable.control_close);
		}
		if(!isVibration){
			imgVibration.setImageResource(R.drawable.control_open);
		}else{
			imgVibration.setImageResource(R.drawable.control_close);
		}
		if(!isDisturbing){
			imgDisturbing.setImageResource(R.drawable.control_open);
		}else{
			imgDisturbing.setImageResource(R.drawable.control_close);
		}
		
		
	}


	/**
	 * @Title: voiceOnclick
	 * @Description: 声音开关监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.reming_setting_voice })
	public  void voiceOnclick(View v) {
		if (!isVoice) {
			imgVoice.setImageResource(R.drawable.control_close);
			isVoice = true;
		} else {
			imgVoice.setImageResource(R.drawable.control_open);
			isVoice = false;
		}
	}

	/**
	 * @Title: vibrationOnclick
	 * @Description: 震动开关监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.reming_setting_vibration })
	public  void vibrationOnclick(View v) {
		if (!isVibration) {
			imgVibration.setImageResource(R.drawable.control_close);
			isVibration = true;
		} else {
			imgVibration.setImageResource(R.drawable.control_open);
			isVibration = false;
		}
	}

	/**
	 * 
	 * @Title: disturbingOnclick
	 * @Description: 免打扰开关监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.reming_setting_disturbing })
	public void disturbingOnclick(View v) {
		if (!isDisturbing) {
			imgDisturbing.setImageResource(R.drawable.control_close);
			isDisturbing = true;
		} else {
			imgDisturbing.setImageResource(R.drawable.control_open);
			isDisturbing = false;
		}
	}
	/**
	* @Title: imgBackOnclick
	* @Description: 返回按钮,返回的时候要修改数据库里面开关的状态
	* @param @param v
	* @return void
	* @throws
	 */
	@OnClick({ R.id.common_title_back })
	public  void imgBackOnclick(View v) {
		sharedPrefrencesHelper(DBConstants.REMIND_VOICE,isVoice);
		sharedPrefrencesHelper(DBConstants.REMIND_VIBRATION,isVibration);
		sharedPrefrencesHelper(DBConstants.REMIND_DISTURBING,isDisturbing);
		RemindSettingActivity.this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		sharedPrefrencesHelper(DBConstants.REMIND_VOICE,isVoice);
		sharedPrefrencesHelper(DBConstants.REMIND_VIBRATION,isVibration);
		sharedPrefrencesHelper(DBConstants.REMIND_DISTURBING,isDisturbing);
		RemindSettingActivity.this.finish();
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * @Title: sharedPrefrencesHelper
	 * @Description: 根据键值对来修改SharedPreferences当中的数据
	 * @param @param key
	 * @param @param value
	 * @return void
	 * @throws
	 */
	private void sharedPrefrencesHelper(String key, boolean value) {
		editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
}
