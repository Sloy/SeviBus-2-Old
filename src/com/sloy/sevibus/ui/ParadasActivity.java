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
import android.widget.Toast;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.common.collect.Lists;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.IntentParada;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParadasActivity extends FragmentActivity {

	private ListView mList;
	private ParadasAdapter mAdapter;
	private String mLinea;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);

		// coge la línea que se le ha pasado
		final long linea = getIntent().getLongExtra("linea", 0);
		mLinea = getIntent().getStringExtra("nombre");
		if(linea == 0){
			// TODO comprobar otra cosa?
			Toast.makeText(this, "No se pasó nunguna línea", Toast.LENGTH_SHORT).show();
			finish();
		}

		setTitle("Paradas de la línea " + mLinea);

		mList = (ListView)findViewById(android.R.id.list);
		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				startActivity(new IntentParada(ParadasActivity.this, mAdapter.getItem(pos).getId()).setLinea(linea));
			}
		});
		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			// ArrayList<Entity> lista = db.getEntityListWithFrom("paradas",
			// "paradas AS p JOIN relaciones AS r ON p._id=r.parada_id JOIN lineas AS l ON r.linea_id=l._id",
			// "r.linea_id=1", null, null);
			List<Entity> paradas = Lists.newArrayList();
			List<Entity> rel = db.getEntityList("relaciones", "linea_id=" + linea);
			for(Entity e : rel){
				Entity parada = db.getTopEntity("paradas", "_id=" + e.getInt("parada_id"), null);
				paradas.add(parada);
			}
			Collections.sort(paradas, new Comparator<Entity>() {
				@Override
				public int compare(Entity lhs, Entity rhs) {
					return new Integer(lhs.getString("numero")).compareTo(new Integer(rhs.getString("numero")));
				}
			});
			mAdapter = new ParadasAdapter(this, paradas);
		}catch(Exception e){
			Log.e("sevibus", e.toString(), e);
		}finally{
			db.close();
		}

		if(mAdapter != null){
			mList.setAdapter(mAdapter);
		}
	}

	private class ParadasAdapter extends BaseAdapter {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.paradas, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
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
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.format(getString(R.string.email_text_paradas), mLinea));
		startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent)));
	}
}
