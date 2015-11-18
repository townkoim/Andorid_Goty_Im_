package com.slife.gopapa.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gotye.api.GotyeProgressListener;
import com.gotye.api.GotyeStreamPlayListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.news.ImageDetailActivity;
import com.slife.gopapa.activity.ranking.PersonInformationActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.model.ChatImageMessage;
import com.slife.gopapa.model.ChatRichTextMessage;
import com.slife.gopapa.model.ChatTextMessage;
import com.slife.gopapa.model.ChatVoiceMessage;
import com.slife.gopapa.model.MyChatMessage;
import com.slife.gopapa.utils.DataUtils;
import com.slife.gopapa.utils.FileUtils;
import com.slife.gopapa.utils.SmileyParserUtil;
import com.slife.gopapa.view.XCRoundRectImageView;

/**
* @ClassName: ChatAdapter 
* @Description:聊天适配器（根据list.get(position).getSender().getExtend_user_account()发送者的聊天账号和自己的聊天账号进行配对。如果匹配的上说明是自己发送的消息，则加载右边的布局。反之，加载左边的布局
* 			此处需要重写BaseAdapter的getItemViewType（）和 getViewTypeCount（）方法），实现了GotyeProgressListener（下载语音的接口），GotyeStreamPlayListener（播放语音的接口）
* @author 菲尔普斯
* @date 2015-1-8 下午5:27:35 
*
 */
public class ChatAdapter extends BaseAdapter implements GotyeProgressListener,GotyeStreamPlayListener{
	private class ViewHolder {
		@ViewInject(R.id.chat_items_time)
		private TextView tvTime;
		@ViewInject(R.id.chat_items_icon_accept)
		private XCRoundRectImageView iconAccept;
		@ViewInject(R.id.chat_items_icon_send)
		private XCRoundRectImageView iconSend;
		@ViewInject(R.id.chat_items_text)
		private TextView tvMessage;
		@ViewInject(R.id.chat_items_voice)
		private ImageView imgVoice;
		@ViewInject(R.id.chat_items_image)
		private ImageView imgImage;
	}
	private Context context;//上下文对象
	private List<MyChatMessage>list; //数据集合
	private SmileyParserUtil smileParserUtils;//表情
	
	
	private ViewHolder holder;
	private boolean isSender=false;//是否自己发送
	private boolean voiceSener=false;
	
	private AnimationDrawable animation=null;// 动画
	private InputStream inputStream;//语音流
	private ImageView imgAnimation;//动画控件

