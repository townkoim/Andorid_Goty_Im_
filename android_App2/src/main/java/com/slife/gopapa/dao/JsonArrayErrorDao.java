package com.slife.gopapa.dao;

import org.json.JSONArray;
/***
* @ClassName: JsonErrorDao 
* @Description: 处理Json数组返回错误的回调
* @author 菲尔普斯
* @date 2015-1-15 上午10:20:08 
*
 */
public interface JsonArrayErrorDao {
	//处理json数组
		public void disposeJsonArray(JSONArray array);
}
