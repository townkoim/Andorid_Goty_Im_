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

/**
 * @ClassName: SelectAdapter
 * @Description: 普通列表的适配器
 * @author 肖邦
 * @date 2015-1-26 下午5:28:43
 */
public class SelectAdapter extends BaseAdapter {

	private ArrayList<String> list;
	private LayoutInflater inflater;
	public static final int TYPE_COMPETITION_FEE = 0;//约赛费用类型
	public static final int TYPE_ACTIVITY = 1;//一般的类型
	private int type = -1;

	public SelectAdapter(Context context, ArrayList<String> list, int type) {
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
		SelectViewHolder holder;
		if (convertView == null) {
			holder = new SelectViewHolder();
			convertView = inflater.inflate(R.layout.listview_select_item, parent, false);
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (SelectViewHolder) convertView.getTag();
		}
		holder.tvName.setText(list.get(position));
		if (position == list.size() - 1) {
			holder.v.setVisibility(View.GONE);
		} else {
			holder.v.setVisibility(View.VISIBLE);
		}
		if (type == TYPE_COMPETITION_FEE) {
			holder.v.setVisibility(View.GONE);
			if (position != list.size() - 1)
				holder.v2.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	public class SelectViewHolder {
		@ViewInject(R.id.listview_select_item)
		public TextView tvName;
		@ViewInject(R.id.listview_select_view)
		public View v;
		@ViewInject(R.id.listview_select_view2)
		public View v2;
	}
}
