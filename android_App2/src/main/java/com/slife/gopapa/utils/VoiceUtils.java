package com.slife.gopapa.utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.database.DBConstants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

/***
 * @ClassName: VoiceUtils
 * @Description: 控制播放声音（短声音） 以及手机震动的助手类。
 * @author 菲尔普斯
 * @date 2015-2-13 上午9:55:56
 * 
 */
@SuppressLint("UseSparseArrays")
public class VoiceUtils {
	private SoundPool sp;
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	private Vibrator vibrator;
	private Context context;

	public VoiceUtils(Context context) {
		this.context = context;
		loadVoice();
	}

	/***
	 * @Title: loadVoice
	 * @Description:初始化声音播放的类和震动的类
	 * @param
	 * @return void
	 * @throws
	 */
	private void loadVoice() {
		sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		map.put(1, sp.load(context, R.raw.sound, 1));
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	/***
	 * @Title: playSound
	 * @Description: 播放声音以及手机震动
	 * @param
	 * @return void
	 * @throws
	 */
	public void playSound() {
		if (MyApplication.preferences.getBoolean(DBConstants.REMIND_DISTURBING,
				true)) {
			Calendar cal = Calendar.getInstance();// 当前日期
			int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
			int minute = cal.get(Calendar.MINUTE);// 获取分钟
			int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
			final int start = 2 * 60 + 01;// 起始时间 2:01的分钟数
			final int end = 7 * 60;// 结束时间 7:00的分钟数
			if (minuteOfDay >= start && minuteOfDay <= end) { //在时间范围内，则不播放声音和震动
				
			} else {
				SoundStart();
			}
		} else {
			SoundStart();
		}
	}

	private void SoundStart() {
		if (MyApplication.preferences
				.getBoolean(DBConstants.REMIND_VOICE, true)) {
			AudioManager am = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			float currentSound = am.getStreamVolume(AudioManager.STREAM_MUSIC);
			float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float volume = currentSound / maxSound;
			sp.play(map.get(1), volume, volume, 1, 0, 1.0f);
		}
		if (MyApplication.preferences.getBoolean(DBConstants.REMIND_VIBRATION,
				true)) {
			vibrator.vibrate(500);
		}
	}
}
