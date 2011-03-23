package com.binomed.showtime.android.service;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.database.SQLException;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.util.CineShowtimeDB2AndShowtimeBeans;

public class CineShowCleanFileService extends Service {

	private static final String TAG = "CleanFileService"; //$NON-NLS-1$

	private boolean inThread;

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

		try {

			if (!inThread) {
				try {
					mDbHelper = new CineShowtimeDbAdapter(this);
					mDbHelper.open();
				} catch (SQLException e) {
					Log.e(TAG, "error opening database", e);
				}
				Thread fillDBThread = new Thread(fillDBRunnable);
				fillDBThread.start();
			}

		} catch (Exception e) {
			Log.e(TAG, "Error while cleaning files", e);
			if (mDbHelper != null && mDbHelper.isOpen()) {
				mDbHelper.close();
			}
		}
	}

	private Runnable fillDBRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				inThread = true;

				File root = Environment.getExternalStorageDirectory();
				Calendar lastWeek = Calendar.getInstance();
				lastWeek.add(Calendar.WEEK_OF_MONTH, -1);
				Map<String, MovieBean> mapMovie = null;
				if (mDbHelper.isOpen()) {
					mapMovie = new HashMap<String, MovieBean>(CineShowtimeDB2AndShowtimeBeans.extractMovies(mDbHelper, null));
				} else {
					mapMovie = new HashMap<String, MovieBean>();
				}
				long lastWeekTimeInMillis = lastWeek.getTimeInMillis();
				if (root.canWrite()) {
					File posterDir = new File(root, CineShowtimeCst.FOLDER_POSTER);
					// we skim throught all files in order to remove thoses who passed a week and which are not yet in data base
					if (posterDir.exists()) {
						String fileName = null;
						String movieId = null;
						for (File posterFile : posterDir.listFiles()) {
							fileName = posterFile.getName();
							if (fileName.endsWith(".jpg")) { //$NON-NLS-1$
								if (posterFile.lastModified() < lastWeekTimeInMillis) {
									movieId = fileName.substring(0, fileName.length() - 4);
									if (!mapMovie.containsKey(movieId)) {
										if (!posterFile.delete()) {
											Log.w(TAG, fileName + " was not deleted"); //$NON-NLS-1$
										}
									}
								}
							}
						}
					}
				}

			} catch (Exception e) {
				Log.e(TAG, "error cleaning file", e);
			} finally {
				inThread = false;
				if (mDbHelper != null && mDbHelper.isOpen()) {
					mDbHelper.close();
				}
				CineShowCleanFileService.this.stopSelf();
			}

		}
	};

}
