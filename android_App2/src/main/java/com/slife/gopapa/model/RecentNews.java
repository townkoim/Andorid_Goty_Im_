package com.slife.gopapa.model;

import java.io.Serializable;

/***
* @ClassName: RecentNews 
* @Description:最近的消息的实体类，用做最近消息的列表的适配器的数据库
* @author 菲尔普斯
* @date 2015-2-2 上午9:56:10 
*
 */
public class RecentNews   implements Serializable,Comparable<RecentNews> {
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	private static final long serialVersionUID = 4051491881019359067L;
	private String user_myacount;	//我的啪啪号
	private String user_target_account;//目标的啪啪号
	private String user_extend_account;  //联系人的聊天账号
	private String user_nick_name;  	//联系人的昵称
	private String user_last_message;	//联系人的最后一条消息
	private String user_logo;  //联系人的头像
	private String time;      //最后操作时间
	private int msgCount; 		//未读的消息条数
	public String getUser_myacount() {
		return user_myacount;
	}
	
	
	public String getUser_target_account() {
		return user_target_account;
	}


	public void setUser_target_account(String user_target_account) {
		this.user_target_account = user_target_account;
	}


	public void setUser_myacount(String user_myacount) {
		this.user_myacount = user_myacount;
	}
	public String getUser_extend_account() {
		return user_extend_account;
	}
	public void setUser_extend_account(String user_extend_account) {
		this.user_extend_account = user_extend_account;
	}
	public String getUser_nick_name() {
		return user_nick_name;
	}
	public void setUser_nick_name(String user_nick_name) {
		this.user_nick_name = user_nick_name;
	}
	public String getUser_last_message() {
		return user_last_message;
	}
	public void setUser_last_message(String user_last_message) {
		this.user_last_message = user_last_message;
	}
	public String getUser_logo() {
		return user_logo;
	}
	public void setUser_logo(String user_logo) {
		this.user_logo = user_logo;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getMsgCount() {
		return msgCount;
	}
	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}
	
	
	@Override
	public int compareTo(RecentNews another) {
		if(Long.valueOf(this.getTime())>Long.valueOf(another.getTime())){
			return -1;
		}else{
			return 1;
		}
	}
	
	
	
}
