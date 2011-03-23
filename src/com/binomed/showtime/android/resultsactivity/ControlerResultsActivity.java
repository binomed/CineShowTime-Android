package com.binomed.showtime.android.resultsactivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.SQLException;
import android.location.Location;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.binomed.showtime.android.adapter.db.AndShowtimeDbAdapter;
import com.binomed.showtime.android.aidl.ICallbackSearch;
import com.binomed.showtime.android.aidl.IServiceSearch;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.movieactivity.AndShowTimeMovieActivity;
import com.binomed.showtime.android.service.AndShowDBGlobalService;
import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.AndShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.localisation.LocationUtils;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;

public class ControlerResultsActivity {

	private static final String TAG = "ControlerResultActivity"; //$NON-NLS-1$

	private AndShowTimeResultsActivity resultActivity;
	private ModelResultsActivity model;
	private AndShowtimeDbAdapter mDbHelper;

	private IServiceSearch serviceResult;

	private static ControlerResultsActivity instance;

	public static ControlerResultsActivity getInstance() {
		if (instance == null) {
			instance = new ControlerResultsActivity();
		}
		return instance;
	}

	private ControlerResultsActivity() {
		super();
	}

	public void registerView(AndShowTimeResultsActivity resultActivity) {
		this.resultActivity = resultActivity;
		bindService();
		initDB();
	}

