package com.slife.gopapa.activity.news;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gotye.api.bean.GotyeImageMessage;
import com.gotye.api.bean.GotyeTextMessage;
import com.gotye.api.bean.GotyeUser;
import com.gotye.api.media.WhineMode;
import com.gotye.api.utils.TimeUtil;
import com.gotye.api.utils.UriImage;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.adapter.ChatAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.dao.ChatListener;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.database.DBHelperOperation;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.ChatImageMessage;
import com.slife.gopapa.model.ChatRichTextMessage;
import com.slife.gopapa.model.ChatTextMessage;
import com.slife.gopapa.model.ChatUserInfo;
import com.slife.gopapa.model.ChatVoiceMessage;
import com.slife.gopapa.model.MyChatMessage;
import com.slife.gopapa.model.RecentNews;
import com.slife.gopapa.utils.DataUtils;
import com.slife.gopapa.utils.FileUtils;
import com.slife.gopapa.view.ImagePopupWindow;

/**
 * @ClassName: ChatActivity
 * @Description:聊天界面 1、从上个界面 接收传递过来的ChatUserInfo
 *                   的targertUser发送目标的信息（键名为“users_info”），
 *                   2、给gotyApi添加本界面的专属聊天接口回调的监听事件(ChatListener)
 *                   3、根据targertUser拉取历史消息。
 *                   4、发送语音消息。构造GotyeMessage对象。并设置text的值，发送之后会在ChatListener的onSendMessage中回调
 *                   （语音一样）
 *                   5、接收消息。当有消息到达的时候，会判断和我聊天的人的聊天账号和消息到达的时候的发送者的聊天账号进行匹配（
 *                   此步骤会在ChatListener的onReceiveMessage里面回调）
 *                   如果是一样的，说明是当前正在和我聊天的人发来的消息
 *                   步骤4和步骤5都会把数据通过ModelChangeUtils的GotyeToMyChat去进行转换成自己的实体类
 *                   。并添加到List<MyChatMessage> list集合，然后通知适配器，数据发生改变
 * @author 菲尔普斯
 * @date 2015-1-8 下午5:27:23
 * 
 */
