package com.sloy.sevibus.utils;

import android.app.Application;
import android.util.Log;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

public class SeviApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Datos.initialize(this);

		/* DB stuff */

		// Si no hay base de datos, la crea
		try{
			Utils.createDataBase(this);
		}catch(IOException e1){
			Log.e("sevibus", "Error creando la base de datos", e1);
		}

		// Comprueba si la tiene que actualizar
		if(true | Datos.getPrefs().getInt(Datos.DB_VERSION, Integer.MAX_VALUE) < Datos.getAppVersion()){
			// si la versión guardada es menor que esta versión
			// -> la actualiza

			// Cogemos los favoritos
			List<FavoritosHelper> favoritos = Lists.newArrayList();
			DataFramework db = null;
			try{
				db = DataFramework.getInstance();
				db.open(this, getPackageName());
				// antes de la versión 13 las favoritas tenían menos columnas, cuidado
				if(Datos.getAppVersion()<=13){
					db.getDB().execSQL("ALTER TABLE favoritas ADD linea_id INTEGER");
					db.getDB().execSQL("ALTER TABLE favoritas ADD orden INTEGER");
					db.getDB().execSQL("ALTER TABLE favoritas ADD usada INTEGER");
				}
				for(Entity e : db.getEntityList("favoritas")){
					FavoritosHelper f = new FavoritosHelper(e.getInt("parada_id"));
					String descripcion = e.getString("descripcion");
					if(descripcion != null){
						f.setDescripcion(descripcion);
					}
					Integer linea = e.getInt("linea_id");
					if(linea != null){
						f.setLineaID(linea);
					}
					Integer orden = e.getInt("orden");
					if(orden != null){
						f.setOrden(orden);
					}
					Integer usada = e.getInt("usada");
					if(usada != null){
						f.setUsada(usada);
					}
					favoritos.add(f);
				}
				db.close();
				// Sustituímos la base de datos
				Utils.copyDataBase(this);
				db = DataFramework.getInstance();
				db.open(this, getPackageName());
				
				// Guardamos los favoritos en la nueva base de datos
				for(FavoritosHelper fav:favoritos){
					Entity f = new Entity("favoritas");
					f.setValue("parada_id", fav.getParadaID());
					f.setValue("linea_id", fav.getLineaID());
					f.setValue("descripcion", fav.getDescripcion());
					f.setValue("orden", fav.getOrden());
					f.setValue("usada", fav.getUsada());
					f.save();
				}

				// Actualizamos la versión
				Datos.getPrefs().edit().putInt(Datos.DB_VERSION, Datos.getAppVersion()).commit();

			}catch(Exception e){
				Log.e("sevibus", "Error actualizando la base de datos", e);
			}finally{
				db.close();
			}
		}
	}

}
