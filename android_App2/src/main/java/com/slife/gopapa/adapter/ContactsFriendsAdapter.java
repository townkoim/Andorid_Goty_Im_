package com.slife.gopapa.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.model.ContactsPerson;
import com.slife.gopapa.task.AddFriendsDisponseTask;
import com.slife.gopapa.view.XCRoundRectImageView;

/**
* @ClassName: ContactsFriendsAdapter 
* @Description:消息里面的联系人界面的适配器
* 		 	status	联系人状态
			-2 : 不是手机号
			-1 : 是手机但是没有注册啪啪,可邀请
			0 : 是手机且已经注册啪啪,不可添加为好友
			1 : 是手机且已经注册啪啪,可添加为好友
			2 : 是手机且已经注册啪啪,已经是好友
			音位要加载两种布局（字母的布局。姓名的布局） 所以重写getItemViewType和getViewTypeCount方法。根据cp.getName().length()是否为1，以及是否是字母来进行判断
* @author 菲尔普斯
* @date 2015-1-7 上午10:42:58 
*
 */
public class ContactsFriendsAdapter extends BaseAdapter {
	private Context context;
	private List<ContactsPerson> list;
	private ViewHolder holder;
	private boolean isWord = false;

	public interface IMsgViewType {
		int WORD = 0;
		int NAME = 1;
	}

	public ContactsFriendsAdapter(Context context, List<ContactsPerson> list) {
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
	public int getItemViewType(int position) {
		ContactsPerson cp = list.get(position);
		if (cp.getName().length() == 1) {
			char c = cp.getName().toCharArray()[0];
			if (Character.isUpperCase(c)) {
				return IMsgViewType.WORD;
			}else{
				return IMsgViewType.NAME;
			}
		}else{
			return IMsgViewType.NAME;
		}
	}

	@Override
	public boolean isEnabled(int position) {
		ContactsPerson cp = list.get(position);
		if (cp.getName().length() == 1) {
			char c = cp.getName().toCharArray()[0];
			if (Character.isUpperCase(c)) {
				return false;
			}
		}
		return super.isEnabled(position);
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ContactsPerson cp = list.get(position);
		if (getItemViewType(position)==IMsgViewType.WORD) {
			isWord=true;
		}else if(getItemViewType(position)==IMsgViewType.NAME){
			isWord=false;
		}
		if (convertView == null) {
			if (isWord) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.listview_contacts_friends_item1, parent, false);
			} else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.listview_contacts_friends_item2, parent, false);
			}
			holder = new ViewHolder();
			holder.tvIndex = (TextView) convertView.findViewById(R.id.contacts_friends_index);
			holder.tvName = (TextView) convertView.findViewById(R.id.contacts_friends_name);
			holder.imgLogo=(XCRoundRectImageView) convertView.findViewById(R.id.contacts_friends_icon);
			holder.tvInvitation=(TextView) convertView.findViewById(R.id.contacts_friends_addstate);
			holder.tvAddState=(TextView) convertView.findViewById(R.id.contacts_friends_added);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (isWord) {
			holder.tvIndex.setText(cp.getName());
		} else {
			holder.tvName.setText(cp.getName());
			holder.tvInvitation.setVisibility(View.GONE);
			holder.tvAddState.setVisibility(View.GONE);
			//根据state状态来判断此联系人的啪啪状态
			final String state=cp.getStatus();
			if(state!=null&&!"".equals(state)){
			if(state.equals("-1")){//是手机但是没有注册啪啪,可邀请
				holder.tvInvitation.setVisibility(View.VISIBLE);
				holder.tvInvitation.setText(R.string.news_invitation);
			}else if(state.equals("0")){
				holder.tvAddState.setVisibility(View.VISIBLE);
				holder.tvAddState.setText("不可添加");
			}else if(state.equals("1")){
				holder.tvInvitation.setVisibility(View.VISIBLE);
				holder.tvInvitation.setText(R.string.news_add);
			}else if(state.equals("2")){
				holder.tvAddState.setVisibility(View.VISIBLE);
				holder.tvAddState.setText(R.string.news_added);
				holder.tvName.setText(cp.getUser_nickname());
			}
			if(state.equals(0)||state.equals("1")||state.equals("2")){
//				String nickName=cp.getUser_nickname();
//				if(nickName!=null&&!"".equals(nickName)&&!"null".equals(nickName)){
//					holder.tvName.setText(nickName);
//				}
				if(cp.getUser_logo()!=null&&!"".equals(cp.getUser_logo())&&!"null".equals(cp.getUser_logo())){
					MyApplication.bitmapUtils.display(holder.imgLogo, cp.getUser_logo());
				}else{
					holder.imgLogo.setImageResource(R.drawable.common_users_icon_default);
				}
			}else{
				holder.imgLogo.setImageResource(R.drawable.common_users_icon_default);
			}
			//添加和邀请按钮监听器
			holder.tvInvitation.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(state.equals("-1")){
						sendSMS(list.get(position).getPhone(),"我正在玩啪啪江湖，约赛、挑战、排名等你来挑战！http://spapa.com.cn/apk/ppjh.apk");
					}else if(state.equals("1")){
						new AddFriendsDisponseTask(context, MyApplication.preferences.getString(DBConstants.USER_ACCOUNT, ""), list.get(position).getUser_account()).execute();
					}
				}
			});
		}
		}
		isWord = false;
		return convertView;
	}

	 class ViewHolder {
		private XCRoundRectImageView imgLogo;
		private TextView tvIndex;
		private TextView tvName;
		private TextView tvInvitation;
		private TextView tvAddState;
	}
	 private void sendSMS(String smsTel,String smsBody){
		 Uri smsToUri = Uri.parse("smsto:"+smsTel);  
		 Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);  
		 intent.putExtra("sms_body", smsBody);  
		 context.startActivity(intent);  
	 }

}
