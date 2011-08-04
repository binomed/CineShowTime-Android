package com.binomed.showtime.android.screen.movie;

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import android.content.Intent;
import android.view.Menu;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.screen.movie.CineShowTimeMovieFragment.MovieFragmentInteraction;
import com.binomed.showtime.android.util.activity.AbstractCineShowTimeActivity;

public class CineShowTimeMovieActivity extends AbstractCineShowTimeActivity<IModelMovie> //
		implements MovieFragmentInteraction<IModelMovie> //
{

	private static final String TAG = "MovieActivity"; //$NON-NLS-1$
	private static final String TRACKER_NAME = "/MovieActivity"; //$NON-NLS-1$

	private static final int ITEM_TRANSLATE = Menu.NONE + 2;
	private static final int MENU_PREF = Menu.NONE;

	private CineShowTimeMovieFragment fragment;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void finish() {
		boolean resetTheme = getIntent() != null ? getIntent().getBooleanExtra(ParamIntent.PREFERENCE_RESULT_THEME, false) : false;
		if (resetTheme) {
			setResult(CineShowtimeCst.RESULT_PREF_WITH_NEW_THEME);
		}
		super.finish();
	}

	/*
	 * Override methods
	 */

	@Override
	protected void initContentView() {
		fragment = (CineShowTimeMovieFragment) getSupportFragmentManager().findFragmentById(R.id.FragmentMovie);

	}

	@Override
	protected int getLayout() {
		return R.layout.activity_movie;
	}

	@Override
	protected int getMenuKey() {
		return MENU_PREF;
	}

	@Override
	protected String getTrackerName() {
		return TRACKER_NAME;
	}

	@Override
	protected String getTAG() {
		return TAG;
	}

	@Override
	protected ModelMovieActivity getModel() {
		return new ModelMovieActivity();
	}

	@Override
	protected void doOnCancel() {
		fragment.onCancel();

	}

	@Override
	protected void doChangeFromPref() {
		fragment.changePreferences();

	}

	@Override
	protected int getDialogTitle() {
		return R.string.movieProgressTitle;
	}

	@Override
	protected int getDialogMsg() {
		return R.string.movieProgressMsg;
	}

	@Override
	protected boolean delegateOnActionBarItemClick(ActionBarItem item, int position) {
		// nothing to do
		return false;
	}

	@Override
	protected void addActionBarItems(ActionBar actionBar) {
		// nothing to do

	}

	@Override
	protected boolean isHomeActivity() {
		return false;
	}

	/*
	 * Fragment Interaction
	 */

	@Override
	public Intent getIntentMovie() {
		return getIntent();
	}

}
