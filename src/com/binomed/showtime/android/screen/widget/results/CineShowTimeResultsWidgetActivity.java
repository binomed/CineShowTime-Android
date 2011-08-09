package com.binomed.showtime.android.screen.widget.results;

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import android.app.Activity;

import com.binomed.showtime.R;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment.CineShowTimeResultInteraction;
import com.binomed.showtime.android.util.activity.AbstractSimpleCineShowTimeActivity;
import com.binomed.showtime.android.widget.CineShowTimeWidgetHelper;

public class CineShowTimeResultsWidgetActivity extends AbstractSimpleCineShowTimeActivity<CineShowTimeResultsFragment, ModelResultsWidgetActivity> implements CineShowTimeResultInteraction<ModelResultsWidgetActivity> {

	public static final Integer ACTIVITY_OPEN_MOVIE = 0;

	private static final String TAG = "ResultsWidgetActivity"; //$NON-NLS-1$

	/*
	 * Override methods
	 */

	@Override
	protected CineShowTimeResultsFragment getFragment() {
		setResult(Activity.RESULT_CANCELED);
		CineShowTimeResultsFragment resultFragment = new CineShowTimeResultsFragment();
		resultFragment.setNonExpendable(true);
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
		CineShowTimeWidgetHelper.finalizeWidget(this, theaterBean, getModelActivity().getCityName());
	}

	@Override
	public void onChildClick() {
		// nothing to do
	}

	@Override
	public void onFocusListener(boolean focus) {
		// nothing to do
	}
}