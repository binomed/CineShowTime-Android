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
package com.binomed.showtime.android.service;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.util.CineShowtimeDB2AndShowtimeBeans;

public class CineShowCleanFileService extends IntentService {

	private static final String TAG = "CleanFileService"; //$NON-NLS-1$

	private CineShowtimeDbAdapter mDbHelper;

	public CineShowCleanFileService() {
		super(TAG);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	protected void onHandleIntent(Intent arg0) {

		try {
			mDbHelper = new CineShowtimeDbAdapter(this);
			mDbHelper.open();

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
			if ((mDbHelper != null) && mDbHelper.isOpen()) {
				mDbHelper.close();
			}
			CineShowCleanFileService.this.stopSelf();
		}

	}

}
