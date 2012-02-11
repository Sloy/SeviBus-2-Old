package com.sloy.sevibus.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dataframework.Entity;
import com.sloy.sevibus.R;

import java.util.List;

public class ParadasAdapter extends BaseAdapter {
	List<Entity> mItems;
	private Context mContext;

	public ParadasAdapter(Context context, List<Entity> items) {
		mItems = items;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Entity getItem(int pos) {
		return mItems.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return mItems.get(pos).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = getItem(position);
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.list_item_parada, null);
		}
		TextView numero = (TextView)convertView.findViewById(R.id.item_parada_numero);
		TextView nombre = (TextView)convertView.findViewById(R.id.item_parada_nombre);
		ImageView mapa = (ImageView)convertView.findViewById(R.id.item_parada_mapa);

		numero.setText(item.getString("numero"));
		nombre.setText(item.getString("nombre"));
		if(item.getDouble("latitud") != 0.0 && item.getDouble("longitud") != 0.0){
			mapa.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
}