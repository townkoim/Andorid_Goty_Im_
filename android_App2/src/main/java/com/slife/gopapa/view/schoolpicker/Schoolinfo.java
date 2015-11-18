package com.slife.gopapa.view.schoolpicker;

import java.io.Serializable;

public class Schoolinfo implements Serializable {

	private String id;
	private String school_name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSchool_name() {
		return school_name;
	}

	public void setSchool_name(String city_name) {
		this.school_name = city_name;
	}

	@Override
	public String toString() {
		return "Cityinfo [id=" + id + ", city_name=" + school_name + "]";
	}

}
