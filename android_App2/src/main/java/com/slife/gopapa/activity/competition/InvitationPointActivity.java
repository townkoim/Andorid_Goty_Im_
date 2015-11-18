package com.slife.gopapa.activity.competition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;

/**
 * 
 * @ClassName: InvitationPointActivity
 * @Description: 邀请比赛的第二步(填写花费的活力)//此页面暂时不做
 * @author 肖邦
 * @date 2015-1-7 下午4:51:04
 * 
 */
@ContentView(R.layout.activity_competition_invitation_point)
public class InvitationPointActivity extends BaseActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;
	@ViewInject(R.id.activity_invitation_available_point)
	private TextView tvAvailablePoint;
	@ViewInject(R.id.activity_invitation_point)
	private EditText etPoint;
	private InvitationPointActivity activity;
	private String competitionTheme;//约赛主题
	private String competitionItem;//约赛项目
	private String competitionFee;//约赛费用
	private String competitionTime;//时间
	private String competitionAddress;//地址
	private String competitionIllustration;//说明
	private String[] arr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		getBunbleData(getIntent());
		initView();
	}

	private void getBunbleData(Intent intent) {
		Bundle bundle = null;
		if (intent != null && (bundle = intent.getExtras()) != null) {
			arr = bundle.getStringArray("invitation_detail");
			//获取从上一个界面传来的值
			competitionTheme = arr[0];
			competitionItem = arr[1];
			competitionFee = arr[2];
			competitionTime = arr[3];
			competitionAddress = arr[4];
			competitionIllustration = arr[5];
		}
	}

	private void initView() {
		tvTitle.setText("愿花费的活力");
	}

	@OnClick({ R.id.common_title_back, R.id.activity_invitation_point_publish })
	public void onclick(View v) {
		if (v.getId() == R.id.common_title_back) {
			finish();
		}
		if (v.getId() == R.id.activity_invitation_point_publish) {
			if (arr != null) {
				String strvailablePoint = tvAvailablePoint.getText().toString();//挑战者可用的活力
				String strPoint = etPoint.getText().toString();//挑战者输入的活力
				if(strvailablePoint!=null&&strvailablePoint.length()>0){
					int availablePoint = Integer.parseInt(strvailablePoint);
					if(strPoint!=null&&strPoint.length()>0){
						int point = Integer.parseInt(strPoint);
						if(point>availablePoint){
							Toast.makeText(activity, "您的活力值不够", Toast.LENGTH_SHORT).show();
						}else{
							
						}
					}else{
						Toast.makeText(activity, "请输入正确的活力值", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(activity, "没有可用活力", Toast.LENGTH_SHORT).show();
				}
			}
		}

	}
}
