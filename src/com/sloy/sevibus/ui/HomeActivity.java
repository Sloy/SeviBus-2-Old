package com.sloy.sevibus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.sloy.sevibus.R;

public class HomeActivity extends FragmentActivity {

	private Context mContext = this;

	private Button mBtCercanas, mBtFavoritas, mBtLineas, mBtParadas, mBtMapa, mBtOpciones;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home_activity);

		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);

		// mBtCercanas = (ImageButton) findViewById(R.id.main_cercanas_button);
		mBtFavoritas = (Button)findViewById(R.id.main_favoritas_button);
		mBtLineas = (Button)findViewById(R.id.main_lineas_button);
		// mBtParadas = (ImageButton) findViewById(R.id.main_paradas_button);
		mBtMapa = (Button)findViewById(R.id.main_mapa_button);
		mBtOpciones = (Button)findViewById(R.id.main_acercade_button);

		/* Establece los listeners */
		/*
		 * mBtCercanas.setOnClickListener(new View.OnClickListener() {
		 * @Override
		 * public void onClick(View v) {
		 * }
		 * });
		 */
		mBtFavoritas.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, FavoritasActivity.class));
			}
		});
		mBtLineas.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, LineasActivity.class));

			}
		});
		/*
		 * mBtParadas.setOnClickListener(new View.OnClickListener() {
		 * @Override
		 * public void onClick(View v) {
		 * }
		 * });
		 */
		mBtMapa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, MapaActivity.class));
			}
		});
		mBtOpciones.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, AcercadeActivity.class));
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				startActivity(new Intent(mContext, AcercadeActivity.class));
				break;
			case R.id.menu_reportar:
				reportar();
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void reportar() {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_address)});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.email_text_general));
		startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent)));
	}

}
