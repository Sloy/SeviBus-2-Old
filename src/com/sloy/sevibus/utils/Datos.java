package com.sloy.sevibus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Datos {

	public static final String FLURRY_KEY = "6NGELGNPTRH5NIR1XK78";
	private static SharedPreferences preferencias;

	public static final String DB_VERSION = "dbversion";

	public static final String packageName = "com.sloy.sevibus2"; // TODO final

	public static SharedPreferences getPrefs(Context context) {
		if(preferencias==null){
			preferencias = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return preferencias;
	}


	public static int getAppVersion(Context context) {
		try{
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		}catch(Exception e){
			Log.e("sevibus", "Error obteniendo la versión de la app :S", e);
			return 0;
		}
	}
}
