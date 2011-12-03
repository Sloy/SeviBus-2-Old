package com.sloy.sevibus.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.IntentMapa;

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
				startActivity(new IntentMapa(mContext));
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

	private void donar() {
		new AlertDialog.Builder(this)
				.setTitle("Inv�tame a un caf�")
				.setMessage(
						"Este bot�n es para donar (dinero). Pulsando donar te mandar� a una p�gian de PayPal a trav�s de la cual puedes donar la cantidad que quieras. \n\n�Por qu�? Porque hacer esta aplicaci�n requiere mucho trabajo, porque soy un �nico desarrollador trabajando en ella, estudiante, desempleado, que no cobra por la aplicaci�n que puede facilitar a muchos el d�a a d�a. \n\nNo tienes que hacerlo si no quieres, la aplicaci�n sigue siendo gratuita y por donar no se obtiene ninguna funci�n extra. Pero si quieres apoyar el desarrollo y mejora de la aplicaci�n, o simplemente quieres agradecerme mi trabajo, aqu� tienes una buena oportunidad. \n\nGracias.")
				.setPositiveButton("Donar", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri
								.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=TA2XH2L4B7MAW&lc=ES&item_name=SeviBus&item_number=sevibus&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted")));
					}
				}).setNegativeButton("No quiero", null).create().show();
	}
}
