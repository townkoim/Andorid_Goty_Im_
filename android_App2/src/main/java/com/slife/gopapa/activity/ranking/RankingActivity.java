package com.slife.gopapa.activity.ranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseFragmentActivity;
import com.slife.gopapa.activity.login.SelectCityActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.fragment.RankingFragment;
import com.slife.gopapa.fragment.RankingFragment.onListViewScrollListener;
import com.slife.gopapa.fragment.RankingFragment.onRefreshingListener;
import com.slife.gopapa.utils.DialogUtils;
import com.slife.gopapa.utils.FileUtils;
import com.slife.gopapa.view.CityDialog;
import com.slife.gopapa.view.CityDialog.OnDialogDismissListener;
import com.slife.gopapa.view.citypicker.CityPicker.JSONParser;
import com.slife.gopapa.view.citypicker.CitycodeUtil;
import com.slife.gopapa.view.citypicker.Cityinfo;
import com.slife.gopapa.view.schoolpicker.FileUtil;
import com.viewpagerindicator.TabPageIndicator;

/**
 * 
 * @ClassName: RankingActivity
 * @Description: 这是排名的界面
 * @author 肖邦
 * @date 2015-1-4 上午10:00:21
 * 
 */
@ContentView(R.layout.activity_ranking)
public class RankingActivity extends BaseFragmentActivity {
	@ViewInject(R.id.activity_ranking_pager)
	private ViewPager pager;// ViewPager的adapter
	@ViewInject(R.id.activity_ranking_indicator)
	private TabPageIndicator indicator;// 实例化TabPageIndicator然后设置ViewPager与之关联
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;
	@ViewInject(R.id.common_address)
	private TextView tvAddress;// 地区
	@ViewInject(R.id.activity_ranking_school)
	private TextView tvSchool;// 校园
	@ViewInject(R.id.activity_ranking_district)
	private TextView tvDistrict;// 区
	@ViewInject(R.id.activity_ranking_city)
	private TextView tvCity;// 市
	@ViewInject(R.id.activity_ranking_province)
	private TextView tvProvince;// 省
	@ViewInject(R.id.activity_ranking_current_ranking)
	private TextView tvCurrentRanking;// 我的排名
	@ViewInject(R.id.activity_ranking_current_point)
	private TextView tvPoint;// 我的排名
	@ViewInject(R.id.activity_ranking_head)
	private ImageView imgHead;// 我的头像
	@ViewInject(R.id.activity_ranking_frame)
	private RelativeLayout rlFrame;// 底部我的排名
	@ViewInject(R.id.activity_ranking_area)
	private LinearLayout ll;// 区域布局
	@ViewInject(R.id.ll_has_ranking)
	private LinearLayout llRanking;// 排名布局
	@ViewInject(R.id.rl_no_ranking)
	private RelativeLayout rlNoRanking;// 无排名布局
	private RankingActivity activity;
	private Animation downScrollAnimation;// 下滑时的动画
	private Animation upScrollAnimation;// 上滑的动画
	public boolean isMyRankingShow = true;//个人排名数据是否显示
	private ArrayList<String> list;// 运动标签集合
	private FragmentPagerAdapter adapter;
	private int area;// 区域ID 可以为省/市/区 ID 为0时为全国; type=1 时使用
	private static int type = 1;// 选择的筛选类型(1: 区域 2:本校)
	private int school_id = 0;// 学校ID type = 2 时使用
	private boolean hasRanking = false;
	private String[] item = new String[] { "羽毛球", "篮球", "乒乓球", "足球", "排球", "网球", "高尔夫", "保龄球", "台球", "门球", "自行车", "游泳", "帆船", "射箭", "手球", "棒球", "击剑", "曲棍球", "马术", "跆拳道", "健身",
			"溜冰", "瑜伽" };
	private ArrayList<RankingFragment> fragmentList; // fragment集合
	private ArrayList<onFragmentContentRefresh> listenerList;// 为每个fragment设置监听器的集合
	private int currentPage = 0;// 当前页面的页数
	private static final int REFRESHING = 0;// 刷新中的标记
	private static final int REFRESHING_COMPLETE = 1;// 刷新完成的标记
	private static String province = "";// 省
	private static String city = "";// 市
	private static String district = "";// 区
	private static int province_code = 0;// 省码
	private static int city_code = 0;// 市码
	private static int district_code = 0;// 区码
	private static final int REQUESTCODE = 2500;
	
