package com.slife.gopapa.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;

/**
 * @ClassName: PoiSearchAdapter
 * @Description: 地图兴趣点搜索结果适配器
 * @author 肖邦
 * @date 2015-1-26 下午5:27:43
 */
public class PoiSearchAdapter extends BaseAdapter {
	private List<PoiInfo> list;
	private LayoutInflater inflater;

	public PoiSearchAdapter(Context context, List<PoiInfo> list) {
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
		PoiViewHolder holder;
		if (convertView == null) {
			holder = new PoiViewHolder();
			convertView = inflater.inflate(R.layout.listview_poi_search_item, parent, false);
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (PoiViewHolder) convertView.getTag();
		}
		if (position == list.size() - 1) {
			holder.v.setVisibility(View.GONE);
		} else {
			holder.v.setVisibility(View.VISIBLE);
		}
		PoiInfo info = list.get(position);
		holder.tvName.setText(info.name);
		holder.tvAddress.setText(info.address);
		return convertView;
	}

	public class PoiViewHolder {
		@ViewInject(R.id.listview_poi_search_item_name)
		public TextView tvName;
		@ViewInject(R.id.listview_poi_search_item_address)
		public TextView tvAddress;
		@ViewInject(R.id.listview_select_view)
		public View v;
	}
}
