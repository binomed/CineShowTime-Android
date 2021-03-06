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
package com.binomed.showtime.android.adapter.view;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ObjectSubViewNew;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;

public class CineShowTimeShowTimesListAdapter extends BaseAdapter {

	private List<MovieBean> movieList;
	private List<TheaterBean> theaterList;
	private TheaterBean theater;
	private MovieBean movie;
	private Context mainContext;
	private boolean kmUnit;
	private boolean distanceTime;
	private boolean blackTheme;
	private boolean format24;
	private boolean movieView;

	private static final String TAG = "CineShowTimeExpandableListAdapter";

	public CineShowTimeShowTimesListAdapter(Context context) {
		// super();
		mainContext = context;
		changePreferences();

	}

	public TheaterBean getMasterTheater() {
		return theater;
	}

	public MovieBean getMasterMovie() {
		return movie;
	}

	public boolean isMovieView() {
		return movieView;
	}

	public void changePreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainContext);
		String measure = prefs.getString(mainContext.getResources().getString(R.string.preference_loc_key_measure)//
				, mainContext.getResources().getString(R.string.preference_loc_default_measure));
		String defaultTheme = prefs.getString(mainContext.getResources().getString(R.string.preference_gen_key_theme)//
				, mainContext.getResources().getString(R.string.preference_gen_default_theme));
		distanceTime = prefs.getBoolean(mainContext.getResources().getString(R.string.preference_loc_key_time_direction)//
				, false);
		kmUnit = mainContext.getResources().getString(R.string.preference_loc_default_measure).equals(measure);
		blackTheme = mainContext.getResources().getString(R.string.preference_gen_default_theme).equals(defaultTheme);
		format24 = CineShowtimeDateNumberUtil.isFormat24(mainContext);
	}

	public void setShowTimesList(List<MovieBean> movieList, TheaterBean theater) {
		this.movieView = false;
		this.movieList = movieList;
		this.theater = theater;
	}

	public void setShowTimesList(List<TheaterBean> theaterList, MovieBean movie) {
		this.movieView = true;
		this.theaterList = theaterList;
		this.movie = movie;
	}

	@Override
	public int getCount() {
		int result = movieView ? ((theaterList != null) ? theaterList.size() : 0) : ((movieList != null) ? movieList.size() : 0);
		return result;
	}

	@Override
	public Object getItem(int position) {
		Object result = null;
		if (movieView) {
			if ((theaterList != null) && (position < theaterList.size())) {
				result = theaterList.get(position);
			}
		} else {
			if ((movieList != null) && (position < movieList.size())) {
				result = movieList.get(position);
			}

		}
		return result;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ObjectSubViewNew showTimeView = null;
		if (convertView == null) {
			showTimeView = new ObjectSubViewNew(mainContext, kmUnit);
		} else {
			showTimeView = (ObjectSubViewNew) convertView;
		}

		MovieBean movieBean = null;
		TheaterBean theaterBean = null;

		if (!movieView) {
			movieBean = (MovieBean) getItem(position);
			theaterBean = this.theater;
		} else {
			theaterBean = (TheaterBean) getItem(position);
			movieBean = this.movie;

		}

		showTimeView.setMovie(movieBean//
				, theaterBean//
				, distanceTime//
				, movieView // movieView
				, blackTheme//
				, format24//
				, false // LightFormat
				);
		return showTimeView;
	}

}
