package com.sloy.sevibus.utils;

import android.content.Context;
import android.content.Intent;

import com.sloy.sevibus.ui.MapaActivity;

public class IntentMapa extends Intent {
	
	public IntentMapa(Context context){
		super(context,MapaActivity.class);
	}
	
	public IntentMapa setLinea(int lineaID){
		limpiarExtras();
		this.putExtra("linea", lineaID);
		return this;
	}
	
	public IntentMapa setParada(int paradaID){
		limpiarExtras();
		this.putExtra("parada", paradaID);
		return this;
	}
	
	private void limpiarExtras(){
		this.getExtras().clear();
	}
	

}
