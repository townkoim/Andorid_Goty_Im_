package com.slife.gopapa.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @ClassName: MyImageView
 * @Description: 提供给相册用的自定义ImageView
 * @author 肖邦
 * @date 2015-1-26 下午5:43:36
 */
public class MyImageView extends ImageView {
	private OnMeasureListener onMeasureListener;

	public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
		this.onMeasureListener = onMeasureListener;
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 将图片测量的大小回调到onMeasureSize()方法中
		if (onMeasureListener != null) {
			onMeasureListener.onMeasureSize(getMeasuredWidth(), getMeasuredHeight());
		}
	}

	public interface OnMeasureListener {
		public void onMeasureSize(int width, int height);
	}

}
