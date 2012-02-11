package com.sloy.sevibus.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.android.dataframework.Entity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.common.collect.Lists;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.sloy.sevibus.R;

import java.util.List;

public class MyItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private List<Entity> mParadas;
	private List<OverlayItem> mOverlays;
	private boolean enabled = true;
	private Context mContext;

	public MyItemizedOverlay(Drawable defaultMarker, MapView mapView, Context context) {
		super(boundCenter(defaultMarker), mapView);
		mParadas = Lists.newArrayList();
		mOverlays = Lists.newArrayList();
		mContext = context;
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	public void addParada(Entity parada) {
		int lat = (int)(parada.getDouble("latitud") * 1E6);
		int lon = (int)(parada.getDouble("longitud") * 1E6);
		GeoPoint point = new GeoPoint(lat, lon);
		String numero = parada.getString("numero");
		String nombre = parada.getString("nombre");
		OverlayItem overlayitem = new OverlayItem(point, "Parada nº " + numero, nombre);
		mParadas.add(parada);
		addOverlay(overlayitem);
	}

	public void addAllParadas(List<Entity> paradas) {
		for(Entity e : paradas){
			addParada(e);
		}
	}

	public void clear() {
		this.mOverlays.clear();
		this.mParadas.clear();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void toogleEnabled() {
		enabled = !enabled;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	protected OverlayItem createItem(int index) {
		return mOverlays.get(index);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		mContext.startActivity(new IntentParada(mContext, mParadas.get(index).getId()));
		return true;
	}

	@Override
	public void hideBalloon() {
		super.hideBalloon();
	}
}
