package com.sloy.sevibus.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.flurry.android.FlurryAgent;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.Datos;
import com.sloy.sevibus.utils.IntentMapa;

public class HomeActivity extends SherlockActivity  {

	private Context mContext = this;
	private SharedPreferences prefs;
	private Button mBtCercanas, mBtFavoritas, mBtLineas, mBtParadas, mBtMapa, mBtAcerca, mBtNovedades,mBtNotificacionAbrir;
	private ImageButton mBtNotificacionCerrar;
	private View mNotificacion;
	private Intent mNotificationAction;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.onStartSession(this, Datos.FLURRY_KEY);
		setContentView(R.layout.home_activity);

		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		prefs = Datos.getPrefs();

		// mBtCercanas = (ImageButton) findViewById(R.id.main_cercanas_button);
		mBtFavoritas = (Button)findViewById(R.id.main_favoritas_button);
		mBtLineas = (Button)findViewById(R.id.main_lineas_button);
		mBtParadas = (Button)findViewById(R.id.main_paradas_button);
		mBtMapa = (Button)findViewById(R.id.main_mapa_button);
		mBtAcerca = (Button)findViewById(R.id.main_acerca_button);
		mBtNovedades = (Button)findViewById(R.id.main_novedades_button);
		
		mNotificacion = findViewById(R.id.home_notification);
		mBtNotificacionAbrir = (Button)findViewById(R.id.notification_text);
		mBtNotificacionCerrar = (ImageButton)findViewById(R.id.notification_dismiss);

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

		mBtParadas.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, ParadasBusquedaActivity.class));
			}
		});

		mBtMapa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new IntentMapa(mContext));
			}
		});
		mBtNovedades.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, NovedadesActivity.class));
			}
		});
		mBtAcerca.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, AcercadeActivity.class));
			}
		});
		
		mBtNotificacionAbrir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openNotification();
			}
		});
		mBtNotificacionCerrar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissNotification();
			}
		});
	
		
		// Muestra notificacion de twitter
		if(prefs.getBoolean("twitter", true)){
			Intent i = new Intent(this, NovedadesActivity.class);
			i.putExtra("default", "twitter");
			showNotification("¡@SeviBus ahora está en Twitter! ¿No lo has visto? Pulsa aquí.", i);
			prefs.edit().putBoolean("twitter", false).commit();
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
				break;
			case R.id.menu_donar:
				donar();
				break;
			default:
				break;
		}
		return true;
	}

	private void reportar() {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_address)});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.email_text_general));
		startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent)));
	}
	
	private void showNotification(String text, Intent action){
		mNotificacion.setVisibility(View.VISIBLE);
		mNotificationAction=action;
		mBtNotificacionAbrir.setText(text);
	}
	
	private void openNotification(){
		if(mNotificationAction!=null){
			startActivity(mNotificationAction);
		}
		dismissNotification();
	}
	
	private void dismissNotification(){
		mNotificacion.setVisibility(View.GONE);
	}

	private void donar() {
		new AlertDialog.Builder(this)
				.setTitle("Invítame a un café")
				.setMessage(
						"Este botón es para donar (dinero). Pulsando donar te mandará a una página de PayPal a través de la cual puedes donar la cantidad que quieras. \n\n¿Por qué? Porque hacer esta aplicación requiere mucho trabajo, porque soy un único desarrollador trabajando en ella, estudiante, desempleado, que no cobra por la aplicación que puede facilitar a muchos el día a día. \n\nNo tienes que hacerlo si no quieres, la aplicación sigue siendo gratuita y por donar no se obtiene ninguna función extra. Pero si quieres apoyar el desarrollo y mejora de la aplicación, o simplemente quieres agradecerme mi trabajo, aquí tienes una buena oportunidad. \n\nGracias.")
				.setPositiveButton("Donar", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri
								.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=TA2XH2L4B7MAW&lc=ES&item_name=SeviBus&item_number=sevibus&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted")));
					}
				}).setNegativeButton("No quiero", null).create().show();
	}
}
