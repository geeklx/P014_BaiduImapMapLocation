package com.liangxiao.petrolstation;

import java.util.ArrayList;

import com.liangxiao.petrolstation.adapter.StationListAdapter;
import com.liangxiao.petrolstation.bean.Station;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class StationListActivity extends Activity {
	private Context mContext;
	private ListView lv_Station;
	private ArrayList<Station> list;
	private ImageView iv_back;
	private StationListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		mContext = this;
		initView();
	}

	private void initView() {
		// 返回加油站
		findViewById(R.id.iv_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						finish();
					}
				});
		lv_Station = (ListView) findViewById(R.id.lv_station);
		list = getIntent().getParcelableArrayListExtra("list");

		adapter = new StationListAdapter(mContext, list);
		lv_Station.setAdapter(adapter);

		lv_Station.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int positon, long id) {
				Intent intent = new Intent(mContext, StationInfoActivity.class);
				intent.putExtra("s", list.get(positon));
				intent.putExtra("loclat",
						getIntent().getDoubleExtra("loclat", 0));// 当前位置的经度
				intent.putExtra("loclon",
						getIntent().getDoubleExtra("loclon", 0));// 当前位置的纬度
				startActivity(intent);

			}
		});

	}
}