	public ChatAdapter(Context context,List<MyChatMessage>list) {
		super();
		this.context = context;
		this.list=list;
		this.smileParserUtils=MyApplication.smileParserUtils;
	}
	//消息类型 
	public interface IMsgViewType {
		int SEND_MESSAGE = 0; // 发送消息
		int ACCEPT_MESSAGE = 1; // 接收消息
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	//根据不同的类型来返回不用的布局
	@Override
	public int getItemViewType(int position) {
		if (list.get(position).getSender().getExtend_user_account().equals(MyApplication.senderUser.getExtend_user_account())) {
			return IMsgViewType.SEND_MESSAGE;
		}else{
		return IMsgViewType.ACCEPT_MESSAGE;
		}
	}
	//返回的布局的总数为2
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final MyChatMessage mcMsg =list.get(position);	//得到MyChatMessage对象
		//根据getItemViewType判断当前是加载的那个布局对象，来设置isSender的值，true表示发送的消息，false表示接受的消息
		if(getItemViewType(position)==IMsgViewType.SEND_MESSAGE){		
			isSender=true;
		}else if(getItemViewType(position)==IMsgViewType.ACCEPT_MESSAGE){
			isSender=false;
		}
		//重用convertView
		if (convertView == null) {
			if (!isSender) { 
				convertView = LayoutInflater.from(context).inflate(R.layout.listview_chat_accept_message, parent, false);
			} else {
				convertView = LayoutInflater.from(context).inflate(R.layout.listview_chat_send_message, parent, false);
			}
			holder = new ViewHolder();
			ViewUtils.inject(holder, convertView);
			//为要重用的布局设置tag标签
			convertView.setTag(holder);	
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position>0){
			if(mcMsg.getCreareTime()-list.get(position-1).getCreareTime()>600){
				//设置界面的时间显示
				holder.tvTime.setVisibility(View.VISIBLE);
				holder.tvTime.setText(DataUtils.getGotyTime(mcMsg.getCreareTime()));
			}else{
				holder.tvTime.setVisibility(View.GONE);
			}
		}else{
			//设置界面的时间显示
			holder.tvTime.setVisibility(View.VISIBLE);
			holder.tvTime.setText(DataUtils.getGotyTime(mcMsg.getCreareTime()));
		}
		holder.tvMessage.setVisibility(View.GONE);
		holder.imgVoice.setVisibility(View.GONE);
		holder.imgImage.setVisibility(View.GONE);
		if(isSender){	//如果是发送
			String logoURLSender=mcMsg.getSender().getUser_logo();		//头像
			if(logoURLSender!=null&&!"".equals(logoURLSender)&&!"null".equals(logoURLSender)){
				MyApplication.bitmapUtils.display(holder.iconSend, logoURLSender);
				holder.iconSend.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context,PersonInformationActivity.class);
						intent.putExtra("by_user_account", mcMsg.getSender().getUser_account());
						context.startActivity(intent);
					}
				});
			}
			if(mcMsg instanceof ChatTextMessage){	//普通文本消息
				holder.tvMessage.setVisibility(View.VISIBLE);
				holder.tvMessage.setText(smileParserUtils.strToSmiley(((ChatTextMessage) mcMsg).getText(),64,64));
			}else if(mcMsg instanceof ChatVoiceMessage){	//语音消息
				holder.imgVoice.setVisibility(View.VISIBLE);
				holder.imgVoice.setImageResource(R.drawable.chat_voice_play05);
			}else if(mcMsg instanceof ChatRichTextMessage){//自定义消息
				holder.tvMessage.setVisibility(View.VISIBLE);
				holder.tvMessage.setText("系统消息：好友请求");
			}else if(mcMsg instanceof ChatImageMessage){
				holder.imgImage.setVisibility(View.VISIBLE);
				holder.imgImage.setImageBitmap(BitmapFactory.decodeByteArray(
						((ChatImageMessage) mcMsg).getThumbnailData(), 0,
						((ChatImageMessage) mcMsg).getThumbnailData().length));
			}
			
		}else{		//接收消息
			String logoURLTarget=mcMsg.getSender().getUser_logo();		//头像
			if(logoURLTarget!=null&&!"".equals(logoURLTarget)&&!"null".equals(logoURLTarget)){
				MyApplication.bitmapUtils.display(holder.iconAccept, logoURLTarget);
				//头像按钮监听器
				holder.iconAccept.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context,PersonInformationActivity.class);
						intent.putExtra("by_user_account", mcMsg.getSender().getUser_account());
						context.startActivity(intent);
					}
				});
			}
			if(mcMsg instanceof ChatTextMessage){//普通文本消息
				holder.tvMessage.setVisibility(View.VISIBLE);
				holder.tvMessage.setText(smileParserUtils.strToSmiley(((ChatTextMessage) mcMsg).getText(),64,64));
			}else if(mcMsg instanceof ChatVoiceMessage){	//语音消息
				holder.imgVoice.setVisibility(View.VISIBLE);
				holder.imgVoice.setImageResource(R.drawable.chat_voice_play01);
			}else if(mcMsg instanceof ChatRichTextMessage){//自定义消息
				holder.tvMessage.setVisibility(View.VISIBLE);
				holder.tvMessage.setText("系统消息：好友请求");
			}else if(mcMsg instanceof ChatImageMessage){
				holder.imgImage.setVisibility(View.VISIBLE);
				holder.imgImage.setImageBitmap(BitmapFactory.decodeByteArray(
						((ChatImageMessage) mcMsg).getThumbnailData(), 0,
						((ChatImageMessage) mcMsg).getThumbnailData().length));
			}
		}


		//声音按钮监听器
		holder.imgVoice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(animation!=null){
					if(animation.isRunning()){
						animation.stop();
					}
					animation=null;
					voiceSener=false;
				}
						if(list.get(position).getSender().getExtend_user_account().equals(MyApplication.senderUser.getExtend_user_account())){	//如果是自己发送的消息就加载发送消息的语音动画
							voiceSener=true;
							animation=(AnimationDrawable) context.getResources().getDrawable(R.anim.chat_voice_playing_send);
						}else{
							animation=(AnimationDrawable) context.getResources().getDrawable(R.anim.chat_voice_playing_accept);
							voiceSener=false;
						}
						imgAnimation=(ImageView) v;
						imgAnimation.setImageDrawable(animation);
						//动画配置完毕，开始播放语音
						playVoice(String.valueOf(mcMsg.getCreareTime()),((ChatVoiceMessage)mcMsg).getDownloadUrl());
					}
		});
		//图片点击事件
		holder.imgImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,ImageDetailActivity.class);
				intent.putExtra("chatMessage", list.get(position));
				context.startActivity(intent);
			}
		});
		isSender=false;
		return convertView;
	}
	/**
	 * @Title: playVoice
	 * @Description: 播放语音
	 * @param @param voiceName
	 * @return void
	 * @throws
	 */
	public void playVoice(String voiceName, String voiceUrl) {
		String voicePath = FileUtils.getVoicePath(voiceName);	//先从本地得到语音文件的路径
		inputStream= FileUtils.getVoiceStream(voicePath);     //根据文件的路径去加载本地语音文本得到输出流
		if (inputStream != null) { 						      //如果流 为空，表示这个语音还没下载过，则去下载语音，下载过则直接播放，回调给GotyeStreamPlayListener
			MyApplication.gotyeApi.startPlayStream(inputStream, ChatAdapter.this);
		} else { // 去下载录音文件
			try {
				MyApplication.gotyeApi.downloadRes(voiceUrl,voicePath,this);//下载语音，回调为GotyeProgressListener
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	@Override
	public void onPlayStart() {
			animation.start();
	}

	@Override
	public void onPlayStop() {
		if(animation!=null){
			animation.stop();
			if(voiceSener){
				handler.sendEmptyMessage(1);
			}else{
				handler.sendEmptyMessage(2);
			}
		}
		if(inputStream!=null){
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(imgAnimation!=null){
				if(msg.what==2){	//语音播放完毕之后设置控件显示图片
				imgAnimation.setImageResource(R.drawable.chat_voice_play01);
				}else if(msg.what==1){
					imgAnimation.setImageResource(R.drawable.chat_voice_play05);
				}
				imgAnimation.clearAnimation();
				animation=null;
				imgAnimation=null;
				voiceSener=false;
			}
		};
	};

	@Override
	public void onPlaying(float arg0) {
			
	}

	//下载语音结束
		@Override
		public void onDownloadRes(String appKey, String username,
				String dowrlnloadUrl, String path, int code) {
			inputStream=FileUtils.getVoiceStream(path);
			MyApplication.gotyeApi.startPlayStream(inputStream, ChatAdapter.this);
		}

		@Override
		public void onProgressUpdate(String appKey, String username, String resID,
				String path, long totalByte, long downloadByte, byte[] data,
				int offset, int len) {
		}

}
