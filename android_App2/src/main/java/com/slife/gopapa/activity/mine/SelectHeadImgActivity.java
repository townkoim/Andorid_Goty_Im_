package com.slife.gopapa.activity.mine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
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
import com.slife.gopapa.adapter.GroupAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.model.ImageBean;
import com.slife.gopapa.utils.FileUtils;
import com.slife.gopapa.utils.PictureUtil;

/**
 * 
 * @ClassName: SelectHeadImgActivity
 * @Description: 无原型图,首先获取手机中的文件,然后按照文件夹的名字将图片分类装入GridView里面.
 * @author 肖邦
 * @date 2015-1-20 上午10:11:10
 * 
 */
@ContentView(R.layout.activity_select_head_img)
public class SelectHeadImgActivity extends BaseNoServiceActivity {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;//返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;//标题
	@ViewInject(R.id.select_head_img_rl)
	private RelativeLayout relativeLayout;//手机拍摄按钮
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();// 文件夹的名字,对应文件夹图片地址集合
	private List<ImageBean> list = new ArrayList<ImageBean>();// 图片Model
	private final static int SCAN_OK = 1;
	private static final int REQUEST_TAKE_PHOTO = 3000;//请求码
	private ProgressDialog mProgressDialog;//加载进度条
	private GroupAdapter adapter;
	private GridView mGroupGridView;
	private SelectHeadImgActivity activity;//本类对象
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			// 扫描图片完成
			case SCAN_OK:
				// 关闭进度条
				mProgressDialog.dismiss();
				adapter = new GroupAdapter(SelectHeadImgActivity.this, list = subGroupOfImage(mGruopMap), mGroupGridView);
				mGroupGridView.setAdapter(adapter);
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		tvTitle.setText("选择头像");
		mGroupGridView = (GridView) findViewById(R.id.main_grid);
		getImages();
		mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//选择对应的文件夹后将文件夹相册的路径集合传递到下一个界面
				List<String> childList = mGruopMap.get(list.get(position).getFolderName());
				Intent mIntent = new Intent(activity, ShowImageStep1Activity.class);
				mIntent.putStringArrayListExtra("data", (ArrayList<String>) childList);
				startActivity(mIntent);
				activity.finish();
			}
		});
		if (getIntent() != null) { // 如果有数据
			if (getIntent().getBooleanExtra("isMine", false)) {// 如果是从MineActivity传过来的,就把拍照的按钮显示出来
				relativeLayout.setVisibility(View.VISIBLE);
			}
		}
	}

	@OnClick({ R.id.common_title_back,R.id.select_head_img_rl })
	public 	void onclick(View v) {
		if (v.getId() == R.id.common_title_back) {
			activity.finish();
		}
		if(v.getId()==R.id.select_head_img_rl){
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
	}

	/** (非 Javadoc) 
	* Title: onActivityResult
	* Description:
	* @param requestCode
	* @param resultCode
	* @param data
	* @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	*/ 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == REQUEST_TAKE_PHOTO) {
				if (resultCode == Activity.RESULT_OK) {
					// 添加到图库,这样可以在手机的图库程序中看到程序拍摄的照片
					MyApplication.isRegister = 0;//不是注册时选头像赋值0
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
	
	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}

		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable() {

			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = activity.getContentResolver();
				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?", new String[] {
						"image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
					// 获取该图片的父路径名
					String parentName = new File(path).getParentFile().getName();
					// 根据父路径名将图片放入到mGruopMap中
					if (!mGruopMap.containsKey(parentName)) {
						List<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}
				mCursor.close();
				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(SCAN_OK);
			}
		}).start();
	}

	/**
	 * 
	* @Title: subGroupOfImage
	* @Description: 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中 所以需要遍历HashMap将数据组装成List
	* @param @param mGruopMap
	* @param @return
	* @return List<ImageBean>
	* @throws
	 */
	private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
		if (mGruopMap.size() == 0) {
			return null;
		}
		List<ImageBean> list = new ArrayList<ImageBean>();
		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
		while (it.hasNext()) {
			//将获取的相册信息封装到ImageBean实体类中,适配器需要用到这些信息
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));// 获取该组的第一张图片
			list.add(mImageBean);
		}
		return list;
	}
}
