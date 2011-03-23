package com.binomed.showtime.android.widget.one;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

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
import com.binomed.showtime.android.resultsactivity.AndShowTimeResultsService;
import com.binomed.showtime.android.service.AndShowDBGlobalService;
import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.AndShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.localisation.LocationUtils;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.TheaterBean;

public class ControlerResultsWidgetActivity {

	private static final String TAG = "ControlerResultActivity"; //$NON-NLS-1$

	private AndShowTimeResultsWidgetActivity resultActivity;
	private ModelResultsWidgetActivity model;
	private AndShowtimeDbAdapter mDbHelper;

	private IServiceSearch serviceResult;

	private static ControlerResultsWidgetActivity instance;

	public static ControlerResultsWidgetActivity getInstance() {
		if (instance == null) {
			instance = new ControlerResultsWidgetActivity();
		}
		return instance;
	}

	private ControlerResultsWidgetActivity() {
		super();
	}

	public void registerView(AndShowTimeResultsWidgetActivity resultActivity) {
		this.resultActivity = resultActivity;
		bindService();
		initDB();
	}

	public ModelResultsWidgetActivity getModelResultsActivity() {
		if (model == null) {
			model = new ModelResultsWidgetActivity();
		}
		return model;
	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public void launchSearchService() throws UnsupportedEncodingException {

		Location gpsLocation = model.getLocalisation();
		String cityName = model.getCityName();
		String theaterId = model.getFavTheaterId();
		int start = model.getStart();

		AndShowtimeFactory.initGeocoder(resultActivity);
		Intent intentResultService = new Intent(resultActivity, AndShowTimeResultsService.class);

		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, AndShowTimeEncodingUtil.getEncoding()) : cityName));
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_THEATER_ID, theaterId);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_DAY, 0);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_ORIGIN, AndShowTimeResultsWidgetActivity.class.getName());
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

			try {
				resultActivity.launchNearService();
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "error while rerun service", e); //$NON-NLS-1$
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
			Log.i(TAG, "Finish Service Search");
			Intent intentNearFillDBService = new Intent(resultActivity, AndShowDBGlobalService.class);
			intentNearFillDBService.putExtra(ParamIntent.SERVICE_DB_TYPE, AndShowtimeCst.DB_TYPE_NEAR_RESP_WRITE);
			resultActivity.startService(intentNearFillDBService);

			resultActivity.m_callbackHandler.sendInputRecieved();

		}

	};

}
