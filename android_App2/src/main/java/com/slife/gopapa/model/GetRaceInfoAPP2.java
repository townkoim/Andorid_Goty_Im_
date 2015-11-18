/**   
* @Title: GetRaceInfoAPP2.java
* @Package com.slife.gopapa.model
* @Description: TODO(用一句话描述该文件做什么)
* @author 肖邦
* @date 2015-1-22 上午9:54:22
* @version V1.0
*/ 
package com.slife.gopapa.model;

import java.io.Serializable;

/** 
 * @ClassName: GetRaceInfoAPP2
 * @Description: 约赛详情
 * @author 肖邦
 * @date 2015-1-22 上午9:54:22
 * 
 */
public class GetRaceInfoAPP2 implements Serializable{
	public static final long serialVersionUID = -6787566236056520184L;
	public String race_title;//约赛主题
	public String sport_tag_name;//运动项目
	public String race_expenses;//付费方式
	public String race_time;//约赛时间
	public String race_address;//约赛地点
	public String longitude;//经度
	public String latitude;//纬度
	public String race_desc;//约赛详情
	public String is_challenge;//是否挑战赛
	public String from_user_race_result;//发起人比赛结果(1: 胜 2: 平 3: 负)
	public String to_user_race_result;//赴约人比赛结果(1: 胜 2: 平 3: 负)
	public String race_result;//当双方都上传了比分返回比赛结果
	public int race_status;//赛事状态（-1：已发送邀请；1：已接受邀请；2：发起人已上报结果；3：接收人已上报结果；4 : 双方都已上报结果）
	public boolean is_submit;//是否可以提交战绩
	public boolean is_sponsor;//是否是赛事发起人
	public FromUserInfo from_user_info ;//发起人用户信息 
	public ToUserInfo to_user_info;//赴约人用户信息
	
	
	public String getRace_result() {
		return race_result;
	}
	public void setRace_result(String race_result) {
		this.race_result = race_result;
	}
	public String getRace_title() {
		return race_title;
	}
	public void setRace_title(String race_title) {
		this.race_title = race_title;
	}
	public String getSport_tag_name() {
		return sport_tag_name;
	}
	public void setSport_tag_name(String sport_tag_name) {
		this.sport_tag_name = sport_tag_name;
	}
	public String getRace_expenses() {
		return race_expenses;
	}
	public void setRace_expenses(String race_expenses) {
		this.race_expenses = race_expenses;
	}
	public String getRace_time() {
		return race_time;
	}
	public void setRace_time(String race_time) {
		this.race_time = race_time;
	}
	public String getRace_address() {
		return race_address;
	}
	public void setRace_address(String race_address) {
		this.race_address = race_address;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getRace_desc() {
		return race_desc;
	}
	public void setRace_desc(String race_desc) {
		this.race_desc = race_desc;
	}
	public String getIs_challenge() {
		return is_challenge;
	}
	public void setIs_challenge(String is_challenge) {
		this.is_challenge = is_challenge;
	}
	public String getFrom_user_race_result() {
		return from_user_race_result;
	}
	public void setFrom_user_race_result(String from_user_race_result) {
		this.from_user_race_result = from_user_race_result;
	}
	public String getTo_user_race_result() {
		return to_user_race_result;
	}
	public void setTo_user_race_result(String to_user_race_result) {
		this.to_user_race_result = to_user_race_result;
	}
	public int getRace_status() {
		return race_status;
	}
	public void setRace_status(int race_status) {
		this.race_status = race_status;
	}
	public boolean isIs_submit() {
		return is_submit;
	}
	public void setIs_submit(boolean is_submit) {
		this.is_submit = is_submit;
	}
	public boolean isIs_sponsor() {
		return is_sponsor;
	}
	public void setIs_sponsor(boolean is_sponsor) {
		this.is_sponsor = is_sponsor;
	}
	public FromUserInfo getFrom_user_info() {
		return from_user_info;
	}
	public void setFrom_user_info(FromUserInfo from_user_info) {
		this.from_user_info = from_user_info;
	}
	public ToUserInfo getTo_user_info() {
		return to_user_info;
	}
	public void setTo_user_info(ToUserInfo to_user_info) {
		this.to_user_info = to_user_info;
	}
	
}
