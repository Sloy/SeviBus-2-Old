package com.sloy.sevibus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.common.collect.Lists;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.Datos;
import com.sloy.sevibus.utils.Datos.OnDialogListener;

import java.util.List;

public class FavoritasActivity extends FragmentActivity {

	private ListView mList;
	private FavoritasAdapter mAdapter;
	private boolean editMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);

		mList = (ListView)findViewById(android.R.id.list);
		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if(!editMode){
					Intent i = new Intent(FavoritasActivity.this, ParadaInfoActivity.class);
					i.putExtra("parada", mAdapter.getItem(pos).getId());
					startActivity(i);
				}else{
					OnDialogListener listener = new OnDialogListener() {
						@Override
						public void onDialog() {
							recargarLista();
						}
					};
					Datos.createAlertDialog(FavoritasActivity.this, mAdapter.getItem(pos), listener).show();
				}
			}
		});
		recargarLista();

	}

	private void recargarLista() {
		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			// ArrayList<Entity> lista = db.getEntityListWithFrom("paradas",
			// "paradas AS p JOIN relaciones AS r ON p._id=r.parada_id JOIN lineas AS l ON r.linea_id=l._id",
			// "r.linea_id=1", null, null);
			List<Entity> paradas = Lists.newArrayList();
			List<String> descs = Lists.newArrayList();
			List<Entity> rel = db.getEntityList("favoritas");
			for(Entity e : rel){
				Entity parada = db.getTopEntity("paradas", "_id=" + e.getInt("parada_id"), null);
				paradas.add(parada);
				descs.add(e.getString("descripcion"));
			}
			mAdapter = new FavoritasAdapter(this, paradas, descs);
		}catch(Exception e){
			Log.e("sevibus", e.toString(), e);
		}finally{
			db.close();
		}

		if(mAdapter != null){
			mList.setAdapter(mAdapter);
		}
	}

	private class FavoritasAdapter extends BaseAdapter {

		List<Entity> mItems;
		List<String> mDescripciones;
		private Context mContext;

		public FavoritasAdapter(Context context, List<Entity> items, List<String> descripciones) {
			mItems = items;
			mDescripciones = descripciones;
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

			String desc = mDescripciones.get(position);
			if(!desc.equals("")){
				TextView estatico = (TextView)convertView.findViewById(R.id.item_parada_numero_staticText);
				estatico.setText(desc);
				numero.setText("(" + item.getString("numero") + ")");
			}else{
				numero.setText(item.getString("numero"));

			}
			nombre.setText(item.getString("nombre"));
			if(item.getDouble("latitud") != 0.0 && item.getDouble("longitud") != 0.0){
				mapa.setVisibility(View.VISIBLE);
			}
			return convertView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.favoritas, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.menu_editar:
				setEditMode(!editMode);
				return true;
			case R.id.menu_reportar:
				reportar();
				return true;
			case android.R.id.home:
				startActivity(new Intent(this, HomeActivity.class));
				return true;
			default:
				return false;
		}
	}

	private void reportar() {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_address)});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.email_text_favoritas));
		startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent)));
	}

	private void setEditMode(boolean flag) {
		editMode = flag;
	}

}
