package com.slife.gopapa.task;

import org.json.JSONObject;

import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.http.MyHttpClient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @ClassName: CheckVersionTask
 * @Description: 检测APP的版本更新
 * @author 菲尔普斯
 * @date 2015-2-27 下午2:40:42
 * 
 */
public class CheckVersionTask extends AsyncTask<Void, Void, String[]> {
	private Context context;
	private ImageView img = null;
	private Animation animation = null; // 更新版本的时候旋转的动画

	public CheckVersionTask(Context context) {
		super();
		this.context = context;
	}

	public CheckVersionTask(Context context, ImageView img) {
		super();
		this.context = context;
		this.img = img;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (img != null) {
			animation = AnimationUtils.loadAnimation(context,
					R.anim.update_new_version_animation);
			LinearInterpolator lin = new LinearInterpolator();
			animation.setInterpolator(lin);
			img.startAnimation(animation);
		}
	}

	@Override
	protected String[] doInBackground(Void... params) {
		return MyHttpClient.getJsonFromService(context,
				APPConstants.URL_CHECK_APP_VERSION + MyApplication.commonToken
						+ "&version_type=2&facility_type=1");
	}

	@Override
	protected void onPostExecute(String[] result) {
		super.onPostExecute(result);
		if (img != null) {
			img.clearAnimation();
			animation.cancel();
		}
		JsonObjectErrorDaoImpl.resolveJson(context, result,
				new JsonObjectErrorDao() {

					@Override
					public void disposeJsonObj(JSONObject obj) {
						try {
							PackageInfo pi = context.getPackageManager()
									.getPackageInfo(context.getPackageName(), 0);
							double newVersion = Double.valueOf(obj.optString("version_code"));
							if (newVersion> Double.valueOf(pi.versionCode)) {
								showDialog("版本更新",
										"发现新版本" + obj.optString("version_name")
												+ "\n是否立即下载?",
										obj.optString("dowload_url"));
							}else{
								if(img!=null){
									Toast.makeText(context, "已经是最新版本了", Toast.LENGTH_SHORT).show();
								}
							}
						} catch (NameNotFoundException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * @Title: showDialog
	 * @Description: 显示有新版本的弹框
	 * @param @param title 标题
	 * @param @param message 内容
	 * @param @param downUrl 下载地址
	 * @return void
	 * @throws
	 */
	private void showDialog(String title, String message, String downUrl) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message);
		setPositiveButton(builder);
		setNegativeButton(builder, downUrl);
		builder.show();
	}

	/**
	 * @Title: setPositiveButton
	 * @Description: 弹框取消按钮事件
	 * @param @param builder
	 * @param @return
	 * @return AlertDialog.Builder
	 * @throws
	 */
	private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {
		return builder.setPositiveButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
	}

	/**
	 * @Title: setNegativeButton
	 * @Description: 弹框确定按钮事件
	 * @param @param builder
	 * @param @param downUrl
	 * @param @return
	 * @return AlertDialog.Builder
	 * @throws
	 */
	private AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder,
			final String downUrl) {
		return builder.setNegativeButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						Uri content_url = Uri.parse(downUrl);
						intent.setData(content_url);
						intent.setClassName("com.android.browser",
								"com.android.browser.BrowserActivity");
						context.startActivity(intent);

					}
				});
	}

}
