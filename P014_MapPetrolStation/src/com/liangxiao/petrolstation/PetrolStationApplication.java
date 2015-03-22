package com.liangxiao.petrolstation;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class PetrolStationApplication extends Application {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// °Ù¶ÈµØÍ¼
		SDKInitializer.initialize(getApplicationContext());
		com.thinkland.sdk.android.SDKInitializer
				.initialize(getApplicationContext());

	}
}
