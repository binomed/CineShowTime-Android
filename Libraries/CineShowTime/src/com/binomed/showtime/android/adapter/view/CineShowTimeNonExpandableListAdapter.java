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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ObjectMasterView;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.comparator.CineShowtimeComparator;

public class CineShowTimeNonExpandableListAdapter extends BaseAdapter {

	private NearResp nearRespBean;
	private Map<String, TheaterBean> theatherFavList;
	private List<TheaterBean> theatherList;
	private Context mainContext;
	private boolean kmUnit;
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

	private void setNearRespList(NearResp nearRespBean, Map<String, TheaterBean> theaterFavList, CineShowtimeComparator<?> comparator) {
		this.selectedPosition = -1;
		this.nearRespBean = nearRespBean;
		this.theatherFavList = theaterFavList;
		if (this.nearRespBean != null) {
			this.theatherList = this.nearRespBean.getTheaterList();
		}
		if (comparator != null) {
			Collections.sort(theatherList, (Comparator<TheaterBean>) comparator);
		}

	}

	@Override
	public int getCount() {
		int result = (theatherList != null) ? theatherList.size() : 0;
		if ((nearRespBean != null) && nearRespBean.isHasMoreResults()) {
			result++;
		}
		return result;
	}

	@Override
	public Object getItem(int position) {
		Object result = null;
		if ((theatherList != null) && (position < theatherList.size())) {
			result = theatherList.get(position);
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

		TheaterBean theater = (TheaterBean) getItem(position);
		if ((nearRespBean != null) && nearRespBean.isHasMoreResults() && (theater == null)) {
			objectMasterView.setTheater(null, false, false, blackTheme);
		} else if ((theater != null) && (theater.getTheaterName() != null)) {
			objectMasterView.setTheater(theater, (theatherFavList != null) && theatherFavList.containsKey(theater.getId()), false, blackTheme);
		}
		if (position == selectedPosition) {
			objectMasterView.setBackgroundColor(R.color.select_color);
		} else {
			objectMasterView.setBackgroundDrawable(null);

		}

		return objectMasterView;
	}

}
