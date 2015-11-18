package com.slife.gopapa.common;



public class APPConstants {
	public static final String APP_ID = "wxb6d8d7fbdf99e01d";
	/************URL部分************************/
	public static final String URL_COMMON_HOST_NAME="https://125.94.212.253/PaPaPublicWebService";	//公共模块主机名
	public static final String URL_COMMON_TOKEN="/oauth/access_token?app_id=100000001&app_key=2bd45723c3fc477d6633b5d26ef06fdf"; //公共模块的授权码
	
	public static  final String URL_APP2_HOST_NAME="https://125.94.212.253/PaPaWebAPP2";	//APP2的主机名
	public static final String URL_APP2_TOKEN="/oauth/access_token?app_id=100000003&app_key=2bd45723c3fc477d6633b5d26ef06fdf";//APP2的授权码
	public static final String URL_VERIFYCODE="/user/getVerifyCode";	//获取验证码的URL
	public static final String URL_USER_REGISTER="/user/register";  //用户注册
	public static final String URL_USER_LOGIN="/user/loginAPP2"; //用户登陆
	public static final String URL_RESETPASSWORD="/user/resetPwd"; //重置密码（找回密码）
	public static final String URL_EDIT_PASSWORD="/user/editPwd";//修改密码
	public static final String URL_FRIENDS_LISTSAPP2="/friend/listsAPP2";//我的好友列表
	public static final String URL_UPLOAD_PUSH_CONTENT="/push/addTaskAPP2";//添加推送任务,当推送用户啪啪号和登陆用户啪啪号不一致时回调
	public static final String URL_LOGIN_BIND="https://125.94.212.253/PaPaWebAPP2/user/loginBindAPP2";//绑定用户和推送的关系
	public static final String URL_FEEDBACK="/feedback/submitFeedback";//意见反馈
	public static final String URL_FRIENDS_LISTCONTACTSAPP2="/friend/listContactsAPP2";//联系人好友判断
	public static final String URL_FRIENDS_LISTAPPLYAPP2="/friend/listApplyAPP2"; //好友请求列表
	public static final String URL_FRIENDS_SENDAPPLYAPP2="/friend/sendApplyAPP2";//发送好友请求
	public static final String URL_FRIENDS_DISPOSEAPPLYAPP2="/friend/disposeApplyAPP2";//处理好友请求
	public static final String URL_FRIENDS_SERACHAPP2="/user/searchAPP2";//搜索好友
	public static final String URL_USER_LOGINOUTAPP2="/user/loginOutAPP2"; //登出
	public static final String URL_GETEXTEND_INFOAPP2="/user/getExtendInfoAPP2";
	public static final String URL_GETBASEINFOAPP2 = "https://125.94.212.253/PaPaWebAPP2/user/getBaseInfoAPP2?access_token=";//获取用户基本资料
	public static final String URL_GETPERSONALINFOAPP2 = "https://125.94.212.253/PaPaWebAPP2/user/getPersonalInfoAPP2?access_token=";//获取用户个人资料
	public static final String URL_SPORT_TAG = "https://125.94.212.253/PaPaPublicWebService/sportTag/lists?access_token=";//运动标签
	public static final String URL_USER_SPORT_TAG = "https://125.94.212.253/PaPaWebAPP2/ranking/listUserSportTagAPP2?access_token=";//用户标签
	public static final String URL_UPDATEINFOAPP2 = "https://125.94.212.253/PaPaWebAPP2/user/updateInfoAPP2";//修改个人资料
	public static final String URL_SENDRACEAPP2 = "https://125.94.212.253/PaPaWebAPP2/race/sendRaceAPP2";//约赛
	public static final String URL_DISPOSERACEAPP2 = "https://125.94.212.253/PaPaWebAPP2/race/disposeRaceAPP2";//约赛
	public static final String URL_SUBMITRACEAPP2 = "https://125.94.212.253/PaPaWebAPP2/race/submitRaceAPP2";//提交战绩
	public static final String URL_GETRACEINFOAPP2 = "https://125.94.212.253/PaPaWebAPP2/race/getRaceInfoAPP2?access_token=";//约赛详情
	public static final String URL_RACE = "https://125.94.212.253/PaPaWebAPP2/race/listsAPP2?access_token=";//我发起的我参加的
	
