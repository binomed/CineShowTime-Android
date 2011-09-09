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
package com.binomed.showtime.android.widget;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsActivity;
import com.binomed.showtime.android.screen.results.tablet.CineShowTimeResultsTabletActivity;
import com.binomed.showtime.android.util.activity.TestSizeHoneyComb;
import com.binomed.showtime.android.util.activity.TestSizeOther;

public class CineShowTimeWidgetServiceOpenResults extends Service {

	private static final String TAG = "ServiceOpenResults"; //$NON-NLS-1$

	private CineShowtimeDbAdapter mDbHelper;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public ComponentName startService(Intent service) {
		return super.startService(service);
	}

	@Override
	public boolean stopService(Intent name) {
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		mDbHelper = new CineShowtimeDbAdapter(this);
		try {
			try {

				mDbHelper.open();
			} catch (SQLException e) {
				Log.e(TAG, "error opening database", e);
			}
			Class<?> clazz = CineShowTimeResultsActivity.class;

			boolean largeScreen = false;
			if (Integer.valueOf(Build.VERSION.SDK) <= 10) {
				largeScreen = TestSizeOther.checkLargeScreen(getResources().getConfiguration().screenLayout);

			} else {
				largeScreen = TestSizeHoneyComb.checkLargeScreen(getResources().getConfiguration().screenLayout);

			}

			if (largeScreen) {
				clazz = CineShowTimeResultsTabletActivity.class;
			}

			Intent intentStartResultActivity = new Intent(this, clazz);
			intentStartResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, intent.getStringExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID));
			intentStartResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, intent.getStringExtra(ParamIntent.ACTIVITY_SEARCH_CITY));
			double latitude = intent.getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, -1);
			double longitude = intent.getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, -1);
			if (latitude != -1) {
				intentStartResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, latitude);
			}
			if (longitude != -1) {
				intentStartResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, longitude);
			}

			intentStartResultActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

			this.startActivity(intentStartResultActivity);
		} catch (Exception e) {
			Log.e(TAG, "error launching activity movie", e);
		} finally {
			if ((mDbHelper != null) && mDbHelper.isOpen()) {
				mDbHelper.close();
			}
		}
		stopSelf();
	}
}
