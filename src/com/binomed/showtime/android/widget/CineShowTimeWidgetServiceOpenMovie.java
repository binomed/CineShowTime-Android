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
import android.os.IBinder;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.movie.CineShowTimeMovieActivity;

public class CineShowTimeWidgetServiceOpenMovie extends Service {

	private static final String TAG = "ServiceOpenMovie"; //$NON-NLS-1$

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
			Intent intentStartMovieActivity = new Intent(this, CineShowTimeMovieActivity.class);
			MovieBean movie = intent.getParcelableExtra(ParamIntent.MOVIE);
			TheaterBean theaterBean = intent.getParcelableExtra(ParamIntent.THEATER);
			String near = intent.getStringExtra(ParamIntent.ACTIVITY_MOVIE_NEAR);
			int widgetId = intent.getIntExtra(ParamIntent.WIDGET_ID, 0);
			// Object[] currentMovie = CineShowtimeDB2AndShowtimeBeans.extractCurrentWidgetMovie(mDbHelper);
			// TheaterBean theaterBean = (TheaterBean) currentMovie[0];
			// MovieBean movie = (MovieBean) currentMovie[1];
			Log.i(TAG, "Service with id : " + movie.getId());
			intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_FROM_WIDGET, true);
			intentStartMovieActivity.putExtra(ParamIntent.MOVIE_ID, movie.getId());
			intentStartMovieActivity.putExtra(ParamIntent.MOVIE, movie);
			intentStartMovieActivity.putExtra(ParamIntent.MOVIE, movie);
			intentStartMovieActivity.putExtra(ParamIntent.THEATER_ID, theaterBean.getId());
			intentStartMovieActivity.putExtra(ParamIntent.THEATER, theaterBean);
			intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_NEAR, near);
			intentStartMovieActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

			this.startActivity(intentStartMovieActivity);
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
