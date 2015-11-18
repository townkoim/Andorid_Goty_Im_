package com.slife.gopapa.activity;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.mine.MineActivity;
import com.slife.gopapa.activity.news.NewsActivity;
import com.slife.gopapa.activity.ranking.RankingActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.database.DBHelperOperation;
import com.slife.gopapa.task.CheckVersionTask;

/**
 * @ClassName: TabMainActivity
 * @Description:底部菜单栏的界面。（第一个为排行榜，第二个为消息，第三个为我的）
 * @author 菲尔普斯
 * @date 2015-1-4 上午11:54:36
 * 
 */
@SuppressLint("ResourceAsColor")
@SuppressWarnings("deprecation")
@ContentView(R.layout.activity_tab_main)
public class TabMainActivity extends TabActivity {
	@ViewInject(R.id.tab_relativelayout_ranking)
	private RelativeLayout layoutRanking;// 排名版的线性布局
	@ViewInject(R.id.tab_relativelayout_news)
	private RelativeLayout layoutNews;// 消息的线性布局
	@ViewInject(R.id.tab_relativelayout_mine)
	private RelativeLayout layoutMine;// 我的线性布局
	@ViewInject(R.id.tab_img_ranking)
	private ImageView imgRanking;// 排名的图片
	@ViewInject(R.id.tab_img_news)
	private ImageView imgNews;// 消息的图片
	@ViewInject(R.id.tab_img_mine)
	private ImageView imgMine; // 我的图片
	@ViewInject(R.id.tab_ranking_text)
	private TextView tvRanking;
	@ViewInject(R.id.tab_news_text)
	private TextView tvNews;
	@ViewInject(R.id.tab_mine_text)
	private TextView tvMine;
	
	
	@ViewInject(R.id.tab_news_count)
	private Button btnTrackPoint; // 显示消息数量的小红点
	private Intent rankingIntent, newsIntent, mineIntent; // 三个intent对象，用来根据不用的tab来设置intent以便跳转到对应的Activity
	private static TabHost tabHost;// 选项卡
	int mCurTabId = R.id.tab_relativelayout_ranking; // 默认为第一个tab选项卡(默认就为排名的线性布局的选项卡)
	int imgTabId = R.id.tab_img_ranking;

