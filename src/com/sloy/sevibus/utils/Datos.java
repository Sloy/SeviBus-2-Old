package com.sloy.sevibus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Datos {

	private static SharedPreferences preferencias;
	private static Context mContext;
	
	public static final String packageName = "com.sloy.sevibus2"; //TODO final

	public static void initialize(Context ctx) {
		mContext = ctx;
		preferencias = PreferenceManager.getDefaultSharedPreferences(mContext);
	}
	
	public static Context getAppContext(){
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
}
