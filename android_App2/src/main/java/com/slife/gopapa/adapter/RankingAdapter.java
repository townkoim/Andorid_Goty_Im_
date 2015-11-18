package com.slife.gopapa.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.model.RankingList;
import com.slife.gopapa.view.XCRoundRectImageView;

/**
 * @ClassName: RankingAdapter
 * @Description: 排名界面适配器
 * @author 肖邦
 * @date 2015-1-26 下午5:28:18
 */
public class RankingAdapter extends BaseAdapter {
	public ArrayList<RankingList> list;
	public LayoutInflater inflater;
	public Context context;
	public RankingHolder holder;
	public RankingAdapter(Context context, ArrayList<RankingList> list) {
		super();
		this.list = list;
		this.inflater = LayoutInflater.from(context);
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
		if (convertView == null) {
			holder = new RankingHolder();
			convertView = inflater.inflate(R.layout.listview_ranking, parent, false);
			ViewUtils.inject(holder, convertView);
			holder.tvRanking = (TextView) convertView.findViewById(R.id.listview_ranking_ranking);
			convertView.setTag(holder);
		} else {
			holder = (RankingHolder) convertView.getTag();
		}
		RankingList ranking = list.get(position);
		holder.tvRanking.setText(String.valueOf(ranking.getRank()));
		holder.tvName.setText(String.valueOf(ranking.getUser_nickname()));
		holder.tvWin.setText(String.valueOf(ranking.getWin_num()));
		holder.tvDogfall.setText(String.valueOf(ranking.getFlat_num()));
		holder.tvLost.setText(String.valueOf(ranking.getLose_num()));
		holder.tvCombatHistory.setText(String.valueOf(ranking.getPower_num()));
//		final View v1 = convertView;
//		holder.tvCombatHistory.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				holder.tvWin = (TextView)v1.findViewById(R.id.listview_ranking_win);
//				Toast.makeText(context, ""+holder.tvWin.getText(), Toast.LENGTH_LONG).show();
//			}
//		});
		if(position>2){
			int colorId = context.getResources().getColor(R.color.black);
			holder.tvRanking.setTextColor(colorId);
			holder.tvName.setTextColor(colorId);
			holder.tvWin.setTextColor(colorId);
			holder.tvDogfall.setTextColor(colorId);
			holder.tvLost.setTextColor(colorId);
			holder.tvCombatHistory.setTextColor(colorId);
		}else{
			int colorId = context.getResources().getColor(R.color.white);
			holder.tvRanking.setTextColor(colorId);
			holder.tvName.setTextColor(colorId);
			holder.tvWin.setTextColor(colorId);
			holder.tvDogfall.setTextColor(colorId);
			holder.tvLost.setTextColor(colorId);
			holder.tvCombatHistory.setTextColor(colorId);
		}
		if(!"".equals(ranking.getUser_logo_200())){
			MyApplication.bitmapUtils.display(holder.imgHead, ranking.getUser_logo_200());
		}else{
			holder.imgHead.setImageResource(R.drawable.common_users_icon_default);
		}
		if (position <= 2) {
			holder.ll.setBackgroundResource(R.drawable.selector_ranking_listview_top3);
		} else {
			holder.ll.setBackgroundResource(R.drawable.selector_ranking_listview);
		}
		return convertView;
	}

	public class RankingHolder {
		@ViewInject(R.id.listview_ranking_head)
		public XCRoundRectImageView imgHead;//
		@ViewInject(R.id.listview_ranking_ranking)
		public TextView tvRanking;
		@ViewInject(R.id.listview_ranking_name)
		public TextView tvName;
		@ViewInject(R.id.listview_ranking_win)
		public TextView tvWin;
		@ViewInject(R.id.listview_ranking_dogfall)
		public TextView tvDogfall;
		@ViewInject(R.id.listview_ranking_lose)
		public TextView tvLost;
		@ViewInject(R.id.listview_ranking_combat_history)
		public TextView tvCombatHistory;
		@ViewInject(R.id.listview_ranking_ll)
		public LinearLayout ll;
	}
}
