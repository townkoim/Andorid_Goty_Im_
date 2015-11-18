package com.slife.gopapa.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

/**
 * @ClassName: MyFragmentPagerAdapter
 * @Description: ViewPageFragment适配器
 * @author 菲尔普斯
 * @date 2014-12-29 下午3:03:05
 * 
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragmentsList;

	public MyFragmentPagerAdapter(FragmentManager fm,
			List<Fragment> fragmentsList) {
		super(fm);
		this.fragmentsList = fragmentsList;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragmentsList.get(arg0);
	}

	@Override
	public int getCount() {
		return fragmentsList.size();
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
	}
}
