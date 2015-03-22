package com.liangxiao.mapdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

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
	private String latitude;
	private String longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initLocation();// ��ʼ����λ

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
			loc = location;
			MyLocationData data = new MyLocationData.Builder()//
					.accuracy(location.getRadius())// ��λ����
					.latitude(location.getLatitude())// ��λ���ݷ���
					.longitude(location.getLongitude())//
					.build();

			mBaiduMap.setMyLocationData(data);
			// MyLocationConfiguration config = new
			// MyLocationConfiguration(LocationMode.NORMAL, arg1, arg2);

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
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
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
}
