package com.liangxiao.mapdemo.application;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class MapApplication extends Application {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 百度地图初始化
		SDKInitializer.initialize(getApplicationContext());
	}
}
