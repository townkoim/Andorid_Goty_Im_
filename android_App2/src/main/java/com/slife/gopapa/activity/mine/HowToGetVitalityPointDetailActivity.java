package com.slife.gopapa.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.R;

/**
 * @ClassName: HowToGetVitalityPointDetailActivity
 * @Description: 如何获得积分详情页面,原型图"成功提交战绩"、"发布邀请"、"分享战绩"、"接受邀请";
 * 				此页面只用来展示信息,无其他操作
 * @author 肖邦
 * @date 2015-1-26 上午11:21:19
 * 
 */
@ContentView(R.layout.activity_how_to_get_vitality_point_detail)
public class HowToGetVitalityPointDetailActivity extends BaseActivity {
	@ViewInject(R.id.common_title_name)
	private TextView tvCommonTitle;//标题
	@ViewInject(R.id.activity_vitality_detail_title)
	private TextView tvTitle;//获得积分的详情标题
	@ViewInject(R.id.activity_vitality_detail_tip1)
	private TextView tvTip1;//提示1
	@ViewInject(R.id.activity_vitality_detail_tip2)
	private TextView tvTip2;//提示2
	@ViewInject(R.id.activity_vitality_detail_tip3)
	private TextView tvTip3;//提示3
	@ViewInject(R.id.activity_vitality_detail_tip4)
	private TextView tvTip4;//提示4
	@ViewInject(R.id.activity_vitality_detail_tip_img)
	private LinearLayout linearLayout;//
	private HowToGetVitalityPointDetailActivity activity;//本类对象
	public static final int TYPE_PUBLISH = 1;// 发布邀请
	public static final int TYPE_ACCEPT = 2;// 接受邀请
	public static final int TYPE_SHARE = 3;// 成功提交战绩
	public static final int TYPE_SUBMIT = 4;// 推荐给好友

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		initView(getIntent().getIntExtra("type", 0));
	}

	/**
	 * @Title: initView
	 * @Description: 根据上个界面传过来的值判断显示提示的类型
	 * @param @param intExtra
	 * @return void
	 * @throws
	 */
	private void initView(int intExtra) {
		if (intExtra != 0) {
			switch (intExtra) {
			case TYPE_PUBLISH:// 发布邀请
				tvCommonTitle.setText("发布邀请");
				tvTitle.setText("发布邀请给对方获得3");
				tvTip1.setText("1、打开个人资料");
				tvTip2.setText("2、点击按钮邀约");
				tvTip3.setText("3、点击填写发布约赛的信息");
				tvTip4.setText("4、发布成功即可获得活力积分奖励");
				linearLayout.setVisibility(View.VISIBLE);
				tvTip3.setVisibility(View.VISIBLE);
				tvTip4.setVisibility(View.VISIBLE);
				break;
			case TYPE_ACCEPT:// 接受邀请
				tvCommonTitle.setText("接受邀请");
				tvTitle.setText("接受对方邀请将获得3");
				tvTip1.setText("1、从约赛消息打开约赛详情");
				tvTip2.setText("2、点击接受按钮");
				tvTip3.setText("3、接受后可获得活力积分奖励");
				tvTip3.setVisibility(View.VISIBLE);
				break;
			case TYPE_SHARE:// 分享战绩
				tvCommonTitle.setText("分享战绩");
				tvTitle.setText("分享战绩将获得3");
				tvTip1.setText("1、从我的战绩积分进入");
				tvTip2.setText("2、长按要分享的战绩后，选择要分享的平台，分享成功后即可获得活力积分");
				break;
			case TYPE_SUBMIT:// 成功提交战绩
				tvCommonTitle.setText("成功提交战绩");
				tvTitle.setText("接受对方邀请将获得5");
				tvTip1.setText("1、从约赛消息打开提交战绩");
				tvTip2.setText("2、点击提交按钮，提交后获得活力积分");
				break;
			default:
				break;
			}
		}
	}

	@OnClick(R.id.common_title_back)
	public void onclick(View v) {
		this.finish();
	}
}
