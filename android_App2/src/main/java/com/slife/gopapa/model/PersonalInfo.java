package com.slife.gopapa.model;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 
* @ClassName: PersonalInfo
* @Description: 获取用户个人信息(参数  user_account 用户啪啪号)
* @author 肖邦
* @date 2015-1-14 下午4:24:53
*
 */
public class PersonalInfo implements Serializable{
	private static final long serialVersionUID = 1834549168961701030L;
	public UserInfo user_info;//用户基本信息
	public int power_num;//战绩积分
	public int vitality_num;//活力值
	public String vitality_name;//活力名称
	public ArrayList<Race> race;//比赛结果
	public boolean is_friend;//是否是好友
	
	public boolean isIs_friend() {
		return is_friend;
	}

	public void setIs_friend(boolean is_friend) {
		this.is_friend = is_friend;
	}

	public String getVitality_name() {
		return vitality_name;
	}

	public void setVitality_name(String vitality_name) {
		this.vitality_name = vitality_name;
	}

	public UserInfo getUser_info() {
		return user_info;
	}

	public void setUser_info(UserInfo user_info) {
		this.user_info = user_info;
	}

	public ArrayList<Race> getRace() {
		return race;
	}

	public void setRace(ArrayList<Race> race) {
		this.race = race;
	}

	public int getPower_num() {
		return power_num;
	}

	public void setPower_num(int power_num) {
		this.power_num = power_num;
	}

	public int getVitality_num() {
		return vitality_num;
	}

	public void setVitality_num(int vitality_num) {
		this.vitality_num = vitality_num;
	}

}