	public ModelResultsActivity getModelResultsActivity() {
		if (model == null) {
			model = new ModelResultsActivity();
		}
		return model;
	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public void openMovieActivity(MovieBean movie, TheaterBean theater) {
		Intent intentStartMovieActivity = new Intent(resultActivity, AndShowTimeMovieActivity.class);

		intentStartMovieActivity.putExtra(ParamIntent.MOVIE_ID, movie.getId());
		intentStartMovieActivity.putExtra(ParamIntent.THEATER_ID, theater.getId());
		intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LATITUDE, (model.getLocalisation() != null) ? model.getLocalisation().getLatitude() : null);
		intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, (model.getLocalisation() != null) ? model.getLocalisation().getLongitude() : null);
		StringBuilder place = new StringBuilder();
		if (theater != null) {
			if (theater.getPlace() != null) {
				if (theater.getPlace().getCityName() != null //
						&& theater.getPlace().getCityName().length() > 0) {
					place.append(theater.getPlace().getCityName());
				}
				if (theater.getPlace().getPostalCityNumber() != null //
						&& theater.getPlace().getPostalCityNumber().length() > 0) {
					place.append(" ").append(theater.getPlace().getPostalCityNumber());
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
		intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_NEAR, place.toString());
		resultActivity.startActivityForResult(intentStartMovieActivity, AndShowtimeCst.ACTIVITY_RESULT_MOVIE_ACTIVITY);
	}

	public void launchSearchService() throws UnsupportedEncodingException {

		Location gpsLocation = model.getLocalisation();
		String cityName = model.getCityName();
		String movieName = model.getMovieName();
		String theaterId = model.getFavTheaterId();
		int day = model.getDay();
		int start = model.getStart();

		if (mDbHelper.isOpen()) {
			mDbHelper.createNearRequest(cityName //
					, (gpsLocation != null) ? gpsLocation.getLatitude() : null //
					, (gpsLocation != null) ? gpsLocation.getLongitude() : null //
					, theaterId//
					);
		}
		if ((cityName != null) && (cityName.length() > 0)) {
			model.getRequestList().add(cityName);
		}

		AndShowtimeFactory.initGeocoder(resultActivity);
		Intent intentResultService = new Intent(resultActivity, AndShowTimeResultsService.class);

		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, AndShowTimeEncodingUtil.getEncoding()) : cityName));
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_MOVIE_NAME, ((movieName != null) ? URLEncoder.encode(movieName, AndShowTimeEncodingUtil.getEncoding()) : movieName));
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_THEATER_ID, theaterId);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_DAY, day);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_START, start);

		resultActivity.startService(intentResultService);
	}

	/*
	 * 
	 * DB
	 */

	public void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new AndShowtimeDbAdapter(resultActivity);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			openDB();

			getModelResultsActivity();

			boolean rerunService = model.isForceResearch();
			if (mDbHelper.isOpen()) {

				List<TheaterBean> theaterFav = AndShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);
				if (theaterFav != null) {
					Map<String, TheaterBean> theaterFavList = new HashMap<String, TheaterBean>();
					for (TheaterBean theater : theaterFav) {
						theaterFavList.put(theater.getId(), theater);
					}
					model.setTheaterFavList(theaterFavList);
				}
			}
			if (rerunService) {
				try {
					resultActivity.launchNearService();
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "error while rerun service", e); //$NON-NLS-1$
				}
			} else {

				NearResp nearResp = BeanManagerFactory.getNearResp();
				if (nearResp == null) {
					nearResp = new NearResp();
					if (mDbHelper.isOpen()) {
						nearResp.setTheaterList(AndShowtimeDB2AndShowtimeBeans.extractTheaterList(mDbHelper));
						if ((nearResp.getTheaterList() != null) && !nearResp.getTheaterList().isEmpty()) {
							nearResp.setMapMovies(AndShowtimeDB2AndShowtimeBeans.extractMovies(mDbHelper, nearResp.getTheaterList()));
							Log.i(TAG, "Datas found"); //$NON-NLS-1$
							BeanManagerFactory.setNearResp(nearResp);

						} else {
							Log.i(TAG, "No datas founds"); //$NON-NLS-1$
						}
					}
				}

				if (nearResp != null) {
					// We manage particular case of fav theaterResults :
					// if previous result was just favorite => we have to relaunch the request
					// if previous result was a full results => we have to filtered the results
					if (model.getTheaterFavList() != null && !model.getTheaterFavList().isEmpty()) {
						if (nearResp.getTheaterList().size() == 1 && model.getFavTheaterId() == null) {
							thLabel: for (String thFavId : model.getTheaterFavList().keySet()) {
								for (TheaterBean thTmp : nearResp.getTheaterList()) {
									if (thTmp.getId().equals(thFavId)) {
										try {
											resultActivity.launchNearService();
											break thLabel;
										} catch (UnsupportedEncodingException e) {
											Log.e(TAG, "error while rerun service", e); //$NON-NLS-1$
										}
									}
								}
							}
						} else if (model.getFavTheaterId() != null && nearResp.getTheaterList().size() > 0) {
							List<TheaterBean> filteredTheaterBean = new ArrayList<TheaterBean>();
							for (TheaterBean thTmp : nearResp.getTheaterList()) {
								if (thTmp.getId().equals(model.getFavTheaterId())) {
									filteredTheaterBean.add(thTmp);
									break;
								}
							}
							Map<String, MovieBean> mapMovieFiltered = new HashMap<String, MovieBean>();
							String theaterFavId = model.getFavTheaterId();
							List<String> theaterIdListTmp = new ArrayList<String>();
							theaterIdListTmp.add(theaterFavId);
							for (Entry<String, MovieBean> entryMovieTmp : nearResp.getMapMovies().entrySet()) {
								if (entryMovieTmp.getValue().getTheaterList().contains(theaterFavId)) {
									entryMovieTmp.getValue().setTheaterList(theaterIdListTmp);
									mapMovieFiltered.put(entryMovieTmp.getKey(), entryMovieTmp.getValue());
								}
							}

							nearResp.setTheaterList(filteredTheaterBean);
							nearResp.setMapMovies(mapMovieFiltered);
						}
					}

					BeanManagerFactory.setNearResp(nearResp);
				}
			}
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void closeDB() {
		try {
			if (mDbHelper.isOpen()) {
				Log.i(TAG, "Close DB"); //$NON-NLS-1$
				mDbHelper.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "error onDestroy of movie Activity", e); //$NON-NLS-1$
		}
	}

	public void addFavorite(TheaterBean theaterBean) {
		try {
			if (LocationUtils.isEmptyLocation(theaterBean.getPlace())) {
				LocalisationBean place = theaterBean.getPlace();
				if (place == null) {
					place = new LocalisationBean();
					theaterBean.setPlace(place);
				}
				place.setCityName(model.getCityName());
			}
			model.getTheaterFavList().put(theaterBean.getId(), theaterBean);
			BeanManagerFactory.setTheaterTemp(theaterBean);
			Intent service = new Intent(resultActivity, AndShowDBGlobalService.class);
			service.putExtra(ParamIntent.SERVICE_DB_TYPE, AndShowtimeCst.DB_TYPE_FAV_WRITE);
			resultActivity.startService(service);
		} catch (Exception e) {
			Log.e(TAG, "error putting data into data base", e);
		}

	}

	public void removeFavorite(TheaterBean theaterBean) {
		try {
			model.getTheaterFavList().remove(theaterBean);
			BeanManagerFactory.setTheaterTemp(theaterBean);
			Intent service = new Intent(resultActivity, AndShowDBGlobalService.class);
			service.putExtra(ParamIntent.SERVICE_DB_TYPE, AndShowtimeCst.DB_TYPE_FAV_DELETE);
			resultActivity.startService(service);
		} catch (Exception e) {
			Log.e(TAG, "error removing theater from fav", e);
		}

	}

	public List<TheaterBean> getFavTheater() {
		List<TheaterBean> theaterList = AndShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);

		return theaterList;
	}

	/*
	 * 
	 * CALL BACK SERVICE
	 */

	public void bindService() {
		resultActivity.bindService(new Intent(resultActivity, AndShowTimeResultsService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() {
		try {
			serviceResult.unregisterCallback(m_callback);
			resultActivity.unbindService(mConnection);
		} catch (Exception e) {
			Log.e(TAG, "error while unbinding service", e);
		}
	}

	/**
	 * The service connection inteface with our binded service {@link http ://code .google.com/android/reference/android/content/ServiceConnection.html}
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceResult = IServiceSearch.Stub.asInterface(service);

			try {
				serviceResult.registerCallback(m_callback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	protected boolean isServiceRunning() {
		if (serviceResult != null) {
			try {
				return serviceResult.isServiceRunning();
			} catch (RemoteException e) {
				Log.e(TAG, "Eror during checking service", e); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * The callback object that will return from the service
	 */
	private ICallbackSearch m_callback = new ICallbackSearch.Stub() {

		@Override
		public void finish() throws RemoteException {

			Location gpsLocation = model.getLocalisation();
			String cityName = model.getCityName();
			String movieName = model.getMovieName();
			String theaterId = model.getFavTheaterId();

			if (mDbHelper.isOpen()) {
				mDbHelper.createMovieRequest(cityName //
						, movieName //
						, (gpsLocation != null) ? gpsLocation.getLatitude() : null //
						, (gpsLocation != null) ? gpsLocation.getLongitude() : null //
						, theaterId//
						, BeanManagerFactory.getNearResp() == null //
						);
			}

			model.setNullResult(BeanManagerFactory.getNearResp() == null);

			Intent intentNearFillDBService = new Intent(resultActivity, AndShowDBGlobalService.class);
			intentNearFillDBService.putExtra(ParamIntent.SERVICE_DB_TYPE, AndShowtimeCst.DB_TYPE_NEAR_RESP_WRITE);
			resultActivity.startService(intentNearFillDBService);

			resultActivity.m_callbackHandler.sendInputRecieved();

		}

	};

}
