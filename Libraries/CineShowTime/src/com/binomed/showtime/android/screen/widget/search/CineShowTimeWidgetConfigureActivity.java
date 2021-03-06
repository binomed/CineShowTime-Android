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
package com.binomed.showtime.android.screen.widget.search;

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;

import java.util.Calendar;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.fav.CineShowTimeFavFragment;
import com.binomed.showtime.android.screen.fav.CineShowTimeFavFragment.FavFragmentInteraction;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment.CineShowTimeResultInteraction;
import com.binomed.showtime.android.screen.search.CineShowTimeSearchFragment;
import com.binomed.showtime.android.screen.search.CineShowTimeSearchFragment.SearchFragmentInteraction;
import com.binomed.showtime.android.screen.widget.results.CineShowTimeResultsWidgetActivity;
import com.binomed.showtime.android.util.activity.AbstractCineShowTimeActivity;
import com.binomed.showtime.android.widget.CineShowTimeWidgetHelper;

public class CineShowTimeWidgetConfigureActivity extends AbstractCineShowTimeActivity<ModelCineShowTimeWidget> implements //
		SearchFragmentInteraction<ModelCineShowTimeWidget> //
		, FavFragmentInteraction<ModelCineShowTimeWidget> //
		, CineShowTimeResultInteraction<ModelCineShowTimeWidget> {

	private static final String TAG = "SearchWidgetActivity"; //$NON-NLS-1$
	private static final int MENU = Menu.FIRST;

	private CineShowTimeSearchFragment searchFragment;
	private CineShowTimeFavFragment favFragment;
	private CineShowTimeResultsFragment resultFragment;
	private LinearLayout zoneResultWidget;
	private Intent searchIntent;

	/*
	 * Overrides
	 */

	@Override
	protected int getMenuKey() {
		return MENU;
	}

	@Override
	protected int getLayout() {
		return R.layout.activity_widget;
	}

	@Override
	protected String getTrackerName() {
		return TAG;
	}

	@Override
	protected String getTAG() {
		return TAG;
	}

	@Override
	protected void initContentView() {
		favFragment = (CineShowTimeFavFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentFav);
		searchFragment = (CineShowTimeSearchFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentSearch);
		zoneResultWidget = (LinearLayout) findViewById(R.id.zoneWidgetResults);

		searchFragment.hideMovieFields();
		setResult(Activity.RESULT_CANCELED);

	}

	@Override
	protected ModelCineShowTimeWidget getModel() {
		return new ModelCineShowTimeWidget();
	}

	@Override
	protected void doOnCancel() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doChangeFromPref() {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getDialogTitle() {
		return R.string.searchNearProgressTitle;
	}

	@Override
	protected int getDialogMsg() {
		return R.string.searchNearProgressMsg;
	}

	@Override
	protected void addActionBarItems(ActionBar actionBar) {
		// nothing to do
	}

	@Override
	protected boolean delegateOnActionBarItemClick(ActionBarItem item, int position) {
		return false;
	}

	@Override
	protected boolean isHomeActivity() {
		return false;
	}

	/*
	 * Overides methods from activity
	 */

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(ParamIntent.BUNDLE_SAVE, true);
		// we save search fragment information
		searchFragment.savedInstance();
		outState.putString(ParamIntent.ACTIVITY_SEARCH_CITY, getModelActivity().getCityName());
		outState.putString(ParamIntent.ACTIVITY_SEARCH_MOVIE_NAME, getModelActivity().getMovieName());
		outState.putInt(ParamIntent.ACTIVITY_SEARCH_DAY, getModelActivity().getDay());
		outState.putDouble(ParamIntent.ACTIVITY_SEARCH_LATITUDE, getModelActivity().getLocalisation() != null ? getModelActivity().getLocalisation().getLatitude() : 0);
		outState.putDouble(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, getModelActivity().getLocalisation() != null ? getModelActivity().getLocalisation().getLongitude() : 0);
		// We save results fragment information
		if (resultFragment != null) {
			outState.putBoolean(ParamIntent.ACTIVITY_WIDGET_SHOW_RESULTS, resultFragment != null);
			if (getModelActivity().getNearResp() != null) {
				outState.putParcelable(ParamIntent.NEAR_RESP, getModelActivity().getNearResp());
			} else {
				outState.putBoolean(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, getModelActivity().isForceResearch());
			}
			if (getModelActivity().getLocalisation() != null) {
				outState.putDouble(ParamIntent.ACTIVITY_SEARCH_LATITUDE, getModelActivity().getLocalisation().getLatitude());
				outState.putDouble(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, getModelActivity().getLocalisation().getLongitude());
			}
			outState.putString(ParamIntent.ACTIVITY_SEARCH_CITY, getModelActivity().getCityName());
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPostRestoreBundle(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			boolean saved = savedInstanceState.getBoolean(ParamIntent.BUNDLE_SAVE, false);
			if (saved) {
				// We restore search Fragment informations
				getModelActivity().setLastRequestCity(savedInstanceState.getString(ParamIntent.ACTIVITY_SEARCH_CITY));
				getModelActivity().setLastRequestMovie(savedInstanceState.getString(ParamIntent.ACTIVITY_SEARCH_MOVIE_NAME));
				getModelActivity().setDay(savedInstanceState.getInt(ParamIntent.ACTIVITY_SEARCH_DAY, 0));
				double latitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_SEARCH_LATITUDE, 0);
				double longitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, 0);
				if ((latitude != 0) && (longitude != 0)) {
					Location location = new Location("GPS");
					location.setLatitude(latitude);
					location.setLongitude(longitude);
					getModelActivity().setLocalisation(location);
				}
				searchFragment.refreshAfterSavedBundle();

				// We restore result fragment informations
				boolean restoreResult = savedInstanceState.getBoolean(ParamIntent.ACTIVITY_WIDGET_SHOW_RESULTS, false);
				if (restoreResult) {
					getModelActivity().setNearResp((NearResp) savedInstanceState.getParcelable(ParamIntent.NEAR_RESP));
					getModelActivity().setForceResearch(savedInstanceState.getBoolean(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, false));
					resultFragment = new CineShowTimeResultsFragment();
					resultFragment.setNonExpendable(true);
					Intent intentResult = new Intent();
					intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, false);
					String cityName = savedInstanceState.getString(ParamIntent.ACTIVITY_SEARCH_CITY);
					intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, cityName);
					getModelActivity().setCityName(cityName);
					latitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_SEARCH_LATITUDE, 0);
					longitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, 0);
					if ((latitude != 0) && (longitude != 0)) {
						intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, latitude);
						intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, longitude);
						Location location = new Location("GPS");
						location.setLatitude(latitude);
						location.setLongitude(longitude);
						getModelActivity().setLocalisation(location);
					}
					resultFragment.setIntentResult(intentResult);
					getSupportFragmentManager().beginTransaction().replace(R.id.zoneWidgetResults, resultFragment).commit();
				}

			}
		}
	}

	/*
	 * Interaction
	 */

	@Override
	public void hasFav(boolean hasFav) {
		// nothing to do

	}

	@Override
	public int getRequestCode(int itemId) {
		return CineShowtimeCst.ACTIVITY_RESULT_CITY_SPEECH_SEARCH;
	}

	@Override
	public void fireSearch(int viewId, String text) {
		searchFragment.launchSearchWithVerif(viewId, text);
	}

	@Override
	public void setNullResult(boolean result) {
		getModelActivity().setNullResult(result);

	}

	@Override
	public boolean isNullResult() {
		return getModelActivity().isNullResult();
	}

	@Override
	public void setLastRequestDate(Calendar lastRequestDate) {
		getModelActivity().setLastRequestDate(lastRequestDate);

	}

	@Override
	public Calendar getLastRequestDate() {
		return getModelActivity().getLastRequestDate();
	}

	@Override
	public void delegateStartSearchResult(Intent intent, int requestCode) {
		if (zoneResultWidget == null) {
			intent.setClass(getApplicationContext(), CineShowTimeResultsWidgetActivity.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID));
			startActivityForResult(intent, requestCode);
		} else {
			resultFragment = new CineShowTimeResultsFragment();
			resultFragment.setNonExpendable(true);
			searchIntent = intent;
			resultFragment.setIntentResult(intent);
			getSupportFragmentManager().beginTransaction().replace(R.id.zoneWidgetResults, resultFragment).commit();
		}

	}

	@Override
	public boolean onFavClick(TheaterBean theater) {
		getTracker().trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_WIDGET // Category
				, CineShowtimeCst.ANALYTICS_ACTION_CHOOSE // Action
				, CineShowtimeCst.ANALYTICS_VALUE_WIDGET_FAV // Label
				, 0 // Value
				);
		CineShowTimeWidgetHelper.finalizeWidget(this, theater, getModelActivity().getCityName());
		return true;
	}

	@Override
	public void openMovieScreen(MovieBean movie, TheaterBean theater) {
		// nothing to do

	}

	@Override
	public void onGroupClick() {
		// nothing to do

	}

	@Override
	public void onTheaterClick(TheaterBean theater) {
		getTracker().trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_WIDGET // Category
				, CineShowtimeCst.ANALYTICS_ACTION_CHOOSE // Action
				, CineShowtimeCst.ANALYTICS_VALUE_WIDGET_SEARCH // Label
				, 0 // Value
				);
		CineShowTimeWidgetHelper.finalizeWidget(this, theater, getModelActivity().getCityName());
	}

	@Override
	public void onChildClick() {
		// nothing to do

	}

	@Override
	public void onFocusListener(boolean focus) {
		// nothing to do

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == CineShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY) && (resultCode == RESULT_OK)) {
			getTracker().trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_WIDGET // Category
					, CineShowtimeCst.ANALYTICS_ACTION_CHOOSE // Action
					, CineShowtimeCst.ANALYTICS_VALUE_WIDGET_SEARCH // Label
					, 0 // Value
					);
			TheaterBean theaterBean = data.getExtras().getParcelable(ParamIntent.THEATER);
			CineShowTimeWidgetHelper.finalizeWidget(this, theaterBean, getModelActivity().getCityName());
		}
	}

	@Override
	public void onMovieClick(MovieBean movie) {
		// nothing to do

	}

}