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
package com.binomed.showtime.android.adapter.db;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.ReviewBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.model.YoutubeBean;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations for the notepad example, and gives the ability to list all notes as well as retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the addition of better error handling and also using returning a Cursor instead of using a collection of inner classes (which is less scalable and not recommended).
 */
public class CineShowtimeDbAdapter {

	public static final String KEY_THEATER_NAME = "theater_name"; //$NON-NLS-1$
	public static final String KEY_THEATER_PHONE = "phone_number"; //$NON-NLS-1$
	public static final String KEY_THEATER_ID = "_id"; //$NON-NLS-1$

	public static final String KEY_MOVIE_ID = "_id"; //$NON-NLS-1$
	public static final String KEY_MOVIE_CID = "cid"; //$NON-NLS-1$
	public static final String KEY_MOVIE_IMDB_ID = "imdb_id"; //$NON-NLS-1$
	public static final String KEY_MOVIE_NAME = "movie_name"; //$NON-NLS-1$
	public static final String KEY_MOVIE_ENGLISH_NAME = "movie_english_name"; //$NON-NLS-1$
	public static final String KEY_MOVIE_IMG_URL = "url_img"; //$NON-NLS-1$
	public static final String KEY_MOVIE_WIKIPEDIA_URL = "url_wikipedia"; //$NON-NLS-1$
	public static final String KEY_MOVIE_IMDB_DESC = "imdb_desc"; //$NON-NLS-1$
	public static final String KEY_MOVIE_DESC = "desc"; //$NON-NLS-1$
	public static final String KEY_MOVIE_TR_DESC = "trDesc"; //$NON-NLS-1$
	public static final String KEY_MOVIE_TIME = "movie_time"; //$NON-NLS-1$
	public static final String KEY_MOVIE_TIME_FORMAT = "movie_time_format"; //$NON-NLS-1$
	public static final String KEY_MOVIE_STYLE = "style"; //$NON-NLS-1$
	public static final String KEY_MOVIE_RATE = "rate"; //$NON-NLS-1$
	public static final String KEY_MOVIE_LANG = "lang"; //$NON-NLS-1$
	public static final String KEY_MOVIE_ACTORS = "actors"; //$NON-NLS-1$
	public static final String KEY_MOVIE_DIRECTORS = "directors"; //$NON-NLS-1$

	public static final String KEY_LOCALISATION_THEATER_ID = "theater_id"; //$NON-NLS-1$
	public static final String KEY_LOCALISATION_CITY_NAME = "city_name"; //$NON-NLS-1$
	public static final String KEY_LOCALISATION_COUNTRY_NAME = "country_name"; //$NON-NLS-1$
	public static final String KEY_LOCALISATION_COUNTRY_CODE = "country_code"; //$NON-NLS-1$
	public static final String KEY_LOCALISATION_DISTANCE = "distance"; //$NON-NLS-1$
	public static final String KEY_LOCALISATION_DISTANCE_TIME = "distance_time"; //$NON-NLS-1$
	public static final String KEY_LOCALISATION_LATITUDE = "latitude"; //$NON-NLS-1$
	public static final String KEY_LOCALISATION_LONGITUDE = "longitude"; //$NON-NLS-1$
	public static final String KEY_LOCALISATION_POSTAL_CODE = "postal_code"; //$NON-NLS-1$
	public static final String KEY_LOCALISATION_SEARCH_QUERY = "search_query"; //$NON-NLS-1$

	public static final String KEY_SHOWTIME_ID = "_id"; //$NON-NLS-1$
	public static final String KEY_SHOWTIME_THEATER_ID = "theater_id"; //$NON-NLS-1$
	public static final String KEY_SHOWTIME_MOVIE_ID = "movie_id"; //$NON-NLS-1$
	public static final String KEY_SHOWTIME_TIME = "showtime"; //$NON-NLS-1$
	public static final String KEY_SHOWTIME_LANG = "lang"; //$NON-NLS-1$
	public static final String KEY_SHOWTIME_RESERVATION_URL = "reservation_url"; //$NON-NLS-1$
	public static final String KEY_SHOWTIME_FORMAT_24 = "format_24"; //$NON-NLS-1$
	public static final String KEY_SHOWTIME_FORMAT_12 = "format_12"; //$NON-NLS-1$

	public static final String KEY_NEAR_REQUEST_ID = "_id"; //$NON-NLS-1$
	public static final String KEY_NEAR_REQUEST_LATITUDE = "latitude"; //$NON-NLS-1$
	public static final String KEY_NEAR_REQUEST_LONGITUDE = "longitude"; //$NON-NLS-1$
	public static final String KEY_NEAR_REQUEST_CITY_NAME = "city_name"; //$NON-NLS-1$
	public static final String KEY_NEAR_REQUEST_TIME = "time"; //$NON-NLS-1$
	public static final String KEY_NEAR_REQUEST_THEATER_ID = "theater_id"; //$NON-NLS-1$

	public static final String KEY_MOVIE_REQUEST_ID = "_id"; //$NON-NLS-1$
	public static final String KEY_MOVIE_REQUEST_LATITUDE = "latitude"; //$NON-NLS-1$
	public static final String KEY_MOVIE_REQUEST_LONGITUDE = "longitude"; //$NON-NLS-1$
	public static final String KEY_MOVIE_REQUEST_CITY_NAME = "city_name"; //$NON-NLS-1$
	public static final String KEY_MOVIE_REQUEST_MOVIE_NAME = "movie_name"; //$NON-NLS-1$
	public static final String KEY_MOVIE_REQUEST_TIME = "time"; //$NON-NLS-1$
	public static final String KEY_MOVIE_REQUEST_THEATER_ID = "theater_id"; //$NON-NLS-1$
	public static final String KEY_MOVIE_REQUEST_NULL_RESULT = "nullResult"; //$NON-NLS-1$

	public static final String KEY_FAV_TH_THEATER_ID = "theater_id"; //$NON-NLS-1$
	public static final String KEY_FAV_TH_THEATER_NAME = "theater_name"; //$NON-NLS-1$
	public static final String KEY_FAV_TH_THEATER_PLACE = "theater_place_city_name"; //$NON-NLS-1$
	public static final String KEY_FAV_TH_THEATER_COUNRTY_CODE = "theater_place_counry"; //$NON-NLS-1$
	public static final String KEY_FAV_TH_THEATER_POSTAL_CODE = "theater_place_postal_code"; //$NON-NLS-1$
	public static final String KEY_FAV_TH_THEATER_LAT = "theater_place_lat"; //$NON-NLS-1$
	public static final String KEY_FAV_TH_THEATER_LONG = "theater_place_long"; //$NON-NLS-1$

	public static final String KEY_FAV_SHOWTIME_ID = "id"; //$NON-NLS-1$
	public static final String KEY_FAV_SHOWTIME_MOVIE_ID = "movie_id"; //$NON-NLS-1$
	public static final String KEY_FAV_SHOWTIME_THEATER_ID = "theater_id"; //$NON-NLS-1$
	public static final String KEY_FAV_SHOWTIME_TIME = "showtime"; //$NON-NLS-1$
	public static final String KEY_FAV_SHOWTIME_LANG = "lang"; //$NON-NLS-1$
	public static final String KEY_FAV_SHOWTIME_RESERVATION_URL = "reservation_url"; //$NON-NLS-1$

	public static final String KEY_WIDGET_THEATER_WIDGET_ID = "widget_id"; //$NON-NLS-1$
	public static final String KEY_WIDGET_THEATER_ID = "theater_id"; //$NON-NLS-1$
	public static final String KEY_WIDGET_THEATER_NAME = "theater_name"; //$NON-NLS-1$
	public static final String KEY_WIDGET_THEATER_PLACE = "theater_place_city_name"; //$NON-NLS-1$
	public static final String KEY_WIDGET_THEATER_COUNRTY_CODE = "theater_place_counry"; //$NON-NLS-1$
	public static final String KEY_WIDGET_THEATER_POSTAL_CODE = "theater_place_postal_code"; //$NON-NLS-1$
	public static final String KEY_WIDGET_THEATER_LAT = "theater_place_lat"; //$NON-NLS-1$
	public static final String KEY_WIDGET_THEATER_LONG = "theater_place_long"; //$NON-NLS-1$
	public static final String KEY_WIDGET_THEATER_DATE = "date"; //$NON-NLS-1$

	public static final String KEY_WIDGET_MOVIE_WIDGET_ID = "widget_id"; //$NON-NLS-1$
	public static final String KEY_WIDGET_MOVIE_ID = "id"; //$NON-NLS-1$
	public static final String KEY_WIDGET_MOVIE_MID = "movie_id"; //$NON-NLS-1$
	public static final String KEY_WIDGET_MOVIE_NAME = "movie_name"; //$NON-NLS-1$
	public static final String KEY_WIDGET_MOVIE_EN_NAME = "movie_en_name"; //$NON-NLS-1$
	public static final String KEY_WIDGET_MOVIE_LENGTH = "movie_length"; //$NON-NLS-1$
	public static final String KEY_WIDGET_MOVIE_SHOWTIME = "showtime"; //$NON-NLS-1$
	public static final String KEY_WIDGET_MOVIE_SHOWTIME_LANG = "lang"; //$NON-NLS-1$
	public static final String KEY_WIDGET_MOVIE_SHOWTIME_RESERVATION = "reservation_url"; //$NON-NLS-1$

	public static final String KEY_REVIEW_ID = "id"; //$NON-NLS-1$
	public static final String KEY_REVIEW_MOVIE_MID = "movie_id"; //$NON-NLS-1$
	public static final String KEY_REVIEW_RATE = "rate"; //$NON-NLS-1$
	public static final String KEY_REVIEW_SOURCE = "source"; //$NON-NLS-1$
	public static final String KEY_REVIEW_URL_REVIEW = "url_review"; //$NON-NLS-1$
	public static final String KEY_REVIEW_AUTHOR = "author"; //$NON-NLS-1$
	public static final String KEY_REVIEW_CONTENT = "review"; //$NON-NLS-1$

