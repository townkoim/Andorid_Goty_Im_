package com.slife.gopapa.activity.mine.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.task.FeedBackTask;

/**
 * @ClassName: OpinionFeddBackActivity
 * @Description: 意见反馈界面
 * @author 菲尔普斯
 * @date 2015-1-9 下午2:20:52
 * 
 */
@ContentView(R.layout.activity_opinion_feedback)
public class OpinionFeddBackActivity extends BaseActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;
	@ViewInject(R.id.common_title_name)
	private TextView tvTitleName;
	@ViewInject(R.id.common_title_right)
	private TextView tvTitleRight;
	@ViewInject(R.id.feedback_title)
	private EditText etTitle;
	@ViewInject(R.id.feedback_title_count)
	private TextView tvTitleCount;
	@ViewInject(R.id.feedback_content)
	private EditText etContent;
	@ViewInject(R.id.feedback_content_count)
	private TextView tvContentCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		tvTitleName.setText(R.string.setting_opinion_feedback);
		tvTitleRight.setText(R.string.submit);
		// 标题文字监听器，为了不使他超过150个字；
		etTitle.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				tvTitleCount.setText(String.valueOf(temp.length()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (temp.length() > 150) {	//如果文字的长度超过了150就提示用户不能再输了
					Toast.makeText(OpinionFeddBackActivity.this,
							R.string.space_constraints, Toast.LENGTH_SHORT)
							.show();
					etTitle.setText(temp.subSequence(0, 150));
					etTitle.setSelection(etTitle.getText().toString().length());
					tvTitleCount.setText(String.valueOf(etTitle.getText()
							.toString().length()));
				}
			}
		});
		// 内容文字监听器，为了不使他超过2000个字；
		etContent.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				tvContentCount.setText(String.valueOf(temp.length()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (temp.length() > 2000) {//如果文字的长度超过了2000就提示用户不能再输了
					Toast.makeText(OpinionFeddBackActivity.this,
							R.string.space_constraints, Toast.LENGTH_SHORT)
							.show();
					etContent.setText(temp.subSequence(0, 2000));
					etContent.setSelection(etTitle.getText().toString()
							.length());
					tvContentCount.setText(String.valueOf(etTitle.getText()
							.toString().length()));
				}
			}
		});
	}

	/**
	 * @Title: titleOnclicl
	 * @Description: 标题栏监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.common_title_back, R.id.common_title_right })
	public  void titleOnclicl(View v) {
		int id = v.getId();
		if (id == R.id.common_title_back) {
			OpinionFeddBackActivity.this.finish();
		} else if (id == R.id.common_title_right) {
			String title = etTitle.getText().toString();
			String content = etContent.getText().toString();
			if (title != null && !"".equals(title) && content != null&& !"".equals(content)) {
				if (MyApplication.myLoginState&&NetWorkState.checkNet(OpinionFeddBackActivity.this)) {
					new FeedBackTask(OpinionFeddBackActivity.this, etTitle,etContent).execute();
				}
			} else {
				Toast.makeText(OpinionFeddBackActivity.this, "标题或者内容不能为空...",Toast.LENGTH_SHORT).show();
			}
		}
	}
}
