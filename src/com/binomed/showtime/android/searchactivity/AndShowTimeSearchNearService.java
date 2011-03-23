package com.binomed.showtime.android.searchactivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
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

public class AndShowTimeSearchNearService extends Service {

	private double latitude, longitude;
	private String cityName;
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
	private static final String TAG = "NearService"; //$NON-NLS-1$

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

		latitude = Double.valueOf(intent.getExtras().getDouble(ParamIntent.SERVICE_NEAR_LATITUDE, 0));
		longitude = Double.valueOf(intent.getExtras().getDouble(ParamIntent.SERVICE_NEAR_LONGITUDE, 0));
		cityName = intent.getExtras().getString(ParamIntent.SERVICE_NEAR_CITY);
		origin = intent.getExtras().getString(ParamIntent.SERVICE_NEAR_ORIGIN);
		theaterId = intent.getExtras().getString(ParamIntent.SERVICE_NEAR_THEATER_ID);
		day = intent.getExtras().getInt(ParamIntent.SERVICE_NEAR_DAY);
		start = intent.getExtras().getInt(ParamIntent.SERVICE_NEAR_START);
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
				NearResp resultBean = AndShowtimeRequestManage.searchTheatersOrMovies(//
						latitude //
						, longitude //
						, cityName //
						, null //
						, theaterId //
						, day //
						, start //
						, origin //
						);
				if (origin == null || AndShowTimeSearchActivity.class.getName().equals(origin)) {
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
