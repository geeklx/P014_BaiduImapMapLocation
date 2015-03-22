package com.liangxiao.petrolstation;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.liangxiao.petrolstation.bean.Petrol;
import com.liangxiao.petrolstation.bean.Station;
import com.liangxiao.petrolstation.util.StationData;

public class MainActivity extends Activity implements OnClickListener {

	private TextView tv_title_button;// 3km>
	private ImageView iv_loc;// 左边定位
	private ImageView iv_list;// 右边周围的个体信息
	private LinearLayout ll_summary;// 查看详情
	private TextView tv_name;// 当前加油站名字
	private TextView tv_distance;// 当前加油站距离
	private TextView tv_price_a;// 当前加油站价格a
	private TextView tv_price_b;// 当前加油站价格b

	private Context mContext;
	private Toast mToast;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocationClient;
	private Dialog selectDialog, loadingDialog;
	private BDLocationListener mListener;

	private StationData stationData;
	private BDLocation loc;
	private ArrayList<Station> mList;
	private Station mStation;
	private int mDistance;
	private Marker lastMarker;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x01:
				mList = (ArrayList<Station>) msg.obj;
				System.out.println(mList.toString());
				setMarker(mList);
				// showLayoutInfo("1", mList.get(0));
				loadingDialog.dismiss();
				break;
			case 0x02:
				loadingDialog.dismiss();
				showToast(String.valueOf(msg.obj));
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 添加地图覆盖物
	 * 
	 * @param list
	 */
	private void setMarker(ArrayList<Station> list) {
		View view = LayoutInflater.from(mContext)
				.inflate(R.layout.marker, null);
		final TextView tv = (TextView) view.findViewById(R.id.tv_marker);
		for (int i = 0; i < list.size(); i++) {
			Station s = list.get(i);// 获取一个Station对象
			tv.setText(i + 1 + "");// 123456789
			if (i == 0) {
				tv.setBackgroundResource(R.drawable.icon_focus_mark);// 蓝色
			} else {
				tv.setBackgroundResource(R.drawable.icon_mark);// 红色
			}
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
			LatLng latLng = new LatLng(s.getLat(), s.getLon());// 纬度，经度
			Bundle b = new Bundle();
			b.putParcelable("s", list.get(i));
			OverlayOptions oo = new MarkerOptions().position(latLng)
					.icon(bitmap).title(i + 1 + "").extraInfo(b);
			if (i == 0) {
				lastMarker = (Marker) mBaiduMap.addOverlay(oo);
				mStation = s;
				showLayoutInfo(i + 1 + "", s);
			} else {
				mBaiduMap.addOverlay(oo);
			}
		}

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				if (lastMarker != null) {
					tv.setText(lastMarker.getTitle());// 123456789
					tv.setBackgroundResource(R.drawable.icon_mark);// 红色
					BitmapDescriptor bitmap = BitmapDescriptorFactory
							.fromView(tv);
					lastMarker.setIcon(bitmap);
				}
				lastMarker = marker;
				String position = marker.getTitle();
				tv.setText(position);// 123456789
				tv.setBackgroundResource(R.drawable.icon_focus_mark);// 蓝色
				BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
				marker.setIcon(bitmap);

				mStation = marker.getExtraInfo().getParcelable("s");
				showLayoutInfo(position, mStation);

				return false;
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// PushManager.startWork(getApplicationContext(),
		// PushConstants.LOGIN_TYPE_API_KEY,
		// Utils.getMetaValue(MainActivity.this, "api_key"));
		mContext = this;
		stationData = new StationData(mHandler);
		initView();
	}

