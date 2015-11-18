package com.slife.gopapa.model;

import java.io.Serializable;

/**
 * @ClassName: ListPowerAPP2
 * @Description: 积分战绩
 * @author 肖邦
 * @date 2015-1-26 下午5:32:05
 */
public class ListPowerAPP2 implements Serializable{
	public static final long serialVersionUID = 421496222866025301L;
	public String race_id;//约赛ID
	public String from_user_nikename;//约赛人昵称
	public String to_user_nikename;//赴约人昵称
	public String sport_tag_name;//运动项目
	public String race_data;//比赛时间
	public int is_challenge;//是否挑战赛
	public String race_result;//比赛结果
	public int add_power_num;//增加战力

	public ListPowerAPP2() {
		super();
	}

	
	public ListPowerAPP2(String race_id, String from_user_nikename, String to_user_nikename, String sport_tag_name, String race_data, int is_challenge, String race_result,
			int add_power_num) {
		super();
		this.race_id = race_id;
		this.from_user_nikename = from_user_nikename;
		this.to_user_nikename = to_user_nikename;
		this.sport_tag_name = sport_tag_name;
		this.race_data = race_data;
		this.is_challenge = is_challenge;
		this.race_result = race_result;
		this.add_power_num = add_power_num;
	}


	public String getRace_id() {
		return race_id;
	}

	public void setRace_id(String race_id) {
		this.race_id = race_id;
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

	public int getIs_challenge() {
		return is_challenge;
	}

	public void setIs_challenge(int is_challenge) {
		this.is_challenge = is_challenge;
	}

	public String getRace_result() {
		return race_result;
	}

	public void setRace_result(String race_result) {
		this.race_result = race_result;
	}

	public int getAdd_power_num() {
		return add_power_num;
	}

	public void setAdd_power_num(int add_power_num) {
		this.add_power_num = add_power_num;
	}
}
