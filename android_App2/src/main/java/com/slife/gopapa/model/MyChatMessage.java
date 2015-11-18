package com.slife.gopapa.model;

import java.io.Serializable;

/***
* @ClassName: MyChatMessage 
* @Description: 聊天所有消息类的父类
* @author 菲尔普斯
* @date 2015-1-16 上午10:08:33 
*
 */
public class MyChatMessage implements Serializable{
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	private static final long serialVersionUID = 1412091087468866070L;
	private long creareTime;	//创建时间
	private byte[] extraData; //额外二进制数据
	private String messageID; //消息的ID
	private String recordID; //记录ID
	private ChatUserInfo sender; //发送者
	private ChatUserInfo target; //发送目标
	private int count;//消息条数
	public long getCreareTime() {
		return creareTime;
	}
	public void setCreareTime(long creareTime) {
		this.creareTime = creareTime;
	}
	public byte[] getExtraData() {
		return extraData;
	}
	public void setExtraData(byte[] extraData) {
		this.extraData = extraData;
	}
	public String getMessageID() {
		return messageID;
	}
	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}
	public String getRecordID() {
		return recordID;
	}
	public void setRecordID(String recordID) {
		this.recordID = recordID;
	}
	public ChatUserInfo getSender() {
		return sender;
	}
	public void setSender(ChatUserInfo sender) {
		this.sender = sender;
	}
	public ChatUserInfo getTarget() {
		return target;
	}
	public void setTarget(ChatUserInfo target) {
		this.target = target;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
	
	
	
}
