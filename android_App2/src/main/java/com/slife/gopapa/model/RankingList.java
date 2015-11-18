/**
 * 
 */
package com.slife.gopapa.model;

import java.io.Serializable;

/**
 * 
 * @ClassName: RankingList
 * @Description: 排名列表
 * @author 肖邦
 * @date 2015-1-23 上午10:03:22
 * 
 */
public class RankingList implements Serializable {
	private static final long serialVersionUID = 4920475842466070237L;
	public int user_account;//啪啪号
	public String user_nickname;//昵称
	public int win_num;//胜局
	public int flat_num;//平局
	public int lose_num;//负局
	public int power_num;//战绩积分
	public int rank;//排名
	public String user_logo_200;//头像
	public String user_logo_500;//头像

	public RankingList() {
		super();
	}

	public RankingList(int user_account, String user_nickname, int win_num, int flat_num, int lose_num, int power_num, int rank, String user_logo_200, String user_logo_500) {
		super();
		this.user_account = user_account;
		this.user_nickname = user_nickname;
		this.win_num = win_num;
		this.flat_num = flat_num;
		this.lose_num = lose_num;
		this.power_num = power_num;
		this.rank = rank;
		this.user_logo_200 = user_logo_200;
		this.user_logo_500 = user_logo_500;
	}

	public int getUser_account() {
		return user_account;
	}

	public void setUser_account(int user_account) {
		this.user_account = user_account;
	}

	public String getUser_nickname() {
		return user_nickname;
	}

	public void setUser_nickname(String user_nickname) {
		this.user_nickname = user_nickname;
	}

	public int getWin_num() {
		return win_num;
	}

	public void setWin_num(int win_num) {
		this.win_num = win_num;
	}

	public int getFlat_num() {
		return flat_num;
	}

	public void setFlat_num(int flat_num) {
		this.flat_num = flat_num;
	}

	public int getLose_num() {
		return lose_num;
	}

	public void setLose_num(int lose_num) {
		this.lose_num = lose_num;
	}

	public int getPower_num() {
		return power_num;
	}

	public void setPower_num(int power_num) {
		this.power_num = power_num;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
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

}
