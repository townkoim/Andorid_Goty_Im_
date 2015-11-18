package com.slife.gopapa.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.model.ListPowerAPP2;

/**
 * @ClassName: CompetitionPointAdapter
 * @Description: 积分战绩适配器
 * @author 肖邦
 * @date 2015-1-26 下午5:25:36
 */
public class CompetitionPointAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<ListPowerAPP2> list;
	private Context context;

	public CompetitionPointAdapter(Context context, ArrayList<ListPowerAPP2> list) {
		super();
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
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
		CompetitionPointAdapterViewHolder holder;
		if (convertView == null) {
			holder = new CompetitionPointAdapterViewHolder();
			convertView = inflater.inflate(R.layout.listview_competition_combat_point_item, parent, false);
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (CompetitionPointAdapterViewHolder) convertView.getTag();
		}
		ListPowerAPP2 listPowerAPP2 = list.get(position);
		holder.tvCombater.setText(new StringBuffer(listPowerAPP2.getFrom_user_nikename()).append("  VS  ").append(listPowerAPP2.getTo_user_nikename()).toString());
		holder.tvType.setText(listPowerAPP2.getSport_tag_name());
		holder.tvTime.setText(listPowerAPP2.getRace_data());
		holder.tvResult.setText(listPowerAPP2.getRace_result());
		holder.tvPoint.setText(listPowerAPP2.getAdd_power_num() + "");
		if (listPowerAPP2.getIs_challenge() == 1) {
			holder.relativeLayout.setVisibility(View.VISIBLE);
			holder.imgTypeBackGrond.setImageResource(R.drawable.combat);
		}
		if ("胜".equals(listPowerAPP2.getRace_result())) {
			holder.tvPoint.setTextColor(context.getResources().getColor(R.color.red));
			holder.tvResult.setTextColor(context.getResources().getColor(R.color.red));
		}
		if (position == getCount() - 1) {
			holder.v.setVisibility(View.GONE);
		} else {
			holder.v.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	public class CompetitionPointAdapterViewHolder {
		@ViewInject(R.id.listview_competition_combat_point_item_img)
		public ImageView imgTypeBackGrond;
		@ViewInject(R.id.listview_competition_combat_point_item_type)
		public TextView tvType;
		@ViewInject(R.id.listview_competition_combat_point_item_combater)
		public TextView tvCombater;
		@ViewInject(R.id.listview_competition_combat_point_item_time)
		public TextView tvTime;
		@ViewInject(R.id.listview_competition_combat_point_item_result)
		public TextView tvResult;
		@ViewInject(R.id.listview_competition_combat_point_item_point)
		public TextView tvPoint;
		@ViewInject(R.id.listview_competition_combat_point_item_v)
		public View v;
		@ViewInject(R.id.listview_competition_combat_point_item_ischallenge)
		public RelativeLayout relativeLayout;
	}
}
