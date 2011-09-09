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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.binomed.showtime.android.layout.view.TheaterFavView;
import com.binomed.showtime.android.model.TheaterBean;

public class TheaterFavMainListAdapter extends AbstractTheaterListAdapter {

	private OnClickListener listener;

	public TheaterFavMainListAdapter(Context context, List<TheaterBean> theaterList, OnClickListener clickListener) {
		super(context, theaterList, false);
		this.listener = clickListener;
	}

	@Override
	public View getView(int groupPosition, View convertView, ViewGroup parent) {

		TheaterFavView mainView = null;
		if (convertView == null) {
			mainView = new TheaterFavView(mainContext);
			mainView.setOnClickListener(listener);
		} else {
			mainView = (TheaterFavView) convertView;
		}

		TheaterBean theater = (TheaterBean) getItem(groupPosition);
		mainView.setTheater(theater);

		return mainView;
	}

}
