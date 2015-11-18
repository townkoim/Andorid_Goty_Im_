package com.slife.gopapa.common;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.utils.FileUtils;
import com.slife.gopapa.view.XCRoundRectImageView;

/***
 * @ClassName: ImageLoader
 * @Description: 图片缓存处理类（用来加载自定义XCRoundRectImageView的缓存）
 * @author 菲尔普斯
 * @date 2015-1-21 下午2:42:56
 * 
 */
public class ImageLoader {
	/** 缓存Image的类，当存储Battery SavingImage的大小大于LruCache设定的值，系统自动释放内存 */
	private static LruCache<String, Bitmap> lruCache;
	/** 文件操作工具类 */
	private FileUtils utils;
	private static LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(4);
	private static ThreadPoolExecutor executor;

	static {
		// 开启线程池 最小线程数
		executor = new ThreadPoolExecutor(1, 4, 10, TimeUnit.SECONDS, queue,  new ThreadPoolExecutor.DiscardOldestPolicy());
		// 获取系统分配给应用程序的最大内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int maxSize = maxMemory / 8;
		lruCache = new LruCache<String, Bitmap>(maxSize) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				// 测量Bitmap的大小 默认返回图片数量
				return value.getRowBytes() * value.getHeight(); 
			}

		};
	}
	public ImageLoader(Context context){
		super();
		utils = new FileUtils(context);
	}

	/**
	 * @Title: display
	 * @Description: 调用此方法来加载图片缓存,调用之前应该给相应的控件设置一个tag
	 * @param @param img
	 * @return void
	 * @throws
	 */
	public void display(final XCRoundRectImageView img) {
		final String  url=(String) img.getTag();
		if (url != null) {
			Bitmap bitmap = showCacheBitmap(url); //先从缓存当中去找
			if (bitmap != null) {     
				img.setImageBitmap(bitmap);
			} else {              
				final Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						img.setImageBitmap((Bitmap) msg.obj);
					}
				};
				executor.execute(new Runnable() {

					@Override
					public void run() {
						if (url != null) {
							Bitmap	netBitmap = MyHttpClient.getBitmapFromUrl(url);
							if (netBitmap != null) {
								Message msg = new Message();
								msg.obj = netBitmap;
								msg.what = 1;
								handler.sendMessage(msg);
								// 保存到SD卡当中去
								try {
									utils.savaBitmap(url, netBitmap);
								} catch (IOException e) {
									e.printStackTrace();
								}
								// 保存在系统缓存中
								lruCache.put(url, netBitmap);
							}
						}
					}
				});

			}
		}
	}

	/**
	 * 
	 * @Title: showCacheBitmap
	 * @说 明: 获取bitmap对象 : 内存中没有就去sd卡中去找
	 * @参 数: @param url 图片地址
	 * @参 数: @return
	 * @return Bitmap 返回类型 图片
	 * @throws
	 */
	public Bitmap showCacheBitmap(String url) {
		Bitmap	bitmap = getMemoryBitmap(url); //先从缓存中去找，如果没找到则去SD卡中寻找
		if (bitmap != null) {
			lruCache.put(url, bitmap);
			return bitmap;
		} else if (utils.isFileExists(url) && utils.getFileSize(url) > 0) {
			bitmap = utils.getBitmap(url);
			lruCache.put(url, bitmap);
			return bitmap;
		}
		return null;
	}

	/**
	 * 
	 * @Title: getMemoryBitmap
	 * @说 明:获取内存中的图片
	 * @参 数: @param url
	 * @参 数: @return
	 * @return Bitmap 返回类型
	 * @throws
	 */
	private Bitmap getMemoryBitmap(String url) {
		return lruCache.get(url);
	}

	/**
	 * 
	 * @Title: cancelTask
	 * @说 明:停止所有下载线程
	 * @参 数:
	 * @return void 返回类型
	 * @throws
	 */
	public void cancelTask() {
		if (executor != null) {
			executor.shutdownNow();
		}
	}

}
