package com.liangxiao.petrolstation;

import com.liangxiao.petrolstation.adapter.PriceListAdapter;
import com.liangxiao.petrolstation.bean.Station;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class StationInfoActivity extends Activity {
	private ImageView iv_back;
	private TextView tv_title_button;
	private TextView tv_name;
	private TextView tv_distance;
	private TextView tv_area;
	private TextView tv_addr;
	private ListView lv_gast_price;
	private ListView lv_price;
	private Context mContext;
	private ScrollView sv;
	private Station s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		mContext = this;
		initView();
		// ��������
		setText();
	}

	private void initView() {
		// ������һ��
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setVisibility(View.VISIBLE);
		iv_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});
		// ���õ���
		tv_title_button = (TextView) findViewById(R.id.tv_title_button);
		tv_title_button.setVisibility(View.VISIBLE);
		tv_title_button.setText("���� >");
		tv_title_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intentRoute = new Intent(mContext, RouteActivity.class);
				intentRoute.putExtra("Petrollat", s.getLat());// ����վ�ľ���λ��
				intentRoute.putExtra("Petrollon", s.getLon());// ����վ��γ��λ��
				intentRoute.putExtra("locationlat",
						getIntent().getDoubleExtra("loclat", 0));// ��ǰλ�õľ���
				intentRoute.putExtra("locationlon",
						getIntent().getDoubleExtra("loclon", 0));// ��ǰλ�õ�γ��
				startActivity(intentRoute);
			}
		});
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_area = (TextView) findViewById(R.id.tv_area);
		tv_addr = (TextView) findViewById(R.id.tv_addr);
		lv_gast_price = (ListView) findViewById(R.id.lv_gast_price);
		lv_price = (ListView) findViewById(R.id.lv_price);
		sv = (ScrollView) findViewById(R.id.sv);

	}

	/**
	 * �������ݲ���
	 */
	private void setText() {
		s = getIntent().getParcelableExtra("s");
		tv_name.setText(s.getName() + "-" + s.getBrandname());
		tv_distance.setText(s.getDistance() + "m");
		tv_area.setText(s.getArea());
		tv_addr.setText(s.getAddress());

		// ����listView����
		PriceListAdapter adapterGastpriceList = new PriceListAdapter(mContext,
				s.getGastpriceList());
		PriceListAdapter adapterPriceList = new PriceListAdapter(mContext,
				s.getPriceList());
		lv_gast_price.setAdapter(adapterGastpriceList);
		lv_price.setAdapter(adapterPriceList);

		sv.smoothScrollTo(0, 0);
	}
}
