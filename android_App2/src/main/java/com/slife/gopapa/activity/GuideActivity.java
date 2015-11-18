package com.slife.gopapa.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.adapter.MyFragmentPagerAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.fragment.GuidFragment;

/**
 * @ClassName: GuideActivity
 * @Description: 引导页界面。（总共三张图片） 利用Viewpager+Fragment来实现
 * @author 菲尔普斯
 * @date 2015-1-4 下午4:57:24
 * 
 */
@ContentView(R.layout.activity_guide)
public class GuideActivity extends FragmentActivity {
	@ViewInject(R.id.guide_viewpager)
	private ViewPager viewPager;
	private List<Fragment> list;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 去除标题栏
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // 隐藏键盘
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 设置只能竖屏
		AppManager.addActivity(this);
		if (MyApplication.IMEI == null || "".equals(MyApplication.IMEI)) {
			MyApplication.IMEI = ((TelephonyManager) this
					.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
		}
		ViewUtils.inject(GuideActivity.this);
		list = new ArrayList<>();
		// 循环加载三个Fragment
		for (int i = 1; i < 5; i++) {
			list.add(new GuidFragment(i));
		}
		//为ViewPager设置适配器
		viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), list));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
}
