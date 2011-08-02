package com.binomed.showtime.android.layout.view;

import java.net.URLDecoder;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.ProjectionListAdapter;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.movie.IModelMovie;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class PageProjectionView extends LinearLayout implements View.OnClickListener {

	private static final String TAG = "CineShowTime-PageProjectionView";

	private TextView theaterTitle, theaterAddress;
	private ListView movieProjectionTimeList;
	private ImageButton movieBtnMap, movieBtnDirection, movieBtnCall;
	protected ProjectionListAdapter projectionAdapter;

	private IModelMovie model;
	private GoogleAnalyticsTracker tracker;

	private boolean distanceTime;

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
		projectionAdapter.changePreferences();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_projection_button:
			tracker.trackEvent("Action", "Click", "Use popup button", 0);
			ImageButton imageBtn = (ImageButton) v;
			ProjectionView parentView = (ProjectionView) imageBtn.getParent().getParent();

			ListPopupWindow popupWindow = new ListPopupWindow(v, getContext(), model.getTheater(), model.getMovie(), parentView.getProjectionBean(), model.isCalendarInstalled());
			// popupWindow.showLikeQuickAction(0, -30);
			// popupWindow.showLikePopDownMenu(0, -100);
			popupWindow.loadView();
			// Log.i("ListenerMovieActivity", "Rect : " + popupWindow.getSize() + ", list : " + popupWindow.getSizeList());
			popupWindow.showLikePopDownMenu(0, -(popupWindow.getOptions().size() * 40));
			break;
		case R.id.movieBtnMap:
			tracker.trackEvent("Action", "Click", "Use map button", 0);
			tracker.dispatch();
			if (model.isMapInstalled()) {
				getContext().startActivity(IntentShowtime.createMapsIntent(model.getTheater()));
			} else {
				getContext().startActivity(IntentShowtime.createMapsIntentBrowser(model.getTheater()));

			}
			break;
		case R.id.movieBtnDirection:
			tracker.trackEvent("Action", "Click", "Use map navigation button", 0);
			tracker.dispatch();
			Intent intentDirection = IntentShowtime.createMapsWithDrivingDirectionIntent(model.getTheater(), model.getGpsLocation());
			if (intentDirection != null) {
				getContext().startActivity(intentDirection);
			}
			break;
		case R.id.movieBtnCall:
			tracker.trackEvent("Action", "Click", "Use call button", 0);
			tracker.dispatch();
			getContext().startActivity(IntentShowtime.createCallIntent(model.getTheater()));
			break;
		default:
			break;
		}

	}

}
