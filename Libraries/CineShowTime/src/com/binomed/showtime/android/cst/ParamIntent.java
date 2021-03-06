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
package com.binomed.showtime.android.cst;

public interface ParamIntent {

	public static final String MOVIE_ID = "and.binomed.showtime.movie_id"; //$NON-NLS-1$
	public static final String MOVIE = "and.binomed.showtime.movie"; //$NON-NLS-1$
	public static final String THEATER_ID = "and.binomed.showtime.theater_id"; //$NON-NLS-1$
	public static final String THEATER = "and.binomed.showtime.theater"; //$NON-NLS-1$
	public static final String NEAR_RESP = "and.binomed.showtime.near_resp"; //$NON-NLS-1$
	public static final String BUNDLE_SAVE = "and.binomed.showtime.bundle_save"; //$NON-NLS-1$

	//	public static final String ACTIVITY_NEAR_THEATER_ID = "and.binomed.showtime.near.theaterId"; //$NON-NLS-1$
	//	public static final String ACTIVITY_NEAR_LATITUDE = "and.binomed.showtime.near.latitude"; //$NON-NLS-1$
	//	public static final String ACTIVITY_NEAR_LONGITUDE = "and.binomed.showtime.near.longitude"; //$NON-NLS-1$
	//	public static final String ACTIVITY_NEAR_CITY_NAME = "and.binomed.showtime.near.cityName"; //$NON-NLS-1$

	public static final String ACTIVITY_LARGE_SCREEN = "and.binomed.showtime.large_screen"; //$NON-NLS-1$

	public static final String ACTIVITY_MOVIE_LATITUDE = "and.binomed.showtime.movie.latitude"; //$NON-NLS-1$
	public static final String ACTIVITY_MOVIE_LONGITUDE = "and.binomed.showtime.movie.longitude"; //$NON-NLS-1$
	public static final String ACTIVITY_MOVIE_NEAR = "and.binomed.showtime.movie.near"; //$NON-NLS-1$
	public static final String ACTIVITY_MOVIE_FROM_WIDGET = "and.binomed.showtime.movie.fromwidget"; //$NON-NLS-1$

	public static final String ACTIVITY_WIDGET_SHOW_RESULTS = "and.binomed.showtime.widget.show_results"; //$NON-NLS-1$

	public static final String ACTIVITY_SEARCH_LATITUDE = "and.binomed.showtime.search.latitude"; //$NON-NLS-1$
	public static final String ACTIVITY_SEARCH_LONGITUDE = "and.binomed.showtime.search.longitude"; //$NON-NLS-1$
	public static final String ACTIVITY_SEARCH_CITY = "and.binomed.showtime.search.city"; //$NON-NLS-1$
	public static final String ACTIVITY_SEARCH_MOVIE_NAME = "and.binomed.showtime.search.movieName"; //$NON-NLS-1$
	public static final String ACTIVITY_SEARCH_FORCE_REQUEST = "and.binomed.showtime.search.forceRequest"; //$NON-NLS-1$
	public static final String ACTIVITY_SEARCH_THEATER_ID = "and.binomed.showtime.search.theaterId"; //$NON-NLS-1$
	public static final String ACTIVITY_SEARCH_DAY = "and.binomed.showtime.search.day"; //$NON-NLS-1$
	public static final String ACTIVITY_SEARCH_NULL_RESULT = "and.binomed.showtime.search.day"; //$NON-NLS-1$
	public static final String ACTIVITY_SEARCH_GROUP_EXPAND = "and.binomed.showtime.search.group_expand"; //$NON-NLS-1$

	public static final String SERVICE_NEAR_LATITUDE = "and.binomed.showtime.near.service.latitude"; //$NON-NLS-1$
	public static final String SERVICE_NEAR_LONGITUDE = "and.binomed.showtime.near.service.longitude"; //$NON-NLS-1$
	public static final String SERVICE_NEAR_CITY = "and.binomed.showtime.near.service.city"; //$NON-NLS-1$
	public static final String SERVICE_NEAR_THEATER_ID = "and.binomed.showtime.near.service.theaterId"; //$NON-NLS-1$
	public static final String SERVICE_NEAR_DAY = "and.binomed.showtime.near.service.day"; //$NON-NLS-1$
	public static final String SERVICE_NEAR_START = "and.binomed.showtime.near.service.start"; //$NON-NLS-1$
	public static final String SERVICE_NEAR_ORIGIN = "and.binomed.showtime.near.service.origin"; //$NON-NLS-1$

