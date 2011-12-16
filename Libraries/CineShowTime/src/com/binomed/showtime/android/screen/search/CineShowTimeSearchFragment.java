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
package com.binomed.showtime.android.screen.search;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.layout.view.AutoCompleteTextWithSpeech;
import com.binomed.showtime.android.layout.view.AutoCompleteTextWithSpeech.AutoCompleteInteraction;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsActivity;
import com.binomed.showtime.android.screen.results.tablet.CineShowTimeResultsTabletActivity;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.android.util.CineShowTimeLayoutUtils;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.android.util.activity.IFragmentCineShowTimeInteraction;
import com.binomed.showtime.android.util.localisation.IListenerLocalisationUtilCallBack;
import com.binomed.showtime.cst.SpecialChars;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class CineShowTimeSearchFragment extends Fragment implements OnClickListener //
		, OnItemSelectedListener //
{

	private static final String TAG = "SearchActivity"; //$NON-NLS-1$

	protected TextView lblMovieName, lblDay;
	protected AutoCompleteTextWithSpeech fieldCityName, fieldMovieName;
	protected Button searchButton;
	protected Spinner spinnerChooseDay;
	protected ImageView gpsImgView;

	private IModelSearch model;
	private CineShowtimeDbAdapter mDbHelper;

	protected IListenerLocalisationUtilCallBack localisationCallBack;

	protected GoogleAnalyticsTracker tracker;

	private SearchFragmentInteraction<? extends IModelSearch> fragmentInteraction;

	public void setNullResult(boolean nullResult) {
		if (model != null) {
			fragmentInteraction.setNullResult(nullResult);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// super.onCreate(savedInstanceState);
		tracker = fragmentInteraction.getTracker();

		// Bug fix do to pb of theme. Was forced to set manually the theme.
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(fragmentInteraction.getMainContext());
		String defaultTheme = fragmentInteraction.getMainContext().getResources().getString(R.string.preference_gen_default_theme);
		String theme = pref.getString(fragmentInteraction.getMainContext().getResources().getString(R.string.preference_gen_key_theme), defaultTheme);
		int themeRessource = R.style.Theme_Dark_Night;
		if (!theme.equals(defaultTheme)) {
			themeRessource = R.style.Theme_Shine_the_lite;
		}

		LayoutInflater newInflater = inflater.cloneInContext(new ContextThemeWrapper(fragmentInteraction.getMainContext(), themeRessource));
		View mainView = newInflater.inflate(R.layout.fragment_search, container, false);

		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		model = fragmentInteraction.getModelActivity();

		initViews(mainView);
		initDB();

		display();

		return mainView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		fragmentInteraction = (SearchFragmentInteraction) activity;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy"); //$NON-NLS-1$
		closeDB();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (localisationCallBack != null) {
			localisationCallBack.onPause();
		}
	}

	@Override
	public void onResume() {
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
	 * 
	 * @param mainView
	 */
	private void initViews(View mainView) {

		gpsImgView = (ImageView) mainView.findViewById(R.id.searchImgGps);
		searchButton = (Button) mainView.findViewById(R.id.searchBtnSearch);
		fieldCityName = (AutoCompleteTextWithSpeech) mainView.findViewById(R.id.searchCityName);
		lblMovieName = (TextView) mainView.findViewById(R.id.searchTxtMovieName);
		fieldMovieName = (AutoCompleteTextWithSpeech) mainView.findViewById(R.id.searchMovieName);
		lblDay = (TextView) mainView.findViewById(R.id.searchTxtSpinner);
		spinnerChooseDay = (Spinner) mainView.findViewById(R.id.searchSpinner);

		// manageCallBack
		localisationCallBack = CineShowTimeLayoutUtils.manageLocationManagement(fragmentInteraction.getMainContext(), tracker, gpsImgView, fieldCityName, model);
	}

	private void initViewsState() {

		fillAutoField();
		ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(fragmentInteraction.getMainContext() //
				, R.layout.view_spinner_item//
				, CineShowtimeDateNumberUtil.getSpinnerDaysValues(fragmentInteraction.getMainContext())//
		);
		adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerChooseDay.setAdapter(adapterSpinner);

	}

	protected void fillAutoField() {
		if (fieldCityName != null) {
			ArrayAdapter<String> adapterII = new ArrayAdapter<String>( //
					fragmentInteraction.getMainContext() //
					, android.R.layout.simple_dropdown_item_1line //
					, new ArrayList<String>(model.getRequestList()) //
			);
			fieldCityName.setAdapter(adapterII);
		}
		fieldCityName.setCallBack(fragmentInteraction, getResources().getString(R.string.cityName));
		fieldMovieName.setCallBack(fragmentInteraction, getResources().getString(R.string.movieName));
	}

	private void initListeners() {
		searchButton.setOnClickListener(this);
		spinnerChooseDay.setOnItemSelectedListener(this);

	}

	protected void display() {

		if (model.getLastRequestCity() != null) {
			try {
				fieldCityName.setText(URLDecoder.decode(model.getLastRequestCity(), CineShowTimeEncodingUtil.getEncoding()));
			} catch (Exception e) {
				fieldCityName.setText(model.getLastRequestCity());
			}
		}
		if (model.getLastRequestMovie() != null) {
			try {
				fieldMovieName.setText(URLDecoder.decode(model.getLastRequestMovie(), CineShowTimeEncodingUtil.getEncoding()));
			} catch (Exception e) {
				fieldMovieName.setText(model.getLastRequestMovie());
			}
		}
	}

	/*
	 * 
	 * Event
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.searchBtnSearch) {
			launchSearchWithVerif(-1, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android .widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int groupPositon, long id) {
		if (adapter.getId() == R.id.searchSpinner) {
			model.setDay(groupPositon);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android .widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public boolean delegateOnResultActivity(int requestCode, int resultCode, Intent data) {
		tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_SEARCH // Category
				, CineShowtimeCst.ANALYTICS_ACTION_INTERACTION // Action
				, CineShowtimeCst.ANALYTICS_VALUE_SEARCH_USE_VOICE_SEARCH // Label
				, 0 // Value
		);
		return fieldCityName.onVoiceRecognitionResult(data, resultCode, requestCode) //
				|| fieldMovieName.onVoiceRecognitionResult(data, resultCode, requestCode);
	}

	public void launchSearchWithVerif(int viewId, String text) {
		String cityName = null;
		String movieName = null;
		if (viewId != -1) {
			if (viewId == R.id.searchCityName) {
				cityName = text;
			} else if (viewId == R.id.searchMovieName) {
				movieName = text;
			}
		}
		if (fieldCityName.getText().toString().length() > 0) {
			cityName = fieldCityName.getText().toString();
		}
		if (fieldMovieName.getText().toString().length() > 0) {
			movieName = fieldMovieName.getText().toString();
		}

		model.setCityName(cityName);
		model.setMovieName(movieName);
		model.setFavTheaterId(null);
		model.setStart(0);

		try {

			boolean canLaunch = true;
			boolean btnCheck = localisationCallBack.isGPSCheck();
			if (btnCheck && (model.getLocalisation() == null)) {
				Toast.makeText(fragmentInteraction.getMainContext() //
						, R.string.msgNoGps //
						, Toast.LENGTH_LONG) //
						.show();
				canLaunch = false;
			} else if (!btnCheck && (cityName == null)) {
				Toast.makeText(fragmentInteraction.getMainContext() //
						, R.string.msgNoCityName //
						, Toast.LENGTH_LONG) //
						.show();
				canLaunch = false;
			}

			if (canLaunch) {
				if (!btnCheck) {
					// model.setCityName(null);
					// model.setLocalisation(model.getLocalisation());
					// } else {
					model.setLocalisation(null);
				}
				openResultActivity();
			}

		} catch (Exception e) {
			Log.e(TAG, "erreur au lancement du service", e); //$NON-NLS-1$
		}
	}

	public void openResultActivity() {
		Location gpsLocation = model.getLocalisation();
		String cityName = model.getCityName();
		String movieName = model.getMovieName();
		String lastCityName = model.getLastRequestCity();
		String lastMovieName = model.getLastRequestMovie();
		String theaterId = model.getFavTheaterId();
		boolean nullResult = fragmentInteraction.isNullResult();
		int day = model.getDay();
		boolean forceRequest = false;

		Calendar today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_MONTH, day);
		Calendar calendarLastRequest = fragmentInteraction.getLastRequestDate();
		if (calendarLastRequest != null) {
			int yearToday = today.get(Calendar.YEAR);
			int monthToday = today.get(Calendar.MONTH);
			int dayToday = today.get(Calendar.DAY_OF_MONTH);
			int yearLast = calendarLastRequest.get(Calendar.YEAR);
			int monthLast = calendarLastRequest.get(Calendar.MONTH);
			int dayLast = calendarLastRequest.get(Calendar.DAY_OF_MONTH);
			if ((yearToday != yearLast) //
					|| (monthToday != monthLast) //
					|| (dayToday != dayLast) //
			) {//

				forceRequest = true;
			} else {
				// On a eu un changement de nom de ville ou de nom de film
				forceRequest = ((lastCityName != null) && (cityName != null) && !lastCityName.equals(cityName)) //
						|| ((lastCityName == null) && (cityName != null)) //
						|| ((lastMovieName != null) && (movieName != null) && !lastMovieName.equals(movieName)) //
						|| ((lastMovieName == null) && (movieName != null)) //
						|| ((lastMovieName != null) && (movieName == null)) //
				;
			}
		} else {
			forceRequest = true;
		}

		forceRequest = forceRequest || nullResult;

		if ((cityName != null) && (cityName.length() > 0)) {
			model.getRequestList().add(cityName);
		}
		if ((movieName != null) && (movieName.length() > 0)) {
			model.getRequestMovieList().add(movieName);
		}

		try {
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_SEARCH // Category
					, CineShowtimeCst.ANALYTICS_ACTION_INTERACTION // Action
					, CineShowtimeCst.ANALYTICS_LABEL_SEARCH_GPS // Label
					, model.getLocalisation() != null ? 1 : 0 // Value
			);
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_SEARCH // Category
					, CineShowtimeCst.ANALYTICS_ACTION_INTERACTION // Action
					, CineShowtimeCst.ANALYTICS_LABEL_SEARCH_DAY // Label
					, model.getDay() // Value
			);
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_SEARCH // Category
					, CineShowtimeCst.ANALYTICS_ACTION_INTERACTION // Action
					, CineShowtimeCst.ANALYTICS_LABEL_SEARCH_CITY // Label
					, model.getCityName() != null ? 1 : 0 // Value
			);
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_SEARCH // Category
					, CineShowtimeCst.ANALYTICS_ACTION_INTERACTION // Action
					, CineShowtimeCst.ANALYTICS_LABEL_SEARCH_MOVIE // Label
					, model.getMovieName() != null ? 1 : 0 // Value
			);
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_SEARCH // Category
					, CineShowtimeCst.ANALYTICS_ACTION_INTERACTION // Action
					, CineShowtimeCst.ANALYTICS_LABEL_SEARCH_FORCE_REQUEST // Label
					, forceRequest ? 1 : 0 // Value
			);

			model.setLastRequestCity(cityName);
			model.setLastRequestMovie(movieName);
			model.setLastRequestTheaterId(theaterId);
			fragmentInteraction.setLastRequestDate(today);

			CineShowtimeFactory.initGeocoder(fragmentInteraction.getMainContext());
			Intent intentResultActivity = null;
			if (getActivity().getIntent().getBooleanExtra(ParamIntent.ACTIVITY_LARGE_SCREEN, false)) {
				intentResultActivity = new Intent(fragmentInteraction.getMainContext(), CineShowTimeResultsTabletActivity.class);
			} else {
				intentResultActivity = new Intent(fragmentInteraction.getMainContext(), CineShowTimeResultsActivity.class);
			}

			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, CineShowTimeEncodingUtil.getEncoding()) : cityName));
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_MOVIE_NAME, ((movieName != null) ? URLEncoder.encode(movieName, CineShowTimeEncodingUtil.getEncoding()) : movieName));
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, theaterId);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_DAY, day);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, forceRequest);
			fragmentInteraction.delegateStartSearchResult(intentResultActivity, CineShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY);

		} catch (Exception e) {
			Log.e(TAG, "error before sending search intent", e);
		}
	}

	public void hideMovieFields() {
		if (lblMovieName != null) {
			lblMovieName.setVisibility(View.GONE);
			lblDay.setVisibility(View.GONE);
		}
		fieldMovieName.setVisibility(View.GONE);
		spinnerChooseDay.setVisibility(View.GONE);
	}

	public void savedInstance() {
		model.setCityName(fieldCityName.getText().toString());
		model.setMovieName(fieldMovieName.getText().toString());
		model.setDay(spinnerChooseDay.getSelectedItemPosition());
	}

	public void refreshAfterSavedBundle() {
		fieldCityName.setText(model.getLastRequestCity());
		fieldMovieName.setText(model.getLastRequestMovie());
		spinnerChooseDay.setSelection(model.getDay());

	}

	/*
	 * 
	 * DB
	 */

	public void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new CineShowtimeDbAdapter(fragmentInteraction.getMainContext());
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			openDB();

			if (mDbHelper.isOpen()) {
				// Init requests
				Cursor cursorRequestHistory = mDbHelper.fetchAllMovieRequest();
				String cityName, movieName;
				if (cursorRequestHistory.moveToFirst()) {
					int columnIndex = 0;
					model.getRequestList().clear();
					model.getRequestMovieList().clear();
					do {
						columnIndex = cursorRequestHistory.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_CITY_NAME);
						try {
							cityName = cursorRequestHistory.getString(columnIndex);
							if (cityName != null) {
								model.getRequestList().add(URLDecoder.decode(cityName, CineShowTimeEncodingUtil.getEncoding()));
							}
							columnIndex = cursorRequestHistory.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_MOVIE_NAME);
							movieName = cursorRequestHistory.getString(columnIndex);
							if (movieName != null) {
								model.getRequestMovieList().add(URLDecoder.decode(movieName, CineShowTimeEncodingUtil.getEncoding()));
							}
						} catch (Exception e) {
							Log.e(TAG, "Encode Error", e);
						}
					} while (cursorRequestHistory.moveToNext());
				}
				cursorRequestHistory.close();

				// else we just look at previous request in order to check it's time
				Cursor cursorLastResult = mDbHelper.fetchLastMovieRequest();
				if (cursorLastResult.moveToFirst()) {
					Calendar calendarLastRequest = Calendar.getInstance();
					long timeLastRequest = cursorLastResult.getLong(cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_TIME));
					calendarLastRequest.setTimeInMillis(timeLastRequest);

					// Init localisation from data base
					Location location = new Location(SpecialChars.EMPTY);
					int columnIndex = cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_LATITUDE);
					location.setLatitude(cursorLastResult.getDouble(columnIndex));
					columnIndex = cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_LONGITUDE);
					location.setLongitude(cursorLastResult.getDouble(columnIndex));

					model.setLocalisation(location);
					fragmentInteraction.setLastRequestDate(calendarLastRequest);

					try {
						columnIndex = cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_CITY_NAME);
						cityName = cursorLastResult.getString(columnIndex);
						// Manage case of saved bundle
						if (cityName != null) {
							model.setLastRequestCity(URLDecoder.decode(cityName, CineShowTimeEncodingUtil.getEncoding()));
						}
						columnIndex = cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_MOVIE_NAME);
						movieName = cursorLastResult.getString(columnIndex);
						// Manage case of saved bundle
						if (movieName != null) {
							model.setLastRequestMovie(URLDecoder.decode(movieName, CineShowTimeEncodingUtil.getEncoding()));
						}
						columnIndex = cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_THEATER_ID);
						model.setLastRequestTheaterId(cursorLastResult.getString(columnIndex));
						columnIndex = cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_NULL_RESULT);
						fragmentInteraction.setNullResult(cursorLastResult.getShort(columnIndex) == 1);
					} catch (Exception e) {
						Log.e(TAG, "Encode Error", e);
					}
				}
				cursorLastResult.close();
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

	public interface SearchFragmentInteraction<M extends IModelSearch> extends IFragmentCineShowTimeInteraction<M>, AutoCompleteInteraction {

		void setNullResult(boolean result);

		boolean isNullResult();

		void setLastRequestDate(Calendar lastRequestDate);

		Calendar getLastRequestDate();

		void delegateStartSearchResult(Intent intent, int requestCode);

	}

}