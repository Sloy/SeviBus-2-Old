package com.sloy.sevibus.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.sloy.sevibus.utils.IntentEditarFavorita;
import com.sloy.sevibus.utils.IntentMapa;
import com.sloy.sevibus.utils.Llegada;
import com.sloy.sevibus.utils.ServerErrorException;
import com.sloy.sevibus.utils.Utils;

import java.net.SocketTimeoutException;
import java.util.List;

public class ParadaInfoActivity extends SherlockActivity {

	/**
	 * Entida correspondiente a la parada actual
	 */
	private Entity mParada;
	/**
	 * Lista de líneas pertenecientes a esta parada
	 */
	private List<Entity> mLineas;

	private LlegadasAdapter mAdapter;

	/**
	 * Índice de la línea prioritaria en la lista de líneas. Si no la hay será
	 * -1
	 */
	private int mLineaPrioritaria = -1;

	private ListView mList;
	private TextView mTxtNombre, mTxtNumero, mTxtDireccion;
	private Button mBtMapa;
	private ImageButton mBtActualizar;
	private View mContainerDireccion;
	private Animation mAnimBlink;

	private boolean mLoading = false;
	private boolean mTimeout = false;
	private boolean mDisconnected = false;
	private boolean mError = false;

	private List<Entity> mCola;

	private Handler handler = new Handler();
	private Runnable backgroundDownload = new Runnable() {
		@Override
		public void run() {
			Entity linea = mCola.get(0);
			try{
				// Comienza la descarga
				Llegada tiempo = Utils.getTiempos(linea, mParada.getInt("numero"));

				// Actualiza el adapter
				mAdapter.addLlegada(tiempo);
				// mAdapter.addLlegada(new Llegada(linea.getId(), null, null));

				// Quita la línea de la cola
				mCola.remove(linea);

				// Notifica al hilo principal de que se terminó la descarga para
				// que
				// continúe con la cola
				Log.d("sevibus", "Actualizada línea " + linea.getString("nombre"));
				handler.post(finishBackgroundDownload);
			}catch(SocketTimeoutException e){
				Log.e("sevibus", "Se alcanzó el timeout", e);
				// Notifica a la interfaz del error y detiene las descargas
				mTimeout = true;
				handler.post(timeoutBackgroundDownload);

			}catch(ServerErrorException e){
				handler.post(serverError);
			}

		}
	};
	private Runnable finishBackgroundDownload = new Runnable() {
		@Override
		public void run() {
			// Notifica de los cambios
			mAdapter.notifyDataSetChanged();
			// Actualiza la barra de progreso
			setSupportProgress(getProgress(100 - (100 * mCola.size() / mLineas.size())));
			// Sigue con las descargas
			runNext();

		}
	};
	private Runnable timeoutBackgroundDownload = new Runnable() {
		@Override
		public void run() {
			// Notifica para que se actualice la lista
			mAdapter.notifyDataSetChanged();
			// Actualiza la barra de progreso
			setSupportProgress(getProgress(100));
			// Avisa al usuario
			notificaTimeout();
			// Y para la interfaz
			finCola();
		}
	};

