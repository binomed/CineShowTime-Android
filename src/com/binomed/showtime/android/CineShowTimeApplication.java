package com.binomed.showtime.android;

import greendroid.app.GDApplication;
import android.content.Intent;
import android.content.res.Configuration;

import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.screen.main.CineShowTimeMainActivity;
import com.binomed.showtime.android.screen.main.SplashScreen;

public class CineShowTimeApplication extends GDApplication {

	@Override
	public Class<?> getHomeActivityClass() {
		return SplashScreen.class;
	}

	@Override
	public Intent getMainApplicationIntent() {
		Intent startIntent = new Intent(getApplicationContext(), CineShowTimeMainActivity.class);
		startIntent.putExtra(ParamIntent.ACTIVITY_LARGE_SCREEN, ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE //
				)
				|| ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE //
				));
		return startIntent;
	}

}
