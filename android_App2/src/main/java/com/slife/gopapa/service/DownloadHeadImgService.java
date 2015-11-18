/**
 * 
 */
package com.slife.gopapa.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.utils.FileUtils;

public class DownloadHeadImgService extends Service {

	/**
	 * (非 Javadoc) Title: onBind Description:
	 * 
	 * @param intent
	 * @return
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		if (!"".equals(MyApplication.preferences.getString("user_logo_500", ""))) {
			try {
				new DownLoadTask().execute(MyApplication.preferences.getString("user_logo_500", ""));
			} catch (Exception e) {
			}
		}
	}

	class DownLoadTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			// 设置超时时间
			HttpConnectionParams.setConnectionTimeout(new BasicHttpParams(), 6 * 1000);
			HttpGet get = new HttpGet(params[0]);
			try {
				HttpResponse response = httpClient.execute(get);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					InputStream result = entity.getContent();
					if (result != null) {
						BufferedOutputStream bos = null;
						FileOutputStream fos = null;
						try {
							File dir = new File(FileUtils.getStorageDirectory());
							if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
								dir.mkdirs();
							}
							String user_account = MyApplication.preferences.getString("user_account", "");
							File file = new File(FileUtils.getStorageDirectory() + "/" + user_account + ".jpg");
							if (!file.exists()) {
								file.createNewFile();
							}
							fos = new FileOutputStream(file);
							bos = new BufferedOutputStream(fos);
							int len = 0;
							byte[] buffer = new byte[1204];
							while ((len = result.read(buffer)) != -1) {
								bos.write(buffer, 0, len);
							}
							MyApplication.preferences.edit().putString(user_account+"_head_file", file.getAbsolutePath()).commit();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (bos != null) {
								try {
									bos.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							if (fos != null) {
								try {
									fos.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
