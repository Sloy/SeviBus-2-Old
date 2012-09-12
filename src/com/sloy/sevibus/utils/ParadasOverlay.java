package com.sloy.sevibus.utils;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.dataframework.Entity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.common.collect.Lists;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;
import com.readystatesoftware.mapviewballoons.R;

public class ParadasOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private List<Entity> mParadas;
	private List<OverlayItem> mOverlays;
	private boolean enabled = true;
	private Context mContext;

	public ParadasOverlay(Drawable defaultMarker, MapView mapView, Context context) {
		super(boundCenter(defaultMarker), mapView);

		mParadas = Lists.newArrayList();
		mOverlays = Lists.newArrayList();
		mContext = context;

		this.setShowClose(false);
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	public void addParada(Entity parada) {
		int lat = (int) (parada.getDouble("latitud") * 1E6);
		int lon = (int) (parada.getDouble("longitud") * 1E6);
		GeoPoint point = new GeoPoint(lat, lon);
		String numero = parada.getString("numero");
		String nombre = parada.getString("nombre");
		OverlayItem overlayitem = new OverlayItem(point, "Parada nº " + numero, nombre);
		mParadas.add(parada);
		addOverlay(overlayitem);
	}

	public void addAllParadas(List<Entity> paradas) {
		for (Entity e : paradas) {
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

	@Override
	protected BalloonOverlayView<OverlayItem> createBalloonOverlayView() {
		return new CustomBalloonOverlayView(getMapView().getContext(), getBalloonBottomOffset());
	}

	public class CustomBalloonOverlayView extends BalloonOverlayView<OverlayItem> {

		private TextView title;
		private TextView snippet;

		public CustomBalloonOverlayView(Context context, int balloonBottomOffset) {
			super(context, balloonBottomOffset);
		}

		@Override
		protected void setupView(Context context, final ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.balloon_overlay, parent);
			title = (TextView) v.findViewById(R.id.balloon_item_title);
			snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);

			title.setTextColor(0xFF990000);
			title.setTypeface(null, Typeface.BOLD);

		}

		@Override
		protected void setBalloonData(OverlayItem item, ViewGroup parent) {
			if (item.getTitle() != null) {
				title.setVisibility(VISIBLE);
				title.setText(item.getTitle());
			} else {
				title.setText("");
				title.setVisibility(GONE);
			}
			if (item.getSnippet() != null) {
				snippet.setVisibility(VISIBLE);
				snippet.setText(item.getSnippet());
			} else {
				snippet.setText("");
				snippet.setVisibility(GONE);
			}
		}

	}

	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		hideAllBalloons();
		return super.onTap(arg0, arg1);
	}

	@Override
	public void draw(Canvas arg0, MapView arg1, boolean arg2) {
		// Elimina la sombra
		super.draw(arg0, arg1, false);
	}

}
