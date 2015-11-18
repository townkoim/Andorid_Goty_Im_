package com.slife.gopapa.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;

/**
* @ClassName: MyInvitationAdapter
* @Description: 我参与的和我发起的列表适配器
* @author 肖邦
* @date 2015-1-26 下午5:26:49
 */
public class MyInvitationAdapter extends BaseAdapter {
	private ArrayList<String> list;
	private LayoutInflater inflater;

	public MyInvitationAdapter(Context context, ArrayList<String> list) {
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
		MyInvitationHolder holder;
		if (convertView == null) {
			holder = new MyInvitationHolder();
			convertView = inflater.inflate(R.layout.activity_myjoin_or_invitation_detail, parent, false);
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (MyInvitationHolder) convertView.getTag();
		}
		return convertView;
	}

	public class MyInvitationHolder {
		@ViewInject(R.id.activity_myjoin_or_invitation_detail_head)
		public ImageView imgHead;
		@ViewInject(R.id.activity_myjoin_or_invitation_detail_name)
		public TextView tvName;
		@ViewInject(R.id.activity_myjoin_or_invitation_detail_vitality_name)
		public TextView tvVitalityName;
		@ViewInject(R.id.activity_myjoin_or_invitation_detail_item)
		public TextView tvItem;
		@ViewInject(R.id.activity_myjoin_or_invitation_detail_position)
		public TextView tvAddress;
		@ViewInject(R.id.activity_myjoin_or_invitation_detail_time)
		public TextView tvTime;
		@ViewInject(R.id.activity_myjoin_or_invitation_detail_fee)
		public TextView tvFee;
		@ViewInject(R.id.activity_myjoin_or_invitation_detail_illustration)
		public TextView tvIllustration;
	}
}
