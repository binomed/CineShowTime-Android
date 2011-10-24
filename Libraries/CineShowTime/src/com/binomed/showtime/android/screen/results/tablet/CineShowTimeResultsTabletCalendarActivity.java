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

import java.util.ArrayList;
import java.util.List;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.CineShowTimeShowTimesListAdapter;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment;

public class CineShowTimeResultsTabletCalendarActivity extends CineShowTimeResultsTabletActivity {

	private ListView showtimeList;
	private CineShowTimeShowTimesListAdapter adapter;

	/*
	 * OverRide methods
	 */

	@Override
	protected int getLayout() {
		return R.layout.activity_result_calendar;
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
			hideRight = false;
			frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
			infoLayout = (LinearLayout) findViewById(R.id.fragmentInfo);
			frameLayout.setVisibility(View.INVISIBLE);

			fragmentResult.setNonExpendable(true);
			showtimeList = (ListView) findViewById(R.id.showtimesResults);
			adapter = new CineShowTimeShowTimesListAdapter(this);
			showtimeList.setAdapter(adapter);
		}

	}

	@Override
	public void onTheaterClick(TheaterBean theater) {
		// nothing to do
		List<MovieBean> movieList = new ArrayList<MovieBean>();
		for (String movieId : theater.getMovieMap().keySet()) {
			movieList.add(getModel().getNearResp().getMapMovies().get(movieId));
		}
		adapter.setShowTimesList(movieList, theater);
		adapter.notifyDataSetChanged();

	}

}