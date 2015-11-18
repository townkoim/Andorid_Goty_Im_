package com.slife.gopapa.model;

/***
* @ClassName: ChatTextMessage 
* @Description: 普通文本消息类(MyChatMessage子类之一) 
* @author 菲尔普斯
* @date 2015-1-16 上午10:07:45 
*
 */
public class ChatTextMessage extends MyChatMessage {
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	private static final long serialVersionUID = 1938953288526783239L;
	private String text;	//普通文本消息

	public ChatTextMessage() {
		super();
	}

	public ChatTextMessage(String text) {
		super();
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
