package com.slife.gopapa.activity.mine;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseNoServiceActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.utils.ParseErrorJsonUtils;
import com.slife.gopapa.view.MyProgressDialog;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * @ClassName: ShareActivity
 * @Description: 分享界面,实现原型图中 "分享赚积分.jpg."
 * @author 肖邦
 * @date 2015-1-29 下午3:29:30
 */
@ContentView(R.layout.activity_share)
public class ShareActivity extends BaseNoServiceActivity implements PlatformActionListener, Callback {
	private ShareActivity activity;//本类对象
	public static Tencent mTencent;//QQ空间分享
	// QZone分享， SHARE_TO_QQ_TYPE_DEFAULT 图文，SHARE_TO_QQ_TYPE_IMAGE 纯图
	private int shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;
	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;// 分享成功标记
	private static final int MSG_CANCEL_NOTIFY = 3;// 分享取消标记
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;//返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;//页面标题
	private String title = "啪啪江湖";//分享标题
	private String summary = "我正在玩啪啪江湖，约赛、挑战、排名等你来挑战！http://spapa.com.cn/apk/ppjh.apk";//分享内容
	private String homePageURL = "http://spapa.com.cn";//分享超链接
	private String imgUrl = "";//分享图片地址
	private boolean isCompetitionShare = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		mTencent = Tencent.createInstance("1104217523", ShareActivity.this);//用QQ空间APP_KEY获得实例
		ViewUtils.inject(activity);
		// 初始化ShareSDK
		ShareSDK.initSDK(this);
		tvTitle.setText("分享赞活力");
		if(getIntent()!=null&&getIntent().getStringArrayExtra("arr")!=null){
			String [] arr = getIntent().getStringArrayExtra("arr");
			isCompetitionShare = true;
//			title = arr[0];
//			summary = arr[1];
			homePageURL = arr[2];
			imgUrl = arr[3];
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}

	@OnClick({ R.id.activity_share_qq_rl, R.id.activity_share_we_rl, R.id.activity_share_sina_rl,R.id.common_title_back })
	public void onclick(View v) {
		if (v.getId() == R.id.activity_share_qq_rl) {//QQ空间分享
//			final Bundle params = new Bundle();
//			params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
//			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
//			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
//			if (shareType != QzoneShare.SHARE_TO_QZONE_TYPE_APP) {
//				// app分享不支持传目标链接
//				params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, homePageURL);
//			}
//			// 支持传多个imageUrl
//			// ArrayList<String> imageUrls = new ArrayList<String>();
//			// for (int i = 0; i < mImageContainerLayout.getChildCount(); i++) {
//			// LinearLayout addItem = (LinearLayout)
//			// mImageContainerLayout.getChildAt(i);
//			// EditText editText = (EditText) addItem.getChildAt(1);
//			// imageUrls.add(editText.getText().toString());
//			// }
//			// String imageUrl = "XXX";
//			// params.putString(Tencent.SHARE_TO_QQ_IMAGE_URL, imageUrl);
//			params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, "http://su.bdimg.com/static/superplus/img/logo_white_ee663702.png");
//			doShareToQzone(params);
			
			
//			QZone.ShareParams sp = new QZone.ShareParams();
//			// 设置分享内容
//			sp.text = summary;
//			sp.title = "haha";
//			sp.titleUrl = "http://spapa.com.cn";
//			sp.site = "啪啪江湖";
//			sp.siteUrl = "http://spapa.com.cn";
//			sp.imageUrl = "http://su.bdimg.com/static/superplus/img/logo_white_ee663702.png";
//			// 分享网络图片，新浪分享网络图片，需要申请高级权限,否则会报10014的错误
//			// 权限申请：新浪开放平台-你的应用中-接口管理-权限申请-微博高级写入接口-statuses/upload_url_text
//			// 注意：本地图片和网络图片，同时设置时，只分享本地图片
//			// 初始化新浪分享平台
//			Platform pf = ShareSDK.getPlatform(ShareActivity.this, QZone.NAME);
//			// 设置分享监听
//			pf.setPlatformActionListener(ShareActivity.this);
//			// 执行分享
//			pf.share(sp);
			ShareParams sp = new ShareParams();
			sp.setTitle("啪啪江湖");
			sp.setTitleUrl("http://spapa.com.cn/apk/ppjh.apk"); // 标题的超链接
			sp.setText("我正在玩啪啪江湖，约赛、挑战、排名等你来挑战！");
			//sp.setImagePath("/sdcard/cccc.png");
			sp.setImageUrl("http://365ccyx.com/templates/ccyx/images/pic/logo.png");
			sp.setSite("啪啪江湖");
			sp.setSiteUrl("http://spapa.com.cn");
			Platform qzone = ShareSDK.getPlatform(activity, QZone.NAME);
			qzone.setPlatformActionListener(this);
			qzone.share(sp);
		}
		if (v.getId() == R.id.activity_share_we_rl) {//朋友圈分享
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = homePageURL;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = title;
			msg.description = summary;
			// 这里替换一张自己工程里的图片资源
			Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
			msg.setThumbImage(thumb);
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			MyApplication.api.sendReq(req);
		}
		if (v.getId() == R.id.activity_share_sina_rl) {//微博分享
			SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
			// 设置分享内容
			sp.text = summary;
			// 分享网络图片，新浪分享网络图片，需要申请高级权限,否则会报10014的错误
			// 权限申请：新浪开放平台-你的应用中-接口管理-权限申请-微博高级写入接口-statuses/upload_url_text
			// 注意：本地图片和网络图片，同时设置时，只分享本地图片
			// 初始化新浪分享平台
			Platform pf = ShareSDK.getPlatform(ShareActivity.this, SinaWeibo.NAME);
			// 设置分享监听
			pf.setPlatformActionListener(ShareActivity.this);
			// 执行分享
			pf.share(sp);
		}
		if(v.getId()==R.id.common_title_back){
			activity.finish();
		}
	}

