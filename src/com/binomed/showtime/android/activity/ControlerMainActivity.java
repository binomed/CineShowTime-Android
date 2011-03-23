package com.binomed.showtime.android.activity;

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
import com.binomed.showtime.android.searchmovieactivity.AndShowTimeSearchMovieActivity;
import com.binomed.showtime.android.searchnearactivity.AndShowTimeSearchNearActivity;
import com.binomed.showtime.android.service.AndShowDBGlobalService;
import com.binomed.showtime.android.util.AndShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.TheaterBean;

public class ControlerMainActivity {

	private static final String TAG = "ControlerMainActivity"; //$NON-NLS-1$

	private AndShowTimeMainActivity mainActivity;
	private AndShowtimeDbAdapter mDbHelper;

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
	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public void openSearchNearActivity(TheaterBean theater) {
		Intent intentStartNearActivity = new Intent(mainActivity, AndShowTimeSearchNearActivity.class);
		Bundle extras = new Bundle();
		if (theater != null) {
			extras.putString(ParamIntent.ACTIVITY_NEAR_THEATER_ID, theater.getId());
			intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_NEAR_THEATER_ID, theater.getId());
			if (theater.getPlace() != null) {
				LocalisationBean localisation = theater.getPlace();
				if (localisation.getLatitude() != null && localisation.getLongitude() != null) {
					extras.putDouble(ParamIntent.ACTIVITY_NEAR_LATITUDE, localisation.getLatitude());
					extras.putDouble(ParamIntent.ACTIVITY_NEAR_LONGITUDE, localisation.getLongitude());
					intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_NEAR_LATITUDE, localisation.getLatitude());
					intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_NEAR_LONGITUDE, localisation.getLongitude());
				} else {
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
					extras.putString(ParamIntent.ACTIVITY_NEAR_CITY_NAME, place.toString());
					intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_NEAR_CITY_NAME, place.toString());
				}
			}
		} else {
			extras.putString(ParamIntent.ACTIVITY_NEAR_THEATER_ID, null);
		}
		intentStartNearActivity.replaceExtras(extras);
		mainActivity.startActivityForResult(intentStartNearActivity, mainActivity.ACTIVITY_NEAR);
	}

	public void openSearchMovieActivity() {
		Intent intentStartMovieActivity = new Intent(mainActivity, AndShowTimeSearchMovieActivity.class);

		mainActivity.startActivityForResult(intentStartMovieActivity, mainActivity.ACTIVITY_MOVIE);

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
		List<TheaterBean> theaterList = AndShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);

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
