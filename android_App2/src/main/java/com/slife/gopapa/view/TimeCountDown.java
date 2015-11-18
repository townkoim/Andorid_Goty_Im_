package com.slife.gopapa.view;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

/**
 * @ClassName: TimeCountUtils
 * @Description: 倒计时按钮
 * @author 菲尔普斯
 * @date 2014-12-30 下午3:30:37
 * 
 */
public class TimeCountDown extends CountDownTimer {
	private View view;

	public TimeCountDown(long millisInFuture, long countDownInterval,
			View view) {
		super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		this.view = view;
	}

	@Override
	public void onTick(long millisUntilFinished) {
		view.setClickable(false);
		((TextView) view).setText(millisUntilFinished / 1000 + "秒");
		if(millisUntilFinished / 1000==1){
			onFinish();
		}
	}

	@Override
	public void onFinish() {
		view.setClickable(true);
		((TextView) view).setText("重新获取");
		TimeCountDown.this.cancel();
	}
}
