package com.binomed.showtime.android.widget.one;

import java.util.Calendar;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.database.SQLException;
import android.os.IBinder;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.movieactivity.AndShowTimeMovieActivity;
import com.binomed.showtime.android.util.AndShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.TheaterBean;

public class AndShowTimeWidgetServiceOpenMovie extends Service {

	private static final String TAG = "ServiceOpenMovie"; //$NON-NLS-1$

	private AndShowtimeDbAdapter mDbHelper;

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
		mDbHelper = new AndShowtimeDbAdapter(this);
		try {
			try {

				mDbHelper.open();
			} catch (SQLException e) {
				Log.e(TAG, "error opening database", e);
			}
			Intent intentStartMovieActivity = new Intent(this, AndShowTimeMovieActivity.class);
			String movieId = intent.getStringExtra(ParamIntent.MOVIE_ID);
			String theaterId = intent.getStringExtra(ParamIntent.THEATER_ID);
			String near = intent.getStringExtra(ParamIntent.ACTIVITY_MOVIE_NEAR);
			Log.i(TAG, "Service with id : " + movieId);

			TheaterBean theaterBean = BeanManagerFactory.getTheaterForId(theaterId);
			if (theaterBean == null && mDbHelper.isOpen()) {
				theaterBean = AndShowtimeDB2AndShowtimeBeans.extractWidgetTheater(mDbHelper, Calendar.getInstance());
			}
			if (theaterBean != null) {
				BeanManagerFactory.putTheater(theaterBean);
			}

			MovieBean movie = null;
			if (mDbHelper.isOpen()) {
				movie = AndShowtimeDB2AndShowtimeBeans.extractWidgetMovie(mDbHelper, movieId, theaterBean);
				BeanManagerFactory.putMovie(movie);
			}
			intentStartMovieActivity.putExtra(ParamIntent.MOVIE_ID, movieId);
			intentStartMovieActivity.putExtra(ParamIntent.THEATER_ID, theaterId);
			intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_NEAR, near);
			intentStartMovieActivity.replaceExtras(intent);
			intentStartMovieActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			this.startActivity(intentStartMovieActivity);
		} catch (Exception e) {
			Log.e(TAG, "error launching activity movie", e);
		} finally {
			if (mDbHelper != null && mDbHelper.isOpen()) {
				mDbHelper.close();
			}
		}
		stopSelf();
	}
}
