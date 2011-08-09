package com.binomed.showtime.android.screen.widget.search;

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;

import java.util.Calendar;

import android.content.Intent;
import android.view.Menu;
import android.widget.LinearLayout;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.fav.CineShowTimeFavFragment;
import com.binomed.showtime.android.screen.fav.CineShowTimeFavFragment.FavFragmentInteraction;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment.CineShowTimeResultInteraction;
import com.binomed.showtime.android.screen.search.CineShowTimeSearchFragment;
import com.binomed.showtime.android.screen.search.CineShowTimeSearchFragment.SearchFragmentInteraction;
import com.binomed.showtime.android.screen.widget.results.CineShowTimeResultsWidgetActivity;
import com.binomed.showtime.android.util.activity.AbstractCineShowTimeActivity;
import com.binomed.showtime.android.widget.CineShowTimeWidgetHelper;

public class CineShowTimeWidgetConfigureActivity extends AbstractCineShowTimeActivity<ModelCineShowTimeWidget> implements //
		SearchFragmentInteraction<ModelCineShowTimeWidget> //
		, FavFragmentInteraction<ModelCineShowTimeWidget> //
		, CineShowTimeResultInteraction<ModelCineShowTimeWidget> {

	private static final String TAG = "SearchWidgetActivity"; //$NON-NLS-1$
	private static final int MENU = Menu.FIRST;

	private CineShowTimeSearchFragment searchFragment;
	private CineShowTimeFavFragment favFragment;
	private CineShowTimeResultsFragment resultFragment;
	private LinearLayout zoneResultWidget;

	/*
	 * Overrides
	 */

	@Override
	protected int getMenuKey() {
		return MENU;
	}

	@Override
	protected int getLayout() {
		return R.layout.activity_widget;
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
	protected void initContentView() {
		favFragment = (CineShowTimeFavFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentFav);
		searchFragment = (CineShowTimeSearchFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentSearch);
		zoneResultWidget = (LinearLayout) findViewById(R.id.zoneWidgetResults);

		searchFragment.hideMovieFields();

	}

	@Override
	protected ModelCineShowTimeWidget getModel() {
		return new ModelCineShowTimeWidget();
	}

	@Override
	protected void doOnCancel() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doChangeFromPref() {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getDialogTitle() {
		return R.string.searchNearProgressTitle;
	}

	@Override
	protected int getDialogMsg() {
		return R.string.searchNearProgressMsg;
	}

	@Override
	protected void addActionBarItems(ActionBar actionBar) {
		// nothing to do
	}

	@Override
	protected boolean delegateOnActionBarItemClick(ActionBarItem item, int position) {
		return false;
	}

	@Override
	protected boolean isHomeActivity() {
		return false;
	}

	/*
	 * Interaction
	 */

	@Override
	public int getRequestCode(int itemId) {
		return CineShowtimeCst.ACTIVITY_RESULT_CITY_SPEECH_SEARCH;
	}

	@Override
	public void fireSearch(int viewId, String text) {
		searchFragment.launchSearchWithVerif(viewId, text);
	}

	@Override
	public void setNullResult(boolean result) {
		getModelActivity().setNullResult(result);

	}

	@Override
	public boolean isNullResult() {
		return getModelActivity().isNullResult();
	}

	@Override
	public void setLastRequestDate(Calendar lastRequestDate) {
		getModelActivity().setLastRequestDate(lastRequestDate);

	}

	@Override
	public Calendar getLastRequestDate() {
		return getModelActivity().getLastRequestDate();
	}

	@Override
	public void delegateStartSearchResult(Intent intent, int requestCode) {
		if (zoneResultWidget == null) {
			intent.setClass(getApplicationContext(), CineShowTimeResultsWidgetActivity.class);
			startActivityForResult(intent, requestCode);
		} else {
			CineShowTimeResultsFragment resultFragment = new CineShowTimeResultsFragment();
			resultFragment.setNonExpendable(true);
			resultFragment.setIntentResult(intent);
			getSupportFragmentManager().beginTransaction().replace(R.id.zoneWidgetResults, resultFragment).commit();
		}

	}

	@Override
	public boolean onFavClick(TheaterBean theater) {
		CineShowTimeWidgetHelper.finalizeWidget(this, theater, getModelActivity().getCityName());
		return true;
	}

	@Override
	public void openMovieScreen(MovieBean movie, TheaterBean theater) {
		// nothing to do

	}

	@Override
	public void onGroupClick() {
		// nothing to do

	}

	@Override
	public void onTheaterClick(TheaterBean theater) {
		CineShowTimeWidgetHelper.finalizeWidget(this, theater, getModelActivity().getCityName());
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