package com.slife.gopapa.activity.news;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseFragmentActivity;
import com.slife.gopapa.adapter.MyFragmentPagerAdapter;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.fragment.ContactsFriendsFragment;
import com.slife.gopapa.fragment.NewsFragment;

/**
 * @ClassName: NewsActivity
 * @Description: 消息类(有两个Fragment,1：NewsFragment消息界面 ，2：ContactsFriendsFragment 联系人界面)
 * @author 菲尔普斯
 * @date 2014-12-31 下午5:49:35
 * 
 */
@ContentView(R.layout.activity_news)
public class NewsActivity extends BaseFragmentActivity implements
		OnPageChangeListener {
	@ViewInject(R.id.news_new)
	private ImageView imgNews; // 消息
	@ViewInject(R.id.news_friends)
	private ImageView imgFriends; // 好友
	@ViewInject(R.id.news_viewpager)
	private ViewPager viewPager;
	@ViewInject(R.id.news_add)
	private ImageView imgAdd; // 标题栏的加号
	private MyFragmentPagerAdapter adapter;
	private List<Fragment> list;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ViewUtils.inject(NewsActivity.this);
		list = new ArrayList<>();
		list.add(new NewsFragment());
		list.add(new ContactsFriendsFragment());
		adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), list);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
	}

	@OnClick({ R.id.news_add })
	public  void addOnclick(View v) {
		startActivity(new Intent(NewsActivity.this, SearchFriendsActivity.class));
	}

	@OnClick({ R.id.news_new, R.id.news_friends })
	public  void textViewOnclick(View v) {
		initTextView();
		if (v.getId() == R.id.news_new) {
			viewPager.setCurrentItem(0);
			imgNews.setImageResource(R.drawable.news_select);
		} else if (v.getId() == R.id.news_friends) {
			viewPager.setCurrentItem(1);
			imgFriends.setImageResource(R.drawable.contact_select);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		initTextView();
		if (position == 0) {
			imgNews.setImageResource(R.drawable.news_select);
		} else if (position == 1) {
			imgFriends.setImageResource(R.drawable.contact_select);
		}

	}

	private void initTextView() {
		imgNews.setImageResource(R.drawable.news_unselect);
		imgFriends.setImageResource(R.drawable.contact_unselect);
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
