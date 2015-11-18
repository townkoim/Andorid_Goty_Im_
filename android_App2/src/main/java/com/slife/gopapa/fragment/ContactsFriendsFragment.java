package com.slife.gopapa.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.slife.gopapa.R;
import com.slife.gopapa.activity.news.MyFriendsActivity;
import com.slife.gopapa.activity.ranking.PersonInformationActivity;
import com.slife.gopapa.adapter.ContactsFriendsAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.dao.JsonObjectErrorDao;
import com.slife.gopapa.dao.impl.JsonObjectErrorDaoImpl;
import com.slife.gopapa.database.DBConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.ContactsPerson;
import com.slife.gopapa.utils.ChineseTransferUtils;
import com.slife.gopapa.utils.ListUtils;

/**
 * @ClassName: ContactsFriendsFragment
 * @Description:通讯录联系人的Fragment 
 * 				1、先从通讯录读取通讯录的好友，再通过Pinyin4J将通讯录好友的名字转换成汉语拼音，再取得拼音的首字母。同时先绘制好右边的字母索引栏
 * 				2、根据通讯录的电话号码去服务器查询。判断这个电话号码是不是啪啪号或者好友
 * 			status	联系人状态
			-2 : 不是手机号
			-1 : 是手机但是没有注册啪啪,可邀请
			0 : 是手机且已经注册啪啪,不可添加为好友
			1 : 是手机且已经注册啪啪,可添加为好友
			2 : 是手机且已经注册啪啪,已经是好友
 * @author 菲尔普斯
 * @date 2014-12-31 下午5:21:04
 * 
 */
