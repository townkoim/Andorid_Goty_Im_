package com.slife.gopapa.activity.mine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseNoServiceActivity;
import com.slife.gopapa.activity.TabMainActivity;
import com.slife.gopapa.activity.login.RegisterSuccessActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.utils.FileUtils;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * 
 * @ClassName: ShowHeadImgActivity
 * @Description: 无原型图,剪切完图片后展示图片,并且将文件保存的手机中,按确认键完成图片上传
 * @author 肖邦
 * @date 2015-1-20 上午10:19:22
 * 
 */
@ContentView(R.layout.activity_show_head)
public class ShowHeadImgActivity extends BaseNoServiceActivity {
	private ShowHeadImgActivity activity;//本类对象
	@ViewInject(R.id.activity_show_head_img)
	private ImageView imgHead;// 剪切后的头像
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;//返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;//标题
	@ViewInject(R.id.common_title_right)
	private TextView tvRight;//确认按钮
	private boolean isSuccess = false;// 是否提交修改成功标志
	private byte[] b = new byte[] {};// bitmap数组
	private File file = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		b = getIntent().getByteArrayExtra("bitmap");// 上一个界面传过来的图片数组
		Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);// 将数组转化成Bitmap
		if (bitmap != null) {
			imgHead.setImageBitmap(bitmap);
		}
		initView();
	}
	/**
	 * 
	* @Title: initView
	* @Description: 初始化控件内容
	* @param 
	* @return void
	* @throws
	 */
	private void initView() {
		tvTitle.setText("选择头像");
		tvRight.setText("确定");
	}

	@OnClick({ R.id.common_title_back, R.id.common_title_right })
	public void onclick(View v) {
		if (v.getId() == R.id.common_title_back) {
			activity.finish();
		}
		if (v.getId() == R.id.common_title_right) {//上传头像
			getFile(b, FileUtils.getStorageDirectory(), "user_head");
		}
	}

	/**
	 * 根据byte数组，生成文件存放到手机内存当中
	 */
	public void getFile(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			String user_account = MyApplication.preferences.getString("user_account", "");
			file = new File(FileUtils.getStorageDirectory() + "/" + user_account + ".jpg");
//			file = new File(filePath + "/" + MyApplication.preferences.getString("user_account", "44723") + ".jpg");
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
			MyApplication.preferences.edit().putString(user_account+"_head_file", file.getAbsolutePath()).commit();
			if (MyApplication.isRegister == 0) {
				new PostFileTask().execute(file);
			} else {
				Intent intent = new Intent(activity, RegisterSuccessActivity.class);
				intent.putExtra("is_first_registered", false);
				startActivity(intent);
				activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * @ClassName: PostTask
	 * @Description: 修改用户资料(updateInfoAPP2)资料的异步任务
	 * @author 肖邦
	 * @date 2015-1-19 上午9:12:25
	 */
	class PostFileTask extends AsyncTask<File, Void, String[]> {

		@Override
		protected void onPreExecute() {
			MyProgressDialog.showDialog(activity, "修改资料", "上传图片中...");
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(File... params) {
			String[] result = MyHttpClient.postDataToService(ShowHeadImgActivity.this,APPConstants.URL_UPDATEINFOAPP2, MyApplication.app2Token, new String[] { "user_account" },
					new String[] { MyApplication.preferences.getString("user_account", "") }, new String[] { "logo" }, new File[] { params[0] });
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			//
			JsonObjectErrorDaoImpl.resolveJson(ShowHeadImgActivity.this, result, new JsonObjectErrorDao() {
				
				@Override
				public void disposeJsonObj(JSONObject obj) {
					try {
						isSuccess = obj.optBoolean("is_sussess");
						String new_user_logo_200 = obj.getString("new_user_logo_200");
						if (isSuccess) {
							MyApplication.preferences.edit().putString("baseImg", file.getAbsolutePath()).commit();
							MyApplication.preferences.edit().putString("user_logo_200",new_user_logo_200).commit();
							MyApplication.bitmapUtils.clearCache();
							Toast.makeText(activity, "修改成功", Toast.LENGTH_LONG).show();
							Intent intent2 = new Intent(ShowHeadImgActivity.this, TabMainActivity.class);
							intent2.putExtra("tag", true);
							startActivity(intent2);
							activity.finish();
						} else {
							Toast.makeText(activity, "修改失败", Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			
			MyProgressDialog.closeDialog();
		}
	}
}
