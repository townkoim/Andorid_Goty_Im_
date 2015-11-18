package com.slife.gopapa.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.slife.gopapa.application.MyApplication;

/***
 * @ClassName: FileUtils
 * @Description: 文件操作类
 * @author 菲尔普斯
 * @date 2015-2-2 上午10:01:12
 * 
 */
public class FileUtils {
	/**
	 * sd卡的根目录
	 */
	private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();
	/**
	 * 手机的缓存根目录
	 */
	private static String mDataRootPath = null;
	/**
	 * 保存Image的目录名
	 */
	private final static String FOLDER_NAME = "/gopapa/image";

	public FileUtils(Context context) {
		mDataRootPath = context.getCacheDir().getPath();
	}

	/**
	 * 获取储存Image的目录
	 * 
	 * @return
	 */
	public static String getStorageDirectory() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME : mDataRootPath + FOLDER_NAME;
	}
	

	/**
	 * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
	 * 
	 * @param fileName
	 * @param bitmap
	 * @throws IOException
	 */
	public void savaBitmap(String url, Bitmap bitmap) throws IOException {
		if (bitmap == null) {
			return;
		}
		String path = getStorageDirectory();
		File folderFile = new File(path);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
		}
		File file = new File(path + File.separator + getFileName(url));
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();
	}

	/**
	 * 从手机或者sd卡获取Bitmap
	 * 
	 * @param fileName
	 * @return
	 */
	public Bitmap getBitmap(String url) {
		return BitmapFactory.decodeFile(getStorageDirectory() + File.separator + getFileName(url));
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExists(String fileName) {
		return new File(getStorageDirectory() + File.separator + getFileName(fileName)).exists();
	}

	/**
	 * 获取文件的大小
	 * 
	 * @param fileName
	 * @return
	 */
	public long getFileSize(String url) {
		return new File(getStorageDirectory() + File.separator + getFileName(url)).length();
	}

	/**
	 * 删除SD卡或者手机的缓存图片和目录
	 */
	public void deleteFile() {
		File dirFile = new File(getStorageDirectory());
		if (!dirFile.exists()) {
			return;
		}
		if (dirFile.isDirectory()) {
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}

		dirFile.delete();
	}

	/**
	 * 
	 * @Title: getFileName
	 * @说 明: 根据url截取文件名
	 * @参 数: @param url
	 * @参 数: @return
	 * @return String 返回类型
	 * @throws
	 */
	public String getFileName(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}

	/***
	 * @Title: getVoicePath
	 * @Description: 根据文件名字得到文件路径
	 * @param @param voiceName 文件名
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getVoicePath() {
		String path = null;
		if (Environment.getExternalStorageState().equals( // 如果有SD卡，就存SD卡
				Environment.MEDIA_MOUNTED)) {
			path = Environment.getExternalStorageDirectory() + "/gopapa/voice";
		} else { // 保存在手机
			path = Environment.getDataDirectory() + "/gopapa/voice";
		}
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	/**
	 * @Title: getVoicePath
	 * @Description: 根据名字得到当前录音的路径
	 * @param @param voiceName
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getVoicePath(String voiceName) {
		return getVoicePath() + "/" + voiceName;
	}

	/***
	 * @Title: getVoiceStream
	 * @Description: 根据文件路径，读取文件转化为InputStream
	 * @param @param voiceName 文件名
	 * @param @return
	 * @return InputStream
	 * @throws
	 */
	public static InputStream getVoiceStream(String path) {
		FileInputStream fs = null;
		InputStream is = null;
		byte[] buffer = null;
		try {
			fs = new FileInputStream(path);
			if (fs != null) {
				int length = fs.available();
				buffer = new byte[length];
				fs.read(buffer);
				fs.close();
				is = new ByteArrayInputStream(buffer);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}

	/**
	 * 把程序拍摄的照片放到 SD卡的 Pictures目录中 sheguantong 文件夹中
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressLint("SimpleDateFormat")
	public static File createImageFile() throws IOException {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String timeStamp = format.format(new Date());
		String imageFileName = "gopapa_head_" + timeStamp + ".jpg";
		File image = new File(PictureUtil.getAlbumDir(), imageFileName);
		MyApplication.currentPhotoPath = image.getAbsolutePath();
		return image;
	}
	
	public static Bitmap getUserHeadImg(){
		File file = new File(MyApplication.preferences.getString(MyApplication.preferences.getString("user_account","")+"_head_file", ""));
		if(file != null && file.exists()){
			return BitmapFactory.decodeFile(file.getAbsolutePath());
		}
		return null;
	}
	
	public static String getUserHeadImgPath(){
		File file = new File(MyApplication.preferences.getString(MyApplication.preferences.getString("user_account","")+"_head_file", ""));
		if(file != null && file.exists()){
			return file.getAbsolutePath();
		}
		return null;
	}
	
	/**
	* @Title: delAllFile
	* @Description: 删除指定文件夹下所有文件
	* @param @param path 文件夹完整绝对路径
	* @param @return
	* @return boolean
	* @throws
	 */
	   public static boolean delAllFile(String path) {
	       boolean flag = false;
	       File file = new File(path);
	       if (!file.exists()) {
	         return flag;
	       }
	       if (!file.isDirectory()) {
	         return flag;
	       }
	       String[] tempList = file.list();
	       File temp = null;
	       for (int i = 0; i < tempList.length; i++) {
	          if (path.endsWith(File.separator)) {
	             temp = new File(path + tempList[i]);
	          } else {
	              temp = new File(path + File.separator + tempList[i]);
	          }
	          if (temp.isFile()) {
	             temp.delete();
	          }
	          if (temp.isDirectory()) {
	             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
	             delFolder(path + "/" + tempList[i]);//再删除空文件夹
	             flag = true;
	          }
	       }
	       return flag;
	     }
	   /***
	   * @Title: delFolder
	   * @Description: 删除文件夹
	   * @param @param 文件夹完整绝对路径
	   * @return void
	   * @throws
	    */
	     public static void delFolder(String folderPath) {
	         try {
	            delAllFile(folderPath); //删除完里面所有内容
	            String filePath = folderPath;
	            filePath = filePath.toString();
	            java.io.File myFilePath = new java.io.File(filePath);
	            myFilePath.delete(); //删除空文件夹
	         } catch (Exception e) {
	           e.printStackTrace(); 
	         }
	    }
	     
	     
	 	/**
	 	 * @Title: getFile
	 	 * @Description: 根据名称创建文件夹和文件
	 	 * @param @param fileName
	 	 * @param @return
	 	 * @return File
	 	 * @throws
	 	 */
	 	public static File getImgFile(String imgName) {
	 		// path表示你所创建文件的路径
	 		String path = getImgPath();
	 		File f = new File(path);
	 		if (!f.exists()) {
	 			f.mkdirs();
	 		}
	 		File file = new File(f,imgName + ".jpg");
	 		if (!file.exists()) {
	 			try {
	 				file.createNewFile();
	 			} catch (IOException e) {
	 				e.printStackTrace();
	 			}
	 		}
	 		return file;
	 	}
	 	
		/***
		 * @Title: getVoicePath
		 * @Description: 设置图片存储路径
		 * @param @return
		 * @return String 文件路径
		 * @throws
		 */
		public static String getImgPath() {
			String path = null;
			if (Environment.getExternalStorageState().equals( // 如果有SD卡，就存SD卡
					Environment.MEDIA_MOUNTED)) {
				path = Environment.getExternalStorageDirectory() + "/gopapa/image";
			} else { // 保存在手机
				path = Environment.getDataDirectory() + "/gopapa/image";
			}
			File file =new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
			return path;
		}
		/**
		* @Title: getImgPath
		* @Description: 根据图片名字得到当前图片路径
		* @param @param imgName	图片名字
		* @param @return
		* @return String 图片的路径
		* @throws
		 */
		public static String getImgPath(String imgName) {
			return getImgPath()+"/" + imgName + ".jpg";
		}
}
