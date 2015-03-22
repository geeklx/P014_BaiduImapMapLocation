package com.liangxiao.petrolstation.bean;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Station implements Parcelable {
	private String name;
	private String address;
	private String area;
	private String brandname;
	private double lat;
	private double lon;
	private String distance;
	private ArrayList<Petrol> gastpriceList;
	private ArrayList<Petrol> priceList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getBrandname() {
		return brandname;
	}

	public void setBrandname(String brandname) {
		this.brandname = brandname;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public ArrayList<Petrol> getGastpriceList() {
		return gastpriceList;
	}

	public void setGastpriceList(ArrayList<Petrol> gastpriceList) {
		this.gastpriceList = gastpriceList;
	}

	public ArrayList<Petrol> getPriceList() {
		return priceList;
	}

	public void setPriceList(ArrayList<Petrol> priceList) {
		this.priceList = priceList;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(name);
		arg0.writeString(address);
		arg0.writeString(area);
		arg0.writeString(brandname);
		arg0.writeDouble(lat);
		arg0.writeDouble(lon);
		arg0.writeString(distance);
		arg0.writeList(gastpriceList);
		arg0.writeList(priceList);

	}

	public static final Parcelable.Creator<Station> CREATOR = new Creator<Station>() {

		@Override
		public Station[] newArray(int arg0) {

			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Station createFromParcel(Parcel arg0) {
			Station s = new Station();
			s.name = arg0.readString();
			s.address = arg0.readString();
			s.area = arg0.readString();
			s.brandname = arg0.readString();
			s.lat = arg0.readDouble();
			s.lon = arg0.readDouble();
			s.distance = arg0.readString();
			s.gastpriceList = arg0.readArrayList(Petrol.class.getClassLoader());
			s.priceList = arg0.readArrayList(Petrol.class.getClassLoader());
			return s;
		}
	};
}
