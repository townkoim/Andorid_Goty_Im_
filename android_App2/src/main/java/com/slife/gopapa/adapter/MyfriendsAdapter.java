package com.slife.gopapa.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.slife.gopapa.R;
import com.slife.gopapa.activity.ranking.PersonInformationActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.model.ChatUserInfo;
import com.slife.gopapa.view.XCRoundRectImageView;

/***
 * @ClassName: MyfriendsAdapter
 * @Description: 我的好友列表适配器
 * @author 菲尔普斯
 * @date 2015-1-19 下午2:43:43
 * 
 */
public class MyfriendsAdapter extends BaseAdapter {
	private Context context;
	private List<ChatUserInfo> list;
	private ViewHolder holder;

	public MyfriendsAdapter(Context context, List<ChatUserInfo> list) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		final ChatUserInfo friends = list.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listview_myfriends, parent, false);
			holder = new ViewHolder();
			holder.imgIcon = (XCRoundRectImageView) convertView
					.findViewById(R.id.my_friends_icon);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.my_friends_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvName.setText(friends.getUser_nickname()); // 名字(昵称)
		if (friends.getUser_logo()!= null&&!"".equals(friends.getUser_logo())&&!"null".equals(friends.getUser_logo())) {
			MyApplication.bitmapUtils.display(holder.imgIcon, friends.getUser_logo());
		}else{
			holder.imgIcon.setImageResource(R.drawable.common_users_icon_default);
		}
		// 头像点击事件
		holder.imgIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,PersonInformationActivity.class);
				intent.putExtra("by_user_account", friends.getUser_account());//将好友的啪啪号传递给聊天界面
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	private class ViewHolder {
		private XCRoundRectImageView imgIcon;
		private TextView tvName;
	}

}
