package com.sloy.sevibus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.widget.TextView;

import com.sloy.sevibus.R;

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

}
