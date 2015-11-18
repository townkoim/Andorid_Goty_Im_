package com.slife.gopapa.model;

import java.io.Serializable;

/**
 * @ClassName: ErrorMsg
 * @Description: 错误信息
 * @author 肖邦
 * @date 2015-1-26 下午5:30:13
 */
public class ErrorMsg implements Serializable{
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	public static final long serialVersionUID = -1966914474286348239L;
	public String error_code;//错误码
	public String request;
	public int api_code;
	public String error;//错误详情

	public ErrorMsg() {
		super();
	}

	public ErrorMsg(String error_code, String request, int api_code, String error) {
		super();
		this.error_code = error_code;
		this.request = request;
		this.api_code = api_code;
		this.error = error;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public int getApi_code() {
		return api_code;
	}

	public void setApi_code(int api_code) {
		this.api_code = api_code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
