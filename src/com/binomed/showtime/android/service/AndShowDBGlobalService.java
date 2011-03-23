package com.binomed.showtime.android.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.IBinder;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.objects.DBActionObject;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.MovieResp;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.ProjectionBean;
import com.binomed.showtime.beans.TheaterBean;

public class AndShowDBGlobalService extends Service {

	private static final String TAG = "DBGlobalService"; //$NON-NLS-1$

	private boolean inThread;

	private AndShowtimeDbAdapter mDbHelper;

	private ArrayList<DBActionObject> taskList = new ArrayList<DBActionObject>();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public ComponentName startService(Intent service) {
		Log.i(TAG, "Start Service : " + System.identityHashCode(this));
		return super.startService(service);
	}

	@Override
	public boolean stopService(Intent name) {
		Log.i(TAG, "Stop Service : " + System.identityHashCode(this));
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "OnDestroy : " + System.identityHashCode(this));
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "OnStart : " + System.identityHashCode(this));
		super.onStart(intent, startId);

		try {

			int type = intent.getIntExtra(ParamIntent.SERVICE_DB_TYPE, AndShowtimeCst.DB_TYPE_MOVIE_RESP_WRITE);
			Object data = null;
			// We determine the type of object for write datas
			switch (type) {
			case AndShowtimeCst.DB_TYPE_NEAR_RESP_WRITE: {
				data = BeanManagerFactory.getNearResp();
				break;
			}
			case AndShowtimeCst.DB_TYPE_MOVIE_RESP_WRITE: {
				data = BeanManagerFactory.getMovieResp();
				break;
			}
			case AndShowtimeCst.DB_TYPE_MOVIE_WRITE: {
				data = BeanManagerFactory.getMovieDesc();
				break;
			}
			case AndShowtimeCst.DB_TYPE_FAV_WRITE:
			case AndShowtimeCst.DB_TYPE_FAV_DELETE:
			case AndShowtimeCst.DB_TYPE_WIDGET_WRITE: {
				data = BeanManagerFactory.getTheaterTemp();
				break;
			}
			case AndShowtimeCst.DB_TYPE_WIDGET_WRITE_LIST: {
				data = BeanManagerFactory.getNearRespFromWidget();
				break;
			}
			case AndShowtimeCst.DB_TYPE_CURENT_MOVIE_WRITE: {
				data = new String[] { //
				intent.getStringExtra(ParamIntent.THEATER_ID) //
						, intent.getStringExtra(ParamIntent.MOVIE_ID) //
				};
				break;
			}
			default:
				break;
			}
			// We add object into syncrhonised list
			synchronized (taskList) {
				taskList.add(new DBActionObject(type, data));
			}
			// We check if we create the Thread or just use list;
			if (!inThread) {
				try {
					mDbHelper = new AndShowtimeDbAdapter(this);
					mDbHelper.open();
				} catch (SQLException e) {
					Log.e(TAG, "error opening database", e);
				}

				Thread fillDBThread = new Thread(fillDBRunnable);
				fillDBThread.start();
				// We passed flag to true in order to wait for start of Thread;
				inThread = true;
			}

		} catch (Exception e) {
			Log.e(TAG, "Error while cleaning files", e);
			if (mDbHelper != null && mDbHelper.isOpen()) {
				mDbHelper.close();
			}
		}
	}

	private Runnable fillDBRunnable = new Runnable() {
		public void run() {
			try {
				inThread = true;

				ArrayList<DBActionObject> tempList = null;
				do {
					tempList = new ArrayList<DBActionObject>(taskList);
					synchronized (taskList) {
						taskList.clear();
					}
					for (DBActionObject action : tempList) {
						switch (action.getType()) {
						case AndShowtimeCst.DB_TYPE_NEAR_RESP_WRITE: {
							writeNearResp((NearResp) action.getData());
							break;
						}
						case AndShowtimeCst.DB_TYPE_MOVIE_RESP_WRITE: {
							writeMovieResp((MovieResp) action.getData());
							break;
						}
						case AndShowtimeCst.DB_TYPE_MOVIE_WRITE: {
							mDbHelper.createOrUpdateMovie((MovieBean) action.getData());
							break;
						}
						case AndShowtimeCst.DB_TYPE_FAV_WRITE: {
							mDbHelper.addTheaterToFavorites((TheaterBean) action.getData());
							break;
						}
						case AndShowtimeCst.DB_TYPE_FAV_DELETE: {
							mDbHelper.deleteFavorite(((TheaterBean) action.getData()).getId());
							break;
						}
						case AndShowtimeCst.DB_TYPE_WIDGET_WRITE: {
							mDbHelper.setWidgetTheater((TheaterBean) action.getData());
							break;
						}
						case AndShowtimeCst.DB_TYPE_WIDGET_WRITE_LIST: {
							writeWidgetResp((NearResp) action.getData());
							break;
						}
						case AndShowtimeCst.DB_TYPE_CURENT_MOVIE_WRITE: {
							mDbHelper.createCurentMovie(((String[]) action.getData())[0] //
									, ((String[]) action.getData())[1] //
									);
							break;
						}
						case AndShowtimeCst.DB_TYPE_SKYHOOK_REGISTRATION: {
							mDbHelper.createSkyHookRegistration();
							break;
						}
						default:
							break;
						}
					}
				} while (!taskList.isEmpty());

			} catch (Exception e) {
				Log.e(TAG, "error cleaning file", e);
			} finally {
				// We have to finally close the data base in order to release it
				if (mDbHelper != null && mDbHelper.isOpen()) {
					mDbHelper.close();
				}
				inThread = false;
				AndShowDBGlobalService.this.stopSelf();
			}

		}

		private void writeWidgetResp(NearResp nearResp) {
			mDbHelper.deleteWidgetShowtime();
			if (nearResp != null && nearResp.getTheaterList() != null) {
				Map<String, MovieBean> movieBeanList = nearResp.getMapMovies();
				MovieBean movieBean = null;
				ProjectionBean minTime = null;
				for (Entry<String, List<ProjectionBean>> showTime : nearResp.getTheaterList().get(0).getMovieMap().entrySet()) {
					movieBean = movieBeanList.get(showTime.getKey());
					minTime = AndShowtimeDateNumberUtil.getMinTime(showTime.getValue(), null);
					if (minTime != null) {
						for (ProjectionBean time : showTime.getValue()) {
							mDbHelper.createWidgetShowtime(movieBean, time);
						}
					}
				}
				mDbHelper.updateWidgetTheater();
			}

		}

		private void writeMovieResp(MovieResp movieResp) {
			if (movieResp != null) {
				ArrayList<TheaterBean> copyListTheater = new ArrayList<TheaterBean>(movieResp.getTheaterList());
				MovieBean copyMovie = movieResp.getMovie();

				mDbHelper.deleteTheatersShowtimeRequestAndLocation();
				for (TheaterBean theater : copyListTheater) {
					mDbHelper.createTheater(theater);
					if (theater.getPlace() != null) {
						mDbHelper.createLocation(theater.getPlace(), theater.getId());
					}
					for (String movieId : theater.getMovieMap().keySet()) {
						for (ProjectionBean showTime : theater.getMovieMap().get(movieId)) {
							mDbHelper.createShowtime(theater.getId(), movieId, showTime);
						}
					}
				}
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
							columnIndex = cursorFav.getColumnIndex(AndShowtimeDbAdapter.KEY_FAV_TH_THEATER_ID);
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
							columnIndex = cursorFav.getColumnIndex(AndShowtimeDbAdapter.KEY_FAV_SHOWTIME_MOVIE_ID);
							movieIdFavSet.add(cursorFav.getString(columnIndex));
						} while (cursorFav.moveToNext());
					}
				} finally {
					cursorFav.close();
				}
				for (TheaterBean theater : copyListTheater) {
					mDbHelper.createTheater(theater);
					if (theater.getPlace() != null) {
						mDbHelper.createLocation(theater.getPlace(), theater.getId());
					}
					for (String movieId : theater.getMovieMap().keySet()) {
						for (ProjectionBean showTime : theater.getMovieMap().get(movieId)) {
							try {
								mDbHelper.createShowtime(theater.getId(), movieId, showTime);
								if (theaterIdFavList.contains(theater.getId())) {
									mDbHelper.createFavShowtime(theater.getId(), movieId, showTime);
								}
							} catch (Exception e) {
								Log.e(TAG, "error inserting showtime : " + theater.getTheaterName() + " movie  : " + movieId);
							}
						}
					}
				}
				for (MovieBean movie : copyMovieMap.values()) {
					mDbHelper.createOrUpdateMovie(movie);
				}
				movieIdFavSet.addAll(copyMovieMap.keySet());
				mDbHelper.deleteMovies(movieIdFavSet);
			}

		}
	};

}
