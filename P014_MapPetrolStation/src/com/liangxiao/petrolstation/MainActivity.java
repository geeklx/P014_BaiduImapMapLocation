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
	private ImageView iv_loc;// ��߶�λ
	private ImageView iv_list;// �ұ���Χ�ĸ�����Ϣ
	private LinearLayout ll_summary;// �鿴����
	private TextView tv_name;// ��ǰ����վ����
	private TextView tv_distance;// ��ǰ����վ����
	private TextView tv_price_a;// ��ǰ����վ�۸�a
	private TextView tv_price_b;// ��ǰ����վ�۸�b

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
	 * ��ӵ�ͼ������
	 * 
	 * @param list
	 */
	private void setMarker(ArrayList<Station> list) {
		View view = LayoutInflater.from(mContext)
				.inflate(R.layout.marker, null);
		final TextView tv = (TextView) view.findViewById(R.id.tv_marker);
		for (int i = 0; i < list.size(); i++) {
			Station s = list.get(i);// ��ȡһ��Station����
			tv.setText(i + 1 + "");// 123456789
			if (i == 0) {
				tv.setBackgroundResource(R.drawable.icon_focus_mark);// ��ɫ
			} else {
				tv.setBackgroundResource(R.drawable.icon_mark);// ��ɫ
			}
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
			LatLng latLng = new LatLng(s.getLat(), s.getLon());// γ�ȣ�����
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
					tv.setBackgroundResource(R.drawable.icon_mark);// ��ɫ
					BitmapDescriptor bitmap = BitmapDescriptorFactory
							.fromView(tv);
					lastMarker.setIcon(bitmap);
				}
				lastMarker = marker;
				String position = marker.getTitle();
				tv.setText(position);// 123456789
				tv.setBackgroundResource(R.drawable.icon_focus_mark);// ��ɫ
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
		tv_title_button.setOnClickListener(this);// ����km�¼�����

		iv_loc = (ImageView) findViewById(R.id.iv_loc);
		iv_loc.setOnClickListener(this);// ��߶�λ�����¼�����

		iv_list = (ImageView) findViewById(R.id.iv_list);
		iv_list.setOnClickListener(this);// ��Χ�б��¼�����

		ll_summary = (LinearLayout) findViewById(R.id.ll_summary);
		ll_summary.setOnClickListener(this);// �鿴�����¼�����

		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_price_a = (TextView) findViewById(R.id.tv_price_a);
		tv_price_b = (TextView) findViewById(R.id.tv_price_b);

		// ��ʼ������
		mMapView = null;
		mBaiduMap = null;
		mLocationClient = null;
		mListener = new MyLocationListener();
		mStation = null;
		mDistance = 3000;
		lastMarker = null;

		// ��ͼͼ�㲿��
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.showScaleControl(false);
		mMapView.showZoomControls(false);
		mBaiduMap = mMapView.getMap();// ��ֵ���ٶ�api

		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
		mBaiduMap.setMyLocationEnabled(true);

		mLocationClient = new LocationClient(mContext);
		mLocationClient.registerLocationListener(mListener);// ע�ᶨλ��Ϣ

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// �߾���ģʽ;Battery_Saving
															// �;���ģʽ
		option.setCoorType("bd09ll");// ���ع���־�γ������ϵ��gcj02 ���ذٶ�ī��������ϵ ��bd09
										// ���ذٶȾ�γ������ϵ ��bd09ll
		option.setScanSpan(0);// ����ɨ��������λ���룬��<1000(1s)ʱ����ʱ��λ��Ч
		option.setIsNeedAddress(true);// �����Ƿ���Ҫ��ַ��Ϣ��Ĭ��Ϊ�޵�ַ
		option.setNeedDeviceDirect(true);// �����綨λʱ���Ƿ���Ҫ�豸����
		mLocationClient.setLocOption(option);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// ��Χ�б�ҳ��
		case R.id.iv_list:
			Intent listIntent = new Intent(mContext, StationListActivity.class);
			listIntent.putParcelableArrayListExtra("list", mList);
			listIntent.putExtra("loclat", loc.getLatitude());// ��ǰλ�õľ���
			listIntent.putExtra("loclon", loc.getLongitude());// ��ǰλ�õ�γ��
			startActivity(listIntent);

			break;
		case R.id.iv_loc:
			int r = mLocationClient.requestLocation();
			switch (r) {
			case 1:
				showToast("����û������");
				break;
			case 2:
				showToast("û�м�������");
				break;
			case 6:
				showToast("����ʱ�����");
				break;
			default:
				break;
			}
			break;
		case R.id.tv_title_button:
			showSelectDialog();
			break;
		// ��ǰλ������ҳ��
		case R.id.ll_summary:
			Intent infoIntent = new Intent(mContext, StationInfoActivity.class);
			infoIntent.putExtra("s", mStation);
			infoIntent.putExtra("loclat", loc.getLatitude());// ��ǰλ�õľ���
			infoIntent.putExtra("loclon", loc.getLongitude());// ��ǰλ�õ�γ��
			startActivity(infoIntent);
			break;
		default:
			break;
		}
	}

	/**
	 * ������ʾ�򲿷�
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
	 * ѡ��3km����dialog����ʾ
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
	 * ѡ��3kmdialog�ĵ���¼�����
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
		// ���м�Ĳ��еĶ�����ֵ
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
	 * ��λ�����ť�¼�����
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
					// ��λ����
					.direction(location.getDirection())
					// ��λ���ݷ���
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			// Ѱ����Χ3km
			searchStation(location.getLatitude(), location.getLongitude(),
					mDistance);
		}

	}

	/**
	 * Ѱ����Χ3km
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
