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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ReviewView;
import com.binomed.showtime.android.model.ReviewBean;

public class ReviewListAdapter extends BaseAdapter {

	private List<ReviewBean> reviewList;
	private Context mainContext;

	public ReviewListAdapter(Context context, List<ReviewBean> reviewList) {
		super();
		mainContext = context;
		this.reviewList = reviewList;

	}

	@Override
	public int getCount() {
		return (reviewList != null) ? (reviewList.size() != 0 ? reviewList.size() : 1) : 1;
	}

	@Override
	public Object getItem(int position) {
		ReviewBean review = null;
		if ((reviewList != null) && (reviewList.size() >= position)) {
			review = reviewList.get(position);
		}
		return review;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int groupPosition, View convertView, ViewGroup parent) {
		ReviewView reviewView = null;
		ReviewBean review = (ReviewBean) getItem(groupPosition);
		if (review != null) {
			if (convertView == null) {
				reviewView = new ReviewView(mainContext);
			} else {
				reviewView = (ReviewView) convertView;
			}

			reviewView.setReviewBean(review);
			return reviewView;
		} else {
			TextView noResultView = new TextView(mainContext);
			noResultView.setText(R.string.noReview);
			return noResultView;
		}
	}

}
