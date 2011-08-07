package com.binomed.showtime.android.widget.search;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.adapter.view.TheaterFavMainListAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.layout.view.AutoCompleteTextWithSpeech;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.android.util.CineShowTimeLayoutUtils;
import com.binomed.showtime.android.util.CineShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.android.util.localisation.IListenerLocalisationUtilCallBack;
import com.binomed.showtime.android.widget.CineShowTimeWidgetHelper;
import com.binomed.showtime.android.widget.results.CineShowTimeResultsWidgetActivity;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class CineShowTimeWidgetConfigureActivity extends Activity implements OnClickListener //
		// , LocationListener //
		, OnItemClickListener //
{

	private static final int MENU_PREF = Menu.FIRST;

	public static final Integer ACTIVITY_OPEN_RESULTS = 0;

	private static final String TAG = "SearchWidgetActivity"; //$NON-NLS-1$

	protected AutoCompleteTextWithSpeech fieldCityName;
	protected Button searchButton;
	protected ImageView gpsImgView;
	private ListView theaterFavList;
	protected TheaterFavMainListAdapter adapter;
	private CineShowtimeDbAdapter mDbHelper;

	private ModelAndShowTimeWidget model;

	protected IListenerLocalisationUtilCallBack localisationCallBack;

	private SharedPreferences prefs;
	protected GoogleAnalyticsTracker tracker;

	protected EditText getFieldName() {
		return fieldCityName.getEditText();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start(CineShowtimeCst.GOOGLE_ANALYTICS_ID, this);
		tracker.trackPageView("/ConfigureWidgetActivity");
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		CineShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
		Log.i(TAG, "onCreate"); //$NON-NLS-1$
		setContentView(R.layout.activity_widget_one_search);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		model = new ModelAndShowTimeWidget();

		initViews();
		CineShowTimeWidgetHelper.initWidgetId(this);

		display();
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
		closeDB();
		tracker.dispatch();
		tracker.stop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (localisationCallBack != null) {
			localisationCallBack.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume"); //$NON-NLS-1$
		initListeners();
		initViewsState();

		if (localisationCallBack != null) {
			localisationCallBack.onResume();
		}
	}

	/**
	 * init the view of activity
	 */
	private void initViews() {

		gpsImgView = (ImageView) findViewById(R.id.searchWidgetImgGps);
		searchButton = (Button) findViewById(R.id.searchWidgetBtnSearch);
		fieldCityName = (AutoCompleteTextWithSpeech) findViewById(R.id.searchWidgetCityName);
		theaterFavList = (ListView) findViewById(R.id.searchWidgetFavList);

		// manageCallBack
		localisationCallBack = CineShowTimeLayoutUtils.manageLocationManagement(this, tracker, gpsImgView, fieldCityName, model);
	}

	private void initViewsState() {

	}

	private void initListeners() {
		searchButton.setOnClickListener(this);
		theaterFavList.setOnItemClickListener(this);

	}

	protected void display() {
		List<TheaterBean> favList = getFavTheater();

		if ((favList == null) || (favList.size() == 0)) {
			favList = new ArrayList<TheaterBean>();
			TheaterBean thTmp = new TheaterBean();
			thTmp.setId("0");
			thTmp.setTheaterName(getResources().getString(R.string.msgNoDFav));

			favList.add(thTmp);
		}

		model.setFavList(favList);

		adapter = new TheaterFavMainListAdapter(this, favList, this);

		this.theaterFavList.setAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			setResult(RESULT_OK, data);
			finish();
			break;

		default:
			break;
		}
	}

	/*
	 * 
	 * Events
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchWidgetBtnSearch: {
			String cityName = null;
			if (fieldCityName.getText().toString().length() > 0) {
				cityName = fieldCityName.getText().toString();
			}
			model.setCityName(cityName);

			try {

				boolean canLaunch = true;
				boolean btnCheck = localisationCallBack.isGPSCheck();
				if (btnCheck && (model.getLocalisation() == null)) {
					Toast.makeText(this //
							, R.string.msgNoGps //
							, Toast.LENGTH_LONG) //
							.show();
					canLaunch = false;
				} else if (!btnCheck && (cityName == null)) {
					Toast.makeText(this //
							, R.string.msgNoCityName //
							, Toast.LENGTH_LONG) //
							.show();
					canLaunch = false;
				}

				if (canLaunch) {
					if (btnCheck) {
						model.setCityName(null);
						// model.setLocalisation(model.getLocalisation());
					} else {
						model.setLocalisation(null);
					}
					openResultActivity();
				}

			} catch (Exception e) {
				Log.e(TAG, "erreur au lancement du service", e); //$NON-NLS-1$
			}
			break;
		}
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		TheaterBean theater = model.getFavList().get(groupPosition);
		model.setTheater(theater);
		CineShowTimeWidgetHelper.finalizeWidget(this, theater, model.getCityName());
	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public void openResultActivity() {
		Location gpsLocation = model.getLocalisation();
		String cityName = model.getCityName();
		boolean forceRequest = false;

		Calendar today = Calendar.getInstance();
		forceRequest = true;

		try {
			CineShowtimeFactory.initGeocoder(this);
			Intent intentResultActivity = new Intent(this, CineShowTimeResultsWidgetActivity.class);

			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, CineShowTimeEncodingUtil.getEncoding()) : cityName));
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, forceRequest);
			startActivityForResult(intentResultActivity, ACTIVITY_OPEN_RESULTS);
		} catch (Exception e) {
			Log.e(TAG, "error before sending search intent", e);
		}
	}

	/*
	 * 
	 * DB
	 */

	/**
	 * 
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

		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		} finally {
			closeDB();
		}
	}

	/**
	 * /**
	 * 
	 * @return
	 */
	public List<TheaterBean> getFavTheater() {
		openDB();
		List<TheaterBean> theaterList = null;
		try {
			theaterList = CineShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);
		} catch (Exception e) {
			Log.e(TAG, "Error during getting fav", e);
		} finally {
			closeDB();
		}

		return theaterList;
	}

	/**
	 * 
	 */
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

	public void removeFavorite(TheaterBean theaterBean) {
		try {
			if (mDbHelper.isOpen()) {
				mDbHelper.deleteFavorite(theaterBean.getId());
			}
		} catch (Exception e) {
			Log.e(TAG, "error removing theater from fav", e);
		}

	}

}