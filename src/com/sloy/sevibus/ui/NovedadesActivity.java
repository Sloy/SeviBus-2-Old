package com.sloy.sevibus.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
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

	AsyncTask<Void, Void, List<TweetHolder>> downloadTweets = new AsyncTask<Void, Void, List<TweetHolder>>() {

		@Override
		protected List<TweetHolder> doInBackground(Void... params) {
			try{
				/*
				 * Debe obtener la lista de �ltimos tweets de tussam,
				 * compararlos con los guardados en la BD y actualizar la lista
				 * seg�n los que haya nuevos
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
					// Coge el �ltimo tweet como referencia
					TweetHolder lastTwitCached = cache.get(cache.size() - 1);
					// Itera sobre la lista de nuevos tweets para quedarse con
					// los
					// que no est�n guardados
					for(int i = 0; i < newReceived.size(); i++){
						// Si el tweet i es m�s nuevo que el �ltimo
						if(newReceived.get(i).compareTo(lastTwitCached) > 0){
							// Deja en la lista �nicamente los nuevos
							newReceived = newReceived.subList(i, newReceived.size());
						}
					}
				}
				// Guarda los nuevos en la cach�
				guardarCache(newReceived);
				// Los devuelve al hilo principal para trabajar con ellos
				return newReceived;
			}catch(TwitterException e){
				Log.e("sevibus", "Error al descargar los tweets de @TussamSevilla", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<TweetHolder> result) {
			if(result == null){
				Toast.makeText(mCtx, "Error al descargar los tweets", Toast.LENGTH_SHORT).show();
			}else{
				// Descarga correcta, actualiza la lista
				// Primero marca todos los actuales como no nuevos, yatussabe
				for(TweetHolder th : mListTweets){
					th.setNuevo(false);
				}
				// Luego a�ade los nuevos
				mListTweets.addAll(result);
				// Y actualiza la interfaz
				mAdapter.notifyDataSetChanged();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novedades);
		mCtx = this;
		// Carga los tweets guardados actualmente
		mListTweets = cargarCache();
		// Asigna el adapter al listview
		ListView list = (ListView)findViewById(android.R.id.list);
		mAdapter = new TwitterAdapter();
		list.setAdapter(mAdapter);

		// Carga los nuevos
		downloadTweets.execute();
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
			Log.e("sevibus", "Error al guardar la la cach�", e);
		}
		db.close();
	}

	private class TwitterAdapter extends BaseAdapter {

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

			fecha.setText(th.getFecha().toString());// TODO DateFormatter
			texto.setText(th.getTexto());
			return v;
		}

	}

}
