package com.binomed.showtime.android.screen.movie;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.util.CineShowtimeRequestManage;

public class CineShowTimeMovieService extends Service {

	private boolean serviceRunning;
	private double latitude, longitude;
	private String movieId;
	private MovieBean movie;
	private String near;
	private boolean inBrodCast = false;
	private Thread thread = null;

	private static final String TAG = "CineShowTimeMovieService";

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
			if (serviceRunning && thread.isAlive()) {
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
			if (serviceRunning && thread.isAlive()) {
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

		movieId = intent.getExtras().getString(ParamIntent.SERVICE_MOVIE_ID);
		movie = intent.getExtras().getParcelable(ParamIntent.SERVICE_MOVIE);
		near = intent.getExtras().getString(ParamIntent.SERVICE_MOVIE_NEAR);

		try {
			serviceRunning = true;
			thread = new Thread(runnable);
			thread.start();
		} catch (Exception e) {
			Log.e("ServiceMovie", "error searching Movie", e);
		}
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				CineShowtimeRequestManage.completeMovieDetail(movie, near);

			} catch (Exception e) {
				Log.e(TAG, "error searching movie", e);
			} finally {
				try {
					serviceRunning = false;
					while (inBrodCast) {
						Thread.sleep(100);
					}
					binder.finish(movieId);
					stopSelf();
				} catch (Exception e) {
					Log.e(TAG, "Error during finishing service", e);
				}
			}

		}
	};

}
