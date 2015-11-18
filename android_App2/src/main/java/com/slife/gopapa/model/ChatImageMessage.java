package com.slife.gopapa.model;

public class ChatImageMessage extends MyChatMessage{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	private static final long serialVersionUID = -1411613605745300991L;

	private String downLoadUrl; //图片下载地址
	private byte[] imageData; //图片文件
	private byte[] thumbnailData; //图片缩略图
	public String getDownLoadUrl() {
		return downLoadUrl;
	}
	public void setDownLoadUrl(String downLoadUrl) {
		this.downLoadUrl = downLoadUrl;
	}
	public byte[] getImageData() {
		return imageData;
	}
	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}
	public byte[] getThumbnailData() {
		return thumbnailData;
	}
	public void setThumbnailData(byte[] thumbnailData) {
		this.thumbnailData = thumbnailData;
	}
	
	
	
}
