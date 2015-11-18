package com.slife.gopapa.model;

import java.io.Serializable;

/**
* @ClassName: MyFriends 
* @Description: 我的好友列表的实体类(聊天界面也是用的这个实体类)
* @author 菲尔普斯
* @date 2015-1-16 上午9:58:25 
*
 */
public class ChatUserInfo implements Serializable{
	@Override
	public String toString() {
		return "ChatUserInfo [user_account=" + user_account
				+ ", extend_user_account=" + extend_user_account
				+ ", user_nickname=" + user_nickname + ", user_logo="
				+ user_logo + "]";
	}

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	private static final long serialVersionUID = 3804985653459791724L;
	private String user_account; //好友的啪啪号
	private String extend_user_account; //好友的聊天账号
	private String user_nickname; //好友的昵称
	private String user_logo; //好友的头像url
	
	public ChatUserInfo() {
		super();
	}
	
	

	public ChatUserInfo(String extend_user_account, String user_nickname,
			String user_logo) {
		super();
		this.extend_user_account = extend_user_account;
		this.user_nickname = user_nickname;
		this.user_logo = user_logo;
	}



	public ChatUserInfo(String user_account, String extend_user_account,
			String user_nickname, String user_logo) {
		super();
		this.user_account = user_account;
		this.extend_user_account = extend_user_account;
		this.user_nickname = user_nickname;
		this.user_logo = user_logo;
	}



	public String getUser_account() {
		return user_account;
	}

	public void setUser_account(String user_account) {
		this.user_account = user_account;
	}

	public String getExtend_user_account() {
		return extend_user_account;
	}

	public void setExtend_user_account(String extend_user_account) {
		this.extend_user_account = extend_user_account;
	}

	public String getUser_nickname() {
		return user_nickname;
	}

	public void setUser_nickname(String user_nickname) {
		this.user_nickname = user_nickname;
	}

	public String getUser_logo() {
		return user_logo;
	}

	public void setUser_logo(String user_logo) {
		this.user_logo = user_logo;
	}
	
	

	
}
