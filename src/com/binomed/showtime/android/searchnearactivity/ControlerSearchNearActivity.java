package com.binomed.showtime.android.searchnearactivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Location;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.aidl.ICallbackSearchNear;
import com.binomed.showtime.android.aidl.IServiceSearchNear;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.movieactivity.AndShowTimeMovieActivity;
import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.AndShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;
import com.binomed.showtime.cst.SpecialChars;

public class ControlerSearchNearActivity {

	private static final String TAG = "ControlerNearActivity"; //$NON-NLS-1$

	private AndShowTimeSearchNearActivity nearActivity;
	private ModelSearchNearActivity model;
	private AndShowtimeDbAdapter mDbHelper;

	private IServiceSearchNear serviceNear;

	private static ControlerSearchNearActivity instance;

	public static ControlerSearchNearActivity getInstance() {
		if (instance == null) {
			instance = new ControlerSearchNearActivity();
		}
		return instance;
	}

	private ControlerSearchNearActivity() {
		super();
	}

	public void registerView(AndShowTimeSearchNearActivity nearActivity) {
		this.nearActivity = nearActivity;
		bindService();
		initDB();
	}

	public ModelSearchNearActivity getModelNearActivity() {
		if (model == null) {
			model = new ModelSearchNearActivity();
		}
		return model;
	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public void openMovieActivity(MovieBean movie, TheaterBean theater) {
		Intent intentStartMovieActivity = new Intent(nearActivity, AndShowTimeMovieActivity.class);

		intentStartMovieActivity.putExtra(ParamIntent.MOVIE_ID, movie.getId());
		intentStartMovieActivity.putExtra(ParamIntent.THEATER_ID, theater.getId());
		nearActivity.startActivityForResult(intentStartMovieActivity, nearActivity.ACTIVITY_OPEN_MOVIE);
	}

	public void launchNearService() throws UnsupportedEncodingException {
		// bindService();

		Location gpsLocation = model.getLocalisationSearch();
		String cityName = model.getCityName();
		String theaterId = model.getFavTheaterId();
		int day = model.getDay();
		int start = model.getStart();

		if (mDbHelper.isOpen()) {
			mDbHelper.createNearRequest(cityName //
					, (gpsLocation != null) ? gpsLocation.getLatitude() : null //
					, (gpsLocation != null) ? gpsLocation.getLongitude() : null //
					, theaterId//
					);
		}
		if ((cityName != null) && (cityName.length() > 0)) {
			model.getRequestList().add(cityName);
			nearActivity.fillAutoField();
		}

		Intent intentNearService = new Intent(nearActivity, AndShowTimeSearchNearService.class);

		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_CITY, ((cityName != null) ? URLEncoder.encode(cityName, AndShowTimeEncodingUtil.getEncoding()) : cityName));
		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_THEATER_ID, theaterId);
		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_DAY, day);
		intentNearService.putExtra(ParamIntent.SERVICE_NEAR_START, start);

		nearActivity.startService(intentNearService);
	}

	/*
	 * 
	 * DB
	 */

	public void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new AndShowtimeDbAdapter(nearActivity);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			openDB();

			getModelNearActivity();

			boolean rerunService = false;
			if (mDbHelper.isOpen()) {
				Cursor cursorRequestHistory = mDbHelper.fetchAllNearRequest();
				if (cursorRequestHistory.moveToFirst()) {
					int columnIndex = 0;
					model.getRequestList().clear();
					do {
						columnIndex = cursorRequestHistory.getColumnIndex(AndShowtimeDbAdapter.KEY_NEAR_REQUEST_CITY_NAME);
						model.getRequestList().add(cursorRequestHistory.getString(columnIndex));
					} while (cursorRequestHistory.moveToNext());
				}
				cursorRequestHistory.close();

				Cursor cursorLastResult = mDbHelper.fetchLastNearRequest();
				if (cursorLastResult.moveToFirst()) {
					Calendar calendarLastRequest = Calendar.getInstance();
					Calendar today = Calendar.getInstance();
					long timeLastRequest = cursorLastResult.getLong(cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_NEAR_REQUEST_TIME));
					calendarLastRequest.setTimeInMillis(timeLastRequest);
					int yearToday = today.get(Calendar.YEAR);
					int monthToday = today.get(Calendar.MONTH);
					int dayToday = today.get(Calendar.DAY_OF_MONTH);
					int yearLast = calendarLastRequest.get(Calendar.YEAR);
					int monthLast = calendarLastRequest.get(Calendar.MONTH);
					int dayLast = calendarLastRequest.get(Calendar.DAY_OF_MONTH);
					if ((yearToday != yearLast) //
							|| (monthToday != monthLast) //
							|| (dayToday != dayLast) //
					) {//
						Location location = new Location(SpecialChars.EMPTY);
						int columnIndex = cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_NEAR_REQUEST_LATITUDE);
						location.setLatitude(cursorLastResult.getDouble(columnIndex));
						columnIndex = cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_NEAR_REQUEST_LONGITUDE);
						location.setLongitude(cursorLastResult.getDouble(columnIndex));

						if (model == null) {
							getModelNearActivity();
						}
						model.setLocalisationSearch(location);

						columnIndex = cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_NEAR_REQUEST_CITY_NAME);
						model.setCityName(cursorLastResult.getString(columnIndex));

						rerunService = true;

						boolean checkboxPreferenceAutoReload;
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(nearActivity.getBaseContext());
						checkboxPreferenceAutoReload = prefs.getBoolean(nearActivity.getResources().getString(R.string.preference_gen_key_auto_reload), true);

						rerunService = rerunService && checkboxPreferenceAutoReload;
					}
				}
				cursorLastResult.close();
			}
			if (rerunService) {
				try {
					nearActivity.launchNearService();
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "error while rerun service", e); //$NON-NLS-1$
				}
			} else {

				NearResp nearResp = BeanManagerFactory.getNearResp();
				if (nearResp == null) {
					nearResp = new NearResp();
					if (mDbHelper.isOpen()) {
						nearResp.setTheaterList(AndShowtimeDB2AndShowtimeBeans.extractTheaterList(mDbHelper));
						if ((nearResp.getTheaterList() != null) && !nearResp.getTheaterList().isEmpty()) {
							nearResp.setMapMovies(AndShowtimeDB2AndShowtimeBeans.extractMovies(mDbHelper));
							BeanManagerFactory.setNearResp(nearResp);
							Log.i(TAG, "Datas found"); //$NON-NLS-1$
						} else {
							Log.i(TAG, "No datas founds"); //$NON-NLS-1$
						}
					}
				}
			}
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void closeDB() {
		try {
			if (mDbHelper.isOpen()) {
				Log.i(TAG, "Close DB"); //$NON-NLS-1$
				mDbHelper.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "error onDestroy of movie Activity", e); //$NON-NLS-1$
		}
	}

	private Runnable fillDBRunnable = new Runnable() {
		public void run() {
			try {
				NearResp nearResp = BeanManagerFactory.getNearResp();
				if (mDbHelper.isOpen()) {
					mDbHelper.deleteTheatersShowtimeRequestAndLocation();
					for (TheaterBean theater : nearResp.getTheaterList()) {
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
					for (MovieBean movie : nearResp.getMapMovies().values()) {
						mDbHelper.createOrUpdateMovie(movie);
					}
					mDbHelper.deleteMovies(nearResp.getMapMovies().keySet());
				}
			} catch (Exception e) {
				Log.e(TAG, "error putting data into data base", e);
			}

		}
	};

	public void addFavorite(TheaterBean theaterBean) {
		try {
			if (mDbHelper.isOpen()) {
				mDbHelper.addTheaterToFavorites(theaterBean);
			} else {
				Toast.makeText(this.nearActivity, R.string.msgErrorNoDb, Toast.LENGTH_LONG);
			}
		} catch (Exception e) {
			Log.e(TAG, "error putting data into data base", e);
		}

	}

	public void removeFavorite(TheaterBean theaterBean) {
		try {
			if (mDbHelper.isOpen()) {
				mDbHelper.deleteFavorite(theaterBean.getId());
			}
		} catch (Exception e) {
			Log.e(TAG, "error removing theater from fav", e);
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
		nearActivity.bindService(new Intent(nearActivity, AndShowTimeSearchNearService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() {
		try {
			serviceNear.unregisterCallback(m_callback);
			nearActivity.unbindService(mConnection);
		} catch (Exception e) {
			Log.e(TAG, "error while unbinding service", e);
		}
	}

	/**
	 * The service connection inteface with our binded service {@link http ://code .google.com/android/reference/android/content/ServiceConnection.html}
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceNear = IServiceSearchNear.Stub.asInterface(service);

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

	protected boolean isServiceRunning() {
		if (serviceNear != null) {
			try {
				return serviceNear.isServiceRunning();
			} catch (RemoteException e) {
				Log.e(TAG, "Eror during checking service", e); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * The callback object that will return from the service
	 */
	private ICallbackSearchNear m_callback = new ICallbackSearchNear.Stub() {

		@Override
		public void finish() throws RemoteException {
			// Thread threadFillDB = new Thread(fillDBRunnable);
			// threadFillDB.start();
			Intent intentNearFillDBService = new Intent(nearActivity, AndShowTimeSearchNearDBService.class);
			nearActivity.startService(intentNearFillDBService);

			nearActivity.m_callbackHandler.sendInputRecieved();

		}

	};

}
