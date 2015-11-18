package com.slife.gopapa.activity.news;

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

@ContentView(R.layout.activity_offcial_assistant)
public class OffcialAssistantActivity extends BaseActivity{
	 @ViewInject(R.id.common_title_back)
	private ImageView imgBack;
	 @ViewInject(R.id.common_title_name)
	private TextView tvTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		tvTitle.setText(R.string.news_offcial_assistant);
		imgBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OffcialAssistantActivity.this.finish();
			}
		});
	}
	

}
   