package com.liangxiao.mapdemo.orientationlistener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyOrientationListener implements SensorEventListener {

	private SensorManager mSensorManager;
	private Context mContext;
	private Sensor mSensor;

	private float lastX;

	public MyOrientationListener(Context context) {
		this.mContext = context;
	}

	public void start() {
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager != null) {
			// ��ȡ���򴫸���
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		}
		if (mSensor != null) {
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_UI);

		}

	}

	public void stop() {
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// ���ȵĸı�

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			float x = event.values[SensorManager.DATA_X];
			// ����һ�ȵĻ���֪ͨ���������
			if (Math.abs(x - lastX) > 1.0) {
				if (mOrientationListener != null) {
					mOrientationListener.onOrientationChanged(x);// �ص�
				}
			}

			lastX = x;
		}
	}

	// ����ӿڲ���
	private onOrientationListener mOrientationListener;

	public void setmOrientationListener(
			onOrientationListener mOrientationListener) {
		this.mOrientationListener = mOrientationListener;
	}

	public interface onOrientationListener {
		void onOrientationChanged(float x);
	}

}