@SuppressLint("NewApi")
public class ContactsFriendsFragment extends Fragment {
	private ListView listView; // 联系人列表
	private TextView txCenter; // 显示在中间的提示键
	private LinearLayout layoutIndex;
	private HashMap<String, Integer> selector = new HashMap<>();// 存放含有索引字母的位置
	private static final String[] indexStr = { "☆", "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z", "#" };// 右边的字母
	private List<ContactsPerson> persons = new ArrayList<>(); // 联系人,从通讯录读取出来的
	private List<ContactsPerson> newPersons = new ArrayList<ContactsPerson>();// 对联系人进行转换成拼音
	private int height;// 字体高度
	private boolean flag = false;// 选中标签
	private ContactsFriendsAdapter adapter;// ListVeiw适配器
	private LinearLayout linearlayout;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_news_contacts_friends,
				container, false);
		listView = (ListView) view.findViewById(R.id.contacts_friends_listview);
		txCenter = (TextView) view
				.findViewById(R.id.contacts_friends_tv_center);
		layoutIndex = (LinearLayout) view
				.findViewById(R.id.contacts_friends_linearlayout);
		View headView = inflater.inflate(
				R.layout.headview_news_contacts_friends, null); //头部布局（我的好友一栏）
		linearlayout = (LinearLayout) headView
				.findViewById(R.id.headview_news_friends_myfriends);
		listView.addHeaderView(headView);
		initOverload();
		initListView();
		// 我的好友监听器
		linearlayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().startActivity(
						new Intent(getActivity(), MyFriendsActivity.class));
			}
		});
		// 异步加载通讯录里面的好友
		new Thread(new Runnable() {	

			@Override
			public void run() {
				getPhoneContacts();
			}
		}).start();
		return view;
	}

	/**
	 * @Title: initOverload
	 * @Description: 初始化右侧字母栏高度
	 * @param
	 * @return void
	 * @throws
	 */
	private void initOverload() {
		// 这是获取屏幕宽高的监听器
		ViewTreeObserver observer = layoutIndex.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (!flag) {
					// 获取每个字母的高度
					height = layoutIndex.getMeasuredHeight() / indexStr.length;
					getIndexView();
					flag = true;
				}
				return true;
			}
		});
	}

	/**
	 * @Title: getIndexView
	 * @Description: 绘制索引列表(字母列表的监听事件)
	 * @param
	 * @return void
	 * @throws
	 */
	public void getIndexView() {
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, height);
		for (int i = 0; i < indexStr.length; i++) {
			final TextView tv = new TextView(this.getActivity());// 右侧字母索引
			tv.setLayoutParams(params);
			tv.setText(indexStr[i]);// 右侧字母
			tv.setPadding(10, 0, 10, 0);
			tv.setTextSize(12);
			layoutIndex.addView(tv);// 设置右侧索引布局的触摸监听器
			layoutIndex.setOnTouchListener(new OnTouchListener() {
				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					float y = event.getY();
					int index = (int) (y / height);
					if (index > -1 && index < indexStr.length) {// 防止越界
						String key = indexStr[index];
						if (selector.containsKey(key)) {
							int pos = selector.get(key);
							// 将ListView的item移动到点击相应字母
							if (listView.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有。
								listView.setSelectionFromTop(
										pos + listView.getHeaderViewsCount(), 0);
							} else {
								listView.setSelectionFromTop(pos, 0);// 滑动到第一项
							}
							txCenter.setVisibility(View.VISIBLE);
							txCenter.setText(indexStr[index]);
						} else {
							txCenter.setVisibility(View.VISIBLE);
							txCenter.setText(indexStr[index]);
						}
					}
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						layoutIndex.setBackgroundColor(Color
								.parseColor("#606060"));
						txCenter.setVisibility(View.VISIBLE);
						break;
					case MotionEvent.ACTION_MOVE:
						break;
					case MotionEvent.ACTION_UP:
						layoutIndex.setBackgroundColor(Color
								.parseColor("#00ffffff"));
						txCenter.setVisibility(View.GONE);
						break;
					}
					return true;
				}
			});
		}
	}

	/**
	 * @Title: initListView
	 * @Description: 初始化ListView
	 * @param
	 * @return void
	 * @throws
	 */
	private void initListView() {
		adapter = new ContactsFriendsAdapter(getActivity(), newPersons);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					// parent.getChildAt(position).setBackgroundColor(Color.parseColor("#FFFFFF"));
				} else{
					Intent intent =new Intent();
					String state=newPersons.get(position-1).getStatus();
					if(state!=null&&!"".equals(state)){
					if(state.equals("0")||state.equals("1")||state.equals("2")){	//好友信息界面
						intent.setClass(getActivity(),PersonInformationActivity.class);
						intent.putExtra("by_user_account", newPersons.get(position-1).getUser_account());
						getActivity().startActivity(intent);
						}
					}
				}
			}

		});
	}

	/**
	 * @Title: sortList
	 * @Description: 重新排序获得一个新的List集合
	 * @param @param allNames
	 * @return void
	 * @throws
	 */
	private void sortList(String[] allNames) {
		String pre = null;
		for (int i = 0; i < allNames.length; i++) {
			if (allNames[i].length() != 1) {
				for (int j = 0; j < persons.size(); j++) {
					pre = persons.get(j).getPinyinName();// 获得用户名称的拼音
					if (allNames[i].substring(0, allNames[i].length() - 4)
							.equals(pre)) {// allNames数组中用户名称后面加了一串字符串，以便于索引字母区分
						ContactsPerson p;
						if (65 <= pre.charAt(0) && pre.charAt(0) <= 90
								|| 97 <= pre.charAt(0) && pre.charAt(0) <= 122) {// 条件为首字母在A-Z,a-z
							p = new ContactsPerson(persons.get(j).getName(),
									persons.get(j).getPinyinName(), persons
											.get(j).getPhone());
						} else {
							p = new ContactsPerson(persons.get(j).getName(),
									"#" + persons.get(j).getPinyinName(),
									persons.get(j).getPhone());
						}
						newPersons.add(p);
					}
				}
			} else {
				newPersons.add(new ContactsPerson(allNames[i]));
			}
		}
		newPersons=ListUtils.removeRepeatContact(newPersons);
	}

	/**
	 * @Title: getPhoneContacts
	 * @Description: 读取手机联系人的信息
	 * @param
	 * @return void
	 * @throws
	 */
	private void getPhoneContacts() {
		ContentResolver resolver = getActivity().getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, null, null,
				null, null); // 传入正确的uri
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				String phoneNumber = phoneCursor.getString(phoneCursor
						.getColumnIndex(Phone.NUMBER)); // 获取联系人number
				if (!TextUtils.isEmpty(phoneNumber)) {
					int nameIndex = phoneCursor
							.getColumnIndex(Phone.DISPLAY_NAME); // 获取联系人name
					String name = phoneCursor.getString(nameIndex);
					// 以下是我自己的数据封装。
					ContactsPerson cp = new ContactsPerson();
					cp.setName(name);
					if(phoneNumber.startsWith("+86")){
						phoneNumber=phoneNumber.substring(3, phoneNumber.length());
					}
					cp.setPhone(phoneNumber.replaceAll(" ", ""));
					persons.add(cp);
				}
			}
			phoneCursor.close();
			Message msg = new Message();
			msg.what = 1;
			handler.sendMessage(msg);
		}
	}

	/****
	 * @ClassName: MyContactsTask
	 * @Description: 根据通讯录的号码来验证此手机号码是不是啪啪好友
	 * @author 菲尔普斯
	 * @date 2015-1-19 上午9:10:21
	 * 
	 */
	class MyContactsTask extends AsyncTask<Void, Void, String[]> {
		String[] mobile = new String[newPersons.size()];

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			for (int i = 0; i < newPersons.size(); i++) {
				if (newPersons.get(i).getPhone() != null&& !"".equals(newPersons.get(i).getPhone())) {
					mobile[i] = newPersons.get(i).getPhone();
				} else {
					mobile[i] = "101" + i; // 没有电话号码传一个固定的自增的变量(表示是索引字母)
				}
			}
		}

		@Override
		protected String[] doInBackground(Void... params) {
			String json = null;
			json = com.alibaba.fastjson.JSONArray.toJSONString(mobile);
			String[] result = MyHttpClient.postDataToService(getActivity(),
					APPConstants.URL_APP2_HOST_NAME
							+ APPConstants.URL_FRIENDS_LISTCONTACTSAPP2,
					MyApplication.app2Token,
					new String[] { "user_account", "mobile" },
					new String[] {
							MyApplication.preferences.getString(
									DBConstants.USER_ACCOUNT, ""), json },
					null, null);
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			if(result!=null){
			JsonObjectErrorDaoImpl.resolveJson(getActivity(), result,
					new JsonObjectErrorDao() { // 接口回调

						@Override
						public void disposeJsonObj(final org.json.JSONObject obj) {
							new Thread(new Runnable() {
								
								@Override
								public void run() {		//启动线程去解析json(因为手机联系人的数量比较大，开启线程解析)
									for (int i = 0; i < newPersons.size(); i++) {
										try {  
											JSONObject obj1 = obj.getJSONObject(mobile[i]);
											for (int j = 0; j < newPersons.size(); j++) {
												if (mobile[i].equals(newPersons.get(j).getPhone())) {
													String state = obj1.optString("status");
													newPersons.get(j).setStatus(state);
													if (!state.equals("-2")&& !state.equals("-1")) {// 说明已经注册了啪啪，但不是好友
															String account = obj1.optString("user_account");
															newPersons.get(j).setUser_account(account);
															newPersons.get(j).setExtend_user_account(obj.optString("extend_user_account")); 
															String nickName = obj1.optString("user_nickname");
															String logo = obj1.optString("user_logo_200");
															if (nickName!= null&&!"".equals(nickName)) {
																newPersons.get(j).setUser_nickname(nickName);
															}
															if (logo!= null&&!"".equals(logo)) {
																newPersons.get(j).setUser_logo(logo);
															}
													}
												}
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
									Message msg = new Message();
									msg.what=2;
									handler.sendMessage(msg);
								}
							}).start();
						}
					});
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {// 读取通讯录之后调用的handler
				layoutIndex.setBackgroundColor(Color.parseColor("#00ffffff"));// 设置背景颜色
				txCenter.setVisibility(View.GONE);
				String[] allNames = ChineseTransferUtils.sortIndex(persons);// 装载非字母为首的数组
				sortList(allNames);
				for (int j = 0; j < indexStr.length; j++) {// 循环字母表，找出newPersons中对应字母的位置
					for (int i = 0; i < newPersons.size(); i++) {
						if (newPersons.get(i).getName().equals(indexStr[j])) {
							selector.put(indexStr[j], i);
						}
					}
				}
				adapter.notifyDataSetChanged();
				if(NetWorkState.checkNet(getActivity())&&newPersons.size()>0){
					new MyContactsTask().execute();
				}
			} else if (msg.what == 2) {
				adapter.notifyDataSetChanged();
			}
		};
	};
}
