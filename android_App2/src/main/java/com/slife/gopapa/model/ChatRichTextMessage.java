package com.slife.gopapa.model;

/***
* @ClassName: ChatRichTextMessage 
* @Description:自定义消息实体类(MyChatMessage子类之一)
* @author 菲尔普斯
* @date 2015-1-16 上午10:07:19 
*
 */
public class ChatRichTextMessage extends MyChatMessage{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	private static final long serialVersionUID = 2426466943549366407L;
	private byte[] richText;	//自定义消息

	public ChatRichTextMessage(byte[] richText) {
		super();
		this.richText = richText;
	}

	public ChatRichTextMessage() {
		super();
	}

	public byte[] getRichText() {
		return richText;
	}

	public void setRichText(byte[] richText) {
		this.richText = richText;
	}
	
}
