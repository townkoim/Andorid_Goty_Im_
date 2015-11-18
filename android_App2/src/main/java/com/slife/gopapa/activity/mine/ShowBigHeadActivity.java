package com.slife.gopapa.activity.mine;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.polites.android.GestureImageView;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.utils.FileUtils;

/**
 * @ClassName: ShowBigHeadActivity
 * @Description: 无原型图; 显示大头像的界面
 * @author 肖邦
 * @date 2015-1-26 下午5:23:58
 * 
 */
@ContentView(R.layout.activity_show_big_head)
public class ShowBigHeadActivity extends BaseActivity {
	@ViewInject(R.id.activity_show_big_head_img)
	private GestureImageView imgHead;// 可放缩的头像视图控件
	private ShowBigHeadActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		Bitmap bitmap = FileUtils.getUserHeadImg();
		if (bitmap != null) {
			imgHead.setImageBitmap(bitmap);
		} else {
			new Task().execute();
		}
	}

	/**
	 * 
	 * @ClassName: Task
	 * @Description: 异步任务获取Bitmap
	 * @author 肖邦
	 * @date 2015-2-26 下午4:33:51
	 * 
	 */
	class Task extends AsyncTask<Void, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			if (!"".equals(MyApplication.preferences.getString("user_logo_500", ""))) {
				return MyHttpClient.getBitmapFromUrl(MyApplication.preferences.getString("user_logo_500", ""));
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null) {
				imgHead.setImageBitmap(result);
			} else {
				imgHead.setImageResource(R.drawable.common_users_icon_default);
			}
		}
	}
}
