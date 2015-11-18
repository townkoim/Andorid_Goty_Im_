package com.slife.gopapa.activity.competition;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slife.gopapa.R;
import com.slife.gopapa.activity.BaseActivity;
import com.slife.gopapa.adapter.PoiSearchAdapter;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.view.MyProgressDialog;

/**
 * @ClassName: MapActivity
 * @Description: 选择比赛地点,进入页面时即定位到当前的位置,
 * 				可以手动拖拽标记进行选择,也可以输入关键字搜索
 * @author 肖邦
 * @date 2015-1-26 下午5:19:37
 */
@ContentView(R.layout.activity_map)
public class MapActivity extends BaseActivity implements OnMarkerClickListener, OnMarkerDragListener, OnGetPoiSearchResultListener, android.view.View.OnClickListener {
	@ViewInject(R.id.common_title_back)
	private ImageView imgBack;// 返回键
	@ViewInject(R.id.common_title_name)
	private TextView tvTitle;// 标题
	@ViewInject(R.id.common_title_right)
	private TextView tvRight;// 下一步
	@ViewInject(R.id.activity_map_city)
	private EditText etCity;// 城市
	@ViewInject(R.id.activity_map_detail)
	private EditText etDetail;// 城市相应的详细地址
	@ViewInject(R.id.activity_map_list)
	private ListView lv;// 兴趣点搜索结果对应的列表
	@ViewInject(R.id.activity_map_search)
	private Button btnSearch;//搜索按钮
	private MapView mMapView = null;// 地图控件
	private BaiduMap mBaiduMap; // 地图控制类
	private BDLocation bdLocation; // 地图定位类
	private GeoCoder geoCoder; // 经纬度地理位置坐标反转类
	private OverlayOptions option; // 地图覆盖物
	private MyGeoCoder myGeo; // 自定义类，实现坐标反转接口
	public static final int RESULT_CODE = 1010;
	private double lat;// 纬度
	private double lon;// 经度
	private String address;// 详细地址
	private List<PoiInfo> list;// 兴趣点结合
	private MapActivity activity;//本类对象
	private PoiSearch mPoiSearch = null;// 兴趣点搜索
	private PoiSearchAdapter adapter;// 兴趣点适配器
	private int mPosition = 0;// 详细地址对应的位置

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		ViewUtils.inject(activity);
		intiUI();
		initGetAddress();
		initMyLocation();
	}

	/**
	 * @Title: intiUI
	 * @Description: 初始化UI
	 * @param
	 * @return void
	 * @throws
	 */
	private void intiUI() {
		mMapView = (MapView) findViewById(R.id.activity_map_mapview);
		mBaiduMap = mMapView.getMap();//获得地图
		tvTitle.setText("点击进行定位");// 界面标题
		tvRight.setText("下一步");//
		mBaiduMap.setOnMarkerClickListener(MapActivity.this);// 设置地图标记点击监听器
		mBaiduMap.setOnMarkerDragListener(MapActivity.this);// 设置地图标记拖动监听器
		mPoiSearch = PoiSearch.newInstance();// 获得兴趣点搜索类
		mPoiSearch.setOnGetPoiSearchResultListener(this);// 设置兴趣点搜索结果监听器
		list = new ArrayList<PoiInfo>();//兴趣点集合
		adapter = new PoiSearchAdapter(activity, list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mPosition = position;
				// 选择后弹出对话框确认约赛地点
				new AlertDialog.Builder(activity).setTitle("约赛地点").setMessage(list.get(position).name + ":" + list.get(position).address)
						.setNegativeButton("取消", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// 如果点击取消对话框消失
								dialog.dismiss();
							}
						}).setPositiveButton("确定", new OnClickListener() {
							// 如果点击确定将地址.详细地址.经纬度传回约赛界面
							@Override
							public void onClick(DialogInterface dialog, int which) {

								Intent data = new Intent();
								data.putExtra("name", list.get(mPosition).name);// 地点
								data.putExtra("address", list.get(mPosition).address);// 地点相应的地址
								data.putExtra("lat", String.valueOf(list.get(mPosition).location.latitude));// 纬度
								data.putExtra("lon", String.valueOf(list.get(mPosition).location.longitude));// 经度
								setResult(MapActivity.RESULT_CODE, data);
								activity.finish();
							}
						}).show();
			}
		});
		// EditText文字内容改变的监听器
		etDetail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().isEmpty()) {
					// 如果没有文字就隐藏ListVIew
					lv.setVisibility(View.INVISIBLE);
					return;
				}
			}
		});
		btnSearch.setOnClickListener(this);
	}

	/***
	 * @Title: initGetAddress
	 * @Description: 初始化根据经纬度反地理位置查询的基本类
	 * @param
	 * @return void
	 * @throws
	 */
	private void initGetAddress() {
		geoCoder = GeoCoder.newInstance();
		myGeo = new MyGeoCoder();// 自定义反地理位置查询类
		geoCoder.setOnGetGeoCodeResultListener(myGeo);// 设置反地理查询监听器
	}

	/***
	 * @Title: initMyLocation
	 * @Description: 初始化我的位置
	 * @param
	 * @return void
	 * @throws
	 */
	private void initMyLocation() {
		MyApplication.mLocationClient.requestLocation();// 定位
		bdLocation = MyApplication.mLocationClient.getLastKnownLocation();// 获得定位信息
		if (bdLocation == null || bdLocation.getLongitude() == 0 || bdLocation.getLatitude() == 0) {
			// 定位不成功
			Toast.makeText(MapActivity.this, "定位失败，正在重新定位", Toast.LENGTH_SHORT).show();
			tvRight.setVisibility(View.INVISIBLE);// 让下一步按钮消失
			MyApplication.mLocationClient.requestLocation();
		} else {
			// 定位成功
			tvRight.setVisibility(View.VISIBLE);// 让下一步按钮显示
			Toast.makeText(MapActivity.this, bdLocation.getAddrStr(), Toast.LENGTH_SHORT).show();
			lat = bdLocation.getLatitude();
			lon = bdLocation.getLongitude();
			address = bdLocation.getAddrStr();
			initMarket();
		}
	}

	/***
	 * @Title: initMarket
	 * @Description: 在地图上构建覆盖物
	 * @param
	 * @return void
	 * @throws
	 */
	private void initMarket() {
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);// 设置地图定位类型
		mBaiduMap.setTrafficEnabled(true); // 开启交通图
		MapStatusUpdate statusUpdate = MapStatusUpdateFactory.zoomTo(19);// 地图当前级别
		mBaiduMap.setMapStatus(statusUpdate);
		LatLng point = new LatLng(bdLocation.getLatitude(), // 构建坐标点的经纬度
				bdLocation.getLongitude());
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
		if (option == null) { // 判断地图上是否已经存在定位标记
			option = new MarkerOptions().position(point).icon(bitmap).draggable(true);// 设置标记的经纬度,图片,可拖动
			mBaiduMap.addOverlay(option);
		} else {//重新部署定位标记
			mBaiduMap.clear();
			option = null;
			option = new MarkerOptions().position(point).icon(bitmap).draggable(true);
			mBaiduMap.addOverlay(option);
		}
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);// 将覆盖物设置为地图中心
		mBaiduMap.animateMapStatus(u);
	}

	@Override
	public void onMarkerDrag(Marker marker) {

	}

	// 地图覆盖物拖拽物事件结束
	@Override
	public void onMarkerDragEnd(Marker marker) {
		if (NetWorkState.checkNet(MapActivity.this)) {// 拖拽完标记之后,如果可以上网,这进行搜索
			geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(marker.getPosition()));
		} else {
			Toast.makeText(MapActivity.this, "没有网络，定位失败。。", Toast.LENGTH_SHORT).show();
			tvRight.setVisibility(View.INVISIBLE);// 让下一步按钮消失
		}
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
	}

	// 覆盖物点击事件
	@Override
	public boolean onMarkerClick(Marker marker) {
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(marker.getPosition()));
		return true;
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		mPoiSearch.destroy();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (NetWorkState.checkNet(MapActivity.this)) {
			initMyLocation();
		} else {
			Toast.makeText(MapActivity.this, "没有网络", Toast.LENGTH_SHORT).show();
			tvRight.setVisibility(View.INVISIBLE);// 让下一步按钮消失
		}
		mMapView.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	/**
	 * 
	 * @ClassName: MyGeoCoder
	 * @Description: TODO根据坐标反地理位置查询类
	 * @author 肖邦
	 * @date 2014-11-19 上午9:58:43
	 * 
	 */
	public class MyGeoCoder implements OnGetGeoCoderResultListener {
		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			// 如果有结果则将地址.经纬度赋值给全局变量
			Toast.makeText(MapActivity.this, result.getAddress(), Toast.LENGTH_SHORT).show();
			address = result.getAddress();
			lon = result.getLocation().longitude;
			lat = result.getLocation().latitude;
			tvRight.setVisibility(View.VISIBLE);// 让下一步按钮消失
		}
	}

	@OnClick({ R.id.common_title_back, R.id.common_title_name, R.id.common_title_right })
	public void onclick(View v) {
		if (v.getId() == R.id.common_title_name) {
			initMyLocation();
		} else if (v.getId() == R.id.common_title_right) {
			if(lat != 0 && lon != 0){
				Intent data = new Intent();
				// 选择地点之后将地址 经纬度返回到上一个界面
				data.putExtra("name", address);
				data.putExtra("address", address);
				data.putExtra("lat", String.valueOf(lat));
				data.putExtra("lon", String.valueOf(lon));
				setResult(MapActivity.RESULT_CODE, data);
				MapActivity.this.finish();
			}else{
				Toast.makeText(activity, "未成功定位", Toast.LENGTH_SHORT).show();
			}
		} else if (v.getId() == R.id.common_title_back) {
			activity.finish();
		}

	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
	}

	/**
	 * (非 Javadoc) Title: onGetPoiResult Description: Poi搜索完成回调方法
	 * 
	 * @param result
	 * @see com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener#onGetPoiResult(com.baidu.mapapi.search.poi.PoiResult)
	 */
	@Override
	public void onGetPoiResult(PoiResult result) {
		MyProgressDialog.closeDialog();
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(MapActivity.this, "未找到结果", Toast.LENGTH_LONG).show();
			lv.setVisibility(View.INVISIBLE);
			return;
		} else if (result.error == SearchResult.ERRORNO.NO_ERROR) {// 有结果
			List<PoiInfo> pi = result.getAllPoi();//获得Poi集合
			list.clear();
			if (pi.size() > 0) {
				for (PoiInfo info : pi) {
					list.add(info);
				}
				adapter.notifyDataSetChanged();
			}
		} else if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(MapActivity.this, strInfo, Toast.LENGTH_LONG).show();
			lv.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * (非 Javadoc) Title: onClick Description:
	 * 点击下一步进行的操作
	 * @param v
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (!"".equals(etDetail.getText()) && !"".equals(etCity.getText())) {
			// 如果有文字就加载兴趣点搜索并显示ListVIew
			mPoiSearch.searchInCity((new PoiCitySearchOption()).city(etCity.getText().toString()).keyword(etDetail.getText().toString()).pageCapacity(50));
			lv.setVisibility(View.VISIBLE);
			MyProgressDialog.showDialog(activity, "地点", "加载地点信息中...",true);
		} else {
			Toast.makeText(activity, "请输入完整信息", Toast.LENGTH_LONG).show();
			lv.setVisibility(View.INVISIBLE);
		}
	}
}
