package com.sloy.sevibus.ui;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.ParadasAdapter;

public class CercanasActivity extends FragmentActivity {

	private String mProvider;
	private LocationManager mLocationManager;
	
	private ListView mList;
	private ParadasAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);
		
		mList = (ListView)findViewById(android.R.id.list);
		
		// Obtiene el mejor proveedor de localización
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria cri = new Criteria();
		cri.setAccuracy(Criteria.ACCURACY_COARSE);
		mProvider = mLocationManager.getBestProvider(cri, true);

		// Carga (primera vez) las paradas basado en la última posición conocida
		Location loc = mLocationManager.getLastKnownLocation(mProvider);
		
		
	}

}
