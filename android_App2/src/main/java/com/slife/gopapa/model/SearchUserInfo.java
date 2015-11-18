package com.slife.gopapa.model;

/***
* @ClassName: SearchUserInfo 
* @Description: 搜索用户的实体类 （得到用户的基本信息)
* @author 菲尔普斯
* @date 2015-1-19 下午5:06:41 
*
 */
public class SearchUserInfo {
	private String user_account; //啪啪号
	private String user_extends_account; //聊天账号
	private String user_nickname; //昵称
	private String user_logo; //头像
	private String user_sign;//用户个性签名
	private boolean is_friend; //是否为好友
	public SearchUserInfo() {
		super();
	}
	
	public String getUser_account() {
		return user_account;
	}


	public void setUser_account(String user_account) {
		this.user_account = user_account;
	}


	public String getUser_extends_account() {
		return user_extends_account;
	}

	public void setUser_extends_account(String user_extends_account) {
		this.user_extends_account = user_extends_account;
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
	public String getUser_sign() {
		return user_sign;
	}
	public void setUser_sign(String user_sign) {
		this.user_sign = user_sign;
	}
	public boolean isIs_friend() {
		return is_friend;
	}
	public void setIs_friend(boolean is_friend) {
		this.is_friend = is_friend;
	}
	
	
}
