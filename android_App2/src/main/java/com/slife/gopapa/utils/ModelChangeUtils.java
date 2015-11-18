package com.slife.gopapa.utils;

import com.gotye.api.bean.GotyeImageMessage;
import com.gotye.api.bean.GotyeMessage;
import com.gotye.api.bean.GotyeRichTextMessage;
import com.gotye.api.bean.GotyeTextMessage;
import com.gotye.api.bean.GotyeVoiceMessage;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.model.ChatImageMessage;
import com.slife.gopapa.model.ChatRichTextMessage;
import com.slife.gopapa.model.ChatTextMessage;
import com.slife.gopapa.model.ChatVoiceMessage;
import com.slife.gopapa.model.MyChatMessage;
import com.slife.gopapa.model.ChatUserInfo;
import com.slife.gopapa.model.RecentNews;

/***
 * @ClassName: ModelChangeUtils
 * @Description: 实体转换类,把亲加的实体类以及所包含的信息全部转换为自己的实体类或者集合
 * @author 菲尔普斯
 * @date 2015-2-2 上午10:30:03
 * 
 */
public class ModelChangeUtils {
	/***
	 * @Title: GotyeToMyChat
	 * @Description: TODO GotyeMessage 转MyChatMessage
	 * @param @param gMSG 亲加对象
	 * @param @param senderUser 发送者
	 * @param @param targertUser 发送目标
	 * @param @return
	 * @return MyChatMessage 返回我的消息的实体类
	 * @throws
	 */
	public static MyChatMessage GotyeToMyChat(GotyeMessage gMSG,
			ChatUserInfo senderUser, ChatUserInfo targertUser) {
		MyChatMessage mctMsg = null;
		if (gMSG instanceof GotyeTextMessage) { // 文本消息
			mctMsg = new ChatTextMessage();
			((ChatTextMessage) mctMsg).setText(((GotyeTextMessage) gMSG)
					.getText());
		} else if (gMSG instanceof GotyeRichTextMessage) {
			mctMsg = new ChatRichTextMessage();
			((ChatRichTextMessage) mctMsg)
					.setRichText(((GotyeRichTextMessage) gMSG).getRichText());
		} else if (gMSG instanceof GotyeVoiceMessage) {
			mctMsg = new ChatVoiceMessage();
			((ChatVoiceMessage) mctMsg).setVoiceData(((GotyeVoiceMessage) gMSG)
					.getVoiceData());
			((ChatVoiceMessage) mctMsg)
					.setDownloadUrl(((GotyeVoiceMessage) gMSG).getDownloadUrl());
			((ChatVoiceMessage) mctMsg).setDuration(((GotyeVoiceMessage) gMSG)
					.getDuration());
		}else if(gMSG instanceof GotyeImageMessage){
			mctMsg = new ChatImageMessage();
			((ChatImageMessage) mctMsg).setDownLoadUrl(((GotyeImageMessage) gMSG).getDownloadUrl());
			((ChatImageMessage) mctMsg).setImageData(((GotyeImageMessage) gMSG).getImageData());
			((ChatImageMessage) mctMsg).setThumbnailData(((GotyeImageMessage) gMSG).getThumbnailData());
		}
		mctMsg.setCreareTime(gMSG.getCreateTime());
		mctMsg.setExtraData(gMSG.getExtraData());
		mctMsg.setMessageID(gMSG.getMessageID());
		mctMsg.setRecordID(gMSG.getRecordID());
		if (gMSG.getSender().getUsername()
				.equals(senderUser.getExtend_user_account())) { // 如果gotyeUser的sender发送者的userName和senerUser的extend_user_account一样，说明自己是发送者
			mctMsg.setSender(senderUser);
			mctMsg.setTarget(targertUser);
		} else {
			mctMsg.setSender(targertUser);
			mctMsg.setTarget(senderUser);
		}
		return mctMsg;
	}

	/**
	 * @Title: GotyeToRecentNews
	 * @Description: 根据秦加的GotyeMessage，转换成自己的消息对象RecentNews
	 * @param @param message
	 * @param @return
	 * @return RecentNews
	 * @throws
	 */
	public static RecentNews GotyeToRecentNews(GotyeMessage message) {
		String msg = null;
		if (message instanceof GotyeTextMessage) {
			msg = ((GotyeTextMessage) message).getText();
		} else if (message instanceof GotyeVoiceMessage) {
			msg = "voice";
		} else if (message instanceof GotyeRichTextMessage) {
			String richText = new String(
					((GotyeRichTextMessage) message).getRichText());
			if (richText.equals(APPConstants.RICHTEXT_ADDFRIENDS_SUCCESS)) {
				msg = "richtext";
			}
		}else if( message instanceof GotyeImageMessage){
			msg="imagetext";
		}
		RecentNews news = new RecentNews();
		news.setUser_myacount(MyApplication.senderUser.getUser_account());
		news.setUser_extend_account(message.getSender().getUsername());
		news.setUser_last_message(msg);
		news.setMsgCount(1);
		news.setTime(String.valueOf(System.currentTimeMillis()));
		return news;
	}

}
