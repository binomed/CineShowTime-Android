/*
 * Copyright (C) 2011 Binomed (http://blog.binomed.fr)
 *
 * Licensed under the Eclipse Public License - v 1.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC 
 * LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM 
 * CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 */
package com.binomed.showtime.android.widget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsService;
import com.binomed.showtime.android.screen.widget.search.CineShowTimeWidgetConfigureActivity;
import com.binomed.showtime.android.service.CineShowDBGlobalService;
import com.binomed.showtime.android.util.CineShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.android.util.localisation.LocationUtils;

/**
 * Helper methods to simplify talking with and parsing responses from a lightweight Wiktionary API. Before making any requests, you should call {@link #prepareUserAgent(Context)} to generate a User-Agent string based on your application package name and version.
 */
public class CineShowTimeWidgetHelper {

	private static final String TAG = "WidgetHelper"; //$NON-NLS-1$

	private static int mAppWidgetId;

	/**
	 * Build a widget update to show the current Wiktionary "Word of the day." Will block until the online API returns.
	 */
	public static RemoteViews showError(Context context) {
		RemoteViews updateViews = null;
		updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_one);
		updateViews.setTextViewText(R.id.widget_one_theater_title, context.getText(R.string.errorMsg)); //$NON-NLS-1$
		updateViews.setTextViewText(R.id.widget_one_movie_txt_1, "Widget Error, please remove the widget and re-add it");
		updateViews.setTextViewText(R.id.widget_one_movie_hour_1, ""); //$NON-NLS-1$
		return updateViews;
	}

	/**
	 * Build a widget update to show the current Wiktionary "Word of the day." Will block until the online API returns.
	 */
	public static RemoteViews buildUpdate(Context context, Intent intent, TheaterBean theater, Map<MovieBean, List<ProjectionBean>> movieBeanShowtimes, int widgetId) {
		RemoteViews updateViews = null;
		updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_one);
		updateViews.setTextViewText(R.id.widget_one_theater_title, (theater != null) ? theater.getTheaterName() : ""); //$NON-NLS-1$

		HashMap<String, ProjectionBean> showTimeMap = new HashMap<String, ProjectionBean>();
		List<MovieBean> movieList = new ArrayList<MovieBean>();
		MovieBean movieBean = null;
		List<ProjectionBean> projectionList = null;
		ProjectionBean currentProjection = null;
		long currentTime = System.currentTimeMillis();
		boolean refresh = (intent != null) ? intent.getBooleanExtra(ParamIntent.WIDGET_REFRESH, false) : false;
		int start = 0;
		int sens = 0;

		LocalisationBean localisation = null;
		StringBuilder place = null;
		if (theater != null) {
			if (theater.getPlace() != null) {
				localisation = theater.getPlace();
				place = new StringBuilder();
				if ((theater.getPlace().getCityName() != null //
						)
						&& (theater.getPlace().getCityName().length() > 0)) {
					place.append(theater.getPlace().getCityName());
				}
				if ((theater.getPlace().getCountryNameCode() != null //
						)
						&& (theater.getPlace().getCountryNameCode().length() > 0 //
						) && (place.length() > 0)) {
					place.append(", ").append(theater.getPlace().getCountryNameCode()); //$NON-NLS-1$
				}
				if (place.length() == 0) {
					place.append(theater.getPlace().getSearchQuery());
				}

			}
		}

		for (Entry<MovieBean, List<ProjectionBean>> movieShowTime : movieBeanShowtimes.entrySet()) {
			movieBean = movieShowTime.getKey();
			projectionList = movieShowTime.getValue();
			currentProjection = null;
			projectionLabel: for (ProjectionBean projection : projectionList) {
				if (projection.getShowtime() > currentTime) {
					currentProjection = projection;
					break projectionLabel;
				}
			}
			if (currentProjection != null) {
				movieList.add(movieBean);
				showTimeMap.put(movieBean.getId(), currentProjection);
			}
		}
		if (movieList.size() == 0) {
			updateViews.setTextViewText(R.id.widget_one_movie_txt_1, context.getResources().getString(((refresh) ? R.string.msgLoading : R.string.msgNoResults)));
			updateViews.setTextViewText(R.id.widget_one_movie_hour_1, ""); //$NON-NLS-1$
		} else {
			Collections.sort(movieList, CineShowtimeFactory.getMovieNameComparator());
			if (intent != null) {
				start = intent.getIntExtra(ParamIntent.WIDGET_START, 0);
				sens = intent.getIntExtra(ParamIntent.WIDGET_SCROLL_SENS, 0);
			}
			int size = movieList.size();
			start += sens;
			if (start < 0) {
				start = size - 1;
			}
			movieBean = movieList.get(start % size);
			updateViews.setTextViewText(R.id.widget_one_movie_txt_1, movieBean.getMovieName());
			ProjectionBean projectionTime = showTimeMap.get(movieBean.getId());
			updateViews.setTextViewText(R.id.widget_one_movie_hour_1, ((projectionTime.getLang() != null) ? projectionTime.getLang() + " " : "") + CineShowtimeDateNumberUtil.showMovieTime(context, projectionTime.getShowtime(), CineShowtimeDateNumberUtil.isFormat24(context)));

			Intent openMovieIntent1 = new Intent(context, CineShowTimeWidgetServiceOpenMovie1.class);
			openMovieIntent1.putExtra(ParamIntent.MOVIE, movieBean);
			openMovieIntent1.putExtra(ParamIntent.THEATER, theater);
			openMovieIntent1.putExtra(ParamIntent.ACTIVITY_MOVIE_NEAR, place.toString());
			PendingIntent pendingOpenMovieIntent1 = PendingIntent.getService(context, widgetId /*
																								 * no requestCode
																								 */, openMovieIntent1, PendingIntent.FLAG_UPDATE_CURRENT /*
																																						 * no flags
																																						 */);
			updateViews.setOnClickPendingIntent(R.id.widget_one_group_movie_1, pendingOpenMovieIntent1);

			start = start % size;

			Intent intentCurrentMovie = new Intent(context, CineShowDBGlobalService.class);
			intentCurrentMovie.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_CURENT_MOVIE_WRITE);
			intentCurrentMovie.putExtra(ParamIntent.THEATER_ID, theater.getId());
			intentCurrentMovie.putExtra(ParamIntent.MOVIE_ID, movieBean.getId());
			intentCurrentMovie.putExtra(ParamIntent.WIDGET_ID, String.valueOf(widgetId));

			context.startService(intentCurrentMovie);
		}

		Intent defineIntent = new Intent(context, CineShowTimeWidgetServiceOpenResults.class);
		if (theater != null) {
			defineIntent.putExtra(ParamIntent.WIDGET_ID, theater.getId());
			defineIntent.putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, theater.getId());
			if ((localisation.getLatitude() != null) && (localisation.getLongitude() != null)) {
				defineIntent.putExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, localisation.getLatitude());
				defineIntent.putExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, localisation.getLongitude());
			}
			defineIntent.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, place.toString());
		}
		PendingIntent pendingIntent = PendingIntent.getService(context, widgetId /*
																				 * no requestCode
																				 */, defineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		updateViews.setOnClickPendingIntent(R.id.widget_top_one, pendingIntent);

		Intent intentScrollLeft = new Intent(context, CineShowTimeWidgetServiceLeft.class);
		intentScrollLeft.putExtra(ParamIntent.WIDGET_SCROLL_SENS, -1);
		intentScrollLeft.putExtra(ParamIntent.WIDGET_START, start);
		intentScrollLeft.putExtra(ParamIntent.WIDGET_ID, widgetId);
		PendingIntent pendingIntentScrollLeft = PendingIntent.getService(context, widgetId /*
																							 * no requestCode
																							 */, intentScrollLeft, PendingIntent.FLAG_UPDATE_CURRENT /*
																																					 * no flags
																																					 */);
		updateViews.setOnClickPendingIntent(R.id.widget_one_button_scroll_left, pendingIntentScrollLeft);

		Intent intentScrollRight = new Intent(context, CineShowTimeWidgetServiceRight.class);
		intentScrollRight.putExtra(ParamIntent.WIDGET_SCROLL_SENS, 1);
		intentScrollRight.putExtra(ParamIntent.WIDGET_START, start);
		intentScrollRight.putExtra(ParamIntent.WIDGET_ID, widgetId);
		PendingIntent pendingIntentScrollRight = PendingIntent.getService(context, widgetId /*
																							 * no requestCode
																							 */, intentScrollRight, PendingIntent.FLAG_UPDATE_CURRENT /*
																																					 * no flags
																																					 */);
		updateViews.setOnClickPendingIntent(R.id.widget_one_button_scroll_right, pendingIntentScrollRight);

		Intent intentScrollRefresh = new Intent(context, CineShowTimeWidgetServiceRefresh.class);
		intentScrollRefresh.putExtra(ParamIntent.WIDGET_REFRESH, true);
		intentScrollRefresh.putExtra(ParamIntent.WIDGET_ID, widgetId);
		PendingIntent pendingIntentScrollRefresh = PendingIntent.getService(context, widgetId /*
																							 * no requestCode
																							 */, intentScrollRefresh, PendingIntent.FLAG_UPDATE_CURRENT /*
																																						 * no flags
																																						 */);
		updateViews.setOnClickPendingIntent(R.id.widget_one_button_refresh, pendingIntentScrollRefresh);

		return updateViews;
	}

	public static void updateWidget(Context context, Intent intent, TheaterBean theater, int widgetId) {
		Log.i(TAG, "Update Widget");
		CineShowtimeDbAdapter mdbHelper = new CineShowtimeDbAdapter(context);
		try {
			try {
				mdbHelper.open();
			} catch (SQLException e) {
				Log.e(TAG, "error opening database", e);
			}
			Calendar dateLastSearch = Calendar.getInstance();

			if ((theater == null) && mdbHelper.isOpen()) {
				theater = CineShowtimeDB2AndShowtimeBeans.extractWidgetTheater(mdbHelper, dateLastSearch, widgetId);
			}
			Map<MovieBean, List<ProjectionBean>> movieShowTimeMap = new HashMap<MovieBean, List<ProjectionBean>>();
			Log.i(TAG, "Theater != null : " + (theater != null));
			if (theater != null) {

				Calendar dateToday = Calendar.getInstance();

				boolean refresh = (intent != null) ? intent.getBooleanExtra(ParamIntent.WIDGET_REFRESH, false) : false;
				if ((dateToday.get(Calendar.DAY_OF_MONTH) != dateLastSearch.get(Calendar.DAY_OF_MONTH)) || refresh) {
					Log.i(TAG, "Force refresh " + refresh + ", " + (dateToday.get(Calendar.DAY_OF_MONTH) != dateLastSearch.get(Calendar.DAY_OF_MONTH)) + ", today : " + dateToday.get(Calendar.DAY_OF_MONTH) + ", last day : " + dateLastSearch.get(Calendar.DAY_OF_MONTH));
					try {
						RemoteViews updateViews = CineShowTimeWidgetHelper.buildUpdate(context, intent, theater, movieShowTimeMap, widgetId);
						// Push update for this widget to the home screen
						AppWidgetManager manager = AppWidgetManager.getInstance(context);
						manager.updateAppWidget(widgetId, updateViews);

						Intent intentSearchDatas = new Intent(context, CineShowTimeResultsService.class);

						CineShowtimeFactory.initGeocoder(context);
						LocalisationBean localisationTheater = theater.getPlace();
						intentSearchDatas.putExtra(ParamIntent.SERVICE_SEARCH_CITY, localisationTheater.getCityName());
						intentSearchDatas.putExtra(ParamIntent.SERVICE_SEARCH_ORIGIN, CineShowTimeWidgetConfigureActivity.class.getName());
						intentSearchDatas.putExtra(ParamIntent.SERVICE_SEARCH_THEATER_ID, theater.getId());
						intentSearchDatas.putExtra(ParamIntent.SERVICE_SEARCH_DAY, 0);
						intentSearchDatas.putExtra(ParamIntent.SERVICE_SEARCH_START, 0);
						intentSearchDatas.putExtra(ParamIntent.WIDGET_ID, widgetId);
						context.startService(intentSearchDatas);

					} catch (Exception e) {
						Log.e(TAG, "Error during service widget", e); //$NON-NLS-1$
					}
				} else {
					Map<MovieBean, List<ProjectionBean>> movieShowtimeMap = CineShowtimeDB2AndShowtimeBeans.extractWidgetShowtimes(mdbHelper, widgetId);
					Log.i(TAG, "Extract datas from base : " + movieShowtimeMap.size());
					// ProjectionBean minTime = null;
					for (Entry<MovieBean, List<ProjectionBean>> movieShowTime : movieShowtimeMap.entrySet()) {
						// minTime = CineShowtimeDateNumberUtil.getMinTime(movieShowTime.getValue(), null);
						// if (minTime != null) {
						movieShowTimeMap.put(movieShowTime.getKey(), movieShowTime.getValue());
						// }
					}

					RemoteViews updateViews = CineShowTimeWidgetHelper.buildUpdate(context, intent, theater, movieShowTimeMap, widgetId);
					// Push update for this widget to the home screen
					AppWidgetManager manager = AppWidgetManager.getInstance(context);
					manager.updateAppWidget(widgetId, updateViews);
				}
			}

		} finally {
			if (mdbHelper.isOpen()) {
				mdbHelper.close();
			}
		}
	}

	/*
	 * 
	 * Widget
	 */

	public static void initWidgetId(Activity activity) {
		Intent intent = activity.getIntent();
		// Find the widget id from the intent.
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			activity.finish();
		}
	}

	public static void finalizeWidget(Activity activity, TheaterBean theater, String cityName) {
		final Context context = activity;

		initWidgetId(activity);

		// We fill db
		Intent intentWidgetDb = new Intent(activity, CineShowDBGlobalService.class);
		intentWidgetDb.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_WIDGET_WRITE);
		theater.setWidgetId(mAppWidgetId);
		intentWidgetDb.putExtra(ParamIntent.SERVICE_DB_DATA, theater);
		if (LocationUtils.isEmptyLocation(theater.getPlace())) {
			LocalisationBean place = theater.getPlace();
			if (theater.getPlace() == null) {
				place = new LocalisationBean();
				theater.setPlace(place);
			}
			place.setCityName(cityName);
		}
		activity.startService(intentWidgetDb);

		// Make sure we pass back the original appWidgetId
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		activity.setResult(Activity.RESULT_OK, resultValue);
		activity.finish();

		// We force widget to refresh
		Intent intentRefreshWidget = new Intent(activity, CineShowTimeWidgetHelper.class);
		intentRefreshWidget.putExtra(ParamIntent.WIDGET_REFRESH, true);
		CineShowTimeWidgetHelper.updateWidget(context, intentRefreshWidget, theater, mAppWidgetId);

	}

}
