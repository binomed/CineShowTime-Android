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
package com.binomed.showtime.android.util.activity;

import greendroid.app.ActionBarActivity;
import greendroid.app.GDApplication;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.util.Config;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBar.OnActionBarListener;
import greendroid.widget.ActionBarHost;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.layout.view.AboutView;
import com.binomed.showtime.android.screen.pref.CineShowTimePreferencesActivity;
import com.binomed.showtime.android.util.CineShowTimeLayoutUtils;
import com.binomed.showtime.android.util.MyQuickAction;
import com.cyrilmottier.android.greendroid.R;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public abstract class AbstractCineShowTimeActivity<M extends ICineShowTimeActivityHelperModel> //
		extends FragmentActivity //
		implements //
		OnCancelListener, //
		IFragmentCineShowTimeInteraction<M>, //
		ActionBarActivity {

	private GoogleAnalyticsTracker tracker;
	private SharedPreferences prefs;
	private M model;
	private ProgressDialog progressDialog;
	private CineShowtimeDbAdapter mDbHelper;
	private ActionBarHost mActionBarHost;
	private QuickActionWidget mBar;
	protected final int MENU_PREF = getMenuKey();
	private long startActivityTime = System.currentTimeMillis();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Debug.startMethodTracing("CineShowtime");
		startActivityTime = System.currentTimeMillis();
		getTracker();
		CineShowTimeLayoutUtils.onActivityCreateSetTheme(this, getPrefs());
		tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_ACTIVITY // Category
				, CineShowtimeCst.ANALYTICS_ACTION_OPEN // Action
				, getTrackerName() // Label
				, 0 // Value
		);

		// Init vital informations
		openDB();
		getModelActivity();

		// try to restore informations before
		onPreRestoreBundle(savedInstanceState);
		// We call the contentView
		setContentView(R.layout.cst_action_bar_activity);
		Drawable d = getResources().getDrawable(R.drawable.background_dark);
		String defaultTheme = getResources().getString(R.string.preference_gen_default_theme);
		String theme = getPrefs().getString(getResources().getString(R.string.preference_gen_key_theme), defaultTheme);
		if (!theme.equals(defaultTheme)) {
			d = getResources().getDrawable(R.drawable.background_light);
		}
		d.setDither(true);
		mActionBarHost = (ActionBarHost) findViewById(R.id.gd_action_bar_host);
		mActionBarHost.setBackgroundDrawable(d);
		mActionBarHost.getContentView().removeAllViews();
		LayoutInflater.from(this).inflate(getLayout(), mActionBarHost.getContentView());

		// We load the content
		initContentView();

		// We add actions bar items
		addActionBarItems(getActionBar());
		addActionBarItem(getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(new ActionBarDrawable(this, R.drawable.ic_menu_moreoverflow_normal_holo_light)), R.id.action_bar_menu);
		prepareQuickActionBar();

		// We manage the results for other activity
		initResults();

		// try to restore information after
		onPostRestoreBundle(savedInstanceState);
	}

	/**
	 * Call in the saveInstanceState is to manage. in first
	 * 
	 * @param savedInstanceState
	 */
	protected void onPreRestoreBundle(Bundle savedInstanceState) {
		// Nothing to do, just a method to override
	}

	/**
	 * Call in the saveInstanceState is to manage. at last
	 * 
	 * @param savedInstanceState
	 */
	protected void onPostRestoreBundle(Bundle savedInstanceState) {
		// Nothing to do, just a method to override
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeDB();
		getTracker().dispatch();
		getTracker().stop();
		Debug.stopMethodTracing();
	}

	@Override
	protected void onPause() {
		super.onPause();
		long currentTime = System.currentTimeMillis();

		tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_ACTIVITY // Category
				, CineShowtimeCst.ANALYTICS_ACTION_TIME // Action
				, getTrackerName() // Label
				, Long.valueOf((currentTime - startActivityTime) / 1000).intValue() // Value
		);
		if ((progressDialog != null) && progressDialog.isShowing()) {
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_ACTIVITY // Category
					, CineShowtimeCst.ANALYTICS_ACTION_CANCEL_SERVICE // Action
					, getTrackerName() // Label
					, 0 // Value
			);
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
			// Analytics part
			String defaultTheme = getResources().getString(R.string.preference_gen_default_theme);
			String theme = getPrefs().getString(getResources().getString(R.string.preference_gen_key_theme), defaultTheme);
			if (theme.equals(defaultTheme)) {
				tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_ACTIVITY // Category
						, CineShowtimeCst.ANALYTICS_ACTION_CHANGE_THEME // Action
						, getTrackerName() // Label
						, CineShowtimeCst.ANALYTICS_VALUE_THEME_BLACK // Value
				);
			} else {
				tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_ACTIVITY // Category
						, CineShowtimeCst.ANALYTICS_ACTION_CHANGE_THEME // Action
						, getTrackerName() // Label
						, CineShowtimeCst.ANALYTICS_VALUE_THEME_LIGHT // Value
				);
			}

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

	protected abstract void addActionBarItems(ActionBar actionBar);

	protected abstract boolean delegateOnActionBarItemClick(ActionBarItem item, int position);

	protected abstract boolean isHomeActivity();

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

	/*
	 * ActionBarCode
	 */

	@Override
	public void onContentChanged() {
		super.onContentChanged();

		onPreContentChanged();
		onPostContentChanged();
	}

	private void prepareQuickActionBar() {
		mBar = new QuickActionBar(this);
		mBar.addQuickAction(new MyQuickAction(this, android.R.drawable.ic_menu_preferences, R.string.menuPreferences, true));
		mBar.addQuickAction(new MyQuickAction(this, android.R.drawable.ic_menu_info_details, R.string.menuAbout, true));
		mBar.addQuickAction(new MyQuickAction(this, android.R.drawable.ic_menu_help, R.string.menuHelp, true));

		mBar.setOnQuickActionClickListener(mActionListener);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		if (position == MENU_PREF) {
			mBar.show(item.getItemView());
			return true;
		} else {
			return delegateOnActionBarItemClick(item, position);
		}
	}

	@Override
	public FrameLayout getContentView() {
		return mActionBarHost.getContentView();
	}

	@Override
	public ActionBar getActionBar() {
		return mActionBarHost.getActionBar();
	}

	@Override
	public GDApplication getGDApplication() {
		return (GDApplication) getApplication();
	}

	@Override
	public ActionBarItem addActionBarItem(ActionBarItem item) {
		return getActionBar().addItem(item);
	}

	@Override
	public ActionBarItem addActionBarItem(ActionBarItem item, int itemId) {
		return getActionBar().addItem(item, itemId);
	}

	@Override
	public ActionBarItem addActionBarItem(ActionBarItem.Type actionBarItemType) {
		return getActionBar().addItem(actionBarItemType);
	}

	@Override
	public ActionBarItem addActionBarItem(ActionBarItem.Type actionBarItemType, int itemId) {
		return getActionBar().addItem(actionBarItemType, itemId);
	}

	@Override
	public int createLayout() {
		return getLayout();
	}

	@Override
	public void onPreContentChanged() {
		mActionBarHost = (ActionBarHost) findViewById(R.id.gd_action_bar_host);
		if (mActionBarHost == null) {
			throw new RuntimeException("Your content must have an ActionBarHost whose id attribute is R.id.gd_action_bar_host");
		}
		mActionBarHost.getActionBar().setOnActionBarListener(mActionBarListener);

	}

	@Override
	public void onPostContentChanged() {
		boolean titleSet = false;

		final Intent intent = getIntent();
		if (intent != null) {
			String title = intent.getStringExtra(ActionBarActivity.GD_ACTION_BAR_TITLE);
			if (title != null) {
				titleSet = true;
				setTitle(title);
			}
		}

		if (!titleSet) {
			// No title has been set via the Intent. Let's look in the
			// ActivityInfo
			try {
				final ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), 0);
				if (activityInfo.labelRes != 0) {
					setTitle(activityInfo.labelRes);
				}
			} catch (NameNotFoundException e) {
				// Do nothing
			}
		}

		final int visibility = intent.getIntExtra(ActionBarActivity.GD_ACTION_BAR_VISIBILITY, View.VISIBLE);
		getActionBar().setVisibility(visibility);

	}

	private OnActionBarListener mActionBarListener = new OnActionBarListener() {
		@Override
		public void onActionBarItemClicked(int position) {
			if ((position == OnActionBarListener.HOME_ITEM) && !isHomeActivity()) {

				tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_ACTIVITY // Category
						, CineShowtimeCst.ANALYTICS_ACTION_RETURN_HOME // Action
						, getTrackerName() // Label
						, 0 // Value
				);

				final GDApplication app = getGDApplication();
				final Class<?> klass = app.getHomeActivityClass();
				if ((klass != null) && !klass.equals(AbstractCineShowTimeActivity.this.getClass())) {
					if (Config.GD_INFO_LOGS_ENABLED) {
						Log.i(getTAG(), "Going back to the home activity");
					}
					Intent homeIntent = new Intent(AbstractCineShowTimeActivity.this, klass);
					if (Integer.valueOf(Build.VERSION.SDK) <= 10) {
						homeIntent.putExtra(ParamIntent.ACTIVITY_LARGE_SCREEN, TestSizeOther.checkLargeScreen(getResources().getConfiguration().screenLayout));

					} else {
						homeIntent.putExtra(ParamIntent.ACTIVITY_LARGE_SCREEN, TestSizeHoneyComb.checkLargeScreen(getResources().getConfiguration().screenLayout));

					}
					homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(homeIntent);
				}

			} else {
				if (!onHandleActionBarItemClick(getActionBar().getItem(position), position)) {
					if (Config.GD_WARNING_LOGS_ENABLED) {
						Log.w(getTAG(), "Click on item at position " + position + " dropped down to the floor");
					}
				}
			}
		}
	};

	private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
		@Override
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
			final int MENU_PREF = 0;
			final int MENU_ABOUT = 1;
			final int MENU_HELP = 2;
			if (position == MENU_PREF) {
				tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_ACTIVITY // Category
						, CineShowtimeCst.ANALYTICS_ACTION_OPEN // Action
						, "Preferences" // Label
						, 0 // Value
				);
				Intent launchPreferencesIntent = new Intent().setClass(getApplicationContext(), CineShowTimePreferencesActivity.class);

				// Make it a subactivity so we know when it returns
				startActivityForResult(launchPreferencesIntent, CineShowtimeCst.ACTIVITY_RESULT_PREFERENCES);
			} else if (position == MENU_ABOUT) {
				tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_ACTIVITY // Category
						, CineShowtimeCst.ANALYTICS_ACTION_OPEN // Action
						, "About" // Label
						, 0 // Value
				);
				AlertDialog.Builder aboutDialog = new AlertDialog.Builder(AbstractCineShowTimeActivity.this);
				try {
					PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
					aboutDialog.setTitle(Html.fromHtml(new StringBuilder() //
							.append("CineShowTime ").append(pi.versionName).append("<br>") // //$NON-NLS-1$ //$NON-NLS-2$
							.toString()));
				} catch (Exception e) {
				}
				aboutDialog.setCancelable(false);
				aboutDialog.setIcon(R.drawable.icon);
				aboutDialog.setNeutralButton(R.string.btnClose, null);

				AboutView aboutView = new AboutView(AbstractCineShowTimeActivity.this);
				aboutDialog.setView(aboutView);

				// aboutDialog.create();
				aboutDialog.show();
			} else if (position == MENU_HELP) {
				tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_ACTIVITY // Category
						, CineShowtimeCst.ANALYTICS_ACTION_OPEN // Action
						, "Help" // Label
						, 0 // Value
				);
				Intent launchPreferencesIntent = IntentShowtime.createHelpAndShowTime(getApplicationContext());

				// Make it a subactivity so we know when it returns
				startActivityForResult(launchPreferencesIntent, 0);
			}
		}
	};

}