	private Runnable serverError = new Runnable() {
		@Override
		public void run() {
			mError=true;
			// Notifica para que se actualice la lista
			mAdapter.notifyDataSetChanged();
			// Actualiza la barra de progreso
			setSupportProgress(getProgress(100));
			// Avisa al usuario
			Toast.makeText(ParadaInfoActivity.this, "Error con el servidor, inténtalo de nuevo", Toast.LENGTH_SHORT).show();
			// Y para la interfaz
			finCola();
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mCola != null){
			mCola.clear();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// This has to be called before setContentView and you must use the
		// class in android.support.v4.view and NOT android.view
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_parada);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setTitle("Info. de parada");

		mAnimBlink = AnimationUtils.loadAnimation(this, R.anim.blink);
		mTxtNumero = (TextView)findViewById(R.id.parada_nombre_numero);
		mTxtNombre = (TextView)findViewById(R.id.parada_nombre_nombre);
		mTxtDireccion = (TextView)findViewById(R.id.parada_direccion_direccion);
		mBtMapa = (Button)findViewById(R.id.parada_direccion_mapa);
		mContainerDireccion = findViewById(R.id.parada_seccion_direccion);
		mList = (ListView)findViewById(android.R.id.list);
		mBtActualizar = (ImageButton)findViewById(R.id.parada_llegadas_actualizar);

		mList.setChoiceMode(ListView.CHOICE_MODE_NONE);
		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if(mTimeout){
					notificaTimeout();
				}else{
					// Muestra un diálogo con las 2 llegadas
					Llegada llegada = mAdapter.getItem(pos);
					if(llegada == null){
						Toast.makeText(ParadaInfoActivity.this, "Espera a que termine de cargar los tiempos", Toast.LENGTH_SHORT).show();
					}else{
						String title = String.format("Línea %1s", mLineas.get(pos).getString("nombre"));
						String display = String.format("Siguiente llegada:\n%1s\n\nPróxima llegada:\n%2s", llegada.getTexto1(), llegada.getTexto2());
						new AlertDialog.Builder(ParadaInfoActivity.this).setTitle(title).setMessage(display).setNeutralButton("Cerrar", null)
								.create().show();
					}
				}
			}
		});
		mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				// Cambia la lína preferente
				mLineaPrioritaria = pos;
				// Le dice algo al usuario
				Toast.makeText(ParadaInfoActivity.this, "Se establecido " + mLineas.get(pos).getString("nombre") + " como prioritaria",
						Toast.LENGTH_SHORT).show();
				return true;
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
				cargaTiempos();
			}
		});

		/* -- Carga las líneas de esta parada -- */
		// obtiene el id de la parada pasada por el intent
		long parada = getIntent().getLongExtra("parada", 0);
		if(parada == 0){
			Toast.makeText(this, "No se pasó ninguna parada", Toast.LENGTH_SHORT).show();
			finish();
		}
		// Línea prioritaria, si se le ha pasado
		long linea = getIntent().getLongExtra("linea", 0);

		// Saca los datos de la BD
		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			// Mírame si la tengo de favorita o no, anda
			// isFavorita = db.getEntityListCount("favoritas", "parada_id=" +
			// parada) > 0;

			// Saca esta parada de la base de datos
			mParada = db.getTopEntity("paradas", "_id = " + parada, null);

			// Saca, si se le ha pasado, la línea prioritaria
			Entity prioritaria = db.getTopEntity("lineas", "_id=" + linea, null);

			// Saca la lista de líneas que pasan por esta parada...
			List<Entity> rel = db.getEntityList("relaciones", "parada_id=" + parada);
			mLineas = Lists.newArrayList();
			for(Entity e : rel){
				Entity l = db.getTopEntity("lineas", "_id=" + e.getString("linea_id"), null);
				mLineas.add(l);
				// busca la línea prioritaria, si hay
				if(prioritaria != null && l.getId() == prioritaria.getId()){
					// bingo!
					mLineaPrioritaria = mLineas.size() - 1;
				}
			}
			// Crea el adapter vacío
			mAdapter = new LlegadasAdapter(this, null);
			mList.setAdapter(mAdapter);
		}catch(Exception e){
			Log.e("sevibus", e.toString(), e);
		}finally{
			db.close();
		}

		// Pone la información de la parada en la pantalla
		mTxtNumero.setText("Parada nº " + mParada.getString("numero"));
		mTxtNombre.setText(mParada.getString("nombre"));
		String direccion = mParada.getString("direccion");
		if(direccion == null || direccion.equals("")){
			mContainerDireccion.setVisibility(View.GONE);
		}else{
			mTxtDireccion.setText(direccion);
		}

		// Carga los tiempos
		cargaTiempos();

	}

	private class LlegadasAdapter extends BaseAdapter {

		Context mContext;
		List<Llegada> mLlegadas;

		public LlegadasAdapter(Context context, List<Llegada> llegadas) {
			if(llegadas == null){
				mLlegadas = Lists.newArrayList();
			}else{
				mLlegadas = llegadas;
			}
			mContext = context;
		}

		/**
		 * Añade una llegada al adapter. Requiere notificar los cambios
		 * manualmente (thread safe)
		 * 
		 * @param l
		 */
		public void addLlegada(Llegada l) {
			mLlegadas.add(l);
			// notifyDataSetChanged();
		}

		/**
		 * Vacía la lista de llegadas y actualiza la lista para mostrarse
		 * cargando
		 */
		public void reset() {
			mLlegadas.clear();
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mLineas.size();
		}

		@Override
		/**
		 * Devuelve la llegada correspondiente a la línea marcada por la posición, null si no está aún
		 */
		public Llegada getItem(int position) {
			// Comprobamos que no esté consultando fuera de la lista
			for(Llegada l : mLlegadas){
				// Si la llegada corresponde a la línea la devuelve
				if(l.getLineaID() == mLineas.get(position).getId()){
					return l;
				}
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// return getItem(position).getId();
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = View.inflate(mContext, R.layout.item_list_llegada, null);
			}
			TextView linea = (TextView)convertView.findViewById(R.id.item_llegada_linea);
			TextView text = (TextView)convertView.findViewById(R.id.item_llegada_texto);

			// Pone el nombre de la línea
			linea.setText(mLineas.get(position).getString("nombre"));

			// Si se ha producido timeout muestro el error, me da igual el resto
			if(mError){
				text.setText("Error");
			}else if(mDisconnected){
				text.setText("Necesaria conexión a Internet");
			}else if(mTimeout){
				text.setText("No hay respuesta :(");
			}else{
				// Si tenemos la llegada ponemos la info, si no cargando
				Llegada llegada = getItem(position);
				if(llegada != null){
					text.setText(llegada.getTexto1());
				}else{
					text.setText("Cargando...");
				}
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
			case android.R.id.home:
				startActivity(new Intent(this, HomeActivity.class));
				return true;
			default:
				return false;
		}
	}

	private void cargaTiempos() {
		// Comprueba la conexión a Internet, importante
		mDisconnected = !Utils.isNetworkAvailable(this);
		if(mDisconnected){
			mDisconnected = true;
			notificaSinConexion();
			mAdapter.notifyDataSetChanged();
			return;
		}
		// Pone la interfaz cargando
		setSupportProgress(getProgress(1));
		mBtActualizar.startAnimation(mAnimBlink);
		mTimeout = false;
		mError = false;

		mAdapter.reset();
		// Limpia y crea la cola
		if(mCola == null){
			mCola = Lists.newArrayList();
		}else{
			mCola.clear();
		}
		mCola.addAll(mLineas);

		// Si hay prioritaria la pone en primer lugar
		if(mLineaPrioritaria != -1){
			mCola.remove(mLineaPrioritaria);
			mCola.add(0, mLineas.get(mLineaPrioritaria));
		}

		// Comenzar descargas si no está ya en ello
		if(!mLoading){
			runNext();
		}

	}

	private void runNext() {
		// android.os.Debug.waitForDebugger();
		mLoading = true;
		if(mCola.size() == 0){
			// Se acabó
			finCola();
		}else{
			// Descarga el siguiente tiempo en la cola
			Thread downloadThread = new Thread(backgroundDownload, "Linea " + mCola.get(0).getString("nombre"));
			downloadThread.start();
		}
	}

	private void finCola() {
		mLoading = false;
		setProgressBarIndeterminateVisibility(Boolean.FALSE);
		mBtActualizar.clearAnimation();
		mAnimBlink.reset();
	}

	private void notificaTimeout() {
		Toast.makeText(ParadaInfoActivity.this, "El servidor está tardando demasiado en responder. Intenta recargar de nuevo más tarde.",
				Toast.LENGTH_LONG).show();
	}

	private void notificaSinConexion() {
		Toast.makeText(ParadaInfoActivity.this, "No hay conexión a Internet.", Toast.LENGTH_LONG).show();
	}

	private int getProgress(int percentage) {
		return (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * percentage;
	}
}