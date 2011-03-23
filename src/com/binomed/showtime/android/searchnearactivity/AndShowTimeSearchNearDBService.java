package com.binomed.showtime.android.searchnearactivity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;

public class AndShowTimeSearchNearDBService extends Service {

	private static final String TAG = "SearchDBService"; //$NON-NLS-1$

	private AndShowtimeDbAdapter mDbHelper;

	private boolean newThread;
	private boolean inThread;

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

		try {
			newThread = true;

			if (!inThread) {
				mDbHelper = new AndShowtimeDbAdapter(this);
				mDbHelper.open();
				Thread fillDBThread = new Thread(fillDBRunnable);
				fillDBThread.start();
			}

		} catch (Exception e) {
			Log.e(TAG, "Error writing datas", e);
			if (mDbHelper != null && mDbHelper.isOpen()) {
				mDbHelper.close();
			}
		}
	}

	private Runnable fillDBRunnable = new Runnable() {
		public void run() {
			try {
				inThread = true;
				do {
					newThread = false;
					NearResp nearResp = BeanManagerFactory.getNearResp();
					if (nearResp != null) {
						ArrayList<TheaterBean> copyListTheater = new ArrayList<TheaterBean>(nearResp.getTheaterList());
						HashMap<String, MovieBean> copyMovieMap = new HashMap<String, MovieBean>(nearResp.getMapMovies());

						mDbHelper.deleteTheatersShowtimeRequestAndLocation();
						for (TheaterBean theater : copyListTheater) {
							mDbHelper.createTheater(theater);
							if (theater.getPlace() != null) {
								mDbHelper.createLocation(theater.getPlace(), theater.getId());
							}
							for (String movieId : theater.getMovieMap().keySet()) {
								for (Long showTime : theater.getMovieMap().get(movieId)) {
									try {
										mDbHelper.createShowtime(theater.getId(), movieId, showTime);
									} catch (Exception e) {
										Log.e(TAG, "error inserting showtime : " + theater.getTheaterName() + " movie  : " + movieId);
									}
								}
							}
						}
						for (MovieBean movie : copyMovieMap.values()) {
							mDbHelper.createOrUpdateMovie(movie);
						}
						mDbHelper.deleteMovies(copyMovieMap.keySet());
					}

				} while (newThread);
			} catch (Exception e) {
				Log.e(TAG, "error putting data into data base", e);
			} finally {
				if (mDbHelper.isOpen()) {
					mDbHelper.close();
				}
				inThread = false;
				newThread = false;
				AndShowTimeSearchNearDBService.this.stopSelf();
			}

		}
	};

}
