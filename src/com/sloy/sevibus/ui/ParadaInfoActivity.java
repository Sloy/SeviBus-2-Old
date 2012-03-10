package com.sloy.sevibus.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.common.collect.Lists;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.Datos;
import com.sloy.sevibus.utils.IntentEditarFavorita;
import com.sloy.sevibus.utils.IntentMapa;
import com.sloy.sevibus.utils.Utils;

import java.util.List;

public class ParadaInfoActivity extends SherlockActivity  {

	private Entity mParada;
	private List<String> mLineas;
	private List<Integer[][]> mTiempos;
	private LlegadasAdapter mAdapter;
	private Entity mLineaProcedente;

	private ListView mList;
	private TextView mTxtNombre, mTxtNumero, mTxtDireccion;
	private Button mBtMapa;
	private ImageButton mBtActualizar, mBtMostrarTodas;
	private View mContainerDireccion;
	private boolean isFavorita;
	private boolean mostrarTodas = false;
	private boolean mLoading = true;
	private Animation mAnimBlink,mAnimExpand;
	private TiemposLoader mLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// This has to be called before setContentView and you must use the
		// class in android.support.v4.view and NOT android.view
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_parada);
		
		setTitle("Info. de parada");

		mAnimBlink = AnimationUtils.loadAnimation(this, R.anim.blink);
		mAnimExpand = AnimationUtils.loadAnimation(this, R.anim.expand_contract);
		
		mTxtNumero = (TextView)findViewById(R.id.parada_nombre_numero);
		mTxtNombre = (TextView)findViewById(R.id.parada_nombre_nombre);
		mTxtDireccion = (TextView)findViewById(R.id.parada_direccion_direccion);
		mBtMapa = (Button)findViewById(R.id.parada_direccion_mapa);
		mContainerDireccion = findViewById(R.id.parada_seccion_direccion);
		mList = (ListView)findViewById(android.R.id.list);
		mBtActualizar = (ImageButton)findViewById(R.id.parada_llegadas_actualizar);
		mBtMostrarTodas = (ImageButton)findViewById(R.id.parada_llegadas_todas_button);

		mBtMostrarTodas.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleMostrar();
			}
		});

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if(mLoading){
					// Aún no ha cargado los tiempos, avisa para que espere
					Toast.makeText(ParadaInfoActivity.this, "Espera a que terminen de cargar los tiempos", Toast.LENGTH_SHORT).show();
				}else{
					String lin = null;
					if(mostrarTodas || mLineaProcedente == null){
						lin = mLineas.get(pos);
					}else{
						lin = mLineaProcedente.getString("nombre");
					}

					Integer[][] t = mTiempos.get(pos);
					String display = String.format("Siguiente llegada:\n%1s\n\nPróxima llegada:\n%2s", getTextoDisplay(t[0][0], t[0][1]),
							getTextoDisplay(t[1][0], t[1][1]));
					new AlertDialog.Builder(ParadaInfoActivity.this).setTitle("Línea " + lin).setMessage(display).setNeutralButton("Cerrar", null)
							.create().show();
				}
			}
		});

		mBtMapa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new IntentMapa(ParadaInfoActivity.this).setParada(mParada.getId()));
			}
		});

		mBtActualizar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refresh();
			}
		});
		
		// Obtiene la opción de mostrar todas
		mostrarTodas = Datos.getPrefs().getBoolean("mostrar_todas", true);

		// obtiene el id de la parada pasada por el intent
		long parada = getIntent().getLongExtra("parada", 0);
		if(parada == 0){
			Toast.makeText(this, "No se pasó ninguna parada", Toast.LENGTH_SHORT).show();
			finish();
		}
		long linea = getIntent().getLongExtra("linea", 0);

		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			// Mírame si la tengo de favorita o no, anda
			isFavorita = db.getEntityListCount("favoritas", "parada_id=" + parada) > 0;
			// Saca la entity de la base de datos
			mParada = db.getTopEntity("paradas", "_id = " + parada, null);
			// Saca la línea, si se le ha pasado
			mLineaProcedente = db.getTopEntity("lineas", "_id=" + linea, null);
			// saca la lista de líneas que pasan por esta parada
			List<Entity> rel = db.getEntityList("relaciones", "parada_id=" + parada);
			// List<Entity> lineas = Lists.newArrayList();
			mLineas = Lists.newArrayList();
			for(Entity e : rel){
				Entity l = db.getTopEntity("lineas", "_id=" + e.getString("linea_id"), null);
				// lineas.add(l);
				mLineas.add(l.getString("nombre"));
			}
		}catch(Exception e){
			Log.e("sevibus", e.toString(), e);
		}finally{
			db.close();
		}
		if(mParada == null){
			// TODO controlar error
			Log.e("sevibus", "Entity null");
		}

		// pone la información en pantalla
		mTxtNumero.setText("Parada nº " + mParada.getString("numero"));
		mTxtNombre.setText(mParada.getString("nombre"));
		String direccion = mParada.getString("direccion");
		if(direccion == null || direccion.equals("")){
			mContainerDireccion.setVisibility(View.GONE);
		}else{
			mTxtDireccion.setText(direccion);
		}

		if(mLineaProcedente == null){
			mBtMostrarTodas.setVisibility(View.GONE);
		}else{
			mBtMostrarTodas.startAnimation(mAnimExpand);
		}
		refresh();

	}

	protected void toggleMostrar() {
		// Cambia el estado de mostrar
		mostrarTodas = !mostrarTodas;
		// Vacía los tiempos para evitar error
//		mTiempos = null;
		// Refresca los tiempos
		refresh();
		// Guarda el nuevo estado de mostrar en sharedPreferences para recordarlo
		Datos.getPrefs().edit().putBoolean("mostrar_todas", mostrarTodas).commit();
	}

	// TODO esto es una mierda que hay que cambiar por completo
	private class LlegadasAdapter extends BaseAdapter {

		Context mContext;
		List<String> mItems;
		List<String> mTiempos; //

		public LlegadasAdapter(Context context, List<String> lineas, List<String> tiempos) {
			mItems = lineas;
			mTiempos = tiempos;
			mContext = context;
		}

		public LlegadasAdapter(Context context, String linea, String tiempo) {
			mItems = Lists.newArrayList();
			mItems.add(linea);
			if(tiempo == null){
				mTiempos = null;
			}else{
				mTiempos = Lists.newArrayList();
				mTiempos.add(tiempo);
			}
			mContext = context;
		}

		public void setTiempos(List<String> tiempos) {
			mTiempos = tiempos;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public String getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			// return getItem(position).getId();
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Entity item = getItem(position);
			if(convertView == null){
				convertView = View.inflate(mContext, R.layout.item_list_llegada, null);
			}
			TextView linea = (TextView)convertView.findViewById(R.id.item_llegada_linea);
			TextView text = (TextView)convertView.findViewById(R.id.item_llegada_texto);
			// linea.setText(item.getString("nombre"));
			linea.setText(getItem(position));
			if(mTiempos == null){
				text.setText("Cargando...");
			}else{
				text.setText(mTiempos.get(position));
			}

			if(mLineaProcedente != null && mostrarTodas && mLineaProcedente.getString("nombre").equals(mItems.get(position))){
				convertView.setBackgroundResource(R.drawable.button_trans_pressed);
			}else{
				convertView.setBackgroundResource(R.drawable.button_trans_normal);
			}
			return convertView;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.parada_fav, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.menu_parada_fav:
				startActivity(new IntentEditarFavorita(this, mParada.getId()));
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

	private class TiemposLoader extends AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... params) {
			List<String> tiempos = Lists.newArrayList();
			mTiempos = Lists.newArrayList();
			int parada = mParada.getInt("numero");
			if(mostrarTodas || mLineaProcedente == null){
				// muestra todas
				for(String lin : mLineas){
					Integer[][] tiempo = Utils.getTiempos(lin, parada);
					mTiempos.add(tiempo);
					tiempos.add(getTextoDisplay(tiempo[0][0], tiempo[0][1]));
				}
			}else{
				// muestra una
				Integer[][] tiempo = Utils.getTiempos(mLineaProcedente.getString("nombre"), parada);
				mTiempos.add(tiempo);
				tiempos.add(getTextoDisplay(tiempo[0][0], tiempo[0][1]));
			}
			return tiempos;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			mAdapter.setTiempos(result);
			setProgressBarIndeterminateVisibility(Boolean.FALSE);
//			mAnimBlink.cancel();
			mBtActualizar.clearAnimation();
			mAnimBlink.reset();
			mLoading = false;
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(Boolean.TRUE);
			mBtActualizar.startAnimation(mAnimBlink);
			mLoading = true;
			super.onPreExecute();
		}

	}

	private void reportar() {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_address)});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.format(getString(R.string.email_text_parada), mParada.getString("numero")));
		startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent)));
	}

	private String getTextoDisplay(int tiempo, int distancia) {
		String texto = null;
		if(tiempo > 0){
			texto = tiempo + " minutos (" + distancia + " metros)";
		}else if(tiempo == 0){
			texto = "Llegada inminente";
		}else{
			texto = "Sin estimaciones";
		}
		return texto;
	}

	public void refresh() {
		if(mostrarTodas){
			mBtMostrarTodas.setImageResource(R.drawable.expander_close_holo_light);
		}else{
			mBtMostrarTodas.setImageResource(R.drawable.expander_open_holo_light);
		}

		// pone los tiempos de llegada
//		mTiempos = null;
		if(mostrarTodas || mLineaProcedente == null){
			// Todas
			mAdapter = new LlegadasAdapter(this, mLineas, null);
		}else{
			// Sólo una
			mAdapter = new LlegadasAdapter(this, mLineaProcedente.getString("nombre"), null);
		}
		mList.setAdapter(mAdapter); // cargando, provisional

		if(mLoader != null){
			mLoader.cancel(true);
		}
		mLoader = new TiemposLoader();
		mLoader.execute();
	}

}