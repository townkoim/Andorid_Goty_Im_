package com.slife.gopapa.adapter;

import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gotye.api.bean.GotyeRichTextMessage;
import com.gotye.api.bean.GotyeUser;
import com.gotye.api.utils.TimeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.ContactsPerson;
import com.slife.gopapa.view.MyProgressDialog;
import com.slife.gopapa.view.XCRoundRectImageView;

/**
 * @ClassName: NewsNewFriendsAdapter
 * @Description: 新朋友消息的适配器
 * @author 菲尔普斯
 * @date 2015-2-2 上午9:29:58
 * 
 */
public class NewsNewFriendsAdapter extends BaseAdapter {
	private Context context;
	private List<ContactsPerson> lists;
	private ViewHolder holder;

	public NewsNewFriendsAdapter(Context context, List<ContactsPerson> lists) {
		super();
		this.context = context;
		this.lists = lists;
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ContactsPerson cp = lists.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listview_news_new_friends, parent, false);
			holder = new ViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.btnInvitation.setVisibility(View.GONE);
		holder.tvAddState.setVisibility(View.GONE);
		// 昵称
		holder.tvName.setText(cp.getUser_nickname());
		// 添加的状态 (根据实体类返回的state状态，来控制控件的显示与隐藏)
		String state = cp.getStatus();
		if (state != null && !"".equals(state)) {
			if (state.equals("-1")) {
				holder.btnInvitation.setVisibility(View.VISIBLE);
			} else if (state.equals("1")) {
				holder.tvAddState.setVisibility(View.VISIBLE);
			}
		}
		// 显示头像
		String logo = cp.getUser_logo();
		if (logo != null && !"".equals(logo) && !"null".equals(logo)) {
			MyApplication.bitmapUtils.display(holder.imgIcom, logo);
		}
		holder.btnInvitation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new DisponseFriendsTask(position).execute();
			}
		});
		return convertView;
	}

	private class ViewHolder {
		@ViewInject(R.id.news_new_friends_icon)
		private XCRoundRectImageView imgIcom; // 头像
		@ViewInject(R.id.news_new_friends_name)
		private TextView tvName; // 姓名
		@ViewInject(R.id.news_new_friends_addstate)
		private TextView btnInvitation; // 按钮状态
		@ViewInject(R.id.news_new_friends_added)
		private TextView tvAddState; // 文字状态显示已接受
		@ViewInject(R.id.news_new_friends_state)
		private TextView tvMsgState; // 消息状态
	}

	/***
	 * @ClassName: DisponseFriendsTask
	 * @Description: 接收添加好友的异步任务
	 * @author 菲尔普斯
	 * @date 2015-2-2 上午9:30:47
	 * 
	 */
	private class DisponseFriendsTask extends AsyncTask<Void, Void, String[]> {
		private int position; //联系人实体类

		public DisponseFriendsTask(int  position) {
			super();
			this.position = position;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			MyProgressDialog.showDialog(context, "接收请求", "正在处理");
		}

		@Override
		protected String[] doInBackground(Void... params) {
			return MyHttpClient.postDataToService(context,
					APPConstants.URL_APP2_HOST_NAME
							+ APPConstants.URL_FRIENDS_DISPOSEAPPLYAPP2,
					MyApplication.app2Token,
					new String[] { "send_user_account", "to_user_account" },
					new String[] {
							lists.get(position).getUser_account(),
							MyApplication.preferences.getString(
									DBConstants.USER_ACCOUNT, null) }, null,
					null);
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			MyProgressDialog.closeDialog();
			JsonObjectErrorDaoImpl.resolveJson(context, result,
					new JsonObjectErrorDao() {

						@Override
						public void disposeJsonObj(JSONObject obj) {
							boolean isSuccess = obj.optBoolean("is_success");
							if (isSuccess) {
								lists.get(position).setStatus("1");
								notifyDataSetChanged();
								if (MyApplication.senderUser != null&& MyApplication.gotyeState) { // 如果登陆了服务器且我的数据不为空，而且登陆秦加成功了,就发送自定义消息给
									GotyeRichTextMessage msg = new GotyeRichTextMessage(
											UUID.randomUUID().toString(),
											TimeUtil.getCurrentTime(),
											new GotyeUser(lists.get(position).getExtend_user_account()),
											new GotyeUser(MyApplication.senderUser.getExtend_user_account()));
									msg.setRichText(APPConstants.RICHTEXT_ADDFRIENDS_SUCCESS.getBytes());
									MyApplication.gotyeApi.sendMessageToTarget(msg);
								}
							}
						}
					});
		}

	}

}
