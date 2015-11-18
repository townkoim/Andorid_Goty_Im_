package com.slife.gopapa.model;

import java.io.ByteArrayOutputStream;
/***
* @ClassName: ChatVoiceMessage 
* @Description: 声音消息实体类(MyChatMessage子类之一)
* @author 菲尔普斯
* @date 2015-1-16 上午10:08:14 
*
 */
public class ChatVoiceMessage extends MyChatMessage {
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	private static final long serialVersionUID = 1178656414847272899L;
	private String downloadUrl;	//声音下载地址
	private long duration; //下载时长
	private ByteArrayOutputStream voiceData;//声音流
	
	
	
	public ChatVoiceMessage() {
		super();
	}

	public ChatVoiceMessage(String downloadUrl, long duration,
			ByteArrayOutputStream voiceData) {
		super();
		this.downloadUrl = downloadUrl;
		this.duration = duration;
		this.voiceData = voiceData;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public ByteArrayOutputStream getVoiceData() {
		return voiceData;
	}

	public void setVoiceData(ByteArrayOutputStream voiceData) {
		this.voiceData = voiceData;
	}
	
	
	
}
