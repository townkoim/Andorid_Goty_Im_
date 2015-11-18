package com.slife.gopapa.activity.news;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gotye.api.GotyeProgressListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.model.ChatImageMessage;
import com.slife.gopapa.model.MyChatMessage;
import com.slife.gopapa.utils.FileUtils;

/***
 * @ClassName: ImageDetailActivity
 * @Description: 图片详情界面
 * @author 菲尔普斯
 * @date 2015-3-5 下午4:33:11
 * 
 */
@ContentView(R.layout.activity_imagedetail)
public class ImageDetailActivity extends BaseActivity implements
		GotyeProgressListener {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;
	@ViewInject(R.id.imagedetail_img)
	private ImageView img;
	private MyChatMessage myMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		tvTitle.setText("图片详情");
		myMsg = (MyChatMessage) getIntent().getSerializableExtra("chatMessage");
		downImg();
		imgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageDetailActivity.this.finish();
			}
		});
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageDetailActivity.this.finish();
			}
		});
	}

	/**
	 * @Title: downImg
	 * @Description: 下载图片,先从内存去找此图片，找不到的再去下载
	 * @param
	 * @return void
	 * @throws
	 */
	private void downImg() {
		Bitmap bitmap = BitmapFactory.decodeFile(FileUtils.getImgPath(String
				.valueOf(myMsg.getCreareTime())));
		if (bitmap != null) { // 先判断图片是否在SD卡存在
			img.setImageBitmap(bitmap);
		} else {
			try {
				MyApplication.gotyeApi.downloadRes(((ChatImageMessage) myMsg)
						.getDownLoadUrl(), FileUtils.getImgPath(String
						.valueOf(myMsg.getCreareTime())), this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDownloadRes(String arg0, String arg1, String arg2,
			String arg3, int arg4) {
		// 发送handler去改变UI
		handler.sendEmptyMessage(0);
	}

	@Override
	public void onProgressUpdate(String arg0, String arg1, String arg2,
			String arg3, long arg4, long arg5, byte[] arg6, int arg7, int arg8) {

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bitmap bitmap = BitmapFactory.decodeFile(FileUtils
					.getImgPath(String.valueOf(myMsg.getCreareTime())));
			img.setImageBitmap(bitmap);
		};

	};
}
