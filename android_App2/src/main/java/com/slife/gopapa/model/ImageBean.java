package com.slife.gopapa.model;

import java.io.Serializable;

/**
 * 
 * @ClassName: ImageBean
 * @Description: 相册图片
 * @author 肖邦
 * @date 2015-1-26 下午5:31:40
 * 
 */
public class ImageBean implements Serializable{
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	public static final long serialVersionUID = 3728663773688244984L;
	/**
	 * 文件夹的第一张图片路径
	 */
	public String topImagePath;
	/**
	 * 文件夹名
	 */
	public String folderName;
	/**
	 * 文件夹中的图片数
	 */
	public int imageCounts;

	public String getTopImagePath() {
		return topImagePath;
	}

	public void setTopImagePath(String topImagePath) {
		this.topImagePath = topImagePath;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public int getImageCounts() {
		return imageCounts;
	}

	public void setImageCounts(int imageCounts) {
		this.imageCounts = imageCounts;
	}

}
