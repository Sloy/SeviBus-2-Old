package com.sloy.sevibus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.common.collect.Lists;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.IntentEditarFavorita;
import com.sloy.sevibus.utils.IntentParada;

import java.util.List;

public class FavoritasActivity extends SherlockActivity {

	private ListView mList;
	private FavoritasAdapter mAdapter;
	private List<Entity> mFavoritas;
	private Animation mAnimShake;
	private TextView mEmpty;

	private int selectedItem = -1;
	private ActionMode mActionMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mAnimShake = AnimationUtils.loadAnimation(this, R.anim.shake);
		setTitle("Paradas favoritas");

		mEmpty = (TextView)findViewById(android.R.id.empty);
		mList = (ListView)findViewById(android.R.id.list);
		mList.setChoiceMode(ListView.CHOICE_MODE_NONE);

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if(mActionMode == null){
					startActivity(new IntentParada(FavoritasActivity.this, mAdapter.getItem(pos).getId()).setLinea(mFavoritas.get(pos).getLong(
							"linea_id")));
				}else{
					selectedItem = pos;
					mAdapter.notifyDataSetChanged();
				}
			}
		});
		mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v, final int pos, long arg3) {
				// Abre el actionmode
				if(mActionMode == null){
					mActionMode = startActionMode(new FavoritasActionMode());
				}
				// Marca la parada seleccionada
				selectedItem = pos;
				mAdapter.notifyDataSetChanged();
				return true;
			}
		});
	}

	private void editar() {
		startActivity(new IntentEditarFavorita(FavoritasActivity.this, mAdapter.getItem(selectedItem).getId()));
	}

	private void eliminar() {
		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(FavoritasActivity.this, getPackageName());
			db.getTopEntity("favoritas", "parada_id=" + mAdapter.getItemId(selectedItem), null).delete();
		}catch(Exception e){
			Log.e("sevibus", "Error al eliminar la favorita", e);
		}finally{
			db.close();
		}
		recargarLista();
		shake(mList);
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
			mFavoritas = rel;
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
			if(mAdapter.getCount() == 0){
				// No hay elementos
				mEmpty.setText("No tienes ningún favorito. Guárdalos desde la pantalla de parada pulsando el icono de estrella del ActionBar");
				mEmpty.setVisibility(View.VISIBLE);
			}else{
				mEmpty.setVisibility(View.GONE);
			}
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
			/*if(convertView == null){
				convertView = View.inflate(mContext, R.layout.list_item_parada, null);
			}*/
			convertView = View.inflate(mContext, R.layout.list_item_parada, null);
			if(mActionMode != null && selectedItem == position){
				convertView.setBackgroundResource(R.color.sevibus_seleccionado);
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
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.favoritas, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				startActivity(new Intent(this, HomeActivity.class));
				return true;
			default:
				return false;
		}
	}

	private void shake(View v) {
		v.startAnimation(mAnimShake);
	}

	@Override
	protected void onResume() {
		super.onResume();
		recargarLista();
	}

	private final class FavoritasActionMode implements ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.favoritas_actionmode, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			mode.setTitle("Modificar favoritas");
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()){
				case R.id.menu_editar:
					editar();
					break;
				case R.id.menu_eliminar:
					eliminar();
					break;
				default:
					return false;
			}
			mode.finish();

			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			mAdapter.notifyDataSetChanged();
		}

	}

}
