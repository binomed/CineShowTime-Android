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
package com.binomed.showtime.android.screen.results.tablet;

import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.CineShowTimeShowTimesListAdapter;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.movie.CineShowTimeMovieActivity;
import com.binomed.showtime.android.screen.movie.CineShowTimeMovieFragment;
import com.binomed.showtime.android.screen.movie.CineShowTimeMovieFragment.MovieFragmentInteraction;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment.CineShowTimeResultInteraction;
import com.binomed.showtime.android.util.activity.AbstractCineShowTimeActivity;

public class CineShowTimeResultsTabletActivity extends AbstractCineShowTimeActivity<IModelResultTablet> implements // OnChildClickListener //
		CineShowTimeResultInteraction<IModelResultTablet> //
		, MovieFragmentInteraction<IModelResultTablet> //
		, OnItemClickListener //
{

	protected static final int MENU_SORT = Menu.NONE;
	protected static final int MENU_PREF = Menu.NONE + 1;

	protected static final int ID_SORT = 1;

	private static final String TAG = "ResultsActivity"; //$NON-NLS-1$
	private static final String TRACKER_NAME = "/ResultActivity"; //$NON-NLS-1$

	protected CineShowTimeResultsFragment fragmentResult;
	protected CineShowTimeMovieFragment fragmentMovie;
	protected CineShowTimeFrameFragment fragmentFrame;
	private ListView showtimeList;
	private CineShowTimeShowTimesListAdapter adapter;

	protected Intent intentStartMovieActivity;
	protected boolean openMovie = false;

	private List<MovieBean> movieList = null;
	private TheaterBean theater = null;

	// Var for portrait mode
	protected boolean portraitMode;
	protected LinearLayout infoLayout = null;
	protected final Handler mHandler = new Handler();

	protected Intent intentResult;

	/*
	 * Override LyfeCycle
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if ((getModelActivity().getNearResp() != null) || (fragmentMovie != null)) {
			outState.putBoolean(ParamIntent.BUNDLE_SAVE, true);
			// Save results state
			if (getModelActivity().getNearResp() != null) {
				outState.putParcelable(ParamIntent.NEAR_RESP, getModelActivity().getNearResp());
			} else {
				outState.putBoolean(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, getModelActivity().isForceResearch());
			}
			if (getModelActivity().getLocalisation() != null) {
				outState.putDouble(ParamIntent.ACTIVITY_SEARCH_LATITUDE, getModelActivity().getLocalisation().getLatitude());
				outState.putDouble(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, getModelActivity().getLocalisation().getLongitude());
			}
			outState.putString(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, getModelActivity().getFavTheaterId());
			outState.putInt(ParamIntent.ACTIVITY_SEARCH_DAY, getModelActivity().getDay());
			outState.putString(ParamIntent.ACTIVITY_SEARCH_CITY, getModelActivity().getCityName());
			outState.putString(ParamIntent.ACTIVITY_SEARCH_MOVIE_NAME, getModelActivity().getMovieName());
			outState.putIntegerArrayList(ParamIntent.ACTIVITY_SEARCH_GROUP_EXPAND, new ArrayList<Integer>(getModelActivity().getGroupExpanded()));

			// Save movie state
			if ((fragmentMovie != null) && (getModelActivity().getMovie() != null)) {
				outState.putParcelable(ParamIntent.MOVIE, getModelActivity().getMovie());
				outState.putParcelable(ParamIntent.THEATER, getModelActivity().getTheater());
				outState.putDouble(ParamIntent.ACTIVITY_MOVIE_LATITUDE, (getModelActivity().getLocalisation() != null) ? getModelActivity().getLocalisation().getLatitude() : -1);
				outState.putDouble(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, (getModelActivity().getLocalisation() != null) ? getModelActivity().getLocalisation().getLongitude() : -1);
			}
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPreRestoreBundle(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			boolean saved = savedInstanceState.getBoolean(ParamIntent.BUNDLE_SAVE, false);
			if (saved) {
				// Restore results
				getModelActivity().setNearResp((NearResp) savedInstanceState.getParcelable(ParamIntent.NEAR_RESP));
				intentResult = new Intent();
				getModelActivity().setForceResearch(savedInstanceState.getBoolean(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, false));
				intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, getModelActivity().isForceResearch());
				getModelActivity().setCityName(savedInstanceState.getString(ParamIntent.ACTIVITY_SEARCH_CITY));
				intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, getModelActivity().getCityName());
				getModelActivity().setMovieName(savedInstanceState.getString(ParamIntent.ACTIVITY_SEARCH_MOVIE_NAME));
				intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_MOVIE_NAME, getModelActivity().getMovieName());
				intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, savedInstanceState.getString(ParamIntent.ACTIVITY_SEARCH_THEATER_ID));
				getModelActivity().setDay(savedInstanceState.getInt(ParamIntent.ACTIVITY_SEARCH_DAY, 0));
				intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_DAY, getModelActivity().getDay());
				ArrayList<Integer> expandGroup = savedInstanceState.getIntegerArrayList(ParamIntent.ACTIVITY_SEARCH_GROUP_EXPAND);
				getModelActivity().setGroupExpanded(new HashSet<Integer>(expandGroup));
				intentResult.putIntegerArrayListExtra(ParamIntent.ACTIVITY_SEARCH_GROUP_EXPAND, expandGroup);
				if ((expandGroup != null) && (expandGroup.size() > 0)) {
					movieList = new ArrayList<MovieBean>();
					theater = getModelActivity().getNearResp().getTheaterList().get(expandGroup.get(expandGroup.size() - 1));
					for (String movieId : theater.getMovieMap().keySet()) {
						movieList.add(getModelActivity().getNearResp().getMapMovies().get(movieId));
					}
				}
				Double latitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_SEARCH_LATITUDE, 0);
				Double longitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, 0);
				if ((latitude != 0) && (longitude != 0)) {
					intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, latitude);
					intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, longitude);
				}

				// Restore movie

				MovieBean movie = savedInstanceState.getParcelable(ParamIntent.MOVIE);
				if (movie != null) {
					TheaterBean theater = savedInstanceState.getParcelable(ParamIntent.THEATER);
					latitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_MOVIE_LATITUDE, -1);
					longitude = savedInstanceState.getDouble(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, -1);
					intentStartMovieActivity = new Intent(this, CineShowTimeMovieActivity.class);
					intentStartMovieActivity.putExtra(ParamIntent.MOVIE_ID, movie.getId());
					intentStartMovieActivity.putExtra(ParamIntent.MOVIE, movie);
					intentStartMovieActivity.putExtra(ParamIntent.THEATER_ID, theater.getId());
					intentStartMovieActivity.putExtra(ParamIntent.THEATER, theater);
					intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LATITUDE, latitude != -1 ? latitude : null);
					intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, longitude != -1 ? longitude : null);
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
					Fragment fragmentRecycle = getSupportFragmentManager().findFragmentById(R.id.fragmentInfo);
					if ((fragmentRecycle != null) && (fragmentRecycle.getClass() == CineShowTimeMovieFragment.class)) {
						fragmentMovie = (CineShowTimeMovieFragment) fragmentRecycle;
					} else {
						fragmentMovie = new CineShowTimeMovieFragment();
					}
					if ((fragmentRecycle != null) && !fragmentMovie.equals(fragmentRecycle)) {
						getSupportFragmentManager().beginTransaction().replace(R.id.fragmentInfo, fragmentMovie).commit();
					}
				}
			}

		}
	}

	/*
	 * OverRide methods
	 */

	@Override
	protected int getMenuKey() {
		return MENU_PREF;
	}

	@Override
	protected int getLayout() {
		return R.layout.activity_result;
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
	protected void onPostResume() {
		Log.i(TAG, "onPostResume");
		super.onPostResume();
	}

	@Override
	protected void initContentView() {
		Log.i(getTAG(), "initContentView");
		fragmentResult = (CineShowTimeResultsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentResults);
		Fragment recycleFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentInfo);
		if (recycleFragment == null) {
			fragmentFrame = new CineShowTimeFrameFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.fragmentInfo, fragmentFrame).commit();
		} else if (recycleFragment.getClass() == CineShowTimeFrameFragment.class) {
			fragmentFrame = (CineShowTimeFrameFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentInfo);
		}

		// We check if we're in portrait mode in order to manage specific expand
		Configuration conf = getResources().getConfiguration();
		portraitMode = conf.orientation == Configuration.ORIENTATION_PORTRAIT;
		if (portraitMode) {
			infoLayout = (LinearLayout) findViewById(R.id.fragmentInfo);

			fragmentResult.setNonExpendable(true);
			showtimeList = (ListView) findViewById(R.id.showtimesResults);
			showtimeList.setOnItemClickListener(this);
			adapter = new CineShowTimeShowTimesListAdapter(this);
			if ((movieList != null) && (movieList.size() > 0) && (theater != null)) {
				adapter.setShowTimesList(movieList, theater);
				getModelActivity().getGroupExpanded().add(getModelActivity().getNearResp().getTheaterList().indexOf(theater));
			}
			showtimeList.setAdapter(adapter);
		}

	}

	@Override
	protected IModelResultTablet getModel() {
		return new ModelResultTablet();
	}

	@Override
	protected void doOnCancel() {
		fragmentResult.onCancel();
	}

	@Override
	protected void doChangeFromPref() {
		fragmentResult.changePreferences();

	}

	@Override
	protected int getDialogTitle() {
		return openMovie ? R.string.searchMovieProgressTitle : R.string.searchNearProgressTitle;
	}

	@Override
	protected int getDialogMsg() {
		return openMovie ? R.string.searchMovieProgressMsg : R.string.searchNearProgressMsg;
	}

	@Override
	protected boolean delegateOnActionBarItemClick(ActionBarItem item, int position) {
		switch (position) {
		case MENU_SORT: {
			fragmentResult.openSortDialog();
			return true;
		}
		}
		return true;
	}

	@Override
	protected void addActionBarItems(ActionBar actionBar) {
		addActionBarItem(getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(new ActionBarDrawable(this, android.R.drawable.ic_menu_sort_by_size)), R.id.menuSort);

	}

	@Override
	protected boolean isHomeActivity() {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (adapter.isMovieView()) {
			openMovieScreen(adapter.getMasterMovie(), (TheaterBean) adapter.getItem(arg2));
		} else {
			openMovieScreen((MovieBean) adapter.getItem(arg2), adapter.getMasterTheater());

		}
	}

	/*
	 * 
	 * Fragment Result interaction
	 */

	@Override
	public void openMovieScreen(MovieBean movie, TheaterBean theater) {
		if (movie != null) {
			openMovie = true;
			intentStartMovieActivity = new Intent(this, CineShowTimeMovieActivity.class);

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
			fragmentMovie = new CineShowTimeMovieFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.fragmentInfo, fragmentMovie).commit();
		}

	}

	/*
	 * Fragment Movie interaction
	 */

	@Override
	public Intent getIntentMovie() {
		return intentStartMovieActivity;
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

	@Override
	public void onTheaterClick(TheaterBean theater) {
		if (portraitMode) {

			List<MovieBean> movieList = new ArrayList<MovieBean>();
			if (theater != null && theater.getMovieMap() != null) {
				for (String movieId : theater.getMovieMap().keySet()) {
					movieList.add(getModelActivity().getNearResp().getMapMovies().get(movieId));
				}
			}
			adapter.setShowTimesList(movieList, theater);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onMovieClick(MovieBean movie) {
		if (portraitMode) {
			List<TheaterBean> theaterList = new ArrayList<TheaterBean>();
			if (movie != null) {
				for (String theaterId : movie.getTheaterList()) {
					thLbl: for (TheaterBean theater : getModelActivity().getNearResp().getTheaterList()) {
						if ((theaterId != null) && theaterId.equals(theater.getId())) {
							theaterList.add(theater);
							break thLbl;
						}
					}
				}
			}
			adapter.setShowTimesList(theaterList, movie);
			adapter.notifyDataSetChanged();
		}

	}

}