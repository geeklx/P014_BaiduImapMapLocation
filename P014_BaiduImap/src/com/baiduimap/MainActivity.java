package com.baiduimap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button button;
	private Button button2;

	// ��Ӱٶȵ�ͼ��صĿؼ�
	// private MapView mapView;
	// private BMapManager bMapManager;//���ص�ͼ������
	// private String keystring = "Bqrb3neoBZSxyUwnD6e5LnBA";//�ٶȵ�ͼ��key
	// �ڰٶȵ�ͼ�����һЩ�Ŵ���С�Ŀؼ�
	// private MapController mapController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// mapView = (MapView)this.findViewById(R.id.bmapView);
		// bMapManager = new BMapManager(MainActivity.this);
		// ����Ҫ���ص�key
		// bMapManager.init(arg0)��
		button = (Button) this.findViewById(R.id.button);
		
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						MyMapActivity.class);
				startActivity(intent);
			}
		});
		button2 = (Button) this.findViewById(R.id.button2);
		
		button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						LocationOverlayDemo.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