@ContentView(R.layout.activity_chat)
public class ChatActivity extends BaseActivity implements
		OnRefreshListener2<ListView> {
	@ViewInject(R.id.common_title_name)
	private TextView tvTitleName;// 标题
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack; // 标题返回按钮
	@ViewInject(R.id.chat_ptr_listview)
	private PullToRefreshListView ptrListView; // 列表ListView
	@ViewInject(R.id.chat_img_voice_or_keyboard)
	private ImageView imgVoiceOrKeyboard; // 显示语音还是键盘的控件（显示语音则表示当前正在输入文字，反之）
	@ViewInject(R.id.chat_relayout_text_message)
	private RelativeLayout layoutTextMessage; // 包裹输入文字的编辑框和标情况的外层父布局
	@ViewInject(R.id.chat_et_message)
	private EditText etMessage; // 文字编辑框
	@ViewInject(R.id.chat_img_expression)
	private ImageView imgExpression; // 表情图片
	@ViewInject(R.id.chat_btn_voice)
	private Button btnVoice; // 按住说话的按钮
	@ViewInject(R.id.chat_img_send_state)
	private ImageView imgSendMessage; // 发送消息的按钮
	@ViewInject(R.id.chat_layout_popup)
	private RelativeLayout llyout; // 底部布局显示表情的
	@ViewInject(R.id.chat_img_voice)
	private ImageView imgVoice;// 录音的时候显示的动画控件
	@ViewInject(R.id.chat_viewpager)
	private ViewPager viewPager; // 底部表情的viewpager
	@ViewInject(R.id.chat_dot_viewgroup)
	private LinearLayout dotLayout; // 底部表情的控制页数的圆点
	@ViewInject(R.id.chat_rl_bottom)
	private RelativeLayout rlBottom;

	private ListView listView;// 聊天列表
	private boolean isTextInput = true;// 判断当前是否是文字输入 ,true表示是，false表示是语音输入
	private float startY; // 手指按下btnVoice时候的坐标
	private WindowManager windowManager; // 窗体管理类
	private int height; // 设定手指要上滑多高才取消发送语音
	private AnimationDrawable animation; // 按住说话的语音动画集合
	private ChatAdapter adapter;// 适配器
	private List<MyChatMessage> list;// 数据集合

	private ChatUserInfo targertUser;// 发送目标的实体类
	private GotyeUser targetGotye;// 发送目标的亲加实体类
	private GotyeUser senderGotye;// 发送者秦加实体类
	public static int historyMsgCount = 0;// 历史消息的条数

	// 底部表情兰的全局对象、变量
	private String[] expressNames; // 图片所对应的转义字符的名称的数组
	private int[] expressIds; // 图片的id数组
	private List<View> gridViewList; // 裝載gridview的容器
	private int expressPage; // 表情的页数（表示多少个GridView）
	private ImageView[] dots;
	private Context context = ChatActivity.this;

	// 发送图片先关的全局变量
	private ImagePopupWindow menuWindow; // 底部的popupWindows
	private String imgName = null;// 要发送的图片的名字

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(ChatActivity.this);
		etMessage.addTextChangedListener(new EdTextchangeListener());
		initListView();
		initViewpager();
		initParams();
		btnVoice.setOnTouchListener(new BtnVoiceOnTouchListener());// 按住说话按钮的监听事件
		etMessage.setOnTouchListener(new OnTouchListener() { // 文本输入框的触摸事件

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							llyout.setVisibility(View.GONE);
							listView.setSelection(listView.getAdapter()
									.getCount() - 1);
						}
						return false;
					}
				});
	}

	/**
	 * @Title: imgSendMessageOnClick
	 * @Description: 发送文字或者图片按钮的监听事件
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.chat_img_send_state })
	public void imgSendMessageOnClick(View v) {
		listView.setSelection(listView.getAdapter().getCount() - 1);
		if (isTextInput) { // 表示当前是文本输入状态
			String message = etMessage.getText().toString();
			if (message != null && !"".equals(message)) { // 发送普通文本消息
				if (MyApplication.gotyeState) {
					sendTextMsg(message);
				} else {
					Toast.makeText(ChatActivity.this, "请检查网络连接",
							Toast.LENGTH_SHORT).show();
				}
			} else {// 显示弹框
				menuWindow = new ImagePopupWindow(ChatActivity.this,
						new PicPopupWindowOncick());// 设置popouWindos的监听器
				// 显示窗口
				menuWindow.showAtLocation(rlBottom, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			}
		}
	}

	/**
	 * 
	 * @Title: voiceOrKeyboardOnclick
	 * @Description: 切换发送文字还是发送语音监听器
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.chat_img_voice_or_keyboard })
	public void voiceOrKeyboardOnclick(View v) {
		llyout.setVisibility(View.GONE);
		if (isTextInput) { // 表示当前处于文本输入状态,要切换为语音输入状态
			layoutTextMessage.setVisibility(View.GONE); // 让输入文本的不可见
			btnVoice.setVisibility(View.VISIBLE); // 按住说话按钮可见
			imgSendMessage
					.setImageResource(R.drawable.chat_message_send_unselect); // 文字发送按钮设置成灰色
			imgVoiceOrKeyboard.setImageResource(R.drawable.chat_voice);
			isTextInput = false; // 当前为语音输入，isTextInput则为false
		} else { // 表示当前是语音输入状态，要切换为文本输入状态
			layoutTextMessage.setVisibility(View.VISIBLE); // 让输入文本控件可见
			btnVoice.setVisibility(View.GONE); // 按住说话的按钮不可见
			isTextInput = true; // 当前是文本输入，isTextInput则为true
			if (!etMessage.getText().toString().equals("")) {
				imgSendMessage
						.setImageResource(R.drawable.chat_message_send_select);
			}
			imgVoiceOrKeyboard.setImageResource(R.drawable.chat_keyboard);
		}
	}

	/**
	 * @Title: titleOnclick
	 * @Description: 标题栏返回按钮
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.common_title_back })
	public void titleOnclick(View v) {
		insertDataToDB();
		ChatActivity.this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		insertDataToDB();
		return super.onKeyDown(keyCode, event);
	}

	/***
	 * @Title: insertDataToDB
	 * @Description: 将此数据保存到数据库(在返回的时候要将数据保存起来)
	 * @param
	 * @return void
	 * @throws
	 */
	private void insertDataToDB() {
		historyMsgCount = 0;
		ChatListener.mVoiceSendTime = 0;
		imgName = null;
		MyApplication.targetUser = null; // 在这里首先要将全局对象我正在聊天的对象清空
		if (list != null && list.size() > 0) { // 如果聊天的集合不为空的话
			MyChatMessage mcMsg = list.get(list.size() - 1); // 得到集合最后一条对象
			String msg = null;
			if (mcMsg instanceof ChatTextMessage) { // 判断最后一条对象是文本信息还是语音信息还是自定义信息
				msg = ((ChatTextMessage) mcMsg).getText();
			} else if (mcMsg instanceof ChatVoiceMessage) {
				msg = "voice";
			} else if (mcMsg instanceof ChatRichTextMessage) {
				msg = "richtext";
			} else if (mcMsg instanceof ChatImageMessage) {
				msg = "imagetext";
			}
			if (DBHelperOperation.queryRecentContactByExtendAccount(targertUser
					.getExtend_user_account())) { // 首先查询数据库有没有此人的聊天记录，说明数据库有这个人的信息,修改这个人的信息
				DBHelperOperation.updateRecentContact(targertUser, msg); // 修改数据
			} else {
				DBHelperOperation.insertRecentContact(targertUser, msg);// 插入数据
			}
			RecentNews news = new RecentNews();
			if (MyApplication.senderUser != null && MyApplication.gotyeState) { // 构建最近联系人RecentNews对象，然后发送光比给NewsFragment页面，通知他最近联系人已经发生改变，并吧这个数据广播给NewsFragment的广播接受者
				news.setUser_myacount(MyApplication.senderUser
						.getExtend_user_account());
				news.setUser_extend_account(targertUser
						.getExtend_user_account());
				news.setUser_nick_name(targertUser.getUser_nickname());
				news.setUser_logo(targertUser.getUser_logo());
				news.setUser_last_message(msg);
				news.setTime(String.valueOf(System.currentTimeMillis()));
				Intent intent = new Intent();
				intent.setAction(APPConstants.ACTION_NEWS_DBCHANGE);
				intent.putExtra("recent_news", news);
				sendBroadcast(intent);
			}
		}
	}

	/************************** 初始化聊天的ListView的业务逻辑（包括校验用户）Begin *************************/

	/**
	 * @Title: initListView
	 * @Description: ListView列表初始化
	 * @param
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	private void initListView() {
		ptrListView.setOnRefreshListener(this);
		ptrListView.setPullLabel("下拉刷新..."); // 设置下拉的时候显示的文字
		ptrListView.setRefreshingLabel("放开刷新..."); // 设置下拉标签中间显示的文字
		ptrListView.setReleaseLabel("正在刷新"); // 设置下拉放开显示的文字
		listView = ptrListView.getRefreshableView(); // 得到listView对象
		list = new ArrayList<>();
		adapter = new ChatAdapter(ChatActivity.this, list); // 初始化适配器
		listView.setAdapter(adapter);
		listView.setSelection(listView.getAdapter().getCount() - 1);// 设置当前ListView显示在最低端
	}

	/**
	 * @Title: initParams
	 * @Description:初始化参数
	 * @param
	 * @return void
	 * @throws
	 */
	private void initParams() {
		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// 获取到屏幕窗体管理类
		height = windowManager.getDefaultDisplay().getHeight() / 5; // 设定上滑的高度
		animation = (AnimationDrawable) getResources().getDrawable( // 设定动画
				R.anim.chat_voice_saying);
		// 构建聊天亲加对象
		targertUser = (ChatUserInfo) getIntent().getSerializableExtra(
				"users_info");
		checkTargetAccount();// 校验用户信息，如果啪啪号都不存在则直接获取用户信息
		targetGotye = new GotyeUser(targertUser.getExtend_user_account());
		MyApplication.targetUser = targertUser;
		historyMsgCount += 20;
		initHistoryMsg(historyMsgCount);
	}

	/**
	 * @Title: initHistoryMsg
	 * @Description: 初始化历史消息
	 * @param
	 * @return void
	 * @throws
	 */
	private void initHistoryMsg(int msgHistoryCount) {
		if (MyApplication.senderUser != null
				&& !"".equals(MyApplication.senderUser)
				&& MyApplication.myLoginState && MyApplication.gotyeState) { // 判断我的信息存不存在
			senderGotye = new GotyeUser(
					MyApplication.senderUser.getExtend_user_account());// 构建亲加对象
			MyApplication.gotyeApi.addChatListener(new ChatListener(
					ChatActivity.this, ptrListView, list, adapter, listView,
					MyApplication.senderUser, targertUser));// 聊天回调类
			// 拉取历史消息,第一个参数为拉取谁的历史消息，第二个参数为消息的id，第三个参数为拉取历史消息的条数，第四个参数为是否包含起始ID
			MyApplication.gotyeApi.getHistoryMessage(targetGotye, null,
					msgHistoryCount, false);
		} else {
			historyMsgCount -= 20;
			Toast.makeText(ChatActivity.this, "连接服务器失败...检查网络连接",
					Toast.LENGTH_LONG).show();
			// if (!MyApplication.verifyUserServiceRuning) {
			// startService(new Intent(ChatActivity.this,
			// VerifyUserService.class));
			// }
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		historyMsgCount += 20;
		// 下拉刷新的回调
		initHistoryMsg(historyMsgCount);

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// 上拉加载更多（本界面设定不支持上拉）
	}

	/***
	 * @Title: checkTargetAccount
	 * @Description: 校验上个页面传递过来的targertUser的啪啪号是否为空，如果为空则访问服务器取加载
	 * @param
	 * @return void
	 * @throws
	 */
	private void checkTargetAccount() {
		tvTitleName.setText(targertUser.getUser_nickname());
		if (targertUser != null && !"".equals(targertUser)) {
			if (targertUser.getUser_account() == null
					|| "".equals(targertUser.getUser_account())) { // 如果啪啪号为空，开启线程需获取这个人的用户信息*（根据聊天账号）
				if (NetWorkState.checkNet(ChatActivity.this)) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							String[] reuslt = MyHttpClient
									.postDataToService(
											ChatActivity.this,
											APPConstants.URL_APP2_HOST_NAME
													+ APPConstants.URL_GETEXTEND_INFOAPP2,
											MyApplication.app2Token,
											new String[] { "extend_user_account" },
											new String[] { com.alibaba.fastjson.JSONArray.toJSONString(new String[] { targertUser
													.getExtend_user_account() }) },
											null, null);
							// 得到返回的数据吗，发送handler去更新UI
							Message msg = new Message();
							msg.obj = reuslt;
							handler.sendMessage(msg);
						}
					}).start();
				}
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String[] result = (String[]) msg.obj;
			JsonObjectErrorDaoImpl.resolveJson(ChatActivity.this, result,
					new JsonObjectErrorDao() {

						@Override
						public void disposeJsonObj(JSONObject obj) {
							try {
								JSONObject obj1 = obj.getJSONObject(targertUser
										.getExtend_user_account());
								targertUser.setUser_account(obj1
										.optString("user_account"));
								targertUser.setUser_logo(obj1
										.optString("user_logo_200"));
								targertUser.setUser_nickname(obj1
										.optString("user_nickname"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
							adapter.notifyDataSetChanged();
						}
					});
			tvTitleName.setText(targertUser.getUser_nickname());
		};
	};

	/****************** ListView的业务逻辑End **************************************/

	/****************** 输入文本框的监听器逻辑与按住说话按钮的监听事件Begin ********************/
	/**
	 * @ClassName: EdTextchangeListener
	 * @Description: EditTextView 文字改变时候的监听器
	 * @author 菲尔普斯
	 * @date 2015-1-8 下午2:46:41
	 * 
	 */
	class EdTextchangeListener implements TextWatcher {
		// 文字变化之前
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			imgSendMessage
					.setImageResource(R.drawable.chat_message_send_unselect);
		}

		// 文字变化的时候
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (isTextInput) {
				if (s.toString() != null && !"".equals(s.toString())) {
					imgSendMessage
							.setImageResource(R.drawable.chat_message_send_select);
				}
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
		}

	}

	/**
	 * @ClassName: BtnVoiceOnTouchListener
	 * @Description: 按住说话按钮监听事件
	 * @author 菲尔普斯
	 * @date 2015-1-9 下午5:31:51
	 * 
	 */
	class BtnVoiceOnTouchListener implements OnTouchListener {
		private float changeY;
		private boolean isEnd = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				imgVoice.setBackgroundDrawable(animation);
				imgVoice.setVisibility(View.VISIBLE);
				animation.start();
				btnVoice.setText(R.string.loose_end_talk);
				startY = event.getY();
				// 开始发送语音,调用MyChatListeneron的StartTalkTo回调,false表示为语音短信模式（非抢麦模式，抢麦模式只能在聊天室用）
				MyApplication.gotyeApi.startTalkTo(targetGotye,
						WhineMode.DEFAULT, false, 60000);
				break;
			case MotionEvent.ACTION_MOVE:
				if (startY - event.getY() > height) { // 判断手指上滑状态
					animation.stop();
					imgVoice.setBackgroundResource(R.drawable.chat_voice_say_loosen);
					changeY = event.getY();
					isEnd = true;
				}
				if (isEnd) { // 判断手指下滑状态
					if (changeY - event.getY() < 0) {
						imgVoice.setBackgroundDrawable(animation);
						animation.start();
						isEnd = false;
					}
				}
				break;

			case MotionEvent.ACTION_UP:
				imgVoice.setVisibility(View.GONE);
				btnVoice.setText(R.string.hold_down_talk);
				animation.stop();
				if (startY - event.getY() > height && isEnd) { // 说明取消了发送语音
					Toast.makeText(ChatActivity.this, "取消发送语音了......",
							Toast.LENGTH_SHORT).show();
				} else { // 发送语音成功
					ChatListener.mVoiceSendTime = 0;
					// 停止录音,调用MyChatListeneron的onStopTalkTo
					MyApplication.gotyeApi.stopTalk();// 停止录音

				}
				break;
			}
			return true;
		}

	}

	/****************************** 输入文本框的监听器逻辑与按住说话按钮的监听事件End ***************************************/

	/****************************** 底部弹出popupWindows用来发送图片和拍照的业务逻辑功能 ****************************/

	/**
	 * @ClassName: PicPopupWindowOncick
	 * @Description: popupWidow用来显示发送图片的功能
	 * @author 菲尔普斯
	 * @date 2015-3-5 下午2:47:56
	 * 
	 */
	class PicPopupWindowOncick implements OnClickListener {

		@Override
		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				Toast.makeText(ChatActivity.this, "拍照", Toast.LENGTH_SHORT)
						.show();
				imgName = DataUtils.getCurrenTime();
				startCamare(APPConstants.STARTCAMERA_REQUESTCODE, imgName);
				break;
			case R.id.btn_pick_photo:
				takePic();
				break;
			default:
				break;
			}
		}

	}

	/**
	 * @Title: takePic
	 * @Description: 打开系统图库选取图片
	 * @param
	 * @return void
	 * @throws
	 */
	private void takePic() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		startActivityForResult(intent, APPConstants.TAKE_PIC_REQUESTCODE);
	}

	/***
	 * @Title: startCamare
	 * @Description: 打开系统相机,设定照片存储的路径和名字
	 * @param @param requestCode 请求码
	 * @param @param imgName 图片名字
	 * @return void
	 * @throws
	 */
	private void startCamare(int requestCode, String imgName) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri imageUri = Uri.fromFile(FileUtils.getImgFile(imgName));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == APPConstants.STARTCAMERA_REQUESTCODE) { // 如果是相机返回的照片
			File cameraTmp = new File(FileUtils.getImgPath(imgName));
			if (cameraTmp.exists()) {
				if (cameraTmp.length() > 0) {
					if (handlePic(Uri.fromFile(cameraTmp), 0)) {
						return;
					}
				}
			}
		} else if (requestCode == APPConstants.TAKE_PIC_REQUESTCODE) { // 图库返回的照片
			if (data != null && data.getData() != null
					& !"".equals(data.getData())) {
				Uri originalUri = data.getData();
				if (handlePic(originalUri, 0)) {
					return;
				}
			}
		}
	}

	// 获取图片失败的操作

	/***
	 * @Title: handlePic
	 * @Description:获得图片对图片进行处理
	 * @param @param originalUri
	 * @param @param degree
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	private boolean handlePic(Uri originalUri, int degree) {
		if (originalUri != null && !"".equals(originalUri)) {
			// 照片的原始资源地址
			WindowManager wm = (WindowManager) this
					.getSystemService(Context.WINDOW_SERVICE);
			int width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
			int height = wm.getDefaultDisplay().getHeight();// 屏幕高度
			UriImage uriImage = new UriImage(this, originalUri);
			byte[] imageData = uriImage.getResizedImage(width, height,
					10240 * 5);
			// 使用ContentProvider通过URI获取原始图片
			Bitmap photo = BitmapFactory.decodeByteArray(imageData, 0,
					imageData.length);
			photo = com.gotye.api.utils.ImageUtils.ratoteBitmap(photo, degree);
			if (photo != null) {
				// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
				// 释放原始图片占用的内存，防止out of memory异常发生
				sendImageMessage(photo); // 发送图片
				return true;
			}
		}
		return false;
	}

	/**************************** 底部弹出popupWindows用来发送图片和拍照的业务逻辑功能End **********************************/

	/******************************** 底部GridView小表情业务逻辑部分Begin *********************************/

	/***
	 * @Title: initViewpager
	 * @Description:初始化底部表情ViewPager适配器
	 * @param
	 * @return void
	 * @throws
	 */
	private void initViewpager() {
		expressNames = getResources().getStringArray(R.array.smiley_array);
		expressIds = MyApplication.smileParserUtils.DEFAULT_SMILEY_RES_IDS;
		gridViewList = new ArrayList<>();
		int size = expressIds.length;
		if (size == 0) {
			return;
		}
		// 確定viewpager的页数
		int len = size / 9;
		if (size % 9 == 0) {
			expressPage = len;
		} else {
			expressPage = len + 1;
		}
		viewPager.setAdapter(new ChatPagerAdapter());
		initdotPoint();
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				for (int i = 0; i < dots.length; i++) {
					if (position == i) {
						dots[i].setImageResource(R.drawable.page);
					} else {
						dots[i].setImageResource(R.drawable.page_now);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	/**
	 * @Title: initdotPoint
	 * @Description: 初始化底部表情的页数显示的小圆点
	 * @param
	 * @return void
	 * @throws
	 */
	private void initdotPoint() {
		dots = new ImageView[expressPage];
		for (int i = 0; i < dots.length; i++) {
			ImageView dotView = new ImageView(ChatActivity.this);
			// dotView.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
			dotView.setPadding(20, 0, 20, 0);
			dots[i] = dotView;
			if (i == 0) {
				dotView.setImageResource(R.drawable.page);
			} else {
				dotView.setImageResource(R.drawable.page_now);
			}
			dotLayout.addView(dots[i]);
		}
	}

	/**
	 * @Title: imgExpressionOnclick
	 * @Description: 底部弹出表情布局
	 * @param @param v
	 * @return void
	 * @throws
	 */
	@OnClick({ R.id.chat_img_expression })
	public void imgExpressionOnclick(View v) {
		llyout.setVisibility(View.VISIBLE);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
		listView.setSelection(listView.getAdapter().getCount() - 1);
	}

	/***
	 * @ClassName: ChatPagerAdapter
	 * @Description: 底部表情ViewPager的适配器
	 * @author 菲尔普斯
	 * @date 2015-3-3 下午3:23:27
	 * 
	 */
	class ChatPagerAdapter extends PagerAdapter {

		public ChatPagerAdapter() {
			for (int i = 0; i < expressPage; i++) { // 循环创建GridView。根据expressPage页数来决定
				GridView gridView = new GridView(context);
				gridView.setNumColumns(3);
				gridView.setAdapter(new ChatGridViewAdapter(i));
				gridViewList.add(gridView);
			}
		}

		@Override
		public int getCount() {
			return expressPage;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = gridViewList.get(position);
			container.addView(view);
			return view;
		}

	}

	/**
	 * @ClassName: ChatGridViewAdapter
	 * @Description: ViewPager中嵌套的GridView的适配器
	 * @author 菲尔普斯
	 * @date 2015-3-3 下午3:23:52
	 * 
	 */
	class ChatGridViewAdapter extends BaseAdapter {
		private ViewHolder holder = null;
		int imgIds[];

		public ChatGridViewAdapter(int page) {
			imgIds = new int[9];
			for (int i = page * 9; i < page * 9 + 9; i++) { // 判断当前页数，如果是第0页，就直接让这个GridView的适配数据的下标的值=expressIds【i】的值，否则要用i-page*9，才能是下标对应上
				if (i == expressIds.length)
					break;
				if (page > 0) {
					imgIds[i - page * 9] = expressIds[i];
				} else {
					imgIds[i] = expressIds[i];
				}
			}
		}

		@Override
		public int getCount() {
			return imgIds == null ? 0 : imgIds.length;
		}

		@Override
		public Object getItem(int position) {
			return imgIds == null ? null : imgIds[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.gridview_chat_expression, parent, false);
				holder = new ViewHolder();
				holder.img = (ImageView) convertView
						.findViewById(R.id.gridview_img);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.img.setImageResource(imgIds[position]);
			holder.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					etMessage.setText(MyApplication.smileParserUtils
							.strToSmiley(etMessage.getText().toString()
									+ expressNames[viewPager.getCurrentItem()
											* 9 + position], 64, 64));
					etMessage.setSelection(etMessage.getText().toString()
							.length());
				}
			});
			return convertView;
		}

		class ViewHolder {
			private ImageView img;
		}
	}

	/************** 底部GridView小表情业务逻辑部分End **********************************/

	/************** 调用亲加，发送聊天消息的业务逻辑Begin **********************************/

	/**
	 * @Title: sendTextMsg
	 * @Description: 发送普通文本消息
	 * @param @param msg
	 * @return void
	 * @throws
	 */
	private void sendTextMsg(String msg) {
		// 构建亲加消息对象，第一个参数为消息的ID，第二个参数为消息时间，第三个参数为发送对象，第四个参数为发送者
		GotyeTextMessage textMsg = new GotyeTextMessage(UUID.randomUUID()
				.toString(), TimeUtil.getCurrentTime(), targetGotye,
				senderGotye);
		textMsg.setText(msg); // 设置消息对象的文本内容
		MyApplication.gotyeApi.sendMessageToTarget(textMsg); // 发送消息，会触发ChatListener的onSendMessage回调
		etMessage.setText("");
	}

	/**
	 * @Title: sendImageMessage
	 * @Description: 发送图片信息
	 * @param @param imgData
	 * @return void
	 * @throws
	 */
	private void sendImageMessage(Bitmap bitmap) {
		GotyeImageMessage imageMessage = new GotyeImageMessage(UUID
				.randomUUID().toString(), TimeUtil.getCurrentTime(),
				targetGotye, senderGotye);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 80, bout);
		imageMessage.setImageData(bout.toByteArray());
		MyApplication.gotyeApi.sendMessageToTarget(imageMessage);
	}
	/************** 调用亲加，发送聊天消息的业务逻辑End **********************************/
}
