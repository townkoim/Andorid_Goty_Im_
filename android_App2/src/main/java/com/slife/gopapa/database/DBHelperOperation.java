package com.slife.gopapa.database;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.model.ChatUserInfo;
import com.slife.gopapa.model.PushAboutMatch;
import com.slife.gopapa.model.RecentNews;
import com.slife.gopapa.utils.ListUtils;

/***
 * @ClassName: DBHelperOperation
 * @Description: 所有有关数据库操作的类都在这里面（让UI、Sqlite分离）
 * @author 菲尔普斯
 * @date 2015-2-2 上午9:46:04
 * 
 */
public class DBHelperOperation {
	/***
	 * @Title: updateRecentContact
	 * @Description: 
	 *               修改RecentContact表的一条记录（根据userInfo.getuserInfo_extend_user_account
	 *               ）
	 * @param @param userInfo 要修改的对象
	 * @param @param message 要修改的消息内容
	 * @return void
	 * @throws
	 */
	public static void updateRecentContact(ChatUserInfo userInfo, String message) {
		MyApplication.dbHelper.excuteSQL(DBConstants.UPDATE_RECENT_CONTACT_ALL,
				userInfo.getUser_nickname(), message, 0,
				userInfo.getUser_logo(),
				String.valueOf(System.currentTimeMillis()),
				userInfo.getExtend_user_account(),
				MyApplication.senderUser.getUser_account());
	}

	/**
	 * @Title: insertRecentContact
	 * @Description: 向RecentContact表插入一条数据
	 * @param @param userInfo 要插入的对象
	 * @param @param message 要插入的消息
	 * @return void
	 * @throws
	 */
	public static void insertRecentContact(ChatUserInfo userInfo, String message) {
		MyApplication.dbHelper.excuteSQL(DBConstants.INSERT_RECENT_CONTACT,
				MyApplication.senderUser.getUser_account(),
				userInfo.getUser_account(), userInfo.getExtend_user_account(),
				userInfo.getUser_nickname(), message, 0,
				userInfo.getUser_logo(),
				String.valueOf(System.currentTimeMillis()));
	}

	/**
	 * @Title: queryRecentContactByExtendAccount
	 * @Description: 从RecentContact查询一条数据存不存在
	 * @param @param extendAccount 聊天账号
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean queryRecentContactByExtendAccount(String extendAccount) {
		Cursor cursor = MyApplication.dbHelper.query(
				// 根据目标对象的聊天号，查询数据库有无此记录
				DBConstants.QUERY_RECENT_CONTACT_BY_CONTACT_EXTEND_ACCOUNT,
				extendAccount, MyApplication.senderUser.getUser_account());
		if (cursor.moveToNext()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @Title: updataRecentContactMsgCount
	 * @Description:根据聊天账号，修改此最近联系人的未读消息的条数
	 * @param @param count 消息条数
	 * @param @param extendAccount 聊天账号
	 * @return void
	 * @throws
	 */
	public static void updataRecentContactMsgCount(int count,
			String extendAccount) {
		MyApplication.dbHelper.excuteSQL(
				DBConstants.UPDATE_RECENT_CONTACT_MSGCOUNT, count,
				extendAccount, MyApplication.preferences.getString(
						DBConstants.USER_ACCOUNT, ""));
	}

	/**
	 * @Title: updateRecentContact
	 * @Description: 
	 *               根据RecentNews修改数据库的RecentContact表中一条数据，修改他的最后一条消息，消息的数量，最后操作时间
	 * @param @param news
	 * @return void
	 * @throws
	 */
	public static void updateRecentContact(RecentNews news) {
		Cursor cursor = MyApplication.dbHelper.query(
				DBConstants.QUERY_RECENT_CONTACT_BY_CONTACT_EXTEND_ACCOUNT,
				news.getUser_extend_account(),
				MyApplication.senderUser.getUser_account());
		int msgCount = 0;
		while (cursor.moveToNext()) {
			msgCount = cursor.getInt(cursor
					.getColumnIndex("user_message_count"));
		}
		MyApplication.dbHelper.excuteSQL(DBConstants.UPDATE_RECENT_CONTACT,
				news.getUser_last_message(), news.getMsgCount() + msgCount,
				String.valueOf(System.currentTimeMillis()),
				news.getUser_extend_account(),
				MyApplication.senderUser.getUser_account());
	}

	/***
	 * @Title: updataRecentContactUserInfo
	 * @Description: 根据目标对象的聊天账号和我的啪啪号来修改目标对象的用户信息（昵称和头像）
	 * @param @param nickeName 昵称
	 * @param @param userLogo 头像
	 * @param @param extendAccount 目标对象的聊天号
	 * @return void
	 * @throws
	 */
	public static void updataRecentContactUserInfo(String nickeName,
			String userLogo, String extendAccount) {
		MyApplication.dbHelper.excuteSQL(
				DBConstants.UPDATE_RECENT_CONTACT_USERINFO, nickeName,
				userLogo, extendAccount, MyApplication.preferences.getString(
						DBConstants.USER_ACCOUNT, null));
	}

