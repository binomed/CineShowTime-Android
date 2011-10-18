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
package com.binomed.showtime.android.screen.results;

import java.util.HashMap;
import java.util.Map;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.service.CineShowDBGlobalService;
import com.binomed.showtime.android.util.CineShowtimeRequestManage;

public class CineShowTimeResultsService extends IntentService {

	private double latitude, longitude;
	private String cityName;
	private String movieName;
	private String theaterId;
	private String origin;
	private int day;
	private int start;
	private NearResp nearResp;
	private Map<String, LocalisationBean> localisationMap = new HashMap<String, LocalisationBean>();
	private HashMap<Integer, Boolean> mapCancel = new HashMap<Integer, Boolean>();
	private int compt = 0;

	private boolean serviceStarted;
	/**
	 * The list of all available callbacks
	 */
	private final RemoteCallbackList<ICallbackSearch> m_callbacks = new RemoteCallbackList<ICallbackSearch>();
	private static final String TAG = "SearchService"; //$NON-NLS-1$

	public CineShowTimeResultsService() {
		super(TAG);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	private final IServiceSearch.Stub binder = new IServiceSearch.Stub() {

		@Override
		public void finish() throws RemoteException {
			final int callbacks = m_callbacks.beginBroadcast();
			for (int i = 0; i < callbacks; i++) {
				try {
					m_callbacks.getBroadcastItem(i).finish();
				} catch (RemoteException e) {
					Log.e(TAG, "Error during call back finsih", e);
				}
			}
			m_callbacks.finishBroadcast();
		}

		@Override
		public void registerCallback(ICallbackSearch cb) throws RemoteException {
			if (cb != null) {
				synchronized (m_callbacks) {
					m_callbacks.register(cb);
				}
			}
		}

		@Override
		public void unregisterCallback(ICallbackSearch cb) throws RemoteException {
			synchronized (m_callbacks) {
				m_callbacks.unregister(cb);
			}
		}

		@Override
		public boolean isServiceRunning() throws RemoteException {
			return serviceStarted;
		}

		@Override
		public NearResp getNearResp() throws RemoteException {
			return nearResp;
		}

		@Override
		public void finishLocation(String theaterId) throws RemoteException {
			final int callbacks = m_callbacks.beginBroadcast();
			for (int i = 0; i < callbacks; i++) {
				try {
					m_callbacks.getBroadcastItem(i).finishLocation(theaterId);
				} catch (RemoteException e) {
					Log.e(TAG, "Error during call back finsih", e);
				}
			}
			m_callbacks.finishBroadcast();

		}

		@Override
		public LocalisationBean getLocalisation(String theaterId) throws RemoteException {
			return localisationMap.get(theaterId);
		}

		@Override
		public void cancelService() throws RemoteException {
			if (serviceStarted) {
				mapCancel.put(compt, true);
			}

		}

		@Override
		public void errorService() throws RemoteException {
			final int callbacks = m_callbacks.beginBroadcast();
			for (int i = 0; i < callbacks; i++) {
				try {
					m_callbacks.getBroadcastItem(i).error();
				} catch (RemoteException e) {
					Log.e(TAG, "Error during call back finsih", e);
				}
			}
			m_callbacks.finishBroadcast();

		}
	};

	@Override
	public ComponentName startService(Intent service) {
		return super.startService(service);
	}

	@Override
	public boolean stopService(Intent name) {
		try {
			if (serviceStarted) {
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
		if (intent != null) {

			Bundle extras = intent.getExtras();
			latitude = Double.valueOf(extras.getDouble(ParamIntent.SERVICE_SEARCH_LATITUDE, 0));
			longitude = Double.valueOf(extras.getDouble(ParamIntent.SERVICE_SEARCH_LONGITUDE, 0));
			cityName = extras.getString(ParamIntent.SERVICE_SEARCH_CITY);
			movieName = extras.getString(ParamIntent.SERVICE_SEARCH_MOVIE_NAME);
			origin = extras.getString(ParamIntent.SERVICE_SEARCH_ORIGIN);
			theaterId = extras.getString(ParamIntent.SERVICE_SEARCH_THEATER_ID);
			day = extras.getInt(ParamIntent.SERVICE_SEARCH_DAY);
			start = extras.getInt(ParamIntent.SERVICE_SEARCH_START);
			int widgetId = extras.getInt(ParamIntent.WIDGET_ID, -1);

			try {
				serviceStarted = true;
				localisationMap = new HashMap<String, LocalisationBean>();
				nearResp = CineShowtimeRequestManage.searchTheatersOrMovies( //
						latitude //
						, longitude //
						, cityName //
						, movieName //
						, theaterId //
						, day //
						, start //
						, origin //
						);

				// if request comes from widget, we have to refresh it
				if ((widgetId != -1) && (nearResp != null) && (nearResp.getTheaterList() != null) && (nearResp.getTheaterList().size() > 0)) {
					nearResp.getTheaterList().get(0).setWidgetId(widgetId);
				}

				Intent intentNearFillDBService = new Intent(CineShowTimeResultsService.this, CineShowDBGlobalService.class);
				intentNearFillDBService.putExtra(ParamIntent.SERVICE_DB_TYPE, widgetId == -1 ? CineShowtimeCst.DB_TYPE_NEAR_RESP_WRITE : CineShowtimeCst.DB_TYPE_WIDGET_WRITE_LIST);
				intentNearFillDBService.putExtra(ParamIntent.SERVICE_DB_DATA, nearResp);
				CineShowTimeResultsService.this.startService(intentNearFillDBService);

			} catch (Exception e) {
				Log.e(TAG, "error searching theaters", e);
				nearResp = null;
			} finally {
				try {
					serviceStarted = false;
					if (!mapCancel.get(compt)) {
						binder.finish();
					}
				} catch (RemoteException e) {
					//
				}
			}
		} else {
			nearResp = null;
			try {
				serviceStarted = false;
				if (!mapCancel.get(compt)) {
					binder.finish();
				}
			} catch (RemoteException e) {
				//
			}

		}

		compt++;

	}

}
