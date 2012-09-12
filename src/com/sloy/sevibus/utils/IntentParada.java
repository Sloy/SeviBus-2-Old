package com.sloy.sevibus.utils;

import android.content.Context;
import android.content.Intent;

import com.sloy.sevibus.ui.ParadaInfoActivity;

public class IntentParada extends Intent {
	
	public IntentParada(Context context, long id){
		super(context, ParadaInfoActivity.class);
		this.putExtra("parada", id);
	}
	
	public IntentParada setLinea(long linea){
		this.putExtra("linea", linea);
		return this;
	}

}
