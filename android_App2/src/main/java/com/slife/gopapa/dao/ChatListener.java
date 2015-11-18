package com.slife.gopapa.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;

import com.gotye.api.GotyeChatListener;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.bean.GotyeMessage;
import com.gotye.api.bean.GotyeTargetable;
import com.gotye.api.bean.GotyeVoiceMessage;
import com.gotye.api.media.WhineMode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.slife.gopapa.activity.news.ChatActivity;
import com.slife.gopapa.adapter.ChatAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.model.MyChatMessage;
import com.slife.gopapa.model.ChatUserInfo;
import com.slife.gopapa.utils.ListUtils;
import com.slife.gopapa.utils.ModelChangeUtils;

/***
* @ClassName: ChatListener 
* @Description: 聊天界面实现亲加聊天回调的类
* @author 菲尔普斯
* @date 2015-1-22 上午10:31:44 
*
 */
public class ChatListener implements GotyeChatListener{

	private Context context; //上下文对象
	private PullToRefreshListView ptrListView;
	private List<MyChatMessage> list; //ListView的数据源
	private ChatAdapter adapter; //适配器
	private ListView listView;  //列表控件
	private ChatUserInfo senderUser; //发送者
	private ChatUserInfo targertUser; //发送目标
	public static  long mVoiceSendTime=0;// 计算按两次发送语音时间间隔
	
	public ChatListener(Context context,PullToRefreshListView ptrListView, List<MyChatMessage> list,
			ChatAdapter adapter, ListView listView,ChatUserInfo senderUser,ChatUserInfo targertUser) {
		this.context = context;
		this.ptrListView=ptrListView;
		this.list = list;
		this.adapter = adapter;
		this.listView = listView;
		this.senderUser=senderUser;
		this.targertUser=targertUser;
	}

	// 历史消息
	@Override
	public void onGetHistoryMessages(String appKey, String username,
			GotyeTargetable target, String msgID, List<GotyeMessage> msgs,
			boolean contain, int code) {
		List<MyChatMessage> listMsg = new ArrayList<>();
		for(int i=0;i<msgs.size();i++){
			listMsg.add(ModelChangeUtils.GotyeToMyChat(msgs.get(i), senderUser, targertUser));
		}
		
		if(ChatActivity.historyMsgCount>20){
			list.clear();
			list.addAll(ListUtils.ReverseOrder(listMsg));
			listView.setSelection(listView.getAdapter().getCount()-20);
			adapter.notifyDataSetChanged();
		}else{
			list.addAll(ListUtils.ReverseOrder(listMsg));
			adapter.notifyDataSetChanged();
			listView.setSelection(listView.getAdapter().getCount()-1);
		}
		ptrListView.onRefreshComplete();

	}

	// 离线消息
	@Override
	public void onGetOfflineMessage(String appKey, String username,
			List<GotyeMessage> msgs, int code) {
	}

	// 收到消息
	@Override
	public void onReceiveMessage(String appKey, String username,
			GotyeMessage message) {
		MyChatMessage mMsg=ModelChangeUtils.GotyeToMyChat(message, senderUser, targertUser);
		if(message.getSender().getUsername().equals(targertUser.getExtend_user_account())){//表示是当前和正在聊天的人的消息
		list.add(mMsg);
		adapter.notifyDataSetChanged();
		listView.setSelection(listView.getAdapter().getCount()-1);
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
		if (MyApplication.targetUser != null&& message.getSender().getUsername().equals(senderUser.getExtend_user_account())) { // 如果这条消息恰好是和我正在聊天的人的消息，则不处理，由ChatListener去处理
			if (status == GotyeStatusCode.STATUS_OK) {	//说明发送成功
				// 将数据加载到列表，通知适配器发生改变
				MyChatMessage mChatMsg = ModelChangeUtils.GotyeToMyChat(message, senderUser, targertUser);
				list.add(mChatMsg);
				handler.sendEmptyMessage(0);
			}else{
				Toast.makeText(context, "网络连接失败，请稍后重新发送", Toast.LENGTH_SHORT).show();
			}
		}
	}
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			adapter.notifyDataSetChanged();
			listView.setSelection(listView.getAdapter().getCount()-1);
		};
	};

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
		if (code == GotyeStatusCode.STATUS_VOICE_TIME_UP) {
			Toast.makeText(context, "录音时间到", Toast.LENGTH_SHORT).show();
		} else if (!isRealTime) { // 如果不是抢麦模式（语音短信模式）
			// 判断状态码，以及录音时间，和声音为不为空
			if ((code == GotyeStatusCode.STATUS_OK || code == GotyeStatusCode.STATUS_VOICE_TIME_UP)
					&& voiceMessage != null) {
				if (duration < 1000) { // 判断录音时间
					Toast.makeText(context, "录音时间过短", Toast.LENGTH_SHORT).show();
				} else {
						if (MyApplication.targetUser != null&& voiceMessage.getSender().getUsername().equals(senderUser.getExtend_user_account())) { // 如果这条消息恰好是和我正在聊天的人的消息，则不处理，由ChatListener去处理
							long nowTime=System.currentTimeMillis();
							if(mVoiceSendTime==0||nowTime-mVoiceSendTime>1000){
								mVoiceSendTime=nowTime;
								MyApplication.gotyeApi.sendMessageToTarget(voiceMessage); // 发送语音
								
							}
						}
					}
			} else {
				Toast.makeText(context, "录音错误", Toast.LENGTH_SHORT).show();
			}
		} else if (code == GotyeStatusCode.STATUS_NETWORK_DISCONNECTED) { // 出现网络错误
			Toast.makeText(context, "实时语音出现网络断开", Toast.LENGTH_SHORT).show();
		}
	}

	// 实时语音结束回调
	@Override
	public void onVoiceMessageEnd(String appKey, String username,
			GotyeTargetable sender, GotyeTargetable target3) {
	}

}
