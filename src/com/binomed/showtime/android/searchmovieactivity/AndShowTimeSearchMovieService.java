package com.binomed.showtime.android.searchmovieactivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.binomed.showtime.android.aidl.ICallbackSearchMovie;
import com.binomed.showtime.android.aidl.IServiceSearchMovie;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.util.AndShowtimeRequestManage;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.MovieResp;

public class AndShowTimeSearchMovieService extends Service {

	private double latitude, longitude;
	private String cityName;
	private String movieName;
	private String theaterId;
	private int day;
	private boolean serviceRunning;

	/**
	 * The list of all available callbacks
	 */
	private final RemoteCallbackList<ICallbackSearchMovie> m_callbacks = new RemoteCallbackList<ICallbackSearchMovie>();

	private static final String TAG = "SearchMovieService"; //$NON-NLS-1$

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	private final IServiceSearchMovie.Stub binder = new IServiceSearchMovie.Stub() {

		@Override
		public void finish() throws RemoteException {
			final int callbacks = m_callbacks.beginBroadcast();
			for (int i = 0; i < callbacks; i++) {
				try {
					m_callbacks.getBroadcastItem(i).finish();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			m_callbacks.finishBroadcast();
		}

		@Override
		public void registerCallback(ICallbackSearchMovie cb) throws RemoteException {
			if (cb != null) {
				m_callbacks.register(cb);
			}
		}

		@Override
		public void unregisterCallback(ICallbackSearchMovie cb) throws RemoteException {
			m_callbacks.unregister(cb);
		}

		@Override
		public boolean isServiceRunning() throws RemoteException {
			return serviceRunning;
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

		latitude = Double.valueOf(intent.getExtras().getDouble(ParamIntent.SERVICE_MOVIE_LATITUDE, 0));
		longitude = Double.valueOf(intent.getExtras().getDouble(ParamIntent.SERVICE_MOVIE_LONGITUDE, 0));
		cityName = intent.getExtras().getString(ParamIntent.SERVICE_MOVIE_CITY);
		movieName = intent.getExtras().getString(ParamIntent.SERVICE_MOVIE_NAME);
		theaterId = intent.getExtras().getString(ParamIntent.SERVICE_MOVIE_THEATER_ID);
		day = intent.getExtras().getInt(ParamIntent.SERVICE_MOVIE_DAY);
		try {
			serviceRunning = true;
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
				MovieResp resultBean = AndShowtimeRequestManage.searchMovies( //
						latitude, longitude, cityName, movieName, theaterId, day);
				BeanManagerFactory.setMovieResp(resultBean);

			} catch (Exception e) {
				Log.e(TAG, "error searching theaters", e);
				BeanManagerFactory.setMovieResp(null);
			} finally {
				try {
					serviceRunning = false;
					binder.finish();
					stopSelf();
				} catch (RemoteException e) {
					//
				}
			}

		}
	};

}
