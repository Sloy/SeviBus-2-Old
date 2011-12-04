package com.sloy.sevibus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.common.collect.Lists;
import com.sloy.sevibus.R;

import java.util.List;

public class EditarFavoritaActivity extends FragmentActivity {

	private Entity mParada;
	private Entity mFavorita;
	private ChoicesAdapter mAdapter;

	private EditText mDescripcion;
	private TextView mNombre, mNumero, mLineasTodas;
	private ListView mList;
	private CheckBox mTodas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editar_favorito);

		mDescripcion = (EditText)findViewById(R.id.favorita_nombre_editar);
		mNombre = (TextView)findViewById(R.id.parada_nombre_nombre);
		mNumero = (TextView)findViewById(R.id.parada_nombre_numero);
		mList = (ListView)findViewById(android.R.id.list);
		mTodas = (CheckBox)findViewById(R.id.favorita_lineas_todas);
		mLineasTodas = (TextView)findViewById(R.id.favorita_lineas_todas_text);
		
		mTodas.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					// Todas
					mLineasTodas.setVisibility(View.VISIBLE);
					mList.setVisibility(View.GONE);
				}else{
					// Seleccionar
					mLineasTodas.setVisibility(View.GONE);
					mList.setVisibility(View.VISIBLE);
				}
			}
		});

		// Coge la parada
		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			// Obtiene id de parada del Intent
			long parada = getIntent().getLongExtra("parada", 0);
			if(parada == 0){
				throw new Exception("No se pasó parada");
			}
			// Sacamos la favorita y la parada de la base de datos
			mFavorita = db.getTopEntity("favoritas", "parada_id = " + parada, null);
			mParada = db.getTopEntity("paradas", "_id = " + parada, null);
			// Sacamos la lista de líneas de esta parada
			List<Entity> rel = db.getEntityList("relaciones", "parada_id=" + parada);
			List<Entity> lineas = Lists.newArrayList();
			for(Entity e : rel){
				Entity l = db.getTopEntity("lineas", "_id=" + e.getString("linea_id"), null);
				lineas.add(l);
			}
			mAdapter = new ChoicesAdapter(this, lineas);
		}catch(Exception e){
			Log.e("sevibus", "Error manejando la base de datos", e);
		}finally{
			if(db != null){
				db.close();
			}
		}

		// Coloca los valores por defecto
		// -- Independientes
		mDescripcion.setHint("Parada nº " + mParada.getString("numero"));
		mNombre.setText(mParada.getString("nombre"));
		mNumero.setText("Parada nº " + mParada.getString("numero"));
		mList.setAdapter(mAdapter);
		mList.setItemsCanFocus(false);

		// -- Dependientes
		if(mFavorita == null){
			// Nueva
			setTitle("Nueva favorita");
			// Por defecto todas las líneas
			mList.setVisibility(View.GONE);
			mLineasTodas.setVisibility(View.VISIBLE);
			//TODO la línea se le pasa por Intent
		}else{
			// Editar
			setTitle("Editar favorita");
			mDescripcion.setText(mFavorita.getString("descripcion"));
			// Si hay línea guardada...
			long linea = mFavorita.getLong("linea_id");
			if(linea!=0){
				// La selecciona y muestra la lista
				for(int i=0;i<mAdapter.getCount();i++){
					if(mAdapter.getItemId(i)==linea){
						// Es nuestra línea
						mList.setItemChecked(i, true);
					}
				}
				mList.setVisibility(View.VISIBLE);
				mLineasTodas.setVisibility(View.GONE);
				mTodas.setChecked(false);
			}else{
				// No hay línea guardada, muestra todas
				mList.setVisibility(View.GONE);
				mLineasTodas.setVisibility(View.VISIBLE);
			}
			
		}
		
		// Mierda del foco
		mNumero.requestFocus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.editar_favorita, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.menu_ok:
				guardar();
				finish();
				return true;
			case R.id.menu_cancelar:
				finish();
				return true;
			case android.R.id.home:
				startActivity(new Intent(this, HomeActivity.class));
				return true;
			default:
				return false;
		}
	}
	
	private void guardar() {
		//TODO guardar el favorito
	}

	private class ChoicesAdapter extends BaseAdapter {
		
		private Context mContext;
		private List<Entity> mLineas;
		
		public ChoicesAdapter(Context context, List<Entity> lineas) {
			mContext = context;
			mLineas = lineas;
		}

		@Override
		public int getCount() {
			return mLineas.size();
		}

		@Override
		public Entity getItem(int pos) {
			return mLineas.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return getItem(pos).getId();
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			Entity item = getItem(pos);
			if(convertView == null){
				convertView = View.inflate(mContext, R.layout.item_list_choice_linea, null);
			}
			TextView nombre = (TextView)convertView.findViewById(R.id.item_linea_nombre);
			TextView trayecto = (TextView)convertView.findViewById(R.id.item_linea_trayecto);
			nombre.setText(item.getString("nombre"));
			trayecto.setText(item.getString("trayecto"));
			trayecto.setSingleLine();
			trayecto.setEllipsize(TextUtils.TruncateAt.END);
			return convertView;
		}

	}

}
