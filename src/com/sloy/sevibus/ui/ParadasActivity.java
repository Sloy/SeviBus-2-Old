package com.sloy.sevibus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.flurry.android.FlurryAgent;
import com.google.common.collect.Lists;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.Datos;
import com.sloy.sevibus.utils.IntentMapa;
import com.sloy.sevibus.utils.IntentParada;
import com.sloy.sevibus.utils.ParadasAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParadasActivity extends SherlockActivity {

	private ListView mList;
	private ParadasAdapter mAdapter;
	private String mLinea;
	private long mLineaID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.onStartSession(this, Datos.FLURRY_KEY);
		setContentView(R.layout.list_activity);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// coge la línea que se le ha pasado
		mLineaID = getIntent().getLongExtra("linea", 0);
		mLinea = getIntent().getStringExtra("nombre");
		if(mLineaID == 0){
			// TODO comprobar otra cosa?
			Toast.makeText(this, "No se pasó nunguna línea", Toast.LENGTH_SHORT).show();
			finish();
		}

		setTitle("Paradas");
		getSupportActionBar().setSubtitle("De la línea " + mLinea);

		mList = (ListView)findViewById(android.R.id.list);
		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				startActivity(new IntentParada(ParadasActivity.this, mAdapter.getItem(pos).getId()).setLinea(mLineaID));
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
			List<Entity> rel = db.getEntityList("relaciones", "linea_id=" + mLineaID);
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

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.paradas, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.menu_reportar:
				reportar();
				return true;
			case R.id.menu_mapa:
				startActivity(new IntentMapa(this).setLinea(mLineaID));
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
