package com.slife.gopapa.model;

import java.io.Serializable;

/**
* @ClassName: ContactsPerson 
* @Description: 通讯录联系人的实体类 ,以及好友请求列表的实体类
* 
* 			status	联系人状态
			-2 : 不是手机号
			-1 : 是手机但是没有注册啪啪,可邀请
			0 : 是手机且已经注册啪啪,不可添加为好友
			1 : 是手机且已经注册啪啪,可添加为好友
			2 : 是手机且已经注册啪啪,已经是好友
* @author 菲尔普斯
* @date 2015-1-4 下午2:15:48 
*
 */
public class ContactsPerson implements Serializable{
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	private static final long serialVersionUID = -8839670785761661380L;
	public String name;//中文名
	public String pinyinName;//拼音的名字
	public String phone; //电话号码
	public String status;//是不是好友（状态，默认-1，代表是还未处理）
	public String user_account;	//啪啪号
	public String extend_user_account; //聊天账号
	public String user_nickname; //用户昵称
	public String user_logo; //用户头像
	
	
	
	
	public ContactsPerson() {
		super();
	}
	public ContactsPerson(String name) {
		super();
		this.name = name;
	}
	
	public ContactsPerson(String name, String pinyinName) {
		super();
		this.name = name;
		this.pinyinName = pinyinName;
	}
	
	public ContactsPerson(String name, String pinyinName, String phone) {
		super();
		this.name = name;
		this.pinyinName = pinyinName;
		this.phone = phone;
	}
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPinyinName() {
		return pinyinName;
	}
	public void setPinyinName(String pinyinName) {
		this.pinyinName = pinyinName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
