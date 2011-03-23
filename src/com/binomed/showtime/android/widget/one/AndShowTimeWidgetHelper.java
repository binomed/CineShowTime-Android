/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.binomed.showtime.android.widget.one;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;
import android.widget.RemoteViews;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.searchnearactivity.AndShowTimeSearchNearActivity;
import com.binomed.showtime.android.service.AndShowDBGlobalService;
import com.binomed.showtime.android.util.AndShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.AndShowtimeRequestManage;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.ProjectionBean;
import com.binomed.showtime.beans.TheaterBean;

/**
 * Helper methods to simplify talking with and parsing responses from a lightweight Wiktionary API. Before making any requests, you should call {@link #prepareUserAgent(Context)} to generate a
 * User-Agent string based on your application package name and version.
 */
public class AndShowTimeWidgetHelper {

	private static final String TAG = "WidgetHelper"; //$NON-NLS-1$

	/**
	 * Build a widget update to show the current Wiktionary "Word of the day." Will block until the online API returns.
	 */
	public static RemoteViews buildUpdate(Context context, Intent intent, TheaterBean theater, Map<MovieBean, ProjectionBean> movieBeanShowtimes) {
		boolean cupcake = true;
		RemoteViews updateViews = null;
		updateViews = new RemoteViews(context.getPackageName(), cupcake ? R.layout.and_showtime_widget_one : R.layout.and_showtime_widget);
		updateViews.setTextViewText(cupcake ? R.id.widget_one_theater_title : R.id.widget_theater_title, (theater != null) ? theater.getTheaterName() : ""); //$NON-NLS-1$

		HashMap<String, ProjectionBean> showTimeMap = new HashMap<String, ProjectionBean>();
		List<MovieBean> movieList = new ArrayList<MovieBean>();
		MovieBean movieBean = null;
		boolean refresh = (intent != null) ? intent.getBooleanExtra(ParamIntent.WIDGET_REFRESH, false) : false;
		int start = 0;
		int sens = 0;

		LocalisationBean localisation = null;
		StringBuilder place = null;
		if (theater != null) {
			if (theater.getPlace() != null) {
				localisation = theater.getPlace();
				place = new StringBuilder();
				if (theater.getPlace().getCityName() != null //
						&& theater.getPlace().getCityName().length() > 0) {
					place.append(theater.getPlace().getCityName());
				}
				if (theater.getPlace().getCountryNameCode() != null //
						&& theater.getPlace().getCountryNameCode().length() > 0 //
						&& place.length() > 0) {
					place.append(", ").append(theater.getPlace().getCountryNameCode()); //$NON-NLS-1$
				}
				if (place.length() == 0) {
					place.append(theater.getPlace().getSearchQuery());
				}

			}
		}

		if (movieBeanShowtimes.size() == 0) {
			updateViews.setTextViewText(cupcake ? R.id.widget_one_movie_txt_1 : R.id.widget_movie_txt_1, context.getResources().getString(((refresh) ? R.string.msgLoading : R.string.msgNoResults)));
			updateViews.setTextViewText(cupcake ? R.id.widget_one_movie_hour_1 : R.id.widget_movie_hour_1, ""); //$NON-NLS-1$
		} else {
			for (Entry<MovieBean, ProjectionBean> movieShowTime : movieBeanShowtimes.entrySet()) {
				movieBean = movieShowTime.getKey();
				movieList.add(movieBean);
				showTimeMap.put(movieBean.getId(), movieShowTime.getValue());
			}
			Collections.sort(movieList, AndShowtimeFactory.getMovieNameComparator());
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
			updateViews.setTextViewText(cupcake ? R.id.widget_one_movie_txt_1 : R.id.widget_movie_txt_1, movieBean.getMovieName());
			ProjectionBean projectionTime = showTimeMap.get(movieBean.getId());
			updateViews.setTextViewText(cupcake ? R.id.widget_one_movie_hour_1 : R.id.widget_movie_hour_1, ((projectionTime.getLang() != null) ? projectionTime.getLang() + " " : "") + AndShowtimeDateNumberUtil.showMovieTime(context, projectionTime.getShowtime()));

			Intent openMovieIntent1 = new Intent(context, AndShowTimeWidgetServiceOpenMovie1.class);
			openMovieIntent1.putExtra(ParamIntent.MOVIE_ID, movieBean.getId());
			openMovieIntent1.putExtra(ParamIntent.THEATER_ID, theater.getId());
			openMovieIntent1.putExtra(ParamIntent.ACTIVITY_MOVIE_NEAR, place.toString());
			PendingIntent pendingOpenMovieIntent1 = PendingIntent.getService(context, 0 /* no requestCode */, openMovieIntent1, PendingIntent.FLAG_UPDATE_CURRENT /* no flags */);
			// PendingIntent pendingOpenMovieIntent1 = PendingIntent.getService(context, 0 /* no requestCode */, openMovieIntent1, 0 /* no flags */);
			updateViews.setOnClickPendingIntent(cupcake ? R.id.widget_one_group_movie_1 : R.id.widget_group_movie_1, pendingOpenMovieIntent1);

			start = start % size;
		}

		Intent defineIntent = new Intent(context, AndShowTimeSearchNearActivity.class);
		if (theater != null) {
			defineIntent.putExtra(ParamIntent.ACTIVITY_NEAR_THEATER_ID, theater.getId());
			if (localisation.getLatitude() != null && localisation.getLongitude() != null) {
				defineIntent.putExtra(ParamIntent.ACTIVITY_NEAR_LATITUDE, localisation.getLatitude());
				defineIntent.putExtra(ParamIntent.ACTIVITY_NEAR_LONGITUDE, localisation.getLongitude());
			} else {
				defineIntent.putExtra(ParamIntent.ACTIVITY_NEAR_CITY_NAME, place.toString());
			}
		}
		// PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* no requestCode */, defineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* no requestCode */, defineIntent, 0);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* no requestCode */, defineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* no requestCode */, defineIntent, 0);
		updateViews.setOnClickPendingIntent(cupcake ? R.id.widget_top_one : R.id.widget, pendingIntent);

		Intent intentScrollLeft = new Intent(context, AndShowTimeWidgetServiceLeft.class);
		intentScrollLeft.putExtra(ParamIntent.WIDGET_SCROLL_SENS, -1);
		intentScrollLeft.putExtra(ParamIntent.WIDGET_START, start);
		intentScrollLeft.putExtra(ParamIntent.WIDGET_CUPCAKE, cupcake);
		PendingIntent pendingIntentScrollLeft = PendingIntent.getService(context, 0 /* no requestCode */, intentScrollLeft, PendingIntent.FLAG_UPDATE_CURRENT /* no flags */);
		updateViews.setOnClickPendingIntent(cupcake ? R.id.widget_one_button_scroll_left : R.id.widget_button_scroll_left, pendingIntentScrollLeft);

		Intent intentScrollRight = new Intent(context, AndShowTimeWidgetServiceRight.class);
		intentScrollRight.putExtra(ParamIntent.WIDGET_SCROLL_SENS, 1);
		intentScrollRight.putExtra(ParamIntent.WIDGET_START, start);
		intentScrollRight.putExtra(ParamIntent.WIDGET_CUPCAKE, cupcake);
		PendingIntent pendingIntentScrollRight = PendingIntent.getService(context, 0 /* no requestCode */, intentScrollRight, PendingIntent.FLAG_UPDATE_CURRENT /* no flags */);
		updateViews.setOnClickPendingIntent(cupcake ? R.id.widget_one_button_scroll_right : R.id.widget_button_scroll_right, pendingIntentScrollRight);

		Intent intentScrollRefresh = new Intent(context, AndShowTimeWidgetServiceRefresh.class);
		intentScrollRefresh.putExtra(ParamIntent.WIDGET_REFRESH, true);
		intentScrollRefresh.putExtra(ParamIntent.WIDGET_CUPCAKE, cupcake);
		PendingIntent pendingIntentScrollRefresh = PendingIntent.getService(context, 0 /* no requestCode */, intentScrollRefresh, PendingIntent.FLAG_UPDATE_CURRENT /* no flags */);
		updateViews.setOnClickPendingIntent(cupcake ? R.id.widget_one_button_refresh : R.id.widget_button_refresh, pendingIntentScrollRefresh);

		return updateViews;
	}

