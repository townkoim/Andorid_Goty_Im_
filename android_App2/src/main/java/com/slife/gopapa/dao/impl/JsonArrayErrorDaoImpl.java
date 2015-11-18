package com.slife.gopapa.dao.impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.slife.gopapa.dao.JsonArrayErrorDao;
import com.slife.gopapa.http.AccessToken;

/***
* @ClassName: JsonArrayErrorDaoImpl 
* @Description: 处理JSONArray的错误信息，正确信息会在JsonArrayErrorDao中进行回调
* @author 菲尔普斯
* @date 2015-2-2 上午9:45:09 
*
 */
public class JsonArrayErrorDaoImpl {
	/***
	 * @Title: resolveJson
	 * @Description: 处理Login包下，异步任务onPostExecute相同的事情，利用LoginDao接口回调去处理不同的事情
	 * @param @param context 上下文对象
	 * @param @param result 包含responseCode和json的数组
	 * @param @param dao LoginDao接口
	 * @return void
	 * @throws
	 */
	public static void resolveJson(Context context, String[] result,
			JsonArrayErrorDao dao) {
		String errorMsg = null; // 错误信息
		String errorCode = null; // 错误码
		String error = null;// 请求接口成功，但是json返回的是正确的还是错误
		if (result != null && result.length > 0) {
			try {
				try {	//try起来取得错误的信息
					JSONObject obj = new JSONObject(result[1]);
					error = obj.getString("error_code");
					errorMsg = obj.getString("error");
					errorCode = obj.getString("api_code");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (Integer.valueOf(result[0]) == 200) { // 代表获取Json成功
					if (error == null || "".equals(error)) {// 代表登陆成功，取不到错误信息
						JSONArray array = new JSONArray(result[1]);
						dao.disposeJsonArray(array);
					} else{
						Toast.makeText(context,errorMsg,Toast.LENGTH_SHORT).show();
					}
				} else {
					// 如果返回的错误消息，提示用户
					if (error != null && !"".equals(errorMsg)) {
						if(error.equals("401")||errorCode.equals("40101")){
							AccessToken.getCommonToken(context);
							AccessToken.getAPP2Token();
						}
							Toast.makeText(context,errorMsg,Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(context, "请检查网络连接是否可用", Toast.LENGTH_SHORT).show();
		}
	}

}
