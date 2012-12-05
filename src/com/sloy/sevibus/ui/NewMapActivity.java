package com.sloy.sevibus.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Lists;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.IntentParada;

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
                mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                    
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        startActivity(new IntentParada(NewMapActivity.this, singleLayer.getItem(marker).getId()));
                    }
                });
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
                singleLayer.addMarker(p);
            }
            return true;
        } else {
            return false;
        }
    }

    public class MarkerLayer {

        private Map<Marker, Entity> markers;
        private GoogleMap mMap;

        public MarkerLayer(GoogleMap map) {
            this.mMap = map;
            markers = new HashMap<Marker, Entity>();
        }

        public void addMarker(Entity p) {
            LatLng pos = new LatLng(p.getDouble("latitud"), p.getDouble("longitud"));
            
            Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title("Parada nº "+p.getString("numero")).snippet(p.getString("nombre")).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            markers.put(marker, p);
        }
        
        public Entity getItem(Marker marker){
            if(markers.containsKey(marker)){
                return markers.get(marker);
            }else{
                return null;
            }
        }

        public void removeAll() {
            for (Marker m : markers.keySet()) {
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
