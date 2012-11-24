package com.sloy.sevibus.ui;

import android.content.Intent;
import android.os.Build;
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
            if (mDeviceInfo.isChecked()) {
                texto += "\n\n========== ";
                texto += "\nInformación del dispositivo";
                texto += "\n---------- ";
                texto += "\n- Release: " + android.os.Build.VERSION.RELEASE;
                texto += "\n- SDK: " + android.os.Build.VERSION.SDK_INT;
                texto += "\n- Codename: " + android.os.Build.VERSION.CODENAME;
                texto += "\n- Incremental: " + android.os.Build.VERSION.INCREMENTAL;
                texto += "\n- Brand: " + android.os.Build.BRAND;
                texto += "\n- Device: " + android.os.Build.DEVICE;
                texto += "\n- Display: " + android.os.Build.DISPLAY;
                if (Build.VERSION.SDK_INT >= 8) {
                    texto += "\n- Hardware: " + android.os.Build.HARDWARE;
                }
                texto += "\n- Manufacturer: " + android.os.Build.MANUFACTURER;
                texto += "\n- Model: " + android.os.Build.MODEL;
                texto += "\n- Product: " + android.os.Build.PRODUCT;
            }
            mTexto.setError(null);
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { getString(R.string.email_address) });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    getString(R.string.email_subject) + " - " + getResources().getStringArray(R.array.reporte_asuntos)[mAsunto.getSelectedItemPosition()]);
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, texto);
            startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent)));
        }

    }
}