	public static final String KEY_VIDEO_ID = "id"; //$NON-NLS-1$
	public static final String KEY_VIDEO_MOVIE_MID = "movie_id"; //$NON-NLS-1$
	public static final String KEY_VIDEO_URL_IMG = "url_img"; //$NON-NLS-1$
	public static final String KEY_VIDEO_URL_VIDEO = "url_video"; //$NON-NLS-1$
	public static final String KEY_VIDEO_NAME = "video_name"; //$NON-NLS-1$

	public static final String KEY_CURENT_MOVIE_MOVIE_ID = "movie_id"; //$NON-NLS-1$
	public static final String KEY_CURENT_MOVIE_THEATER_ID = "theater_id"; //$NON-NLS-1$
	public static final String KEY_CURENT_MOVIE_WIDGET_ID = "widget_id"; //$NON-NLS-1$

	public static final String KEY_SKYHOOK_REGISTRATION = "skyhook_registration"; //$NON-NLS-1$

	public static final String KEY_LAST_CHANGE_VERSION = "version"; //$NON-NLS-1$

	private static final String TAG = "AndShowtimeDbAdapter"; //$NON-NLS-1$
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "andShowtime"; //$NON-NLS-1$
	private static final String DATABASE_THEATERS_TABLE = "theaters"; //$NON-NLS-1$
	private static final String DATABASE_MOVIE_TABLE = "movies"; //$NON-NLS-1$
	private static final String DATABASE_SHOWTIME_TABLE = "showtimes"; //$NON-NLS-1$
	private static final String DATABASE_LOCATION_TABLE = "location"; //$NON-NLS-1$
	private static final String DATABASE_FAV_THEATER_TABLE = "favTheaters"; //$NON-NLS-1$
	private static final String DATABASE_FAV_SHOWTIME_TABLE = "favShowtimes"; //$NON-NLS-1$
	private static final String DATABASE_NEAR_REQUEST_TABLE = "near_request"; //$NON-NLS-1$
	private static final String DATABASE_MOVIE_REQUEST_TABLE = "movie_request"; //$NON-NLS-1$
	private static final String DATABASE_WIDGET_TABLE = "widget"; //$NON-NLS-1$
	private static final String DATABASE_WIDGET_MOVIE_TABLE = "widget_movie"; //$NON-NLS-1$
	private static final String DATABASE_CURENT_MOVIE_TABLE = "current_movie"; //$NON-NLS-1$
	private static final String DATABASE_SKYHOOK_REGISTRATION_TABLE = "skyhook_registration"; //$NON-NLS-1$
	private static final String DATABASE_LAST_CHANGE_TABLE = "last_change"; //$NON-NLS-1$
	private static final String DATABASE_REVIEW_TABLE = "reviews"; //$NON-NLS-1$
	private static final String DATABASE_VIDEO_TABLE = "videos"; //$NON-NLS-1$
	private static final int DATABASE_VERSION = 29;
	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE_THEATER_TABLE = "create table " + DATABASE_THEATERS_TABLE //$NON-NLS-1$
			+ " (" + KEY_THEATER_ID + " text primary key" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_THEATER_NAME + " text not null" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_THEATER_PHONE + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ");";//$NON-NLS-1$
	private static final String DATABASE_CREATE_FAV_THEATER_TABLE = "create table " + DATABASE_FAV_THEATER_TABLE //$NON-NLS-1$
			+ " (" + KEY_FAV_TH_THEATER_ID + " text primary key" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_FAV_TH_THEATER_NAME + " text not null" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_FAV_TH_THEATER_PLACE + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_FAV_TH_THEATER_COUNRTY_CODE + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_FAV_TH_THEATER_POSTAL_CODE + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_FAV_TH_THEATER_LAT + " double " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_FAV_TH_THEATER_LONG + " double " //$NON-NLS-1$ //$NON-NLS-2$
			+ ");";//$NON-NLS-1$
	private static final String DATABASE_CREATE_FAV_SHOWTIME_TABLE = " create table " + DATABASE_FAV_SHOWTIME_TABLE //$NON-NLS-1$
			+ " (" + KEY_FAV_SHOWTIME_ID + " integer primary key autoincrement" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_FAV_SHOWTIME_MOVIE_ID + " text not null" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_FAV_SHOWTIME_THEATER_ID + " text not null" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_FAV_SHOWTIME_TIME + " long not null" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_FAV_SHOWTIME_LANG + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_FAV_SHOWTIME_RESERVATION_URL + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;
	private static final String DATABASE_CREATE_MOVIE_TABLE = " create table " + DATABASE_MOVIE_TABLE //$NON-NLS-1$
			+ " (" + KEY_MOVIE_ID + " text primary key" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_MOVIE_CID + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_IMDB_ID + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_NAME + " text not null" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_ENGLISH_NAME + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_IMDB_DESC + " integer" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_DESC + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_TR_DESC + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_IMG_URL + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_WIKIPEDIA_URL + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_LANG + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_STYLE + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_RATE + " double" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_TIME + " long" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_TIME_FORMAT + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_ACTORS + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_DIRECTORS + " text" //$NON-NLS-1$ //$NON-NLS-2$
			+ ");";//$NON-NLS-1$
	private static final String DATABASE_CREATE_SHOWTIME_TABLE = " create table " + DATABASE_SHOWTIME_TABLE //$NON-NLS-1$
			+ " (" + KEY_SHOWTIME_ID + " integer primary key autoincrement" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_SHOWTIME_MOVIE_ID + " text not null" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_SHOWTIME_THEATER_ID + " text not null" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_SHOWTIME_TIME + " long not null" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_SHOWTIME_LANG + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_SHOWTIME_RESERVATION_URL + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_SHOWTIME_FORMAT_24 + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_SHOWTIME_FORMAT_12 + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;
	private static final String DATABASE_CREATE_LOCATION_TABLE = " create table " + DATABASE_LOCATION_TABLE //$NON-NLS-1$
			+ " (" + KEY_LOCALISATION_THEATER_ID + " text primary key" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_LOCALISATION_CITY_NAME + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_LOCALISATION_COUNTRY_NAME + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_LOCALISATION_COUNTRY_CODE + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_LOCALISATION_POSTAL_CODE + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_LOCALISATION_DISTANCE + " float " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_LOCALISATION_DISTANCE_TIME + " long " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_LOCALISATION_LATITUDE + " double " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_LOCALISATION_LONGITUDE + " double " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_LOCALISATION_SEARCH_QUERY + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;

	private static final String DATABASE_CREATE_NEAR_REQUEST_TABLE = " create table " + DATABASE_NEAR_REQUEST_TABLE //$NON-NLS-1$
			+ " (" + KEY_NEAR_REQUEST_ID + " integer primary key autoincrement" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_NEAR_REQUEST_CITY_NAME + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_NEAR_REQUEST_THEATER_ID + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_NEAR_REQUEST_LATITUDE + " double " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_NEAR_REQUEST_LONGITUDE + " double " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_NEAR_REQUEST_TIME + " long " //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;

	private static final String DATABASE_CREATE_MOVIE_REQUEST_TABLE = " create table " + DATABASE_MOVIE_REQUEST_TABLE //$NON-NLS-1$
			+ " (" + KEY_MOVIE_REQUEST_ID + " integer primary key autoincrement" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_MOVIE_REQUEST_CITY_NAME + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_REQUEST_MOVIE_NAME + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_REQUEST_THEATER_ID + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_REQUEST_LATITUDE + " double " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_REQUEST_LONGITUDE + " double " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_REQUEST_TIME + " long " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_MOVIE_REQUEST_NULL_RESULT + " short " //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;

	private static final String DATABASE_CREATE_WIDGET_TABLE = " create table " + DATABASE_WIDGET_TABLE //$NON-NLS-1$
			+ " (" + KEY_WIDGET_THEATER_WIDGET_ID + " integer " //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_WIDGET_THEATER_ID + " text primary key" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_WIDGET_THEATER_NAME + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_THEATER_PLACE + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_THEATER_COUNRTY_CODE + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_THEATER_POSTAL_CODE + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_THEATER_LAT + " double " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_THEATER_LONG + " double " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_THEATER_DATE + " long " //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;

	private static final String DATABASE_CREATE_WIDGET_MOVIE_TABLE = " create table " + DATABASE_WIDGET_MOVIE_TABLE //$NON-NLS-1$
			+ " (" + KEY_WIDGET_MOVIE_ID + " integer primary key autoincrement" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_WIDGET_MOVIE_WIDGET_ID + " integer" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_WIDGET_MOVIE_MID + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_MOVIE_NAME + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_MOVIE_EN_NAME + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_MOVIE_LENGTH + " long " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_MOVIE_SHOWTIME + " long " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_MOVIE_SHOWTIME_LANG + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_WIDGET_MOVIE_SHOWTIME_RESERVATION + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;

	private static final String DATABASE_CREATE_REVIEW_TABLE = " create table " + DATABASE_REVIEW_TABLE //$NON-NLS-1$
			+ " (" + KEY_REVIEW_ID + " integer primary key autoincrement" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_REVIEW_MOVIE_MID + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_REVIEW_RATE + " double " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_REVIEW_URL_REVIEW + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_REVIEW_AUTHOR + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_REVIEW_SOURCE + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_REVIEW_CONTENT + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;

