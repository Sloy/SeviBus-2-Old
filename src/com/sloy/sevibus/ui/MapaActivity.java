package com.sloy.sevibus.ui;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
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
import com.sloy.sevibus.utils.BusLocation;
import com.sloy.sevibus.utils.BusesOverlay;
import com.sloy.sevibus.utils.Datos;
import com.sloy.sevibus.utils.ParadasOverlay;
import com.sloy.sevibus.utils.ServerErrorException;
import com.sloy.sevibus.utils.Utils;

public class MapaActivity extends SherlockMapActivity implements OnNavigationListener {

	private TapControlledMapView mapView;
	private MapController myMapController;
	private MyLocationOverlay mOverlayLocation;
	private ParadasOverlay mOverlayCercanas;
	private BusesOverlay busesOvarlay;
	private List<Overlay> mOverlays;
	private List<Entity> mLineas;
	private String mLineaSeleccionada;
	private Entity mParadaUnica;
	private boolean esperandoPunto = false;
	private Toast myToast;
	private boolean siguiendoAutobuses = false;
	private ActualizaBusesTimerTask mTimerTask;
	private Timer mTimer;

	/**
	 * Se encarga de ejecutar el AsyncTask
	 */
	private Runnable actualizaBuses = new Runnable() {
		@Override
		public void run() {
			if (siguiendoAutobuses) {
				new BusTask().execute();
			}
		}
	};

	/**
	 * Ejecuta el Runnable actualizaBuses sobre el hilo de la interfaz, para que
	 * el AsyncTask pueda trabajar con la UI.
	 */
	private class ActualizaBusesTimerTask extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(actualizaBuses);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		FlurryAgent.onStartSession(this, Datos.FLURRY_KEY);
		setContentView(R.layout.activity_mapa);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		myToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mTimer = new Timer("ActualizaBuses");

		mapView = (TapControlledMapView) findViewById(R.id.mapa_mapview);
		myMapController = mapView.getController();
		mOverlays = mapView.getOverlays();

		mapView.setTraffic(true);
		mapView.setBuiltInZoomControls(true);

		// Coloca el centro y zoom
		myMapController.setCenter(new GeoPoint((int) (37.3808828009948 * 1E6), (int) (-5.986958742141724 * 1E6)));
		myMapController.setZoom(14);

		mOverlayLocation = new MyLocationOverlay(this, mapView);
		mOverlayLocation.enableMyLocation();
		mOverlayLocation.enableCompass();
		mOverlays.add(mOverlayLocation);

		mOverlayCercanas = new ParadasOverlay(getResources().getDrawable(R.drawable.marker2), mapView, this);
		mOverlayCercanas.setEnabled(false);

