package com.binomed.showtime.android.screen.movie;

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
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

	private Intent intentMovie;

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
	 * Override LyfeCycle
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Save movie state
		if ((fragment != null) && (getModelActivity().getMovie() != null)) {
			outState.putBoolean(ParamIntent.BUNDLE_SAVE, true);
			outState.putParcelable(ParamIntent.MOVIE, getModelActivity().getMovie());
			outState.putParcelable(ParamIntent.THEATER, getModelActivity().getTheater());
			outState.putDouble(ParamIntent.ACTIVITY_MOVIE_LATITUDE, (getModelActivity().getGpsLocation() != null) ? getModelActivity().getGpsLocation().getLatitude() : -1);
			outState.putDouble(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, (getModelActivity().getGpsLocation() != null) ? getModelActivity().getGpsLocation().getLongitude() : -1);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPreRestoreBundle(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			boolean saved = savedInstanceState.getBoolean(ParamIntent.BUNDLE_SAVE, false);
			if (saved) {
				// Restore movie

				MovieBean movie = savedInstanceState.getParcelable(ParamIntent.MOVIE);
				if (movie != null) {
					TheaterBean theater = savedInstanceState.getParcelable(ParamIntent.THEATER);
					Double latitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_MOVIE_LATITUDE, -1);
					Double longitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, -1);
					intentMovie = new Intent(this, CineShowTimeMovieActivity.class);
					intentMovie.putExtra(ParamIntent.MOVIE_ID, movie.getId());
					intentMovie.putExtra(ParamIntent.MOVIE, movie);
					intentMovie.putExtra(ParamIntent.THEATER_ID, theater.getId());
					intentMovie.putExtra(ParamIntent.THEATER, theater);
					intentMovie.putExtra(ParamIntent.ACTIVITY_MOVIE_LATITUDE, latitude != -1 ? latitude : null);
					intentMovie.putExtra(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, longitude != -1 ? longitude : null);
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
					intentMovie.putExtra(ParamIntent.ACTIVITY_MOVIE_NEAR, place.toString());
				}
			}

		}
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
		if (intentMovie != null) {
			return intentMovie;
		} else {
			return getIntent();
		}
	}

}
