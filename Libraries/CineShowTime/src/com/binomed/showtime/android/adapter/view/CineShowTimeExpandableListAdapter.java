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

import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.comparator.CineShowtimeComparator;

public class CineShowTimeExpandableListAdapter extends BaseExpandableListAdapter {

	private AbstractResultAdapter adapter = null;

	private static final String TAG = "CineShowTimeExpandableListAdapter";

	public CineShowTimeExpandableListAdapter(Context context, OnClickListener listener) {
		adapter = new AbstractResultAdapter(context, listener);
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
	public Object getChild(int groupPosition, int childPosition) {
		return adapter.getChild(groupPosition, childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return adapter.getChildrenCount(groupPosition);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		return adapter.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
	}

	@Override
	public Object getGroup(int groupPosition) {
		return adapter.getGroup(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return adapter.getGroupCount();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		return adapter.getGroupView(groupPosition, isExpanded, convertView, parent);
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

}
