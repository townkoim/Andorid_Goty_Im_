package com.slife.gopapa.model;

import java.io.Serializable;


/**
 * 
 * @ClassName: SportTag
 * @Description:运动标签
 * @author 肖邦
 * @date 2015-1-22 下午4:47:31
 * 
 */
public class SportTag implements Serializable{
	private static final long serialVersionUID = -6601108335916353403L;
	public String tag_id;//运动项目ID
	public String tag_name;//运动项目

	public String getTag_id() {
		return tag_id;
	}

	public void setTag_id(String tag_id) {
		this.tag_id = tag_id;
	}

	public String getTag_name() {
		return tag_name;
	}

	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}

}
