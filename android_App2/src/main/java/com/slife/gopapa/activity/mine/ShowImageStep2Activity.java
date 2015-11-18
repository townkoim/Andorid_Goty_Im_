package com.slife.gopapa.activity.mine;

import java.io.ByteArrayOutputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseNoServiceActivity;
import com.slife.gopapa.utils.PictureUtil;
import com.slife.gopapa.view.ClipImageLayout;

/**
 * 
 * @ClassName: ShowImageStep2Activity
 * @Description: 无原型图;
 * 				获取到图片后进行剪切制作成矩形头像
 * @author 肖邦
 * @date 2015-1-20 上午10:17:22
 * 
 */
@ContentView(R.layout.activity_show_img_step2)
public class ShowImageStep2Activity extends BaseNoServiceActivity {
	private ShowImageStep2Activity activity;//本类对象
	private ClipImageLayout mClipImageLayout;//截取矩形头像的自定义控件
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;//返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;//标题
	@ViewInject(R.id.common_title_right)
	private TextView tvRight;//确定按钮
	@ViewInject(R.id.activity_show_img_step2)
	private LinearLayout ll;//本页面的layout

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		initView(getIntent());
	}

	private void initView(Intent intent) {
		tvTitle.setText("选择头像");
		tvRight.setText("下一步");
		String img_path = "";
		if (intent != null && (img_path = intent.getStringExtra("img_path")) != null && (img_path = intent.getStringExtra("img_path")).length() > 0) {
			mClipImageLayout = new ClipImageLayout(activity, null, new BitmapDrawable(PictureUtil.getSmallBitmap(img_path)));
			android.view.ViewGroup.LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
			ll.addView(mClipImageLayout, lp);
		}
	}

	@OnClick({ R.id.common_title_back, R.id.common_title_right })
	public void onclick(View v) {
		if (v.getId() == R.id.common_title_back) {
			activity.finish();
		}
		if (v.getId() == R.id.common_title_right) {
			Bitmap bitmap = mClipImageLayout.clip();//截取图片
			//将图片转换成byte数组,传到下一个界面
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] datas = baos.toByteArray();
			Intent intent = new Intent(this, ShowHeadImgActivity.class);
			intent.putExtra("bitmap", datas);
			startActivity(intent);
			activity.finish();
		}
	}

}
