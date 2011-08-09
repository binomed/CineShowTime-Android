package com.binomed.showtime.android.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.ReviewBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.model.YoutubeBean;

public abstract class CineShowtimeDB2AndShowtimeBeans {

	private static final String TAG = "AndShowDB2Beans"; //$NON-NLS-1$

	/**
	 * Extract all movies from db
	 * 
	 * @param mDbHelper
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, MovieBean> extractMovies(CineShowtimeDbAdapter mDbHelper, List<TheaterBean> theaterList) throws SQLException {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Extract movies"); //$NON-NLS-1$
		}
		Map<String, MovieBean> movieMap = new HashMap<String, MovieBean>();
		Cursor movieCursor = mDbHelper.fetchAllMovies();
		Cursor reviewsCursor = null;
		Cursor videosCursor = null;

		Map<String, List<String>> mapMovieIdThList = new HashMap<String, List<String>>();

		if (theaterList != null) {
			List<String> thIdList = null;
			for (TheaterBean thTmp : theaterList) {
				for (String movieId : thTmp.getMovieMap().keySet()) {
					thIdList = mapMovieIdThList.get(movieId);
					if (thIdList == null) {
						thIdList = new ArrayList<String>();
						mapMovieIdThList.put(movieId, thIdList);
					}
					thIdList.add(thTmp.getId());
				}
			}
		}

		try {
			MovieBean movieBean = null;
			if (movieCursor.moveToFirst()) {
				do {
					movieBean = extractMovie(movieCursor);
					movieBean.setTheaterList(mapMovieIdThList.get(movieBean.getId()));
					reviewsCursor = mDbHelper.fetchReviews(movieBean.getId());
					try {
						if (reviewsCursor.moveToFirst()) {
							movieBean.setReviews(new ArrayList<ReviewBean>());
							do {
								movieBean.getReviews().add(extractReview(reviewsCursor));
							} while (reviewsCursor.moveToNext());
						}
					} finally {
						if (reviewsCursor != null) {
							reviewsCursor.close();
						}
					}
					videosCursor = mDbHelper.fetchVideos(movieBean.getId());
					try {
						if (videosCursor.moveToFirst()) {
							movieBean.setYoutubeVideos(new ArrayList<YoutubeBean>());
							do {
								movieBean.getYoutubeVideos().add(extractVideo(videosCursor));
							} while (videosCursor.moveToNext());
						}
					} finally {
						if (videosCursor != null) {
							videosCursor.close();
						}
					}
					movieMap.put(movieBean.getId(), movieBean);
				} while (movieCursor.moveToNext());
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, movieMap.size() + " movies extract"); //$NON-NLS-1$
				}
			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No movies found"); //$NON-NLS-1$
				}
			}
		} finally {
			if (movieCursor != null) {
				movieCursor.close();
			}
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
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Extract a movie"); //$NON-NLS-1$
		}
		MovieBean movieBean = new MovieBean();

		int columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_ID);
		movieBean.setId(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_CID);
		movieBean.setCid(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_IMDB_ID);
		movieBean.setImdbId(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_NAME);
		movieBean.setMovieName(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_ENGLISH_NAME);
		movieBean.setEnglishMovieName(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_IMDB_DESC);
		movieBean.setImdbDesrciption(movieCursor.getInt(columnIndex) == 1);

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_DESC);
		movieBean.setDescription(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_LANG);
		movieBean.setLang(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_IMG_URL);
		movieBean.setUrlImg(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_WIKIPEDIA_URL);
		movieBean.setUrlWikipedia(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_RATE);
		movieBean.setRate(movieCursor.getDouble(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_TIME);
		movieBean.setMovieTime(movieCursor.getLong(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_STYLE);
		movieBean.setStyle(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_ACTORS);
		movieBean.setActorList(movieCursor.getString(columnIndex));

		columnIndex = movieCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_DIRECTORS);
		movieBean.setDirectorList(movieCursor.getString(columnIndex));

		return movieBean;
	}

	/**
	 * Extract a Review
	 * 
	 * @param reviewCursor
	 * @return
	 * @throws SQLException
	 */
	public static ReviewBean extractReview(Cursor reviewCursor) throws SQLException {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Extract a review"); //$NON-NLS-1$
		}
		ReviewBean reviewBean = new ReviewBean();

