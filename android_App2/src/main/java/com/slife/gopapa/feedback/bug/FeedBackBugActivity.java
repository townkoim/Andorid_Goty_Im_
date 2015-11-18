package com.slife.gopapa.feedback.bug;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseNoServiceActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.NetWorkState;

/**
* @ClassName: FeedBackBugActivity 
* @Description: BUG/建议反馈，填写反馈摘要及描述，可带问题截图，手机号必填，反馈人姓名可不填
* @author 纳兹
* @date 2015-3-3 上午9:47:22 
*
*/
@ContentView(R.layout.activity_feedback_bug)
public class FeedBackBugActivity extends BaseNoServiceActivity{
	@ViewInject(R.id.common_title_back)
	private ImageView imgReturn;// 返回
	@ViewInject(R.id.common_title_name)
	private TextView txHead;// 标题
	@ViewInject(R.id.common_title_right)
	private TextView txSubmit;// 提交
	@ViewInject(R.id.cb_feedback_bug_problem)
	private CheckBox cbProblem;// 反馈问题
	@ViewInject(R.id.cb_feedback_bug_suggest)
	private CheckBox cbSuggest;// 给点建议
	@ViewInject(R.id.tx_feedback_bug_title)
	private TextView txTitle;// 问题摘要
	@ViewInject(R.id.et_feedback_bug_title)
	private EditText etTitle;// 摘要编辑
	@ViewInject(R.id.tx_feedback_bug_content)
	private TextView txContent;// 建议摘要
	@ViewInject(R.id.et_feedback_bug_content)
	private EditText etContent;// 建议编辑
	@ViewInject(R.id.sp_feedback_bug_frequency)
	private Spinner spfrequency;// 问题出现频率
	@ViewInject(R.id.img_feedback_bug_one)
	private ImageView imgOne;// 第一个ImageView
	@ViewInject(R.id.img_feedback_bug_two)
	private ImageView imgTwo;// 第二个ImageView
	@ViewInject(R.id.img_feedback_bug_three)
	private ImageView imgThree;// 第三个ImageView
	@ViewInject(R.id.img_feedback_bug_one_cancel)
	private ImageView imgOneCancel;// 第一个取消按钮
	@ViewInject(R.id.img_feedback_bug_two_cancel)
	private ImageView imgTwoCancel;// 第二个取消按钮
	@ViewInject(R.id.img_feedback_bug_three_cancel)
	private ImageView imgThreeCancel;// 第三个取消按钮
	@ViewInject(R.id.et_feedback_bug_name)
	private EditText etName;// 反馈人
	@ViewInject(R.id.et_feedback_bug_mobile)
	private EditText etMobile;// 手机
	@ViewInject(R.id.ll_feedback_bug_frequency)
	private LinearLayout llFrequency;// 出现频率布局
	
	private int FIRST_CODE = 1001;// 请求码
	private int SECOND_CODE = 1002;
	private int THIRD_CODE = 1003;
	
