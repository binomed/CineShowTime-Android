package com.binomed.showtime.android;

import greendroid.app.GDApplication;

import com.binomed.showtime.android.screen.main.SplashScreen;

public class CineShowTimeApplication extends GDApplication {

	@Override
	public Class<?> getHomeActivityClass() {
		return SplashScreen.class;
	}

}
