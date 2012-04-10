package com.sloy.sevibus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Datos {

	public static final String FLURRY_KEY = "6NGELGNPTRH5NIR1XK78";
	private static SharedPreferences preferencias;
	private static Context mContext;

	public static final String DB_VERSION = "dbversion";

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

	public static int getAppVersion() {
		try{
			return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
		}catch(Exception e){
			Log.e("sevibus", "Error obteniendo la versión de la app :S", e);
			return 0;
		}
	}

	public interface OnDialogListener {
		public void onDialog();
	}
}
