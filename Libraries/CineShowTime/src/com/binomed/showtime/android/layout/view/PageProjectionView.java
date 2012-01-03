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

import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.ProjectionListAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.OptionEnum;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.movie.IModelMovie;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.MyQuickAction;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class PageProjectionView extends LinearLayout implements View.OnClickListener, OnQuickActionClickListener {

	private static final String TAG = "CineShowTime-PageProjectionView";

	private TextView theaterTitle, theaterAddress;
	private ListView movieProjectionTimeList;
	private ImageButton movieBtnMap, movieBtnDirection, movieBtnCall;
	protected ProjectionListAdapter projectionAdapter;
	private QuickActionWidget mBarProjections;

	private IModelMovie model;
	private GoogleAnalyticsTracker tracker;

	private boolean distanceTime;

	private HashMap<Integer, OptionEnum> mapQuickAction = new HashMap<Integer, OptionEnum>();
	private ProjectionBean currentProjectionBean = null;

	public PageProjectionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PageProjectionView(Context context) {
		super(context);
		init();
	}

	public void changeData(IModelMovie model, GoogleAnalyticsTracker tracker) {
		this.model = model;
		this.tracker = tracker;
		manageViewVisibility();
	}

	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.tab_movie_showtimes, this);

		initViews();
		initListeners();
	}

	private void initViews() {
		theaterTitle = (TextView) findViewById(R.id.movieTheaterTitle);
		theaterAddress = (TextView) findViewById(R.id.movieTheaterAddress);
		movieProjectionTimeList = (ListView) findViewById(R.id.movieListProjection);
		movieBtnMap = (ImageButton) findViewById(R.id.movieBtnMap);
		movieBtnDirection = (ImageButton) findViewById(R.id.movieBtnDirection);
		movieBtnCall = (ImageButton) findViewById(R.id.movieBtnCall);

	}

	private void initListeners() {
		movieBtnMap.setOnClickListener(this);
		movieBtnDirection.setOnClickListener(this);
		movieBtnCall.setOnClickListener(this);

	}

	/**
	 * @param movie
	 * @throws Exception
	 */
	public void fillViews(MovieBean movie) throws Exception {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

		TheaterBean theater = model.getTheater();
		if (theater != null) {
			theaterTitle.setText(theater.getTheaterName());

			LocalisationBean place = theater.getPlace();
			if (place != null) {
				try {
					theaterAddress.setText(URLDecoder.decode(place.getSearchQuery(), CineShowTimeEncodingUtil.getEncoding()));
				} catch (Exception e) {
					Log.e(TAG, "error decoding address", e);
				}
			}
		}
		List<ProjectionBean> projectionList = null;
		distanceTime = prefs.getBoolean(this.getResources().getString(R.string.preference_loc_key_time_direction)//
				, false);
		Long distanceTimeLong = null;
		if (theater != null) {
			projectionList = theater.getMovieMap().get(movie.getId());
			if (distanceTime && (theater.getPlace() != null)) {
				distanceTimeLong = theater.getPlace().getDistanceTime();
			}
		}

		projectionAdapter = new ProjectionListAdapter(getContext()//
				, movie //
				, projectionList //
				, CineShowtimeDateNumberUtil.getMinTime(projectionList, distanceTimeLong) //
				, this//
		);
		movieProjectionTimeList.setAdapter(projectionAdapter);

		prepareQuickActionBar();
	}

	private void prepareQuickActionBar() {
		mBarProjections = new QuickActionBar(getContext());
		mBarProjections.setOnQuickActionClickListener(this);
	}

	public void manageViewVisibility() {
		if ((model.getTheater() == null) //
		) {
			movieBtnMap.setEnabled(false);
		}
		if ((model.getGpsLocation() == null) //
				|| !model.isMapInstalled()//
		) {
			movieBtnDirection.setEnabled(false);
		}
		if (!model.isDialerInstalled() //
				&& ((model.getTheater() == null) || (model.getTheater().getPhoneNumber() == null))) {
			movieBtnCall.setEnabled(false);
		}
	}

	public void changePreferences() {
		if (projectionAdapter != null) {
			projectionAdapter.changePreferences();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.item_projection_button) {
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_MOVIE // Category
					, CineShowtimeCst.ANALYTICS_ACTION_MOVIE_ACTIONS // Action
					, CineShowtimeCst.ANALYTICS_LABEL_MOVIE_INVOCKE_QUICKACTIONS // Label
					, 0 // Value
			);

			mBarProjections.clearAllQuickActions();

			ImageButton imageBtn = (ImageButton) v;
			ProjectionView parentView = (ProjectionView) imageBtn.getParent().getParent();

			int compt = 0;
			currentProjectionBean = parentView.getProjectionBean();
			mBarProjections.addQuickAction(new MyQuickAction(getContext(), OptionEnum.SMS.getRessourceDrawable(), OptionEnum.SMS.getRessourceText(), false));
			mapQuickAction.put(compt, OptionEnum.SMS);
			compt++;
			mBarProjections.addQuickAction(new MyQuickAction(getContext(), OptionEnum.MAIL.getRessourceDrawable(), OptionEnum.MAIL.getRessourceText(), false));
			mapQuickAction.put(compt, OptionEnum.MAIL);
			compt++;
			if ((Integer.valueOf(Build.VERSION.SDK) <= 8) && model.isCalendarInstalled()) {
				mBarProjections.addQuickAction(new MyQuickAction(getContext(), OptionEnum.AGENDA.getRessourceDrawable(), OptionEnum.AGENDA.getRessourceText(), false));
				mapQuickAction.put(compt, OptionEnum.AGENDA);
				compt++;
			}
			if ((currentProjectionBean.getReservationLink() != null) && (currentProjectionBean.getReservationLink().length() > 0)) {
				mBarProjections.addQuickAction(new MyQuickAction(getContext(), OptionEnum.RESERVATION.getRessourceDrawable(), OptionEnum.RESERVATION.getRessourceText(), false));
				mapQuickAction.put(compt, OptionEnum.RESERVATION);
			}

			mBarProjections.show(imageBtn);
		} else if (v.getId() == R.id.movieBtnMap) {
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_MOVIE // Category
					, CineShowtimeCst.ANALYTICS_ACTION_MOVIE_ACTIONS // Action
					, CineShowtimeCst.ANALYTICS_LABEL_MOVIE_ACTIONS_MAP // Label
					, 0 // Value
			);
			if (model.isMapInstalled()) {
				getContext().startActivity(IntentShowtime.createMapsIntent(model.getTheater()));
			} else {
				getContext().startActivity(IntentShowtime.createMapsIntentBrowser(model.getTheater()));

			}
		} else if (v.getId() == R.id.movieBtnDirection) {
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_MOVIE // Category
					, CineShowtimeCst.ANALYTICS_ACTION_MOVIE_ACTIONS // Action
					, CineShowtimeCst.ANALYTICS_LABEL_MOVIE_ACTIONS_DIRECTIONS // Label
					, 0 // Value
			);
			Intent intentDirection = IntentShowtime.createMapsWithDrivingDirectionIntent(model.getTheater(), model.getGpsLocation());
			if (intentDirection != null) {
				getContext().startActivity(intentDirection);
			}
		} else if (v.getId() == R.id.movieBtnCall) {
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_MOVIE // Category
					, CineShowtimeCst.ANALYTICS_ACTION_MOVIE_ACTIONS // Action
					, CineShowtimeCst.ANALYTICS_LABEL_MOVIE_ACTIONS_CALL // Label
					, 0 // Value
			);
			try {
				getContext().startActivity(IntentShowtime.createCallIntent(model.getTheater()));
			} catch (Exception e) {
				movieBtnCall.setEnabled(false);
			}

		}

	}

	@Override
	public void onQuickActionClicked(QuickActionWidget widget, int position) {
		Context ctx = getContext();
		MovieBean movie = model.getMovie();
		TheaterBean theater = model.getTheater();
		ProjectionBean projectionBean = currentProjectionBean;

		boolean format24 = CineShowtimeDateNumberUtil.isFormat24(getContext());

		switch (mapQuickAction.get(position)) {
		case SMS: {
			try {
				tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_MOVIE // Category
						, CineShowtimeCst.ANALYTICS_ACTION_MOVIE_ACTIONS // Action
						, CineShowtimeCst.ANALYTICS_LABEL_MOVIE_ACTIONS_SMS // Label
						, 0 // Value
				);

				Object[] testArgs = { new Long(3), "MyDisk" };

				MessageFormat form = new MessageFormat("The disk \"{1}\" contains {0} file(s).");

				String rest = form.format(testArgs);

				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				String msg = MessageFormat.format(ctx.getResources().getString(R.string.smsContent) // //$NON-NLS-1$
						, movie.getMovieName() //
						, CineShowtimeDateNumberUtil.getDayString(ctx, projectionBean.getShowtime()) //
						, CineShowtimeDateNumberUtil.showMovieTime(ctx, projectionBean.getShowtime(), format24) //
						, theater.getTheaterName());
				sendIntent.putExtra("sms_body", msg);
				sendIntent.setType("vnd.android-dir/mms-sms"); //$NON-NLS-1$
				ctx.startActivity(Intent.createChooser(sendIntent, ctx.getResources().getString(R.string.chooseIntentSms)));

			} catch (Exception e) {
				Log.e(TAG, "error while translating", e); //$NON-NLS-1$
			}
			break;
		}
		case MAIL: {
			try {
				tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_MOVIE // Category
						, CineShowtimeCst.ANALYTICS_ACTION_MOVIE_ACTIONS // Action
						, CineShowtimeCst.ANALYTICS_LABEL_MOVIE_ACTIONS_MAIL // Label
						, 0 // Value
				);

				// Create a new Intent to send messages
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setType("text/html"); //$NON-NLS-1$
				// sendIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
				String subject = MessageFormat.format(ctx.getResources().getString(R.string.mailSubject), movie.getMovieName());
				String msg = MessageFormat.format(ctx.getResources().getString(R.string.mailContent) //
						, movie.getMovieName() //
						, CineShowtimeDateNumberUtil.getDayString(ctx, projectionBean.getShowtime()) //
						, CineShowtimeDateNumberUtil.showMovieTime(ctx, projectionBean.getShowtime(), format24) //
						, theater.getTheaterName());
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
				sendIntent.putExtra(Intent.EXTRA_TEXT,//
						msg);
				ctx.startActivity(Intent.createChooser(sendIntent, ctx.getResources().getString(R.string.chooseIntentMail)));
			} catch (Exception e) {
				Log.e(TAG, "error while translating", e); //$NON-NLS-1$
			}
			break;
		}
		case AGENDA: {
			try {
				tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_MOVIE // Category
						, CineShowtimeCst.ANALYTICS_ACTION_MOVIE_ACTIONS // Action
						, CineShowtimeCst.ANALYTICS_LABEL_MOVIE_ACTIONS_CALENDAR // Label
						, 0 // Value
				);

				// Before or equel Donuts
				Uri uri = null;
				if (Integer.valueOf(Build.VERSION.SDK) <= 7) {
					uri = Uri.parse("content://calendar/events");
				} else if (Integer.valueOf(Build.VERSION.SDK) <= 8) {
					uri = Uri.parse("content://com.android.calendar/events");

				}

				if (uri != null) {
					ContentResolver cr = ctx.getContentResolver();

					Calendar timeAfter = Calendar.getInstance();
					timeAfter.setTimeInMillis(projectionBean.getShowtime());
					Calendar timeMovie = Calendar.getInstance();
					timeMovie.setTimeInMillis(movie.getMovieTime());
					timeAfter.add(Calendar.HOUR_OF_DAY, timeMovie.get(Calendar.HOUR_OF_DAY));
					timeAfter.add(Calendar.MINUTE, timeMovie.get(Calendar.MINUTE));
					ContentValues values = new ContentValues();
					values.put("eventTimezone", TimeZone.getDefault().getID());
					values.put("calendar_id", 1); // query content://calendar/calendars for more
					values.put("title", movie.getMovieName());
					values.put("allDay", 0);
					values.put("dtstart", projectionBean.getShowtime()); // long (start date in ms)
					values.put("dtend", timeAfter.getTimeInMillis()); // long (end date in ms)
					values.put("description", movie.getMovieName() + " at " + theater.getTheaterName());
					values.put("eventLocation", (theater.getPlace() != null) ? theater.getPlace().getSearchQuery() : null);
					values.put("transparency", 0);
					values.put("visibility", 0);
					values.put("hasAlarm", 0);

					cr.insert(uri, values);

					Toast.makeText(ctx, R.string.msgEventAdd, Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				Log.e(TAG, "error while translating", e); //$NON-NLS-1$
			}
			break;
		}
		case RESERVATION: {
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_MOVIE // Category
					, CineShowtimeCst.ANALYTICS_ACTION_MOVIE_ACTIONS // Action
					, CineShowtimeCst.ANALYTICS_LABEL_MOVIE_ACTIONS_RESERVATION // Label
					, 0 // Value
			);
			Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(projectionBean.getReservationLink()));
			ctx.startActivity(myIntent);
			break;
		}
		default:
			break;
		}

	}

}
