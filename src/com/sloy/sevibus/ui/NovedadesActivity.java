package com.sloy.sevibus.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.flurry.android.FlurryAgent;
import com.sloy.sevibus.R;
import com.sloy.sevibus.ui.fragments.NewsFragment;
import com.sloy.sevibus.ui.fragments.TussamFragment;
import com.sloy.sevibus.ui.fragments.TwitterFragment;
import com.sloy.sevibus.utils.Datos;
import com.viewpagerindicator.TabPageIndicator;

public class NovedadesActivity extends SherlockFragmentActivity {

	private NovedadesAdapter mAdapter;
	private ViewPager mViewPager;
	private Boolean[] mLoaders = new Boolean[]{false,false};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.onStartSession(this, Datos.FLURRY_KEY);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_novedades);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Novedades");
		setSupportProgressBarIndeterminateVisibility(false);

		mViewPager = (ViewPager)findViewById(R.id.viewpager);
		mAdapter = new NovedadesAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);

		TabPageIndicator tabIndicator = (TabPageIndicator)findViewById(R.id.titles);
		tabIndicator.setViewPager(mViewPager);
		
		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			String def = extras.getString("default");
			if(def!=null && def.equals("twitter")){
				mViewPager.setCurrentItem(1);
			}
		}
		
		SharedPreferences prefs = Datos.getPrefs();
		if(prefs.getBoolean("novedadesPrimeraVez", true)){
			Toast.makeText(this, "Puedes abrir los perfiles de Twitter desde el botón de arriba", Toast.LENGTH_LONG).show();
			prefs.edit().putBoolean("novedadesPrimeraVez", false).commit();
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
		inflater.inflate(R.menu.novedades, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.menu_actualizar:
				actualizarTodo();
				return true;
			case R.id.menu_navegador_tussam:
				abrirNavegador("tussamsevilla");
				return true;
			case R.id.menu_navegador_twitter:
				abrirNavegador("SeviBus");
				return true;
			case android.R.id.home:
				startActivity(new Intent(this, HomeActivity.class));
				return true;
			default:
					return false;
		}
	}

	private void abrirNavegador(String string) {
		startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://twitter.com/"+string)));
	}

	private void actualizarTodo() {
		mAdapter.getItem(0).actualizar(false);
		mAdapter.getItem(1).actualizar(false);

	}
	
	public void comenzarCarga(int i){
		Log.d("sevibus", "Comienza carga "+i);
		mLoaders[i] = true;
		actualizarLoaders();
	}
	
	public void detenerCarga(int i){
		Log.d("sevibus", "Finaliza carga "+i);
		mLoaders[i] = false;
		actualizarLoaders();
	}
	
	private void actualizarLoaders(){
		setSupportProgressBarIndeterminateVisibility(mLoaders[0] || mLoaders[1]);
	}

	private class NovedadesAdapter extends FragmentPagerAdapter {

		private final String[] titles = new String[]{"@TussamSevilla", "@SeviBus"};
		private NewsFragment[] fragments = new NewsFragment[2];

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		public NovedadesAdapter(FragmentManager fm) {
			super(fm);
			fragments[0] = (NewsFragment)TussamFragment.instantiate(NovedadesActivity.this, "com.sloy.sevibus.ui.fragments.TussamFragment");
			fragments[1] = (NewsFragment)TwitterFragment.instantiate(NovedadesActivity.this, "com.sloy.sevibus.ui.fragments.TwitterFragment");
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public NewsFragment getItem(int position) {
			return fragments[position];
		}
	}

}
