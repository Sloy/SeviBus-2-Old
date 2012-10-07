package com.sloy.sevibus.utils;

import java.io.IOException;
import java.util.Map;

import android.app.Application;
import android.util.Log;

import com.android.dataframework.DataFramework;
import com.flurry.android.FlurryAgent;
import com.sloy.sevibus.R;

public class SeviApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		int lastVersion = Datos.getPrefs(this).getInt(Datos.DB_VERSION, 0);
		int currentVersion = Datos.getAppVersion(this);
		/* DB stuff */

		// Si no hay base de datos, la crea
		try{
			Utils.createDataBase(this);
		}catch(IOException e1){
			Log.e("sevibus", "Error creando la base de datos", e1);
		}
		
		

		// Comprueba si la tiene que actualizar
		if(lastVersion < currentVersion){
			// si la versión guardada es menor que esta versión
			// -> la actualiza
			
			// Obtiene las favoritas
			Map<Integer,String> favoritas = Utils.getFavoritas(getApplicationContext());

			DataFramework db = null;
			try{
				db = DataFramework.getInstance();
				db.open(this, getPackageName());

				// Sustituímos la base de datos
				Utils.copyDataBase(this);
				db = DataFramework.getInstance();
				db.open(this, getPackageName());

				
				// Guardamos los favoritos en la nueva base de datos
				Utils.saveFavoritas(getApplicationContext(), favoritas);
				
				// Actualizamos la versión
				Datos.getPrefs(this).edit().putInt(Datos.DB_VERSION, Datos.getAppVersion(this)).commit();

			}catch(Exception e){
				Log.e("sevibus", "Error actualizando la base de datos", e);
			}finally{
				db.close();
			}
		}
		
		/* Flurry config */
		FlurryAgent.setVersionName(getResources().getString(R.string.version));
		FlurryAgent.setUseHttps(true);
		FlurryAgent.setReportLocation(false);
		FlurryAgent.setLogLevel(0);

	}
}