		int columnIndex = reviewCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_REVIEW_RATE);
		reviewBean.setRate(reviewCursor.getFloat(columnIndex));

		columnIndex = reviewCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_REVIEW_AUTHOR);
		reviewBean.setAuthor(reviewCursor.getString(columnIndex));

		columnIndex = reviewCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_REVIEW_CONTENT);
		reviewBean.setReview(reviewCursor.getString(columnIndex));

		columnIndex = reviewCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_REVIEW_SOURCE);
		reviewBean.setSource(reviewCursor.getString(columnIndex));

		columnIndex = reviewCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_REVIEW_URL_REVIEW);
		reviewBean.setUrlReview(reviewCursor.getString(columnIndex));

		return reviewBean;
	}

	/**
	 * Extract a Video
	 * 
	 * @param videoCursor
	 * @return
	 * @throws SQLException
	 */
	public static YoutubeBean extractVideo(Cursor videoCursor) throws SQLException {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Extract a video"); //$NON-NLS-1$
		}
		YoutubeBean videoBean = new YoutubeBean();

		int columnIndex = videoCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_VIDEO_NAME);
		videoBean.setVideoName(videoCursor.getString(columnIndex));

		columnIndex = videoCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_VIDEO_URL_IMG);
		videoBean.setUrlImg(videoCursor.getString(columnIndex));

		columnIndex = videoCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_VIDEO_URL_VIDEO);
		videoBean.setUrlVideo(videoCursor.getString(columnIndex));

		return videoBean;
	}

	/**
	 * Extract all theaters
	 * 
	 * @param mDbHelper
	 * @return
	 * @throws SQLException
	 */
	public static List<TheaterBean> extractTheaterList(CineShowtimeDbAdapter mDbHelper) throws SQLException {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Extract theaters"); //$NON-NLS-1$
		}
		List<TheaterBean> theaterBeanList = new ArrayList<TheaterBean>();
		Cursor theaterCursor = mDbHelper.fetchAllTheaters();
		TheaterBean theaterBean = null;
		try {
			if (theaterCursor.moveToFirst()) {
				do {
					theaterBean = extractTheaterBean(theaterCursor, mDbHelper);
					theaterBeanList.add(theaterBean);
				} while (theaterCursor.moveToNext());
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, theaterBeanList.size() + " theaters where extract"); //$NON-NLS-1$
				}
			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No theater where extract"); //$NON-NLS-1$
				}
			}
		} finally {
			if (theaterCursor != null) {
				theaterCursor.close();
			}
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
	public static List<TheaterBean> extractFavTheaterList(CineShowtimeDbAdapter mDbHelper) throws SQLException {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Extract theaters"); //$NON-NLS-1$
		}
		List<TheaterBean> theaterBeanList = new ArrayList<TheaterBean>();
		Cursor theaterFavCursor = mDbHelper.fetchAllFavTheaters();
		TheaterBean theaterBean = null;
		LocalisationBean location = null;
		int columnIndex = 0;
		try {
			if (theaterFavCursor.moveToFirst()) {
				do {
					theaterBean = new TheaterBean();

					columnIndex = theaterFavCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_FAV_TH_THEATER_ID);
					theaterBean.setId(theaterFavCursor.getString(columnIndex));

					columnIndex = theaterFavCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_FAV_TH_THEATER_NAME);
					theaterBean.setTheaterName(theaterFavCursor.getString(columnIndex));

					columnIndex = theaterFavCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_FAV_TH_THEATER_PLACE);
					location = new LocalisationBean();
					location.setCityName(theaterFavCursor.getString(columnIndex));

					columnIndex = theaterFavCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_FAV_TH_THEATER_COUNRTY_CODE);
					location.setCountryNameCode(theaterFavCursor.getString(columnIndex));

					columnIndex = theaterFavCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_FAV_TH_THEATER_POSTAL_CODE);
					location.setPostalCityNumber(theaterFavCursor.getString(columnIndex));

					columnIndex = theaterFavCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_FAV_TH_THEATER_LAT);
					location.setLatitude(theaterFavCursor.getDouble(columnIndex));

					columnIndex = theaterFavCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_FAV_TH_THEATER_LONG);
					location.setLongitude(theaterFavCursor.getDouble(columnIndex));

					theaterBean.setPlace(location);

					theaterBeanList.add(theaterBean);
				} while (theaterFavCursor.moveToNext());
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, theaterBeanList.size() + " theaters where extract"); //$NON-NLS-1$
				}
			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No theater where extract"); //$NON-NLS-1$
				}
			}
		} finally {
			if (theaterFavCursor != null) {
				theaterFavCursor.close();
			}
		}
		return theaterBeanList;
	}

	/**
	 * Extract theater of widget
	 * 
	 * @param mDbHelper
	 * @return
	 * @throws SQLException
	 */
	public static TheaterBean extractWidgetTheater(CineShowtimeDbAdapter mDbHelper, Calendar dateSearch, int widgetId) throws SQLException {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Extract widget theater"); //$NON-NLS-1$
		}
		TheaterBean theaterBean = null;
		LocalisationBean location = null;
		Cursor theaterWidgetCursor = mDbHelper.fetchWidgetTheater(widgetId);
		int columnIndex = 0;
		try {
			if (theaterWidgetCursor.moveToFirst()) {

				theaterBean = new TheaterBean();
				theaterBean.setMovieMap(new HashMap<String, List<ProjectionBean>>());

				columnIndex = theaterWidgetCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_THEATER_ID);
				theaterBean.setId(theaterWidgetCursor.getString(columnIndex));

				columnIndex = theaterWidgetCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_THEATER_NAME);
				theaterBean.setTheaterName(theaterWidgetCursor.getString(columnIndex));

				columnIndex = theaterWidgetCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_THEATER_PLACE);
				location = new LocalisationBean();
				location.setCityName(theaterWidgetCursor.getString(columnIndex));

				columnIndex = theaterWidgetCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_THEATER_COUNRTY_CODE);
				location.setCountryNameCode(theaterWidgetCursor.getString(columnIndex));

				columnIndex = theaterWidgetCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_THEATER_POSTAL_CODE);
				location.setPostalCityNumber(theaterWidgetCursor.getString(columnIndex));

				columnIndex = theaterWidgetCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_THEATER_LAT);
				location.setLatitude(theaterWidgetCursor.getDouble(columnIndex));

				columnIndex = theaterWidgetCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_THEATER_LONG);
				location.setLongitude(theaterWidgetCursor.getDouble(columnIndex));

				theaterBean.setPlace(location);

				columnIndex = theaterWidgetCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_THEATER_DATE);
				Long lastSearch = theaterWidgetCursor.getLong(columnIndex);
				if ((lastSearch != null) && (lastSearch > 0)) {
					dateSearch.setTimeInMillis(lastSearch);
				} else {
					dateSearch.add(Calendar.DAY_OF_MONTH, -1);
				}

			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No theater where extract"); //$NON-NLS-1$
				}
			}
		} finally {
			if (theaterWidgetCursor != null) {
				theaterWidgetCursor.close();
			}
		}
		return theaterBean;
	}

	/**
	 * Extract theater of widget
	 * 
	 * @param mDbHelper
	 * @param widgetId
	 * @return
	 * @throws SQLException
	 */
	public static Map<MovieBean, List<ProjectionBean>> extractWidgetShowtimes(CineShowtimeDbAdapter mDbHelper, int widgetId) throws SQLException {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Extract widget showtimes"); //$NON-NLS-1$
		}
		Map<MovieBean, List<ProjectionBean>> movieShowTimeMap = new HashMap<MovieBean, List<ProjectionBean>>();
		Map<String, MovieBean> movieMap = new HashMap<String, MovieBean>();
		Map<String, List<ProjectionBean>> showTimeMap = new HashMap<String, List<ProjectionBean>>();
		List<ProjectionBean> showTimeList = null;
		MovieBean movieBean = null;
		ProjectionBean projectionBean = null;
		String movieId;
		Cursor movieWidgetShowtimeCursor = mDbHelper.fetchAllWidgetShowtime(widgetId);
		int columnIndex = 0;
		try {
			if (movieWidgetShowtimeCursor.moveToFirst()) {
				do {
					columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_MID);
					movieId = movieWidgetShowtimeCursor.getString(columnIndex);
					movieBean = movieMap.get(movieId);
					if (movieBean == null) {
						movieBean = new MovieBean();
						movieMap.put(movieId, movieBean);

						movieBean.setId(movieId);

						columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_NAME);
						movieBean.setMovieName(movieWidgetShowtimeCursor.getString(columnIndex));

						columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_EN_NAME);
						movieBean.setEnglishMovieName(movieWidgetShowtimeCursor.getString(columnIndex));

						columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_LENGTH);
						movieBean.setMovieTime(movieWidgetShowtimeCursor.getLong(columnIndex));

					}
					showTimeList = showTimeMap.get(movieId);

					if (showTimeList == null) {
						showTimeList = new ArrayList<ProjectionBean>();

						showTimeMap.put(movieId, showTimeList);
					}

					projectionBean = new ProjectionBean();

					columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_SHOWTIME);
					projectionBean.setShowtime(movieWidgetShowtimeCursor.getLong(columnIndex));

					columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_SHOWTIME_LANG);
					projectionBean.setSubtitle(movieWidgetShowtimeCursor.getString(columnIndex));

					columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_SHOWTIME_RESERVATION);
					projectionBean.setReservationLink(movieWidgetShowtimeCursor.getString(columnIndex));

					showTimeList.add(projectionBean);

				} while (movieWidgetShowtimeCursor.moveToNext());

				for (MovieBean movieTmp : movieMap.values()) {
					movieShowTimeMap.put(movieTmp, showTimeMap.get(movieTmp.getId()));
				}
			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No theater where extract"); //$NON-NLS-1$
				}
			}
		} finally {
			if (movieWidgetShowtimeCursor != null) {
				movieWidgetShowtimeCursor.close();
			}
		}
		return movieShowTimeMap;
	}

	/**
	 * Extract theater of widget
	 * 
	 * @param mDbHelper
	 * @return
	 * @throws SQLException
	 */
	public static MovieBean extractWidgetMovie(CineShowtimeDbAdapter mDbHelper, String movieId, TheaterBean theaterBean, int widgetId) throws SQLException {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Extract widget movie showtimes"); //$NON-NLS-1$
		}
		List<ProjectionBean> showTimeList = null;
		MovieBean movieBean = null;
		ProjectionBean projectionBean = null;
		Cursor movieWidgetShowtimeCursor = mDbHelper.fetchWidgetMovie(movieId, widgetId);
		int columnIndex = 0;
		try {
			if (movieWidgetShowtimeCursor.moveToFirst()) {
				do {
					columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_MID);
					movieId = movieWidgetShowtimeCursor.getString(columnIndex);
					if (movieBean == null) {
						movieBean = new MovieBean();

						movieBean.setId(movieId);

						columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_NAME);
						movieBean.setMovieName(movieWidgetShowtimeCursor.getString(columnIndex));

						columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_EN_NAME);
						movieBean.setEnglishMovieName(movieWidgetShowtimeCursor.getString(columnIndex));

						columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_LENGTH);
						movieBean.setMovieTime(movieWidgetShowtimeCursor.getLong(columnIndex));

					}
					showTimeList = theaterBean.getMovieMap().get(movieId);

					if (showTimeList == null) {
						showTimeList = new ArrayList<ProjectionBean>();

						theaterBean.getMovieMap().put(movieId, showTimeList);
					}

					projectionBean = new ProjectionBean();
					columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_SHOWTIME);
					projectionBean.setShowtime(movieWidgetShowtimeCursor.getLong(columnIndex));

					columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_SHOWTIME_LANG);
					projectionBean.setSubtitle(movieWidgetShowtimeCursor.getString(columnIndex));

					columnIndex = movieWidgetShowtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_WIDGET_MOVIE_SHOWTIME_RESERVATION);
					projectionBean.setReservationLink(movieWidgetShowtimeCursor.getString(columnIndex));

					showTimeList.add(projectionBean);

				} while (movieWidgetShowtimeCursor.moveToNext());

			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No theater where extract"); //$NON-NLS-1$
				}
			}
		} finally {
			if (movieWidgetShowtimeCursor != null) {
				movieWidgetShowtimeCursor.close();
			}
		}
		return movieBean;
	}

	/**
	 * Extract a theater from cursor
	 * 
	 * @param theaterCursor
	 * @param mDbHelper
	 * @return
	 * @throws SQLException
	 */
	public static TheaterBean extractTheaterBean(Cursor theaterCursor, CineShowtimeDbAdapter mDbHelper) throws SQLException {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Extract a Theater"); //$NON-NLS-1$
		}
		TheaterBean theaterBean = new TheaterBean();
		theaterBean.setMovieMap(new HashMap<String, List<ProjectionBean>>());

		int columnIndex = theaterCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_THEATER_ID);
		theaterBean.setId(theaterCursor.getString(columnIndex));

		columnIndex = theaterCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_THEATER_NAME);
		theaterBean.setTheaterName(theaterCursor.getString(columnIndex));
		ProjectionBean projectionBean = null;

		// Fetch showtimes link to theater
		Cursor showtimeCursor = mDbHelper.fetchShowtime(theaterBean.getId());
		try {
			if (Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d(TAG, "Extract showtime from theater : " + theaterBean.getTheaterName()); //$NON-NLS-1$
			}
			if (showtimeCursor.moveToFirst()) {
				List<ProjectionBean> showtimeList = null;
				do {
					columnIndex = showtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_SHOWTIME_MOVIE_ID);
					String movieId = showtimeCursor.getString(columnIndex);

					showtimeList = theaterBean.getMovieMap().get(movieId);
					if (showtimeList == null) {
						showtimeList = new ArrayList<ProjectionBean>();
						theaterBean.getMovieMap().put(movieId, showtimeList);
					}

					projectionBean = new ProjectionBean();
					columnIndex = showtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_SHOWTIME_TIME);
					projectionBean.setShowtime(showtimeCursor.getLong(columnIndex));
					columnIndex = showtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_SHOWTIME_LANG);
					projectionBean.setSubtitle(showtimeCursor.getString(columnIndex));
					columnIndex = showtimeCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_SHOWTIME_RESERVATION_URL);
					projectionBean.setReservationLink(showtimeCursor.getString(columnIndex));

					showtimeList.add(projectionBean);
				} while (showtimeCursor.moveToNext());
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, showtimeList.size() + " Showtimes where extract for theater : " + theaterBean.getTheaterName()); //$NON-NLS-1$
				}
			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No showtime where extract for theater : " + theaterBean.getTheaterName()); //$NON-NLS-1$
				}
			}
		} finally {
			if (showtimeCursor != null) {
				showtimeCursor.close();
			}
		}

		// Fetch location link to theater
		Cursor locationCursor = mDbHelper.fetchLocation(theaterBean.getId());
		try {
			if (Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d(TAG, "Extract Localisation of theater : " + theaterBean.getTheaterName()); //$NON-NLS-1$
			}
			if (locationCursor.moveToFirst()) {
				theaterBean.setPlace(extractLocalisationBean(locationCursor));
			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No localisation where extract"); //$NON-NLS-1$
				}
			}
		} finally {
			if (locationCursor != null) {
				locationCursor.close();
			}
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
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Extract Localisation"); //$NON-NLS-1$
		}
		LocalisationBean localisationBean = new LocalisationBean();

		int columnIndex = locationCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_LOCALISATION_CITY_NAME);
		localisationBean.setCityName(locationCursor.getString(columnIndex));

		columnIndex = locationCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_LOCALISATION_COUNTRY_NAME);
		localisationBean.setCountryName(locationCursor.getString(columnIndex));

		columnIndex = locationCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_LOCALISATION_COUNTRY_CODE);
		localisationBean.setCountryNameCode(locationCursor.getString(columnIndex));

		columnIndex = locationCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_LOCALISATION_POSTAL_CODE);
		localisationBean.setPostalCityNumber(locationCursor.getString(columnIndex));

		columnIndex = locationCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_LOCALISATION_DISTANCE);
		localisationBean.setDistance(locationCursor.getFloat(columnIndex));

		columnIndex = locationCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_LOCALISATION_DISTANCE_TIME);
		localisationBean.setDistanceTime(locationCursor.getLong(columnIndex));

		columnIndex = locationCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_LOCALISATION_LATITUDE);
		localisationBean.setLatitude(locationCursor.getDouble(columnIndex));

		columnIndex = locationCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_LOCALISATION_LONGITUDE);
		localisationBean.setLongitude(locationCursor.getDouble(columnIndex));

		columnIndex = locationCursor.getColumnIndex(CineShowtimeDbAdapter.KEY_LOCALISATION_SEARCH_QUERY);
		localisationBean.setSearchQuery(locationCursor.getString(columnIndex));

		return localisationBean;
	}

	/**
	 * Extract curent movie information (showtime, desciption ...)
	 * 
	 * @param mDbHelper
	 * @return an Object[] with Object[0]= TheaterBean and Object[1] = MovieBean
	 * @throws SQLException
	 */
	public static Object[] extractCurrentMovie(CineShowtimeDbAdapter mDbHelper) throws SQLException {
		Object[] result = new Object[3];

		Cursor cursorCurerntMovie = mDbHelper.fetchCurentMovie();

		// Fetch location link to theater
		try {
			if (cursorCurerntMovie.moveToFirst()) {
				int columnIndex = cursorCurerntMovie.getColumnIndex(CineShowtimeDbAdapter.KEY_CURENT_MOVIE_THEATER_ID);
				String theaterId = cursorCurerntMovie.getString(columnIndex);

				columnIndex = cursorCurerntMovie.getColumnIndex(CineShowtimeDbAdapter.KEY_CURENT_MOVIE_MOVIE_ID);
				String movieId = cursorCurerntMovie.getString(columnIndex);

				columnIndex = cursorCurerntMovie.getColumnIndex(CineShowtimeDbAdapter.KEY_CURENT_MOVIE_WIDGET_ID);
				Integer widgetId = cursorCurerntMovie.getInt(columnIndex);

				result[2] = widgetId;

				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "Extract curent movie : movieId : " + movieId + ", theaterId : " + theaterId); //$NON-NLS-1$
				}
				Cursor theaterCursor = mDbHelper.fetchTheater(theaterId);
				try {
					if (theaterCursor.moveToFirst()) {
						result[0] = extractTheaterBean(theaterCursor, mDbHelper);

					} else {
						if (Log.isLoggable(TAG, Log.DEBUG)) {
							Log.d(TAG, "No theater"); //$NON-NLS-1$
						}

					}
				} finally {
					if (theaterCursor != null) {
						theaterCursor.close();
					}
				}
				Cursor movieCursor = mDbHelper.fetchMovie(movieId);
				try {
					if (movieCursor.moveToFirst()) {
						result[1] = extractMovie(movieCursor);
					}
				} finally {
					if (movieCursor != null) {
						movieCursor.close();
					} else {
						if (Log.isLoggable(TAG, Log.DEBUG)) {
							Log.d(TAG, "No movie"); //$NON-NLS-1$
						}
					}
				}
			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No curent movie"); //$NON-NLS-1$
				}
			}
		} finally {
			if (cursorCurerntMovie != null) {
				cursorCurerntMovie.close();
			}
		}

		return result;
	}

	/**
	 * Extract curent movie information (showtime, desciption ...)
	 * 
	 * @param mDbHelper
	 * @return an Object[] with Object[0]= TheaterBean and Object[1] = MovieBean
	 * @throws SQLException
	 */
	public static Object[] extractCurrentWidgetMovie(CineShowtimeDbAdapter mDbHelper) throws SQLException {
		Object[] result = new Object[2];

		Cursor cursorCurerntMovie = mDbHelper.fetchCurentMovie();

		// Fetch location link to theater
		try {
			if (cursorCurerntMovie.moveToFirst()) {
				int columnIndex = cursorCurerntMovie.getColumnIndex(CineShowtimeDbAdapter.KEY_CURENT_MOVIE_THEATER_ID);
				String theaterId = cursorCurerntMovie.getString(columnIndex);

				columnIndex = cursorCurerntMovie.getColumnIndex(CineShowtimeDbAdapter.KEY_CURENT_MOVIE_MOVIE_ID);
				String movieId = cursorCurerntMovie.getString(columnIndex);

				columnIndex = cursorCurerntMovie.getColumnIndex(CineShowtimeDbAdapter.KEY_CURENT_MOVIE_WIDGET_ID);
				int widgetId = cursorCurerntMovie.getInt(columnIndex);

				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "Extract curent movie : movieId : " + movieId + ", theaterId : " + theaterId); //$NON-NLS-1$
				}
				result[0] = extractWidgetTheater(mDbHelper, Calendar.getInstance(), widgetId);
				result[1] = extractWidgetMovie(mDbHelper, movieId, (TheaterBean) result[0], widgetId);

			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG, "No curent movie"); //$NON-NLS-1$
				}
			}
		} finally {
			if (cursorCurerntMovie != null) {
				cursorCurerntMovie.close();
			}
		}

		return result;
	}

}
