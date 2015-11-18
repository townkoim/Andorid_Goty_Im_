package com.slife.gopapa.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.gotye.api.GotyeChatListener;
import com.gotye.api.bean.GotyeMessage;
import com.gotye.api.bean.GotyeTargetType;
import com.gotye.api.bean.GotyeTargetable;
import com.gotye.api.bean.GotyeVoiceMessage;
import com.gotye.api.media.WhineMode;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.AppManager;
import com.slife.gopapa.database.DBHelperOperation;
import com.slife.gopapa.model.RecentNews;
import com.slife.gopapa.utils.ListUtils;
import com.slife.gopapa.utils.ModelChangeUtils;

/***
 * @ClassName: ChatService
 * @Description: 亲加聊天的全局后台服务(拉取离线消息，接收在线消息)
 * @author 菲尔普斯
 * @date 2015-1-28 下午4:10:28
 * 
 */
public class ChatService extends Service implements GotyeChatListener {
	private Handler  handler;
	@Override
	public void onCreate() {
		super.onCreate();
			AppManager.addService(ChatService.this);
		
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if(MyApplication.gotyeApi!=null&&MyApplication.gotyeState&&!"".equals(MyApplication.gotyeApi)){
			MyApplication.gotyeApi.addChatListener(ChatService.this);
			// 拉取离线的消息
					MyApplication.gotyeApi.getOfflineMsg(GotyeTargetType.GOTYE_USER, null,Integer.MAX_VALUE);
			}else{
					//重新登录服务器
//					getApplicationContext().startService(new Intent(ChatService.this,VerifyUserService.class));
//					ChatService.this.stopSelf();
			}
	}
	

	// 历史消息
	@Override
	public void onGetHistoryMessages(String appKey, String username,
			GotyeTargetable target, String msgID, List<GotyeMessage> msgs,
			boolean contain, int code) {
	}

	// 离线消息
	@Override
	public void onGetOfflineMessage(String appKey, String username,
			List<GotyeMessage> msgs, int code) {
		List<RecentNews> list = new ArrayList<>();
		if (msgs != null && msgs.size() > 0) {
			if(MyApplication.voiceUtils!=null){	//播放声音
				MyApplication.voiceUtils.playSound();
			}
			for (int i = 0; i < msgs.size(); i++) { // 循环将离线消息转换成自己的对象
				RecentNews news = ModelChangeUtils.GotyeToRecentNews(msgs.get(i));
				list.add(news);
			}
			// 离线消息，要先插入到数据库
			List<RecentNews> newsList = ListUtils.removeRepeatData(list);
			for (int i = 0; i < newsList.size(); i++) {
				RecentNews news = newsList.get(i);
				// 首先得根据聊天发送者的聊天账号和我的啪啪号去数据库查询，看是否存在这条书库，如果数据存在，则只要修改数据库的信息即可
				if (DBHelperOperation.queryRecentContactByExtendAccount(news
						.getUser_extend_account())) {
					DBHelperOperation.updateRecentContact(news);
				} else {
					DBHelperOperation.insertRecentContact(news);
				}
			}
			// 判断NewsFragment界面是否已经初始化了。（只有初始化了才能发送广播，它才能接受到广播）
			if (MyApplication.isInitNewsFragment) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("recent_news_list",
						(Serializable) newsList);
				intent.setAction(APPConstants.ACTION_OFFLINE_MESSAGE);
				intent.putExtras(bundle);
				sendBroadcast(intent);
			}
			initUnreadMsgCount(msgs.size());
		}
	}

	// 收到消息
	@Override
	public void onReceiveMessage(String appKey, String username,
			GotyeMessage message) {
		if (MyApplication.targetUser != null
				&& message
						.getSender()
						.getUsername()
						.equals(MyApplication.targetUser
								.getExtend_user_account())) { // 如果这条消息恰好是和我正在聊天的人的消息，则不处理，由ChatListener去处理

		} else { // 后台处理，发送广播到前台
			// 构建一个RencentNews对象
			if(MyApplication.voiceUtils!=null){	//播放声音
				MyApplication.voiceUtils.playSound();
			}
			RecentNews news = ModelChangeUtils.GotyeToRecentNews(message);
			// 收到消息，首先要插入数据库
			if (DBHelperOperation.queryRecentContactByExtendAccount(news
					.getUser_extend_account())) { // 数据库已经存在修改数据
				DBHelperOperation.updateRecentContact(news);
			} else { // 插入数据
				DBHelperOperation.insertRecentContact(news);
			}
			initUnreadMsgCount(1);
			if (MyApplication.isInitNewsFragment) { // 表示当前已经初始化了,只要发送广播给消息界面数据增加了一条
				Intent intent = new Intent();
				intent.putExtra("recent_news", news);
				intent.setAction(APPConstants.ACTION_ONLINE_MESSAGE);
				sendBroadcast(intent);
			}
		}
	}

	// 收到实时语音回调
	@Override
	public void onReceiveVoiceMessage(String appKey, String username,
			GotyeTargetable sender, GotyeTargetable target) {
	}

	// 发送消息回调
	@Override
	public void onSendMessage(String appKey, String username,
			GotyeMessage message, int status) {
//		if (MyApplication.targetUser != null&&!message.getSender().getUsername()
//						.equals(MyApplication.targetUser.getExtend_user_account())) { // 如果这条消息恰好是和我正在聊天的人的消息，则不处理，由ChatListener去处理
//			
//		} else {
//			if (status == GotyeStatusCode.STATUS_OK) { // 说明发送成功
//			} else if (status == GotyeStatusCode.STATUS_FORBIDDEN_SEND_MSG) {
//				Toast.makeText(ChatService.this, "已被禁言", Toast.LENGTH_SHORT)
//						.show();
//			} else if (status == GotyeStatusCode.STATUS_USER_NOT_EXISTS) {
//				Toast.makeText(ChatService.this, "用户不存在", Toast.LENGTH_SHORT)
//						.show();
//			} else if (status == GotyeStatusCode.STATUS_SEND_MSG_TO_SELF) {
//				Toast.makeText(ChatService.this, "不能向自己发送消息  " + status,
//						Toast.LENGTH_SHORT).show();
//			} else if (status == GotyeStatusCode.STATUS_NOT_IN_ROOM) {
//				Toast.makeText(ChatService.this, "您不在房间，发送失败  " + status,
//						Toast.LENGTH_SHORT).show();
//			}
//		}
	}

	// 开始录音回调
	@Override
	public void onStartTalkTo(String appKey, String username,
			GotyeTargetable target, WhineMode mode, boolean isRealTime) {
	}

	// 停止录音回调
	@Override
	public void onStopTalkTo(String appKey, String username,
			GotyeTargetable target, WhineMode mode, boolean isRealTime,
			GotyeVoiceMessage voiceMessage, long duration, int code) {
	}

	// 实时语音结束回调
	@Override
	public void onVoiceMessageEnd(String appKey, String username,
			GotyeTargetable sender, GotyeTargetable target3) {
	}

	/**
	 * @Title: initUnreadMsgCount
	 * @Description: 发送未读的消息给tabActivity去控制小红点
	 * @param
	 * @return void
	 * @throws
	 */
	private void initUnreadMsgCount(final int count) {
		handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				MyApplication.trackPointMsgCount = MyApplication.trackPointMsgCount+ count;
				if (MyApplication.isTabActivity) {
					Intent intent = new Intent();
					intent.setAction(APPConstants.ACTION_TRACKPOINT_CHANGE);
					sendBroadcast(intent);
				}
			}
		});
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
