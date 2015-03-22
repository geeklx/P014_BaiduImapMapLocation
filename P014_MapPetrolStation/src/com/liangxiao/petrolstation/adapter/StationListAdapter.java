package com.liangxiao.petrolstation.adapter;

import java.util.ArrayList;

import com.liangxiao.petrolstation.R;
import com.liangxiao.petrolstation.bean.Station;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class StationListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Station> list;
	private LayoutInflater mInflater;

	public StationListAdapter(Context context, ArrayList<Station> list) {
		this.mContext = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Station getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = null;
		if (convertView == null) {
			rowView = mInflater.inflate(R.layout.item_station_list, null);
		} else {
			rowView = convertView;
		}
		TextView tv_id = (TextView) rowView.findViewById(R.id.tv_id);
		TextView tv_name = (TextView) rowView.findViewById(R.id.tv_name);
		TextView tv_distance = (TextView) rowView
				.findViewById(R.id.tv_distance);
		TextView tv_addr = (TextView) rowView.findViewById(R.id.tv_addr);
		GridView gv_price = (GridView) rowView.findViewById(R.id.gv_price);
		Station s = getItem(position);
		tv_id.setText((position + 1) + ".");
		tv_name.setText(s.getName());
		tv_distance.setText(s.getDistance()+"m");
		tv_addr.setText(s.getAddress());

		ListGridViewAdapter adapter = new ListGridViewAdapter(mContext,
				s.getGastpriceList());
		gv_price.setAdapter(adapter);
		return rowView;
	}

}
