package com.binomed.showtime.android.resultsactivity;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
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

public class CineShowTimeResultsService extends Service {

	private double latitude, longitude;
	private String cityName;
	private String movieName;
	private String theaterId;
	private String origin;
	private int day;
	private int start;
	private Intent intent;
	private NearResp nearResp;
	private Map<String, LocalisationBean> localisationMap = new HashMap<String, LocalisationBean>();
	private Thread thread;

	private boolean serviceStarted;
	/**
	 * The list of all available callbacks
	 */
	private final RemoteCallbackList<ICallbackSearch> m_callbacks = new RemoteCallbackList<ICallbackSearch>();
	private static final String TAG = "SearchService"; //$NON-NLS-1$

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
			if (serviceStarted && thread.isAlive()) {
				thread.interrupt();
			}

		}
	};

	@Override
	public ComponentName startService(Intent service) {
		return super.startService(service);
	}

	@Override
	public boolean stopService(Intent name) {
		try {
			if (serviceStarted && thread.isAlive()) {
				thread.interrupt();
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

			this.intent = intent;
			try {
				serviceStarted = true;
				thread = new Thread(runnable);
				thread.start();
			} catch (Exception e) {
				Log.e(TAG, "error searching theaters", e);
			}
		} else {
			nearResp = null;
			try {
				serviceStarted = false;
				binder.finish();
				stopSelf();
			} catch (RemoteException e) {
				//
			}

		}
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
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

				Intent intentNearFillDBService = new Intent(CineShowTimeResultsService.this, CineShowDBGlobalService.class);
				intentNearFillDBService.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_NEAR_RESP_WRITE);
				intentNearFillDBService.putExtra(ParamIntent.SERVICE_DB_DATA, nearResp);
				CineShowTimeResultsService.this.startService(intentNearFillDBService);

			} catch (Exception e) {
				Log.e(TAG, "error searching theaters", e);
				nearResp = null;
			} finally {
				try {
					serviceStarted = false;
					binder.finish();
					stopSelf();
				} catch (RemoteException e) {
					//
				}
			}

		}
	};

}
