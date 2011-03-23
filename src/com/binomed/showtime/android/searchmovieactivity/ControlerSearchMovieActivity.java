package com.binomed.showtime.android.searchmovieactivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Location;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.aidl.ICallbackSearchMovie;
import com.binomed.showtime.android.aidl.IServiceSearchMovie;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.movieactivity.AndShowTimeMovieActivity;
import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.AndShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.MovieResp;
import com.binomed.showtime.beans.TheaterBean;

public class ControlerSearchMovieActivity {

	private static final String TAG = "ControlerSearchMovieActivity"; //$NON-NLS-1$

	private AndShowTimeSearchMovieActivity movieActivity;
	private ModelSearchMovieActivity model;
	private AndShowtimeDbAdapter mDbHelper;

	private IServiceSearchMovie serviceNear;

	private static ControlerSearchMovieActivity instance;

	public static ControlerSearchMovieActivity getInstance() {
		if (instance == null) {
			instance = new ControlerSearchMovieActivity();
		}
		return instance;
	}

	private ControlerSearchMovieActivity() {
		super();
	}

	public void registerView(AndShowTimeSearchMovieActivity nearActivity) {
		this.movieActivity = nearActivity;
		bindService();
		initDB();
	}

	public ModelSearchMovieActivity getModelNearActivity() {
		if (model == null) {
			model = new ModelSearchMovieActivity();
		}
		return model;
	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public void openMovieActivity(MovieBean movie, TheaterBean theater) {
		Intent intentStartMovieActivity = new Intent(movieActivity, AndShowTimeMovieActivity.class);

		intentStartMovieActivity.putExtra(ParamIntent.MOVIE_ID, movie.getId());
		intentStartMovieActivity.putExtra(ParamIntent.THEATER_ID, theater.getId());
		movieActivity.startActivityForResult(intentStartMovieActivity, movieActivity.ACTIVITY_OPEN_MOVIE);
	}

	public void launchMovieService() throws UnsupportedEncodingException {
		// bindService();

		Location gpsLocation = model.getLocalisationSearch();
		String cityName = model.getCityName();
		String movieName = model.getMovieName();
		String theaterId = model.getFavTheaterId();
		int day = model.getDay();

		mDbHelper.createMovieRequest(cityName //
				, movieName //
				, (gpsLocation != null) ? gpsLocation.getLatitude() : null //
				, (gpsLocation != null) ? gpsLocation.getLongitude() : null //
				, theaterId//
				);

		if ((cityName != null) && (cityName.length() > 0)) {
			model.getNearRequestList().add(cityName);
		}
		if ((movieName != null) && (movieName.length() > 0)) {
			model.getRequestMovieList().add(movieName);
		}

		movieActivity.fillAutoFields();

		Intent intentNearService = new Intent(movieActivity, AndShowTimeSearchMovieService.class);

		intentNearService.putExtra(ParamIntent.SERVICE_MOVIE_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
		intentNearService.putExtra(ParamIntent.SERVICE_MOVIE_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
		intentNearService.putExtra(ParamIntent.SERVICE_MOVIE_CITY, ((cityName != null) ? URLEncoder.encode(cityName, AndShowTimeEncodingUtil.getEncoding()) : cityName));
		intentNearService.putExtra(ParamIntent.SERVICE_MOVIE_NAME, ((movieName != null) ? URLEncoder.encode(movieName, AndShowTimeEncodingUtil.getEncoding()) : movieName));
		intentNearService.putExtra(ParamIntent.SERVICE_MOVIE_THEATER_ID, theaterId);
		intentNearService.putExtra(ParamIntent.SERVICE_MOVIE_DAY, day);

		movieActivity.startService(intentNearService);
	}

	/*
	 * 
	 * DB
	 */

	public void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new AndShowtimeDbAdapter(movieActivity);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			openDB();

			Cursor cursorRequestHistory = mDbHelper.fetchAllMovieRequest();
			if (cursorRequestHistory.moveToFirst()) {
				int columnIndex = 0;
				model.getNearRequestList().clear();
				model.getRequestMovieList().clear();
				do {
					columnIndex = cursorRequestHistory.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_CITY_NAME);
					model.getNearRequestList().add(cursorRequestHistory.getString(columnIndex));
					columnIndex = cursorRequestHistory.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_MOVIE_NAME);
					model.getRequestMovieList().add(cursorRequestHistory.getString(columnIndex));
				} while (cursorRequestHistory.moveToNext());
			}
			cursorRequestHistory.close();

		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void closeDB() {
		try {
			Log.i(TAG, "Close DB"); //$NON-NLS-1$
			mDbHelper.close();
		} catch (Exception e) {
			Log.e(TAG, "error onDestroy of movie Activity", e); //$NON-NLS-1$
		}
	}

	private Runnable fillDBRunnable = new Runnable() {
		public void run() {
			try {
				MovieResp movieResp = BeanManagerFactory.getMovieResp();
				if (movieResp != null) {

					mDbHelper.deleteTheatersShowtimeRequestAndLocation();
					for (TheaterBean theater : movieResp.getTheaterList()) {
						mDbHelper.createTheater(theater);
						if (theater.getPlace() != null) {
							mDbHelper.createLocation(theater.getPlace(), theater.getId());
						}
						for (String movieId : theater.getMovieMap().keySet()) {
							for (Long showTime : theater.getMovieMap().get(movieId)) {
								mDbHelper.createShowtime(theater.getId(), movieId, showTime);
							}
						}
					}
					if (movieResp.getMovie() != null) {
						mDbHelper.createOrUpdateMovie(movieResp.getMovie());
						Set<String> ids = new HashSet<String>();
						ids.add(movieResp.getMovie().getId());
						mDbHelper.deleteMovies(ids);
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "error putting data into data base", e);
			}

		}
	};

	public void addFavorite(TheaterBean theaterBean) {
		try {
			mDbHelper.addTheaterToFavorites(theaterBean);
		} catch (Exception e) {
			Log.e(TAG, "error putting data into data base", e);
		}

	}

	public List<TheaterBean> getFavTheater() {
		List<TheaterBean> theaterList = AndShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);

		return theaterList;
	}

	/*
	 * 
	 * CALL BACK SERVICE
	 */

	public void bindService() {
		movieActivity.bindService(new Intent(movieActivity, AndShowTimeSearchMovieService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() {
		try {
			serviceNear.unregisterCallback(m_callback);
			movieActivity.unbindService(mConnection);
		} catch (Exception e) {
			Log.e(TAG, "error while unbinding service", e);
		}
	}

	protected boolean isServiceRunning() {
		if (serviceNear != null) {
			try {
				return serviceNear.isServiceRunning();
			} catch (RemoteException e) {
				Log.e(TAG, "Error during checking service", e); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * The service connection inteface with our binded service {@link http ://code .google.com/android/reference/android/content/ServiceConnection.html}
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceNear = IServiceSearchMovie.Stub.asInterface(service);

			try {
				serviceNear.registerCallback(m_callback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	/**
	 * The callback object that will return from the service
	 */
	private ICallbackSearchMovie m_callback = new ICallbackSearchMovie.Stub() {

		@Override
		public void finish() throws RemoteException {
			// Thread threadFillDB = new Thread(fillDBRunnable);
			// threadFillDB.start();
			Intent intentMovieFillDBService = new Intent(movieActivity, AndShowTimeSearchMovieDBService.class);
			movieActivity.startService(intentMovieFillDBService);
			movieActivity.m_callbackHandler.sendInputRecieved();

		}

	};

}
