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

	protected GoogleAnalyticsTracker tracker;
	protected SharedPreferences prefs;
	protected M model;
	protected ProgressDialog progressDialog;
	private String TAG = null;
	protected CineShowtimeDbAdapter mDbHelper;
	protected final int MENU_PREF = getMenuKey();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start(CineShowtimeCst.GOOGLE_ANALYTICS_ID, this);
		tracker.trackPageView(getTrackerName());
		TAG = getTAG();
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		CineShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
		Log.i(TAG, "onCreate"); //$NON-NLS-1$

		// We call the contentView
		openDB();
		model = getModel();
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
		Log.i(TAG, "onDestroy"); //$NON-NLS-1$
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			model.setNullResult(data.getBooleanExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, false));
			model.setResetTheme(data.getBooleanExtra(ParamIntent.PREFERENCE_RESULT_THEME, false));
		} else {
			model.setResetTheme(false);
			model.setNullResult(false);
		}

		initResults();

		if (requestCode == CineShowtimeCst.ACTIVITY_RESULT_PREFERENCES) {
			doChangeFromPref();
		}

		if (model.isResetTheme()) {
			CineShowTimeLayoutUtils.changeToTheme(this, getIntent());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		tracker.trackEvent("Menu", "Open", "Consult menu from main activity", 0);
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
		intentResult.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, model.isResetTheme());
		intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, model.isNullResult());
		setResult(CineShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY, intentResult);
	}

	/*
	 * DB Methods
	 */

	private void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new CineShowtimeDbAdapter(this);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	protected void closeDB() {
		try {
			if ((mDbHelper != null) && mDbHelper.isOpen()) {
				Log.i(TAG, "Close DB"); //$NON-NLS-1$
				mDbHelper.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "error onDestroy of movie Activity", e); //$NON-NLS-1$
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
		return model;
	}

	@Override
	public final GoogleAnalyticsTracker getTracker() {
		return tracker;
	}

	@Override
	public final SharedPreferences getPrefs() {
		return prefs;
	}

	@Override
	public final CineShowtimeDbAdapter getMDbHelper() {
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
