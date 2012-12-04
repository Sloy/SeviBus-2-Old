package com.sloy.sevibus.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Lists;
import com.sloy.sevibus.R;

public class NewMapActivity extends SherlockActivity implements OnNavigationListener {

    private GoogleMap mMap;
    private List<Entity> mLineas;
    private Entity mParadaUnica;
    private MarkerLayer singleLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_new);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setUpMapIfNeeded();

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

        aa.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setListNavigationCallbacks(aa, this);

        long linea = getIntent().getLongExtra("linea", 0);
        if (linea != 0) {
            selectLinea(linea);
        }

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

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map.
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMyLocationEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(37.3808828009948, -5.986958742141724), 13)));
            } else {
                // Tenemos un poblema gordo gordo...
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (itemPosition > 0) {
            if (singleLayer == null) {
                singleLayer = new MarkerLayer(mMap);
            }
            singleLayer.removeAll();

            Entity linea = mLineas.get(itemPosition - 1);

            List<Entity> paradas = Lists.newArrayList();
            DataFramework db = null;
            try {
                db = DataFramework.getInstance();
                db.open(this, getPackageName());
                // mLineas = db.getEntityList("lineas");
                List<Entity> rel = db.getEntityList("relaciones", "linea_id=" + linea.getId());
                for (Entity e : rel) {
                    Entity parada = db.getTopEntity("paradas", "_id=" + e.getInt("parada_id"), null);
                    paradas.add(parada);
                }
            } catch (Exception e) {
                Log.e("sevibus", e.toString(), e);
            } finally {
                db.close();
            }

            for (Entity p : paradas) {
                singleLayer.addMarker(new LatLng(p.getDouble("latitud"), p.getDouble("longitud")), "Parada nº " + p.getString("numero"));
            }
            return true;
        } else {
            return false;
        }
    }

    public class MarkerLayer {

        private List<Marker> markers;
        private GoogleMap mMap;

        public MarkerLayer(GoogleMap map) {
            this.mMap = map;
            markers = new ArrayList<Marker>();
        }

        public void addMarker(LatLng posicion, String title) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(posicion).title(title));
            markers.add(marker);
        }

        public void removeAll() {
            for (Marker m : markers) {
                m.remove();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMap!=null){
            Toast.makeText(this, String.valueOf(mMap.getCameraPosition().zoom), Toast.LENGTH_SHORT).show();
        }
    }
}
