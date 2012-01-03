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

public final class CineShowtimeCst {

	public static final boolean TRACE_VIEW_ENABLE = false;

	//	public static final String COLOR_GREY = "#606060"; //$NON-NLS-1$
	//	public static final String COLOR_WHITE = "#FFFFFF"; //$NON-NLS-1$
	public static final String COLOR_SUB_MAIN_INFO_LIGHT = "#000000"; //$NON-NLS-1$
	public static final String COLOR_SUB_MAIN_INFO_DARK = "#ffffff"; //$NON-NLS-1$
	public static final String COLOR_TIME_OR_DISTANCE_LIGHT = "#3C3C3C"; //$NON-NLS-1$
	public static final String COLOR_TIME_OR_DISTANCE_DARK = "#aaaaaa"; //$NON-NLS-1$
	public static final String COLOR_LANG_LIGHT = "#000000"; //$NON-NLS-1$
	public static final String COLOR_LANG_DARK = "#ffffff"; //$NON-NLS-1$
	public static final String COLOR_PASSED_SHOWTIME_LIGHT = "#D2D2D2"; //$NON-NLS-1$
	public static final String COLOR_PASSED_SHOWTIME_DARK = "#3C3C3C"; //$NON-NLS-1$
	public static final String COLOR_NEAREST_SHOWTIME_LIGHT = "#000000"; //$NON-NLS-1$
	public static final String COLOR_NEAREST_SHOWTIME_DARK = "#FFFFFF"; //$NON-NLS-1$
	public static final String COLOR_NEXT_SHOWTIME_LIGHT = "#101010"; //$NON-NLS-1$
	public static final String COLOR_NEXT_SHOWTIME_DARK = "#D2D2D2"; //$NON-NLS-1$

	public static final String FOLDER_POSTER = ".cineshowtime/"; //$NON-NLS-1$

	public static final int ACTIVITY_RESULT_PREFERENCES = 1;
	public static final int ACTIVITY_RESULT_SEARCH_ACTIVITY = 2;
	public static final int ACTIVITY_RESULT_RESULT_ACTIVITY = 3;
	public static final int ACTIVITY_RESULT_MOVIE_ACTIVITY = 4;
	public static final int ACTIVITY_RESULT_MOVIE_SPEECH_SEARCH = 5;
	public static final int ACTIVITY_RESULT_CITY_SPEECH_SEARCH = 6;

	public static final int ACTIVITY_RESULT_PARAM_NULL_RESULT = 1;

	public static final int SORT_THEATER_NAME = 0;
	public static final int SORT_THEATER_DISTANCE = 1;
	public static final int SORT_SHOWTIME = 2;
	public static final int SORT_MOVIE_NAME = 3;

	public static final int DB_TYPE_NEAR_RESP_WRITE = 0;
	public static final int DB_TYPE_MOVIE_RESP_WRITE = 1;
	public static final int DB_TYPE_MOVIE_WRITE = 2;
	public static final int DB_TYPE_WIDGET_WRITE = 3;
	public static final int DB_TYPE_WIDGET_WRITE_LIST = 4;
	public static final int DB_TYPE_FAV_WRITE = 5;
	public static final int DB_TYPE_FAV_DELETE = 6;
	public static final int DB_TYPE_CURENT_MOVIE_WRITE = 7;
	public static final int DB_TYPE_SKYHOOK_REGISTRATION = 8;
	public static final int DB_TYPE_LAST_CHANGE_WRITE = 9;
	public static final int DB_TYPE_WIDGET_DELETE = 10;
	public static final int DB_TYPE_LOCATION_WRITE = 11;

	public static final int RESULT_PREF_WITH_NEW_THEME = 1;

	public static final String CONTACTS_PACKAGE = "com.android.contacts"; //$NON-NLS-1$
	public static final String CONTACTS_NAME = "com.android.contacts.DialtactsActivity"; //$NON-NLS-1$
	public static final String MAPS_PACKAGE = "com.google.android.apps.maps"; //$NON-NLS-1$
	public static final String MAPS_NAME = "com.google.android.maps.MapsActivity"; //$NON-NLS-1$
	public static final String CALENDAR_PACKAGE_OLD = "com.android.calendar"; //$NON-NLS-1$
	public static final String CALENDAR_PACKAGE = "com.google.android.calendar"; //$NON-NLS-1$
	public static final String CALENDAR_NAME_OLD = "com.android.calendar.LaunchActivity"; //$NON-NLS-1$
	public static final String CALENDAR_NAME = "com.android.calendar.LaunchActivity"; //$NON-NLS-1$

	public static final String GOOGLE_ANALYTICS_ID = "UA-21310883-3";

	public static final String PREF_KEY_ANALYTICS = "com.binomed.showtime.analytics";
	public static final String PREF_KEY_APP_ENGINE = "com.binomed.showtime.appengine";

	public static final String ACRA_FORM_KEY = "dEtLdVJ5aFNjOUFRcGI5Ym5TckhwaVE6MQ";

	public static final String ANALYTICS_CATEGORY_APPLICATION = "Application";
	public static final String ANALYTICS_CATEGORY_ACTIVITY = "Activity";
	public static final String ANALYTICS_CATEGORY_WIDGET = "Widget";
	public static final String ANALYTICS_CATEGORY_SEARCH = "Search";
	public static final String ANALYTICS_CATEGORY_RESULT = "Results";
	public static final String ANALYTICS_CATEGORY_MOVIE = "Movie";
	public static final String ANALYTICS_CATEGORY_FAV = "Favorites";
	public static final String ANALYTICS_CATEGORY_ERROR = "Error";

