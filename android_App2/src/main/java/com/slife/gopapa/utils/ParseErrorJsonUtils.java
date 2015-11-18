package com.slife.gopapa.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.slife.gopapa.model.ErrorMsg;

/**
 * @ClassName: ParseErrorJsonUtils
 * @Description: 解析服务器返回错误代码的工具类
 * @author 肖邦
 * @date 2015-1-26 下午5:37:02
 * 
 */
public class ParseErrorJsonUtils {
	public static ErrorMsg getErrorMsg(String[] json) {
		ErrorMsg errorMsg = null;
		try {
			JSONObject object = new JSONObject(json[1]);
			errorMsg = new ErrorMsg(object.optString("error_code"), object.optString("request"), object.optInt("api_code"), object.optString("error"));
		} catch (JSONException e) {
			errorMsg = new ErrorMsg();
			errorMsg.setError("请求错误");
		}
		return errorMsg;
	}
}
