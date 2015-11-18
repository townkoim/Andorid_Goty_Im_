package com.slife.gopapa.activity.mine.setting;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
/**
* @ClassName: AboutPapaActivity 
* @Description: 关于啪啪界面
* @author 菲尔普斯
* @date 2015-3-2 上午11:27:47 
*
 */
@ContentView(R.layout.activity_aboutpapa)
public class AboutPapaActivity extends BaseActivity{
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;
	@ViewInject(R.id.common_title_name)
	private TextView tvName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(AboutPapaActivity.this);
		tvName.setText(R.string.setting_about_papa);
		imgBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AboutPapaActivity.this.finish();
			}
		});
	}

}