	private TrackPointReceiver receiver = new TrackPointReceiver(); // 广播接受者，用来接收广播，用来更新小红点上显示的未读消息的数量

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 去除标题栏
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // 隐藏键盘
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 设置只能竖屏
		AppManager.addActivity(TabMainActivity.this);
		ViewUtils.inject(TabMainActivity.this);
		MyApplication.isTabActivity = true; // 设置全局变量isTabActivity为true，表示这个界面已经初始化过了
		btnTrackPoint.setText("0");
		btnTrackPoint.setVisibility(View.GONE);
		tabHost = getTabHost(); // 得到tabHost对象
		prepareIntent();
		setupIntent();
		// 检查版本更新
		if (NetWorkState.checkNet(getApplicationContext())) {
			if (MyApplication.commonToken != null
					&& !"".equals(MyApplication.commonToken)) {
				new CheckVersionTask(TabMainActivity.this).execute();
			}
		}
		if (getIntent() != null && getIntent().getBooleanExtra("tag", false)) {
			initView();
			tabHost.setCurrentTabByTag("我的");
			imgMine.setImageResource(R.drawable.tab_mine_select);
			imgTabId = R.id.tab_img_mine;
		} else if (getIntent() != null
				&& getIntent().getBooleanExtra("push", false)) {
			initView();
			tabHost.setCurrentTabByTag("消息");
			imgNews.setImageResource(R.drawable.tab_news_select);
			
			imgTabId = R.id.tab_img_news;
		}
		if (MyApplication.preferences.getString(DBConstants.USER_ACCOUNT, null) != null
				&& !"".equals(MyApplication.preferences.getString(
						DBConstants.USER_ACCOUNT, null))) {
			initTrackPoint();
		}
	}
	

	/***
	 * @Title: initTrackPoint
	 * @Description: 初始化小红点的值（从数据库读取）
	 * @param
	 * @return void
	 * @throws
	 */
	private void initTrackPoint() {
		int count = DBHelperOperation
				.queryRecentContactMsgCount(MyApplication.preferences
						.getString(DBConstants.USER_ACCOUNT, null)) // 数据库的未读消息数量（sqlite）
				+ MyApplication.preferences.getInt(
						DBConstants.NEW_FRIENDS_COUNT, 0) // 新的朋友的未读消息
				+ MyApplication.preferences.getInt(
						DBConstants.NEW_ABOUT_MATCH_COUNT, 0)// 约赛的未读消息
				+ MyApplication.preferences.getInt(
						DBConstants.NEW_OFFCIAL_ASSISTANT_COUNT, 0);// 官方助手的未读消息
		if (count > 0) {
			MyApplication.trackPointMsgCount=count;
			if (MyApplication.trackPointMsgCount > 0) {
				btnTrackPoint.setVisibility(View.VISIBLE);
				btnTrackPoint.setText(String
						.valueOf(MyApplication.trackPointMsgCount));
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(APPConstants.ACTION_TRACKPOINT_CHANGE);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	/***
	 * @Title: prepareIntent
	 * @Description:初始化Intent
	 * @param
	 * @return void
	 * @throws
	 */
	private void prepareIntent() {
		rankingIntent = new Intent(TabMainActivity.this, RankingActivity.class);
		newsIntent = new Intent(TabMainActivity.this, NewsActivity.class);
		mineIntent = new Intent(TabMainActivity.this, MineActivity.class);
	}

	/**
	 * @Title: setupIntent
	 * @Description:建立选项卡
	 * @param
	 * @return void
	 * @throws
	 */
	private void setupIntent() {
		tabHost.addTab(buildTab("排名", "排名", R.drawable.tab_ranking_unselect,
				rankingIntent));
		tabHost.addTab(buildTab("消息", "消息", R.drawable.tab_news_unselect,
				newsIntent));
		tabHost.addTab(buildTab("我的", "我的", R.drawable.tab_mine_unselect,
				mineIntent));
	}

	/***
	 * @Title: buildTab
	 * @Description: 创建tabSpec(选项卡的)
	 * @param @param tag 标签
	 * @param @param label 标注
	 * @param @param drawble 图片
	 * @param @param intent intent对象
	 * @param @return
	 * @return TabHost.TabSpec 具体的选项卡的内容
	 * @throws
	 */
	private TabHost.TabSpec buildTab(String tag, String label, int drawble,
			Intent intent) {
		TabHost.TabSpec spec = tabHost.newTabSpec(tag)
				.setIndicator(label, getResources().getDrawable(drawble))
				.setContent(intent);
		return spec;
	}

	/***
	 * @Title: setCurrentTabByTag
	 * @Description: 设置当前tab选项
	 * @param @param tab
	 * @return void
	 * @throws
	 */
	public static void setCurrentTabByTag(String tab) {
		tabHost.setCurrentTabByTag(tab);
	}

	/**
	 * @Title: tabOnclick
	 * @Description: 底部菜单线性布局的监听器，根据不同的线性布局跳转到不同的Activity界面
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.tab_relativelayout_ranking, R.id.tab_relativelayout_news,
			R.id.tab_relativelayout_mine })
	public void tabOnclick(View v) {
		int checkedId = v.getId();
		if (mCurTabId == checkedId) {
			return;
		}
		initView();
		if (checkedId == R.id.tab_relativelayout_ranking) {
			imgRanking.setImageResource(R.drawable.tab_ranking_select);
			tvRanking.setTextColor(Color.parseColor("#39CC73"));
			setCurrentTabByTag("排名");
		} else if (checkedId == R.id.tab_relativelayout_news) {
			imgNews.setImageResource(R.drawable.tab_news_select);
			tvNews.setTextColor(Color.parseColor("#39CC73"));
			setCurrentTabByTag("消息");
		} else if (checkedId == R.id.tab_relativelayout_mine) {
			imgMine.setImageResource(R.drawable.tab_mine_select);
			tvMine.setTextColor(Color.parseColor("#39CC73"));
			setCurrentTabByTag("我的");
		}
		mCurTabId = checkedId;
	}

	/***
	 * @Title: imgOnclick
	 * @Description: 底部菜单图片的监听器，根据不同的图片跳转到不同的Activity界面
	 * @param @param v
	 * @return void
	 * @throws
	 */
//	@OnClick({ R.id.tab_img_ranking, R.id.tab_img_news, R.id.tab_img_mine })
//	public void imgOnclick(View v) {
//		int id = v.getId();
//		if (imgTabId == id) {
//			return;
//		}
//		initView();
//		if (id == R.id.tab_img_ranking) {
//			imgRanking.setImageResource(R.drawable.tab_ranking_select);
//			setCurrentTabByTag("排名");
//		} else if (id == R.id.tab_img_news) {
//			imgNews.setImageResource(R.drawable.tab_news_select);
//			setCurrentTabByTag("消息");
//		} else if (id == R.id.tab_img_mine) {
//			imgMine.setImageResource(R.drawable.tab_mine_select);
//			setCurrentTabByTag("我的");
//		}
//		imgTabId = id;
//	}

	/**
	 * @Title: initView
	 * @Description: 初始化UI（在监听器之前让选项卡默认设置为全部不选中）
	 * @param
	 * @return void
	 * @throws
	 */
	private void initView() {
		imgRanking.setImageResource(R.drawable.tab_ranking_unselect);
		imgNews.setImageResource(R.drawable.tab_news_unselect);
		imgMine.setImageResource(R.drawable.tab_mine_unselect);
		tvRanking.setTextColor(Color.parseColor("#CCCCCC"));
		tvNews.setTextColor(Color.parseColor("#CCCCCC"));
		tvMine.setTextColor(Color.parseColor("#CCCCCC"));
	}

	/**
	 * @ClassName: TrackPointReceiver
	 * @Description: 广播接受者，用来控制显示小红点
	 * @author 菲尔普斯
	 * @date 2015-1-24 上午10:44:53
	 */
	class TrackPointReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals(APPConstants.ACTION_TRACKPOINT_CHANGE)) { // 得到小红点数量发生改变的广播
				int count = MyApplication.trackPointMsgCount;
				if (count > 0 && MyApplication.isTabActivity) { // 如果数量是大于0的，就将小红点显示并显示数量，否则就不显示小红点
					btnTrackPoint.setVisibility(View.VISIBLE);
					btnTrackPoint.setText(String.valueOf(count));
				} else {
					btnTrackPoint.setVisibility(View.GONE);
				}
			}
		}

	}

}
