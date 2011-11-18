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
package com.binomed.showtime.android.screen.widget.results;

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment.CineShowTimeResultInteraction;
import com.binomed.showtime.android.util.activity.AbstractSimpleCineShowTimeActivity;

public class CineShowTimeResultsWidgetActivity extends AbstractSimpleCineShowTimeActivity<CineShowTimeResultsFragment, ModelResultsWidgetActivity> implements CineShowTimeResultInteraction<ModelResultsWidgetActivity> {

	public static final Integer ACTIVITY_OPEN_MOVIE = 0;
	private static final int MENU_PREF = Menu.NONE;

	private static final String TAG = "ResultsWidgetActivity"; //$NON-NLS-1$
	private Intent intentResult;

	/*
	 * Override methods
	 */

	@Override
	protected CineShowTimeResultsFragment getFragment(Fragment fragmentRecycle) {
		setResult(Activity.RESULT_CANCELED);
		CineShowTimeResultsFragment resultFragment = null;
		if (fragmentRecycle != null) {
			resultFragment = (CineShowTimeResultsFragment) fragmentRecycle;
		} else {
			resultFragment = new CineShowTimeResultsFragment();
		}
		resultFragment.setNonExpendable(true);
		if (intentResult != null) {
			resultFragment.setIntentResult(intentResult);
		}
		return resultFragment;
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
	protected int getMenuKey() {
		return MENU_PREF;
	}

	@Override
	protected ModelResultsWidgetActivity getModel() {
		return new ModelResultsWidgetActivity();
	}

	@Override
	protected void doChangeFromPref() {
		fragment.changePreferences();
	}

	@Override
	protected void doOnCancel() {
		fragment.onCancel();
	}

	@Override
	protected void addActionBarItems(ActionBar actionBar) {
		// nothing to do
	}

	@Override
	protected boolean delegateOnActionBarItemClick(ActionBarItem item, int position) {
		// nothing to do
		return false;
	}

	@Override
	protected boolean isHomeActivity() {
		// nothing to do
		return false;
	}

	/*
	 * Overides methods from activity
	 */

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (getModelActivity().getNearResp() != null) {
			outState.putBoolean(ParamIntent.BUNDLE_SAVE, true);
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
	protected void onPreRestoreBundle(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			boolean saved = savedInstanceState.getBoolean(ParamIntent.BUNDLE_SAVE, false);
			if (saved) {
				getModelActivity().setNearResp((NearResp) savedInstanceState.getParcelable(ParamIntent.NEAR_RESP));
				getModelActivity().setForceResearch(savedInstanceState.getBoolean(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, false));
				intentResult = new Intent();
				intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, false);
				String cityName = savedInstanceState.getString(ParamIntent.ACTIVITY_SEARCH_CITY);
				intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, cityName);
				getModelActivity().setCityName(cityName);
				Double latitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_SEARCH_LATITUDE, 0);
				Double longitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, 0);
				if ((latitude != 0) && (longitude != 0)) {
					intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, latitude);
					intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, longitude);
					Location location = new Location("GPS");
					location.setLatitude(latitude);
					location.setLongitude(longitude);
					getModelActivity().setLocalisation(location);
				}
			}

		}
	}

	/*
	 * Fragment Interaction
	 */

	@Override
	protected int getDialogTitle() {
		return R.string.searchNearProgressTitle;
	}

	@Override
	protected int getDialogMsg() {
		return R.string.searchNearProgressMsg;
	}

	@Override
	public void openMovieScreen(MovieBean movie, TheaterBean theater) {
		// Nothing to do

	}

	@Override
	public void onGroupClick() {
		// nothing to do
	}

	@Override
	public void onTheaterClick(TheaterBean theaterBean) {
		Intent data = new Intent();
		data.putExtra(ParamIntent.THEATER, theaterBean);
		setResult(RESULT_OK, data);
		finish();

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
	public void onMovieClick(MovieBean movie) {
		// nothing to do

	}
}