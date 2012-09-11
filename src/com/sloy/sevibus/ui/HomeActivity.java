package com.sloy.sevibus.ui;

import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

	private MenuItem mDonarItem;

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
		mDonarItem = menu.findItem(R.id.menu_donar);
		new Navidad().execute();
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

	private class Navidad extends AsyncTask<Void, Void, Integer> {
		// 0,1,2,3
		@Override
		protected Integer doInBackground(Void... params) {
			int res = 0;
			Random rand = new Random();
			int seed = rand.nextInt(8);
			if (seed == 0) {
				res += 1;
			}
			Calendar rightNow = Calendar.getInstance();
			if (rightNow.get(Calendar.MONTH) == Calendar.SEPTEMBER && rightNow.get(Calendar.DAY_OF_MONTH) == 25) {
				res += 2;
			}
			return res;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1 || result == 3) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				ImageView iv = (ImageView) inflater.inflate(R.layout.donar_action_view, null);
				Animation rotation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.destacar_donar);
				rotation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						mDonarItem.setActionView(null);
					}
				});
				iv.startAnimation(rotation);
				mDonarItem.setActionView(iv);
			}
			if (result > 1) {
				new AlertDialog.Builder(HomeActivity.this)
						.setTitle("¡Feliz cumpleaños, SeviBus!")
						.setMessage(
								"Hoy mismo se cumplen 2 años desde que la primera versión de SeviBus apareció en el Android Market. Ha sido mucho trabajo, muchas actualizaciones (29) y muchos usuarios contentos. Es una buena oportunidad para donar y apoyar la continuidad del proyecto si aún no lo has hecho. \n\nGracias por usar SeviBus :)")
						.setPositiveButton("Donar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri
										.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=TA2XH2L4B7MAW&lc=ES&item_name=SeviBus&item_number=sevibus&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted")));
							}
						}).setNegativeButton("Meh, paso", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Toast.makeText(HomeActivity.this, ":'(", Toast.LENGTH_LONG).show();
								dialog.dismiss();
							}
						})
						.create().show();
			}
			super.onPostExecute(result);
		}

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
