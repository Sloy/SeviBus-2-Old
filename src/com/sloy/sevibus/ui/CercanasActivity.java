package com.sloy.sevibus.ui;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.flurry.android.FlurryAgent;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.Datos;
import com.sloy.sevibus.utils.ParadasAdapter;

public class CercanasActivity extends SherlockActivity  {

	private String mProvider;
	private LocationManager mLocationManager;
	
	private ListView mList;
	private ParadasAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.onStartSession(this, Datos.FLURRY_KEY);
		setContentView(R.layout.list_activity);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mList = (ListView)findViewById(android.R.id.list);
		
		// Obtiene el mejor proveedor de localización
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria cri = new Criteria();
		cri.setAccuracy(Criteria.ACCURACY_COARSE);
		mProvider = mLocationManager.getBestProvider(cri, true);

		// Carga (primera vez) las paradas basado en la última posición conocida
		Location loc = mLocationManager.getLastKnownLocation(mProvider);
		
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

}
