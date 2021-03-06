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
package com.binomed.showtime.android.screen.movie;

import java.util.HashMap;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.util.CineShowtimeRequestManage;

public class CineShowTimeMovieService extends IntentService {

	private boolean serviceRunning;
	private double latitude, longitude;
	private String movieId;
	private MovieBean movie;
	private String near;
	private boolean inBrodCast = false;
	private HashMap<Integer, Boolean> mapCancel = new HashMap<Integer, Boolean>();
	private int compt = 0;

	private static final String TAG = "CineShowTimeMovieService";

	/**
	 * The list of all available callbacks
	 */
	private final RemoteCallbackList<ICallbackMovie> m_callbacks = new RemoteCallbackList<ICallbackMovie>();

	public CineShowTimeMovieService() {
		super(TAG);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	private final IServiceMovie.Stub binder = new IServiceMovie.Stub() {

		@Override
		public void finish(String movieId) throws RemoteException {
			inBrodCast = true;
			final int callbacks = m_callbacks.beginBroadcast();
			for (int i = 0; i < callbacks; i++) {
				try {
					m_callbacks.getBroadcastItem(i).finish(movieId);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			m_callbacks.finishBroadcast();
			inBrodCast = false;
		}

		@Override
		public void registerCallback(ICallbackMovie cb) throws RemoteException {
			if (cb != null) {
				m_callbacks.register(cb);
			}
		}

		@Override
		public void unregisterCallback(ICallbackMovie cb) throws RemoteException {
			m_callbacks.unregister(cb);
		}

		@Override
		public boolean isServiceRunning() throws RemoteException {
			return serviceRunning;
		}

		@Override
		public MovieBean getMovie(String movieId) throws RemoteException {
			return movie;
		}

		@Override
		public void cancelService() throws RemoteException {
			if (serviceRunning) {
				mapCancel.put(compt, true);
			}

		}

		@Override
		public void error() throws RemoteException {
			inBrodCast = true;
			final int callbacks = m_callbacks.beginBroadcast();
			for (int i = 0; i < callbacks; i++) {
				try {
					m_callbacks.getBroadcastItem(i).error();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			m_callbacks.finishBroadcast();
			inBrodCast = false;

		}
	};

	@Override
	public boolean stopService(Intent name) {
		try {
			if (serviceRunning) {
				stopSelf();
			}
		} catch (Exception e) {
			Log.e(TAG, "Stop service error", e);
		}
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mapCancel.put(compt, false);
		Exception error = null;
		movieId = intent.getExtras().getString(ParamIntent.SERVICE_MOVIE_ID);
		movie = intent.getExtras().getParcelable(ParamIntent.SERVICE_MOVIE);
		near = intent.getExtras().getString(ParamIntent.SERVICE_MOVIE_NEAR);

		try {
			serviceRunning = true;
			CineShowtimeRequestManage.completeMovieDetail(getApplicationContext(), movie, near);

		} catch (Exception e) {
			Log.e(TAG, "error searching movie", e);
			error = e;
			try {
				binder.error();
			} catch (RemoteException e1) {
			}
		} finally {
			try {
				serviceRunning = false;
				while (inBrodCast) {
					Thread.sleep(100);
				}
				if (!mapCancel.get(compt) || (error != null)) {
					binder.finish(movieId);
				}
			} catch (Exception e) {
				Log.e(TAG, "Error during finishing service", e);
			}
		}
		compt++;

	}

}
