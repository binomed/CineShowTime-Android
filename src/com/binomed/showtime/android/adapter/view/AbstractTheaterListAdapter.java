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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.model.TheaterBean;

abstract class AbstractTheaterListAdapter extends BaseAdapter {

	protected List<TheaterBean> theaterList;
	protected Context mainContext;
	private boolean hasMoreTheater;

	public AbstractTheaterListAdapter(Context context, List<TheaterBean> theaterList, boolean hasMoreTheater) {
		super();
		mainContext = context;
		this.theaterList = theaterList;
		this.hasMoreTheater = hasMoreTheater;
	}

	@Override
	public int getCount() {
		int result = (theaterList != null) ? theaterList.size() : 0;
		if (hasMoreTheater) {
			result++;
		}
		return result;
	}

	@Override
	public Object getItem(int position) {
		TheaterBean theater = null;
		if ((theaterList != null) && (theaterList.size() > position)) {
			theater = theaterList.get(position);
		}
		return theater;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int groupPosition, View convertView, ViewGroup parent) {

		TextView theaterView = null;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) mainContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			theaterView = (TextView) inflator.inflate(R.layout.view_text_group_item, null);
		} else {
			theaterView = (TextView) convertView;
		}

		TheaterBean theater = (TheaterBean) getItem(groupPosition);

		if (hasMoreTheater && (theater == null)) {
			theaterView.setText(mainContext.getResources().getString(R.string.itemMoreTheaters));
		} else if (theater != null) {
			theaterView.setText(theater.getTheaterName());
		}

		return theaterView;
	}

}
