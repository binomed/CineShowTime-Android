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

package com.binomed.showtime.android.widget;

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
import android.util.Log;
import android.widget.RemoteViews;

import com.binomed.showtime.android.R;
import com.binomed.showtime.android.activity.AndShowTimeMainActivity;
import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.util.AndShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.AndShowtimeRequestManage;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.NearResp;
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
	public static RemoteViews buildUpdate(Context context, Intent intent, TheaterBean theater, Map<MovieBean, Long> movieBeanShowtimes) {

		RemoteViews updateViews = null;
		updateViews = new RemoteViews(context.getPackageName(), R.layout.and_showtime_widget);
		updateViews.setTextViewText(R.id.widget_theater_title, (theater != null) ? theater.getTheaterName() : ""); //$NON-NLS-1$

		HashMap<String, Long> showTimeMap = new HashMap<String, Long>();
		List<MovieBean> movieList = new ArrayList<MovieBean>();
		MovieBean movieBean = null;
		int start = 0;
		int sens = 0;
		if (movieBeanShowtimes.size() == 0) {
			updateViews.setTextViewText(R.id.widget_movie_txt_1, "loading..."); //$NON-NLS-1$
			updateViews.setTextViewText(R.id.widget_movie_hour_1, ""); //$NON-NLS-1$
			updateViews.setTextViewText(R.id.widget_movie_txt_2, ""); //$NON-NLS-1$
			updateViews.setTextViewText(R.id.widget_movie_hour_2, ""); //$NON-NLS-1$
			updateViews.setTextViewText(R.id.widget_movie_txt_3, ""); //$NON-NLS-1$
			updateViews.setTextViewText(R.id.widget_movie_hour_3, ""); //$NON-NLS-1$
		} else {
			for (Entry<MovieBean, Long> movieShowTime : movieBeanShowtimes.entrySet()) {
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
				start = size - 3;
			}
			movieBean = movieList.get(start % size);
			updateViews.setTextViewText(R.id.widget_movie_txt_1, movieBean.getMovieName());
			updateViews.setTextViewText(R.id.widget_movie_hour_1, AndShowtimeDateNumberUtil.showMovieTime(context, showTimeMap.get(movieBean.getId())));

			Intent openMovieIntent1 = new Intent(context, AndShowTimeWidgetServiceOpenMovie1.class);
			openMovieIntent1.putExtra(ParamIntent.MOVIE_ID, movieBean.getId());
			openMovieIntent1.putExtra(ParamIntent.THEATER_ID, theater.getId());
			PendingIntent pendingOpenMovieIntent1 = PendingIntent.getService(context, 0 /* no requestCode */, openMovieIntent1, PendingIntent.FLAG_UPDATE_CURRENT /* no flags */);
			updateViews.setOnClickPendingIntent(R.id.widget_group_movie_1, pendingOpenMovieIntent1);

			if (size >= 2) {
				movieBean = movieList.get((start + 1) % size);
				updateViews.setTextViewText(R.id.widget_movie_txt_2, movieBean.getMovieName());
				updateViews.setTextViewText(R.id.widget_movie_hour_2, AndShowtimeDateNumberUtil.showMovieTime(context, showTimeMap.get(movieBean.getId())));

				Intent openMovieIntent2 = new Intent(context, AndShowTimeWidgetServiceOpenMovie2.class);
				openMovieIntent2.putExtra(ParamIntent.MOVIE_ID, movieBean.getId());
				openMovieIntent2.putExtra(ParamIntent.THEATER_ID, theater.getId());
				PendingIntent pendingOpenMovieIntent2 = PendingIntent.getService(context, 0 /* no requestCode */, openMovieIntent2, PendingIntent.FLAG_UPDATE_CURRENT /* no flags */);
				updateViews.setOnClickPendingIntent(R.id.widget_group_movie_2, pendingOpenMovieIntent2);
			} else {
				updateViews.setTextViewText(R.id.widget_movie_txt_2, "");
				updateViews.setTextViewText(R.id.widget_movie_hour_2, "");

			}
			if (size >= 3) {
				movieBean = movieList.get((start + 2) % size);
				updateViews.setTextViewText(R.id.widget_movie_txt_3, movieBean.getMovieName());
				updateViews.setTextViewText(R.id.widget_movie_hour_3, AndShowtimeDateNumberUtil.showMovieTime(context, showTimeMap.get(movieBean.getId())));

				Intent openMovieIntent3 = new Intent(context, AndShowTimeWidgetServiceOpenMovie3.class);
				openMovieIntent3.putExtra(ParamIntent.MOVIE_ID, movieBean.getId());
				openMovieIntent3.putExtra(ParamIntent.THEATER_ID, theater.getId());
				PendingIntent pendingOpenMovieIntent3 = PendingIntent.getService(context, 0 /* no requestCode */, openMovieIntent3, PendingIntent.FLAG_UPDATE_CURRENT /* no flags */);
				updateViews.setOnClickPendingIntent(R.id.widget_group_movie_3, pendingOpenMovieIntent3);
			} else {
				updateViews.setTextViewText(R.id.widget_movie_txt_3, "");
				updateViews.setTextViewText(R.id.widget_movie_hour_3, "");

			}

			start = start % size;
		}

		Intent defineIntent = new Intent(context, AndShowTimeMainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* no requestCode */, defineIntent, 0 /* no flags */);
		updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

		Intent intentScrollLeft = new Intent(context, AndShowTimeWidgetServiceLeft.class);
		intentScrollLeft.putExtra(ParamIntent.WIDGET_SCROLL_SENS, -3);
		intentScrollLeft.putExtra(ParamIntent.WIDGET_START, start);
		PendingIntent pendingIntentScrollLeft = PendingIntent.getService(context, 0 /* no requestCode */, intentScrollLeft, PendingIntent.FLAG_UPDATE_CURRENT /* no flags */);
		updateViews.setOnClickPendingIntent(R.id.widget_button_scroll_left, pendingIntentScrollLeft);

		Intent intentScrollRight = new Intent(context, AndShowTimeWidgetServiceRight.class);
		intentScrollRight.putExtra(ParamIntent.WIDGET_SCROLL_SENS, 3);
		intentScrollRight.putExtra(ParamIntent.WIDGET_START, start);
		PendingIntent pendingIntentScrollRight = PendingIntent.getService(context, 0 /* no requestCode */, intentScrollRight, PendingIntent.FLAG_UPDATE_CURRENT /* no flags */);
		updateViews.setOnClickPendingIntent(R.id.widget_button_scroll_right, pendingIntentScrollRight);

		Intent intentScrollRefresh = new Intent(context, AndShowTimeWidgetServiceRefresh.class);
		intentScrollRefresh.putExtra(ParamIntent.WIDGET_REFRESH, true);
		PendingIntent pendingIntentScrollRefresh = PendingIntent.getService(context, 0 /* no requestCode */, intentScrollRefresh, PendingIntent.FLAG_UPDATE_CURRENT /* no flags */);
		updateViews.setOnClickPendingIntent(R.id.widget_button_refresh, pendingIntentScrollRefresh);

		return updateViews;
	}

	public static void updateWidget(Context context, Intent intent) {

		AndShowtimeDbAdapter mdbHelper = new AndShowtimeDbAdapter(context);
		try {
			mdbHelper.open();

			Calendar dateLastSearch = Calendar.getInstance();

			TheaterBean theater = AndShowtimeDB2AndShowtimeBeans.extractWidgetTheater(mdbHelper, dateLastSearch);
			Map<MovieBean, Long> movieShowTimeMap = new HashMap<MovieBean, Long>();
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
						NearResp nearResp = AndShowtimeRequestManage.searchTheaters(localisationTheater.getLatitude(), localisationTheater.getLongitude(), localisationTheater.getCityName(), theater.getId(), 0, 0);
						mdbHelper.deleteWidgetShowtime();
						if (nearResp != null && nearResp.getTheaterList() != null) {
							movieBeanList = nearResp.getMapMovies();
							MovieBean movieBean = null;
							for (Entry<String, List<Long>> showTime : nearResp.getTheaterList().get(0).getMovieMap().entrySet()) {
								movieBean = movieBeanList.get(showTime.getKey());
								movieShowTimeMap.put(movieBean, AndShowtimeDateNumberUtil.getMinTime(showTime.getValue()));
								for (Long time : showTime.getValue()) {
									mdbHelper.createWidgetShowtime(movieBean, time);
								}
							}
							mdbHelper.updateWidgetTheater();
						}

					} catch (Exception e) {
						Log.e(TAG, "Error during service widget", e); //$NON-NLS-1$
					}
				} else {
					Map<MovieBean, List<Long>> movieShowtimeMap = AndShowtimeDB2AndShowtimeBeans.extractWidgetShowtimes(mdbHelper);
					for (Entry<MovieBean, List<Long>> movieShowTime : movieShowtimeMap.entrySet()) {
						movieShowTimeMap.put(movieShowTime.getKey(), AndShowtimeDateNumberUtil.getMinTime(movieShowTime.getValue()));
					}
				}
			}

			RemoteViews updateViews = AndShowTimeWidgetHelper.buildUpdate(context, intent, theater, movieShowTimeMap);
			// Push update for this widget to the home screen
			ComponentName thisWidget = new ComponentName(context, AndShowtimeWidget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(thisWidget, updateViews);
		} finally {
			mdbHelper.close();
		}
	}

}
