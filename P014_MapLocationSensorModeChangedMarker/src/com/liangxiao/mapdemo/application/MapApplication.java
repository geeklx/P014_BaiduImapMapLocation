package com.liangxiao.mapdemo.application;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

public class MapApplication extends Application {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// �ٶȵ�ͼ��ʼ��
		// SDKInitializer.initialize(getApplicationContext());
		SDKInitializer.initialize(getApplicationContext());
	}
}
