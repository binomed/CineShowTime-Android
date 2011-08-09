package com.binomed.showtime.android.screen.results;

import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import android.content.Intent;
import android.view.Menu;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.movie.CineShowTimeMovieActivity;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment.CineShowTimeResultInteraction;
import com.binomed.showtime.android.util.activity.AbstractSimpleCineShowTimeActivity;

public class CineShowTimeResultsActivity extends AbstractSimpleCineShowTimeActivity<CineShowTimeResultsFragment, IModelResults> implements //
		CineShowTimeResultInteraction<IModelResults>//
{

	private static final int MENU_SORT = Menu.NONE;
	private static final int MENU_PREF = Menu.NONE + 1;

	protected static final int ID_SORT = 1;

	private static final String TAG = "ResultsActivity"; //$NON-NLS-1$
	private static final String TRACKER_NAME = "/ResultActivity"; //$NON-NLS-1$

	/*
	 * Override methods
	 */

	@Override
	protected CineShowTimeResultsFragment getFragment() {
		return new CineShowTimeResultsFragment();
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
	protected int getMenuKey() {
		return MENU_PREF;
	}

	@Override
	protected ModelResultsActivity getModel() {
		return new ModelResultsActivity();
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
		addActionBarItem(getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(new ActionBarDrawable(this, android.R.drawable.ic_menu_sort_by_size)), R.id.menuSort);
	}

	@Override
	protected boolean delegateOnActionBarItemClick(ActionBarItem item, int position) {
		switch (position) {
		case MENU_SORT: {
			fragment.openSortDialog();
			return true;
		}
		}
		return false;
	}

	@Override
	protected boolean isHomeActivity() {
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
		if (movie != null) {
			Intent intentStartMovieActivity = new Intent(this, CineShowTimeMovieActivity.class);

			intentStartMovieActivity.putExtra(ParamIntent.MOVIE_ID, movie.getId());
			intentStartMovieActivity.putExtra(ParamIntent.MOVIE, movie);
			intentStartMovieActivity.putExtra(ParamIntent.THEATER_ID, theater.getId());
			intentStartMovieActivity.putExtra(ParamIntent.THEATER, theater);
			intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LATITUDE, (getModelActivity().getLocalisation() != null) ? getModelActivity().getLocalisation().getLatitude() : null);
			intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, (getModelActivity().getLocalisation() != null) ? getModelActivity().getLocalisation().getLongitude() : null);
			StringBuilder place = new StringBuilder();
			if (theater != null) {
				if (theater.getPlace() != null) {
					if ((theater.getPlace().getCityName() != null //
							)
							&& (theater.getPlace().getCityName().length() > 0)) {
						place.append(theater.getPlace().getCityName());
					}
					if ((theater.getPlace().getPostalCityNumber() != null //
							)
							&& (theater.getPlace().getPostalCityNumber().length() > 0)) {
						place.append(" ").append(theater.getPlace().getPostalCityNumber());
					}
					if ((theater.getPlace().getCountryNameCode() != null //
							)
							&& (theater.getPlace().getCountryNameCode().length() > 0 //
							) && (place.length() > 0)) {
						place.append(", ").append(theater.getPlace().getCountryNameCode()); //$NON-NLS-1$
					}
					if (place.length() == 0) {
						place.append(theater.getPlace().getSearchQuery());
					}

				}
			}
			intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_NEAR, place.toString());
			startActivityForResult(intentStartMovieActivity, CineShowtimeCst.ACTIVITY_RESULT_MOVIE_ACTIVITY);
		}

	}

	@Override
	public void onTheaterClick(TheaterBean theaterBean) {
		// nothing to do
	}

	@Override
	public void onGroupClick() {
		// nothing to do
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