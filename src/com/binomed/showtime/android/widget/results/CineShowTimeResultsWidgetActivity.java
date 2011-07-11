package com.binomed.showtime.android.widget.results;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.adapter.view.CineShowTimeNonExpandableListAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.handler.ServiceCallBackSearch;
import com.binomed.showtime.android.layout.view.ObjectMasterView;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsService;
import com.binomed.showtime.android.screen.results.ICallbackSearch;
import com.binomed.showtime.android.screen.results.IServiceSearch;
import com.binomed.showtime.android.service.CineShowDBGlobalService;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.android.util.CineShowTimeLayoutUtils;
import com.binomed.showtime.android.util.CineShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.android.util.localisation.LocationUtils;
import com.binomed.showtime.android.widget.CineShowTimeWidgetHelper;
import com.binomed.showtime.cst.HttpParamsCst;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class CineShowTimeResultsWidgetActivity extends Activity implements OnItemClickListener //
		, OnClickListener//
{

	public static final Integer ACTIVITY_OPEN_MOVIE = 0;

	private static final String TAG = "ResultsWidgetActivity"; //$NON-NLS-1$

	protected ListView resultList;
	protected ProgressDialog progressDialog;
	protected CineShowTimeNonExpandableListAdapter adapter = null;

	private ModelResultsWidgetActivity model;
	private SharedPreferences prefs;
	protected GoogleAnalyticsTracker tracker;
	private CineShowtimeDbAdapter mDbHelper;

	private IServiceSearch serviceResult;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start(CineShowtimeCst.GOOGLE_ANALYTICS_ID, this);
		tracker.trackPageView("/ResultWidgetActivity");
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		CineShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
		Log.i(TAG, "onCreate"); //$NON-NLS-1$
		setContentView(R.layout.activity_widget_one_results);

		model = new ModelResultsWidgetActivity();

		// We init the theater id if set

		getIntent().putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, false);
		model.setFavTheaterId(getIntent().getStringExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID));
		model.setLocalisation(null);
		model.setCityName(getIntent().getStringExtra(ParamIntent.ACTIVITY_SEARCH_CITY));
		Double latitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, 0);
		Double longitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, 0);
		if ((latitude != 0) && (longitude != 0)) {
			Location locationTheater = new Location("GPS");
			locationTheater.setLatitude(latitude);
			locationTheater.setLongitude(longitude);
			model.setLocalisation(locationTheater);
		}
		getIntent().putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, "");

		initViews();

		bindService();
		initDB();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy"); //$NON-NLS-1$
		unbindService();
		closeDB();
		tracker.dispatch();
		tracker.stop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause"); //$NON-NLS-1$
		if ((progressDialog != null) && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume"); //$NON-NLS-1$
		initListeners();

		display();

	}

	/**
	 * init the view of activity
	 */
	private void initViews() {

		resultList = (ListView) findViewById(R.id.resultWidgetListResult);

		// Manage Adapter
		adapter = new CineShowTimeNonExpandableListAdapter(this, this);
	}

	private void initListeners() {
		resultList.setOnItemClickListener(this);
	}

	protected void display() {
		if (isServiceRunning()) {
			openDialog();
		} else {
			NearResp nearResp = model.getNearResp();
			if (nearResp != null) {
				boolean error = false;
				List<TheaterBean> theaterList = nearResp.getTheaterList();
				if ((theaterList != null) && (theaterList.size() == 1)) {
					TheaterBean errorTheater = theaterList.get(0);
					if (errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_WRONG_DATE))//
							|| errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_WRONG_PLACE)) //
							|| errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_NO_DATA)) //
					) {
						error = true;
						switch (Integer.valueOf(errorTheater.getId())) {
						case HttpParamsCst.ERROR_WRONG_DATE:
							errorTheater.setTheaterName(getResources().getString(R.string.msgNoDateMatch));
							break;
						case HttpParamsCst.ERROR_WRONG_PLACE:
							errorTheater.setTheaterName(getResources().getString(R.string.msgNoPlaceMatch));
							break;
						case HttpParamsCst.ERROR_NO_DATA:
							errorTheater.setTheaterName(getResources().getString(R.string.msgNoResultRetryLater));
							break;

						default:
							break;
						}
					}
				} else if ((theaterList == null) || (theaterList.size() == 0)) {
					error = true;
					TheaterBean theaterZeroResp = new TheaterBean();
					theaterZeroResp.setId(String.valueOf(HttpParamsCst.ERROR_NO_DATA));
					theaterZeroResp.setTheaterName(getResources().getString(R.string.msgNoResultRetryLater));
					if (theaterList == null) {
						theaterList = new ArrayList<TheaterBean>();
						nearResp.setTheaterList(theaterList);
					}
					theaterList.add(theaterZeroResp);
				}
				adapter.setTheaterList(nearResp, model.getTheaterFavList(), CineShowtimeFactory.getTheaterDistanceComparator());
				resultList.setAdapter(adapter);
				if ((nearResp != null) && (nearResp.getCityName() != null) && (nearResp.getCityName().length() > 0)) {
					model.setCityName(nearResp.getCityName());
				}
			}
		}

	}

	protected void launchNearService() throws UnsupportedEncodingException {
		openDialog();

		launchSearchService();
	}

	/**
	 * 
	 */
	protected void openDialog() {
		progressDialog = ProgressDialog.show(CineShowTimeResultsWidgetActivity.this, //
				CineShowTimeResultsWidgetActivity.this.getResources().getString(R.string.searchNearProgressTitle)//
				, CineShowTimeResultsWidgetActivity.this.getResources().getString(R.string.searchNearProgressMsg) //
				, true, false);
	}

	/**
	 * The call back message handler
	 */
	public ServiceCallBackSearch m_callbackHandler = new ServiceCallBackSearch() {

		@Override
		public void handleInputRecived() {

			try {
				display();
			} catch (Exception e) {
				Log.e(TAG, "Error during display", e);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

		}

	};

	/*
	 * 
	 * Event
	 */

	@Override
	public void onClick(View v) {
		ImageView imageViewFav = (ImageView) v;

		ObjectMasterView objectMasterView = (ObjectMasterView) imageViewFav.getParent().getParent();

		boolean isFav = objectMasterView.isFav();
		TheaterBean theaterBean = objectMasterView.getTheaterBean();
		if (isFav) {
			removeFavorite(theaterBean);
		} else {
			addFavorite(theaterBean);
		}
		objectMasterView.toggleFav();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int groupPosition, long arg3) {
		switch (parent.getId()) {
		case R.id.resultWidgetListResult: {
			int theaterListSize = model.getNearResp().getTheaterList().size();
			if (theaterListSize == groupPosition) {
				model.setStart(model.getStart() + 10);
				try {
					launchNearService();
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "error launching service", e);
				}
			} else {
				CineShowTimeWidgetHelper.finalizeWidget(this, model.getNearResp().getTheaterList().get(groupPosition) //
						, model.getNearResp().getCityName());
			}
			break;
		}

		default:
			break;
		}

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

		CineShowtimeFactory.initGeocoder(this);
		Intent intentResultService = new Intent(this, CineShowTimeResultsService.class);

		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, CineShowTimeEncodingUtil.getEncoding()) : cityName));
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_THEATER_ID, theaterId);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_DAY, 0);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_ORIGIN, CineShowTimeResultsWidgetActivity.class.getName());
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_START, start);

		startService(intentResultService);
	}

	/*
	 * 
	 * DB
	 */

	public void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new CineShowtimeDbAdapter(this);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			openDB();

			try {
				launchNearService();
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
			Intent service = new Intent(this, CineShowDBGlobalService.class);
			service.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_FAV_WRITE);
			service.putExtra(ParamIntent.SERVICE_DB_DATA, theaterBean);
			startService(service);
		} catch (Exception e) {
			Log.e(TAG, "error putting data into data base", e);
		}

	}

	public void removeFavorite(TheaterBean theaterBean) {
		try {
			model.getTheaterFavList().remove(theaterBean);
			Intent service = new Intent(this, CineShowDBGlobalService.class);
			service.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_FAV_DELETE);
			service.putExtra(ParamIntent.SERVICE_DB_DATA, theaterBean);
			startService(service);
		} catch (Exception e) {
			Log.e(TAG, "error removing theater from fav", e);
		}

	}

	public List<TheaterBean> getFavTheater() {
		List<TheaterBean> theaterList = CineShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);

		return theaterList;
	}

	/*
	 * 
	 * CALL BACK SERVICE
	 */

	public void bindService() {
		bindService(new Intent(this, CineShowTimeResultsService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() {
		try {
			serviceResult.unregisterCallback(m_callback);
			unbindService(mConnection);
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
			NearResp nearResp = serviceResult.getNearResp();
			Intent intentNearFillDBService = new Intent(CineShowTimeResultsWidgetActivity.this, CineShowDBGlobalService.class);
			intentNearFillDBService.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_NEAR_RESP_WRITE);
			intentNearFillDBService.putExtra(ParamIntent.SERVICE_DB_DATA, nearResp);
			startService(intentNearFillDBService);

			model.setNearResp(nearResp);

			m_callbackHandler.sendInputRecieved();

		}

		@Override
		public void finishLocation(String theaterId) throws RemoteException {
			// TODO Auto-generated method stub

		}

	};

}