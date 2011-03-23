package com.binomed.showtime.android.activity;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.resultsactivity.AndShowTimeResultsActivity;
import com.binomed.showtime.android.searchactivity.AndShowTimeSearchActivity;
import com.binomed.showtime.android.service.AndShowDBGlobalService;
import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.AndShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.TheaterBean;

public class ControlerMainActivity {

	private static final String TAG = "ControlerMainActivity"; //$NON-NLS-1$

	private AndShowTimeMainActivity mainActivity;
	private AndShowtimeDbAdapter mDbHelper;
	private ModelMainActivity model;

	private static ControlerMainActivity instance;

	public static synchronized ControlerMainActivity getInstance() {
		if (instance == null) {
			instance = new ControlerMainActivity();
		}
		return instance;
	}

	private ControlerMainActivity() {
		super();
	}

	public void registerView(AndShowTimeMainActivity mainActivity) {
		this.mainActivity = mainActivity;
		initDB();
	}

	public ModelMainActivity getModel() {
		if (this.model == null) {
			this.model = new ModelMainActivity();
		}
		return this.model;
	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public void openSearchActivity(TheaterBean theater) {
		Intent intentStartNearActivity = new Intent(mainActivity, AndShowTimeSearchActivity.class);
		Bundle extras = new Bundle();
		if (theater != null) {
			extras.putString(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, theater.getId());
			intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, theater.getId());
			if (theater.getPlace() != null) {
				LocalisationBean localisation = theater.getPlace();
				if (localisation.getLatitude() != null && localisation.getLongitude() != null) {
					extras.putDouble(ParamIntent.ACTIVITY_SEARCH_LATITUDE, localisation.getLatitude());
					extras.putDouble(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, localisation.getLongitude());
					intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, localisation.getLatitude());
					intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, localisation.getLongitude());
				}
				// else {
				StringBuilder place = new StringBuilder();
				if (theater.getPlace().getCityName() != null //
						&& theater.getPlace().getCityName().length() > 0) {
					place.append(theater.getPlace().getCityName());
				}
				if (theater.getPlace().getCountryNameCode() != null //
						&& theater.getPlace().getCountryNameCode().length() > 0 //
						&& place.length() > 0) {
					place.append(", ").append(theater.getPlace().getCountryNameCode()); //$NON-NLS-1$
				}
				extras.putString(ParamIntent.ACTIVITY_SEARCH_CITY, place.toString());
				intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, place.toString());
				// }
			}
		} else {
			extras.putString(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, null);
		}
		intentStartNearActivity.replaceExtras(extras);
		mainActivity.startActivityForResult(intentStartNearActivity, AndShowtimeCst.ACTIVITY_RESULT_SEARCH_ACTIVITY);
	}

	public void openResultsActivity(TheaterBean theaterBean) {
		openDB();

		try {
			String cityName = theaterBean.getPlace().getCityName();
			if (theaterBean.getPlace().getCountryNameCode() != null) {
				cityName += ", " + theaterBean.getPlace().getCountryNameCode();
			}
			String theaterId = theaterBean.getId();
			boolean forceRequest = false;

			Calendar today = Calendar.getInstance();
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
					Cursor cursorInResults = null;
					if (mDbHelper.isOpen()) {
						cursorInResults = mDbHelper.fetchInResults(theaterBean);
						forceRequest = !cursorInResults.moveToFirst();
					}
				}
			} else {
				forceRequest = true;
			}

			model.setLastRequestDate(today);

			AndShowtimeFactory.initGeocoder(mainActivity);
			Intent intentResultActivity = new Intent(mainActivity, AndShowTimeResultsActivity.class);

			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, AndShowTimeEncodingUtil.getEncoding()) : cityName));
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, theaterId);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_DAY, 0);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, forceRequest);
			mainActivity.startActivityForResult(intentResultActivity, AndShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY);

		} catch (Exception e) {
			Log.e(TAG, "Error during open results activity", e);
		} finally {
			closeDB();
		}

	}

	/*
	 * 
	 * DB
	 */

	/**
	 * 
	 */
	public void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new AndShowtimeDbAdapter(mainActivity);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			openDB();

			getModel();

			if (mDbHelper.isOpen()) {
				// else we just look at previous request in order to check it's time
				Cursor cursorLastResult = mDbHelper.fetchLastMovieRequest();
				if (cursorLastResult.moveToFirst()) {
					Calendar calendarLastRequest = Calendar.getInstance();
					long timeLastRequest = cursorLastResult.getLong(cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_TIME));
					calendarLastRequest.setTimeInMillis(timeLastRequest);

					model.setLastRequestDate(calendarLastRequest);
					model.setNullResult(cursorLastResult.getShort(cursorLastResult.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_REQUEST_NULL_RESULT)) == 1);
				}
				cursorLastResult.close();
			}
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		} finally {
			closeDB();
		}
	}

	/**
	 * @return
	 */
	public boolean showLastChange() {
		openDB();
		boolean result = false;
		Cursor cursorLastChange = mDbHelper.fetchLastChange();
		int versionCode = -1;
		try {
			PackageInfo pi = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (Exception e) {
			Log.e(TAG, "Error getting package for activity", e); //$NON-NLS-1$
		}
		if (cursorLastChange != null) {
			try {
				if (cursorLastChange.moveToFirst()) {
					int columnIndex = cursorLastChange.getColumnIndex(AndShowtimeDbAdapter.KEY_LAST_CHANGE_VERSION);
					int codeVersion = cursorLastChange.getInt(columnIndex);

					result = codeVersion != versionCode;

				} else {
					result = true;
				}
			} finally {
				cursorLastChange.close();
				closeDB();
			}
		} else {
			result = true;
		}

		if (result) {
			Intent intentService = new Intent(mainActivity, AndShowDBGlobalService.class);
			intentService.putExtra(ParamIntent.SERVICE_DB_TYPE, AndShowtimeCst.DB_TYPE_LAST_CHANGE_WRITE);
			intentService.putExtra(ParamIntent.SERVICE_DB_VAL_VERSION_CODE, versionCode);
			mainActivity.startService(intentService);
		}

		return result;
	}

	/**
	 * @return
	 */
	public List<TheaterBean> getFavTheater() {
		openDB();
		List<TheaterBean> theaterList = null;
		try {
			theaterList = AndShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);
		} catch (Exception e) {
			Log.e(TAG, "Error during getting fav", e);
		} finally {
			closeDB();
		}

		return theaterList;
	}

	/**
	 * 
	 */
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

	public void removeFavorite(TheaterBean theaterBean) {
		try {
			if (mDbHelper.isOpen()) {
				mDbHelper.deleteFavorite(theaterBean.getId());
			}
		} catch (Exception e) {
			Log.e(TAG, "error removing theater from fav", e);
		}

	}

}
