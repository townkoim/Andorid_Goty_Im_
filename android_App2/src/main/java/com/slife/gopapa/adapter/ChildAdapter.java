package com.slife.gopapa.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.view.MyImageView;
import com.slife.gopapa.view.MyImageView.OnMeasureListener;

/**
 * @ClassName: ChildAdapter
 * @Description: 相册文件夹适配器
 * @author 肖邦
 * @date 2015-1-26 下午5:24:43
 */
public class ChildAdapter extends BaseAdapter {
	private Point mPoint = new Point(0, 0);// 用来封装ImageView的宽和高的对象
	/**
	 * 用来存储图片的选中情况
	 */
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	@SuppressWarnings("unused")
	private GridView mGridView;
	private List<String> list;
	protected LayoutInflater mInflater;

	public ChildAdapter(Context context, List<String> list, GridView mGridView) {
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = list.get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_head_child_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);

			// 用来监听ImageView的宽和高
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {

				@Override
				public void onMeasureSize(int width, int height) {
					int com = Math.min(width, height);
					mPoint.set(com, com);
				}
			});

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		MyApplication.bitmapUtils.configDefaultLoadFailedImage(R.drawable.friends_sends_pictures_no);
		MyApplication.bitmapUtils.configDefaultLoadingImage(R.drawable.friends_sends_pictures_no);
		MyApplication.bitmapUtils.display(viewHolder.mImageView, path);
		return convertView;
	}

	/**
	 * 获取选中的Item的position
	 * 
	 * @return
	 */
	public List<Integer> getSelectItems() {
		List<Integer> list = new ArrayList<Integer>();
		for (Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, Boolean> entry = it.next();
			if (entry.getValue()) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public static class ViewHolder {
		public MyImageView mImageView;
	}

}