	public static final String URL_LISTUSERSPORTTAGAPP2 = "https://125.94.212.253/PaPaWebAPP2/ranking/listUserSportTagAPP2?access_token=";//用户参与过的项目
	public static final String URL_GETUSERCITYINFOAPP2 = "https://125.94.212.253/PaPaWebAPP2/ranking/getUserCityInfoAPP2?access_token=";//用户参与过的项目
	public static final String URL_ADDSHAREVITALITYAPP2 = "https://125.94.212.253/PaPaWebAPP2/aggregate/addShareVitalityAPP2";//分享增加活力
	
	/********************秦加的接口*******************/
	public static final String URL_GOTYEREGIST1="https://qplusapi.gotye.com.cn:8443/api/ImportUsers?email=papa@slife.cc&devpwd=pasport123&appkey=48377791-c431-4b0b-8372-26d0b95e7aa9&useraccount=";
	public static final String URL_GOTYEREGIST2="&userpwd=";	//注册接口
	
	public static final String URL_LISTPOWERAPP2 = "https://125.94.212.253/PaPaWebAPP2/aggregate/listPowerAPP2?access_token=";//战绩积分
	public static final String URL_RANKING = "https://125.94.212.253/PaPaWebAPP2/ranking/listsAPP2?access_token=";//排名
	public static final String URL_GETUSERVITALITYAPP2 = "https://125.94.212.253/PaPaWebAPP2/aggregate/getUserVitalityAPP2?access_token=";//活力
	public static final String URL_ISSCHOOLINFOAPP2 = "https://125.94.212.253/PaPaWebAPP2/ranking/isSchoolInfoAPP2?access_token=";//是否有学校信息
	public static final String URL_ADDBASEINFOAPP2 = "https://125.94.212.253/PaPaWebAPP2/user/addBaseInfoAPP2";//注册成功后添加个人信息
	public static final String URL_SCHOOL = "https://125.94.212.253/PaPaPublicWebService/school/lists?access_token=";//学校公共接口
	public static final String URL_PROFESSION = "https://125.94.212.253/PaPaPublicWebService/profession/lists?access_token=";//职业公共接口
	public static final String URL_CITY = "https://125.94.212.253/PaPaPublicWebService/city/lists?access_token=";//城市公共接口
	
	public static final String URL_CHECK_APP_VERSION="https://125.94.212.253/PaPaPublicWebService/appVersion/lastVersion?access_token=";
	/******************广播过滤Action*******************************/
	public static final String ACTION_NEWS_DBCHANGE="newsfragment.dbchange"; //数据库发生改变的Action
	public static final String ACTION_ONLINE_MESSAGE="online.message"; //在线的消息
	public static final String ACTION_OFFLINE_MESSAGE="offline.message"; //离线的消息
	public static final String ACTION_TRACKPOINT_CHANGE="track.point.count.change";//小红点的消息
	public static final String ACTION_NEW_FRIENDS="new.friends";  //新的朋友的小红点
	public static final String ACTION_NEW_ABOUT_MATCH="new.about.match"; //约赛消息的小红点
	public static final String ACTION_NEW_OFFICIAL_ASSISTANT="new.official.assistant";//官方助手的小红点
	
	/*******************自定消息的类型*******************************/
	public static final String RICHTEXT_ADDFRIENDS_SUCCESS="add_friends_success";//发送添加好友的请求的自定义文本
	/************常数部分************************/
	public static final String IS_IOS="-1";//是否IOS端 1表示ios  其他则为android(-1)
	
	public static final int STARTCAMERA_REQUESTCODE=0;	//打开照相机的请求码
	public static final int TAKE_PIC_REQUESTCODE=1; 	//打开图库的请求码
}
