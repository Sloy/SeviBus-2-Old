package com.sloy.sevibus.utils;

import android.app.Application;

public class SeviApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Datos.initialize(this);
	}

}