	private void initView() {
		tv_title_button = (TextView) findViewById(R.id.tv_title_button);
		tv_title_button.setText("3km >");
		tv_title_button.setVisibility(View.VISIBLE);
		tv_title_button.setOnClickListener(this);// 更换km事件部分

		iv_loc = (ImageView) findViewById(R.id.iv_loc);
		iv_loc.setOnClickListener(this);// 左边定位功能事件部分

		iv_list = (ImageView) findViewById(R.id.iv_list);
		iv_list.setOnClickListener(this);// 周围列表事件部分

		ll_summary = (LinearLayout) findViewById(R.id.ll_summary);
		ll_summary.setOnClickListener(this);// 查看详情事件部分

		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_price_a = (TextView) findViewById(R.id.tv_price_a);
		tv_price_b = (TextView) findViewById(R.id.tv_price_b);

		// 初始化部分
		mMapView = null;
		mBaiduMap = null;
		mLocationClient = null;
		mListener = new MyLocationListener();
		mStation = null;
		mDistance = 3000;
		lastMarker = null;

		// 地图图层部分
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.showScaleControl(false);
		mMapView.showZoomControls(false);
		mBaiduMap = mMapView.getMap();// 传值给百度api

		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
		mBaiduMap.setMyLocationEnabled(true);

		mLocationClient = new LocationClient(mContext);
		mLocationClient.registerLocationListener(mListener);// 注册定位信息

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 高精度模式;Battery_Saving
															// 低精度模式
		option.setCoorType("bd09ll");// 返回国测局经纬度坐标系：gcj02 返回百度墨卡托坐标系 ：bd09
										// 返回百度经纬度坐标系 ：bd09ll
		option.setScanSpan(0);// 设置扫描间隔，单位毫秒，当<1000(1s)时，定时定位无效
		option.setIsNeedAddress(true);// 设置是否需要地址信息，默认为无地址
		option.setNeedDeviceDirect(true);// 在网络定位时，是否需要设备方向
		mLocationClient.setLocOption(option);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 周围列表页面
		case R.id.iv_list:
			Intent listIntent = new Intent(mContext, StationListActivity.class);
			listIntent.putParcelableArrayListExtra("list", mList);
			listIntent.putExtra("loclat", loc.getLatitude());// 当前位置的经度
			listIntent.putExtra("loclon", loc.getLongitude());// 当前位置的纬度
			startActivity(listIntent);

			break;
		case R.id.iv_loc:
			int r = mLocationClient.requestLocation();
			switch (r) {
			case 1:
				showToast("服务没有启动");
				break;
			case 2:
				showToast("没有监听函数");
				break;
			case 6:
				showToast("请求时间过短");
				break;
			default:
				break;
			}
			break;
		case R.id.tv_title_button:
			showSelectDialog();
			break;
		// 当前位置详情页面
		case R.id.ll_summary:
			Intent infoIntent = new Intent(mContext, StationInfoActivity.class);
			infoIntent.putExtra("s", mStation);
			infoIntent.putExtra("loclat", loc.getLatitude());// 当前位置的经度
			infoIntent.putExtra("loclon", loc.getLongitude());// 当前位置的纬度
			startActivity(infoIntent);
			break;
		default:
			break;
		}
	}

	/**
	 * 加载提示框部分
	 */
	@SuppressLint("InflateParams")
	private void showLoadingDialog() {
		if (loadingDialog != null) {
			loadingDialog.show();
			return;
		}
		loadingDialog = new Dialog(mContext, R.style.dialog_loading);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_loading, null);
		loadingDialog.setContentView(view, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		loadingDialog.setCancelable(false);
		loadingDialog.show();
	}

	/**
	 * 选择3km部分dialog的显示
	 */
	private void showSelectDialog() {
		if (selectDialog != null) {
			selectDialog.show();
			return;
		}
		selectDialog = new Dialog(mContext, R.style.dialog);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_distance, null);
		selectDialog.setContentView(view, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		selectDialog.setCanceledOnTouchOutside(true);
		selectDialog.show();

	}

	/**
	 * 选择3kmdialog的点击事件部分
	 * 
	 * @param v
	 */
	public void onDialogClick(View v) {
		switch (v.getId()) {
		case R.id.bt_3km:
			distanceSearch("3km >", 3000);
			break;
		case R.id.bt_5km:
			distanceSearch("5km >", 5000);
			break;
		case R.id.bt_8km:
			distanceSearch("8km >", 8000);
			break;
		case R.id.bt_10km:
			distanceSearch("10km >", 10000);
			break;
		default:
			break;
		}
	}

	private void distanceSearch(String text, int distance) {
		mDistance = distance;
		tv_title_button.setText(text);
		searchStation(loc.getLatitude(), loc.getLongitude(), distance);
		selectDialog.dismiss();

	}

	private void showLayoutInfo(String position, Station s) {
		// 给中间的层中的东东赋值
		tv_name.setText(position + "." + s.getName());
		tv_distance.setText(s.getDistance() + "m");
		List<Petrol> list = s.getGastpriceList();
		if (list != null && list.size() > 0) {
			String a = list.get(0).getType().replace("E", "") + "#";
			tv_price_a.setText(a + " " + list.get(0).getPrice());
			if (list.size() > 1) {
				String b = list.get(1).getType().replace("E", "") + "#";
				tv_price_b.setText(b + " " + list.get(1).getPrice());
			}
		}
		ll_summary.setVisibility(View.VISIBLE);
	}

	private void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		}
		mToast.setText(msg);
		mToast.show();
	}

	/**
	 * 定位点击按钮事件部分
	 * 
	 * @author lan
	 * 
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			loc = location;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 定位精度
					.direction(location.getDirection())
					// 定位数据方向
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			// 寻找周围3km
			searchStation(location.getLatitude(), location.getLongitude(),
					mDistance);
		}

	}

	/**
	 * 寻找周围3km
	 * 
	 * @param lat
	 * @param lon
	 * @param distance
	 */
	public void searchStation(double lat, double lon, int distance) {
		showLoadingDialog();
		mBaiduMap.clear();
		ll_summary.setVisibility(View.GONE);
		stationData.getStationData(lat, lon, distance);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
		mLocationClient.stop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
		mLocationClient.start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
		mHandler = null;
	}
}
