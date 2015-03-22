package com.liangxiao.petrolstation.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

public class Petrol implements Parcelable {
	private String type;
	private String price;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(type);
		arg0.writeString(price);
	}

	public static final Parcelable.Creator<Petrol> CREATOR = new Creator<Petrol>() {

		@Override
		public Petrol[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Petrol createFromParcel(Parcel arg0) {
			Petrol p = new Petrol();
			p.type = arg0.readString();
			p.price = arg0.readString();
			return p;
		}
	};
}
