package com.binomed.showtime.android.screen.main;

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;

import java.util.Calendar;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

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

public class CineShowTimeMainActivity extends AbstractCineShowTimeActivity<ModelMainFragment> implements CineShowTimeFavFragment.FavFragmentInteraction<ModelMainFragment>, //
		CineShowTimeSearchFragment.SearchFragmentInteraction<ModelMainFragment> //
{

	private static final String TAG = "AndShowTimeMainActivity"; //$NON-NLS-1$
	private static final String TRACKER_NAME = "/MainActivity"; //$NON-NLS-1$

	private static final int MENU_PREF = Menu.NONE;

	private CineShowTimeSearchFragment fragmentSearch;
	private CineShowTimeFavFragment fragmentFav;

	/**
	 * Init views objects
	 */
	private void initViews() {

		// Watch for button clicks.
		fragmentSearch = (CineShowTimeSearchFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentSearch);
		fragmentFav = (CineShowTimeFavFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentFav);

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
		// TODO
		super.onSaveInstanceState(outState);
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
		switch (viewId) {
		case R.id.searchCityName: {
			return CineShowtimeCst.ACTIVITY_RESULT_CITY_SPEECH_SEARCH;
		}
		case R.id.searchMovieName: {
			return CineShowtimeCst.ACTIVITY_RESULT_MOVIE_SPEECH_SEARCH;
		}
		default:
			break;
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

}