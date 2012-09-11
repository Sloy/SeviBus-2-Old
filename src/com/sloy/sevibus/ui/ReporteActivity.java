package com.sloy.sevibus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.flurry.android.FlurryAgent;
import com.sloy.sevibus.R;
import com.sloy.sevibus.utils.Datos;

public class ReporteActivity extends SherlockActivity {

	private Spinner mAsunto;
	private EditText mTexto;
	private CheckBox mDeviceInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.onStartSession(this, Datos.FLURRY_KEY);
		setContentView(R.layout.activity_reporte);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Enviar reporte");

		mAsunto = (Spinner) findViewById(R.id.reporte_asunto);
		mTexto = (EditText) findViewById(R.id.reporte_texto);
		mDeviceInfo = (CheckBox) findViewById(R.id.reporte_device_info);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.reporte, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_enviar:
			enviar();
			return true;
		case android.R.id.home:
			startActivity(new Intent(this, HomeActivity.class));
			return true;
		default:
			return false;
		}
	}

	private void enviar() {
		// Validar
		String texto = mTexto.getText().toString();
		if (TextUtils.isEmpty(texto)) {
			mTexto.setError("Un reporte vacío no sirve de nada");
		} else {
			// Información extra?
			if(mDeviceInfo.isChecked()){
				texto+= "\n\n----------: ";
				texto+= "\nBrand: "+android.os.Build.BRAND;
				texto+= "\nDevice: "+android.os.Build.DEVICE;
				texto+= "\nDisplay: "+android.os.Build.DISPLAY;
				texto+= "\nHardware: "+android.os.Build.HARDWARE;
				texto+= "\nManufacturer: "+android.os.Build.MANUFACTURER;
				texto+= "\nModel: "+android.os.Build.MODEL;
				texto+= "\nProduct: "+android.os.Build.PRODUCT;
			}
			mTexto.setError(null);
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { getString(R.string.email_address) });
			emailIntent.putExtra(
					android.content.Intent.EXTRA_SUBJECT,
					getString(R.string.email_subject) + " - "
							+ getResources().getStringArray(R.array.reporte_asuntos)[mAsunto.getSelectedItemPosition()]);
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, texto);
			startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent)));
		}

	}
}