	/*--------------- 图片路径 ------------------*/
	private String firstPath;
	private String secondPath;
	private String thirdPath;
	/*--------------- 提交文字信息字段 ------------------*/
	private String feedback_type;// 反馈类型
	private String feedback_name;// 反馈人姓名
	private String feedback_introduction;// 反馈摘要
	private String feedback_description;// 反馈描述
	private String feedback_occur_frequency;// 出现频率
	private String feedback_mobile;// 反馈手机号
	private String feedback_client_version;// 手机型号
	private String feedback_client_system;// 系统版本
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		initUI();
		getInfo();
	}

	/**
	 * 初始化界面
	 */
	private void initUI() {
		txSubmit.setText("提交");
		txHead.setText("问题/建议反馈");
		cbProblem.setChecked(true);
		etName.setText(MyApplication.preferences.getString("bug_people_name", ""));// 查询是否有姓名缓存
	}
	
	/**
	 * 获取手机信息
	 */
	private void getInfo() {
	    String mtype = android.os.Build.MODEL; // 手机型号
	    String brand= android.os.Build.BRAND;//手机品牌
	    feedback_client_system = android.os.Build.VERSION.RELEASE;// 系统版本号
	    feedback_client_version = brand + ":" +mtype;
	    Log.i("app2", "手机型号："+mtype+"手机品牌："+feedback_client_version+"系统版本号："+feedback_client_system);  
	}
	
	/**
	 * 选择图片控件打开相册，选择图片
	 * @param v
	 */
	@OnClick({R.id.img_feedback_bug_one, R.id.img_feedback_bug_two, R.id.img_feedback_bug_three})
	public void pictureOnclick(View v){
		int id = v.getId();
		if(id == R.id.img_feedback_bug_one){
			locationPicture(FIRST_CODE);
		}else if(id == R.id.img_feedback_bug_two){
			locationPicture(SECOND_CODE);
		}else if(id == R.id.img_feedback_bug_three){
			locationPicture(THIRD_CODE);
		}
	}
	
	/**
	 * 图片取消按钮点击事件
	 * @param v
	 */
	@OnClick({R.id.img_feedback_bug_one_cancel, R.id.img_feedback_bug_two_cancel, R.id.img_feedback_bug_three_cancel})
	public void cancelOnclick(View v){
		int id = v.getId();
		if(id == R.id.img_feedback_bug_one_cancel){
			toCancel(imgOne, imgOneCancel, firstPath);
		}else if(id == R.id.img_feedback_bug_two_cancel){
			toCancel(imgTwo, imgTwoCancel, secondPath);
		}else if(id == R.id.img_feedback_bug_three_cancel){
			toCancel(imgThree, imgThreeCancel, thirdPath);
		}
	}

	/**
	 * 取消图片操作
	 * @param imgPhoto 图片控件
	 * @param path 图片路径
	 * @param imgCancel 图片取消控件
	 */
	private void toCancel(ImageView imgPhoto, ImageView imgCancel, String path) {
		imgPhoto.setImageResource(R.drawable.sport_no);// 设置默认图片
		imgCancel.setVisibility(View.INVISIBLE);// 取消按钮不可见
		path = "";// 路径置空
	}
	
	/**
	 * 复选框点击事件，选择其中一个则另一个不为选中状态
	 * @param v
	 */
	@OnClick({R.id.cb_feedback_bug_problem, R.id.cb_feedback_bug_suggest})
	public void checkBokOnclick(View v){
		int id = v.getId();
		if(id == R.id.cb_feedback_bug_problem){
			txTitle.setText("问题摘要");
			txContent.setText("问题描述");
			llFrequency.setVisibility(View.VISIBLE);
			cbSuggest.setChecked(false);
		}else if(id == R.id.cb_feedback_bug_suggest){
			txTitle.setText("建议摘要");
			txContent.setText("建议描述");
			llFrequency.setVisibility(View.GONE);
			cbProblem.setChecked(false);
		}
	}
	
	/**
	 * 返回按钮及保存按钮点击事件
	 * @param v
	 */
	@OnClick({R.id.common_title_back, R.id.common_title_right})
	public void headOnclick(View v){
		if(v.getId() == R.id.common_title_back){
			finish();
		}else if(v.getId() == R.id.common_title_right){
			if(NetWorkState.checkNet(this)){// 检测网络状态
				getAllValue();// 获取提交所需数据
				if(checkRequired()){// 检查所需数据是否齐全
					PostFeedBackTask task = new PostFeedBackTask(this, feedback_type, feedback_name, feedback_introduction, feedback_description, feedback_occur_frequency, feedback_mobile, feedback_client_version, feedback_client_system, firstPath, secondPath, thirdPath);
					task.execute();
				}
			}else{
				Toast.makeText(this, "请检查网络状态", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * 打开本地相册
	 * @param requestCode
	 */
	private void locationPicture(int requestCode){
		Intent intent = new Intent();
        /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
        /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
        /* 取得相片后返回本画面 */
        startActivityForResult(intent, requestCode);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			Uri uri = data.getData();// 图片URI
            String[] proj = { MediaStore.Images.Media.DATA };  
            Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);
            File file = getPath(actualimagecursor);// 根据URI转为File
			if(file != null){
				if(requestCode == FIRST_CODE){
					MyApplication.bitmapUtils.display(imgOne, file.getPath());
					firstPath = file.getPath();
					imgOneCancel.setVisibility(View.VISIBLE);
				}else if(requestCode == SECOND_CODE){
					MyApplication.bitmapUtils.display(imgTwo, file.getPath());
					secondPath = file.getPath();
					imgTwoCancel.setVisibility(View.VISIBLE);
				}else if(requestCode == THIRD_CODE){
					MyApplication.bitmapUtils.display(imgThree, file.getPath());
					thirdPath = file.getPath();
					imgThreeCancel.setVisibility(View.VISIBLE);
				}
			}else{
				Toast.makeText(this, "图片路径有误，建议从图库或相册选择图片", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * 获得文件路径
	 * @param cursor
	 * @return
	 */
	private File getPath(Cursor cursor){
		File file = null;
		int actual_image_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
		cursor.moveToFirst();
		String img_path = cursor.getString(actual_image_column_index);
		if(img_path != null){			
			file = new File(img_path);
		}
		
		return file;
	}
	
	/**
	 * @Title: getSpinnerValue
	 * @Description: 格式化spinner,根据spinner的值，返回对应的Id
	 * @param @param spinner
	 * @param @return
	 * @return int
	 * @throws
	 */
	private int getSpinnerValue(Spinner spinner) {
		String msg = spinner.getSelectedItem().toString();
		if (msg.equals("无概率")) {
			return -1;
		} else if (msg.equals("总是出现")) {
			return 1;
		} else if (msg.equals("有时出现")) {
			return 2;
		} else if (msg.equals("随机出现")) {
			return 3;
		} else if (msg.equals("无法重现")) {
			return 4;
		}
		return 401;
	}
	
	/**
	 * 获取界面上填写的值
	 */
	private void getAllValue(){
		if(cbProblem.isChecked()){
			feedback_type = "1";
		}else{
			feedback_type = "2";
		}
		feedback_name = etName.getText().toString();
		feedback_introduction = etTitle.getText().toString();
		feedback_description = etContent.getText().toString();
		feedback_occur_frequency = String.valueOf(getSpinnerValue(spfrequency));
		feedback_mobile = String.valueOf(etMobile.getText().toString());
	}
	
	/**
	 * 检查必填项是否填写
	 * @return boolean
	 */
	private boolean checkRequired(){
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9])|(177))\\d{8}$");
        Matcher m = p.matcher(feedback_mobile);
		if(feedback_type == null || "".equals(feedback_type)){
			Toast.makeText(this, "请选择反馈类型", Toast.LENGTH_SHORT).show();
			return false;
		}else if("".equals(feedback_introduction) || feedback_introduction == null){
			Toast.makeText(this, "请填写反馈摘要", Toast.LENGTH_SHORT).show();
			return false;
		}else if("".equals(feedback_description) || feedback_description == null){
			Toast.makeText(this, "请填写反馈描述", Toast.LENGTH_SHORT).show();
			return false;
		}else if("".equals(feedback_mobile) || feedback_mobile == null){
			Toast.makeText(this, "请填写手机号", Toast.LENGTH_SHORT).show();
			return false;
		}else if(!m.matches()){
			Toast.makeText(this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
			return false;
		}else if("".equals(feedback_name) || feedback_name == null){
			Toast.makeText(this, "请填写姓名", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			return true;
		}
	}
}