	public static final String ANALYTICS_ACTION_VERSION = "Version";
	public static final String ANALYTICS_ACTION_ANDROID_VERSION = "AndroidVersion";
	public static final String ANALYTICS_ACTION_ANDROID_RELEASE = "AndroidVersionRelease";
	public static final String ANALYTICS_ACTION_ANDROID_SDK = "AndroidSDK";
	public static final String ANALYTICS_ACTION_APP_ENGINE_VERSION = "AppEngineVersion";
	public static final String ANALYTICS_ACTION_TABLET = "Tablet";
	public static final String ANALYTICS_ACTION_OPEN = "Open";
	public static final String ANALYTICS_ACTION_TIME = "Time";
	public static final String ANALYTICS_ACTION_GPS = "GpsAction";
	public static final String ANALYTICS_ACTION_CANCEL_SERVICE = "CancelService";
	public static final String ANALYTICS_ACTION_CHANGE_THEME = "ChangeTheme";
	public static final String ANALYTICS_ACTION_RETURN_HOME = "ReturnHome";
	public static final String ANALYTICS_ACTION_CHOOSE = "Choose";
	public static final String ANALYTICS_ACTION_INTERACTION = "Interaction";
	public static final String ANALYTICS_ACTION_RESULTS = "Results";
	public static final String ANALYTICS_ACTION_MOVIE_ACTIONS = "QuickActions";

	public static final int ANALYTICS_VALUE_THEME_BLACK = -1;
	public static final int ANALYTICS_VALUE_THEME_LIGHT = 1;

	public static final String ANALYTICS_VALUE_WIDGET_SEARCH = "From search";
	public static final String ANALYTICS_VALUE_WIDGET_FAV = "From fav";

	public static final String ANALYTICS_VALUE_WIDGET_LEFT = "Action Left";
	public static final String ANALYTICS_VALUE_WIDGET_RIGHT = "Action Right";
	public static final String ANALYTICS_VALUE_WIDGET_REFRESH = "Action Refresh";
	public static final String ANALYTICS_VALUE_WIDGET_OPEN_MOVIE = "Action Open movie";
	public static final String ANALYTICS_VALUE_WIDGET_OPEN_THEATER = "Action Open theater";

	public static final String ANALYTICS_VALUE_SEARCH_USE_VOICE_SEARCH = "Use voice search";

	public static final String ANALYTICS_LABEL_PREFERENCE = "Preferences";
	public static final String ANALYTICS_LABEL_ABOUT = "About";
	public static final String ANALYTICS_LABEL_HELP = "Help";

	public static final String ANALYTICS_LABEL_SEARCH_GPS = "Gps : ";
	public static final String ANALYTICS_LABEL_SEARCH_DAY = "Day : ";
	public static final String ANALYTICS_LABEL_SEARCH_CITY = "City : ";
	public static final String ANALYTICS_LABEL_SEARCH_MOVIE = "Movie : ";
	public static final String ANALYTICS_LABEL_SEARCH_FORCE_REQUEST = "Force Request : ";

	public static final String ANALYTICS_LABEL_SEARCH_GPS_USE = "Use of Gps";
	public static final String ANALYTICS_LABEL_SEARCH_GPS_PROVIDER = "Provider";

	public static final String ANALYTICS_LABEL_RESULTS_SORT = "Use sort : ";
	public static final String ANALYTICS_LABEL_RESULTS_MORE_RESULTS = "Use more results";
	public static final String ANALYTICS_LABEL_RESULTS_FAV = "Fav : ";

	public static final String ANALYTICS_LABEL_MOVIE_REUSE = "ReUse movie in base : ";
	public static final String ANALYTICS_LABEL_MOVIE_GOTO_TAB = "Goto tab : ";
	public static final String ANALYTICS_LABEL_MOVIE_GOTO_TAB_WITH_SLIDE = "Goto tab with slide : ";
	public static final String ANALYTICS_LABEL_MOVIE_INVOCKE_QUICKACTIONS = "Use Quick Actions";
	public static final String ANALYTICS_LABEL_MOVIE_ACTIONS_RESERVATION = "Click on reservation";
	public static final String ANALYTICS_LABEL_MOVIE_ACTIONS_CALENDAR = "Click on calendar";
	public static final String ANALYTICS_LABEL_MOVIE_ACTIONS_SMS = "Click on sms";
	public static final String ANALYTICS_LABEL_MOVIE_ACTIONS_MAIL = "Click on mail";
	public static final String ANALYTICS_LABEL_MOVIE_ACTIONS_CALL = "Click on call";
	public static final String ANALYTICS_LABEL_MOVIE_ACTIONS_MAP = "Click on map";
	public static final String ANALYTICS_LABEL_MOVIE_ACTIONS_DIRECTIONS = "Click on directions";
	public static final String ANALYTICS_LABEL_MOVIE_ACTIONS_TRAILER = "Click on trailer";

	public static final String ANALYTICS_LABEL_FAV_REMOVE = "Remove fav";
	public static final String ANALYTICS_LABEL_FAV_CLICK = "Click on fav";

}
