package com.sloy.sevibus.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class BusesOverlay extends ItemizedOverlay<OverlayItem> {

	public static final int ZONE = 30;
	public static final String LAT_ZONE = "N";

	private List<OverlayItem> mOverlays;
	private Context mContext;

	public BusesOverlay(Drawable defaultMarker, MapView mapView, Context context) {
		super(boundCenter(defaultMarker));
		mOverlays = new ArrayList<OverlayItem>();
		//mContext = context;
	}

	public void clear() {
		mOverlays.clear();
		populate();
	}


	public void addBusList(List<BusLocation> list) {
		for (BusLocation b : list) {
			GeoPoint rawPoint = GeoPointConversion.utmToGeoPoint(b.xcoord, b.ycoord, "30N");
			GeoPoint calibrado = new GeoPoint(rawPoint.getLatitudeE6() - 200 * 10, rawPoint.getLongitudeE6() - 130 * 10);
			addOverlay(new OverlayItem(calibrado, "BusLocation " + size(), "blablabla")); //TODO los buses llevan más información, pero aún no la parseo
		}
		populate();
	}


	private  void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, false);
	}

}
