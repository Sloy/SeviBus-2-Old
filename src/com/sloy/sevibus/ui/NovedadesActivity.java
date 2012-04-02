package com.sloy.sevibus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.sloy.sevibus.utils.TweetHolder;
import com.sloy.sevibus.utils.Utils;

import twitter4j.TwitterException;

import java.util.List;

public class NovedadesActivity extends SherlockActivity {

	private List<TweetHolder> mListTweets;
	private TwitterAdapter mAdapter;
	private Context mCtx;
	private boolean running = false;

	private Handler handler = new Handler();
	
	private Runnable downloadTweets = new Runnable() {
		@Override
		public void run() {
			try{
				/*
				 * Debe obtener la lista de últimos tweets de tussam,
				 * compararlos con los guardados en la BD y actualizar la lista
				 * según los que haya nuevos
				 */
				List<TweetHolder> newReceived = Lists.newArrayList();
				// Obtiene la lista de tweets recientes
				List<twitter4j.Status> stlist = Utils.getTussamNews();
				// Los pasa a tipo tweetholder
				for(twitter4j.Status s : stlist){
					newReceived.add(new TweetHolder(s).setNuevo(true));
				}
				// Coge la lista de tweets guardados anteriormente
				List<TweetHolder> cache = cargarCache();
				if(!cache.isEmpty()){// siempre y cuando la cache tenga algo
										// guardado
					// Coge el último tweet como referencia
					TweetHolder lastTwitCached = cache.get(0);
					// Itera sobre la lista de nuevos tweets para quedarse con
					// los que no estén guardados, y los mete en la lista
					// principal vacía
					//TODO creo que hay un problema al trabajar directamente con esta lista
					mListTweets.clear();
					for(TweetHolder t : newReceived){
						if(t.compareTo(lastTwitCached) > 0){
							// Es más nuevo
							mListTweets.add(t);
						}
					}
					// Guarda en caché los nuevos de ahora
					guardarCache(mListTweets);
					// Mete a continuación los antiguos
					mListTweets.addAll(cache);
				}else{
					mListTweets = newReceived;
					// Guarda los nuevos en la caché
					guardarCache(newReceived);
				}
				// Los devuelve al hilo principal para trabajar con ellos
			}catch(TwitterException e){
				Log.e("sevibus", "Error al descargar los tweets de @TussamSevilla", e);
				Toast.makeText(mCtx, "Error al descargar los tweets", Toast.LENGTH_SHORT).show();
			}
			handler.post(postDownload);
		}
	};

	private Runnable postDownload = new Runnable() {
		@Override
		public void run() {
			setProgressBarIndeterminateVisibility(false);
			mAdapter.notifyDataSetChanged();
			running = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_novedades);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Novedades");

		mCtx = this;
		// Carga los tweets guardados actualmente
		mListTweets = cargarCache();
		// Asigna el adapter al listview
		ListView list = (ListView)findViewById(android.R.id.list);
		mAdapter = new TwitterAdapter();
		list.setAdapter(mAdapter);

		// Carga los nuevos
		actualizar();
	}

	private List<TweetHolder> cargarCache() {
		List<TweetHolder> res = Lists.newArrayList();
		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			for(Entity e : db.getEntityList("tweets", null, "date desc")){
				res.add(new TweetHolder(e));
			}
		}catch(Exception e){
			Log.e("sevibus", "Error obteniendo los tweets guardados", e);
		}
		db.close();
		return res;
	}

	private void actualizar() {
		if(!running){
			setProgressBarIndeterminateVisibility(true);
			new Thread(downloadTweets).start();
		}

	}

	private void abrirNavegador() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.novedades, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.menu_actualizar:
				actualizar();
				return true;
			case R.id.menu_navegador:
				abrirNavegador();
				return true;
			case android.R.id.home:
				startActivity(new Intent(this, HomeActivity.class));
				return true;
			default:
				return false;
		}
	}

	private void guardarCache(List<TweetHolder> tweets) {
		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			// TODO tareas de limpieza, por favor
			Entity e;
			for(TweetHolder th : tweets){
				e = new Entity("tweets");
				e.setValue("id", th.getId());
				e.setValue("date", th.getFecha().getTime());
				e.setValue("text", th.getTexto());
				e.save();
			}
		}catch(Exception e){
			Log.e("sevibus", "Error al guardar la la caché", e);
		}
		db.close();
	}

	private class TwitterAdapter extends BaseAdapter {

		String format = "dd MMM k:mm";

		@Override
		public int getCount() {
			if(mListTweets != null){
				return mListTweets.size();
			}else{
				return 0;
			}
		}

		@Override
		public TweetHolder getItem(int position) {
			return mListTweets.get(position);
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TweetHolder th = getItem(position);
			View v = convertView;
			if(v == null){
				v = LayoutInflater.from(mCtx).inflate(R.layout.item_list_tweet, parent, false);
			}

			TextView fecha = (TextView)v.findViewById(R.id.item_novedades_twitter_fecha);
			TextView texto = (TextView)v.findViewById(R.id.item_novedades_twitter_texto);

			fecha.setText(DateFormat.format(format, th.getFecha()));
			texto.setText(th.getTexto());

			if(th.isNuevo()){
				texto.setTypeface(null, 1);
			}else{
				texto.setTypeface(null, 0);
			}
			return v;
		}

	}

}
