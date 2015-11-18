package com.slife.gopapa.activity.mine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.activity.TabMainActivity;
import com.slife.gopapa.activity.login.SelectCityActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.http.AccessToken;
import com.slife.gopapa.service.PostPersonInfoService;
import com.slife.gopapa.utils.DialogUtils;
import com.slife.gopapa.utils.FileUtils;
import com.slife.gopapa.view.CityDialog;
import com.slife.gopapa.view.CityDialog.OnDialogDismissListener;
import com.slife.gopapa.view.DateTimePickDialog;
import com.slife.gopapa.view.SchoolDialog;
import com.slife.gopapa.view.SchoolDialog.OnDialogSelectListener2;
import com.slife.gopapa.view.schoolpicker.FileUtil;
import com.slife.gopapa.view.schoolpicker.SchoolcodeUtil;
import com.slife.gopapa.view.schoolpicker.Schoolinfo;
import com.slife.gopapa.view.schoolpicker.SchoolPicker.JSONParser;
import com.slife.gopapa.view.XCRoundRectImageView;

/**
 * 
 * @ClassName: PersonInformationActivity
 * @Description: 显示个人信息界面,原型图"个人信息-01"
 * 				
 * @author 肖邦
 * @date 2015-1-7 下午5:10:19
 * 
 */
@ContentView(R.layout.activity_mine_person_information)
public class PersonalInformationActivity extends BaseActivity {
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;// 页面标题
	@ViewInject(R.id.common_title_right)
	private TextView tvRight;// 保存
	@ViewInject(R.id.activity_person_information_nickname)
	private TextView tvNickName;// 昵称
	@ViewInject(R.id.activity_person_information_birthday)
	private TextView tvBirthday;// 生日
	@ViewInject(R.id.activity_person_information_area)
	private TextView tvArea;// 所在地
	@ViewInject(R.id.activity_person_information_signture)
	private TextView tvSignture;// 签名
	@ViewInject(R.id.activity_person_information_occupation)
	private TextView tvOccupation;// 职业
	@ViewInject(R.id.activity_person_information_company)
	private TextView tvCompany;// 公司
	@ViewInject(R.id.activity_person_information_school)
	private TextView tvSchool;// 学校
	@ViewInject(R.id.activity_person_information_sex)
	private TextView tvSex;// 学校
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;//返回键
	@ViewInject(R.id.activity_person_information_head_img)
	private XCRoundRectImageView imgHead;//用户头像
	private PersonalInformationActivity activity;//本类对象
	private SharedPreferences preferences;
	private Editor editor;
	
	private List<Schoolinfo> province_list = new ArrayList<Schoolinfo>();
	private HashMap<String, List<Schoolinfo>> school_map = new HashMap<String, List<Schoolinfo>>();
	private List<String> school_list = new ArrayList<>();
	private String school_name;
	private int province_id;
	
