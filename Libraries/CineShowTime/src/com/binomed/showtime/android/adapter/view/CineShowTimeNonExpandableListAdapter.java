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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ObjectMasterView;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.android.util.comparator.CineShowtimeComparator;

public class CineShowTimeNonExpandableListAdapter extends BaseAdapter {

	private NearResp nearRespBean;
	private Map<String, TheaterBean> theatherFavList;
	private Map<String, TheaterBean> theatherMap;
	private HashMap<String, List<Entry<String, List<ProjectionBean>>>> projectionsThMap;
	private List<TheaterBean> theatherList;
	private List<MovieBean> movieList;
	private Context mainContext;
	private boolean kmUnit;
	private boolean movieView;
	private boolean distanceTime;
	private boolean blackTheme;
	private OnClickListener onClickListener;
	private int selectedPosition;

	private static final String TAG = "CineShowTimeExpandableListAdapter";

	public CineShowTimeNonExpandableListAdapter(Context context, OnClickListener listener) {
		// super();
		mainContext = context;
		this.onClickListener = listener;
		changePreferences();

	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	public List<MovieBean> getMovieList() {
		return movieList;
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
	}

	public void setTheaterList(NearResp nearRespBean, Map<String, TheaterBean> theaterFavList, CineShowtimeComparator<?> comparator) {
		this.setNearRespList(nearRespBean, theaterFavList, comparator);
	}

	public void changeSort(CineShowtimeComparator<?> comparator) {
		this.movieView = comparator.getType() == comparator.COMPARATOR_MOVIE_NAME;
		switch (comparator.getType()) {
		case CineShowtimeComparator.COMPARATOR_MOVIE_ID: {
			break;
		}
		case CineShowtimeComparator.COMPARATOR_MOVIE_NAME: {
			if (movieList != null) {
				Collections.sort(movieList, (Comparator<MovieBean>) comparator);
			}
		}
			break;
		case CineShowtimeComparator.COMPARATOR_THEATER_NAME:
		case CineShowtimeComparator.COMPARATOR_THEATER_DISTANCE:
		case CineShowtimeComparator.COMPARATOR_THEATER_SHOWTIME: {
			if (theatherList != null) {
				Collections.sort(theatherList, (Comparator<TheaterBean>) comparator);
			}
		}
			break;
		default:
			break;
		}
	}

	private void setNearRespList(NearResp nearRespBean, Map<String, TheaterBean> theaterFavList, CineShowtimeComparator<?> comparator) {
		this.selectedPosition = -1;
		this.movieView = comparator != null ? comparator.getType() == comparator.COMPARATOR_MOVIE_NAME : false;
		this.nearRespBean = nearRespBean;
		this.theatherFavList = theaterFavList;
		this.theatherMap = new HashMap<String, TheaterBean>();
		if (this.nearRespBean != null) {
			this.theatherList = this.nearRespBean.getTheaterList();
			this.movieList = this.nearRespBean.getMapMovies() != null ? new ArrayList<MovieBean>(this.nearRespBean.getMapMovies().values()) : null;
		}
		if (comparator != null) {
			switch (comparator.getType()) {
			case CineShowtimeComparator.COMPARATOR_MOVIE_ID: {
				break;
			}
			case CineShowtimeComparator.COMPARATOR_MOVIE_NAME: {
				if (this.movieList != null) {
					Collections.sort(movieList, (Comparator<MovieBean>) comparator);
				}
			}
				break;
			case CineShowtimeComparator.COMPARATOR_THEATER_SHOWTIME: {
				if (theatherList != null) {
					List<Entry<String, List<ProjectionBean>>> entries = null;
					List<TheaterBean> tempTheaterList = new ArrayList<TheaterBean>(theatherList);
					for (TheaterBean theater : tempTheaterList) {
						entries = projectionsThMap.get(theater.getId());

						if ((entries == null) && (theater.getMovieMap() != null)) {

							entries = new ArrayList<Entry<String, List<ProjectionBean>>>(theater.getMovieMap().entrySet());
							Collections.sort(entries, CineShowtimeFactory.getTheaterShowtimeInnerListComparator());
							projectionsThMap.put(theater.getId(), entries);

						}
					}
				}
			}
			case CineShowtimeComparator.COMPARATOR_THEATER_NAME:
			case CineShowtimeComparator.COMPARATOR_THEATER_DISTANCE: {
				if (theatherList != null) {
					Collections.sort(theatherList, (Comparator<TheaterBean>) comparator);
				}
			}
				break;
			default:
				break;
			}
		}
		if (theatherList != null) {
			// On doit initialiser avant tout cette map
			for (TheaterBean theater : theatherList) {
				theatherMap.put(theater.getId(), theater);
			}
		}

	}

	@Override
	public int getCount() {
		int result = !movieView ? ((theatherList != null) ? theatherList.size() : 0) : ((movieList != null) ? movieList.size() : 0);
		if ((nearRespBean != null) && nearRespBean.isHasMoreResults()) {
			result++;
		}
		return result;
	}

	@Override
	public Object getItem(int position) {
		Object result = null;
		if (!movieView) {
			if ((theatherList != null) && (position < theatherList.size())) {
				result = theatherList.get(position);
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
		ObjectMasterView objectMasterView = null;
		if (convertView == null) {
			objectMasterView = new ObjectMasterView(mainContext, onClickListener);
		} else {
			objectMasterView = (ObjectMasterView) convertView;
		}

		if (!movieView) {
			TheaterBean theater = (TheaterBean) getItem(position);
			if ((nearRespBean != null) && nearRespBean.isHasMoreResults() && (theater == null)) {
				objectMasterView.setTheater(null, false, false, blackTheme);
			} else if ((theater != null) && (theater.getTheaterName() != null)) {
				objectMasterView.setTheater(theater, (theatherFavList != null) && theatherFavList.containsKey(theater.getId()), false, blackTheme);
			}
		} else {
			MovieBean movie = (MovieBean) getItem(position);
			objectMasterView.setMovie(movie, false, false);
		}
		if (position == selectedPosition) {
			objectMasterView.setBackgroundColor(R.color.select_color);
		} else {
			objectMasterView.setBackgroundDrawable(null);

		}

		return objectMasterView;
	}

}
