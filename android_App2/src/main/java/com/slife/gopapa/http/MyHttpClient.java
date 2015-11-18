package com.slife.gopapa.http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.http.client.multipart.content.StringBody;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.NetWorkState;

/***
 * @ClassName: MyHttpClient
 * @Description: HttpClient上传数据跳过身份验证的客户端请求联网对象,以及请求网络数据
 * @author 菲尔普斯
 * @date 2015-1-13 下午4:51:37
 * 
 */
public class MyHttpClient {

	/***
	 * @Title: getClient
	 * @Description: 得到跳过身份验证的客户端对象
	 * @param @return
	 * @return DefaultHttpClient
	 * @throws
	 */
	public static DefaultHttpClient getClient() {
		BasicHttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("https", SSLTrustAllSocketFactory
				.getSocketFactory(), 443));

		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
				params, schReg);
		return new DefaultHttpClient(connMgr, params);
	}

	/***
	 * @Title: setEntity
	 * @Description: 提交数据到服务器(调用此方法之前，需要先对access_token进行验证为不为空)
	 * @param @param url 网址
	 * @param @param token 请求的token
	 * @param @param key 存放的参数的键
	 * @param @param value 存放的键所对应的值
	 * @param @return
	 * @return String[] 返回一个存储了返回码和返回值的数组，数组长度为2，第一位为返回的code，第二位为返回的json
	 * @throws
	 */
	public static String[] postDataToService(Context context, String url,
			String token, String[] key, String[] value, String[] fileName,
			File[] files) {
		String[] result = null;
		if (NetWorkState.checkNet(context)) {
			if (key != null && key.length > 0 && value != null
					&& value.length > 0 && key.length == value.length
					&& token != null && !"".equals(token)) {
				try {
					HttpClient client = MyHttpClient.getClient(); // 得到跳过验证的自定义HttpClient
					HttpPost post = new HttpPost(url);
					if (MyApplication.IMEI != null
							&& !"".equals(MyApplication.IMEI)) {
						post.setHeader("Cookie", "PHPSESSID="
								+ MyApplication.IMEI);
					}
					client.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
					// 添加请求参数
					MultipartEntity entity = new MultipartEntity();
					try {
						entity.addPart("access_token", new StringBody(token));
						for (int i = 0; i < key.length; i++) {
							entity.addPart(key[i], new StringBody(value[i]));
						}
					} catch (Exception e) {
//						Log.e("MyHttpClient....................",
//								"有数据为空............");
					}
					if (fileName != null && fileName.length > 0
							&& files != null && files.length > 0
							&& fileName.length == files.length) {
						for (int j = 0; j < fileName.length; j++) {
							entity.addPart(fileName[j], new FileBody(files[j]));
						}
					}
					post.setEntity(entity);
					HttpResponse response = client.execute(post); // 连接服务器
					int code = response.getStatusLine().getStatusCode(); // 获取返回码
					String json = EntityUtils.toString(response.getEntity(),
							"UTF-8"); // 获取返回数据
					result = new String[] { String.valueOf(code), json };
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;

	}

	/***
	 * @Title: getJsonFromService
	 * @Description: Get方法请求服务器数据(调用此方法之前，需要先对access_token进行验证为不为空)
	 * @param @param url 请求的url
	 * @param @return
	 * @return String[] 数组的第一位表示responseCode,第二位为json
	 * @throws
	 */
	public static String[] getJsonFromService(Context context, String url) {
		String json[] = null;
		if (NetWorkState.checkNet(context)) {
			try {
				HttpClient client = MyHttpClient.getClient(); // 得到跳过验证的自定义HttpClient
				HttpGet get = new HttpGet(url);
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
				HttpResponse response = client.execute(get);
				int code = response.getStatusLine().getStatusCode(); // 获取返回码
				String message = EntityUtils.toString(response.getEntity(),
						"UTF-8"); // 获取返回数据
				json = new String[] { String.valueOf(code), message };
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/***
	 * @Title: getJsonFromService
	 * @Description: Get方法请求服务器数据 ,2秒超时(调用此方法之前，需要先对access_token进行验证为不为空)
	 * @param @param url 请求的url
	 * @param @return
	 * @return String[] 数组的第一位表示responseCode,第二位为json
	 * @throws
	 */
	public static String[] getJsonFromService2(Context context, String url) {
		String json[] = null;
		if (NetWorkState.checkNet(context)) {
			try {
				HttpClient client = MyHttpClient.getClient(); // 得到跳过验证的自定义HttpClient
				HttpGet get = new HttpGet(url);
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
				HttpResponse response = client.execute(get);
				int code = response.getStatusLine().getStatusCode(); // 获取返回码
				String message = EntityUtils.toString(response.getEntity(),
						"UTF-8"); // 获取返回数据
				json = new String[] { String.valueOf(code), message };
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	/**
	 * 
	 * @Title: getBitmapFromUrl
	 * @说 明:从服务器获取Bitmap
	 * @参 数: @param url
	 * @参 数: @return
	 * @return Bitmap 返回类型
	 * @throws
	 */
	public static Bitmap getBitmapFromUrl(String url) {
		Bitmap bitmap = null;
		HttpClient httpClient = new DefaultHttpClient();
		// 设置超时时间
		HttpConnectionParams.setConnectionTimeout(new BasicHttpParams(),
				6 * 1000);
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				bitmap = BitmapFactory.decodeStream(entity.getContent());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
