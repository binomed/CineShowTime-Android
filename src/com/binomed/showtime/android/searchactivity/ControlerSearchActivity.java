package com.binomed.showtime.android.searchactivity;

import java.net.URLEncoder;
import java.util.Calendar;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Location;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.resultsactivity.AndShowTimeResultsActivity;
import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.cst.SpecialChars;

public class ControlerSearchActivity {

	private static final String TAG = "ControlerSearchActivity"; //$NON-NLS-1$

	private AndShowTimeSearchActivity searchActivity;
	private ModelSearchActivity model;
	private AndShowtimeDbAdapter mDbHelper;

	private static ControlerSearchActivity instance;

	public static ControlerSearchActivity getInstance() {
		if (instance == null) {
			instance = new ControlerSearchActivity();
		}
		return instance;
	}

	private ControlerSearchActivity() {
		super();
	}

	public void registerView(AndShowTimeSearchActivity nearActivity) {
		this.searchActivity = nearActivity;
		initDB();
	}

	public ModelSearchActivity getModelNearActivity() {
		if (model == null) {
			model = new ModelSearchActivity();
		}
		return model;
	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public void openResultActivity() {
		Location gpsLocation = model.getLocalisation();
		String cityName = model.getCityName();
		String movieName = model.getMovieName();
		String lastCityName = model.getLastRequestCity();
		String lastMovieName = model.getLastRequestMovie();
		String theaterId = model.getFavTheaterId();
		boolean nullResult = model.isNullResult();
		int day = model.getDay();
		boolean forceRequest = false;

		Calendar today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_MONTH, day);
		Calendar calendarLastRequest = model.getLastRequestDate();
		if (calendarLastRequest != null) {
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

				forceRequest = true;
			} else {
				// On a eu un changement de nom de ville ou de nom de film
				forceRequest = (lastCityName != null && cityName != null && !lastCityName.equals(cityName)) //
						|| (lastCityName == null && cityName != null) //
						|| (lastMovieName != null && movieName != null && !lastMovieName.equals(movieName)) //
						|| (lastMovieName == null && movieName != null) //
						|| (lastMovieName != null && movieName == null) //
				;
			}
		} else {
			forceRequest = true;
		}

		forceRequest = forceRequest || nullResult;

		if ((cityName != null) && (cityName.length() > 0)) {
			model.getRequestList().add(cityName);
		}
		if ((movieName != null) && (movieName.length() > 0)) {
			model.getRequestMovieList().add(movieName);
		}

		try {
			model.setLastRequestCity(cityName);
			model.setLastRequestMovie(movieName);
			model.setLastRequestTheaterId(theaterId);
			model.setLastRequestDate(today);

			AndShowtimeFactory.initGeocoder(searchActivity);
			Intent intentResultActivity = new Intent(searchActivity, AndShowTimeResultsActivity.class);

			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, AndShowTimeEncodingUtil.getEncoding()) : cityName));
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_MOVIE_NAME, ((movieName != null) ? URLEncoder.encode(movieName, AndShowTimeEncodingUtil.getEncoding()) : movieName));
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, theaterId);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_DAY, day);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, forceRequest);
			searchActivity.startActivityForResult(intentResultActivity, AndShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY);
		} catch (Exception e) {
			Log.e(TAG, "error before sending search intent", e);
		}
	}

	/*
	 * 
	 * DB
	 */

	public void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new AndShowtimeDbAdapter(searchActivity);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			openDB();

			getModelNearActivity();

			if (mDbHelper.isOpen()) {
				// Init requests
				Cursor cursorRequestHistory = mDbHelper.fetchAllMovieRequest();
				if (cursorRequestHistory.moveToFirst()) {
					int columnIndex = 0;
					model.getRequestList().clear();
					model.getRequestMovieList().clear();
					do {
						columnIndex = cursorRequestHistory.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_CITY_NAME);
						model.getRequestList().add(cursorRequestHistory.getString(columnIndex));
						columnIndex = cursorRequestHistory.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_MOVIE_NAME);
						model.getRequestMovieList().add(cursorRequestHistory.getString(columnIndex));
					} while (cursorRequestHistory.moveToNext());
				}
				cursorRequestHistory.close();

				// else we just look at previous request in order to check it's time
				Cursor cursorLastResult = mDbHelper.fetchLastMovieRequest();
				if (cursorLastResult.moveToFirst()) {
					Calendar calendarLastRequest = Calendar.getInstance();
					long timeLastRequest = cursorLastResult.getLong(cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_TIME));
					calendarLastRequest.setTimeInMillis(timeLastRequest);

					// Init localisation from data base
					Location location = new Location(SpecialChars.EMPTY);
					int columnIndex = cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_LATITUDE);
					location.setLatitude(cursorLastResult.getDouble(columnIndex));
					columnIndex = cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_LONGITUDE);
					location.setLongitude(cursorLastResult.getDouble(columnIndex));

					if (model == null) {
						getModelNearActivity();
					}
					model.setLocalisation(location);
					model.setLastRequestDate(calendarLastRequest);

					columnIndex = cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_CITY_NAME);
					model.setLastRequestCity(cursorLastResult.getString(columnIndex));
					columnIndex = cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_MOVIE_NAME);
					model.setLastRequestMovie(cursorLastResult.getString(columnIndex));
					columnIndex = cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_THEATER_ID);
					model.setLastRequestTheaterId(cursorLastResult.getString(columnIndex));
					columnIndex = cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_NULL_RESULT);
					model.setNullResult(cursorLastResult.getShort(columnIndex) == 1);
				}
				cursorLastResult.close();
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

}
