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

import greendroid.widget.PagedAdapter;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.binomed.showtime.android.layout.view.PageInfoView;
import com.binomed.showtime.android.layout.view.PageInfoView.CallBack;
import com.binomed.showtime.android.layout.view.PageProjectionView;
import com.binomed.showtime.android.layout.view.PageReviewView;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.screen.movie.IModelMovie;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class MoviePagedAdapter extends PagedAdapter {

	private int NB_PAGE = 3;

	protected PageInfoView pageInfoView;
	protected PageProjectionView pageProjectionView;
	protected PageReviewView pageReviewView;

	@Override
	public int getCount() {
		return NB_PAGE;
	}

	private MovieBean movie;
	private Context context;
	private IModelMovie model;
	private GoogleAnalyticsTracker tracker;
	private CallBack callBack;

	public MoviePagedAdapter() {
		super();
	}

	public void changeData(MovieBean movie, Context context, IModelMovie model, GoogleAnalyticsTracker tracker, CallBack callBack) {
		this.movie = movie;
		this.context = context;
		this.model = model;
		this.tracker = tracker;
		this.callBack = callBack;
		try {
			if (pageInfoView != null) {
				pageInfoView.changeData(model, tracker, callBack);
				pageInfoView.fillViews(movie);
			}
			if (pageProjectionView != null) {
				pageProjectionView.changeData(model, tracker);
				pageProjectionView.fillViews(movie);
			}
			if (pageReviewView != null) {
				pageReviewView.changeData(model, tracker);
				pageReviewView.fillViews(movie);
			}
		} catch (Exception e) {
			Log.e("MoviePagedAdapter", "error during getView", e);
		}
	}

	@Override
	public Object getItem(int position) {
		return movie;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		try {
			switch (position) {
			case 0: {
				if (pageInfoView == null) {
					pageInfoView = new PageInfoView(context);
					pageInfoView.changeData(model, tracker, callBack);
					pageInfoView.fillViews(movie);
				}
				view = pageInfoView;
				break;
			}
			case 1: {
				if (pageProjectionView == null) {
					pageProjectionView = new PageProjectionView(context);
					pageProjectionView.changeData(model, tracker);
					pageProjectionView.fillViews(movie);
				}
				view = pageProjectionView;
				break;
			}
			case 2: {
				if (pageReviewView == null) {
					pageReviewView = new PageReviewView(context);
					pageReviewView.changeData(model, tracker);
					pageReviewView.fillViews(movie);
				}
				view = pageReviewView;
				break;
			}
			default:
				if (pageInfoView == null) {
					pageInfoView = new PageInfoView(context);
					pageInfoView.changeData(model, tracker, callBack);
					pageInfoView.fillViews(movie);
				}
				view = pageInfoView;
				break;
			}
		} catch (Exception e) {
			throw new RuntimeException("error during getView", e);
		}
		parent.removeView(view);
		return view;
	}

	public void manageViewVisibility() {
		if (pageProjectionView != null) {
			pageProjectionView.manageViewVisibility();
		}
	}

	public void fillBasicInformations(MovieBean movie) {
		if (pageInfoView != null) {
			pageInfoView.fillBasicInformations(movie);
		}
	}

	public void fillViews(MovieBean movie) throws Exception {
		this.movie = movie;
		if (pageInfoView != null) {
			pageInfoView.fillViews(movie);
		}
		if (pageProjectionView != null) {
			pageProjectionView.fillViews(movie);
		}
		if (pageReviewView != null) {
			pageReviewView.fillViews(movie);
		}
	}

	public void changePreferences() {
		if (pageProjectionView != null) {
			pageProjectionView.changePreferences();
		}
	}
}
