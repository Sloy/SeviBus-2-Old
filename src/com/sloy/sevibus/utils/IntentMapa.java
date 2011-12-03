package com.sloy.sevibus.utils;

import android.content.Context;
import android.content.Intent;

import com.sloy.sevibus.ui.MapaActivity;

public class IntentMapa extends Intent {
	
	public IntentMapa(Context context){
		super(context,MapaActivity.class);
	}
	
	public IntentMapa setLinea(long lineaID){
		limpiarExtras();
		this.putExtra("linea", lineaID);
		return this;
	}
	
	public IntentMapa setParada(long l){
		limpiarExtras();
		this.putExtra("parada", l);
		return this;
	}
	
	private void limpiarExtras(){
		this.getExtras().clear();
	}
	

}
