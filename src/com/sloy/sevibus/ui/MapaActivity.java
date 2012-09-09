package com.sloy.sevibus.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.flurry.android.FlurryAgent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.common.collect.Lists;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.Datos;
import com.sloy.sevibus.utils.MyItemizedOverlay;
import com.sloy.sevibus.utils.SherlockMapActivity;

public class MapaActivity extends SherlockMapActivity implements OnNavigationListener {

	private TapControlledMapView mapView;
	private MapController myMapController;
	private MyLocationOverlay mOverlayLocation;
	private MyItemizedOverlay mOverlayCercanas;
	private List<Overlay> mOverlays;
	private List<Entity> mLineas;
	private Entity mParadaUnica;
	private boolean esperandoPunto = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.onStartSession(this, Datos.FLURRY_KEY);
		setContentView(R.layout.activity_mapa);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		mapView = (TapControlledMapView) findViewById(R.id.mapa_mapview);
		myMapController = mapView.getController();
		mOverlays = mapView.getOverlays();

		mapView.setTraffic(true);
		mapView.setBuiltInZoomControls(true);

		// Coloca el centro y zoom
		myMapController.setCenter(new GeoPoint((int) (37.3808828009948 * 1E6), (int) (-5.986958742141724 * 1E6)));
		myMapController.setZoom(14);

		mOverlayLocation = new MyLocationOverlay(this, mapView);// TODO bifurcar
		mOverlayLocation.enableMyLocation();
		mOverlayLocation.enableCompass();
		mOverlays.add(mOverlayLocation);

		mOverlayCercanas = new MyItemizedOverlay(getResources().getDrawable(R.drawable.marker2), mapView, this);
		mOverlayCercanas.setEnabled(false);

