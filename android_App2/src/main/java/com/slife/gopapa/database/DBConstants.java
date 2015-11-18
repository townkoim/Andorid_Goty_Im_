package com.slife.gopapa.database;

/**
 * @ClassName: DBConstants
 * @Description: 数据库常量类
 * @author 菲尔普斯
 * @date 2015-1-9 上午11:34:50
 * 
 */
public class DBConstants {
	/******************** Sharedpreferences ***************/
	public static final String SHAREDPREFERENCES_NAME = "gopapa";
	public static final int SHAREDPREFERENCES_MODEL = 0;
	/*************** 第一次进入程序 *****************************/
	public static final String ISFIRSTACCESS = "is_first_access";
	/************************ access_token部分 *******************/
	public static final String TOKEN_COMMON = "token_common"; // 公用token
	public static final String TOKEN_APP2 = "token_app2"; // APP2的token
	public static final String TOKEN_COMMON_TIME = "token_common_time"; // 公用的token的存储时间
	public static final String TOKEN_APP2_TIME = "token_app2_time"; // APP2的token存储时间
	public static final String IMEI="imei";
	public static final String CLIENTID="clientID";
	/********************** 保存用户信息的字段 ************************/
	public static final String USER_LOGIN_NAME = "user_login_name"; // 登陆的账号名
	public static final String USER_PASSWORD = "user_password";// 登陆的用户密码
	public static final String USER_EXTEND_ACCOUNT = "user_extend_account";// 用户聊天账号
	public static final String USER_ACCOUNT = "user_account";// 用户啪啪号
	public static final String USER_NICKNAME = "user_nickname";// 用户昵称
	
	/***********************消息部分*********************************/
	public static final String NEW_FRIENDS_COUNT="new_friends_count"; //新的朋友的未读消息
	public static final String NEW_ABOUT_MATCH_COUNT="new_about_match_count"; //约赛的未读消息
	public static final String NEW_OFFCIAL_ASSISTANT_COUNT="new_offcial_assistant_count";//官方助手的未读消息

	/************* 设置部分 ********************************/
	public static final String REMIND_VOICE = "remind_accept_message"; // 声音状态开关
	public static final String REMIND_VIBRATION = "remind_vibration"; // 震动状态开关
	public static final String REMIND_DISTURBING = "remind_disturbing"; // 免打扰开关

	/************Sqlite数据库操作部分********************************/
	public static final String DBNAME="gopapa";
	public static final int DB_VERSION=1;
	public static final String CREATE_TABLE_RECENT_CONTACT = "CREATE TABLE IF NOT EXISTS user_recent_contact(" +		//创建最近联系过的人的表
			"user_myacount TEXT ,"+	//我的啪啪号 
			"user_target_account TEXT,"+ //联系人的啪啪号
			"user_extend_account TEXT ," +  //联系人的聊天账号
			"user_nick_name TEXT ," +				 //联系人的昵称
			"user_last_message TEXT ," +			//联系人的最后一条消息
			"user_message_count INT,"+					//消息的条数
			"user_logo TEXT," +					//联系人的200的头像下载地址
			"time TEXT not null)";					//最后操作时间
	public static final String QUERY_RECENT_CONTACT_BY_MYACCOUNT="select * from user_recent_contact where user_myacount=? ORDER BY time desc";//得到我最近的联系人根据我的user_account
	public static final String QUERY_RECENT_CONTACT_BY_CONTACT_EXTEND_ACCOUNT="select * from user_recent_contact where user_extend_account=? and user_myacount=?";//根据联系人的聊天号以及所对应的啪啪号得到数据库信息 
	public static final String INSERT_RECENT_CONTACT="INSERT INTO user_recent_contact(user_myacount,user_target_account,user_extend_account,user_nick_name,user_last_message,user_message_count,user_logo,time)VALUES(?,?,?,?,?,?,?,?)";
	public static final String UPDATE_RECENT_CONTACT="update user_recent_contact set user_last_message=?,user_message_count=?,time=? where user_extend_account=? and user_myacount=?";
	public static final String UPDATE_RECENT_CONTACT_ALL="update user_recent_contact set user_nick_name=?,user_last_message=?,user_message_count=?,user_logo=?,time=? where user_extend_account=? and user_myacount=?";
	public static final String UPDATE_RECENT_CONTACT_MSGCOUNT="update user_recent_contact set user_message_count=? where user_extend_account=? and user_myacount=?";
	public static final String UPDATE_RECENT_CONTACT_USERINFO="update user_recent_contact set user_nick_name=?,user_logo=? where user_extend_account=? and user_myacount=?";

	//约赛的表
	public static final String CREATE_TABLE_ABOUT_MATCH="CREATE TABLE IF NOT EXISTS about_match("+
			"user_account TEXT,"+
			"content TEXT,"+
			"race_id TEXT,"+
			"time TEXT not null)";
	public static final String QUERY_ABOUT_MATCH_BY_ACCOUNT="select * from about_match where user_account=? order by time desc";
	public static final String INSERT_ABOUT_MATCH="insert into about_match(user_account,content,race_id,time)VALUES(?,?,?,?)";

}
