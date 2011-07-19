package com.binomed.showtime.android.util.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.util.CineShowTimeLayoutUtils;
import com.binomed.showtime.android.util.CineShowTimeMenuUtil;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public abstract class AbstractCineShowTimeActivity<M extends ICineShowTimeActivityHelperModel> //
		extends FragmentActivity //
		implements //
		OnCancelListener, //
		IFragmentCineShowTimeInteraction<M> {

	private GoogleAnalyticsTracker tracker;
	private SharedPreferences prefs;
	private M model;
	private ProgressDialog progressDialog;
	// private String TAG = null;
	private CineShowtimeDbAdapter mDbHelper;
	protected final int MENU_PREF = getMenuKey();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTracker();
		// tracker = GoogleAnalyticsTracker.getInstance();
		// tracker.start(CineShowtimeCst.GOOGLE_ANALYTICS_ID, this);
		// tracker.trackPageView(getTrackerName());
		// TAG = getTAG();
		// prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		CineShowTimeLayoutUtils.onActivityCreateSetTheme(this, getPrefs());
		Log.i(getTAG(), "onCreate"); //$NON-NLS-1$

		// We call the contentView
		openDB();
		getModelActivity();
		setContentView(getLayout());
		initContentView();

		initResults();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(getTAG(), "onDestroy"); //$NON-NLS-1$
		closeDB();
		getTracker().dispatch();
		getTracker().stop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(getTAG(), "onPause"); //$NON-NLS-1$
		if ((progressDialog != null) && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			getModelActivity().setNullResult(data.getBooleanExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, false));
			getModelActivity().setResetTheme(data.getBooleanExtra(ParamIntent.PREFERENCE_RESULT_THEME, false));
		} else {
			getModelActivity().setResetTheme(false);
			getModelActivity().setNullResult(false);
		}

		initResults();

		if (requestCode == CineShowtimeCst.ACTIVITY_RESULT_PREFERENCES) {
			doChangeFromPref();
		}

		if (getModelActivity().isResetTheme()) {
			CineShowTimeLayoutUtils.changeToTheme(this, getIntent());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getTracker().trackEvent("Menu", "Open", "Consult menu from main activity", 0);
		CineShowTimeMenuUtil.createMenu(menu, MENU_PREF, 0);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (CineShowTimeMenuUtil.onMenuItemSelect(this, tracker, MENU_PREF, item.getItemId())) {
			return true;
		}

		return false;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		doOnCancel();
		finish();
	}

	private void initResults() {
		Intent intentResult = new Intent();
		intentResult.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, getModelActivity().isResetTheme());
		intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, getModelActivity().isNullResult());
		setResult(CineShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY, intentResult);
	}

	/*
	 * DB Methods
	 */

	private void openDB() {

		try {
			Log.i(getTAG(), "openDB"); //$NON-NLS-1$
			mDbHelper = new CineShowtimeDbAdapter(this);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(getTAG(), "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	protected void closeDB() {
		try {
			if ((mDbHelper != null) && mDbHelper.isOpen()) {
				Log.i(getTAG(), "Close DB"); //$NON-NLS-1$
				mDbHelper.close();
			}
		} catch (Exception e) {
			Log.e(getTAG(), "error onDestroy of movie Activity", e); //$NON-NLS-1$
		}
	}

	/*
	 * METHODS to redefine
	 */

	protected abstract int getMenuKey();

	protected abstract int getLayout();

	protected abstract String getTrackerName();

	protected abstract String getTAG();

	protected abstract void initContentView();

	protected abstract M getModel();

	protected abstract void doOnCancel();

	protected abstract void doChangeFromPref();

	protected abstract int getDialogTitle();

	protected abstract int getDialogMsg();

	/*
	 * Default implementation of IFragmentCineShowTimeInteraction
	 */

	@Override
	public final M getModelActivity() {
		if (model == null) {
			model = getModel();
		}
		return model;
	}

	@Override
	public final GoogleAnalyticsTracker getTracker() {
		if (tracker == null) {
			tracker = GoogleAnalyticsTracker.getInstance();
			tracker.start(CineShowtimeCst.GOOGLE_ANALYTICS_ID, this);
			tracker.trackPageView(getTrackerName());
		}
		return tracker;
	}

	@Override
	public final SharedPreferences getPrefs() {
		if (prefs == null) {
			prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		}
		return prefs;
	}

	@Override
	public final CineShowtimeDbAdapter getMDbHelper() {
		if (mDbHelper == null) {
			openDB();
		}
		return mDbHelper;
	}

	/*
	 * Dialogs methods
	 */

	@Override
	public final void closeDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public final void openDialog() {
		progressDialog = ProgressDialog.show(this, //
				getResources().getString(getDialogTitle())//
				, getResources().getString(getDialogMsg()) //
				, true // indeterminate
				, true // cancelable
				, this // cancelListener
				);
	}

}
