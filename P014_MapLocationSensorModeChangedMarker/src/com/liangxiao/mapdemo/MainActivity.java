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

	// ��λ���
	private LocationClient mLocationClient;
	private MyLocationListener myLocationListener;
	private boolean isFirstIn = true;
	private BDLocation loc;// ��ȡ��ǰ�Ķ�λ��Ϣ
	// �Զ��嶨λͼ��
	private BitmapDescriptor mIconLocation;
	// ���򴫸���
	private MyOrientationListener myOrientationListener;
	private float mCurrentX;
	private LocationMode mLocationMode;

	// ������Marker
	private BitmapDescriptor mMarker;
	private LinearLayout mMarkerLy;// marker�Ĳ���
	private List<Info> infos = new ArrayList<Info>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initLocation();// ��ʼ����λ

		// ��Ӹ�����
		initSetMarker();
		// ��ͼ�ĵ��
		markeronClick();
	}

	/**
	 * ��ͼ�ĵ��
	 */
	private void markeronClick() {
		// ��ʾ������ݲ���
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
				// ���ø�����ֵ
				iv.setImageResource(info.getImgId());
				id_info_tv_name.setText(info.getName());
				id_info_distance.setText("����" + info.getDistance() + "��");
				id_info_zan.setText(info.getZan() + "");
				mMarkerLy.setVisibility(View.VISIBLE);

				// ��ʾ�����򲿷�
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
				mBaiduMap.showInfoWindow(infoWindow);// ��ʾ������
				mMarkerLy.setVisibility(View.VISIBLE);// ��ʾ������
				return true;
			}
		});
		// �ٴε����ʧ����
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
	 * ��Ӹ�����
	 */
	private void initSetMarker() {
		mMarker = BitmapDescriptorFactory.fromResource(R.drawable.maker);// ����ͼ��
		// ��ֵ����
		infos.add(new Info(34.242652, 108.971171, R.drawable.a01, "Ӣ�׹���С�ù�",
				"209", 1456));
		infos.add(new Info(34.242952, 108.972171, R.drawable.a02, "ɳ������ϴԡ����",
				"897", 456));
		infos.add(new Info(34.242852, 108.973171, R.drawable.a03, "�廷��װ��",
				"249", 1456));
		infos.add(new Info(34.242152, 108.971971, R.drawable.a04, "���׼�����С��",
				"679", 1456));
		mMarkerLy = (LinearLayout) findViewById(R.id.id_markerly);

	}

	private void initView() {
		mContext = this;
		iv_location = (ImageView) findViewById(R.id.iv_location);
		iv_location.setOnClickListener(this);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(12.0f);// ����500����ʾ15.0f
		mBaiduMap.setMapStatus(msu);

	}

	private void initLocation() {
		// ��ͨģʽ
		mLocationMode = LocationMode.NORMAL;

		mLocationClient = new LocationClient(this);
		myLocationListener = new MyLocationListener();
		// ע�ᶨλ
		mLocationClient.registerLocationListener(myLocationListener);

		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);// ÿ������������1000=1second
		mLocationClient.setLocOption(option);

		// ��ʼ��ͼ��
		mIconLocation = BitmapDescriptorFactory
				.fromResource(R.drawable.navi_map_gps_locked);
		// ��ʼ�����򴫸���
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
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);// ������ͨ��ͼ
			break;
		case R.id.id_map_site:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);// �������ǵ�ͼ
			break;
		case R.id.id_map_traffic:
			if (mBaiduMap.isTrafficEnabled()) {
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("ʵʱ��ͨ(off)");
			} else {
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("ʵʱ��ͨ(on)");
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
			// ��Ӹ�����
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
			// ���¾�γ��
			loc = location;
			MyLocationData data = new MyLocationData.Builder()//
					.direction(mCurrentX)// ���򴫸�����Xֵ
					.accuracy(location.getRadius())// ��λ����
					.latitude(location.getLatitude())// ��λ���ݷ���
					.longitude(location.getLongitude())//
					.build();

			mBaiduMap.setMyLocationData(data);
			// ����ͼ��
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
		// ������λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// �������򴫸���
		myOrientationListener.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// ֹͣ��λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// ֹͣ���򴫸���
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

			showToast("λ��:" + loc.getAddrStr() + ",����:" + loc.getLatitude()
					+ ",γ��:" + loc.getLongitude());
			centerToMyLocation();
			// int r = mLocationClient.requestLocation();
			// switch (r) {
			// case 1:
			// showToast("����û������");
			// break;
			// case 4:
			// showToast("û�м�������");
			// break;
			// case 6:
			// showToast("����ʱ�����");
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
	 * ��λ���ҵ�λ��
	 */
	private void centerToMyLocation() {
		LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		// ����ת����λ
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
	 * ��Ӹ�����ӷ�������ȡJson���ݷ���ArrayList����List<Info> infos��infos
	 * 
	 * @param infos
	 */
	private void addOverLays(List<Info> infos) {
		mBaiduMap.clear();
		LatLng latLng = null;
		Marker marker = null;
		OverlayOptions options;

		for (Info info : infos) {
			// ��γ��
			latLng = new LatLng(info.getLatitude(), info.getLongitude());
			// ͼ��
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
