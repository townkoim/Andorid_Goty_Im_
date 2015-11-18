package com.slife.gopapa.activity.news;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.activity.competition.MyJoinOrInvitationDetailActivity;
import com.slife.gopapa.adapter.AboutMatchAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.database.DBHelperOperation;
import com.slife.gopapa.model.PushAboutMatch;
import com.slife.gopapa.utils.ListUtils;

/**
* @ClassName: AboutMatchActivity 
* @Description: 约赛界面
* @author 菲尔普斯
* @date 2015-1-30 下午4:32:35 
*
 */
@ContentView(R.layout.activity_aboutmatch)
public class AboutMatchActivity extends BaseActivity implements OnClickListener{
	@ViewInject(R.id.aboutmatch_listview)
	private ListView listView;
	@ViewInject(R.id.aboutmatch_text)
	private TextView tvJoin;
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;
	@ViewInject(R.id.common_title_name)
	private TextView txTitleName;
	private AboutMatchAdapter adapter;
	private List<PushAboutMatch> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(AboutMatchActivity.this);
		txTitleName.setText(R.string.news_about_match_news);
		imgBack.setOnClickListener(AboutMatchActivity.this);
		list = new ArrayList<>();
		adapter = new AboutMatchAdapter(AboutMatchActivity.this,list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(AboutMatchActivity.this,MyJoinOrInvitationDetailActivity.class);
				intent.putExtra("race_id", Integer.valueOf(list.get(position).getRice_id()));
				intent.putExtra("type", 3);
				startActivity(intent);
			}
		});
		list.addAll(ListUtils.removeRepeatMatch(DBHelperOperation.qeuryAboutMatch(MyApplication.preferences.getString(DBConstants.USER_ACCOUNT, null))));
		adapter.notifyDataSetChanged();
		if(list.size()>0){
			tvJoin.setVisibility(View.GONE);
		}else{
			tvJoin.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	public void onClick(View v) {
		AboutMatchActivity.this.finish();
	}
	

}