	/**
	 * QQ空间分享的回调监听器
	 */
	IUiListener qZoneShareListener = new IUiListener() {

		@Override
		public void onCancel() {// 取消
			Message msg = new Message();
			msg.what = MSG_ACTION_CCALLBACK;
			msg.obj = "1";
			UIHandler.sendMessage(msg, ShareActivity.this);
		}

		@Override
		public void onError(UiError e) {// 失败
			Message msg = new Message();
			msg.what = MSG_ACTION_CCALLBACK;
			msg.obj = "1";
			UIHandler.sendMessage(msg, ShareActivity.this);
		}

		@Override
		public void onComplete(Object response) {// 成功
			new ShareTask().execute(5);
		}
	};

	/**
	 * 用异步方式启动分享
	 * 
	 * @param params
	 */
	private void doShareToQzone(final Bundle params) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ShareActivity.mTencent.shareToQzone(activity, params, qZoneShareListener);
			}
		}).start();
	}

	// 设置监听http://sharesdk.cn/androidDoc/cn/sharesdk/framework/PlatformActionListener.html
	// 监听是子线程，不能Toast，要用handler处理，不要犯这么二的错误
	// Setting listener,
	// http://sharesdk.cn/androidDoc/cn/sharesdk/framework/PlatformActionListener.html
	// The listener is the child-thread that can not handle ui
	/**
	 * (非 Javadoc) Title: onCancel Description:mob分享平台回调
	 * 
	 * @param platform
	 * @param action
	 * @see cn.sharesdk.framework.PlatformActionListener#onCancel(cn.sharesdk.framework.Platform,
	 *      int)
	 */
	@Override
	public void onCancel(Platform platform, int action) {
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = platform;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public void onComplete(Platform platform, int action, HashMap<String, Object> arg2) {
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = platform;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public void onError(Platform platform, int action, Throwable t) {
		t.printStackTrace();
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = t;
		UIHandler.sendMessage(msg, this);
	}

	/**
	 * (非 Javadoc) Title: handleMessage Description:
	 * 
	 * @param msg
	 * @return
	 * @see android.os.Handler.Callback#handleMessage(android.os.Message)
	 */
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_TOAST: {
			String text = String.valueOf(msg.obj);
			Toast.makeText(ShareActivity.this, text, Toast.LENGTH_SHORT).show();
		}
			break;
		case MSG_ACTION_CCALLBACK: {
			switch (msg.arg1) {
			case 1: { // 成功, successful notification
				Platform platform = (Platform) msg.obj;
				String name = platform.getName();
				if ("SinaWeibo".equals(name)) {
					new ShareTask().execute(6);// 6：推荐到微博；
				}
				if("QZone".equals(name)){
					new ShareTask().execute(7);// 7：推荐到QQ空间；
				}
			}
				break;
			case 2: { // 失败, fail notification
				String expName = msg.obj.getClass().getSimpleName();
				if ("WechatClientNotExistException".equals(expName) || "WechatTimelineNotSupportedException".equals(expName)) {
					Toast.makeText(activity, R.string.wechat_client_inavailable, Toast.LENGTH_LONG).show();
				} else if ("GooglePlusClientNotExistException".equals(expName)) {
					Toast.makeText(activity, R.string.google_plus_client_inavailable, Toast.LENGTH_LONG).show();
				} else if ("QQClientNotExistException".equals(expName)) {
					Toast.makeText(activity, R.string.qq_client_inavailable, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(activity, "请稍后再试", Toast.LENGTH_LONG).show();
				}
			}
				break;
			case 3: { // 取消, cancel notification
				Toast.makeText(activity, R.string.share_canceled, Toast.LENGTH_LONG).show();
			}
				break;
			}
		}
			break;
		case MSG_CANCEL_NOTIFY: {
			Toast.makeText(activity, R.string.share_canceled, Toast.LENGTH_LONG).show();
		}
			break;
		}
		return false;
	}

	class ShareTask extends AsyncTask<Integer, Void, String[]> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			MyProgressDialog.showDialog(activity, "分享赚活力", "请稍等...");
		}

		@Override
		protected String[] doInBackground(Integer... params) {
			if(isCompetitionShare){//分享战绩
				return MyHttpClient.postDataToService(activity, APPConstants.URL_ADDSHAREVITALITYAPP2, MyApplication.app2Token, new String[] { "user_account", "type","race_id" }, new String[] {
						MyApplication.preferences.getString("user_account", ""), "4",getIntent().getStringExtra("race_id") }, null, null);
			}else{
				return MyHttpClient.postDataToService(activity, APPConstants.URL_ADDSHAREVITALITYAPP2, MyApplication.app2Token, new String[] { "user_account", "type" }, new String[] {
						MyApplication.preferences.getString("user_account", ""), String.valueOf(params[0]) }, null, null);
			}
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			MyProgressDialog.closeDialog();
			if (result != null) {
				if ("200".equals(result[0])) {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(result[1]);
						int shareResult = jsonObject.optInt("is_sussess");
						if (shareResult == -2) {
							Toast.makeText(activity, "已经在该平台分享过", Toast.LENGTH_LONG).show();
						}
						if (shareResult == -1) {
							Toast.makeText(activity, "分享失败", Toast.LENGTH_LONG).show();
						}
						if (shareResult == 1) {
							Toast.makeText(activity, "分享成功", Toast.LENGTH_LONG).show();
							activity.finish();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(activity, ParseErrorJsonUtils.getErrorMsg(result).getError(), Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(activity, "加分失败", Toast.LENGTH_LONG).show();
			}
		}
	}

}
