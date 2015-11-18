package com.slife.gopapa.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.ranking.PersonInformationActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.model.RecentNews;
import com.slife.gopapa.utils.DataUtils;
import com.slife.gopapa.view.XCRoundRectImageView;

/**
 * @ClassName: NewsAdapter
 * @Description:消息界面的适配器( 用来显示最近的消息)
 * @author 菲尔普斯
 * @date 2015-1-7 上午10:42:44
 * 
 */
public class NewsAdapter extends BaseAdapter {
	private ViewHolder holder;
	private Context context;
	private List<RecentNews> list;

	public NewsAdapter(Context context, List<RecentNews> list) {
		super();
		this.context = context;
		this.list = list;
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
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listview_news_items, parent, false);
			holder = new ViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//昵称
		String nickeName=list.get(position).getUser_nick_name();
		if(nickeName!=null&&!"".equals(nickeName)&&!"null".equals(nickeName)){
			holder.name.setText(nickeName);
		}
		//消息(判断消息的类型)
		String msg=list.get(position).getUser_last_message();
		if(msg.equals("voice")){
			holder.message.setText("语音消息");
		}else if(msg.equals("richtext")){
			holder.message.setText("通过了你的好友请求");
		}else if(msg.equals("imagetext")){
			holder.message.setText("图片");
		}
		else{
			holder.message.setText(MyApplication.smileParserUtils.strToSmiley(msg,36,36));
		}
		//时间
		holder.time.setText(DataUtils.getStringDate(Long.valueOf(list.get(position).getTime())));
		//小红点
		int point=list.get(position).getMsgCount();
		if(point>0){
			holder.trackPoint.setVisibility(View.VISIBLE);
			holder.trackPoint.setText(String.valueOf(point));
		}
		//头像
		String icon = list.get(position).getUser_logo();
		if(icon!=null&&!"".equals(icon)&&!"null".equals(icon)){
			MyApplication.bitmapUtils.display(holder.icon, icon);
		}else{
			holder.icon.setImageResource(R.drawable.common_users_icon_default);
		}
		holder.icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,PersonInformationActivity.class);
				intent.putExtra("by_user_account", list.get(position).getUser_target_account());
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}

	public  class ViewHolder {
		@ViewInject(R.id.news_items_icon)
		public XCRoundRectImageView icon;
		@ViewInject(R.id.news_items_track_point)
		public Button trackPoint;
		@ViewInject(R.id.news_items_name)
		public TextView name;
		@ViewInject(R.id.news_items_message)
		public TextView message;
		@ViewInject(R.id.news_items_time)
		public TextView time;
	}
	

}