		busesOvarlay = new BusesOverlay(getResources().getDrawable(R.drawable.marker_bus), mapView, this);

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
			nombres.add("- Selecciona línea -");
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
		ParadasOverlay marker = new ParadasOverlay(getResources().getDrawable(R.drawable.marker), mapView, this);
		marker.addParada(paradaUnica);
		loadMap(marker);
	}

	private void loadMap(List<Entity> paradas) {
		ParadasOverlay markers = new ParadasOverlay(getResources().getDrawable(R.drawable.marker), mapView, this);
		markers.addAllParadas(paradas);
		loadMap(markers);
	}

	private void loadMap(ParadasOverlay markers) {
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
			if (o instanceof ParadasOverlay) {
				// Es uno de los nuestros
				ParadasOverlay io = (ParadasOverlay) o;
				io.hideBalloon();
			}
		}
		// Hace lo que tenga que hacer
		if (itemPosition == 0) {
			mLineaSeleccionada = null;
			if (mParadaUnica == null) {
				clearMap();
			} else {
				loadMap(mParadaUnica);
			}
		} else {
			Entity linea = mLineas.get(itemPosition - 1);
			mLineaSeleccionada = linea.getString("nombre");
			if(siguiendoAutobuses){
				// Reinicia el timer
				detieneSeguimientoBuses();
				comienzaSeguimientoBuses();
			}
			loadMap(getParadas(linea.getId()));
		}
		invalidateOptionsMenu();
		return true;
	}

	@Override
	protected void onPause() {
		mOverlayLocation.disableMyLocation();
		mOverlayLocation.disableCompass();
		if(siguiendoAutobuses){
			detieneSeguimientoBuses();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		mOverlayLocation.enableMyLocation();
		mOverlayLocation.enableCompass();
		if(siguiendoAutobuses){
			comienzaSeguimientoBuses();
		}
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
		case R.id.menu_mapa_callejero:
			mapView.setSatellite(false);
			return true;
		case R.id.menu_mapa_satelite:
			mapView.setSatellite(true);
			return true;
		case R.id.menu_posicion:
			GeoPoint aquiestausted = mOverlayLocation.getMyLocation();
			myMapController.animateTo(aquiestausted);
			myMapController.setZoom(19);
			mostrarCercanas(aquiestausted.getLatitudeE6() / 1E6, aquiestausted.getLongitudeE6() / 1E6);
			return true;
		case R.id.menu_buses:
			if (!siguiendoAutobuses) {
				// Empieza el seguimiento de autobuses
				if (mLineaSeleccionada != null) {
					siguiendoAutobuses = true;
					comienzaSeguimientoBuses();
				} else {
					myToast.setText("Debes seleccionar primero una línea para ver sus autobuses");
					myToast.show();
				}
			} else {
				// El seguimiento ya está activo, lo para
				siguiendoAutobuses = false;
				detieneSeguimientoBuses();
				busesOvarlay.clear();
				mOverlays.remove(busesOvarlay);
				mapView.postInvalidate();
			}
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
		MenuItem cercanas = menu.findItem(R.id.menu_cercanas);
		if (esperandoPunto) {
			cercanas.setEnabled(false);
		} else {
			cercanas.setEnabled(true);
		}
		MenuItem buses = menu.findItem(R.id.menu_buses);
		if (siguiendoAutobuses) {
			buses.setIcon(R.drawable.ic_action_bus_on);
		} else {
			buses.setIcon(R.drawable.ic_action_bus);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private void comienzaSeguimientoBuses() {
		invalidateOptionsMenu();
		mTimerTask = new ActualizaBusesTimerTask();
		mTimer.schedule(mTimerTask, 0, 10000);
	}

	private void detieneSeguimientoBuses() {
		invalidateOptionsMenu();
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		mTimerTask = null;
		mTimer.purge();
	}

	private void startCercanas() {
		esperandoPunto = true;
		invalidateOptionsMenu();
		myToast.setText("Pulsa en un punto del mapa para mostrar las paradas cercanas");
		myToast.show();
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

			paradas = db.getEntityList("paradas", "latitud < " + maxLatitud + " and latitud > " + minLatitud + " and longitud < " + maxLongitud
					+ " and longitud > " + minLongitud);

		} catch (Exception e) {
			Log.e("sevibus", e.toString(), e);
		} finally {
			db.close();
		}

		// Si muestra el Overlay vacío la lía parda
		if (!paradas.isEmpty()) {
			// Carga la lista en el overlay de cercanas, no en el principal
			mOverlayCercanas.clear();
			mOverlayCercanas.addAllParadas(paradas);
			mOverlays.add(mOverlayCercanas);
			mapView.postInvalidate();
		} else {
			mOverlayCercanas.clear();
			mOverlays.remove(mOverlayCercanas);
			mapView.postInvalidate();
			myToast.setText("No se encontraron paradas cercanas a esa posición");
			myToast.show();
		}

	}

	private class BusTask extends AsyncTask<Void, Void, List<BusLocation>> {

		@Override
		protected List<BusLocation> doInBackground(Void... params) {
			List<BusLocation> buses = null;
			try {
				buses = Utils.getBuses(mLineaSeleccionada);
			} catch (ServerErrorException e) {
				Log.e("SeviBus", "Error de servidor");
			}
			return buses;
		}

		@Override
		protected void onPostExecute(List<BusLocation> result) {
			Log.d("SeviBus","GO!");
			setSupportProgressBarIndeterminateVisibility(false);
			if (result != null) {
				busesOvarlay.clear();
				busesOvarlay.addBusList(result);
				mOverlays.add(busesOvarlay);
				mapView.postInvalidate();
			} else {
				myToast.setText("Problema cargar la posición de los autobuses");
				myToast.show();
				mOverlays.remove(busesOvarlay);
			}
		}

		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
		}
	}

}