package com.sloy.sevibus.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.sloy.sevibus.R;

public class Datos {

	private static SharedPreferences preferencias;
	private static Context mContext;

	public static final String packageName = "com.sloy.sevibus2"; // TODO final

	public static void initialize(Context ctx) {
		mContext = ctx;
		preferencias = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public static Context getAppContext() {
		return mContext;
	}

	public static SharedPreferences getPrefs() {
		return preferencias;
	}

	public static Integer getRadio() {
		return getPrefs().getInt("radio", 500);
	}

	public static Integer getLimiteBusqueda() {
		return getPrefs().getInt("limiteBusqueda", 50);
	}

	/*
	 * public static void setCiudad(String codigo){
	 * getPrefs().edit().putString("ciudad", codigo).commit();
	 * }
	 */

	public static String getCiudad() {
		// return getPrefs().getString("ciudad", null);
		return "se";
	}

	public static AlertDialog createAlertDialog(final Context context, final Entity parada, final OnDialogListener listener) {
		LayoutInflater factory = LayoutInflater.from(context);
		final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);

		AlertDialog.Builder constructor = new AlertDialog.Builder(context);

		return constructor
		// .setIcon(R.drawable.alert_dialog_icon)
				.setTitle("Guardar favorito").setView(textEntryView).setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						DataFramework db = null;
						try{
							db = DataFramework.getInstance();
							db.open(context, context.getPackageName());
							Entity f = db.getTopEntity("favoritas", "parada_id=" + parada.getId(), null);
							if(f == null){
								f = new Entity("favoritas");
								f.setValue("parada_id", parada.getId());
							}
							Dialog curDialog = (Dialog)dialog;
							String nombre = ((EditText)curDialog.findViewById(R.id.txtNombreFavorito)).getText().toString();
							f.setValue("descripcion", nombre);
							f.save();
							if(listener!=null){
								listener.onDialog();
							}else{
								Toast.makeText(context, "Guardada", Toast.LENGTH_SHORT).show();
							}
						}catch(Exception e){
							Log.e("sevibus", e.toString(), e);
							Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
						}finally{
							db.close();
						}
					}
				}).setNegativeButton("Descartar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				}).create();
	}
	
	public interface OnDialogListener{
		public void onDialog();
	}
}

