package com.slife.gopapa.application;

import java.util.HashMap;
import java.util.Properties;

import android.app.Application;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.gotye.api.Gotye;
import com.gotye.api.GotyeAPI;
import com.igexin.sdk.PushManager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.slife.gopapa.R;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.CrashHandler;
import com.slife.gopapa.common.ImageLoader;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.database.DBhelper;
import com.slife.gopapa.http.AccessToken;
import com.slife.gopapa.model.ChatUserInfo;
import com.slife.gopapa.utils.FileUtils;
import com.slife.gopapa.utils.SmileyParserUtil;
import com.slife.gopapa.utils.VoiceUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/***
 * @ClassName: MyApplication
 * @Description: 自定义Application,用来初始化全局变量
 * @author 菲尔普斯
 * @date 2015-2-2 上午9:31:44
 * 
 */
public class MyApplication extends Application {
	public static SharedPreferences preferences;// SharedPreferences数据库操作类
	public static DBhelper dbHelper; // sqlite数据库操作类
	public static GotyeAPI gotyeApi = null;// 亲加api
	public static BitmapUtils bitmapUtils;// 图片缓存类
	public static ImageLoader imgLoader;// 圆角图片的缓存处理类（自己实现）
	public static HttpUtils httpUtils;// 加载网络类

	public static String commonToken = null; // 公用token
	public static String app2Token = null; // APP2的token
	public static String IMEI = null; // 设备的IMEI号
	public static boolean myLoginState = false;// 判断用户接入服务器的状态。false表示未登陆，true表示登陆
	public static boolean gotyeState = false;// 判断亲加的登陆服务器的状态 false表示未登录亲加
	public static String clientID = null; // 用于推送的CilentID号
	public static boolean pushState=true;//当前是否可以推送（clientID是否和啪啪号进行了绑定）
	public static boolean verifyUserServiceRuning = false;// 正在运行的登陆服务器的后台服务
	public static VoiceUtils voiceUtils=null;//声音播放帮助类

	public static SmileyParserUtil smileParserUtils;//聊天表情转义帮助类
	public static ChatUserInfo senderUser = null;// 聊天的发送者信息
	public static ChatUserInfo targetUser = null; // 聊天的发送目标的信息
	public static int isRegister = 1;//是否是注册时修改头像用到的标记
	public static int isRanking = 1;//是否是在Ranking界面打开地区选择器用的标记 
	public static int isModifyTextSize = 1;//因为选择城市和选择学校用的是用一个控件,用这个标记是为了区别选择CityDialog还是选择SchoolDialog,选择学校的时候滚轮字体大小默认不变
	public static int user_ranking = 0;
	public static int user_power_num = 0;
	public static String user_img = "";
	public static String currentPhotoPath = "";
	public static boolean isTabActivity = false;// 判断是否已经初始化了TabMainActivity,只有初始化之后才能收到广播。否则拉取离线消息会TabMainActivity的广播接收者会操作UI控件导致空指针异常
	public static boolean isInitNewsFragment = false;// 判断NewsFragment是否初始化了，只有初始化之后才能得到接收到广播，然后显示最近消息
	public static boolean isInitWelcomeActivity=false;
	public static int trackPointMsgCount = 0;// 用来保存取得离线消息的数量
	public static HashMap<String, Object[]> map;
	public static LocationClient mLocationClient = null;// 定位类
	public static IWXAPI api;// IWXAPI 是第三方app和微信通信的openapi接口
	public BDLocationListener myListener = new BDLocationListener() { // 定位回调

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
		}

	};

	@Override
	public void onCreate() {
		super.onCreate();
		initParams();
		initToken();
		initLocation();
	}

	/**
	 * @Title: initParams
	 * @Description:初始化全局变量
	 * @param
	 * @return void
	 * @throws
	 */
	private void initParams() {
		// 注册crashHandler
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		api = WXAPIFactory.createWXAPI(this, APPConstants.APP_ID, true);
		api.registerApp(APPConstants.APP_ID);
		
		map = new HashMap<String, Object[]>();
		// 初始化Sqlite全局变量操作类
		if (dbHelper == null) {
			dbHelper = new DBhelper(getApplicationContext());
		}
		// 初始化SharedPreferences操作类
		if (preferences == null) {
			preferences = getApplicationContext().getSharedPreferences(DBConstants.SHAREDPREFERENCES_NAME, DBConstants.SHAREDPREFERENCES_MODEL);
		}
		// 初始化个推
		PushManager.getInstance().initialize(getApplicationContext());
		clientID = preferences.getString(DBConstants.CLIENTID, "");
		// 初始化亲加API
		Properties properties = new Properties();
		properties.put(Gotye.PRO_APP_KEY, "48377791-c431-4b0b-8372-26d0b95e7aa9");
		Gotye.getInstance().init(getApplicationContext(), properties);
		// 初始化用于个推的cilentID
		// 初始化地图API
		try {
			SDKInitializer.initialize(getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}

		// 初始化BitmapUtils加载图片缓存（ImageView）
		if (bitmapUtils == null) {
			bitmapUtils = new BitmapUtils(getApplicationContext(), FileUtils.getStorageDirectory());
			MyApplication.bitmapUtils.configDefaultLoadFailedImage(R.drawable.common_users_icon_default);// 设置加载失败的头像
		}
		// 初始化ImageLoader用于加载自定义类型的view的缓存
		if (imgLoader == null) {
			imgLoader = new ImageLoader(getApplicationContext());
		}
		// 初始化请求网络HttpUtils
		if (httpUtils == null) {
			httpUtils = new HttpUtils();
			MyApplication.httpUtils.configSoTimeout(20000);
		}
		if(voiceUtils==null){
			voiceUtils=new VoiceUtils(getApplicationContext());
		}
		// 得到适配的唯一号（用于做登陆验证）
		TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
		IMEI = tm.getDeviceId();
		//注册聊天的时候表情转义类
		SmileyParserUtil.init(getApplicationContext());  
		smileParserUtils = SmileyParserUtil.getInstance();//进行初始化    
	}

	/**
	 * @Title: initToken
	 * @Description:初始化token的值
	 * @param
	 * @return void
	 * @throws
	 */
	private  void initToken() {
		commonToken = preferences.getString(DBConstants.TOKEN_COMMON, "");
		app2Token = preferences.getString(DBConstants.TOKEN_APP2, "");
		if (commonToken==null||"".equals(commonToken)) {
			AccessToken.getCommonToken(getApplicationContext());
		}
		if (app2Token==null||"".equals(app2Token)) {
			AccessToken.getAPP2Token();
		}
		
	}

	/**
	 * @Title: initLocation
	 * @Description:初始化我的位置
	 * @param
	 * @return void
	 * @throws
	 */
	private void initLocation() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener); // 添加回调监听器
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving); // 定位模式设定为省电模式
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation(); // 发起定位请求
		else {
		}
	}

}
