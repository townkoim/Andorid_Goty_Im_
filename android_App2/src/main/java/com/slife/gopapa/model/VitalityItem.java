package com.slife.gopapa.model;

import java.io.Serializable;

/**
 * @ClassName: VitalityItem
 * @Description:  如何获取活力
 * @author 肖邦
 * @date 2015-1-26 下午5:33:07
 */
public class VitalityItem implements Serializable{
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	private static final long serialVersionUID = 1186145214804374716L;
	public String title;//名称
	public String point;//可获得的点数
	public boolean isLine;//是否有下划线

	public VitalityItem(String title, String point, boolean isLine) {
		super();
		this.title = title;
		this.point = point;
		this.isLine = isLine;
	}

	public boolean isLine() {
		return isLine;
	}

	public void setLine(boolean isLine) {
		this.isLine = isLine;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

}
