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
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.Utils;

import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;

public class NovedadesActivity extends SherlockActivity {

	private List<Status> mListTweets;
	private TwitterAdapter mAdapter;
	private Context mCtx;

	AsyncTask<Void, Void, List<Status>> downloadTweets = new AsyncTask<Void, Void, List<Status>>() {

		@Override
		protected List<twitter4j.Status> doInBackground(Void... params) {
			try{
				return Utils.getTussamNews();
			}catch(TwitterException e){
				Log.e("sevibus", "Error al descargar los tweets de @TussamSevilla", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<twitter4j.Status> result) {
			if(result == null){
				Toast.makeText(mCtx, "Error al descargar los tweets", Toast.LENGTH_SHORT).show();
			}else{
				// Descarga correcta, actualiza la lista
				mListTweets = result;
				mAdapter.notifyDataSetChanged();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novedades);
		mCtx = this;
		ListView list = (ListView)findViewById(android.R.id.list);
		mAdapter = new TwitterAdapter();
		list.setAdapter(mAdapter);

		downloadTweets.execute();
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
		public Status getItem(int position) {
			return mListTweets.get(position);
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Status s = getItem(position);
			View v = convertView;
			if(v == null){
				v = LayoutInflater.from(mCtx).inflate(R.layout.item_list_tweet, parent, false);
			}
			TextView fecha = (TextView)v.findViewById(R.id.item_novedades_twitter_fecha);
			TextView texto = (TextView)v.findViewById(R.id.item_novedades_twitter_texto);

			fecha.setText(s.getCreatedAt().toString());
			texto.setText(Utils.limpiarTweet(s));
			return v;
		}

	}

}
