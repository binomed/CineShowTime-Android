package com.binomed.showtime.android.util.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.util.CineShowTimeLayoutUtils;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public abstract class AbstractCineShowTimeActivity<M extends ICineShowTimeActivityHelperModel> //
		extends FragmentActivity //
		implements //
		OnCancelListener //
{

	protected GoogleAnalyticsTracker tracker;
	protected SharedPreferences prefs;
	protected M model;
	protected ProgressDialog progressDialog;
	private String TAG = null;

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
		setContentView(getLayout());
		model = getModel();
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
		doOnDestroy();
		// closeDB();
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

	protected void openDialog(int dialogTitle, int dialogMessage) {
		progressDialog = ProgressDialog.show(this, //
				getResources().getString(dialogTitle)//
				, getResources().getString(dialogMessage) //
				, true // indeterminate
				, true // cancelable
				, this // cancelListener
				);
	}

	protected void dismissDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/*
	 * METHODS to redefine
	 */

	protected abstract int getLayout();

	protected abstract String getTrackerName();

	protected abstract String getTAG();

	protected abstract void initContentView();

	protected abstract M getModel();

	protected abstract void doOnDestroy();

	protected abstract void doOnCancel();

	protected abstract void doChangeFromPref();

}
