package com.sloy.sevibus.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.flurry.android.FlurryAgent;
import com.jakewharton.activitycompat2.ActivityCompat2;
import com.jakewharton.activitycompat2.ActivityOptionsCompat2;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.Datos;
import com.sloy.sevibus.utils.IntentMapa;

public class HomeActivity extends SherlockActivity {

	private Activity mContext = this;
	private Button mBtFavoritas, mBtLineas, mBtParadas, mBtMapa, mBtAcerca, mBtNovedades;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.onStartSession(this, Datos.FLURRY_KEY);
		setContentView(R.layout.home_activity);

		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mBtFavoritas = (Button) findViewById(R.id.main_favoritas_button);
		mBtLineas = (Button) findViewById(R.id.main_lineas_button);
		mBtParadas = (Button) findViewById(R.id.main_paradas_button);
		mBtMapa = (Button) findViewById(R.id.main_mapa_button);
		mBtAcerca = (Button) findViewById(R.id.main_acerca_button);
		mBtNovedades = (Button) findViewById(R.id.main_novedades_button);

		/* Establece los listeners */
		mBtFavoritas.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, FavoritasActivity.class);
				ActivityOptionsCompat2 options = ActivityOptionsCompat2.makeScaleUpAnimation(mBtFavoritas, mBtFavoritas.getWidth() / 2,
						mBtFavoritas.getHeight() / 2, mBtFavoritas.getWidth(), mBtFavoritas.getHeight());
				ActivityCompat2.startActivity(mContext, intent, options.toBundle());
			}
		});
		mBtLineas.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, LineasActivity.class);
				ActivityOptionsCompat2 options = ActivityOptionsCompat2.makeScaleUpAnimation(mBtLineas, mBtLineas.getWidth() / 2,
						mBtLineas.getHeight() / 2, mBtLineas.getWidth(),
						mBtLineas.getHeight());
				ActivityCompat2.startActivity(mContext, intent, options.toBundle());
			}
		});

		mBtParadas.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ParadasBusquedaActivity.class);
				ActivityOptionsCompat2 options = ActivityOptionsCompat2.makeScaleUpAnimation(mBtParadas, mBtParadas.getWidth() / 2,
						mBtParadas.getHeight() / 2, mBtParadas.getWidth(),
						mBtParadas.getHeight());
				ActivityCompat2.startActivity(mContext, intent, options.toBundle());
			}
		});

		mBtMapa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new IntentMapa(mContext);
				ActivityOptionsCompat2 options = ActivityOptionsCompat2.makeScaleUpAnimation(mBtMapa, mBtMapa.getWidth() / 2,
						mBtMapa.getHeight() / 2, mBtMapa.getWidth(), mBtMapa.getHeight());
				ActivityCompat2.startActivity(mContext, intent, options.toBundle());
			}
		});
		mBtNovedades.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, NovedadesActivity.class);
				ActivityOptionsCompat2 options = ActivityOptionsCompat2.makeScaleUpAnimation(mBtNovedades, mBtNovedades.getWidth() / 2,
						mBtNovedades.getHeight() / 2, mBtNovedades.getWidth(),
						mBtNovedades.getHeight());
				ActivityCompat2.startActivity(mContext, intent, options.toBundle());
			}
		});
		mBtAcerca.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AcercadeActivity.class);
				ActivityOptionsCompat2 options = ActivityOptionsCompat2.makeScaleUpAnimation(mBtAcerca, mBtAcerca.getWidth() / 2,
						mBtAcerca.getHeight() / 2, mBtAcerca.getWidth(),
						mBtAcerca.getHeight());
				ActivityCompat2.startActivity(mContext, intent, options.toBundle());
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(mContext, AcercadeActivity.class));
			break;
		case R.id.menu_reportar:
			reportar();
			break;
		case R.id.menu_donar:
			donar();
			break;
		default:
			return false;
		}
		return true;
	}

	private void reportar() {
		startActivity(new Intent(this, ReporteActivity.class));
	}

	private void donar() {
		new AlertDialog.Builder(this)
				.setTitle("Invítame a un café")
				.setMessage(
						"Este botón es para donar (dinero). Pulsando donar te mandará a una página de PayPal a través de la cual puedes donar la cantidad que quieras. La aplicación es completamente gratuita por lo que la donación es opcional.")
				.setPositiveButton("Donar", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri
								.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=TA2XH2L4B7MAW&lc=ES&item_name=SeviBus&item_number=sevibus&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted")));
					}
				}).setNegativeButton("Cancelar", null).create().show();
	}
}
