package com.slife.gopapa.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.model.MyJoinOrInvitation;
/**
* @ClassName: MyInvitationAdapter
* @Description: 我参与的和我发起的列表适配器
* @author 肖邦
* @date 2015-1-26 下午5:26:49
 */
public class MyJoinAdapter extends BaseAdapter {
	private ArrayList<MyJoinOrInvitation> list;
	private LayoutInflater inflater;
	private int type;

	public MyJoinAdapter(Context context, ArrayList<MyJoinOrInvitation> list, int type) {
		super();
		this.list = list;
		this.inflater = LayoutInflater.from(context);
		this.type = type;
	}

	@Override
	public int getCount() {
		return list != null && list.size() > 0 ? list.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return list != null && list.size() > 0 ? list.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyJoinHolder holder;
		if (convertView == null) {
			holder = new MyJoinHolder();
			convertView = inflater.inflate(R.layout.listview_my_join, parent, false);
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (MyJoinHolder) convertView.getTag();
		}
		MyJoinOrInvitation joinOrInvitation = list.get(position);
		if (type == 2) {
			holder.tvInvitation.setText(joinOrInvitation.getTo_user_nikename());
		} else if (type == 3) {
			holder.tvInvitation.setText(joinOrInvitation.getFrom_user_nikename());
		}
		holder.tvItem.setText(joinOrInvitation.getSport_tag_name());
		holder.tvTime.setText(joinOrInvitation.getRace_data());
		holder.tvTitle.setText(joinOrInvitation.getRace_title());
		return convertView;
	}

	public class MyJoinHolder {
		@ViewInject(R.id.listview_my_join_invitation)
		public TextView tvInvitation;
		@ViewInject(R.id.listview_my_join_item)
		public TextView tvItem;
		@ViewInject(R.id.listview_my_join_time)
		public TextView tvTime;
		@ViewInject(R.id.listview_my_join_title)
		public TextView tvTitle;
	}
}
