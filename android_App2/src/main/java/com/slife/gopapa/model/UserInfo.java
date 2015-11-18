package com.slife.gopapa.model;

import java.io.Serializable;

/**
 * 
* @ClassName: BaseInfo
* @Description: 获取用户基本信息(参数  user_account 用户啪啪号)
* @author 肖邦
* @date 2015-1-14 下午4:28:08
*
 */
public class UserInfo implements Serializable{
	private static final long serialVersionUID = 5414123322184872437L;
	public String user_nickname;
	public String user_sign;
	public String user_gender;
	public String user_birth;
	public String user_age;
	public String now_province_id;
	public String now_city_id;
	public String now_county_id;
	public String profession_id;
	public String pro_id;
	public String school_id;
	public String user_company;
	public String user_logo_200;
	public String user_logo_500;
	public String now_name;
	public String profession_name;
	public String school_name;
	public String extend_user_account;
	public String vitality_name;
	
	public String getPro_id() {
		return pro_id;
	}
	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}
	public String getVitality_name() {
		return vitality_name;
	}
	public void setVitality_name(String vitality_name) {
		this.vitality_name = vitality_name;
	}
	public String getExtend_user_account() {
		return extend_user_account;
	}
	public void setExtend_user_account(String extend_user_account) {
		this.extend_user_account = extend_user_account;
	}
	public String getNow_province_id() {
		return now_province_id;
	}
	public void setNow_province_id(String now_province_id) {
		this.now_province_id = now_province_id;
	}
	public String getNow_city_id() {
		return now_city_id;
	}
	public void setNow_city_id(String now_city_id) {
		this.now_city_id = now_city_id;
	}
	public String getNow_county_id() {
		return now_county_id;
	}
	public void setNow_county_id(String now_county_id) {
		this.now_county_id = now_county_id;
	}
	public String getProfession_id() {
		return profession_id;
	}
	public void setProfession_id(String profession_id) {
		this.profession_id = profession_id;
	}
	public String getSchool_id() {
		return school_id;
	}
	public void setSchool_id(String school_id) {
		this.school_id = school_id;
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
	public String getNow_name() {
		return now_name;
	}
	public void setNow_name(String now_name) {
		this.now_name = now_name;
	}
	public String getUser_nickname() {
		return user_nickname;
	}
	public void setUser_nickname(String user_nickname) {
		this.user_nickname = user_nickname;
	}
	public String getUser_sign() {
		return user_sign;
	}
	public void setUser_sign(String user_sign) {
		this.user_sign = user_sign;
	}
	public String getUser_gender() {
		return user_gender;
	}
	public void setUser_gender(String user_gender) {
		this.user_gender = user_gender;
	}
	public String getUser_birth() {
		return user_birth;
	}
	public void setUser_birth(String user_birth) {
		this.user_birth = user_birth;
	}
	public String getUser_age() {
		return user_age;
	}
	public void setUser_age(String user_age) {
		this.user_age = user_age;
	}
	public String getUser_company() {
		return user_company;
	}
	public void setUser_company(String user_company) {
		this.user_company = user_company;
	}
	public String getProfession_name() {
		return profession_name;
	}
	public void setProfession_name(String profession_name) {
		this.profession_name = profession_name;
	}
	public String getSchool_name() {
		return school_name;
	}
	public void setSchool_name(String school_name) {
		this.school_name = school_name;
	}
	
}
