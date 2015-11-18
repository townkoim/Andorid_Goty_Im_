/**   
* @Title: ToUserInfo.java
* @Package com.slife.gopapa.model
* @Description: TODO(用一句话描述该文件做什么)
* @author 肖邦
* @date 2015-1-22 上午9:57:58
* @version V1.0
*/ 
package com.slife.gopapa.model;

import java.io.Serializable;

/** 
 * @ClassName: ToUserInfo
 * @Description: 约赛详情的接受者
 * @author 肖邦
 * @date 2015-1-22 上午9:57:58
 * 
 */
public class ToUserInfo implements Serializable{
	public static final long serialVersionUID = -2713013767559571201L;
	public int user_account; //用户啪啪号
	public String extend_user_account;//用户聊天号
	public String user_nickname;//昵称
	public String user_logo_200;//用户头像
	public String user_logo_500;//用户头像
	public int user_gender;//性别
	public String vitality_name;//活力名称
	public int getUser_account() {
		return user_account;
	}
	public void setUser_account(int user_account) {
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
	public String getUser_logo_200() {
		return user_logo_200;
	}
	public void setUser_logo_200(String user_logo_200) {
		this.user_logo_200 = user_logo_200;
	}
	public String getUser_logo_500() {
		return user_logo_500;
	}
	public void setUser_logo_500(String user_logo_500) {
		this.user_logo_500 = user_logo_500;
	}
	public int getUser_gender() {
		return user_gender;
	}
	public void setUser_gender(int user_gender) {
		this.user_gender = user_gender;
	}
	public String getVitality_name() {
		return vitality_name;
	}
	public void setVitality_name(String vitality_name) {
		this.vitality_name = vitality_name;
	}
	
}
