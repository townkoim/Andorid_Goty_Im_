package com.slife.gopapa.dao.impl;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.http.AccessToken;

/***
 * @ClassName: JsonErrorDaoImpl
 * @Description:处理JSONObject的错误信息，正确信息会在JsonObjectErrorDao中进行回调
 * @author 菲尔普斯
 * @date 2015-1-15 上午10:27:10
 * 
 */
public class JsonObjectErrorDaoImpl {
	/***
	 * @Title: skipUI
	 * @Description: 处理Login包下，异步任务onPostExecute相同的事情，利用LoginDao接口回调去处理不同的事情，
	 *               当在LoginDao接口里面处理业务逻辑的时候 说明获取到的数据是成功的且正确的
	 * 
	 * @param @param context 上下文对象
	 * @param @param result 包含responseCode和json的数组
	 * @param @param dao LoginDao接口
	 * @return void
	 * @throws
	 */
	public static void resolveJson(Context context,String[] result,
			JsonObjectErrorDao dao) {
		String errorMsg = null; // 错误信息
		String errorCode = null; // 错误码
		String error = null;// 请求接口成功，但是json返回的是正确的还是错误
		if (result != null && result.length > 0) {
			try {
				JSONObject obj = new JSONObject(result[1]);
					error = obj.optString("error_code");
					errorMsg = obj.optString("error");
					errorCode = obj.optString("api_code");
				if (Integer.valueOf(result[0]) == 200) { // 代表获取Json成功
					if (error == null || "".equals(error)) {// 代表登陆成功，取不到错误信息
						dao.disposeJsonObj(obj);
					} else{
						Toast.makeText(context,errorMsg,Toast.LENGTH_SHORT).show();
					}
				} else {
					// 如果返回的错误消息，提示用户
					if (error != null && !"".equals(errorMsg)) {
						if(error.equals("401")||errorCode.equals("40101")){
							AccessToken.getCommonToken(context);
							AccessToken.getAPP2Token();
						}else{
							Toast.makeText(context,errorMsg,Toast.LENGTH_SHORT).show();
						}
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
