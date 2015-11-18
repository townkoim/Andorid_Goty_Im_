package com.slife.gopapa.activity.login;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseNoServiceActivity;
import com.slife.gopapa.activity.mine.SelectHeadImgActivity;
import com.slife.gopapa.activity.mine.ShowImageStep2Activity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.http.AccessToken;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.utils.DialogUtils;
import com.slife.gopapa.utils.FileUtils;
import com.slife.gopapa.utils.PictureUtil;
import com.slife.gopapa.view.CityDialog;
import com.slife.gopapa.view.CityDialog.OnDialogDismissListener;
import com.slife.gopapa.view.DateTimePickDialog;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * @ClassName: RegisterSuccessActivity
 * @Description: 注册成功界面,完善个人信息 点击上传头像可以在手机相册中选择照片 点击相机可以拍照 当头像 可填写姓名,出生日期,地址
 * @author 菲尔普斯（UI界面部分）@author 肖邦(业务逻辑)
 * @date 2015-1-4 下午2:10:41
 * 
 */
@ContentView(R.layout.activity_register_success)
public class RegisterSuccessActivity extends BaseNoServiceActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgTitleBack;// 返回图片按钮
	@ViewInject(R.id.common_title_name)
	private TextView txTitleName;// 标题
	@ViewInject(R.id.success_head)
	private RelativeLayout rlHead;// 上传头像
	@ViewInject(R.id.success_logo)
	private ImageView imgLogo; // 显示当前头像
	@ViewInject(R.id.success_camera)
	private ImageView imgCarema; // 照相机
	@ViewInject(R.id.success_et_papanumber)
	private TextView tvPapaName; // 啪啪号
	@ViewInject(R.id.success_upload_logo)
	private TextView tvUpload; // 上传
	@ViewInject(R.id.success_et_nickname)
	private EditText etNickName;// 昵称
	@ViewInject(R.id.success_sex)
	private RelativeLayout rlSex; // 性别
	@ViewInject(R.id.success_tv_sex)
	private TextView txSex; // 文本显示性别
	@ViewInject(R.id.success_age)
	private RelativeLayout rlAge;// 年龄
	@ViewInject(R.id.success_tv_age)
	private TextView txAge;// 文本显示年龄
	@ViewInject(R.id.success_address)
	private RelativeLayout rlAddress;// 地址
	@ViewInject(R.id.success_tv_address)
	private TextView txAddress;// 文本显示地址
	@ViewInject(R.id.success_submit)
	private Button btnSubmit;// 提交
	private int gender = 1;// 性别
	private ProgressDialog dialog;
	private boolean isSuccess = false;
	private String[] result;// 从服务器返回的结果
	private RegisterSuccessActivity activity;
	private String user_account = "";// 用户账号
	private Editor editor;// preference编辑器
	private File file;// 头像文件
	public static final int REQUEST_TAKE_PHOTO = 2000;
	private String userAccount;// 用户账号
	private String phone;// 用户手机
	private String userChatAccount;// 用户聊天账号
	private String nickName;// 姓名
	private String password;// 密码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		txTitleName.setText(R.string.register_success);
		imgTitleBack.setVisibility(View.GONE);
		dialog = new ProgressDialog(getApplicationContext());
		dialog.setMessage("提交数据中");
		editor = MyApplication.preferences.edit();
		user_account = MyApplication.preferences.getString(DBConstants.USER_ACCOUNT, "");
		tvPapaName.setText(user_account);
		if (getIntent().getBooleanExtra("is_first_registered", false)) {// 是否第一次进入注册成功界面
			initParams();
		} else {
			initView();
		}
	}

	/**
	 * @Title: initView
	 * @Description: 选择头像后设置回原来的数据
	 * @param
	 * @return void
	 * @throws
	 */
	private void initView() {
		tvPapaName.setText(MyApplication.preferences.getString("user_account", ""));
		etNickName.setText(MyApplication.preferences.getString("user_nickname", ""));
		txAge.setText(MyApplication.preferences.getString("register_age", "1990-01-01"));
		txAddress.setText(MyApplication.preferences.getString("register_address", "广东-深圳-南山"));
		if (1 == MyApplication.preferences.getInt("register_gender", 1)) {
			gender = 1;
			txSex.setText("男");
		} else {
			gender = 2;
			txSex.setText("女");
		}
		try {
			file = new File(MyApplication.preferences.getString(MyApplication.preferences.getString("user_account", "")+"_head_file",""));
			if (file != null && file.exists()) {
				imgLogo.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
				tvUpload.setVisibility(View.GONE);
			} else {
				file = null;
			}
		} catch (Exception e) {
		}
	}

	private void initParams() {
		userAccount = getIntent().getStringExtra("user_account");
		phone = getIntent().getStringExtra("user_login_number");
		userChatAccount = getIntent().getStringExtra("extend_user_account");
		nickName = getIntent().getStringExtra("user_nickname");
		password = getIntent().getStringExtra("user_password");
		// 将信息储存到Sharedpreference中.以便以后获取
		if (userAccount != null && !"".equals(userAccount)) {
			editor.putString(DBConstants.USER_ACCOUNT, userAccount);
		}
		if (phone != null && !"".equals(phone)) {
			editor.putString(DBConstants.USER_LOGIN_NAME, phone);
		}
		if (userChatAccount != null && !"".equals(userChatAccount)) {
			editor.putString(DBConstants.USER_EXTEND_ACCOUNT, userChatAccount);
		}
		if (nickName != null && !"".equals(nickName)) {
			editor.putString(DBConstants.USER_NICKNAME, nickName);
		}
		if (password != null && !"".equals(password)) {
			editor.putString(DBConstants.USER_PASSWORD, password);
		}
		editor.commit();
		if (userAccount != null && !"".equals(userAccount)) {
			tvPapaName.setText(userAccount);
		}
		if (nickName != null && !"".equals(nickName)) {
			etNickName.setText(nickName);
		}
	}

	/**
	 * @Title: titleOnclick
	 * @Description: 标题栏的监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.success_camera, R.id.success_head, R.id.common_title_back, R.id.success_sex, R.id.success_age, R.id.success_submit, R.id.success_address })
	public void titleOnclick(View v) {
		if (v.getId() == R.id.common_title_back) { // 返回按钮
			RegisterSuccessActivity.this.finish();
		}
		if (v.getId() == R.id.success_head) {
			editor.putString("register_age", txAge.getText().toString()).commit();
			MyApplication.isRegister = 1;//这个标记表示是注册时跳转到SelectHeadImgActivity的,用户选择头像后 再返回这个界面
			startActivity(new Intent(activity, SelectHeadImgActivity.class));
		}
		if (v.getId() == R.id.success_camera) {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			try {
				// 指定存放拍摄照片的位置
				File f = FileUtils.createImageFile();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (v.getId() == R.id.success_sex) {
			if (txSex.getText().equals("男")) {
				txSex.setText("女");
				gender = 2;
			} else {
				txSex.setText("男");
				gender = 1;
			}
			editor.putInt("register_gender", gender).commit();
		}
		if (v.getId() == R.id.success_age) {
			// 弹出自定义时间选择器
			DateTimePickDialog dateTimePicKDialog = new DateTimePickDialog(RegisterSuccessActivity.this, txAge.getText().toString());
			dateTimePicKDialog.dateTimePicKDialog(txAge);
		}
		if (v.getId() == R.id.success_address) {
			// 用户所在地
			CityDialog dialog = DialogUtils.createCityDialog(activity, R.style.dialog,true);
			// 设置Dialog消失时的监听器
			dialog.setOnDialogDismissListener(new OnDialogDismissListener() {

				@Override
				public void onSelected(int province_code, int city_code, int district_code, String province, String city, String district) {
					Editor editor = MyApplication.preferences.edit();
					editor.putInt("province_code", province_code);
					editor.putInt("city_code", city_code);
					editor.putInt("district_code", district_code);
					editor.putString("city_name", province + city + district);
					txAddress.setText(province + "-" + city + "-" + district);
					editor.commit();
				}
			});
			dialog.show();
		}
		if (v.getId() == R.id.success_submit) {
			// 提交按钮
			AccessToken.getAPP2Token();
			if (MyApplication.app2Token != null) {
				try {
					JSONObject object = new JSONObject();
					object.put("user_nickname", etNickName.getText().toString());
					object.put("user_gender", gender);
					object.put("user_birth", txAge.getText().toString());
					object.put("now_province_id", MyApplication.preferences.getInt("province_code", 44));
					object.put("now_city_id", MyApplication.preferences.getInt("city_code", 4403));
					object.put("now_county_id", MyApplication.preferences.getInt("district_code", 440305));
					// File file = new File("");
					new PostTask().execute(object.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	void closeDialog() {
		if (dialog != null) {
			dialog.cancel();
		}
	}

	/**
	 * 
	 * @ClassName: PostTask
	 * @Description: 完善个人资料的异步任务
	 * @author 肖邦
	 * @date 2015-1-19 上午9:12:25
	 * 
	 */
	class PostTask extends AsyncTask<String, Void, String[]> {

		@Override
		protected void onPreExecute() {
			MyProgressDialog.showDialog(activity, "基本资料", "提交中...");
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(String... params) {
			if (file == null) {
				result = MyHttpClient.postDataToService(RegisterSuccessActivity.this, APPConstants.URL_ADDBASEINFOAPP2, MyApplication.app2Token, new String[] { "user_account",
						"user_info" }, new String[] { MyApplication.preferences.getString("user_account", ""), params[0] }, null, null);
			} else {
				result = MyHttpClient.postDataToService(RegisterSuccessActivity.this, APPConstants.URL_ADDBASEINFOAPP2, MyApplication.app2Token, new String[] { "user_account",
						"user_info" }, new String[] { MyApplication.preferences.getString("user_account", ""), params[0] }, new String[] { "logo" }, new File[] { file });
			}
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			//
			if (result != null) {
				if (Integer.parseInt(result[0]) == 200) {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(result[1]);
						isSuccess = jsonObject.optBoolean("is_sussess");
						if (isSuccess) {
							editor.putString("baseNickName", etNickName.getText().toString());
							editor.putInt("baseGender", gender);
							editor.putString("baseBirth", txAge.getText().toString());
							editor.commit();
							startActivity(new Intent(activity, LoginedActivity.class));
							activity.finish();
						} else {
//							Toast.makeText(activity, "提交失败", Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
//					Toast.makeText(activity, "提交失败:" + ParseErrorJsonUtils.getErrorMsg(result).getError(), Toast.LENGTH_LONG).show();
				}
			}
			MyProgressDialog.closeDialog();
			super.onPostExecute(result);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Toast.makeText(this, "请完善个人信息", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_TAKE_PHOTO) {
			if (resultCode == Activity.RESULT_OK) {
				// 添加到图库,这样可以在手机的图库程序中看到程序拍摄的照片
				MyApplication.isRegister = 1;
				PictureUtil.galleryAddPic(this, MyApplication.currentPhotoPath);
				Intent intent = new Intent(activity, ShowImageStep2Activity.class);
				intent.putExtra("img_path", MyApplication.currentPhotoPath);
				startActivity(intent);
				activity.finish();
			} else {
				// 取消照相后，删除已经创建的临时文件。
				PictureUtil.deleteTempFile(MyApplication.currentPhotoPath);
			}
		}
	}
}
