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
import com.slife.gopapa.model.VitalityItem;

/**
 * @ClassName: HowToGetVitalityPointAdapter
 * @Description: 如何获得活力界面的适配器
 * @author 肖邦
 * @date 2015-1-26 下午5:26:11
 * 
 */
public class HowToGetVitalityPointAdapter extends BaseAdapter {

	private ArrayList<VitalityItem> list;
	private LayoutInflater inflater;

	public HowToGetVitalityPointAdapter(Context context, ArrayList<VitalityItem> list) {
		super();
		this.list = list;
		this.inflater = LayoutInflater.from(context);
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
		HowToGetVitalityPointViewHolder holder;
		VitalityItem item = list.get(position);
		if (convertView == null) {
			holder = new HowToGetVitalityPointViewHolder();
			convertView = inflater.inflate(R.layout.listview_vitality_point_item, parent, false);
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (HowToGetVitalityPointViewHolder) convertView.getTag();
		}
		holder.tvName.setText(item.getTitle());
		holder.tvPoint.setText(item.getPoint());
		if (position == list.size() - 1) {
			holder.v.setVisibility(View.GONE);
		} else {
			holder.v.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	public class HowToGetVitalityPointViewHolder {
		@ViewInject(R.id.listview_vitality_point_item_name)
		public TextView tvName;
		@ViewInject(R.id.listview_vitality_point_item_point)
		public TextView tvPoint;
		@ViewInject(R.id.listview_vitality_point_item_view)
		public View v;
	}
}