	/**
	 * @Title: insertRecentContact
	 * @Description: 根据RecentNews向数据库的RecentContact表插入一条数据
	 * @param @param news
	 * @return void
	 * @throws
	 */
	public static void insertRecentContact(RecentNews news) {
		MyApplication.dbHelper
				.excuteSQL(DBConstants.INSERT_RECENT_CONTACT,
						MyApplication.senderUser.getUser_account(), null,
						news.getUser_extend_account(),
						news.getUser_nick_name(), news.getUser_last_message(),
						news.getMsgCount(), news.getUser_logo(),
						String.valueOf(System.currentTimeMillis()));
	}

	/**
	 * @Title: queryRecentContactMsgCount
	 * @Description: 查询啪啪号为myAccount的未读消息
	 * @param @param myAccount 我的啪啪号
	 * @param @return 未读消息条数
	 * @return int
	 * @throws
	 */
	public static int queryRecentContactMsgCount(String myAccount) {
		int count = 0;
		Cursor cursor = MyApplication.dbHelper.query(
		// 根据目标对象的聊天号，查询数据库有无此记录
				DBConstants.QUERY_RECENT_CONTACT_BY_MYACCOUNT, myAccount);
		while (cursor.moveToNext()) {
			count = count+ cursor.getInt(cursor.getColumnIndex("user_message_count"));
		}
		return count;
	}

	/**
	 * @Title: queryRecentContactMsgCountByExtendAccount
	 * @Description: 很据此人的聊天账号，得到这个人有几条消息未读
	 * @param @param extendAccount 聊天账号
	 * @param @return
	 * @return int 未读消息的条数
	 * @throws
	 */
	public static int queryRecentContactMsgCountByExtendAccount(
			String extendAccount) {
		int count = 0;
		Cursor cursor = MyApplication.dbHelper.query(
				// 根据目标对象的聊天号，查询数据库有无此记录
				DBConstants.QUERY_RECENT_CONTACT_BY_CONTACT_EXTEND_ACCOUNT,
				extendAccount, MyApplication.preferences.getString(
						DBConstants.USER_ACCOUNT, ""));
		while (cursor.moveToNext()) {
			count = count
					+ cursor.getInt(cursor.getColumnIndex("user_message_count"));
		}
		return count;
	}

	/***
	 * @Title: getRecentNewsData
	 * @Description: 根据登陆的之后返回的啪啪号来得到数据库最近跟我联系过的人
	 * @param @param list
	 * @param @return
	 * @return List<RecentNews>
	 * @throws
	 */
	public static List<RecentNews> getRecentNewsData() {
		List<RecentNews> list = new ArrayList<>();
		Cursor cursor = MyApplication.dbHelper.query(
				DBConstants.QUERY_RECENT_CONTACT_BY_MYACCOUNT,
				MyApplication.preferences.getString(DBConstants.USER_ACCOUNT,null));
		while (cursor.moveToNext()) {
			String user_target_account = cursor.getString(cursor
					.getColumnIndex("user_target_account"));
			String user_extend_account = cursor.getString(cursor
					.getColumnIndex("user_extend_account"));
			String user_nick_name = cursor.getString(cursor
					.getColumnIndex("user_nick_name"));
			String user_last_message = cursor.getString(cursor
					.getColumnIndex("user_last_message"));
			int uset_msg_count = cursor.getInt(cursor
					.getColumnIndex("user_message_count"));
			String user_logo = cursor.getString(cursor
					.getColumnIndex("user_logo"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			RecentNews news = new RecentNews();
			news.setUser_myacount(MyApplication.preferences.getString(
					DBConstants.USER_ACCOUNT, null));
			news.setUser_target_account(user_target_account);
			news.setUser_extend_account(user_extend_account);
			news.setUser_nick_name(user_nick_name);
			news.setUser_last_message(user_last_message);
			news.setMsgCount(uset_msg_count);
			news.setUser_logo(user_logo);
			news.setTime(time);
			list.add(news);
		}
		return ListUtils.removeRepeatData1(list);
	}
	/**
	* @Title: insertAboutMatch
	* @Description:向about_match表插入一条数据
	* @param @param userAccount  啪啪号
	* @param @param content 内容
	* @param @param riceID  约赛的ID
	* @return void
	* @throws
	 */
	public static void insertAboutMatch(String userAccount,String content,String riceID){
		MyApplication.dbHelper.excuteSQL(DBConstants.INSERT_ABOUT_MATCH, userAccount,content,riceID,System.currentTimeMillis());
	}
	/**
	* @Title: qeuryAboutMatch
	* @Description: 查询about_match的所有数据当user_account为userAccount的时候
	* @param @param userAccount  要查询的啪啪号
	* @param @return  
	* @return List<PushAboutMatch>   返回数据集合
	* @throws
	 */
	public static List<PushAboutMatch> qeuryAboutMatch(String userAccount){
		 List<PushAboutMatch> list = new ArrayList<>();
		 if(userAccount!=null&&!"".equals(userAccount)){
		 Cursor cursor = MyApplication.dbHelper.query(DBConstants.QUERY_ABOUT_MATCH_BY_ACCOUNT, userAccount);
		 while(cursor.moveToNext()){
			 String user_account=cursor.getString(cursor.getColumnIndex("user_account"));
			 String content = cursor.getString(cursor.getColumnIndex("content"));
			 String riceID=cursor.getString(cursor.getColumnIndex("race_id"));
			 PushAboutMatch paMath = new PushAboutMatch(user_account,content,riceID);
			 list.add(paMath);
		 	}
		 }
		 return list;
	}
	
}