	private SchoolcodeUtil schoolcodeUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		preferences = MyApplication.preferences;
		editor = preferences.edit();
		initView();
		getSchoolInfo();
	}
	
	/**
	 * 
	* @Title: initView
	* @Description:  从SharedPreferences中获取加载后的信息并填充到控件中
	* @param 
	* @return void
	* @throws
	 */
	private void initView() {
		tvTitle.setText("个人信息");
		tvRight.setText("保存");
		tvRight.setVisibility(View.GONE);
		school_name = preferences.getString("school_name", "");
		province_id = preferences.getInt("basePro", 0);
		//设置用户头像
		String path = FileUtils.getUserHeadImgPath();
		if (path!=null) {
			MyApplication.bitmapUtils.display(imgHead, path);
		}else{
			if (!"".equals(MyApplication.preferences.getString("user_logo_200", ""))) {
				MyApplication.bitmapUtils.display(imgHead, preferences.getString("user_logo_200", ""));
			}else{
				imgHead.setImageResource(R.drawable.common_users_icon_default);
			}
		}
		tvNickName.setText(preferences.getString("user_nickname", ""));
		tvBirthday.setText(preferences.getString("user_birth", ""));
		tvArea.setText(preferences.getString("now_name", ""));
		tvSignture.setText(preferences.getString("user_sign", ""));
		tvOccupation.setText(preferences.getString("profession_name", ""));
		tvCompany.setText(preferences.getString("user_company", ""));
		tvSchool.setText(preferences.getString("school_name", ""));
		if("1".equals(preferences.getString("user_gender", ""))){
			tvSex.setText("男");
		}else{
			tvSex.setText("女");
		}
	}
	
	/**
	 * 获取学校信息
	 */
	private void getSchoolInfo() {
		schoolcodeUtil = SchoolcodeUtil.getSingleton();
		// 读取城市信息string
		JSONParser parser = new JSONParser();
		String area_str = FileUtil.readAssets(this, "school.json");
		province_list = parser.getJSONParserResult(area_str, "area0");
		school_map = parser.getJSONParserResultArray(area_str, "area1");
		schoolcodeUtil.getProvince(province_list);
		for(int i = 0 ; i < province_list.size() ; i++){
			if(province_list.get(i).getId().equals(String.valueOf(province_id))){
				editor.putInt("SchoolProvincePosition", i);
				school_list = schoolcodeUtil.getCity(school_map, schoolcodeUtil.getProvince_list_code().get(i));
			}
		}
		if(province_id != 0){
			for(int i = 0 ; i < school_list.size() ; i++){
				if(school_list.get(i).equals(school_name)){
					editor.putInt("SchoolPosition", i);
				}
			}
		}
		editor.commit();
	}
	
	@OnClick({ R.id.activity_person_information_rl_nickname, R.id.activity_person_information_rl_area, R.id.activity_person_information_head_img,
			R.id.activity_person_information_rl_birthday, R.id.activity_person_information_rl_company, R.id.activity_person_information_rl_occupation,
			R.id.activity_person_information_rl_occupation, R.id.activity_person_information_rl_school, R.id.activity_person_information_rl_signture, R.id.common_title_back,
			R.id.common_title_right, R.id.activity_person_information_head_rl,R.id.activity_person_information_rl_sex })
	public void layoutOnclick(View v) {
		Intent intent = new Intent(activity, ModifyPersonInformationActivity.class);
		if (v.getId() == R.id.common_title_back) {//返回
			activityFinish();
		} else if (v.getId() == R.id.activity_person_information_head_img) {//头像
			if (!"".equals(MyApplication.preferences.getString(MyApplication.preferences.getString("user_account","")+"_head_file", ""))) {
				// 如果有头像的话,就点击进入放大头像的界面
				startActivity(new Intent(activity, ShowBigHeadActivity.class));
			} else {
				// 如果没有头像,就进入选择头像的界面
				MyApplication.isRegister = 0;
				Intent intent2 = new Intent(activity, SelectHeadImgActivity.class);
				intent2.putExtra("isMine", true);// 是否是从MineActivity跳转过去的
				startActivity(intent2);
			}
		} else if (v.getId() == R.id.activity_person_information_head_rl) {//选择头像
			// 如果没有头像,就进入选择头像的界面
			MyApplication.isRegister = 0;
			Intent intent2 = new Intent(activity, SelectHeadImgActivity.class);
			intent2.putExtra("isMine", true);// 是否是从MineActivity跳转过去的
			startActivity(intent2);
		} else if (v.getId() == R.id.activity_person_information_rl_nickname) {// 修改昵称
			intent.putExtra("oldInformation", tvNickName.getText().toString());
			intent.putExtra("modifyType", ModifyPersonInformationActivity.NICK_NAME);
			startActivityForResult(intent, ModifyPersonInformationActivity.RESULTCODE);
		} else if (v.getId() == R.id.activity_person_information_rl_birthday) {// 修改生日
			// 弹出自定义时间选择器
			DateTimePickDialog dateTimePicKDialog = new DateTimePickDialog(activity, tvBirthday.getText().toString());
			dateTimePicKDialog.dateTimePicKDialog(tvBirthday);
		} else if (v.getId() == R.id.activity_person_information_rl_area) {// 修改地区
			// 用户所在地
			MyApplication.isRanking = 0;
			CityDialog dialog = DialogUtils.createCityDialog(activity,R.style.dialog, true);
			// 设置Dialog消失时的监听器
			dialog.setOnDialogDismissListener(new OnDialogDismissListener() {

				@Override
				public void onSelected(int province_code, int city_code, int district_code, String province, String city, String district) {
					Editor editor = MyApplication.preferences.edit();
					editor.putInt("province_code", province_code);
					editor.putInt("city_code", city_code);
					editor.putInt("district_code", district_code);
					editor.putString("province_name",province);
					editor.putString("city_name",city);
					editor.putString("district_name",district);
					editor.putString("now_name", province + "-" + city + "-" + district);
					tvArea.setText(province + "-" + city + "-" + district);
					editor.commit();
				}
			});
			dialog.show();
		} else if (v.getId() == R.id.activity_person_information_rl_signture) {// 修改个性签名
			intent.putExtra("oldInformation", tvSignture.getText().toString());
			intent.putExtra("modifyType", ModifyPersonInformationActivity.SIGNTURE);
			startActivityForResult(intent, ModifyPersonInformationActivity.RESULTCODE);
		} else if (v.getId() == R.id.activity_person_information_rl_occupation) {// 修改职业
			intent.putExtra("modifyType", ModifyPersonInformationActivity.OCCUPATION);
			startActivityForResult(intent, ModifyPersonInformationActivity.RESULTCODE);
		} else if (v.getId() == R.id.activity_person_information_rl_company) {// 修改公司
			intent.putExtra("oldInformation", tvCompany.getText().toString());
			intent.putExtra("modifyType", ModifyPersonInformationActivity.COMPANY);
			startActivityForResult(intent, ModifyPersonInformationActivity.RESULTCODE);
		} else if (v.getId() == R.id.activity_person_information_rl_school) {// 修改学校
			MyApplication.isModifyTextSize=0;
			SchoolDialog dialog = DialogUtils.createSchoolDialog(activity, R.style.dialog);
			dialog.setOnDialogDismissListener(new OnDialogSelectListener2() {
				
				@Override
				public void onSelected(int province_code, int city_code_, String province, String city) {
					editor.putInt("basePro", province_code+1);
					editor.putInt("baseSchool", city_code_);
					editor.putString("school_name", city);
					editor.putString("school_id", String.valueOf(city_code_));
					tvSchool.setText(city);
					editor.commit();
					MyApplication.preferences.edit().putBoolean("is_school", true).commit();// 保存到手机
				}
			});
			dialog.show();
		} else if (v.getId() == R.id.activity_person_information_rl_sex) {//性别
			Toast.makeText(activity, "性别不能更改", Toast.LENGTH_SHORT).show();
//			SelectSexDialog dialog = new SelectSexDialog(activity, R.style.dialog);// 实例化
//			dialog.setCanceledOnTouchOutside(true);// 设置外面点击可令Dialog消失
//			Window dialogWindow = dialog.getWindow();// 设置Dialog的位置
//			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//			dialogWindow.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);// 垂直居中和水平居中
//			dialogWindow.setAttributes(lp);
//			// 设置Dialog消失时的监听器
//			dialog.setOnSelectedSexListener(new OnSelectedSexListener() {
//				
//				@Override
//				public void selectedSex(String sex, String gender) {
//					
//				}
//			});
//			dialog.show();
		}
	}
	/** 
	* @Title: activityFinish
	* @Description: 结束activity时在交给后台提交个人信息
	* @param 
	* @return void
	* @throws 
	*/ 
	private void activityFinish() {
		AccessToken.getAPP2Token();
		if (MyApplication.app2Token != null) {
			try {
				JSONObject object = new JSONObject();
				//将数据保存为JSON格式对象,并传递给异步任务
				object.put("user_nickname", tvNickName.getText().toString());
				object.put("user_birth", tvBirthday.getText().toString());
				object.put("now_province_id", MyApplication.preferences.getInt("province_code", 44));
				object.put("now_city_id", MyApplication.preferences.getInt("city_code", 4403));
				object.put("now_county_id", MyApplication.preferences.getInt("district_code", 440305));
				object.put("profession_id", MyApplication.preferences.getInt("baseProfession", 1));
				object.put("pro_id", MyApplication.preferences.getInt("basePro", 0));
				object.put("school_id", MyApplication.preferences.getInt("baseSchool", 0));
				object.put("user_company", tvCompany.getText().toString());
				object.put("user_sign", tvSignture.getText().toString());
				Intent service = new Intent(PersonalInformationActivity.this,PostPersonInfoService.class);
				service.putExtra("user_info", object.toString());
				startService(service);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Intent intent2 = new Intent(PersonalInformationActivity.this, TabMainActivity.class);
		intent2.putExtra("tag", true);
		startActivity(intent2);
		activity.finish();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			if (resultCode == ModifyPersonInformationActivity.RESULTCODE) {
				if (data.getIntExtra("modifyType", -1) != -1) {
					//获得修改过后的信息的类型,后面需要根据这个类型修改控件中的文字
					int modifyType = data.getIntExtra("modifyType", -1);
					String newInformation = data.getStringExtra("newInformation");
					//获得修改过后的信息,并加载到控件中
					if (newInformation != null) {
						if (modifyType == ModifyPersonInformationActivity.NICK_NAME) {//名称
							tvNickName.setText(newInformation);
							editor.putString("user_nickname", newInformation);
						}
						if (modifyType == ModifyPersonInformationActivity.SIGNTURE) {//个性签名
							tvSignture.setText(newInformation);
							editor.putString("user_sign", newInformation);
						}
						if (modifyType == ModifyPersonInformationActivity.COMPANY) {//公司
							tvCompany.setText(newInformation);
							editor.putString("user_company", newInformation);
						}
						if (modifyType == ModifyPersonInformationActivity.SCHOOL) {//学校
							tvSchool.setText(newInformation);
							editor.putString("school_name", newInformation);
						}
						if (modifyType == ModifyPersonInformationActivity.OCCUPATION) {//职业
							tvOccupation.setText(newInformation);
							editor.putString("profession_name", newInformation);
						}
						editor.commit();
					}
				}
			}
			if (resultCode == SelectCityActivity.RESULTCODE) {
				if (data.getStringExtra("city_name") != null) {
					tvArea.setText(data.getStringExtra("city_name"));
				}
			}
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			activityFinish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
