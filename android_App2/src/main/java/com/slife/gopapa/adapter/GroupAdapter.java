package com.slife.gopapa.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.model.ImageBean;
import com.slife.gopapa.view.MyImageView;
import com.slife.gopapa.view.MyImageView.OnMeasureListener;

/**
* @ClassName: GroupAdapter
* @Description: 相册适配器
* @author 肖邦
* @date 2015-1-26 下午5:25:56
 */
public class GroupAdapter extends BaseAdapter{
	private List<ImageBean> list;
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	@SuppressWarnings("unused")
	private GridView mGridView;
	protected LayoutInflater mInflater;
	
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
	
	public GroupAdapter(Context context, List<ImageBean> list, GridView mGridView){
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		ImageBean mImageBean = list.get(position);
		String path = mImageBean.getTopImagePath();
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listview_head_group_item, null);
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.group_image);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.group_title);
			viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_count);
			
			//用来监听ImageView的宽和高
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		viewHolder.mTextViewTitle.setText(mImageBean.getFolderName());//文件夹名称
		viewHolder.mTextViewCounts.setText(Integer.toString(mImageBean.getImageCounts()));//文件数量
		MyApplication.bitmapUtils.configDefaultLoadingImage(R.drawable.friends_sends_pictures_no);
		MyApplication.bitmapUtils.configDefaultLoadFailedImage(R.drawable.friends_sends_pictures_no);
		MyApplication.bitmapUtils.display(viewHolder.mImageView, path);
		return convertView;
	}
	
	
	
	public static class ViewHolder{
		public MyImageView mImageView;
		public TextView mTextViewTitle;
		public TextView mTextViewCounts;
	}

}
