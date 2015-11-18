package com.slife.gopapa.view.schoolpicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.slife.gopapa.R;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.view.citypicker.ScrollerNumberPicker;
import com.slife.gopapa.view.citypicker.ScrollerNumberPicker.OnSelectListener;

/**
 * 城市Picker
 * 
 * @author zd
 * 
 */
public class SchoolPicker extends LinearLayout {
	private static final String TAG = "SchoolPicker";
	private int province_position = 0;
	private int school_position = 0;
	/** 滑动控件 */
	private ScrollerNumberPicker provincePicker;
	private ScrollerNumberPicker cityPicker;
	/** 选择监听 */
	private OnSelectingListener onSelectingListener;
	/** 刷新界面 */
	private static final int REFRESH_VIEW = 0x001;
	/** 临时日期 */
	private int tempProvinceIndex = -1;
	private int temCityIndex = -1;
	private Context context;
	private List<Schoolinfo> province_list = new ArrayList<Schoolinfo>();
	private HashMap<String, List<Schoolinfo>> school_map = new HashMap<String, List<Schoolinfo>>();
	private static ArrayList<String> province_list_code = new ArrayList<String>();
	private static ArrayList<String> city_list_code = new ArrayList<String>();

	private SchoolcodeUtil citycodeUtil;
	private String city_code_string;
	private String city_string;
	private String school;
	private String schoolId ="1001";
	public SchoolPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		getaddressinfo();
		// TODO Auto-generated constructor stub
	}

	public SchoolPicker(Context context) {
		super(context);
		this.context = context;
		getaddressinfo();
		// TODO Auto-generated constructor stub
	}

	// 获取城市信息
	private void getaddressinfo() {
		// TODO Auto-generated method stub
		// 读取城市信息string
		JSONParser parser = new JSONParser();
		String area_str = FileUtil.readAssets(context, "school.json");
		province_list = parser.getJSONParserResult(area_str, "area0");
		// citycodeUtil.setProvince_list_code(parser.province_list_code);
		school_map = parser.getJSONParserResultArray(area_str, "area1");
		// citycodeUtil.setCity_list_code(parser.city_list_code);
		province_position = MyApplication.preferences.getInt("SchoolProvincePosition", 0);
		school_position = MyApplication.preferences.getInt("SchoolPosition", 0);
	}

	public static class JSONParser {
		public ArrayList<String> province_list_code = new ArrayList<String>();
		public ArrayList<String> city_list_code = new ArrayList<String>();

		public List<Schoolinfo> getJSONParserResult(String JSONString, String key) {
			List<Schoolinfo> list = new ArrayList<Schoolinfo>();
			JsonObject result = new JsonParser().parse(JSONString).getAsJsonObject().getAsJsonObject(key);

			Iterator iterator = result.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator.next();
				Schoolinfo cityinfo = new Schoolinfo();

				cityinfo.setSchool_name(entry.getValue().getAsString());
				cityinfo.setId(entry.getKey());
				province_list_code.add(entry.getKey());
				list.add(cityinfo);
			}
			return list;
		}

		public HashMap<String, List<Schoolinfo>> getJSONParserResultArray(String JSONString, String key) {
			HashMap<String, List<Schoolinfo>> hashMap = new HashMap<String, List<Schoolinfo>>();
			JsonObject result = new JsonParser().parse(JSONString).getAsJsonObject().getAsJsonObject(key);

			Iterator iterator = result.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator.next();
				List<Schoolinfo> list = new ArrayList<Schoolinfo>();
				JsonArray array = entry.getValue().getAsJsonArray();
				for (int i = 0; i < array.size(); i++) {
					Schoolinfo cityinfo = new Schoolinfo();
					cityinfo.setSchool_name(array.get(i).getAsJsonArray().get(0).getAsString());
					cityinfo.setId(array.get(i).getAsJsonArray().get(1).getAsString());
					city_list_code.add(array.get(i).getAsJsonArray().get(1).getAsString());
					list.add(cityinfo);
				}
				hashMap.put(entry.getKey(), list);
			}
			return hashMap;
		}
	}

	/**
	 * (非 Javadoc) Title: onFinishInflate Description: 加载完XML之后，加载城市数据
	 * 
	 * @see android.view.View#onFinishInflate()
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.school_picker, this);
		citycodeUtil = SchoolcodeUtil.getSingleton();
		// 获取控件引用
		provincePicker = (ScrollerNumberPicker) findViewById(R.id.province);

		cityPicker = (ScrollerNumberPicker) findViewById(R.id.city);

		provincePicker.setData(citycodeUtil.getProvince(province_list));
		provincePicker.setDefault(province_position);

		if (citycodeUtil.getProvince_list_code().size() > province_position) {
			cityPicker.setData(citycodeUtil.getCity(school_map, citycodeUtil.getProvince_list_code().get(province_position)));
			cityPicker.setDefault(school_position);
			school = citycodeUtil.getCity(school_map, citycodeUtil.getProvince_list_code().get(province_position)).get(school_position);
		} else {
			cityPicker.setData(citycodeUtil.getCity(school_map, citycodeUtil.getProvince_list_code().get(0)));
			cityPicker.setDefault(0);
			school = citycodeUtil.getCity(school_map, citycodeUtil.getProvince_list_code().get(0)).get(0);
		}
		provincePicker.setOnSelectListener(new OnSelectListener() {

			@Override
			public void endSelect(int id, String text) {
				city_code_string = citycodeUtil.getCity_list_code().get(0);
				school = citycodeUtil.getCity(school_map, citycodeUtil.getProvince_list_code().get(id)).get(0);
				schoolId = citycodeUtil.getCity_list_code().get(0);
				province_position = id;
				school_position = 0;
				if (text.equals("") || text == null)
					return;
				if (tempProvinceIndex != id) {
					String selectDay = cityPicker.getSelectedText();
					if (selectDay == null || selectDay.equals(""))
						return;
					// 城市数组
					cityPicker.setData(citycodeUtil.getCity(school_map, citycodeUtil.getProvince_list_code().get(id)));
					cityPicker.setDefault(0);
					int lastDay = Integer.valueOf(provincePicker.getListSize());
					if (id > lastDay) {
						provincePicker.setDefault(lastDay - 1);
					}
				}
				tempProvinceIndex = id;
				Message message = new Message();
				message.what = REFRESH_VIEW;
				handler.sendMessage(message);
			}

			@Override
			public void selecting(int id, String text) {
				// TODO Auto-generated method stub
			}
		});
		cityPicker.setOnSelectListener(new OnSelectListener() {

			@Override
			public void endSelect(int id, String text) {
				school = text;
				schoolId = citycodeUtil.getCity_list_code().get(id);
				school_position = id;
				if (text.equals("") || text == null)
					return;
				if (temCityIndex != id) {
					String selectDay = provincePicker.getSelectedText();
					if (selectDay == null || selectDay.equals(""))
						return;
					int lastDay = Integer.valueOf(cityPicker.getListSize());
					if (id > lastDay) {
						cityPicker.setDefault(lastDay - 1);
					}
				}
				temCityIndex = id;
				Message message = new Message();
				message.what = REFRESH_VIEW;
				handler.sendMessage(message);
			}

			@Override
			public void selecting(int id, String text) {

			}
		});
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH_VIEW:
				if (onSelectingListener != null)
					onSelectingListener.selected(true);
				break;
			default:
				break;
			}
		}

	};

	public void setOnSelectingListener(OnSelectingListener onSelectingListener) {
		this.onSelectingListener = onSelectingListener;
	}

	public String getCity_code_string() {
		return city_code_string;
	}

	public String getCity_string() {
		city_string = provincePicker.getSelectedText() + cityPicker.getSelectedText();
		return cityPicker.getSelectedText();
	}

	public String getProvince_string() {
		return provincePicker.getSelectedText();
	}

	public int getProvincePosition() {
		return province_position;
	}

	public int getCityPosition() {
		return school_position;
	}

	public String getSchool() {
		return school;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public interface OnSelectingListener {

		public void selected(boolean selected);
	}
}
