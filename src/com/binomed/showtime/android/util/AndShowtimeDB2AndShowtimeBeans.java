package com.binomed.showtime.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.TheaterBean;

public abstract class AndShowtimeDB2AndShowtimeBeans {

	private static final String TAG = "AndShowDB2Beans"; //$NON-NLS-1$

	/**
	 * Extract all movies from db
	 * 
	 * @param mDbHelper
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, MovieBean> extractMovies(AndShowtimeDbAdapter mDbHelper) throws SQLException {
		Log.d(TAG, "Extract movies"); //$NON-NLS-1$
		Map<String, MovieBean> movieMap = new HashMap<String, MovieBean>();
		Cursor movieCursor = mDbHelper.fetchAllMovies();
		MovieBean movieBean = null;
		if (movieCursor.moveToFirst()) {
			do {
				movieBean = extractMovie(movieCursor);
				movieMap.put(movieBean.getId(), movieBean);
				BeanManagerFactory.putMovie(movieBean);
			} while (movieCursor.moveToNext());
			Log.d(TAG, movieMap.size() + " movies extract"); //$NON-NLS-1$
		} else {
			Log.d(TAG, "No movies found"); //$NON-NLS-1$
		}
		if (movieCursor != null) {
			movieCursor.close();
		}
		return movieMap;
	}

	/**
	 * Extract a movie
	 * 
	 * @param movieCursor
	 * @return
	 * @throws SQLException
	 */
	public static MovieBean extractMovie(Cursor movieCursor) throws SQLException {
		Log.d(TAG, "Extract a movie"); //$NON-NLS-1$
		MovieBean movieBean = new MovieBean();

		int columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_ID);
		movieBean.setId(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_CID);
		movieBean.setCid(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_IMDB_ID);
		movieBean.setImdbId(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_NAME);
		movieBean.setMovieName(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_ENGLISH_NAME);
		movieBean.setEnglishMovieName(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_IMDB_DESC);
		movieBean.setImdbDesrciption(movieCursor.getInt(columnIndex) == 1);

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_DESC);
		movieBean.setDescription(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_LANG);
		movieBean.setLang(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_IMG_URL);
		movieBean.setUrlImg(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_WIKIPEDIA_URL);
		movieBean.setUrlWikipedia(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_RATE);
		movieBean.setRate(movieCursor.getDouble(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_TIME);
		movieBean.setMovieTime(movieCursor.getLong(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_STYLE);
		movieBean.setStyle(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_ACTORS);
		movieBean.setActorList(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_MOVIE_DIRECTORS);
		movieBean.setDirectorList(movieCursor.getString(columnIndex));

		return movieBean;
	}

	/**
	 * Extract all theaters
	 * 
	 * @param mDbHelper
	 * @return
	 * @throws SQLException
	 */
	public static List<TheaterBean> extractTheaterList(AndShowtimeDbAdapter mDbHelper) throws SQLException {
		Log.d(TAG, "Extract theaters"); //$NON-NLS-1$
		List<TheaterBean> theaterBeanList = new ArrayList<TheaterBean>();
		Cursor theaterCursor = mDbHelper.fetchAllTheaters();
		TheaterBean theaterBean = null;
		if (theaterCursor.moveToFirst()) {
			do {
				theaterBean = extractTheaterBean(theaterCursor, mDbHelper);
				BeanManagerFactory.putTheater(theaterBean);
				theaterBeanList.add(theaterBean);
			} while (theaterCursor.moveToNext());
			Log.d(TAG, theaterBeanList.size() + " theaters where extract"); //$NON-NLS-1$
		} else {
			Log.d(TAG, "No theater where extract"); //$NON-NLS-1$
		}
		if (theaterCursor != null) {
			theaterCursor.close();
		}
		return theaterBeanList;
	}

	/**
	 * Extract all theaters
	 * 
	 * @param mDbHelper
	 * @return
	 * @throws SQLException
	 */
	public static List<TheaterBean> extractFavTheaterList(AndShowtimeDbAdapter mDbHelper) throws SQLException {
		Log.d(TAG, "Extract theaters"); //$NON-NLS-1$
		List<TheaterBean> theaterBeanList = new ArrayList<TheaterBean>();
		Cursor theaterFavCursor = mDbHelper.fetchAllFavTheaters();
		TheaterBean theaterBean = null;
		LocalisationBean location = null;
		int columnIndex = 0;
		if (theaterFavCursor.moveToFirst()) {
			do {
				theaterBean = new TheaterBean();

				columnIndex = theaterFavCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_FAV_TH_THEATER_ID);
				theaterBean.setId(theaterFavCursor.getString(columnIndex));

				columnIndex = theaterFavCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_FAV_TH_THEATER_NAME);
				theaterBean.setTheaterName(theaterFavCursor.getString(columnIndex));

				columnIndex = theaterFavCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_FAV_TH_THEATER_PLACE);
				location = new LocalisationBean();
				location.setCityName(theaterFavCursor.getString(columnIndex));

				columnIndex = theaterFavCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_FAV_TH_THEATER_COUNRTY_CODE);
				location.setCountryNameCode(theaterFavCursor.getString(columnIndex));

				columnIndex = theaterFavCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_FAV_TH_THEATER_POSTAL_CODE);
				location.setPostalCityNumber(theaterFavCursor.getString(columnIndex));

				columnIndex = theaterFavCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_FAV_TH_THEATER_LAT);
				location.setLatitude(theaterFavCursor.getDouble(columnIndex));

				columnIndex = theaterFavCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_FAV_TH_THEATER_LONG);
				location.setLongitude(theaterFavCursor.getDouble(columnIndex));

				theaterBean.setPlace(location);

				BeanManagerFactory.putTheater(theaterBean);
				theaterBeanList.add(theaterBean);
			} while (theaterFavCursor.moveToNext());
			Log.d(TAG, theaterBeanList.size() + " theaters where extract"); //$NON-NLS-1$
		} else {
			Log.d(TAG, "No theater where extract"); //$NON-NLS-1$
		}
		if (theaterFavCursor != null) {
			theaterFavCursor.close();
		}
		return theaterBeanList;
	}

	/**
	 * Extract a theater from cursor
	 * 
	 * @param theaterCursor
	 * @param mDbHelper
	 * @return
	 * @throws SQLException
	 */
	public static TheaterBean extractTheaterBean(Cursor theaterCursor, AndShowtimeDbAdapter mDbHelper) throws SQLException {
		Log.d(TAG, "Extract a Theater"); //$NON-NLS-1$
		TheaterBean theaterBean = new TheaterBean();
		theaterBean.setMovieMap(new HashMap<String, List<Long>>());

		theaterBean.setMovieMap(new HashMap<String, List<Long>>());
		int columnIndex = theaterCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_THEATER_ID);
		theaterBean.setId(theaterCursor.getString(columnIndex));

		columnIndex = theaterCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_THEATER_NAME);
		theaterBean.setTheaterName(theaterCursor.getString(columnIndex));

		// Fetch showtimes link to theater
		Cursor showtimeCursor = mDbHelper.fetchShowtime(theaterBean.getId());
		Log.d(TAG, "Extract showtime from theater : " + theaterBean.getTheaterName()); //$NON-NLS-1$
		if (showtimeCursor.moveToFirst()) {
			List<Long> showtimeList = null;
			do {
				columnIndex = showtimeCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_SHOWTIME_MOVIE_ID);
				String movieId = showtimeCursor.getString(columnIndex);

				showtimeList = theaterBean.getMovieMap().get(movieId);
				if (showtimeList == null) {
					showtimeList = new ArrayList<Long>();
					theaterBean.getMovieMap().put(movieId, showtimeList);
				}

				columnIndex = showtimeCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_SHOWTIME_TIME);
				showtimeList.add(showtimeCursor.getLong(columnIndex));
			} while (showtimeCursor.moveToNext());
			Log.d(TAG, showtimeList.size() + " Showtimes where extract for theater : " + theaterBean.getTheaterName()); //$NON-NLS-1$
		} else {
			Log.d(TAG, "No showtime where extract for theater : " + theaterBean.getTheaterName()); //$NON-NLS-1$
		}
		if (showtimeCursor != null) {
			showtimeCursor.close();
		}

		// Fetch location link to theater
		Cursor locationCursor = mDbHelper.fetchLocation(theaterBean.getId());
		Log.d(TAG, "Extract Localisation of theater : " + theaterBean.getTheaterName()); //$NON-NLS-1$
		if (locationCursor.moveToFirst()) {
			theaterBean.setPlace(extractLocalisationBean(locationCursor));
		} else {
			Log.d(TAG, "No localisation where extract"); //$NON-NLS-1$
		}
		if (locationCursor != null) {
			locationCursor.close();
		}
		return theaterBean;
	}

	/**
	 * 
	 * Extract locatio from cursor
	 * 
	 * @param locationCursor
	 * @return
	 * @throws SQLException
	 */
	public static LocalisationBean extractLocalisationBean(Cursor locationCursor) throws SQLException {
		Log.d(TAG, "Extract Localisation"); //$NON-NLS-1$
		LocalisationBean localisationBean = new LocalisationBean();

		int columnIndex = locationCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_LOCALISATION_CITY_NAME);
		localisationBean.setCityName(locationCursor.getString(columnIndex));

		columnIndex = locationCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_LOCALISATION_COUNTRY_NAME);
		localisationBean.setCountryName(locationCursor.getString(columnIndex));

		columnIndex = locationCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_LOCALISATION_COUNTRY_CODE);
		localisationBean.setCountryNameCode(locationCursor.getString(columnIndex));

		columnIndex = locationCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_LOCALISATION_POSTAL_CODE);
		localisationBean.setPostalCityNumber(locationCursor.getString(columnIndex));

		columnIndex = locationCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_LOCALISATION_DISTANCE);
		localisationBean.setDistance(locationCursor.getFloat(columnIndex));

		columnIndex = locationCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_LOCALISATION_LATITUDE);
		localisationBean.setLatitude(locationCursor.getDouble(columnIndex));

		columnIndex = locationCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_LOCALISATION_LONGITUDE);
		localisationBean.setLongitude(locationCursor.getDouble(columnIndex));

		columnIndex = locationCursor.getColumnIndex(AndShowtimeDbAdapter.KEY_LOCALISATION_SEARCH_QUERY);
		localisationBean.setSearchQuery(locationCursor.getString(columnIndex));

		return localisationBean;
	}

}
