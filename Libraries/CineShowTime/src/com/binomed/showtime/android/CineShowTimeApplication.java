/*
 * Copyright (C) 2011 Binomed (http://blog.binomed.fr)
 *
 * Licensed under the Eclipse Public License - v 1.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC 
 * LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM 
 * CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 */
package com.binomed.showtime.android;

import greendroid.app.GDApplication;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.content.Intent;
import android.os.Build;

import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.screen.main.CineShowTimeMainActivity;
import com.binomed.showtime.android.util.activity.TestSizeHoneyComb;
import com.binomed.showtime.android.util.activity.TestSizeOther;

@ReportsCrashes(formKey = CineShowtimeCst.ACRA_FORM_KEY)
public class CineShowTimeApplication extends GDApplication {

	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
	}

	@Override
	public Class<?> getHomeActivityClass() {
		return CineShowTimeMainActivity.class;
	}

	@Override
	public Intent getMainApplicationIntent() {
		Intent startIntent = new Intent(getApplicationContext(), CineShowTimeMainActivity.class);
		if (Integer.valueOf(Build.VERSION.SDK) <= 10) {
			startIntent.putExtra(ParamIntent.ACTIVITY_LARGE_SCREEN, TestSizeOther.checkLargeScreen(getResources().getConfiguration().screenLayout));

		} else {
			startIntent.putExtra(ParamIntent.ACTIVITY_LARGE_SCREEN, TestSizeHoneyComb.checkLargeScreen(getResources().getConfiguration().screenLayout));

		}
		return startIntent;
	}

}
