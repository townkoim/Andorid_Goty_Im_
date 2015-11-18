package com.slife.gopapa.model;

import java.io.Serializable;

/**
 * @ClassName: City
 * @Description: 城市信息
 * @author 肖邦
 * @date 2015-1-26 下午5:29:44
 */
public class City implements Serializable {
	private static final long serialVersionUID = -2654936658433678486L;
	public String sc_id;// 城市id
	public String sc_name;// 城市名

	public City() {
		super();
	}

	public City(String sc_id, String sc_name) {
		super();
		this.sc_id = sc_id;
		this.sc_name = sc_name;
	}

	public String getSc_id() {
		return sc_id;
	}

	public void setSc_id(String sc_id) {
		this.sc_id = sc_id;
	}

	public String getSc_name() {
		return sc_name;
	}

	public void setSc_name(String sc_name) {
		this.sc_name = sc_name;
	}

}
