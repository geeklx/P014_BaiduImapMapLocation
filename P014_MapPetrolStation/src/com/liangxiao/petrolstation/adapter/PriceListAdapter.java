package com.liangxiao.petrolstation.adapter;

import java.util.ArrayList;

import com.liangxiao.petrolstation.R;
import com.liangxiao.petrolstation.bean.Petrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PriceListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<Petrol> list;

	public PriceListAdapter(Context mContext, ArrayList<Petrol> list) {
		this.mContext = mContext;
		this.list = list;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Petrol getItem(int position) {
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
			rowView = mInflater.inflate(R.layout.item_info_list, null);
		} else {
			rowView = convertView;
		}
		TextView tv_name = (TextView) rowView.findViewById(R.id.tv_name);
		TextView tv_price = (TextView) rowView.findViewById(R.id.tv_price);
		Petrol p = getItem(position);
		String a = p.getType().replace("E", "");
		if (!a.contains("#")) {
			a = a + "#";
		}
		tv_name.setText(a);
		tv_price.setText(p.getPrice());

		return rowView;
	}
}