	private static final String DATABASE_CREATE_VIDEO_TABLE = " create table " + DATABASE_VIDEO_TABLE //$NON-NLS-1$
			+ " (" + KEY_VIDEO_ID + " integer primary key autoincrement" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_VIDEO_MOVIE_MID + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_VIDEO_NAME + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_VIDEO_URL_IMG + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_VIDEO_URL_VIDEO + " text " //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;

	private static final String DATABASE_CREATE_CURENT_MOVIE_TABLE = " create table " + DATABASE_CURENT_MOVIE_TABLE //$NON-NLS-1$
			+ " (" + KEY_CURENT_MOVIE_MOVIE_ID + " text primary key" //$NON-NLS-1$//$NON-NLS-2$
			+ ", " + KEY_CURENT_MOVIE_THEATER_ID + " text not null" //$NON-NLS-1$ //$NON-NLS-2$
			+ ", " + KEY_CURENT_MOVIE_WIDGET_ID + " integer" //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;
	private static final String DATABASE_CREATE_SKYHOOK_REGISTRATION_TABLE = " create table " + DATABASE_SKYHOOK_REGISTRATION_TABLE//$NON-NLS-1$
			+ " (" + KEY_SKYHOOK_REGISTRATION + " text primary key" //$NON-NLS-1$//$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;
	private static final String DATABASE_CREATE_LAST_CHANGE_TABLE = " create table " + DATABASE_LAST_CHANGE_TABLE//$NON-NLS-1$
			+ " (" + KEY_LAST_CHANGE_VERSION + " integer primary key" //$NON-NLS-1$//$NON-NLS-2$
			+ ");"//$NON-NLS-1$
	;

	private static final String DROP_THEATER_TABLE = "DROP TABLE IF EXISTS " + DATABASE_THEATERS_TABLE; //$NON-NLS-1$
	private static final String DROP_FAV_THEATER_TABLE = "DROP TABLE IF EXISTS " + DATABASE_FAV_THEATER_TABLE; //$NON-NLS-1$
	private static final String DROP_FAV_SHOWTIME_TABLE = "DROP TABLE IF EXISTS " + DATABASE_FAV_SHOWTIME_TABLE; //$NON-NLS-1$
	private static final String DROP_MOVIE_TABLE = "DROP TABLE IF EXISTS " + DATABASE_MOVIE_TABLE; //$NON-NLS-1$
	private static final String DROP_SHOWTIME_TABLE = "DROP TABLE IF EXISTS " + DATABASE_SHOWTIME_TABLE; //$NON-NLS-1$
	private static final String DROP_LOCATION_TABLE = "DROP TABLE IF EXISTS " + DATABASE_LOCATION_TABLE; //$NON-NLS-1$
	private static final String DROP_NEAR_REQUEST_TABLE = "DROP TABLE IF EXISTS " + DATABASE_NEAR_REQUEST_TABLE; //$NON-NLS-1$
	private static final String DROP_MOVIE_REQUEST_TABLE = "DROP TABLE IF EXISTS " + DATABASE_MOVIE_REQUEST_TABLE; //$NON-NLS-1$
	private static final String DROP_WIDGET_TABLE = "DROP TABLE IF EXISTS " + DATABASE_WIDGET_TABLE; //$NON-NLS-1$
	private static final String DROP_WIDGET_MOVIE_TABLE = "DROP TABLE IF EXISTS " + DATABASE_WIDGET_MOVIE_TABLE; //$NON-NLS-1$
	private static final String DROP_CURENT_MOVIE_TABLE = "DROP TABLE IF EXISTS " + DATABASE_CURENT_MOVIE_TABLE; //$NON-NLS-1$
	private static final String DROP_SKYHOOK_REGISTRATION_TABLE = "DROP TABLE IF EXISTS " + DATABASE_SKYHOOK_REGISTRATION_TABLE; //$NON-NLS-1$
	private static final String DROP_LAST_CHANGE_TABLE = "DROP TABLE IF EXISTS " + DATABASE_LAST_CHANGE_TABLE; //$NON-NLS-1$
	private static final String DROP_REVIEW_TABLE = "DROP TABLE IF EXISTS " + DATABASE_REVIEW_TABLE; //$NON-NLS-1$
	private static final String DROP_VIDEO_TABLE = "DROP TABLE IF EXISTS " + DATABASE_VIDEO_TABLE; //$NON-NLS-1$

	private static final String INSERT_THEATER = "INSERT INTO " + DATABASE_THEATERS_TABLE //
			+ " (" + KEY_THEATER_ID + ", " + KEY_THEATER_NAME + ", " + KEY_THEATER_PHONE + ") " //
			+ " VALUES (?,?,?)";
	private static final String INSERT_SHOWTIMES = "INSERT INTO " + DATABASE_SHOWTIME_TABLE //
			+ " (" + KEY_SHOWTIME_THEATER_ID + ", " + KEY_SHOWTIME_MOVIE_ID + ", " + KEY_SHOWTIME_TIME + ", " + KEY_SHOWTIME_LANG + ", " + KEY_SHOWTIME_RESERVATION_URL + ", " + KEY_SHOWTIME_FORMAT_24 + ", " + KEY_SHOWTIME_FORMAT_12 + ") " //
			+ " VALUES (?,?,?,?,?,?,?)";

	private final Context mCtx;

