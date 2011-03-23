package com.binomed.showtime.android.resultsactivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.binomed.showtime.android.aidl.ICallbackSearch;
import com.binomed.showtime.android.aidl.IServiceSearch;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.util.AndShowtimeRequestManage;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.NearResp;

public class AndShowTimeResultsService extends Service {

	private double latitude, longitude;
	private String cityName;
	private String movieName;
	private String theaterId;
	private String origin;
	private int day;
	private int start;
	private Intent intent;

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
	};

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
			Thread thread = new Thread(runnable);
			thread.start();
		} catch (Exception e) {
			Log.e(TAG, "error searching theaters", e);
		}
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				NearResp resultBean = AndShowtimeRequestManage.searchTheatersOrMovies( //
						latitude //
						, longitude //
						, cityName //
						, movieName //
						, theaterId //
						, day //
						, start //
						, origin //
						);
				if (origin == null || AndShowTimeResultsActivity.class.getName().equals(origin)) {
					BeanManagerFactory.setNearResp(resultBean);
				} else {
					BeanManagerFactory.setNearRespFromWidget(resultBean);
				}

			} catch (Exception e) {
				Log.e(TAG, "error searching theaters", e);
				BeanManagerFactory.setNearResp(null);
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
