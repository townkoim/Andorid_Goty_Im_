package com.slife.gopapa.model;
/***
* @ClassName: PushAboutMatch 
* @Description: 推送约赛消息实体类
* @author 菲尔普斯
* @date 2015-2-4 下午1:51:06 
*
 */
public class PushAboutMatch {
	private String user_account;
	private String content;
	private String rice_id;
	
	
	
	public PushAboutMatch(String user_account, String content, String rice_id) {
		super();
		this.user_account = user_account;
		this.content = content;
		this.rice_id = rice_id;
	}
	public PushAboutMatch() {
		super();
	}
	public String getUser_account() {
		return user_account;
	}
	public void setUser_account(String user_account) {
		this.user_account = user_account;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRice_id() {
		return rice_id;
	}
	public void setRice_id(String rice_id) {
		this.rice_id = rice_id;
	}
	
	
	
}
