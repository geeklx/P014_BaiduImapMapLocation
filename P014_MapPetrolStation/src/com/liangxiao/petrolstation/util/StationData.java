package com.liangxiao.petrolstation.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.liangxiao.petrolstation.bean.Petrol;
import com.liangxiao.petrolstation.bean.Station;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

public class StationData {
	public Handler mHandler;

	public StationData(Handler handler) {
		this.mHandler = handler;
	}

	public void getStationData(double lat, double lon, int distance) {
		Parameters params = new Parameters();
		params.add("lat", lat);// γ��
		params.add("lon", lon);// γ��
		params.add("r", distance);// γ��
		JuheData.executeWithAPI(7, "http://apis.juhe.cn/oil/local",
				JuheData.GET, params, new DataCallBack() {

					@Override
					public void resultLoaded(int err, String reason,
							String result) {
						if (err == 0) {
							ArrayList<Station> list = parser(result);
							if (list != null && list.size() > 0) {
								Message msg = Message.obtain(mHandler, 0x01,
										list);
								msg.sendToTarget();
							} else {
								Message msg = Message.obtain(mHandler, 0x02,
										reason);
								msg.sendToTarget();
							}
						}

					}
				});
	}

	private ArrayList<Station> parser(String str) {
		ArrayList<Station> list = null;
		try {
			JSONObject json = new JSONObject(str);
			int error_code = json.getInt("error_code");
			int resultcode = json.getInt("resultcode");
			if (resultcode == 200 && error_code == 0) {
				list = new ArrayList<Station>();
				JSONArray data = json.getJSONObject("result").getJSONArray(
						"data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject datas = data.getJSONObject(i);
					Station s = new Station();
					s.setName(datas.getString("name"));// ����
					s.setAddress(datas.getString("address"));// ��ַ
					s.setArea(datas.getString("areaname"));// ������
					s.setBrandname(datas.getString("brandname"));// ��֧����
					s.setLon(datas.getDouble("lon"));// ����
					s.setLat(datas.getDouble("lat"));// γ��
					s.setDistance(datas.getString("distance"));// ����

					JSONObject priceJson = datas.getJSONObject("price");
					ArrayList<Petrol> pricelist = new ArrayList<Petrol>();
					Iterator<String> priceI = priceJson.keys();
					while (priceI.hasNext()) {
						Petrol p = new Petrol();
						String key = priceI.next();
						String value = priceJson.getString(key);

						p.setType(key.replace("E", "") + "#");
						p.setPrice(value + "RMB/��");

						pricelist.add(p);
					}
					s.setPriceList(pricelist);

					JSONObject gastpriceJson = datas.getJSONObject("price");
					ArrayList<Petrol> gastpricelist = new ArrayList<Petrol>();
					Iterator<String> gastpriceI = priceJson.keys();
					while (gastpriceI.hasNext()) {
						Petrol p = new Petrol();
						String key = gastpriceI.next();
						String value = gastpriceJson.getString(key);

						p.setType(key);
						p.setPrice(value + "RMB/��");

						gastpricelist.add(p);
					}
					s.setGastpriceList(gastpricelist);

					list.add(s);
				}

			}
			else{
				Message msg = Message.obtain(mHandler, 0x02,
						error_code);
				msg.sendToTarget();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
