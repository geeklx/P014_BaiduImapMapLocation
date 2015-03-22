package com.liangxiao.petrolstation.adapter;

import java.util.ArrayList;

import org.w3c.dom.ls.LSInput;

import com.liangxiao.petrolstation.R;
import com.liangxiao.petrolstation.bean.Petrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListGridViewAdapter extends BaseAdapter {
	private ArrayList<Petrol> list;
	private LayoutInflater mInflater;
	private Context mcContext;

	public ListGridViewAdapter(Context context, ArrayList<Petrol> list) {
		this.mcContext = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);
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
			rowView = mInflater.inflate(R.layout.item_price_gridview, null);
		} else {
			rowView = convertView;
		}
		TextView tv = (TextView) rowView.findViewById(R.id.tv);
		Petrol p = getItem(position);
		String a = p.getType().replace("E", "");
		if(!a.contains("#")){
			a = a +"#";
		}
		tv.setText(a + " " + p.getPrice());

		return rowView;
	}
}
