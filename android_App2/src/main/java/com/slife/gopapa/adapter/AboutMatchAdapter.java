package com.slife.gopapa.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.model.PushAboutMatch;

/**
* @ClassName: AboutMatchAdapter 
* @Description: 约赛消息适配器
* @author 菲尔普斯
* @date 2015-1-7 下午4:41:27 
*
 */
public class AboutMatchAdapter extends BaseAdapter{
	private Context context;
	private List<PushAboutMatch>list;
	private ViewHolder holder;
	public AboutMatchAdapter(Context context,List<PushAboutMatch>list) {
		super();
		this.list=list;
		this.context = context;
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
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.listview_about_match, parent,false);
			holder = new ViewHolder();
			ViewUtils.inject(holder,convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvMessage.setText(list.get(position).getContent());
		return convertView;
	}
	private class ViewHolder{
		@ViewInject(R.id.about_match_items_message)
		private TextView tvMessage; //消息内容
	}
}
