package com.sloy.sevibus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.ExpandAnimation;

import java.lang.reflect.Method;
import java.util.Calendar;

public class AcercadeActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acercade);
		setTitle("Acerca de SeviBus");
		
		Calendar rightNow = Calendar.getInstance();
		if(rightNow.get(Calendar.MONTH)==Calendar.FEBRUARY && rightNow.get(Calendar.DAY_OF_MONTH) == 14){
			//luv mode
			((TextView)findViewById(R.id.dedicatoria)).setText(R.string.dedicatoria_luv);
		}
		((Button)findViewById(R.id.acercade_novedades_contenido_boton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FrameLayout novedadesLayout = (FrameLayout)findViewById(R.id.acercade_novedades_contenido_frame);
				TextView textNovedades = (TextView)findViewById(R.id.acercade_novedades_contenido_text);
				int collapsedHeight = getResources().getDimensionPixelSize(R.dimen.collapsed_text_height);
				expandOrCollapse(novedadesLayout, textNovedades, collapsedHeight);
			}
		});
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

	private static void expandOrCollapse(View view2Expand, View view2Measure, int collapsedHeight) {
		if(view2Expand.getHeight() < collapsedHeight){
			return;
		}
		int measuredHeight = measureViewHeight(view2Expand, view2Measure);
		if(measuredHeight < collapsedHeight){
			measuredHeight = collapsedHeight;
		}

		final int startHeight = view2Expand.getHeight();
		final int finishHeight = startHeight <= collapsedHeight ? measuredHeight : collapsedHeight;

		view2Expand.startAnimation(new ExpandAnimation(view2Expand, startHeight, finishHeight));
	}

}
