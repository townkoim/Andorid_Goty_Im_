package com.slife.gopapa.activity.mine;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.adapter.SelectAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.utils.ParseErrorJsonUtils;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * 
 * @ClassName: ModifyPersonInformationActivity
 * @Description: 修改个人信息界面,原型图"个人信息-02~06"; 修改完信息后,将结果保存并返回个人信息界面
 * @author 肖邦
 * @date 2015-1-7 下午5:09:49
 * 
 */
@ContentView(R.layout.activity_mine_modify_person_information)
public class ModifyPersonInformationActivity extends BaseActivity {
	public static final int NICK_NAME = 0;// 修改姓名
	public static final int SIGNTURE = 1;// 修改个性签名
	public static final int OCCUPATION = 2;// 修改职业
	public static final int COMPANY = 3;// 修改公司
	public static final int SCHOOL = 4;// 修改学校
	public static final int EMAIL = 5;// 修改邮箱
	public static final int SPORT = 6;// 修改运动
	public static final int RESULTCODE = 1001;// 结果码

	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;// 返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;// 标题
	@ViewInject(R.id.common_title_right)
	private TextView tvSave;// 保存键
	@ViewInject(R.id.activity_modify_person_information_tips)
	private TextView tvTips;// 提示的内容
	@ViewInject(R.id.activity_modify_person_information_input)
	private EditText etInput;// 输入框
	private ModifyPersonInformationActivity activity;
	@ViewInject(R.id.activity_modify_person_information_ll_type1)
	private LinearLayout ll;// 昵称.签名.公司公共布局
	@ViewInject(R.id.activity_modify_person_information_input_list)
	private ListView lv;// 职业.学校List
	private int modifyType;// 从上一个界面传过来的修改类型
	private SelectAdapter adapter;
	private ArrayList<String> list;// nameList
	private ArrayList<String> idList;// IDList
	private boolean isSchool = true;// 是否是选择学校信息
	private boolean isProfession = true;// 是否是职业信息

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		Intent intent = getIntent();
		if (intent != null) {
			initTitle(intent);
		} else {

		}
	}

	void initTitle(Intent intent) {
		list = new ArrayList<String>();
		idList = new ArrayList<String>();
		adapter = new SelectAdapter(activity, list, SelectAdapter.TYPE_ACTIVITY);//第三个参数是为了改变布局的样式
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Editor editor = MyApplication.preferences.edit();
				Intent intent = new Intent();
				// 将数据储存到SharedPreference
				//判断修改资料的类型
				if (isSchool) {// 选择学校
					editor.putInt("baseSchool", Integer.parseInt(idList.get(position)));
					editor.putString("school_name", list.get(position));
				} else {
					if (isProfession) {// 选择职业
						editor.putInt("baseProfession", Integer.parseInt(idList.get(position)));
						editor.putString("profession_name", list.get(position));
					} else {// 体育项目
						intent.putExtra("tag_id", idList.get(position));
						intent.putExtra("tag_name", list.get(position));
						setResult(SelectActivity.RESULTCODE, intent);
						finish();
					}
				}
				editor.commit();
				intent.putExtra("newInformation", list.get(position));// 新的资料
				intent.putExtra("modifyType", modifyType);// 资料的类型
				setResult(ModifyPersonInformationActivity.RESULTCODE, intent);
				finish();
			}
		});
		modifyType = -1;
		tvSave.setText("确定");
		String oldInformation = null;
		if ((modifyType = intent.getIntExtra("modifyType", -1)) != -1) {//如果传过来的类型不为空
			//如果修改类型为昵称.公司.个性签名则把Listview隐藏起来
			if (modifyType == ModifyPersonInformationActivity.NICK_NAME || modifyType == ModifyPersonInformationActivity.COMPANY
					|| modifyType == ModifyPersonInformationActivity.SIGNTURE) {
				if ((oldInformation = intent.getStringExtra("oldInformation")) != null) {
					etInput.setText(oldInformation);
				}
				lv.setVisibility(View.GONE);
			} else {
				ll.setVisibility(View.GONE);
			}
			// 初始化名字
			if (modifyType == ModifyPersonInformationActivity.NICK_NAME) {// 修改姓名
				tvTitle.setText("更改名称");
				tvTips.setText("好的名字可以让你的朋友更容易记住你");
			} else if (modifyType == ModifyPersonInformationActivity.SIGNTURE) {// 修改签名
				tvTitle.setText("个性签名");
				tvTips.setText("一种签名，一种态度");
			} else if (modifyType == ModifyPersonInformationActivity.COMPANY) {// 修改公司
				tvTitle.setText("公司");
				tvTips.setText("填公司，发现同事");
			} else if (modifyType == ModifyPersonInformationActivity.SCHOOL) {// 修改学校
				tvSave.setVisibility(View.GONE);
				tvTitle.setText("学校");
				MyApplication.preferences.edit().putBoolean("is_school", true).commit();// 保存到手机
				new GetSchoolOrProfessionTask().execute(APPConstants.URL_SCHOOL + MyApplication.commonToken, "school_id", "school_name");
			} else if (modifyType == ModifyPersonInformationActivity.OCCUPATION) {// 修改职业
				tvSave.setVisibility(View.GONE);
				tvTitle.setText("职业");
				isSchool = false;
				// new
				// GetSchoolOrProfessionTask().execute(APPConstants.URL_PROFESSION
				// + MyApplication.commonToken, "profession_id",
				// "profession_name");
				new GetSchoolOrProfessionTask().execute("profession");
			} else if (modifyType == ModifyPersonInformationActivity.SPORT) {// 修改运动
				tvSave.setVisibility(View.GONE);
				tvTitle.setText("项目");
				isSchool = false;
				isProfession = false;
				new GetSchoolOrProfessionTask().execute(APPConstants.URL_SPORT_TAG + MyApplication.commonToken, "tag_id", "tag_name");
			}
		}
	}

	/**
	 * @ClassName: GetSchoolOrProfessionTask
	 * @Description: 获取公共接口的学校信息或者是职业信息
	 * @author 肖邦
	 * @date 2015-1-27 上午11:46:47
	 */
	class GetSchoolOrProfessionTask extends AsyncTask<String, Integer, ArrayList<String>> {

		@Override
		protected void onPreExecute() {
			MyProgressDialog.showDialog(activity, "基本信息", "获取信息当中...");
			//每次获取信息时将集合中的数据清空
			list.clear();
			idList.clear();
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			ArrayList<String> json = new ArrayList<String>();
			if ("profession".equals(params[0])) {
				//从本地获取职业类型填充到集合当中
				String[] arr = activity.getResources().getStringArray(R.array.occupation);
				int i = 1;
				json.add(0, "201");//用于在onPostExecute区别是在服务器取回的信息还是在本地取回的信息
				for (String s : arr) {
					json.add(i++, s);
				}
				return json;
			} else {
				String[] arr = MyHttpClient.getJsonFromService2(ModifyPersonInformationActivity.this, params[0]);
				if (arr != null) {// 将结果放入集合中
					json.add(0, arr[0]);
					json.add(1, arr[1]);
				} else {
					json.add(0, null);
					json.add(1, null);
				}
				json.add(2, params[1]);// Json中id的键
				json.add(3, params[2]);// Json中id对应学校或职业的键
				return json;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<String> json) {
			if (json != null) {
				if (Integer.parseInt(json.get(0)) == 200) {
					try {
						JSONArray arr = new JSONArray(json.get(1));
						if (arr != null && arr.length() > 0) {
							for (int i = 0; i < arr.length(); i++) {
								JSONObject object = (JSONObject) arr.get(i);
								idList.add(object.optString(json.get(2)));// id
								list.add(object.optString(json.get(3)));// id对应的学校或职业
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else if (Integer.parseInt(json.get(0)) == 201) {
					for(int i =1;i<json.size();i++){
						idList.add("1");
						list.add(json.get(i));
					}
				} else {
					Toast.makeText(
							activity,
							ParseErrorJsonUtils.getErrorMsg(new String[] { json.get(0), json.get(1) }) != null ? ParseErrorJsonUtils.getErrorMsg(
									new String[] { json.get(0), json.get(1) }).getError() : "未知错误", Toast.LENGTH_LONG).show();

				}
			}
			adapter.notifyDataSetChanged();// 提示适配器要更新
			MyProgressDialog.closeDialog();// 关闭对话框
			super.onPostExecute(json);
		}
	}

	@OnClick({ R.id.common_title_back, R.id.common_title_right })
	public void onclick(View v) {
		if (v.getId() == R.id.common_title_back) {
			finish();
		}
		if (v.getId() == R.id.common_title_right) {
			Intent intent = new Intent();
			//判断个性签名的长度
			if (modifyType == ModifyPersonInformationActivity.SIGNTURE){
				if(etInput.length()>60){
					Toast.makeText(activity, "个性签名字符长度过长", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			//判断公司名称的长度
			if (modifyType == ModifyPersonInformationActivity.COMPANY){
				if(etInput.length()>50){
					Toast.makeText(activity, "公司字符长度过长", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			intent.putExtra("newInformation", etInput.getText().toString());
			intent.putExtra("modifyType", modifyType);
			setResult(ModifyPersonInformationActivity.RESULTCODE, intent);
			finish();
		}
	}

}
