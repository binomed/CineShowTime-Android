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
package com.binomed.showtime.android.screen.main;

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;

import java.util.Calendar;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.layout.dialogs.last.LastChangeDialog;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.fav.CineShowTimeFavFragment;
import com.binomed.showtime.android.screen.search.CineShowTimeSearchFragment;
import com.binomed.showtime.android.service.CineShowCleanFileService;
import com.binomed.showtime.android.service.CineShowDBGlobalService;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.android.util.activity.AbstractCineShowTimeActivity;
import com.binomed.showtime.android.util.activity.TestSizeHoneyComb;
import com.binomed.showtime.android.util.activity.TestSizeOther;
import com.binomed.showtime.cst.HttpParamsCst;

public class CineShowTimeMainActivity extends AbstractCineShowTimeActivity<ModelMainFragment> implements CineShowTimeFavFragment.FavFragmentInteraction<ModelMainFragment>, //
		CineShowTimeSearchFragment.SearchFragmentInteraction<ModelMainFragment> //
{

	private static final String TAG = "AndShowTimeMainActivity"; //$NON-NLS-1$
	private static final String TRACKER_NAME = "/MainActivity"; //$NON-NLS-1$
	private TabHost tabHost;

	private static final int MENU_PREF = Menu.NONE;

	private CineShowTimeSearchFragment fragmentSearch;
	private CineShowTimeFavFragment fragmentFav;

	private boolean firstShow = true;

	/**
	 * Init views objects
	 */
	private void initViews() {

		PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			// Send global application informations
			getTracker().trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_APPLICATION // Category
					, CineShowtimeCst.ANALYTICS_ACTION_VERSION // Action
					, pi.versionName // Label
					, 0 // Value
					);
			getTracker().trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_APPLICATION // Category
					, CineShowtimeCst.ANALYTICS_ACTION_ANDROID_RELEASE // Action
					, Build.VERSION.RELEASE // Label
					, 0 // Value
					);
			getTracker().trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_APPLICATION // Category
					, CineShowtimeCst.ANALYTICS_ACTION_ANDROID_SDK // Action
					, Build.VERSION.SDK// Label
					, 0 // Value
					);
			getTracker().trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_APPLICATION // Category
					, CineShowtimeCst.ANALYTICS_ACTION_ANDROID_VERSION // Action
					, Build.VERSION.CODENAME // Label
					, 0 // Value
					);
			getTracker().trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_APPLICATION // Category
					, CineShowtimeCst.ANALYTICS_ACTION_TABLET // Action
					, String.valueOf((Integer.valueOf(Build.VERSION.SDK) <= 10) ? TestSizeOther.checkLargeScreen(getResources().getConfiguration().screenLayout) : TestSizeHoneyComb.checkLargeScreen(getResources().getConfiguration().screenLayout)) // Label
					, 1 // Value
					);
			String appEngineUrl = getPrefs().getString(CineShowtimeCst.PREF_KEY_APP_ENGINE, null);
			if (appEngineUrl == null) {
				appEngineUrl = HttpParamsCst.BINOMED_APP_URL;
			}
			getTracker().trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_APPLICATION // Category
					, CineShowtimeCst.ANALYTICS_ACTION_APP_ENGINE_VERSION // Action
					, appEngineUrl// Label
					, 0 // Value
					);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error trying getting package information");
		}

		// Watch for button clicks.
		fragmentSearch = (CineShowTimeSearchFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentSearch);
		fragmentFav = (CineShowTimeFavFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentFav);

		// Manage case of layout with tabs
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		if (tabHost != null) {
			tabHost.setup();
			tabHost.getTabWidget().setDividerDrawable(R.drawable.cst_tab_divider);

			// We have to create the tabs*
			TabHost.TabSpec tabSearch = tabHost.newTabSpec("Search");
			tabSearch.setContent(R.id.FragmentLayout);
			View viewSearch = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_tab_item, null);
			TextView tvSearch = (TextView) viewSearch.findViewById(R.id.title);
			tvSearch.setText(R.string.search);
			ImageView ivSearch = (ImageView) viewSearch.findViewById(R.id.icon);
			ivSearch.setBackgroundResource(R.drawable.ic_tab_search);

			// tabSearch.setIndicator(getResources().getString(R.string.search), getResources().getDrawable(R.drawable.ic_tab_search));
			tabSearch.setIndicator(viewSearch);

			TabHost.TabSpec tabFav = tabHost.newTabSpec("Fav");
			tabFav.setContent(R.id.fragmentFav);
			View viewFav = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_tab_item, null);
			TextView tvFav = (TextView) viewFav.findViewById(R.id.title);
			tvFav.setText(R.string.btnFav);
			ImageView ivFav = (ImageView) viewFav.findViewById(R.id.icon);
			ivFav.setBackgroundResource(R.drawable.ic_tab_fav);
			// tabFav.setIndicator(getResources().getString(R.string.btnFav), getResources().getDrawable(R.drawable.ic_tab_fav));
			tabFav.setIndicator(viewFav);

			tabHost.addTab(tabSearch);
			tabHost.addTab(tabFav);

		}
	}

	private void display() {
		if (this.showLastChange()) {
			LastChangeDialog dialog = new LastChangeDialog(this);
			dialog.show();
		}

	}

	/*
	 * 
	 * DataBase
	 */

	public void initDB() {

		try {

			if (getMDbHelper().isOpen()) {
				// else we just look at previous request in order to check it's time
				Cursor cursorLastResult = getMDbHelper().fetchLastMovieRequest();
				if (cursorLastResult.moveToFirst()) {
					Calendar calendarLastRequest = Calendar.getInstance();
					long timeLastRequest = cursorLastResult.getLong(cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_TIME));
					calendarLastRequest.setTimeInMillis(timeLastRequest);

					getModelActivity().setLastRequestDate(calendarLastRequest);
					getModelActivity().setNullResult(cursorLastResult.getShort(cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_NULL_RESULT)) == 1);
				}
				cursorLastResult.close();
			}
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		} finally {
			closeDB();
		}
	}

	/**
	 * @return
	 */
	public boolean showLastChange() {
		boolean result = false;
		int versionCode = -1;
		if (getMDbHelper().isOpen()) {
			Cursor cursorLastChange = getMDbHelper().fetchLastChange();
			try {
				PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
				versionCode = pi.versionCode;
			} catch (Exception e) {
				Log.e(TAG, "Error getting package for activity", e); //$NON-NLS-1$
			}
			try {
				if (cursorLastChange != null) {
					if (cursorLastChange.moveToFirst()) {
						int columnIndex = cursorLastChange.getColumnIndex(CineShowtimeDbAdapter.KEY_LAST_CHANGE_VERSION);
						int codeVersion = cursorLastChange.getInt(columnIndex);

						result = codeVersion != versionCode;

					} else {
						result = true;
					}
				} else {
					result = true;
				}
			} finally {
				if (cursorLastChange != null) {
					cursorLastChange.close();
				}
				closeDB();
			}
		}

		if (result) {
			Intent intentService = new Intent(this, CineShowDBGlobalService.class);
			intentService.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_LAST_CHANGE_WRITE);
			intentService.putExtra(ParamIntent.SERVICE_DB_VAL_VERSION_CODE, versionCode);
			startService(intentService);
		}

		return result;
	}

	/**
	 * 
	 */

	/*
	 * 
	 * Fragement Interaction
	 */

	@Override
	public void setLastRequestDate(Calendar today) {
		getModelActivity().setLastRequestDate(today);

	}

	@Override
	public Calendar getLastRequestDate() {
		return getModelActivity().getLastRequestDate();
	}

	@Override
	public void setNullResult(boolean result) {
		getModelActivity().setNullResult(result);
	}

	@Override
	public boolean isNullResult() {
		return getModelActivity().isNullResult();
	}

	/*
	 * Overides methods from activity
	 */

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(ParamIntent.BUNDLE_SAVE, true);
		fragmentSearch.savedInstance();
		outState.putString(ParamIntent.ACTIVITY_SEARCH_CITY, getModelActivity().getCityName());
		outState.putString(ParamIntent.ACTIVITY_SEARCH_MOVIE_NAME, getModelActivity().getMovieName());
		outState.putInt(ParamIntent.ACTIVITY_SEARCH_DAY, getModelActivity().getDay());
		outState.putDouble(ParamIntent.ACTIVITY_SEARCH_LATITUDE, getModelActivity().getLocalisation() != null ? getModelActivity().getLocalisation().getLatitude() : 0);
		outState.putDouble(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, getModelActivity().getLocalisation() != null ? getModelActivity().getLocalisation().getLongitude() : 0);
		Log.i(TAG, "Latitude : " + (getModelActivity().getLocalisation() != null ? getModelActivity().getLocalisation().getLatitude() : 0));
		Log.i(TAG, "Longitude : " + (getModelActivity().getLocalisation() != null ? getModelActivity().getLocalisation().getLongitude() : 0));
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPostRestoreBundle(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			boolean saved = savedInstanceState.getBoolean(ParamIntent.BUNDLE_SAVE, false);
			if (saved) {
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
				Log.i(TAG, "Latitude : " + latitude);
				Log.i(TAG, "Longitude : " + longitude);
				fragmentSearch.refreshAfterSavedBundle();

			}
		}
	}

	/*
	 * 
	 * Override methods
	 */

	@Override
	protected int getLayout() {
		return R.layout.activity_main;
	}

	@Override
	protected String getTrackerName() {
		return TAG;
	}

	@Override
	protected String getTAG() {
		return TRACKER_NAME;
	}

	@Override
	protected void initContentView() {
		CineShowtimeFactory.initGeocoder(this);

		Intent intentCleanFileService = new Intent(CineShowTimeMainActivity.this, CineShowCleanFileService.class);
		startService(intentCleanFileService);

		initViews();

		display();
	}

	@Override
	protected void doOnCancel() {
		// nothing to do

	}

	@Override
	protected void doChangeFromPref() {
		// nothing to do

	}

	@Override
	protected int getMenuKey() {
		return MENU_PREF;
	}

	@Override
	protected ModelMainFragment getModel() {
		return new ModelMainFragment();
	}

	@Override
	protected int getDialogTitle() {
		// nothing to do
		return 0;
	}

	@Override
	protected int getDialogMsg() {
		// nothing to do
		return 0;
	}

	@Override
	protected boolean delegateOnActionBarItemClick(ActionBarItem item, int position) {
		// Nothing to do
		return false;
	}

	@Override
	protected void addActionBarItems(ActionBar actionBar) {
		// nothing to do
	}

	@Override
	protected boolean isHomeActivity() {
		return true;
	}

	@Override
	public int getRequestCode(int viewId) {
		if (viewId == R.id.searchCityName) {
			return CineShowtimeCst.ACTIVITY_RESULT_CITY_SPEECH_SEARCH;
		} else if (viewId == R.id.searchMovieName) {
			return CineShowtimeCst.ACTIVITY_RESULT_MOVIE_SPEECH_SEARCH;
		}
		return 0;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((fragmentSearch != null) && !fragmentSearch.delegateOnResultActivity(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void fireSearch(int viewId, String text) {
		if (fragmentSearch != null) {
			fragmentSearch.launchSearchWithVerif(viewId, text);
		}

	}

	@Override
	public void delegateStartSearchResult(Intent intent, int requestCode) {
		startActivityForResult(intent, requestCode);
	}

	@Override
	public boolean onFavClick(TheaterBean theater) {
		return false;
	}

	@Override
	public void hasFav(boolean hasFav) {
		if ((tabHost != null) && hasFav && firstShow) {
			tabHost.setCurrentTab(1);
		}
		firstShow = false;
	}

}