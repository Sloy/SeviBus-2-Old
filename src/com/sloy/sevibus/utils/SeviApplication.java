package com.sloy.sevibus.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.util.Log;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
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
		
		Map<Integer,String> favoritas = Utils.getFavoritas(getApplicationContext());

		// Comprueba si la tiene que actualizar
		if(lastVersion < currentVersion){
			// si la versión guardada es menor que esta versión
			// -> la actualiza

			DataFramework db = null;
			try{
				db = DataFramework.getInstance();
				db.open(this, getPackageName());

				// Cogemos los recientes
				List<Entity> recientes = null;
				if(lastVersion >= 24){ // Disponible desde 2.2
					  recientes = db.getEntityList("recientes");
				}
				
				db.close();
				// Sustituímos la base de datos
				Utils.copyDataBase(this);
				db = DataFramework.getInstance();
				db.open(this, getPackageName());

				// Guardamos los recientes
				if(lastVersion >= 24){ // Disponible desde 2.2
					for(Entity e : recientes){
						Entity n = new Entity("recientes");
						n.setValue("parada", e.getValue("parada"));
						n.save();
					}
				}
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
