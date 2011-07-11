package com.binomed.showtime.android.screen.main;

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
import com.binomed.showtime.android.screen.fav.CineShowTimeFavFragment;
import com.binomed.showtime.android.screen.search.CineShowTimeSearchFragment;
import com.binomed.showtime.android.service.CineShowCleanFileService;
import com.binomed.showtime.android.service.CineShowDBGlobalService;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.android.util.activity.AbstractCineShowTimeActivity;

public class CineShowTimeMainActivity extends AbstractCineShowTimeActivity<ModelMainFragment> implements
// OnClickListener, //
// OnItemClickListener, //
		CineShowTimeFavFragment.FavFragmentInteraction, //
		CineShowTimeSearchFragment.SearchFragmentInteraction //
{

	private static final String TAG = "AndShowTimeMainActivity"; //$NON-NLS-1$
	private static final String TRACKER_NAME = "/MainActivity"; //$NON-NLS-1$

	private static final int MENU_PREF = Menu.FIRST;

	// private Context mainContext;
	// private ModelMainFragment model;
	// private CineShowtimeDbAdapter mDbHelper;

	// private GoogleAnalyticsTracker tracker;

	private CineShowTimeSearchFragment fragmentSearch;
	private CineShowTimeFavFragment fragmentFav;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// tracker = GoogleAnalyticsTracker.getInstance();
		// tracker.start(CineShowtimeCst.GOOGLE_ANALYTICS_ID, this);
		// tracker.trackPageView("/MainActivity");
		//
		// SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		// CineShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
		// setContentView(R.layout.activity_main);

		// mainContext = this;
		//
		// CineShowtimeFactory.initGeocoder(this);
		//
		// Intent intentCleanFileService = new Intent(CineShowTimeMainActivity.this, CineShowCleanFileService.class);
		// startService(intentCleanFileService);
		//
		// initViews();
		// initListeners();
		//
		// this.model = new ModelMainFragment();
		//
		// display();
		//
		// initResults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	//		Log.i(TAG, "onDestroy"); //$NON-NLS-1$
	// closeDB();
	// tracker.dispatch();
	// tracker.stop();
	// }

	/*
	 * 
	 * Init Views
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	// @Override
	// protected void onResume() {
	// super.onResume();
	// }

	// private void initResults() {
	// Intent intentResult = new Intent();
	// intentResult.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, model.isResetTheme());
	// intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, model.isNullResult());
	// setResult(CineShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY, intentResult);
	// }

	/**
	 * Init views objects
	 */
	private void initViews() {

		// Watch for button clicks.
		fragmentSearch = (CineShowTimeSearchFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentSearch);
		fragmentFav = (CineShowTimeFavFragment) getSupportFragmentManager().findFragmentById(R.id.FragmentFav);
	}

	/**
	 * Init listener
	 */

	private void initListeners() {
		// buttonSearchNear.setOnClickListener(this);
		// theaterFavList.setOnItemClickListener(this);
	}

	private void display() {
		if (this.showLastChange()) {
			LastChangeDialog dialog = new LastChangeDialog(this);
			dialog.show();
		}

	}

	/*
	 * 
	 * 
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// super.onCreateOptionsMenu(menu);
	// tracker.trackEvent("Menu", "Open", "Consult menu from main activity", 0);
	// CineShowTimeMenuUtil.createMenu(menu, MENU_PREF, 0);
	// return true;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	// @Override
	// public boolean onMenuItemSelected(int featureId, MenuItem item) {
	// if (CineShowTimeMenuUtil.onMenuItemSelect(this, tracker, MENU_PREF, item.getItemId())) {
	// return true;
	// }
	//
	// return super.onMenuItemSelected(featureId, item);
	// }

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// if (data != null) {
	// model.setNullResult(data.getBooleanExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, false));
	// model.setResetTheme(data.getBooleanExtra(ParamIntent.PREFERENCE_RESULT_THEME, false));
	// } else {
	// model.setResetTheme(false);
	// model.setNullResult(false);
	// }
	//
	// initResults();
	//
	// if (model.isResetTheme()) {
	// CineShowTimeLayoutUtils.changeToTheme(this, getIntent());
	// }
	// }

	/*
	 * 
	 * DataBase
	 */

	// public void openDB() {
	//
	// try {
	//			Log.i(TAG, "openDB"); //$NON-NLS-1$
	// mDbHelper = new CineShowtimeDbAdapter(this);
	// mDbHelper.open();
	// } catch (SQLException e) {
	//			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
	// }
	// }

	public void initDB() {

		try {

			if (mDbHelper.isOpen()) {
				// else we just look at previous request in order to check it's time
				Cursor cursorLastResult = mDbHelper.fetchLastMovieRequest();
				if (cursorLastResult.moveToFirst()) {
					Calendar calendarLastRequest = Calendar.getInstance();
					long timeLastRequest = cursorLastResult.getLong(cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_TIME));
					calendarLastRequest.setTimeInMillis(timeLastRequest);

					model.setLastRequestDate(calendarLastRequest);
					model.setNullResult(cursorLastResult.getShort(cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_NULL_RESULT)) == 1);
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
		// openDB();
		boolean result = false;
		int versionCode = -1;
		if (mDbHelper.isOpen()) {
			Cursor cursorLastChange = mDbHelper.fetchLastChange();
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
	// public void closeDB() {
	// try {
	// if (mDbHelper.isOpen()) {
	//				Log.i(TAG, "Close DB"); //$NON-NLS-1$
	// mDbHelper.close();
	// }
	// } catch (Exception e) {
	//			Log.e(TAG, "error onDestroy of movie Activity", e); //$NON-NLS-1$
	// }
	// }

	/*
	 * 
	 * Fragement Interaction
	 */

	@Override
	public void setLastRequestDate(Calendar today) {
		model.setLastRequestDate(today);

	}

	@Override
	public Calendar getLastRequestDate() {
		return model.getLastRequestDate();
	}

	@Override
	public void setNullResult(boolean result) {
		model.setNullResult(result);
	}

	@Override
	public boolean isNullResult() {
		return model.isNullResult();
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
		initListeners();

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

}