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
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.comparator.CineShowtimeComparator;

public class CineShowTimeNonExpandableListAdapter extends BaseAdapter {

	private AbstractResultAdapter adapter;
	private int selectedPosition;

	private static final String TAG = "CineShowTimeExpandableListAdapter";

	public CineShowTimeNonExpandableListAdapter(Context context, OnClickListener listener) {
		adapter = new AbstractResultAdapter(context, listener);

	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectPosition) {
		this.selectedPosition = selectPosition;
	}

	public List<MovieBean> getMovieList() {
		return adapter.getMovieList();
	}

	public void changePreferences() {
		adapter.changePreferences();
	}

	public void setTheaterList(NearResp nearRespBean, Map<String, TheaterBean> theaterFavList, CineShowtimeComparator<?> comparator) {
		adapter.setTheaterList(nearRespBean, theaterFavList, comparator);
	}

	public void changeSort(CineShowtimeComparator<?> comparator) {
		adapter.changeSort(comparator);

	}

	public void refreshTheater(String theaterId) {
		adapter.refreshTheater(theaterId);
	}

	@Override
	public int getCount() {
		return adapter.getGroupCount();
	}

	@Override
	public Object getItem(int position) {
		return adapter.getGroup(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return adapter.getGroupView(position, false, convertView, parent);
	}

}
