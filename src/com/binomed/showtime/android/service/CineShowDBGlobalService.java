package com.binomed.showtime.android.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.DBActionObject;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.MovieResp;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.ReviewBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.model.YoutubeBean;
import com.binomed.showtime.android.widget.CineShowTimeWidgetHelper;

public class CineShowDBGlobalService extends IntentService {

	private static final String TAG = "DBGlobalService"; //$NON-NLS-1$

	private CineShowtimeDbAdapter mDbHelper;

	public CineShowDBGlobalService() {
		super(TAG);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "OnStart : " + System.identityHashCode(this));
		super.onStart(intent, startId);

	}

	private void writeWidgetResp(NearResp nearResp) {
		if ((nearResp != null) && (nearResp.getTheaterList() != null)) {
			TheaterBean theater = nearResp.getTheaterList().get(0);
			mDbHelper.deleteWidgetShowtime(theater.getWidgetId());
			Map<String, MovieBean> movieBeanList = nearResp.getMapMovies();
			MovieBean movieBean = null;
			// ProjectionBean minTime = null;
			for (Entry<String, List<ProjectionBean>> showTime : nearResp.getTheaterList().get(0).getMovieMap().entrySet()) {
				movieBean = movieBeanList.get(showTime.getKey());
				// minTime = CineShowtimeDateNumberUtil.getMinTime(showTime.getValue(), null);
				// if (minTime != null) {
				for (ProjectionBean time : showTime.getValue()) {
					mDbHelper.createWidgetShowtime(movieBean, time, theater.getWidgetId());
				}
				// }
			}
			mDbHelper.updateWidgetTheater(theater.getWidgetId());
		}

	}

	private void writeMovieResp(MovieResp movieResp) {
		if (movieResp != null) {
			ArrayList<TheaterBean> copyListTheater = new ArrayList<TheaterBean>(movieResp.getTheaterList());
			MovieBean copyMovie = movieResp.getMovie();

			mDbHelper.deleteTheatersShowtimeRequestAndLocation();
			mDbHelper.createTheaterList(copyListTheater);
			Map<String, Map<String, List<ProjectionBean>>> mapThMap = new HashMap<String, Map<String, List<ProjectionBean>>>();
			for (TheaterBean theater : copyListTheater) {
				// mDbHelper.createTheater(theater);
				if (theater.getPlace() != null) {
					mDbHelper.createLocation(theater.getPlace(), theater.getId());
				}
				mapThMap.put(theater.getId(), theater.getMovieMap());
				// for (String movieId : theater.getMovieMap().keySet()) {
				// for (ProjectionBean showTime : theater.getMovieMap().get(movieId)) {
				// // mDbHelper.createShowtime(theater.getId(), movieId, showTime);
				// }
				// }
			}
			mDbHelper.createShowtimeList(mapThMap);
			if (copyMovie != null) {
				mDbHelper.createOrUpdateMovie(copyMovie);
				Set<String> ids = new HashSet<String>();
				ids.add(copyMovie.getId());
				mDbHelper.deleteMovies(ids);
			}
		}

	}

	private void writeNearResp(NearResp nearResp) {
		if (nearResp != null) {
			ArrayList<TheaterBean> copyListTheater = new ArrayList<TheaterBean>(nearResp.getTheaterList());
			HashMap<String, MovieBean> copyMovieMap = new HashMap<String, MovieBean>(nearResp.getMapMovies());
			List<String> theaterIdFavList = new ArrayList<String>();
			Set<String> movieIdFavSet = new HashSet<String>();
			int columnIndex = 0;
			mDbHelper.deleteTheatersShowtimeRequestAndLocation();
			Cursor cursorFav = mDbHelper.fetchAllFavTheaters();
			try {
				if (cursorFav.moveToFirst()) {
					do {
						columnIndex = cursorFav.getColumnIndex(CineShowtimeDbAdapter.KEY_FAV_TH_THEATER_ID);
						theaterIdFavList.add(cursorFav.getString(columnIndex));
					} while (cursorFav.moveToNext());
				}
			} finally {
				cursorFav.close();
			}
			cursorFav = mDbHelper.fetchAllMovieFavShowtime();
			try {
				if (cursorFav.moveToFirst()) {
					do {
						columnIndex = cursorFav.getColumnIndex(CineShowtimeDbAdapter.KEY_FAV_SHOWTIME_MOVIE_ID);
						movieIdFavSet.add(cursorFav.getString(columnIndex));
					} while (cursorFav.moveToNext());
				}
			} finally {
				cursorFav.close();
			}
			mDbHelper.createTheaterList(copyListTheater);
			Map<String, Map<String, List<ProjectionBean>>> mapThMap = new HashMap<String, Map<String, List<ProjectionBean>>>();
			for (TheaterBean theater : copyListTheater) {
				// mDbHelper.createTheater(theater);
				if (theater.getPlace() != null) {
					mDbHelper.createLocation(theater.getPlace(), theater.getId());
				}
				mapThMap.put(theater.getId(), theater.getMovieMap());
				for (String movieId : theater.getMovieMap().keySet()) {
					for (ProjectionBean showTime : theater.getMovieMap().get(movieId)) {
						try {
							// mDbHelper.createShowtime(theater.getId(), movieId, showTime);
							if (theaterIdFavList.contains(theater.getId())) {
								mDbHelper.createFavShowtime(theater.getId(), movieId, showTime);
							}
						} catch (Exception e) {
							Log.e(TAG, "error inserting showtime : " + theater.getTheaterName() + " movie  : " + movieId);
						}
					}
				}
			}
			mDbHelper.createShowtimeList(mapThMap);
			for (MovieBean movie : copyMovieMap.values()) {
				mDbHelper.createOrUpdateMovie(movie);
			}
			movieIdFavSet.addAll(copyMovieMap.keySet());
			mDbHelper.deleteMovies(movieIdFavSet);
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		int type = intent.getIntExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_MOVIE_RESP_WRITE);
		Object data = null;
		// We determine the type of object for write datas
		switch (type) {
		case CineShowtimeCst.DB_TYPE_NEAR_RESP_WRITE: {
			data = intent.getParcelableExtra(ParamIntent.SERVICE_DB_DATA);
			break;
		}
		case CineShowtimeCst.DB_TYPE_MOVIE_RESP_WRITE: {
			data = intent.getParcelableExtra(ParamIntent.SERVICE_DB_DATA);
			break;
		}
		case CineShowtimeCst.DB_TYPE_MOVIE_WRITE: {
			data = intent.getParcelableExtra(ParamIntent.SERVICE_DB_DATA);
			break;
		}
		case CineShowtimeCst.DB_TYPE_FAV_WRITE:
		case CineShowtimeCst.DB_TYPE_FAV_DELETE:
		case CineShowtimeCst.DB_TYPE_WIDGET_WRITE: {
			data = intent.getParcelableExtra(ParamIntent.SERVICE_DB_DATA);
			break;
		}
		case CineShowtimeCst.DB_TYPE_WIDGET_WRITE_LIST: {
			data = intent.getParcelableExtra(ParamIntent.SERVICE_DB_DATA);
			break;
		}
		case CineShowtimeCst.DB_TYPE_CURENT_MOVIE_WRITE: {
			data = new String[] { //
			intent.getStringExtra(ParamIntent.THEATER_ID) //
					, intent.getStringExtra(ParamIntent.MOVIE_ID) //
					, intent.getStringExtra(ParamIntent.WIDGET_ID) //
			};
			break;
		}
		case CineShowtimeCst.DB_TYPE_LAST_CHANGE_WRITE: {
			data = new int[] { //
			intent.getIntExtra(ParamIntent.SERVICE_DB_VAL_VERSION_CODE, -1) //
			};
			break;
		}
		case CineShowtimeCst.DB_TYPE_WIDGET_DELETE: {
			data = new int[] { //
			intent.getIntExtra(ParamIntent.SERVICE_DB_DATA, -1) //
			};
			break;
		}
		default:
			break;
		}
		try {
			if ((mDbHelper == null) || !mDbHelper.isOpen()) {
				mDbHelper = new CineShowtimeDbAdapter(this);
				mDbHelper.open();
			}

			DBActionObject action = new DBActionObject(type, data);
			switch (action.getType()) {
			case CineShowtimeCst.DB_TYPE_NEAR_RESP_WRITE: {
				writeNearResp((NearResp) action.getData());
				break;
			}
			case CineShowtimeCst.DB_TYPE_MOVIE_RESP_WRITE: {
				writeMovieResp((MovieResp) action.getData());
				break;
			}
			case CineShowtimeCst.DB_TYPE_MOVIE_WRITE: {
				MovieBean movie = (MovieBean) action.getData();
				mDbHelper.createOrUpdateMovie(movie);
				if (movie.getReviews() != null) {
					for (ReviewBean review : movie.getReviews()) {
						mDbHelper.createReview(review, movie.getId());
					}
				}
				if (movie.getYoutubeVideos() != null) {
					for (YoutubeBean video : movie.getYoutubeVideos()) {
						mDbHelper.createVideo(video, movie.getId());
					}
				}
				break;
			}
			case CineShowtimeCst.DB_TYPE_FAV_WRITE: {
				mDbHelper.addTheaterToFavorites((TheaterBean) action.getData());
				break;
			}
			case CineShowtimeCst.DB_TYPE_FAV_DELETE: {
				mDbHelper.deleteFavorite(((TheaterBean) action.getData()).getId());
				break;
			}
			case CineShowtimeCst.DB_TYPE_WIDGET_WRITE: {
				mDbHelper.setWidgetTheater((TheaterBean) action.getData());
				break;
			}
			case CineShowtimeCst.DB_TYPE_WIDGET_DELETE: {
				mDbHelper.deleteWidget((Integer) action.getData());
				break;
			}
			case CineShowtimeCst.DB_TYPE_WIDGET_WRITE_LIST: {
				writeWidgetResp((NearResp) action.getData());
				// if request comes from widget, we have to refresh it
				Integer widgetId = -1;
				NearResp nearResp = (NearResp) action.getData();
				if (nearResp != null && nearResp.getTheaterList() != null && nearResp.getTheaterList().size() > 0) {
					widgetId = nearResp.getTheaterList().get(0).getWidgetId();
				}
				if (widgetId != null && widgetId != -1) {
					CineShowTimeWidgetHelper.updateWidget(getApplicationContext(), null, null, widgetId);
				}
				break;
			}
			case CineShowtimeCst.DB_TYPE_CURENT_MOVIE_WRITE: {
				mDbHelper.createCurentMovie(((String[]) action.getData())[0] //
						, ((String[]) action.getData())[1] //
						, Integer.valueOf(((String[]) action.getData())[2]) //
						);
				break;
			}
			case CineShowtimeCst.DB_TYPE_SKYHOOK_REGISTRATION: {
				mDbHelper.createSkyHookRegistration();
				break;
			}
			case CineShowtimeCst.DB_TYPE_LAST_CHANGE_WRITE: {
				mDbHelper.createLastChange(((int[]) action.getData())[0]);
				break;
			}
			default:
				break;
			}

		} catch (Exception e) {
			Log.e(TAG, "error cleaning file", e);
		}

	}

	@Override
	public void onCreate() {
		try {
			mDbHelper = new CineShowtimeDbAdapter(this);
			mDbHelper.open();
		} catch (Exception e) {
		}
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		try {
			if ((mDbHelper != null) && mDbHelper.isOpen()) {
				mDbHelper.close();
			}
		} catch (Exception e) {
		}
		super.onDestroy();
	}

}
