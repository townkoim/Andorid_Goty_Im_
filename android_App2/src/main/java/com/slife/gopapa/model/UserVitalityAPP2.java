/**
 * 
 */
package com.slife.gopapa.model;

import java.io.Serializable;

/**
 * @ClassName: UserVitalityAPP2
 * @Description: 用户活力
 * @author 肖邦
 * @date 2015-1-26 上午11:04:51
 */
public class UserVitalityAPP2 implements Serializable{
	private static final long serialVersionUID = 9211014388746915649L;
	public int vitality_num;//活力积分
	public String vitality_name;//活力名称
	public int getVitality_num() {
		return vitality_num;
	}
	public void setVitality_num(int vitality_num) {
		this.vitality_num = vitality_num;
	}
	public String getVitality_name() {
		return vitality_name;
	}
	public void setVitality_name(String vitality_name) {
		this.vitality_name = vitality_name;
	}
	
}
