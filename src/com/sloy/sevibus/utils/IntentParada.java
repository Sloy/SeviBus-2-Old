package com.sloy.sevibus.utils;

import android.content.Context;
import android.content.Intent;

import com.sloy.sevibus.ui.ParadaInfoActivity;

public class IntentParada extends Intent {
	
	public IntentParada(Context context, int paradaID){
		super(context, ParadaInfoActivity.class);
		this.putExtra("parada", paradaID);
	}
	
	public IntentParada setLinea(int lineaID){
		this.putExtra("linea", lineaID);
		return this;
	}

}