	public static void updateWidget(Context context, Intent intent, TheaterBean theater) {
		Log.i(TAG, "Update Widget");
		AndShowtimeDbAdapter mdbHelper = new AndShowtimeDbAdapter(context);
		try {
			try {
				mdbHelper.open();
			} catch (SQLException e) {
				Log.e(TAG, "error opening database", e);
			}
			Calendar dateLastSearch = Calendar.getInstance();

			if (theater == null && mdbHelper.isOpen()) {
				theater = AndShowtimeDB2AndShowtimeBeans.extractWidgetTheater(mdbHelper, dateLastSearch);
			}
			Map<MovieBean, ProjectionBean> movieShowTimeMap = new HashMap<MovieBean, ProjectionBean>();
			if (theater != null) {

				Calendar dateToday = Calendar.getInstance();

				Map<String, MovieBean> movieBeanList = null;
				boolean refresh = (intent != null) ? intent.getBooleanExtra(ParamIntent.WIDGET_REFRESH, false) : false;
				if (dateToday.get(Calendar.DAY_OF_MONTH) != dateLastSearch.get(Calendar.DAY_OF_MONTH) || refresh) {
					try {
						RemoteViews updateViews = AndShowTimeWidgetHelper.buildUpdate(context, intent, theater, movieShowTimeMap);
						// Push update for this widget to the home screen
						ComponentName thisWidget = new ComponentName(context, AndShowtimeWidget.class);
						AppWidgetManager manager = AppWidgetManager.getInstance(context);
						manager.updateAppWidget(thisWidget, updateViews);

						LocalisationBean localisationTheater = theater.getPlace();
						AndShowtimeFactory.initGeocoder(context);
						NearResp nearResp = AndShowtimeRequestManage.searchTheaters(localisationTheater.getLatitude(), localisationTheater.getLongitude(), localisationTheater.getCityName(), theater.getId(), 0, 0, AndShowTimeWidgetConfigureActivity.class.getName());
						BeanManagerFactory.setNearRespFromWidget(nearResp);
						if (nearResp != null && nearResp.getTheaterList() != null) {
							movieBeanList = nearResp.getMapMovies();
							MovieBean movieBean = null;
							ProjectionBean minTime = null;
							for (Entry<String, List<ProjectionBean>> showTime : nearResp.getTheaterList().get(0).getMovieMap().entrySet()) {
								movieBean = movieBeanList.get(showTime.getKey());
								minTime = AndShowtimeDateNumberUtil.getMinTime(showTime.getValue(), null);
								if (minTime != null) {
									movieShowTimeMap.put(movieBean, minTime);
								}
							}
						}
						Intent intentFillWidget = new Intent(context, AndShowDBGlobalService.class);
						intentFillWidget.putExtra(ParamIntent.SERVICE_DB_TYPE, AndShowtimeCst.DB_TYPE_WIDGET_WRITE_LIST);
						context.startService(intentFillWidget);

					} catch (Exception e) {
						Log.e(TAG, "Error during service widget", e); //$NON-NLS-1$
					}
				} else {
					Map<MovieBean, List<ProjectionBean>> movieShowtimeMap = AndShowtimeDB2AndShowtimeBeans.extractWidgetShowtimes(mdbHelper);
					ProjectionBean minTime = null;
					for (Entry<MovieBean, List<ProjectionBean>> movieShowTime : movieShowtimeMap.entrySet()) {
						minTime = AndShowtimeDateNumberUtil.getMinTime(movieShowTime.getValue(), null);
						if (minTime != null) {
							movieShowTimeMap.put(movieShowTime.getKey(), minTime);
						}
					}
				}
			}

			RemoteViews updateViews = AndShowTimeWidgetHelper.buildUpdate(context, intent, theater, movieShowTimeMap);
			// Push update for this widget to the home screen
			ComponentName thisWidget = new ComponentName(context, AndShowtimeWidget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(thisWidget, updateViews);
		} finally {
			if (mdbHelper.isOpen()) {
				mdbHelper.close();
			}
		}
	}

}
