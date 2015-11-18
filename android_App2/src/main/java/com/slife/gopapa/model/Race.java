package com.slife.gopapa.model;

import java.io.Serializable;

/**
 * 
 * @ClassName: Race
 * @Description: 最近战绩
 * @author 肖邦
 * @date 2015-1-21 下午2:35:11
 * 
 */
public class Race implements Serializable{
	private static final long serialVersionUID = 7732020628097169956L;
	public String race_title;//约赛主题
	public String from_user_nikename;//约赛人昵称
	public String from_user_logo_200;//约赛人头像
	public String from_user_logo_500;//约赛人头像
	public String to_user_nikename;//赴约人昵称
	public String to_user_logo_200;//赴约人头像
	public String to_user_logo_500;//赴约人头像
	public String sport_tag_name;//运动项目
	public String race_data;//约赛日期
	public String race_result;//比赛结果

	public String getRace_title() {
		return race_title;
	}

	public void setRace_title(String race_title) {
		this.race_title = race_title;
	}

	public String getFrom_user_logo_200() {
		return from_user_logo_200;
	}

	public void setFrom_user_logo_200(String from_user_logo_200) {
		this.from_user_logo_200 = from_user_logo_200;
	}

	public String getFrom_user_logo_500() {
		return from_user_logo_500;
	}

	public void setFrom_user_logo_500(String from_user_logo_500) {
		this.from_user_logo_500 = from_user_logo_500;
	}

	public String getTo_user_logo_200() {
		return to_user_logo_200;
	}

	public void setTo_user_logo_200(String to_user_logo_200) {
		this.to_user_logo_200 = to_user_logo_200;
	}

	public String getTo_user_logo_500() {
		return to_user_logo_500;
	}

	public void setTo_user_logo_500(String to_user_logo_500) {
		this.to_user_logo_500 = to_user_logo_500;
	}

	public String getSport_tag_name() {
		return sport_tag_name;
	}

	public void setSport_tag_name(String sport_tag_name) {
		this.sport_tag_name = sport_tag_name;
	}

	public String getRace_data() {
		return race_data;
	}

	public void setRace_data(String race_data) {
		this.race_data = race_data;
	}

	public String getFrom_user_nikename() {
		return from_user_nikename;
	}

	public void setFrom_user_nikename(String from_user_nikename) {
		this.from_user_nikename = from_user_nikename;
	}

	public String getTo_user_nikename() {
		return to_user_nikename;
	}

	public void setTo_user_nikename(String to_user_nikename) {
		this.to_user_nikename = to_user_nikename;
	}

	public String getRace_result() {
		return race_result;
	}

	public void setRace_result(String race_result) {
		this.race_result = race_result;
	}

}
