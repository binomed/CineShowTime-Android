package com.binomed.showtime.android.widget.one;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.CineShowTimeNonExpandableListAdapter;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.handler.ServiceCallBackSearch;
import com.binomed.showtime.android.util.AndShowTimeLayoutUtils;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;
import com.binomed.showtime.cst.HttpParamsCst;

public class AndShowTimeResultsWidgetActivity extends Activity {

	public static final Integer ACTIVITY_OPEN_MOVIE = 0;

	private static final String TAG = "ResultsWidgetActivity"; //$NON-NLS-1$

	protected ListView resultList;
	protected ProgressDialog progressDialog;
	protected CineShowTimeNonExpandableListAdapter adapter = null;

	private ControlerResultsWidgetActivity controler;
	private ListenerResultsWidgetActivity listener;
	private ModelResultsWidgetActivity model;

	private SharedPreferences prefs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		AndShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
		Log.i(TAG, "onCreate"); //$NON-NLS-1$
		setContentView(R.layout.activity_widget_one_results);

		controler = ControlerResultsWidgetActivity.getInstance();
		model = controler.getModelResultsActivity();
		listener = new ListenerResultsWidgetActivity(this, controler, model);

		// We init the theater id if set

		getIntent().putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, false);
		model.setFavTheaterId(getIntent().getStringExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID));
		model.setLocalisation(null);
		model.setCityName(getIntent().getStringExtra(ParamIntent.ACTIVITY_SEARCH_CITY));
		Double latitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, 0);
		Double longitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, 0);
		if (latitude != 0 && longitude != 0) {
			Location locationTheater = new Location("GPS");
			locationTheater.setLatitude(latitude);
			locationTheater.setLongitude(longitude);
			model.setLocalisation(locationTheater);
		}
		getIntent().putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, "");

		initViews();

		controler.registerView(this);

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
		controler.unbindService();
		controler.closeDB();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause"); //$NON-NLS-1$
		if (progressDialog != null && progressDialog.isShowing()) {
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
		adapter = new CineShowTimeNonExpandableListAdapter(this, listener);
	}

	private void initListeners() {
		resultList.setOnItemClickListener(listener);
	}

	protected void display() {
		if (controler.isServiceRunning()) {
			openDialog();
		} else {
			NearResp nearResp = BeanManagerFactory.getNearRespFromWidget();
			if (nearResp != null) {
				boolean error = false;
				List<TheaterBean> theaterList = nearResp.getTheaterList();
				if (theaterList != null && theaterList.size() == 1) {
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
				} else if (theaterList == null || theaterList.size() == 0) {
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
				adapter.setTheaterList(nearResp, model.getTheaterFavList(), AndShowtimeFactory.getTheaterDistanceComparator());
				resultList.setAdapter(adapter);
				if ((nearResp != null) && (nearResp.getCityName() != null) && (nearResp.getCityName().length() > 0)) {
					model.setCityName(nearResp.getCityName());
				}
			}
		}

	}

	protected void launchNearService() throws UnsupportedEncodingException {
		openDialog();

		controler.launchSearchService();
	}

	/**
	 * 
	 */
	protected void openDialog() {
		progressDialog = ProgressDialog.show(AndShowTimeResultsWidgetActivity.this, //
				AndShowTimeResultsWidgetActivity.this.getResources().getString(R.string.searchNearProgressTitle)//
				, AndShowTimeResultsWidgetActivity.this.getResources().getString(R.string.searchNearProgressMsg) //
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

}