	private SharedPreferences preferences;
	
	private List<Cityinfo> province_list = new ArrayList<Cityinfo>();
	private List<String> city_list = new ArrayList<>();
	private List<String> county_list = new ArrayList<>();
	private HashMap<String, List<Cityinfo>> city_map = new HashMap<String, List<Cityinfo>>();
	private HashMap<String, List<Cityinfo>> couny_map = new HashMap<String, List<Cityinfo>>();
	
	private CitycodeUtil citycodeUtil;
	
	private int province_id;
	private int city_id;
	private int county_id;
	private String province_name;
	private String city_name;
	private String county_name;

	public RankingActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		preferences = MyApplication.preferences;
		tvProvince.setTextColor(getResources().getColor(R.color.black));
		initView();
		getaddressinfo();
	}

	/** (非 Javadoc) 
	* Title: onResume
	* Description:
	* @see android.support.v4.app.FragmentActivity#onResume()
	*/ 
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
		indicator.notifyDataSetChanged();
		adapter.notifyDataSetChanged();
	}
	/**
	 * @Title: initView
	 * @Description: 1.设置运动标签 2.初始化省市区和对应的地区码 3.设置头像
	 * @param
	 * @return void
	 * @throws
	 */
	private void initView() {
		list = new ArrayList<String>();
		fragmentList = new ArrayList<RankingFragment>();
		listenerList = new ArrayList<onFragmentContentRefresh>();
		initSportTag();
		// 初始化地区号 设置城市栏目  开始---------------------
		province = MyApplication.preferences.getString("province_name", "广东");
		city = MyApplication.preferences.getString("city_name", "深圳");
		district = MyApplication.preferences.getString("district_name", "南山区");
		province_code = MyApplication.preferences.getInt("province_code", 44);
		city_code = MyApplication.preferences.getInt("city_code", 4403);
		district_code = MyApplication.preferences.getInt("district_code", 440305);
		setCityData(district, city, province);
		area = MyApplication.preferences.getInt("province_code", 440305);// 默认地区号
		// 初始化地区号 设置城市栏目  结束
		tvTitle.setText("排名");// 设置标题
		tvAddress.setText(district);
		type = 1;
		upScrollAnimation = AnimationUtils.loadAnimation(activity, R.anim.listview_up_scroll_animation);// 初始化动画
		downScrollAnimation = AnimationUtils.loadAnimation(activity, R.anim.listview_down_scroll_animation);// 初始化动画

		isMyRankingShow = false;
		rlFrame.startAnimation(upScrollAnimation);
		
		adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
		// 如果我们要对ViewPager设置监听，用indicator设置就行了
		setCurrentRankingAndPoint();
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (!isMyRankingShow) {
					rlFrame.startAnimation(downScrollAnimation);
					isMyRankingShow = true;
				}
				currentPage = arg0;
				if (listenerList.get(currentPage) != null) {
					listenerList.get(currentPage).onRefreshed(area, type, school_id, getSportTag(list.get(arg0)));
				}
				setCurrentRankingAndPoint();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		//获取用户的个人头像
		String path = FileUtils.getUserHeadImgPath();
		if (path!=null) {
			MyApplication.bitmapUtils.display(imgHead, path);
		}else{
			if (!"".equals(MyApplication.preferences.getString("user_logo_200", ""))) {
				MyApplication.bitmapUtils.display(imgHead, MyApplication.preferences.getString("user_logo_200", ""));
			}else{
				imgHead.setImageResource(R.drawable.common_users_icon_default);
			}
		}
		
		province_id = preferences.getInt("province_code", 0);
		city_id = preferences.getInt("city_code", 0);
		county_id = preferences.getInt("district_code", 0);
		city_name = preferences.getString("city_name", "");
		county_name = preferences.getString("district_name", "");
	}
	
	// 获取城市信息
		private void getaddressinfo(){
			Editor editor = MyApplication.preferences.edit();
			citycodeUtil = CitycodeUtil.getSingleton();
			JSONParser parser = new JSONParser();
			String area_str = FileUtil.readAssets(this, "area.json");
			province_list = parser.getJSONParserResult(area_str, "area0");
			city_map = parser.getJSONParserResultArray(area_str, "area1");
			couny_map = parser.getJSONParserResultArray(area_str, "area2");
			citycodeUtil.getProvince(province_list);
			for(int i = 0 ; i < province_list.size() ; i++){
				if(province_list.get(i).getId().equals(String.valueOf(province_id))){
					editor.putInt("ProvincePosition", i);
					editor.putInt("ProvincePositionRanking", i);
					city_list = citycodeUtil.getCity(city_map, citycodeUtil.getProvince_list_code().get(i));
				}
			}
			if(province_id != 0){
				for(int i = 0 ; i < city_list.size() ; i++){
					if(city_list.get(i).equals(city_name)){
						editor.putInt("CityPosition", i);
						editor.putInt("CityPositionRanking", i);
						county_list = citycodeUtil.getCouny(couny_map, citycodeUtil.getCity_list_code().get(i));
					}
				}
				for(int i = 0 ; i < county_list.size() ; i++){
					if(county_list.get(i).equals(county_name)){
						editor.putInt("DistrictPosition", i);
						editor.putInt("DistrictPositionRanking", i);
					}
				}
			}
			editor.commit();
		}

	/** 
	* @Title: initSportTag
	* @Description: 初始化用户参与过的运动项目
	* @param 
	* @return void
	* @throws 
	*/ 
	private void initSportTag() {
		list.clear();
		// 从sharepreference中获取运动标签集合 开始---------------------
		Set<String> set = MyApplication.preferences.getStringSet("sport_tag", new HashSet<String>());
		Iterator<String> it = set.iterator();
		String tag = "";
		if (it.hasNext()) {// 集合中有数据
			if ("none".equals((tag = it.next()))) {// 第一个数据为none的话,则显示无排名的界面
				rlNoRanking.setVisibility(View.VISIBLE);
			} else {// 且第一个数据不为none
				list.add(tag);//在list的第一位置添加第一个运动项目
				MyApplication.map.put(tag, new Object[] { 0, 0, false });
				hasRanking = true;
				rlNoRanking.setVisibility(View.INVISIBLE);
				tvAddress.setVisibility(View.VISIBLE);
				llRanking.setVisibility(View.VISIBLE);
			}
		}
		while (it.hasNext()) {//如果iterator还有其他运动项目
			String mTag = "";
			list.add(mTag = it.next());
			MyApplication.map.put(mTag, new Object[] { 0, 0, false });
		}
		if (!hasRanking) {// 无运动数据则隐藏排名布局
			rlNoRanking.setVisibility(View.VISIBLE);
			llRanking.setVisibility(View.GONE);
			tvAddress.setVisibility(View.GONE);
		}
		// 运动标签集合大小不为0
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				fragmentList.add(null);
				listenerList.add(null);
			}
		}
		// 从sharepreference中获取运动标签集合 结束---------------------
	}

	/**
	 * @Title: setCurrentRankingAndPoint
	 * @Description: 设置目前排名/和战绩积分
	 * @param
	 * @return void
	 * @throws
	 */
	private void setCurrentRankingAndPoint() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (MyApplication.map.size() > 0 && list.size() > 0) {
						Object[] obj = MyApplication.map.get(list.get(pager.getCurrentItem()));// 获得当前fragment的对象数组
						if (obj != null && obj.length > 0) {
							if ((boolean) obj[2]) {
								handler.sendEmptyMessage(RankingActivity.REFRESHING_COMPLETE);// 刷新完成
								break;
							} else {
								handler.sendEmptyMessage(RankingActivity.REFRESHING);// 正在刷新
							}
						}
					}
					try {
						Thread.sleep(500);// 每隔0.5秒取一次结果
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
	}

	Handler handler = new Handler() {
		/**
		 * Title: handleMessage Description:
		 * 
		 * @param msg
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 加载数据中
			if (msg.what == RankingActivity.REFRESHING) {
				tvCurrentRanking.setText("");
				tvPoint.setText("加载数据中...");
			}
			// 刷新完毕
			if (msg.what == RankingActivity.REFRESHING_COMPLETE) {
				Object[] obj = MyApplication.map.get(list.get(pager.getCurrentItem()));// 获得当前fragment的对象数组
				if (obj != null&&obj.length>0) {
					MyApplication.bitmapUtils.display(imgHead, MyApplication.preferences.getString("user_logo_200", ""));
					tvPoint.setText(" 战绩积分:" + obj[1]);
					tvCurrentRanking.setText("目前排名:" + obj[0]);
					if(obj.length>3){
					if((int)obj[3]>4){
						rlFrame.startAnimation(upScrollAnimation);
						isMyRankingShow = false;
						}
					}
				}
			}
		}
	};

	/**
	 * @Title: setCityData
	 * @Description: 设置Textview中显示的省市区
	 * @param @param district 区
	 * @param @param city 市
	 * @param @param province 省
	 * @return void
	 * @throws
	 */
	private void setCityData(String district, String city, String province) {
		tvDistrict.setText(district);
		tvCity.setText(city);
		tvProvince.setText(province);
	}

	/**
	 * @ClassName: TabPageIndicatorAdapter
	 * @Description: ViewPager适配器
	 * @author 肖邦
	 * @date 2015-1-26 下午3:50:37
	 */
	class TabPageIndicatorAdapter extends FragmentPagerAdapter implements onListViewScrollListener, onRefreshingListener {
		public TabPageIndicatorAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// 新建一个Fragment来展示ViewPager item的内容，并传递参数
			RankingFragment fragment = new RankingFragment(RankingActivity.this);
			fragment.setOnListViewScrollListener(this);
			fragment.setOnRefreshingListener(this);
			Bundle args = new Bundle();
			// 将默认的数据传到fragment当中
			args.putInt("user_account", Integer.parseInt(MyApplication.preferences.getString("user_account", "0")));
			args.putInt("tag", getSportTag(list.get(position)));//运动项目ID
			args.putString("StringTag", list.get(position));
			args.putInt("area", area);
			args.putInt("school_id", school_id);
			args.putInt("type", type);
			args.putInt("position", position);
			fragment.setArguments(args);
			fragmentList.add(position, fragment);
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return list.get(position % list.size());
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * Description: 上滑
		 */
		@Override
		public void onUpScrolled(boolean isUpScroll) {
			if (isUpScroll && isMyRankingShow) {
				rlFrame.startAnimation(upScrollAnimation);
				isMyRankingShow = false;
			}
		}

		/**
		 * Description: 下滑
		 */
		@Override
		public void onDownScrolled(boolean isDownScroll) {
			if (isDownScroll && !isMyRankingShow) {
				rlFrame.startAnimation(downScrollAnimation);
				isMyRankingShow = true;
			}
		}

		/**
		 * (非 Javadoc) Title: onRefreshing Description:
		 * 
		 * @param isRefreshing
		 * @see com.slife.gopapa.fragment.RankingFragment.onRefreshingListener#onRefreshing(boolean)
		 */
		@Override
		public void onRefreshing() {
			setCurrentRankingAndPoint();
		}
	}

	@OnClick(R.id.common_address)
	public void onclick(View v) {
		if (v.getId() == R.id.common_address) {
			createSelectCityDialog(city_code);
		}
	}

	@OnClick({ R.id.activity_ranking_district, R.id.activity_ranking_city, R.id.activity_ranking_school, R.id.activity_ranking_province })
	public void areaOnclick(View v) {
		if (v.getId() == R.id.activity_ranking_school) {
			if (MyApplication.preferences.getBoolean("is_school", false)) {
				tvAddress.setText("学校");
				setDefaultAreaTextColorAndFlag();
				tvSchool.setTextColor(getResources().getColor(R.color.black));
				type = 2;
				school_id = Integer.valueOf(MyApplication.preferences.getString("school_id", ""));
				refresh();
			} else {
				Toast.makeText(activity, "您当前还没有校园信息,请在个人信息中完善.", Toast.LENGTH_LONG).show();
			}
		}
		if (v.getId() == R.id.activity_ranking_district) {
			tvAddress.setText(district);
			setDefaultAreaTextColorAndFlag();
			tvDistrict.setTextColor(getResources().getColor(R.color.black));
			area = district_code;
			type = 1;
			school_id = 0;
			refresh();
		}
		if (v.getId() == R.id.activity_ranking_city) {
			tvAddress.setText(city);
			setDefaultAreaTextColorAndFlag();
			tvCity.setTextColor(getResources().getColor(R.color.black));
			area = city_code;
			type = 1;
			school_id = 0;
			refresh();
		}
		if (v.getId() == R.id.activity_ranking_province) {
			tvAddress.setText(province);
			setDefaultAreaTextColorAndFlag();
			tvProvince.setTextColor(getResources().getColor(R.color.black));
			area = province_code;
			type = 1;
			school_id = 0;
			refresh();
		}

	}

	void createSelectCityDialog(int code) {
		MyApplication.isRanking = 1;
		CityDialog dialog = DialogUtils.createCityDialog(activity, R.style.dialog,false);
		// 设置Dialog消失时的监听器,将选择后的参数传回来
		dialog.setOnDialogDismissListener(new OnDialogDismissListener() {

			@Override
			public void onSelected(int province_code, int city_code, int district_code, String province, String city, String district) {
				setDefaultAreaTextColorAndFlag();
				RankingActivity.district = district;
				RankingActivity.district_code = district_code;
				tvDistrict.setText(district);
				tvDistrict.setTextColor(getResources().getColor(R.color.black));
				tvAddress.setText(district);
				RankingActivity.city = city;
				RankingActivity.city_code = city_code;
				tvCity.setText(city);
				RankingActivity.province = province;
				RankingActivity.province_code = province_code;
				tvProvince.setText(province);
				area = district_code;
				RankingActivity.type = 1;
				school_id = 0;
				refresh();
			}
		});
		dialog.show();
	}

	/**
	 * @Description: 刷新数据
	 * @param
	 * @return void
	 * @throws
	 */
	private void refresh() {
		if (listenerList.get(currentPage) != null) {
			listenerList.get(currentPage).onRefreshed(area, type, school_id, 0);
		}
		MyApplication.map.put(list.get(currentPage), new Object[] { 0, 0, false });
		setCurrentRankingAndPoint();
	}

	/**
	 * @Title: getSportTag
	 * @Description: 获取运动标签对应的ID号
	 * @param @param tag
	 * @param @return
	 * @return int
	 * @throws
	 */
	int getSportTag(String tag) {
		int i = 1;
		for (String s : item) {
			if (s.equals(tag)) {
				return i;
			}
			i++;
		}
		return 0;
	}

	/**
	 * @Title: setDefaultAreaTextColor
	 * @Description: 将学校、地区TextColor设置成原来的颜色
	 * @param
	 * @return void
	 * @throws
	 */
	private void setDefaultAreaTextColorAndFlag() {
		int color = getResources().getColor(R.color.ranking_area_color);
		tvSchool.setTextColor(color);
		tvDistrict.setTextColor(color);
		tvCity.setTextColor(color);
		tvProvince.setTextColor(color);
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

	// 设置获取排名URL的内容
	public interface onFragmentContentRefresh {
		public void onRefreshed(int area, int type, int schoolId, int tag);
	}

	// 为每个fragment设置监听器
	public void setOnFragmentContentRefresh(int position, onFragmentContentRefresh listener) {
		this.listenerList.add(position, listener);
	}

	@Override
	protected void onDestroy() {
		activity.finish();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RankingActivity.REQUESTCODE) {
			if (resultCode == SelectCityActivity.RESULTCODE) {
				String[] areaArr = data.getStringArrayExtra("area_arr");// 获取从上一个界面传来的省市区数据
				province = areaArr[0];// 省
				city = areaArr[1];// 市
				district = areaArr[2];// 区
				setCityData(district, city, province);
				district_code = data.getIntExtra("district_code", 440305);
				city_code = data.getIntExtra("city_code", 4403);
				province_code = data.getIntExtra("province_code", 44);
				setDefaultAreaTextColorAndFlag();
				tvDistrict.setTextColor(getResources().getColor(R.color.black));
				tvAddress.setText(district);
				area = district_code;
				type = 1;
				school_id = 0;
				refresh();
			}
		}
	}
}
