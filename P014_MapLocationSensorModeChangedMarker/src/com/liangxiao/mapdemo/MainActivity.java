package com.liangxiao.mapdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.liangxiao.mapdemo.bean.Info;
import com.liangxiao.mapdemo.orientationlistener.MyOrientationListener;
import com.liangxiao.mapdemo.orientationlistener.MyOrientationListener.onOrientationListener;

public class MainActivity extends Activity implements OnClickListener {
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private ImageView iv_location;
	private Context mContext;

	// 定位相关
	private LocationClient mLocationClient;
	private MyLocationListener myLocationListener;
	private boolean isFirstIn = true;
	private BDLocation loc;// 获取当前的定位信息
	// 自定义定位图标
	private BitmapDescriptor mIconLocation;
	// 方向传感器
	private MyOrientationListener myOrientationListener;
	private float mCurrentX;
	private LocationMode mLocationMode;

	// 覆盖物Marker
	private BitmapDescriptor mMarker;
	private LinearLayout mMarkerLy;// marker的布局
	private List<Info> infos = new ArrayList<Info>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initLocation();// 初始化定位

		// 添加覆盖物
		initSetMarker();
		// 地图的点击
		markeronClick();
	}

	/**
	 * 地图的点击
	 */
	private void markeronClick() {
		// 显示层的内容部分
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				Bundle exBundle = marker.getExtraInfo();
				Info info = (Info) exBundle.getSerializable("info");
				ImageView iv = (ImageView) mMarkerLy
						.findViewById(R.id.id_info_img);
				TextView id_info_tv_name = (TextView) mMarkerLy
						.findViewById(R.id.id_info_tv_name);
				TextView id_info_distance = (TextView) mMarkerLy
						.findViewById(R.id.id_info_distance);
				TextView id_info_zan = (TextView) mMarkerLy
						.findViewById(R.id.id_info_zan);
				// 设置各属性值
				iv.setImageResource(info.getImgId());
				id_info_tv_name.setText(info.getName());
				id_info_distance.setText("距离" + info.getDistance() + "米");
				id_info_zan.setText(info.getZan() + "");
				mMarkerLy.setVisibility(View.VISIBLE);

				// 显示悬浮框部分
				InfoWindow infoWindow;
				TextView tv = new TextView(MainActivity.this);
				tv.setBackgroundResource(R.drawable.location_tips);
				tv.setPadding(30, 20, 30, 50);
				tv.setText(info.getName());
				tv.setTextColor(getResources().getColor(R.color.white));

				LatLng latLng = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
				p.y -= 47;
				LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);

				infoWindow = new InfoWindow(tv, ll,
						new OnInfoWindowClickListener() {

							@Override
							public void onInfoWindowClick() {
								mBaiduMap.hideInfoWindow();
							}
						});
				mBaiduMap.showInfoWindow(infoWindow);// 显示悬浮框
				mMarkerLy.setVisibility(View.VISIBLE);// 显示弹出层
				return true;
			}
		});
		// 再次点击消失部分
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				mMarkerLy.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();
			}
		});

	}

	/**
	 * 添加覆盖物
	 */
	private void initSetMarker() {
		mMarker = BitmapDescriptorFactory.fromResource(R.drawable.maker);// 设置图标
		// 赋值部分
		infos.add(new Info(34.242652, 108.971171, R.drawable.a01, "英伦贵族小旅馆",
				"209", 1456));
		infos.add(new Info(34.242952, 108.972171, R.drawable.a02, "沙井国际洗浴会所",
				"897", 456));
		infos.add(new Info(34.242852, 108.973171, R.drawable.a03, "五环服装城",
				"249", 1456));
		infos.add(new Info(34.242152, 108.971971, R.drawable.a04, "老米家泡馍小炒",
				"679", 1456));
		mMarkerLy = (LinearLayout) findViewById(R.id.id_markerly);

	}

	private void initView() {
		mContext = this;
		iv_location = (ImageView) findViewById(R.id.iv_location);
		iv_location.setOnClickListener(this);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(12.0f);// 设置500米显示15.0f
		mBaiduMap.setMapStatus(msu);

	}

	private void initLocation() {
		// 普通模式
		mLocationMode = LocationMode.NORMAL;

		mLocationClient = new LocationClient(this);
		myLocationListener = new MyLocationListener();
		// 注册定位
		mLocationClient.registerLocationListener(myLocationListener);

		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);// 每个多少秒请求1000=1second
		mLocationClient.setLocOption(option);

		// 初始化图标
		mIconLocation = BitmapDescriptorFactory
				.fromResource(R.drawable.navi_map_gps_locked);
		// 初始化方向传感器
		myOrientationListener = new MyOrientationListener(mContext);
		myOrientationListener
				.setmOrientationListener(new onOrientationListener() {

					@Override
					public void onOrientationChanged(float x) {
						mCurrentX = x;
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.id_map_common:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);// 设置普通地图
			break;
		case R.id.id_map_site:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);// 设置卫星地图
			break;
		case R.id.id_map_traffic:
			if (mBaiduMap.isTrafficEnabled()) {
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("实时交通(off)");
			} else {
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("实时交通(on)");
			}
			break;
		case R.id.id_map_location:
			centerToMyLocation();
			break;
		case R.id.id_map_mode_common:
			mLocationMode = LocationMode.NORMAL;
			break;
		case R.id.id_map_mode_following:
			mLocationMode = LocationMode.FOLLOWING;
			break;

		case R.id.id_map_mode_compass:
			mLocationMode = LocationMode.COMPASS;
			break;
		case R.id.id_map_addOverlay:
			// 添加覆盖物
			addOverLays(infos);
			// mLocationMode =
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			// 更新经纬度
			loc = location;
			MyLocationData data = new MyLocationData.Builder()//
					.direction(mCurrentX)// 方向传感器的X值
					.accuracy(location.getRadius())// 定位精度
					.latitude(location.getLatitude())// 定位数据方向
					.longitude(location.getLongitude())//
					.build();

			mBaiduMap.setMyLocationData(data);
			// 配置图标
			MyLocationConfiguration config = new MyLocationConfiguration(
					mLocationMode, true, mIconLocation);
			mBaiduMap.setMyLocationConfigeration(config);

			if (isFirstIn) {
				centerToMyLocation();
				isFirstIn = false;

			} else {
				// wucaozuo
			}

		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 开启定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// 开启方向传感器
		myOrientationListener.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 停止定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// 停止方向传感器
		myOrientationListener.stop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_location:

			showToast("位置:" + loc.getAddrStr() + ",经度:" + loc.getLatitude()
					+ ",纬度:" + loc.getLongitude());
			centerToMyLocation();
			// int r = mLocationClient.requestLocation();
			// switch (r) {
			// case 1:
			// showToast("服务没有启动");
			// break;
			// case 4:
			// showToast("没有监听函数");
			// break;
			// case 6:
			// showToast("请求时间过短");
			// break;
			// default:
			// break;
			// }
			break;

		default:
			break;
		}
	}

	/**
	 * 定位到我的位置
	 */
	private void centerToMyLocation() {
		LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		// 动画转到定位
		mBaiduMap.animateMapStatus(msu);
	}

	private Toast mToast;

	private void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		}
		mToast.setText(msg);
		mToast.show();
	}

	/**
	 * 添加覆盖物，从服务器获取Json数据返回ArrayList传给List<Info> infos的infos
	 * 
	 * @param infos
	 */
	private void addOverLays(List<Info> infos) {
		mBaiduMap.clear();
		LatLng latLng = null;
		Marker marker = null;
		OverlayOptions options;

		for (Info info : infos) {
			// 经纬度
			latLng = new LatLng(info.getLatitude(), info.getLongitude());
			// 图标
			options = new MarkerOptions().position(latLng).icon(mMarker)
					.zIndex(5);
			marker = (Marker) mBaiduMap.addOverlay(options);
			Bundle bundle = new Bundle();
			bundle.putSerializable("info", info);
			marker.setExtraInfo(bundle);
		}

		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(msu);

	}
}
