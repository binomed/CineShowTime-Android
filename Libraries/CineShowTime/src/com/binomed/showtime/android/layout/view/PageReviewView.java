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
package com.binomed.showtime.android.layout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.ReviewListAdapter;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.screen.movie.IModelMovie;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class PageReviewView extends LinearLayout {

	private static final String TAG = "CineShowTime-PageReviewView";

	private IModelMovie model;
	private GoogleAnalyticsTracker tracker;

	private ListView movieReviewsList;

	public PageReviewView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PageReviewView(Context context) {
		super(context);
		init();
	}

	public void changeData(IModelMovie model, GoogleAnalyticsTracker tracker) {
		this.model = model;
		this.tracker = tracker;
	}

	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.tab_movie_reviews, this);

		initViews();
	}

	private void initViews() {
		movieReviewsList = (ListView) findViewById(R.id.movieListReview);

	}

	/**
	 * @param movie
	 * @throws Exception
	 */
	public void fillViews(MovieBean movie) throws Exception {
		this.movieReviewsList.setAdapter(new ReviewListAdapter(getContext(), movie.getReviews()));
	}

}
