package com.sloy.sevibus.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.flurry.android.FlurryAgent;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.Datos;

import java.util.List;

public class LineasActivity extends SherlockActivity  {

	private ListView mList;
	private LineasAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.onStartSession(this, Datos.FLURRY_KEY);
		setContentView(R.layout.list_activity);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		setTitle("Líneas");
		mList = (ListView)findViewById(android.R.id.list);
		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				Intent i = new Intent(LineasActivity.this,ParadasActivity.class);
				i.putExtra("linea", mAdapter.getItemId(pos));
				i.putExtra("nombre", mAdapter.getItem(pos).getString("nombre"));
				startActivity(i);
			}
		});
		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			mAdapter = new LineasAdapter(this, db.getEntityList("lineas",null,"nombre"));
		}catch(Exception e){
			Log.e("sevibus",e.toString(),e);
		}finally{
			db.close();
		}
		
		if(mAdapter!=null){
			mList.setAdapter(mAdapter);
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	private class LineasAdapter extends BaseAdapter {

		List<Entity> mItems;
		private Context mContext;

		public LineasAdapter(Context context, List<Entity> items) {
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
				convertView = View.inflate(mContext, R.layout.list_item_linea, null);
			}
			TextView nombre = (TextView)convertView.findViewById(R.id.item_linea_nombre);
			TextView trayecto = (TextView)convertView.findViewById(R.id.item_linea_trayecto);
			nombre.setText(item.getString("nombre"));
			trayecto.setText(item.getString("trayecto"));
			return convertView;
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.lineas, menu);
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
	
	private void reportar(){
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_address)});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.email_text_lineas));
		startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent)));
	}

}