	public static final String SERVICE_SEARCH_LATITUDE = "and.binomed.showtime.search.service.latitude"; //$NON-NLS-1$
	public static final String SERVICE_SEARCH_LONGITUDE = "and.binomed.showtime.search.service.longitude"; //$NON-NLS-1$
	public static final String SERVICE_SEARCH_CITY = "and.binomed.showtime.search.service.city"; //$NON-NLS-1$
	public static final String SERVICE_SEARCH_MOVIE_NAME = "and.binomed.showtime.search.service.movieName"; //$NON-NLS-1$
	public static final String SERVICE_SEARCH_THEATER_ID = "and.binomed.showtime.search.service.theaterId"; //$NON-NLS-1$
	public static final String SERVICE_SEARCH_DAY = "and.binomed.showtime.search.service.day"; //$NON-NLS-1$
	public static final String SERVICE_SEARCH_START = "and.binomed.showtime.search.service.start"; //$NON-NLS-1$
	public static final String SERVICE_SEARCH_ORIGIN = "and.binomed.showtime.search.service.origin"; //$NON-NLS-1$

	public static final String SERVICE_MOVIE_LATITUDE = "and.binomed.showtime.movie.service.latitude"; //$NON-NLS-1$
	public static final String SERVICE_MOVIE_LONGITUDE = "and.binomed.showtime.movie.service.longitude"; //$NON-NLS-1$
	public static final String SERVICE_MOVIE_CITY = "and.binomed.showtime.movie.service.city"; //$NON-NLS-1$
	public static final String SERVICE_MOVIE_NAME = "and.binomed.showtime.movie.service.movieName"; //$NON-NLS-1$
	public static final String SERVICE_MOVIE_THEATER_ID = "and.binomed.showtime.movie.service.theaterId"; //$NON-NLS-1$
	public static final String SERVICE_MOVIE_DAY = "and.binomed.showtime.movie.service.day"; //$NON-NLS-1$

	public static final String SERVICE_MOVIE_ID = "and.binomed.showtime.service.movieId"; //$NON-NLS-1$
	public static final String SERVICE_MOVIE = "and.binomed.showtime.service.movie"; //$NON-NLS-1$
	public static final String SERVICE_MOVIE_NEAR = "and.binomed.showtime.service.near"; //$NON-NLS-1$
	public static final String SERVICE_MOVIE_TRANSLATE = "and.binomed.showtime.service.autoTranslate"; //$NON-NLS-1$

	public static final String SERVICE_DB_TYPE = "and.binomed.showtime.db.service.typeDbAction"; //$NON-NLS-1$
	public static final String SERVICE_DB_DATA = "and.binomed.showtime.db.service.data"; //$NON-NLS-1$

	public static final String SERVICE_DB_TYPE_SAVE = "and.binomed.showtime.db.service.typeSave"; //$NON-NLS-1$
	public static final String SERVICE_DB_SAVE_MOVIE_ID = "and.binomed.showtime.db.service.movieId"; //$NON-NLS-1$
	public static final String SERVICE_DB_VAL_SAVE_ALL = "and.binomed.showtime.db.service.saveAll"; //$NON-NLS-1$
	public static final String SERVICE_DB_VAL_SAVE_MOVIE = "and.binomed.showtime.db.service.saveMovie"; //$NON-NLS-1$
	public static final String SERVICE_DB_VAL_VERSION_CODE = "and.binomed.showtime.db.service.codeVersion"; //$NON-NLS-1$

	public static final String PREFERENCE_RESULT_THEME = "and.binomed.showtime.pref.theme";//$NON-NLS-1$

	public static final String WIDGET_START = "and.binomed.showtime.widget.start"; //$NON-NLS-1$
	public static final String WIDGET_SCROLL_SENS = "and.binomed.showtime.widget.scrollSens"; //$NON-NLS-1$
	public static final String WIDGET_ORIGIN = "and.binomed.showtime.widget.widgetOrigin"; //$NON-NLS-1$
	public static final String WIDGET_REFRESH = "and.binomed.showtime.widget.widgetRefresh"; //$NON-NLS-1$
	public static final String WIDGET_ID = "and.binomed.showtime.widget.widgetId"; //$NON-NLS-1$

}
