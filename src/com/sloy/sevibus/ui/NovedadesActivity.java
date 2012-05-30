package com.sloy.sevibus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.flurry.android.FlurryAgent;
import com.sloy.sevibus.R;
import com.sloy.sevibus.ui.fragments.TussamFragment;
import com.sloy.sevibus.ui.fragments.TwitterFragment;
import com.sloy.sevibus.utils.Datos;
import com.viewpagerindicator.TabPageIndicator;

public class NovedadesActivity extends SherlockFragmentActivity {

	private NovedadesAdapter mAdapter;
	private ViewPager mViewPager;

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
			case android.R.id.home:
				startActivity(new Intent(this, HomeActivity.class));
				return true;
			default:
				return false;
		}
	}

	private class NovedadesAdapter extends FragmentPagerAdapter {
		
		private final String[] titles = new String[]{"@TussamSevilla", "@SeviBus"}; 
		
		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		public NovedadesAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position){
				case 0:
					return TussamFragment.instantiate(NovedadesActivity.this, "com.sloy.sevibus.ui.fragments.TussamFragment");
				case 1:
					return TwitterFragment.instantiate(NovedadesActivity.this, "com.sloy.sevibus.ui.fragments.TwitterFragment");

				default:
					return null;
			}
		}
	}

}
