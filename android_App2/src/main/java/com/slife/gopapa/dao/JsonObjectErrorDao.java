package com.slife.gopapa.dao;

import org.json.JSONObject;

/***
* @ClassName: JsonErrorDao 
* @Description: 处理Json对象返回错误的回调
* @author 菲尔普斯
* @date 2015-1-15 上午10:20:08 
*
 */
public interface JsonObjectErrorDao {
	//处理json对象
	public void disposeJsonObj(JSONObject obj);
}
