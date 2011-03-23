package com.binomed.showtime.android.widget.one;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.SQLException;
import android.location.Location;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.AndShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.beans.TheaterBean;

public class ControlerAndShowTimeWidget {
	private static final String TAG = "ControlerWidgetActivity"; //$NON-NLS-1$

	private AndShowTimeWidgetConfigureActivity widgetActivity;
	private ModelAndShowTimeWidget model;
	private AndShowtimeDbAdapter mDbHelper;

	private static ControlerAndShowTimeWidget instance;

	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	public static ControlerAndShowTimeWidget getInstance() {
		if (instance == null) {
			instance = new ControlerAndShowTimeWidget();
		}
		return instance;
	}

	private ControlerAndShowTimeWidget() {
		super();
	}

	public void registerView(AndShowTimeWidgetConfigureActivity widgetActivity) {
		this.widgetActivity = widgetActivity;
		AndShowTimeWidgetHelper.initWidgetId(widgetActivity);
	}

	public ModelAndShowTimeWidget getModelWidgetActivity() {
		if (model == null) {
			model = new ModelAndShowTimeWidget();
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
		boolean forceRequest = false;

		Calendar today = Calendar.getInstance();
		forceRequest = true;

		try {
			AndShowtimeFactory.initGeocoder(widgetActivity);
			Intent intentResultActivity = new Intent(widgetActivity, AndShowTimeResultsWidgetActivity.class);

			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, AndShowTimeEncodingUtil.getEncoding()) : cityName));
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, forceRequest);
			widgetActivity.startActivityForResult(intentResultActivity, widgetActivity.ACTIVITY_OPEN_RESULTS);
		} catch (Exception e) {
			Log.e(TAG, "error before sending search intent", e);
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
			mDbHelper = new AndShowtimeDbAdapter(widgetActivity);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			openDB();

			getModelWidgetActivity();

		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		} finally {
			closeDB();
		}
	}

	/**
	 * /**
	 * 
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
