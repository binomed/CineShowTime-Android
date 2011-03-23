package com.binomed.showtime.android.movieactivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.binomed.showtime.android.aidl.ICallbackMovie;
import com.binomed.showtime.android.aidl.IServiceMovie;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.util.AndShowtimeRequestManage;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.MovieBean;

public class AndShowTimeMovieService extends Service {

	private boolean serviceRunning;

	/**
	 * The list of all available callbacks
	 */
	private final RemoteCallbackList<ICallbackMovie> m_callbacks = new RemoteCallbackList<ICallbackMovie>();

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	private final IServiceMovie.Stub binder = new IServiceMovie.Stub() {

		@Override
		public void finish(String movieId) throws RemoteException {
			final int callbacks = m_callbacks.beginBroadcast();
			for (int i = 0; i < callbacks; i++) {
				try {
					m_callbacks.getBroadcastItem(i).finish(movieId);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			m_callbacks.finishBroadcast();
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

		movieId = intent.getExtras().getString(ParamIntent.SERVICE_MOVIE_ID);

		try {
			serviceRunning = true;
			Thread thread = new Thread(runnable);
			thread.start();
		} catch (Exception e) {
			Log.e("ServiceMovie", "error searching Movie", e);
		}
	}

	private double latitude, longitude;
	private String movieId;

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				MovieBean movie = BeanManagerFactory.getMovieForId(movieId);
				AndShowtimeRequestManage.completeMovieDetail(movie);

				// Intent intentDBService = new
				// Intent(AndShowTimeMovieService.this,
				// AndShowTimeDBService.class);
				// intentDBService.putExtra(ParamIntent.SERVICE_DB_TYPE_SAVE,
				// ParamIntent.SERVICE_DB_VAL_SAVE_MOVIE);
				// intentDBService.putExtra(ParamIntent.SERVICE_DB_SAVE_MOVIE_ID,
				// movieId);
				// startService(intentDBService);

			} catch (Exception e) {
				Log.e("ServiceMovie", "error searching movie", e);
			} finally {
				try {
					serviceRunning = false;
					binder.finish(movieId);
					stopSelf();
				} catch (RemoteException e) {
					//
				}
			}

		}
	};

}
