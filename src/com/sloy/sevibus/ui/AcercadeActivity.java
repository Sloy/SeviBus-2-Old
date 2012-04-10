package com.sloy.sevibus.ui;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.flurry.android.FlurryAgent;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.Datos;
import com.sloy.sevibus.utils.ExpandAnimation;

import java.lang.reflect.Method;

public class AcercadeActivity extends SherlockActivity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.onStartSession(this, Datos.FLURRY_KEY);

		setContentView(R.layout.activity_acercade);
		setTitle("Acerca de SeviBus");

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final ImageButton button = ((ImageButton)findViewById(R.id.acercade_novedades_contenido_boton));
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FrameLayout novedadesLayout = (FrameLayout)findViewById(R.id.acercade_novedades_contenido_frame);
				TextView textNovedades = (TextView)findViewById(R.id.acercade_novedades_contenido_text);
				int collapsedHeight = getResources().getDimensionPixelSize(R.dimen.collapsed_text_height);
				expandOrCollapse(novedadesLayout, textNovedades, collapsedHeight, button);
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
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				startActivity(new Intent(this, HomeActivity.class));
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private static int measureViewHeight(View view2Expand, View view2Measure) {
		try{
			Method m = view2Measure.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
			m.setAccessible(true);
			m.invoke(view2Measure, MeasureSpec.makeMeasureSpec(view2Expand.getWidth(), MeasureSpec.AT_MOST),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		}catch(Exception e){
			return -1;
		}
		int measuredHeight = view2Measure.getMeasuredHeight();
		return measuredHeight;
	}

	private static void expandOrCollapse(View view2Expand, View view2Measure, int collapsedHeight, ImageButton indicator) {
		if(view2Expand.getHeight() < collapsedHeight){
			return;
		}
		int measuredHeight = measureViewHeight(view2Expand, view2Measure);
		if(measuredHeight < collapsedHeight){
			measuredHeight = collapsedHeight;
		}

		final int startHeight = view2Expand.getHeight();
		final int finishHeight = startHeight <= collapsedHeight ? measuredHeight : collapsedHeight;

		if(startHeight > finishHeight){
			// collapse
			indicator.setImageResource(R.drawable.expander_open_holo_light);
		}else{
			// expand
			indicator.setImageResource(R.drawable.expander_close_holo_light);
		}

		view2Expand.startAnimation(new ExpandAnimation(view2Expand, startHeight, finishHeight));
	}

}
