package com.slife.gopapa.activity.login;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseNoServiceActivity;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.APPConstants;
import com.slife.gopapa.http.MyHttpClient;
import com.slife.gopapa.model.City;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * @ClassName: SelectCityActivity
 * @Description:选择城市,有三层(省市区)//此类已被com.slife.gopapa.view.citypicker.CityDialog替代
 * @author 肖邦
 * @date 2015-1-26 下午5:22:50
 */
@ContentView(R.layout.activity_select_city)
public class SelectCityActivity extends BaseNoServiceActivity {
	@ViewInject(R.id.activity_selelct_city_list)
	private ListView lv;
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;
	@ViewInject(R.id.activity_selelct_city)
	private TextView tvCity;
	private SelectCityAdapter adapter;
	private ArrayList<City> list;
	public static final int RESULTCODE = 1003;// 结果码
	private SelectCityActivity activity;
	private StringBuffer address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		address = new StringBuffer();
		tvTitle.setText("选择城市");
		initView();
		new CityTask().execute(APPConstants.URL_CITY + MyApplication.commonToken);
	}

	private void initView() {
		list = new ArrayList<City>();
		adapter = new SelectCityAdapter(getApplicationContext(), list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				City city = list.get(position);
				if (city != null && city.getSc_id().length() <= 6) {
					lv.setSelection(0);// 将listview置顶
					int cityCodeLength = city.getSc_id().length();
					if (cityCodeLength == 2 || cityCodeLength == 4) {
						address.append(city.getSc_name()).append("-");
						tvCity.setText(address.toString());
						new CityTask().execute(APPConstants.URL_CITY + MyApplication.commonToken + "&sc_id=" + city.getSc_id());
					} else if (cityCodeLength == 6) {
						if (getIntent() == null) {
							address.append(city.getSc_name());
							Intent data = new Intent();
							data.putExtra("city_name", address.toString());
							Editor editor = MyApplication.preferences.edit();// 获取编辑器
							editor.putInt("province_code", Integer.parseInt(city.getSc_id().substring(0, 2)));// 省号
							editor.putInt("city_code", Integer.parseInt(city.getSc_id().substring(0, 4)));// 市号
							editor.putInt("district_code", Integer.parseInt(city.getSc_id()));// 区号
							editor.putString("baseAddress", address.toString());
							editor.commit();// 提交修改
							setResult(SelectCityActivity.RESULTCODE, data);
							finish();
						} else {
							address.append(city.getSc_name());
							Intent data = new Intent();
							data.putExtra("area_arr", address.toString().split("-"));
							data.putExtra("province_code", Integer.parseInt(city.getSc_id().substring(0, 2)));
							data.putExtra("city_code", Integer.parseInt(city.getSc_id().substring(0, 4)));
							data.putExtra("district_code", Integer.parseInt(city.getSc_id()));
							setResult(SelectCityActivity.RESULTCODE, data);
							finish();
						}
					}
				}
			}
		});
	}

	@OnClick({ R.id.common_title_back })
	public  void onclick(View v) {
		if (v.getId() == R.id.common_title_back) {
			finish();
		}
	}

	/**
	 * 
	 * @ClassName: CityTask
	 * @Description: 从服务器获取城市列表
	 * @author 肖邦
	 * @date 2015-1-20 上午9:45:37
	 * 
	 */
	class CityTask extends AsyncTask<String, Void, ArrayList<City>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			MyProgressDialog.showDialog(activity, "城市选择", "加载城市数据中...");
		}

		@Override
		protected ArrayList<City> doInBackground(String... params) {
			String[] json = null;
			json = MyHttpClient.getJsonFromService(SelectCityActivity.this,params[0]);
			ArrayList<City> cityList = new ArrayList<City>();
			if (json != null) {
				if ("200".equals(json[0])) {
					try {
						JSONArray arr = new JSONArray(json[1]);
						if (arr != null && arr.length() > 0) {
							for (int i = 0; i < arr.length(); i++) {
								JSONObject object = (JSONObject) arr.get(i);
								cityList.add(new City(object.optString("sc_id"), object.optString("sc_name")));
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return cityList;
		}

		@Override
		protected void onPostExecute(ArrayList<City> result) {
			list.clear();
			list.addAll(result);
			adapter.notifyDataSetChanged();
			MyProgressDialog.closeDialog();
			super.onPostExecute(result);
		}
	}

	/**
	 * 
	 * @ClassName: SelectCityAdapter
	 * @Description: 城市列表的适配器
	 * @author 肖邦
	 * @date 2015-1-20 上午9:49:59
	 * 
	 */
	public class SelectCityAdapter extends BaseAdapter {

		private ArrayList<City> cityList;
		private LayoutInflater inflater;

		public SelectCityAdapter(Context context, ArrayList<City> list) {
			super();
			this.cityList = list;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return cityList != null && cityList.size() > 0 ? cityList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return cityList != null && cityList.size() > 0 ? cityList.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SelectCityViewHolder holder;
			if (convertView == null) {
				holder = new SelectCityViewHolder();
				convertView = inflater.inflate(R.layout.listview_select_item, parent, false);
				ViewUtils.inject(holder, convertView);
				convertView.setTag(holder);
			} else {
				holder = (SelectCityViewHolder) convertView.getTag();
			}
			holder.tvName.setText(cityList.get(position).getSc_name());
			if (position == cityList.size() - 1) {
				holder.v.setVisibility(View.GONE);
			} else {
				holder.v.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		public class SelectCityViewHolder {
			@ViewInject(R.id.listview_select_item)
			public TextView tvName;
			@ViewInject(R.id.listview_select_view)
			public View v;
			@ViewInject(R.id.listview_select_view2)
			public View v2;
		}
	}
}