		mapView.setOnSingleTapListener(new OnSingleTapListener() {

			@Override
			public boolean onSingleTap(MotionEvent e) {
				if (esperandoPunto) {
					GeoPoint point = mapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());
					mostrarCercanas(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6);
					mOverlayCercanas.setEnabled(true);
					esperandoPunto = false;
					invalidateOptionsMenu();
					return true;
				} else {
					return false;
				}
			}
		});

		DataFramework db = null;
		try {
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			mLineas = db.getEntityList("lineas", null, "nombre");
			// parada unica
			long unicaId = getIntent().getLongExtra("parada", 0);
			mParadaUnica = db.getTopEntity("paradas", "_id=" + unicaId, null);
		} catch (Exception e) {
			Log.e("sevibus", e.toString(), e);
		} finally {
			db.close();
		}

		List<String> nombres = Lists.newArrayList();
		if (mParadaUnica == null) {
			nombres.add("- Selecciona -");
		} else {
			nombres.add("Parada " + mParadaUnica.getString("numero"));
		}
		for (Entity e : mLineas) {
			nombres.add("Línea " + e.getString("nombre"));
		}
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, nombres);

		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		getSupportActionBar().setListNavigationCallbacks(aa, this);

		long linea = getIntent().getLongExtra("linea", 0);
		if (linea != 0) {
			selectLinea(linea);
		}

		mapView.postInvalidate();
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	private void selectLinea(long lineaID) {
		for (int i = 0; i < mLineas.size(); i++) {
			Entity e = mLineas.get(i);
			if (e.getId() == lineaID) {
				// Ésta es!
				getSupportActionBar().setSelectedNavigationItem(i + 1);
			}
		}
	}

	private void loadMap(Entity paradaUnica) {
		MyItemizedOverlay marker = new MyItemizedOverlay(getResources().getDrawable(R.drawable.marker), mapView, this);
		marker.addParada(paradaUnica);
		loadMap(marker);
	}

	private void loadMap(List<Entity> paradas) {
		MyItemizedOverlay markers = new MyItemizedOverlay(getResources().getDrawable(R.drawable.marker), mapView, this);
		markers.addAllParadas(paradas);
		loadMap(markers);
	}

	private void loadMap(MyItemizedOverlay markers) {
		clearMap();
		mOverlays.add(markers);
		mapView.postInvalidate();
	}

	private void clearMap() {
		mOverlays.clear();
		mOverlays.add(mOverlayLocation);
		if (mOverlayCercanas.isEnabled()) {
			mOverlays.add(mOverlayCercanas);
		}
		mapView.postInvalidate();
	}

	private List<Entity> getParadas(long linea) {
		List<Entity> paradas = Lists.newArrayList();
		DataFramework db = null;
		try {
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			// mLineas = db.getEntityList("lineas");
			List<Entity> rel = db.getEntityList("relaciones", "linea_id=" + linea);
			for (Entity e : rel) {
				Entity parada = db.getTopEntity("paradas", "_id=" + e.getInt("parada_id"), null);
				paradas.add(parada);
			}
		} catch (Exception e) {
			Log.e("sevibus", e.toString(), e);
		} finally {
			db.close();
		}
		return paradas;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// Oculta si hay algún globo abierto
		for (Overlay o : mOverlays) {
			if (o instanceof MyItemizedOverlay) {
				// Es uno de los nuestros
				MyItemizedOverlay io = (MyItemizedOverlay) o;
				io.hideBalloon();
			}
		}
		// Hace lo que tenga que hacer
		if (itemPosition == 0) {
			if (mParadaUnica == null) {
				clearMap();
				return true;
			} else {
				loadMap(mParadaUnica);
			}
		} else {
			loadMap(getParadas(mLineas.get(itemPosition - 1).getId()));
		}
		return true;
	}

	@Override
	protected void onPause() {
		mOverlayLocation.disableMyLocation();
		mOverlayLocation.disableCompass();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mOverlayLocation.enableMyLocation();
		mOverlayLocation.enableCompass();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.mapa, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_cercanas:
			startCercanas();
			return true;
		case android.R.id.home:
			startActivity(new Intent(this, HomeActivity.class));
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.menu_cercanas);
		if(esperandoPunto){
			item.setEnabled(false);
		}else{
			item.setEnabled(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private void startCercanas() {
		esperandoPunto = true;
		invalidateOptionsMenu();
		Toast.makeText(this, "Pulsa en un punto del mapa para mostrar las paradas cercanas", Toast.LENGTH_SHORT).show();
	}

	private void mostrarCercanas(double refLatitud, double refLongitud) {
		List<Entity> paradas = Lists.newArrayList();
		DataFramework db = null;
		try {
			db = DataFramework.getInstance();
			db.open(this, getPackageName());

			double margen = 0.005;

			double maxLatitud = refLatitud + margen;
			double minLatitud = refLatitud - margen;
			double maxLongitud = refLongitud + margen;
			double minLongitud = refLongitud - margen;

			/*
			 * Entity eP = new Entity("paradas"); eP.setValue("nombre", "P");
			 * eP.setValue("latitud", refLatitud); eP.setValue("longitud",
			 * refLongitud);
			 * 
			 * Entity eA = new Entity("paradas"); eA.setValue("nombre", "A");
			 * eA.setValue("latitud", maxLatitud); eA.setValue("longitud",
			 * minLongitud);
			 * 
			 * Entity eB = new Entity("paradas"); eB.setValue("nombre", "B");
			 * eB.setValue("latitud", maxLatitud); eB.setValue("longitud",
			 * maxLongitud);
			 * 
			 * Entity eC = new Entity("paradas"); eC.setValue("nombre", "C");
			 * eC.setValue("latitud", minLatitud); eC.setValue("longitud",
			 * maxLongitud);
			 * 
			 * Entity eD = new Entity("paradas"); eD.setValue("nombre", "D");
			 * eD.setValue("latitud", minLatitud); eD.setValue("longitud",
			 * minLongitud);
			 * 
			 * paradas.add(eA); paradas.add(eB); paradas.add(eC);
			 * paradas.add(eD); paradas.add(eP);
			 */
			paradas = db.getEntityList("paradas", "latitud < " + maxLatitud + " and latitud > " + minLatitud + " and longitud < " + maxLongitud
					+ " and longitud > " + minLongitud);

		} catch (Exception e) {
			Log.e("sevibus", e.toString(), e);
		} finally {
			db.close();
		}

		// Carga la lista en el overlay de cercanas, no en el principal
		mOverlayCercanas.clear();
		mOverlayCercanas.addAllParadas(paradas);
		mOverlays.add(0,mOverlayCercanas);
		mapView.postInvalidate();
		
	}

}