	// private SQLiteStatement statmentTheater = null;
	// private SQLiteStatement statmentShowtime = null;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Create the data base"); //$NON-NLS-1$
			db.execSQL(DATABASE_CREATE_MOVIE_TABLE);
			db.execSQL(DATABASE_CREATE_THEATER_TABLE);
			db.execSQL(DATABASE_CREATE_FAV_THEATER_TABLE);
			db.execSQL(DATABASE_CREATE_FAV_SHOWTIME_TABLE);
			db.execSQL(DATABASE_CREATE_SHOWTIME_TABLE);
			db.execSQL(DATABASE_CREATE_LOCATION_TABLE);
			db.execSQL(DATABASE_CREATE_NEAR_REQUEST_TABLE);
			db.execSQL(DATABASE_CREATE_MOVIE_REQUEST_TABLE);
			db.execSQL(DATABASE_CREATE_WIDGET_TABLE);
			db.execSQL(DATABASE_CREATE_WIDGET_MOVIE_TABLE);
			db.execSQL(DATABASE_CREATE_CURENT_MOVIE_TABLE);
			db.execSQL(DATABASE_CREATE_SKYHOOK_REGISTRATION_TABLE);
			db.execSQL(DATABASE_CREATE_LAST_CHANGE_TABLE);
			db.execSQL(DATABASE_CREATE_REVIEW_TABLE);
			db.execSQL(DATABASE_CREATE_VIDEO_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " //$NON-NLS-1$ //$NON-NLS-2$
					+ newVersion + ", which will destroy all old data"); //$NON-NLS-1$
			if (oldVersion < 17) {
				db.execSQL(DROP_WIDGET_TABLE);
				db.execSQL(DROP_WIDGET_MOVIE_TABLE);
				db.execSQL(DATABASE_CREATE_WIDGET_TABLE);
				db.execSQL(DATABASE_CREATE_WIDGET_MOVIE_TABLE);
			}
			if (oldVersion < 18) {
				db.execSQL(DROP_LOCATION_TABLE);
				db.execSQL(DATABASE_CREATE_LOCATION_TABLE);
			}
			if (oldVersion < 19) {
				db.execSQL(DROP_SHOWTIME_TABLE);
				db.execSQL(DROP_WIDGET_MOVIE_TABLE);
				db.execSQL(DROP_FAV_THEATER_TABLE);
				db.execSQL(DATABASE_CREATE_SHOWTIME_TABLE);
				db.execSQL(DATABASE_CREATE_WIDGET_MOVIE_TABLE);
				db.execSQL(DATABASE_CREATE_FAV_THEATER_TABLE);
				db.execSQL(DATABASE_CREATE_CURENT_MOVIE_TABLE);
			}
			if (oldVersion < 20) {
				db.execSQL(DATABASE_CREATE_FAV_SHOWTIME_TABLE);
			}
			if (oldVersion < 21) {
				db.execSQL(DATABASE_CREATE_SKYHOOK_REGISTRATION_TABLE);
			}
			if (oldVersion < 22) {
				db.execSQL(DROP_MOVIE_TABLE);
				db.execSQL(DATABASE_CREATE_MOVIE_TABLE);
			}
			if (oldVersion < 23) {
				db.execSQL(DATABASE_CREATE_LAST_CHANGE_TABLE);
			}
			if (oldVersion < 24) {
				db.execSQL(DATABASE_CREATE_REVIEW_TABLE);
				db.execSQL(DATABASE_CREATE_VIDEO_TABLE);
			}
			if (oldVersion < 25) {
				db.execSQL(DROP_MOVIE_REQUEST_TABLE);
				db.execSQL(DATABASE_CREATE_MOVIE_REQUEST_TABLE);
			}
			if (oldVersion < 26) {
				db.execSQL(DROP_WIDGET_TABLE);
				db.execSQL(DATABASE_CREATE_WIDGET_TABLE);
			}
			if (oldVersion < 27) {
				db.execSQL(DROP_WIDGET_MOVIE_TABLE);
				db.execSQL(DROP_CURENT_MOVIE_TABLE);
				db.execSQL(DATABASE_CREATE_WIDGET_MOVIE_TABLE);
				db.execSQL(DATABASE_CREATE_CURENT_MOVIE_TABLE);
			}
			if (oldVersion < 28) {
				db.execSQL(DROP_SHOWTIME_TABLE);
				db.execSQL(DATABASE_CREATE_SHOWTIME_TABLE);
			}
			if (oldVersion < 29) {
				db.execSQL(DROP_MOVIE_TABLE);
				db.execSQL(DATABASE_CREATE_MOVIE_TABLE);
			}

		}
	}

	/**
	 * Constructor - takes the context to allow the database to be opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public CineShowtimeDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the notes database. If it cannot be opened, try to create a new instance of the database. If it cannot be created, throw an exception to signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public CineShowtimeDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		mDb.setLockingEnabled(true);
		// statmentTheater = mDb.compileStatement(INSERT_THEATER);
		// statmentShowtime = mDb.compileStatement(INSERT_SHOWTIMES);
		return this;
	}

	public boolean isOpen() {
		return (mDb != null) && mDb.isOpen();
	}

	public boolean isDbLockedByCurrentThread() {
		return (mDb != null) && mDb.isDbLockedByCurrentThread();
	}

	public boolean isDbLockedByOtherThreads() {
		return (mDb != null) && mDb.isDbLockedByOtherThreads();
	}

	public SQLiteDatabase getSqlLite() {
		return mDb;
	}

	public void close() {
		mDbHelper.close();
	}

	public long addTheaterToFavorites(TheaterBean theater) {
		chekDbAvailable();

		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create favorite theater : ").append(theater.getTheaterName()).toString()); //$NON-NLS-1$
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_FAV_TH_THEATER_ID, theater.getId());
		initialValues.put(KEY_FAV_TH_THEATER_NAME, theater.getTheaterName());
		if (theater.getPlace() != null) {
			if ((theater.getPlace().getCityName() != null //
					)
					&& (theater.getPlace().getCityName().length() > 0)) {
				initialValues.put(KEY_FAV_TH_THEATER_PLACE, theater.getPlace().getCityName());
			}
			if ((theater.getPlace().getCountryNameCode() != null //
					)
					&& (theater.getPlace().getCountryNameCode().length() > 0)) {
				initialValues.put(KEY_FAV_TH_THEATER_COUNRTY_CODE, theater.getPlace().getCountryNameCode());
			}
			if ((theater.getPlace().getPostalCityNumber() != null //
					)
					&& (theater.getPlace().getPostalCityNumber().length() > 0)) {
				initialValues.put(KEY_FAV_TH_THEATER_POSTAL_CODE, theater.getPlace().getPostalCityNumber());
			}
			if (theater.getPlace().getLatitude() != null) {
				initialValues.put(KEY_FAV_TH_THEATER_LAT, theater.getPlace().getLatitude());
			}
			if (theater.getPlace().getLongitude() != null) {
				initialValues.put(KEY_FAV_TH_THEATER_LONG, theater.getPlace().getLongitude());
			}
		}

		StringBuilder closeSelection = new StringBuilder(KEY_FAV_TH_THEATER_ID).append("='").append(theater.getId()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		Cursor favTheaterCursor = mDb.query(//
				DATABASE_FAV_THEATER_TABLE //
				, new String[] { KEY_FAV_TH_THEATER_ID } //
				, closeSelection.toString() //
				, null //
				, null //
				, null //
				, null //
				);
		long result = 0;
		if (!favTheaterCursor.moveToFirst()) {
			result = mDb.insert(DATABASE_FAV_THEATER_TABLE, null, initialValues);
			if (result == -1) {
				Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
			}
		}
		if (favTheaterCursor != null) {
			favTheaterCursor.close();
		}

		return result;
	}

	public long createNearRequest(String cityName, Double latitude, Double longitude, String theaterId) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create near Request").toString()); //$NON-NLS-1$
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NEAR_REQUEST_CITY_NAME, cityName);
		initialValues.put(KEY_NEAR_REQUEST_THEATER_ID, theaterId);
		initialValues.put(KEY_NEAR_REQUEST_LATITUDE, latitude);
		initialValues.put(KEY_NEAR_REQUEST_LONGITUDE, longitude);
		initialValues.put(KEY_NEAR_REQUEST_TIME, Calendar.getInstance().getTimeInMillis());

		long result = mDb.insert(DATABASE_NEAR_REQUEST_TABLE, null, initialValues);

		if (result == -1) {
			Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
		}

		return result;
	}

	public long createMovieRequest(String cityName, String movieName, Double latitude, Double longitude, String theaterId, boolean nullResult) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create movie Request").toString()); //$NON-NLS-1$
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MOVIE_REQUEST_CITY_NAME, cityName);
		initialValues.put(KEY_MOVIE_REQUEST_MOVIE_NAME, movieName);
		initialValues.put(KEY_MOVIE_REQUEST_THEATER_ID, theaterId);
		initialValues.put(KEY_MOVIE_REQUEST_LATITUDE, latitude);
		initialValues.put(KEY_MOVIE_REQUEST_LONGITUDE, longitude);
		initialValues.put(KEY_MOVIE_REQUEST_TIME, Calendar.getInstance().getTimeInMillis());
		initialValues.put(KEY_MOVIE_REQUEST_NULL_RESULT, nullResult ? 1 : 0);

		long result = mDb.insert(DATABASE_MOVIE_REQUEST_TABLE, null, initialValues);

		if (result == -1) {
			Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
		}

		return result;
	}

	/**
	 * Create a new note using the title and body provided. If the note is successfully created return the new rowId for that note, otherwise return a -1 to indicate failure.
	 * 
	 * @param title
	 *            the title of the note
	 * @param body
	 *            the body of the note
	 * @return rowId or -1 if failed
	 */
	public long createTheaterList(List<TheaterBean> theaterList) {
		chekDbAvailable();
		long result = 0;
		// SQLiteStatement statementTheater = mDb.compileStatement(INSERT_THEATER);
		// for (TheaterBean theater : theaterList) {
		// statmentTheater.bindString(1, theater.getId());
		// statmentTheater.bindString(2, theater.getTheaterName());
		// statmentTheater.bindString(3, theater.getPhoneNumber() != null ? theater.getPhoneNumber() : "");
		//			Log.d(TAG, new StringBuilder("Create Theater: ").append(theater.getId()).toString()); //$NON-NLS-1$ 
		// result = statmentTheater.executeInsert();
		// }
		// statmentTheater.close();
		ContentValues initialValues = new ContentValues();
		for (TheaterBean theater : theaterList) {
			if (Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d(TAG, new StringBuilder("Create Theater: ").append(theater.getId()).toString()); //$NON-NLS-1$
			}
			initialValues.clear();
			initialValues.put(KEY_THEATER_ID, theater.getId());
			initialValues.put(KEY_THEATER_NAME, theater.getTheaterName());
			initialValues.put(KEY_THEATER_PHONE, theater.getPhoneNumber());
			result = mDb.insert(DATABASE_THEATERS_TABLE, null, initialValues);
		}

		// long result = mDb.insert(DATABASE_THEATERS_TABLE, null, initialValues);

		if (result == -1) {
			Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
		}

		return result;
	}

	/**
	 * Create a new note using the title and body provided. If the note is successfully created return the new rowId for that note, otherwise return a -1 to indicate failure.
	 * 
	 * @param title
	 *            the title of the note
	 * @param body
	 *            the body of the note
	 * @return rowId or -1 if failed
	 */
	private long createTheater(TheaterBean theater) {
		chekDbAvailable();
		// SQLiteStatement statementTheater = mDb.compileStatement(INSERT_THEATER);
		// statmentTheater.bindString(1, theater.getId());
		// statmentTheater.bindString(2, theater.getTheaterName());
		// statmentTheater.bindString(3, theater.getPhoneNumber() != null ? theater.getPhoneNumber() : "");
		// long result = statmentTheater.executeInsert();
		// statmentTheater.close();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create Theater: ").append(theater.getId()).toString()); //$NON-NLS-1$
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_THEATER_ID, theater.getId());
		initialValues.put(KEY_THEATER_NAME, theater.getTheaterName());
		initialValues.put(KEY_THEATER_PHONE, theater.getPhoneNumber());

		long result = mDb.insert(DATABASE_THEATERS_TABLE, null, initialValues);

		if (result == -1) {
			Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
		}

		return result;
	}

	public long createOrUpdateMovie(MovieBean movie) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create or update for movie: ").append(movie.getId()).toString()); //$NON-NLS-1$
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MOVIE_ID, movie.getId());
		initialValues.put(KEY_MOVIE_IMDB_ID, movie.getImdbId());
		initialValues.put(KEY_MOVIE_CID, movie.getCid());
		initialValues.put(KEY_MOVIE_NAME, movie.getMovieName());
		initialValues.put(KEY_MOVIE_ENGLISH_NAME, movie.getEnglishMovieName());
		initialValues.put(KEY_MOVIE_IMDB_DESC, movie.isImdbDesrciption() ? 1 : 0);
		initialValues.put(KEY_MOVIE_DESC, movie.getDescription());
		initialValues.put(KEY_MOVIE_TR_DESC, movie.getTrDescription());
		initialValues.put(KEY_MOVIE_IMG_URL, movie.getUrlImg());
		initialValues.put(KEY_MOVIE_WIKIPEDIA_URL, movie.getUrlWikipedia());
		initialValues.put(KEY_MOVIE_RATE, movie.getRate());
		initialValues.put(KEY_MOVIE_STYLE, movie.getStyle());
		initialValues.put(KEY_MOVIE_TIME, movie.getMovieTime());
		initialValues.put(KEY_MOVIE_TIME_FORMAT, movie.getMovieTimeFormat());
		initialValues.put(KEY_MOVIE_ACTORS, movie.getActorList());
		initialValues.put(KEY_MOVIE_DIRECTORS, movie.getDirectorList());

		StringBuilder closeSelection = new StringBuilder(KEY_MOVIE_ID).append("='").append(movie.getId()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		Cursor movieCursor = mDb.query(//
				DATABASE_MOVIE_TABLE //
				, new String[] { KEY_MOVIE_ID } //
				, closeSelection.toString() //
				, null //
				, null //
				, null //
				, null //
				);
		long result = 0;
		if (movieCursor.moveToFirst()) {
			if (Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d(TAG, new StringBuilder("Movie already exists : ").append(movie.getId()).toString()); //$NON-NLS-1$
			}
			result = mDb.update(//
					DATABASE_MOVIE_TABLE//
					, initialValues//
					, closeSelection.toString()//
					, null);

		} else {
			if (Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d(TAG, new StringBuilder("Movie to create : ").append(movie.getId()).toString()); //$NON-NLS-1$
			}
			result = mDb.insert(DATABASE_MOVIE_TABLE, null, initialValues);
		}
		if (movieCursor != null) {
			movieCursor.close();
		}
		if (result == -1) {
			Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
		}
		return result;
	}

	public long createShowtimeList(Map<String, Map<String, List<ProjectionBean>>> datas) {
		chekDbAvailable();

		SQLiteStatement statmentShowtime = mDb.compileStatement(INSERT_SHOWTIMES);
		long result = 0;
		for (Entry<String, Map<String, List<ProjectionBean>>> entryThMap : datas.entrySet()) {
			for (Entry<String, List<ProjectionBean>> entryMovList : entryThMap.getValue().entrySet()) {
				for (ProjectionBean projection : entryMovList.getValue()) {
					statmentShowtime.bindString(1, entryThMap.getKey());
					statmentShowtime.bindString(2, entryMovList.getKey());
					statmentShowtime.bindLong(3, projection.getShowtime());
					statmentShowtime.bindString(4, projection.getLang() != null ? projection.getLang() : "");
					statmentShowtime.bindString(5, projection.getReservationLink() != null ? projection.getReservationLink() : "");
					statmentShowtime.bindString(6, projection.getFormat24() != null ? projection.getFormat24() : "");
					statmentShowtime.bindString(7, projection.getFormat12() != null ? projection.getFormat12() : "");
					if (Log.isLoggable(TAG, Log.DEBUG)) {
						Log.d(TAG, new StringBuilder("Create showtime for theater: ").append(entryThMap.getKey()).append(" and movieId : ").append(entryMovList.getKey()).append(" for time : ").append(projection.getShowtime()).toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					result = statmentShowtime.executeInsert();

				}
			}
		}
		statmentShowtime.close();

		// ContentValues initialValues = new ContentValues();
		// initialValues.put(KEY_SHOWTIME_THEATER_ID, theatherId);
		// initialValues.put(KEY_SHOWTIME_MOVIE_ID, movieId);
		// initialValues.put(KEY_SHOWTIME_TIME, time.getShowtime());
		// initialValues.put(KEY_SHOWTIME_LANG, time.getLang());
		// initialValues.put(KEY_SHOWTIME_RESERVATION_URL, time.getReservationLink());
		//
		// long result = mDb.insert(DATABASE_SHOWTIME_TABLE, null, initialValues);
		if (result == -1) {
			Log.e(TAG, "Error inserting row"); //$NON-NLS-1$
		}
		return result;
	}

	private long createShowtime(String theatherId, String movieId, ProjectionBean time) {
		chekDbAvailable();

		SQLiteStatement statmentShowtime = mDb.compileStatement(INSERT_SHOWTIMES);
		statmentShowtime.bindString(1, theatherId);
		statmentShowtime.bindString(2, movieId);
		statmentShowtime.bindLong(3, time.getShowtime());
		statmentShowtime.bindString(4, time.getLang() != null ? time.getLang() : "");
		statmentShowtime.bindString(5, time.getReservationLink() != null ? time.getReservationLink() : "");
		statmentShowtime.bindString(6, time.getFormat24() != null ? time.getFormat24() : "");
		statmentShowtime.bindString(7, time.getFormat12() != null ? time.getFormat12() : "");
		long result = statmentShowtime.executeInsert();
		statmentShowtime.close();

		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create showtime for theater: ").append(theatherId).append(" and movieId : ").append(movieId).append(" for time : ").append(time).toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		// ContentValues initialValues = new ContentValues();
		// initialValues.put(KEY_SHOWTIME_THEATER_ID, theatherId);
		// initialValues.put(KEY_SHOWTIME_MOVIE_ID, movieId);
		// initialValues.put(KEY_SHOWTIME_TIME, time.getShowtime());
		// initialValues.put(KEY_SHOWTIME_LANG, time.getLang());
		// initialValues.put(KEY_SHOWTIME_RESERVATION_URL, time.getReservationLink());
		//
		// long result = mDb.insert(DATABASE_SHOWTIME_TABLE, null, initialValues);
		if (result == -1) {
			Log.e(TAG, "Error inserting row"); //$NON-NLS-1$
		}
		return result;
	}

	public long createFavShowtime(String theatherId, String movieId, ProjectionBean time) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create fav showtime for theater: ").append(theatherId).append(" and movieId : ").append(movieId).append(" for time : ").append(time).toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_FAV_SHOWTIME_THEATER_ID, theatherId);
		initialValues.put(KEY_FAV_SHOWTIME_MOVIE_ID, movieId);
		initialValues.put(KEY_FAV_SHOWTIME_TIME, time.getShowtime());
		initialValues.put(KEY_FAV_SHOWTIME_LANG, time.getLang());
		initialValues.put(KEY_FAV_SHOWTIME_RESERVATION_URL, time.getReservationLink());

		long result = mDb.insert(DATABASE_FAV_SHOWTIME_TABLE, null, initialValues);
		if (result == -1) {
			Log.e(TAG, "Error inserting row"); //$NON-NLS-1$
		}
		return result;
	}

	public long createLocation(LocalisationBean location, String theaterId) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create location for theater: ").append(theaterId).toString()); //$NON-NLS-1$
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LOCALISATION_THEATER_ID, theaterId);
		initialValues.put(KEY_LOCALISATION_CITY_NAME, location.getCityName());
		initialValues.put(KEY_LOCALISATION_COUNTRY_NAME, location.getCountryName());
		initialValues.put(KEY_LOCALISATION_COUNTRY_CODE, location.getCountryNameCode());
		initialValues.put(KEY_LOCALISATION_POSTAL_CODE, location.getPostalCityNumber());
		initialValues.put(KEY_LOCALISATION_DISTANCE, location.getDistance());
		initialValues.put(KEY_LOCALISATION_DISTANCE_TIME, location.getDistanceTime());
		initialValues.put(KEY_LOCALISATION_LATITUDE, location.getLatitude());
		initialValues.put(KEY_LOCALISATION_LONGITUDE, location.getLongitude());
		initialValues.put(KEY_LOCALISATION_SEARCH_QUERY, location.getSearchQuery());

		long result = mDb.insert(DATABASE_LOCATION_TABLE, null, initialValues);
		if (result == -1) {
			Log.e(TAG, "Error inserting row"); //$NON-NLS-1$
		}
		return result;
	}

	public long setWidgetTheater(TheaterBean theater) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create Widget ").toString()); //$NON-NLS-1$
		}

		mDb.delete(DATABASE_WIDGET_TABLE, KEY_WIDGET_THEATER_ID + " = ?", new String[] { theater.getId() });

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_WIDGET_THEATER_WIDGET_ID, theater.getWidgetId());
		initialValues.put(KEY_WIDGET_THEATER_ID, theater.getId());
		initialValues.put(KEY_WIDGET_THEATER_NAME, theater.getTheaterName());
		if (theater.getPlace() != null) {
			if ((theater.getPlace().getCityName() != null //
					)
					&& (theater.getPlace().getCityName().length() > 0)) {
				initialValues.put(KEY_WIDGET_THEATER_PLACE, theater.getPlace().getCityName());
			}
			if ((theater.getPlace().getCountryNameCode() != null //
					)
					&& (theater.getPlace().getCountryNameCode().length() > 0)) {
				initialValues.put(KEY_WIDGET_THEATER_COUNRTY_CODE, theater.getPlace().getCountryNameCode());
			}
			if ((theater.getPlace().getPostalCityNumber() != null //
					)
					&& (theater.getPlace().getPostalCityNumber().length() > 0)) {
				initialValues.put(KEY_WIDGET_THEATER_POSTAL_CODE, theater.getPlace().getPostalCityNumber());
			}
			if (theater.getPlace().getLatitude() != null) {
				initialValues.put(KEY_WIDGET_THEATER_LAT, theater.getPlace().getLatitude());
			}
			if (theater.getPlace().getLongitude() != null) {
				initialValues.put(KEY_WIDGET_THEATER_LONG, theater.getPlace().getLongitude());
			}
		}

		long result = mDb.insert(DATABASE_WIDGET_TABLE, null, initialValues);

		if (result == -1) {
			Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
		}

		return result;
	}

	public long createReview(ReviewBean review, String movieId) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create review for movie: ").append(movieId).toString()); //$NON-NLS-1$
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_REVIEW_MOVIE_MID, movieId);
		initialValues.put(KEY_REVIEW_AUTHOR, review.getAuthor());
		initialValues.put(KEY_REVIEW_URL_REVIEW, review.getUrlReview());
		initialValues.put(KEY_REVIEW_SOURCE, review.getSource());
		initialValues.put(KEY_REVIEW_RATE, review.getRate());
		initialValues.put(KEY_REVIEW_CONTENT, review.getReview());

		long result = mDb.insert(DATABASE_REVIEW_TABLE, null, initialValues);
		if (result == -1) {
			Log.e(TAG, "Error inserting row"); //$NON-NLS-1$
		}
		return result;
	}

	public long createVideo(YoutubeBean video, String movieId) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create video for movie: ").append(movieId).toString()); //$NON-NLS-1$
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_VIDEO_MOVIE_MID, movieId);
		initialValues.put(KEY_VIDEO_NAME, video.getVideoName());
		initialValues.put(KEY_VIDEO_URL_IMG, video.getUrlImg());
		initialValues.put(KEY_VIDEO_URL_VIDEO, video.getUrlVideo());

		long result = mDb.insert(DATABASE_VIDEO_TABLE, null, initialValues);
		if (result == -1) {
			Log.e(TAG, "Error inserting row"); //$NON-NLS-1$
		}
		return result;
	}

	public long updateOldWidgetTheater(int widgetId) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Update date Widget ").toString()); //$NON-NLS-1$
		}

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_WIDGET_THEATER_WIDGET_ID, widgetId);

		long result = mDb.update(DATABASE_WIDGET_TABLE//
				, initialValues//
				, KEY_WIDGET_THEATER_WIDGET_ID + " = -1"//
				, null//
				);

		if (result == -1) {
			Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
		}

		initialValues.clear();
		initialValues.put(KEY_WIDGET_MOVIE_WIDGET_ID, widgetId);
		result = mDb.update(DATABASE_WIDGET_MOVIE_TABLE//
				, initialValues//
				, KEY_WIDGET_MOVIE_WIDGET_ID + " = -1"//
				, null//
				);

		if (result == -1) {
			Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
		}

		initialValues.clear();
		initialValues.put(KEY_CURENT_MOVIE_WIDGET_ID, widgetId);
		result = mDb.update(DATABASE_CURENT_MOVIE_TABLE//
				, initialValues//
				, KEY_CURENT_MOVIE_WIDGET_ID + " = -1"//
				, null//
				);

		if (result == -1) {
			Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
		}

		return result;
	}

	public long updateWidgetTheater(int widgetId) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Update date Widget ").toString()); //$NON-NLS-1$
		}

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_WIDGET_THEATER_DATE, Calendar.getInstance().getTimeInMillis());

		long result = mDb.update(DATABASE_WIDGET_TABLE//
				, initialValues//
				, KEY_WIDGET_MOVIE_WIDGET_ID + " = ?"//
				, new String[] { String.valueOf(widgetId) }//
				);

		if (result == -1) {
			Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
		}

		return result;
	}

	public long createWidgetShowtime(MovieBean movie, ProjectionBean showtime, int widgetId) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create Widget showtime").toString()); //$NON-NLS-1$
		}

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_WIDGET_MOVIE_WIDGET_ID, widgetId);
		initialValues.put(KEY_WIDGET_MOVIE_MID, movie.getId());
		initialValues.put(KEY_WIDGET_MOVIE_NAME, movie.getMovieName());
		initialValues.put(KEY_WIDGET_MOVIE_EN_NAME, movie.getEnglishMovieName());
		initialValues.put(KEY_WIDGET_MOVIE_LENGTH, movie.getMovieTime());
		initialValues.put(KEY_WIDGET_MOVIE_SHOWTIME, showtime.getShowtime());
		initialValues.put(KEY_WIDGET_MOVIE_SHOWTIME_LANG, showtime.getLang());
		initialValues.put(KEY_WIDGET_MOVIE_SHOWTIME_RESERVATION, showtime.getReservationLink());

		long result = mDb.insert(DATABASE_WIDGET_MOVIE_TABLE, null, initialValues);

		if (result == -1) {
			Log.e(TAG, "Error inserting or updating row"); //$NON-NLS-1$
		}

		return result;
	}

	public long createCurentMovie(String theatherId, String movieId, int widgetId) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create curent movie for theater: ").append(theatherId).append(" and movieId : ").append(movieId).toString()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CURENT_MOVIE_THEATER_ID, theatherId);
		initialValues.put(KEY_CURENT_MOVIE_MOVIE_ID, movieId);
		initialValues.put(KEY_CURENT_MOVIE_WIDGET_ID, widgetId);

		long result = mDb.delete(DATABASE_CURENT_MOVIE_TABLE //
				, KEY_CURENT_MOVIE_WIDGET_ID + " = ?" //
				, new String[] { String.valueOf(widgetId) } //
				);
		if (result == -1) {
			Log.e(TAG, "Error deleting current movie"); //$NON-NLS-1$
		}

		result = mDb.insert(DATABASE_CURENT_MOVIE_TABLE, null, initialValues);
		if (result == -1) {
			Log.e(TAG, "Error inserting row"); //$NON-NLS-1$
		}
		return result;
	}

	public long createSkyHookRegistration() {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create skyhook registration: ").toString()); //$NON-NLS-1$
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SKYHOOK_REGISTRATION, "OK"); //$NON-NLS-1$

		long result = mDb.insert(DATABASE_SKYHOOK_REGISTRATION_TABLE, null, initialValues);
		if (result == -1) {
			Log.e(TAG, "Error inserting row"); //$NON-NLS-1$
		}
		return result;
	}

	public long createLastChange(int codeVersion) {
		chekDbAvailable();
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, new StringBuilder("Create lastChange: ").toString()); //$NON-NLS-1$
		}

		long result = mDb.delete(DATABASE_LAST_CHANGE_TABLE //
				, null//
				, null);

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LAST_CHANGE_VERSION, codeVersion);

		result = mDb.insert(DATABASE_LAST_CHANGE_TABLE, null, initialValues);
		if (result == -1) {
			Log.e(TAG, "Error inserting row"); //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllTheaters() {

		return mDb.query(//
				DATABASE_THEATERS_TABLE//
				, new String[] { KEY_THEATER_ID//
						, KEY_THEATER_NAME //
						, KEY_THEATER_PHONE //
				}//
				, null//
				, null//
				, null//
				, null//
				, null//
				);
	}

	/**
	 * Return a Cursor over the list of all request in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllNearRequest() {

		return mDb.query(//
				DATABASE_NEAR_REQUEST_TABLE//
				, new String[] { KEY_NEAR_REQUEST_CITY_NAME //
				}//
				, null//
				, null//
				, null//
				, null//
				, null//
				);
	}

	/**
	 * Return a Cursor over the list of all request in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllMovieRequest() {

		return mDb.query(//
				DATABASE_MOVIE_REQUEST_TABLE//
				, new String[] { KEY_MOVIE_REQUEST_MOVIE_NAME //
						, KEY_MOVIE_REQUEST_CITY_NAME //
				}//
				, null//
				, null//
				, null//
				, null//
				, null//
				);
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchLastNearRequest() {

		return mDb.rawQuery(new StringBuilder("SELECT * FROM ").append(DATABASE_NEAR_REQUEST_TABLE)//
				.append(" WHERE ").append(KEY_NEAR_REQUEST_TIME).append(" = (")//
				.append("SELECT MAX(").append(KEY_NEAR_REQUEST_TIME).append(") FROM ").append(DATABASE_NEAR_REQUEST_TABLE).append(")").toString()//
				, null//
				);
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchLastMovieRequest() {

		return mDb.rawQuery(new StringBuilder("SELECT * FROM ").append(DATABASE_MOVIE_REQUEST_TABLE)//
				.append(" WHERE ").append(KEY_MOVIE_REQUEST_TIME).append(" = (")//
				.append("SELECT MAX(").append(KEY_MOVIE_REQUEST_TIME).append(") FROM ").append(DATABASE_MOVIE_REQUEST_TABLE).append(")").toString()//
				, null//
				);
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllFavTheaters() {
		return mDb.query(//
				DATABASE_FAV_THEATER_TABLE//
				, new String[] { KEY_FAV_TH_THEATER_ID//
						, KEY_FAV_TH_THEATER_NAME //
						, KEY_FAV_TH_THEATER_PLACE //
						, KEY_FAV_TH_THEATER_COUNRTY_CODE //
						, KEY_FAV_TH_THEATER_POSTAL_CODE //
						, KEY_FAV_TH_THEATER_LAT //
						, KEY_FAV_TH_THEATER_LONG //
				}//
				, null //
				, null//
				, null//
				, null//
				, null//
				);
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchTheater(String theaterId) {

		return mDb.query(//
				DATABASE_THEATERS_TABLE//
				, new String[] { KEY_THEATER_ID//
						, KEY_THEATER_NAME //
						, KEY_THEATER_PHONE //
				}//
				, new StringBuilder(KEY_THEATER_ID).append("='").append(theaterId) //$NON-NLS-1$
						.append("'").toString() // //$NON-NLS-1$
				, null//
				, null//
				, null//
				, null//
				);
	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param movieId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchMovie(String movieId) throws SQLException {

		Cursor mCursor =

		mDb.query(true //
				, DATABASE_MOVIE_TABLE //
				, new String[] { KEY_MOVIE_ID //
						, KEY_MOVIE_CID //
						, KEY_MOVIE_IMDB_ID //
						, KEY_MOVIE_NAME //
						, KEY_MOVIE_ENGLISH_NAME //
						, KEY_MOVIE_IMDB_DESC //
						, KEY_MOVIE_DESC //
						, KEY_MOVIE_TR_DESC //
						, KEY_MOVIE_IMG_URL //
						, KEY_MOVIE_WIKIPEDIA_URL //
						, KEY_MOVIE_LANG //
						, KEY_MOVIE_RATE //
						, KEY_MOVIE_STYLE //
						, KEY_MOVIE_TIME //
						, KEY_MOVIE_TIME_FORMAT //
						, KEY_MOVIE_ACTORS //
						, KEY_MOVIE_DIRECTORS //
				} //
				, new StringBuilder(KEY_MOVIE_ID).append("='").append(movieId) //$NON-NLS-1$
						.append("'").toString() // //$NON-NLS-1$
				, null //
				, null //
				, null //
				, null //
				, null//
		);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param movieId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchAllMovies() throws SQLException {

		Cursor mCursor =

		mDb.query(true //
				, DATABASE_MOVIE_TABLE //
				, new String[] { KEY_MOVIE_ID //
						, KEY_MOVIE_CID //
						, KEY_MOVIE_IMDB_ID //
						, KEY_MOVIE_NAME //
						, KEY_MOVIE_ENGLISH_NAME //
						, KEY_MOVIE_IMDB_DESC //
						, KEY_MOVIE_DESC //
						, KEY_MOVIE_TR_DESC //
						, KEY_MOVIE_IMG_URL //
						, KEY_MOVIE_WIKIPEDIA_URL //
						, KEY_MOVIE_LANG //
						, KEY_MOVIE_RATE //
						, KEY_MOVIE_STYLE //
						, KEY_MOVIE_TIME //
						, KEY_MOVIE_TIME_FORMAT //
						, KEY_MOVIE_ACTORS //
						, KEY_MOVIE_DIRECTORS //
				} //
				, null //
				, null //
				, null //
				, null //
				, null //
				, null//
		);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param movieId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchShowtime(String theaterId, String movieId) throws SQLException {

		Cursor mCursor =

		mDb.query(true //
				, DATABASE_SHOWTIME_TABLE //
				, new String[] { KEY_SHOWTIME_THEATER_ID //
						, KEY_SHOWTIME_MOVIE_ID //
						, KEY_SHOWTIME_TIME //
						, KEY_SHOWTIME_LANG //
						, KEY_SHOWTIME_RESERVATION_URL //
						, KEY_SHOWTIME_FORMAT_24 //
						, KEY_SHOWTIME_FORMAT_12 //
				} //
				, new StringBuilder(KEY_MOVIE_ID).append("='").append(movieId) //$NON-NLS-1$
						.append("' AND ").append(KEY_SHOWTIME_THEATER_ID) //$NON-NLS-1$
						.append("='").append(theaterId).append("'").toString()// //$NON-NLS-1$ //$NON-NLS-2$
				, null //
				, null //
				, null //
				, null //
				, null//
		);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param movieId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchFavShowtime(String theaterId, String movieId) throws SQLException {

		Cursor mCursor =

		mDb.query(true //
				, DATABASE_FAV_SHOWTIME_TABLE //
				, new String[] { KEY_FAV_SHOWTIME_THEATER_ID //
						, KEY_FAV_SHOWTIME_MOVIE_ID //
						, KEY_FAV_SHOWTIME_TIME //
						, KEY_FAV_SHOWTIME_LANG //
						, KEY_FAV_SHOWTIME_RESERVATION_URL //
				} //
				, new StringBuilder(KEY_MOVIE_ID).append("='").append(movieId) //$NON-NLS-1$
						.append("' AND ").append(KEY_FAV_SHOWTIME_THEATER_ID) //$NON-NLS-1$
						.append("='").append(theaterId).append("'").toString()// //$NON-NLS-1$ //$NON-NLS-2$
				, null //
				, null //
				, null //
				, null //
				, null//
		);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param movieId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchLocation(String theaterId) throws SQLException {

		Cursor mCursor =

		mDb.query(true //
				, DATABASE_LOCATION_TABLE //
				, new String[] { KEY_LOCALISATION_CITY_NAME //
						, KEY_LOCALISATION_COUNTRY_NAME //
						, KEY_LOCALISATION_COUNTRY_CODE //
						, KEY_LOCALISATION_POSTAL_CODE //
						, KEY_LOCALISATION_DISTANCE //
						, KEY_LOCALISATION_DISTANCE_TIME //
						, KEY_LOCALISATION_LATITUDE //
						, KEY_LOCALISATION_LONGITUDE //
						, KEY_LOCALISATION_SEARCH_QUERY //
				} //
				, new StringBuilder(KEY_LOCALISATION_THEATER_ID).append("='") //$NON-NLS-1$
						.append(theaterId).append("'").toString() // //$NON-NLS-1$
				, null //
				, null //
				, null //
				, null //
				, null//
		);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param movieId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchShowtime(String theaterId) throws SQLException {

		Cursor mCursor =

		mDb.query(true //
				, DATABASE_SHOWTIME_TABLE //
				, new String[] { KEY_SHOWTIME_THEATER_ID //
						, KEY_SHOWTIME_MOVIE_ID //
						, KEY_SHOWTIME_TIME //
						, KEY_SHOWTIME_LANG //
						, KEY_SHOWTIME_RESERVATION_URL //
						, KEY_SHOWTIME_FORMAT_24 //
						, KEY_SHOWTIME_FORMAT_12 //
				} //
				, new StringBuilder(KEY_SHOWTIME_THEATER_ID).append("='") //$NON-NLS-1$
						.append(theaterId).append("'").toString()// //$NON-NLS-1$
				, null //
				, null //
				, null //
				, null //
				, null//
		);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param movieId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchFavShowtime(String theaterId) throws SQLException {

		Cursor mCursor =

		mDb.query(true //
				, DATABASE_FAV_SHOWTIME_TABLE //
				, new String[] { KEY_FAV_SHOWTIME_THEATER_ID //
						, KEY_FAV_SHOWTIME_MOVIE_ID //
						, KEY_FAV_SHOWTIME_TIME //
						, KEY_FAV_SHOWTIME_LANG //
						, KEY_FAV_SHOWTIME_RESERVATION_URL //
				} //
				, new StringBuilder(KEY_FAV_SHOWTIME_THEATER_ID).append("='") //$NON-NLS-1$
						.append(theaterId).append("'").toString()// //$NON-NLS-1$
				, null //
				, null //
				, null //
				, null //
				, null//
		);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param widgetId
	 * 
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchAllWidgetShowtime(int widgetId) throws SQLException {

		Cursor mCursor =

		mDb.query(true //
				, DATABASE_WIDGET_MOVIE_TABLE //
				, new String[] { KEY_WIDGET_MOVIE_ID //
						, KEY_WIDGET_MOVIE_NAME //
						, KEY_WIDGET_MOVIE_EN_NAME //
						, KEY_WIDGET_MOVIE_MID //
						, KEY_WIDGET_MOVIE_LENGTH //
						, KEY_WIDGET_MOVIE_SHOWTIME //
						, KEY_WIDGET_MOVIE_SHOWTIME_LANG //
						, KEY_WIDGET_MOVIE_SHOWTIME_RESERVATION //
				} //
				, KEY_WIDGET_THEATER_WIDGET_ID + " = ?" //
				, new String[] { String.valueOf(widgetId) } //
				, null //
				, null //
				, null //
				, null//
		);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchWidgetMovie(String movieId, int widgetId) throws SQLException {

		Cursor mCursor =

		mDb.query(true //
				, DATABASE_WIDGET_MOVIE_TABLE //
				, new String[] { KEY_WIDGET_MOVIE_ID //
						, KEY_WIDGET_MOVIE_NAME //
						, KEY_WIDGET_MOVIE_EN_NAME //
						, KEY_WIDGET_MOVIE_MID //
						, KEY_WIDGET_MOVIE_LENGTH //
						, KEY_WIDGET_MOVIE_SHOWTIME //
						, KEY_WIDGET_MOVIE_SHOWTIME_LANG //
						, KEY_WIDGET_MOVIE_SHOWTIME_RESERVATION //
				} //
				, new StringBuilder(KEY_WIDGET_MOVIE_MID).append("='") //$NON-NLS-1$
						.append(movieId).append("'")//
						.append(" AND ").append(KEY_WIDGET_MOVIE_WIDGET_ID).append(" = ?")//
						.toString()// //$NON-NLS-1$
				, new String[] { String.valueOf(widgetId) } //
				, null //
				, null //
				, null //
				, null//
		);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @param widgetId
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchWidgetTheater(int widgetId) {

		return mDb.query(//
				DATABASE_WIDGET_TABLE//
				, new String[] { KEY_WIDGET_THEATER_ID//
						, KEY_WIDGET_THEATER_NAME //
						, KEY_WIDGET_THEATER_PLACE //
						, KEY_WIDGET_THEATER_COUNRTY_CODE //
						, KEY_WIDGET_THEATER_POSTAL_CODE //
						, KEY_WIDGET_THEATER_LAT //
						, KEY_WIDGET_THEATER_LONG //
						, KEY_WIDGET_THEATER_DATE //
				}//
				, KEY_WIDGET_THEATER_WIDGET_ID + " = ?"//
				, new String[] { String.valueOf(widgetId) }//
				, null//
				, null//
				, null//
				);
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchCurentMovie() {

		return mDb.query(//
				DATABASE_CURENT_MOVIE_TABLE//
				, new String[] { KEY_CURENT_MOVIE_THEATER_ID//
						, KEY_CURENT_MOVIE_MOVIE_ID //
						, KEY_CURENT_MOVIE_WIDGET_ID //
				}//
				, null //
				, null//
				, null//
				, null//
				, null//
				);
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllMovieFavShowtime() {
		return mDb.query(//
				DATABASE_FAV_SHOWTIME_TABLE//
				, new String[] { KEY_FAV_SHOWTIME_MOVIE_ID //
				}//
				, null //
				, null//
				, null//
				, null//
				, null//
				);
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchSkyHookRegistration() {

		return mDb.query(//
				DATABASE_SKYHOOK_REGISTRATION_TABLE//
				, new String[] { KEY_SKYHOOK_REGISTRATION //
				}//
				, null//
				, null//
				, null//
				, null//
				, null//
				);
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchInResults(TheaterBean theater) {

		return mDb.query(//
				DATABASE_THEATERS_TABLE//
				, new String[] { KEY_THEATER_ID //
				}//
				, KEY_THEATER_ID + "= ? "//
				, new String[] { theater.getId() }//
				, null//
				, null//
				, null//
				);
	}

	public Cursor fetchLastChange() {
		return mDb.query(//
				DATABASE_LAST_CHANGE_TABLE//
				, new String[] { KEY_LAST_CHANGE_VERSION //
				}//
				, null//
				, null//
				, null//
				, null//
				, null//
				);
	}

	/**
	 * 
	 * @param movieId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchReviews(String movieId) throws SQLException {

		Cursor mCursor =

		mDb.query(true //
				, DATABASE_REVIEW_TABLE //
				, new String[] { KEY_REVIEW_AUTHOR //
						, KEY_REVIEW_CONTENT //
						, KEY_REVIEW_RATE //
						, KEY_REVIEW_SOURCE //
						, KEY_REVIEW_URL_REVIEW //
				} //
				, new StringBuilder(KEY_REVIEW_MOVIE_MID).append("='") //$NON-NLS-1$
						.append(movieId).append("'").toString() // //$NON-NLS-1$
				, null //
				, null //
				, null //
				, null //
				, null//
		);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * 
	 * @param movieId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchVideos(String movieId) throws SQLException {

		Cursor mCursor =

		mDb.query(true //
				, DATABASE_VIDEO_TABLE //
				, new String[] { KEY_VIDEO_NAME //
						, KEY_VIDEO_URL_IMG //
						, KEY_VIDEO_URL_VIDEO //
				} //
				, new StringBuilder(KEY_VIDEO_MOVIE_MID).append("='") //$NON-NLS-1$
						.append(movieId).append("'").toString() // //$NON-NLS-1$
				, null //
				, null //
				, null //
				, null //
				, null//
		);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public void deleteTheatersShowtimeRequestAndLocation() {
		chekDbAvailable();
		int result = mDb.delete(DATABASE_THEATERS_TABLE //
				, null //
				, null);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, result + " Theaters where remove from theater table"); //$NON-NLS-1$
		}
		result = mDb.delete(DATABASE_SHOWTIME_TABLE //
				, null //
				, null);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, result + " Showtimes where remove from showtime table"); //$NON-NLS-1$
		}
		result = mDb.delete(DATABASE_LOCATION_TABLE //
				, null //
				, null);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, result + " Locations where remove from location table"); //$NON-NLS-1$
		}
		Calendar dateEraseRequest = Calendar.getInstance();
		int dayOfWeek = dateEraseRequest.get(Calendar.DAY_OF_WEEK);
		dateEraseRequest.set(Calendar.DAY_OF_WEEK, dayOfWeek - 1);
		mDb.execSQL(new StringBuilder("DELETE FROM ").append(DATABASE_FAV_SHOWTIME_TABLE) //
				.append(" WHERE ").append(KEY_FAV_SHOWTIME_THEATER_ID).append(" NOT IN (") //
				.append("SELECT ").append(KEY_FAV_TH_THEATER_ID).append(" FROM ").append(DATABASE_FAV_THEATER_TABLE) //
				.append(")") //
				.append(" OR ").append(KEY_FAV_SHOWTIME_TIME).append("<").append(dateEraseRequest.getTimeInMillis()) //
				.toString() //
		);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, " Fav Showtimes where remove from fav_showtime table"); //$NON-NLS-1$
		}
		dateEraseRequest.set(Calendar.DAY_OF_MONTH, dayOfWeek - 7);
		result = mDb.delete(DATABASE_NEAR_REQUEST_TABLE //
				, new StringBuilder(KEY_NEAR_REQUEST_TIME).append("<").append(dateEraseRequest.getTimeInMillis()).toString() // //$NON-NLS-1$
				, null);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, result + " Request where remove from near_request table"); //$NON-NLS-1$
		}
		result = mDb.delete(DATABASE_MOVIE_REQUEST_TABLE //
				, new StringBuilder(KEY_MOVIE_REQUEST_TIME).append("<").append(dateEraseRequest.getTimeInMillis()).toString() // //$NON-NLS-1$
				, null);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, result + " Request where remove from movie_request table"); //$NON-NLS-1$
		}
	}

	public void deleteWidgetShowtime(int widgetId) {
		chekDbAvailable();
		int result = mDb.delete(DATABASE_WIDGET_MOVIE_TABLE//
				, KEY_WIDGET_MOVIE_WIDGET_ID + " = ?" //
				, new String[] { String.valueOf(widgetId) }//
				);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, result + " Showtimes where remove from widget_movie table"); //$NON-NLS-1$
		}
	}

	public void deleteWidget(int widgetId) {
		int result = mDb.delete(DATABASE_WIDGET_TABLE //
				, KEY_WIDGET_THEATER_WIDGET_ID + " = ?" //
				, new String[] { String.valueOf(widgetId) } //
				);
		// If nothing was removed we have to delete the old widget
		if (result == 0) {
			mDb.delete(DATABASE_WIDGET_TABLE //
					, KEY_WIDGET_THEATER_WIDGET_ID + " is null" //
					, null //
			);

		}
	}

	public void deleteFavorite(String theaterId) {
		chekDbAvailable();
		StringBuilder querySelect = new StringBuilder(KEY_FAV_TH_THEATER_ID).append(" = '"); //$NON-NLS-1$
		querySelect.append(theaterId);
		querySelect.append("'"); //$NON-NLS-1$
		int result = mDb.delete(DATABASE_FAV_THEATER_TABLE //
				, querySelect.toString()//
				, null);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, result + " Favorite where remove from favTheater table"); //$NON-NLS-1$
		}
	}

	public void deleteMovies(Set<String> movieIdList) {
		chekDbAvailable();
		StringBuilder querySelect = new StringBuilder(KEY_MOVIE_ID).append(" NOT IN ('"); //$NON-NLS-1$
		StringBuilder queryReviewSelect = new StringBuilder(KEY_REVIEW_MOVIE_MID).append(" NOT IN ('"); //$NON-NLS-1$
		StringBuilder queryVideoSelect = new StringBuilder(KEY_VIDEO_MOVIE_MID).append(" NOT IN ('"); //$NON-NLS-1$
		boolean first = true;
		for (String movieId : movieIdList) {
			if (!first) {
				querySelect.append("','"); //$NON-NLS-1$
				queryReviewSelect.append("','"); //$NON-NLS-1$
				queryVideoSelect.append("','"); //$NON-NLS-1$
			} else {
				first = false;
			}
			querySelect.append(movieId);
			queryReviewSelect.append(movieId);
			queryReviewSelect.append(movieId);
		}
		querySelect.append("')"); //$NON-NLS-1$
		queryReviewSelect.append("')"); //$NON-NLS-1$
		queryVideoSelect.append("')"); //$NON-NLS-1$
		int result = mDb.delete(DATABASE_MOVIE_TABLE //
				, querySelect.toString()//
				, null);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, result + " Movies where remove from movie table"); //$NON-NLS-1$
		}
		result = mDb.delete(DATABASE_REVIEW_TABLE //
				, queryReviewSelect.toString()//
				, null);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, result + " Reviews where remove from reviews table"); //$NON-NLS-1$
		}
		result = mDb.delete(DATABASE_VIDEO_TABLE //
				, queryVideoSelect.toString()//
				, null);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, result + " Videos where remove from video table"); //$NON-NLS-1$
		}
	}

	public void clean() throws SQLException {
		chekDbAvailable();
		mDb.execSQL(DROP_THEATER_TABLE);
		mDb.execSQL(DROP_FAV_THEATER_TABLE);
		mDb.execSQL(DROP_FAV_SHOWTIME_TABLE);
		mDb.execSQL(DROP_MOVIE_TABLE);
		mDb.execSQL(DROP_SHOWTIME_TABLE);
		mDb.execSQL(DROP_LOCATION_TABLE);
		mDb.execSQL(DROP_NEAR_REQUEST_TABLE);
		mDb.execSQL(DROP_MOVIE_REQUEST_TABLE);
		mDb.execSQL(DROP_WIDGET_TABLE);
		mDb.execSQL(DROP_WIDGET_MOVIE_TABLE);
		mDb.execSQL(DROP_CURENT_MOVIE_TABLE);
		mDb.execSQL(DROP_REVIEW_TABLE);
		mDb.execSQL(DROP_VIDEO_TABLE);
		mDb.execSQL(DROP_SKYHOOK_REGISTRATION_TABLE);
		mDb.execSQL(DROP_LAST_CHANGE_TABLE);
		mDb.execSQL(DATABASE_CREATE_MOVIE_TABLE);
		mDb.execSQL(DATABASE_CREATE_THEATER_TABLE);
		mDb.execSQL(DATABASE_CREATE_FAV_THEATER_TABLE);
		mDb.execSQL(DATABASE_CREATE_FAV_SHOWTIME_TABLE);
		mDb.execSQL(DATABASE_CREATE_SHOWTIME_TABLE);
		mDb.execSQL(DATABASE_CREATE_LOCATION_TABLE);
		mDb.execSQL(DATABASE_CREATE_NEAR_REQUEST_TABLE);
		mDb.execSQL(DATABASE_CREATE_MOVIE_REQUEST_TABLE);
		mDb.execSQL(DATABASE_CREATE_WIDGET_TABLE);
		mDb.execSQL(DATABASE_CREATE_CURENT_MOVIE_TABLE);
		mDb.execSQL(DATABASE_CREATE_SKYHOOK_REGISTRATION_TABLE);
		mDb.execSQL(DATABASE_CREATE_LAST_CHANGE_TABLE);
		mDb.execSQL(DATABASE_REVIEW_TABLE);
		mDb.execSQL(DATABASE_VIDEO_TABLE);
	}

	private void chekDbAvailable() {
		do {
			if (isDbLockedByCurrentThread() || isDbLockedByOtherThreads()) {
				try {
					if (Log.isLoggable(TAG, Log.DEBUG)) {
						Log.d(TAG, "DbBusy wait for availabality");
					}
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		} while (isOpen() && (isDbLockedByCurrentThread() || isDbLockedByOtherThreads()));
		if (!isOpen()) {
			throw new SQLException("DBClosed");
		}
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "DbAvailable");
		}
	}

}
