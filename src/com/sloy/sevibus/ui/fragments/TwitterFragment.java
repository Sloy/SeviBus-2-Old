package com.sloy.sevibus.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.common.collect.Lists;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.TweetHolder;
import com.sloy.sevibus.utils.Utils;

import twitter4j.TwitterException;

import java.util.Date;
import java.util.List;

public class TwitterFragment extends NewsFragment {

	private List<TweetHolder> mListTweets;
	private TwitterAdapter mAdapter;
	private SherlockFragmentActivity mCtx;
	private boolean running = false;

	private Handler handler = new Handler();
	private ListView list;
	private View empty;
	
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
				List<twitter4j.Status> stlist = Utils.getSevibusNews();
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
					// TODO creo que hay un problema al trabajar directamente
					// con esta lista
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
				Log.e("sevibus", "Error al descargar los tweets de @SeviBus", e);
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mCtx, "Error al descargar los tweets. Puede que Twitter esté saturado.", Toast.LENGTH_SHORT).show();
					}
				});
			}
			handler.post(postDownload);
		}
	};

	private Runnable postDownload = new Runnable() {
		@Override
		public void run() {
			empty.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
			mCtx.setSupportProgressBarIndeterminateVisibility(false);
			mAdapter.notifyDataSetChanged();
			running = false;
		}
	};
	
	public TwitterFragment(){};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_twitter, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mCtx = getSherlockActivity();
		
		// Carga los tweets guardados actualmente
		mListTweets = cargarCache();
		// Asigna el adapter al listview
		list = (ListView)getView().findViewById(android.R.id.list);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mListTweets.get(position).getUrl())));
			}
		});
		empty = getView().findViewById(android.R.id.empty);
		((Button)empty.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				actualizar();
			}
		});
		mAdapter = new TwitterAdapter();
		list.setAdapter(mAdapter);

		// ¿Mensaje de bienvenida?
		if(mListTweets.isEmpty()){
			list.setVisibility(View.GONE);
			empty.setVisibility(View.VISIBLE);
		}else{
			empty.setVisibility(View.GONE);
			// Carga los nuevos
			actualizar();
		}
	}

	private List<TweetHolder> cargarCache() {
		List<TweetHolder> res = Lists.newArrayList();
		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(mCtx, mCtx.getPackageName());
			// tareas de limpieza, por favor
			for(Entity e:db.getEntityList("tweetsSevibus", "date < "+(new Date().getTime()-604800000))){
				//elimina los tweets con más de 1 semana
//			for(Entity e:db.getEntityList("tweets", "date < "+(new Date().getTime()-86400000))){
				e.delete();
			}
			
			for(Entity e : db.getEntityList("tweetsSevibus", null, "date desc")){
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
			db.open(mCtx, mCtx.getPackageName());
			
			Entity e;
			for(TweetHolder th : tweets){
				e = new Entity("tweetsSevibus");
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

	@Override
	public void actualizar() {
		if(!running){
			if(Utils.isNetworkAvailable(mCtx)){
				mCtx.setSupportProgressBarIndeterminateVisibility(true);
				new Thread(downloadTweets).start();
			}else{
				Toast.makeText(mCtx, "Necesitas conexión a Internet", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void abrirNavegador() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/tussamsevilla")));
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
