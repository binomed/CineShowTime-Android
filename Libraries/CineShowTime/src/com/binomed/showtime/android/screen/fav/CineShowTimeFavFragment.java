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
package com.binomed.showtime.android.screen.fav;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.adapter.view.TheaterFavMainListAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.layout.view.TheaterFavView;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsActivity;
import com.binomed.showtime.android.screen.results.tablet.CineShowTimeResultsTabletActivity;
import com.binomed.showtime.android.service.CineShowDBGlobalService;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.android.util.CineShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.android.util.activity.ICineShowTimeActivityHelperModel;
import com.binomed.showtime.android.util.activity.IFragmentCineShowTimeInteraction;
import com.binomed.showtime.android.util.activity.TestSizeHoneyComb;
import com.binomed.showtime.android.util.activity.TestSizeOther;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class CineShowTimeFavFragment extends Fragment implements OnClickListener //
		, OnItemClickListener //
{

	private static final String TAG = "CineShowTimeFavFragment"; //$NON-NLS-1$

	private Context mainContext;
	private ModelFavFragment model;
	private ListView theaterFavList;
	private TheaterFavMainListAdapter adapter;
	private CineShowtimeDbAdapter mDbHelper;

	private GoogleAnalyticsTracker tracker;
	private FavFragmentInteraction<? extends ICineShowTimeActivityHelperModel> fragmentInteraction;

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.fragment_fav, container, false);

		tracker = fragmentInteraction.getTracker();
		mainContext = getActivity();

		initViews(mainView);
		initListeners();

		this.model = new ModelFavFragment();

		return mainView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		fragmentInteraction = (FavFragmentInteraction<? extends ICineShowTimeActivityHelperModel>) activity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		closeDB();
	}

	/*
	 * 
	 * Init Views
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		List<TheaterBean> favList = getFavTheater();

		fragmentInteraction.hasFav(favList != null && favList.size() > 0);

		if ((favList == null) || (favList.size() == 0)) {
			favList = new ArrayList<TheaterBean>();
			TheaterBean thTmp = new TheaterBean();
			thTmp.setId("0");
			thTmp.setTheaterName(getResources().getString(R.string.msgNoDFav));

			favList.add(thTmp);
		}

		model.setFavList(favList);

		adapter = new TheaterFavMainListAdapter(mainContext, favList, this);

		this.theaterFavList.setAdapter(adapter);
	}

	/**
	 * Init views objects
	 * 
	 * @param mainView
	 */
	private void initViews(View mainView) {

		// Watch for button clicks.
		theaterFavList = (ListView) mainView.findViewById(R.id.mainFavList);
	}

	/**
	 * Init listener
	 */

	private void initListeners() {
		theaterFavList.setOnItemClickListener(this);
	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public void openResultsActivity(TheaterBean theaterBean) {
		openDB();

		try {
			String cityName = theaterBean.getPlace().getCityName();
			if (theaterBean.getPlace().getCountryNameCode() != null) {
				cityName += ", " + theaterBean.getPlace().getCountryNameCode();
			}
			String theaterId = theaterBean.getId();
			boolean forceRequest = false;

			Calendar today = Calendar.getInstance();
			Calendar calendarLastRequest = fragmentInteraction.getLastRequestDate();
			if (calendarLastRequest != null) {
				int yearToday = today.get(Calendar.YEAR);
				int monthToday = today.get(Calendar.MONTH);
				int dayToday = today.get(Calendar.DAY_OF_MONTH);
				int yearLast = calendarLastRequest.get(Calendar.YEAR);
				int monthLast = calendarLastRequest.get(Calendar.MONTH);
				int dayLast = calendarLastRequest.get(Calendar.DAY_OF_MONTH);
				if ((yearToday != yearLast) //
						|| (monthToday != monthLast) //
						|| (dayToday != dayLast) //
				) {//

					forceRequest = true;
				} else {
					Cursor cursorInResults = null;
					try {
						if (mDbHelper.isOpen()) {
							cursorInResults = mDbHelper.fetchInResults(theaterBean);
							forceRequest = !cursorInResults.moveToFirst();
						}
					} finally {
						if (cursorInResults != null) {
							cursorInResults.close();
						}
					}
				}
			} else {
				forceRequest = true;
			}

			fragmentInteraction.setLastRequestDate(today);

			CineShowtimeFactory.initGeocoder(mainContext);
			Class<?> clazz = CineShowTimeResultsActivity.class;
			boolean largeScreen = false;
			if (Integer.valueOf(Build.VERSION.SDK) <= 10) {
				largeScreen = TestSizeOther.checkLargeScreen(getResources().getConfiguration().screenLayout);

			} else {
				largeScreen = TestSizeHoneyComb.checkLargeScreen(getResources().getConfiguration().screenLayout);

			}

			if (largeScreen) {
				clazz = CineShowTimeResultsTabletActivity.class;
			}
			Intent intentResultActivity = new Intent(mainContext, clazz);

			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, CineShowTimeEncodingUtil.getEncoding()) : cityName));
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, theaterId);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_DAY, 0);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, forceRequest);
			startActivityForResult(intentResultActivity, CineShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY);

		} catch (Exception e) {
			Log.e(TAG, "Error during open results activity", e);
		} finally {
			closeDB();
		}

	}

	/*
	 * 
	 * EVENT Part
	 */

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.favItemDelete) {
			tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_FAV // Category
					, CineShowtimeCst.ANALYTICS_ACTION_INTERACTION // Action
					, CineShowtimeCst.ANALYTICS_LABEL_FAV_REMOVE // Label
					, 0 // Value
			);
			TheaterFavView thFavView = (TheaterFavView) v.getParent().getParent();
			TheaterBean thTmp = thFavView.getTheaterBean();
			Intent intentRemoveTh = new Intent(mainContext, CineShowDBGlobalService.class);
			intentRemoveTh.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_FAV_DELETE);
			intentRemoveTh.putExtra(ParamIntent.SERVICE_DB_DATA, thTmp);
			mainContext.startService(intentRemoveTh);
			model.getFavList().remove(thTmp);
			if (model.getFavList().size() == 0) {
				TheaterBean thEmtpy = new TheaterBean();
				thEmtpy.setId("0");
				thEmtpy.setTheaterName(getResources().getString(R.string.msgNoDFav));
				model.getFavList().add(thEmtpy);
			}
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_FAV // Category
				, CineShowtimeCst.ANALYTICS_ACTION_INTERACTION // Action
				, CineShowtimeCst.ANALYTICS_LABEL_FAV_REMOVE // Label
				, 0 // Value
		);
		// Sinon on ouvre la page résultats
		TheaterBean theater = model.getFavList().get(groupPosition);
		if (!fragmentInteraction.onFavClick(theater)) {
			openResultsActivity(theater);
		}
	}

	/*
	 * 
	 * DataBase
	 */

	public void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new CineShowtimeDbAdapter(mainContext);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			openDB();

		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		} finally {
			closeDB();
		}
	}

	/**
	 * @return
	 */
	public List<TheaterBean> getFavTheater() {
		openDB();
		List<TheaterBean> theaterList = null;
		try {
			theaterList = CineShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);
		} catch (Exception e) {
			Log.e(TAG, "Error during getting fav", e);
		} finally {
			closeDB();
		}

		return theaterList;
	}

	/**
	 * 
	 */
	public void closeDB() {
		try {
			if (mDbHelper.isOpen()) {
				Log.i(TAG, "Close DB"); //$NON-NLS-1$
				mDbHelper.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "error onDestroy of movie Activity", e); //$NON-NLS-1$
		}
	}

	/*
	 * 
	 * Interface of communication
	 */

	public interface FavFragmentInteraction<M extends ICineShowTimeActivityHelperModel> extends IFragmentCineShowTimeInteraction<M> {

		void setLastRequestDate(Calendar today);

		Calendar getLastRequestDate();

		boolean onFavClick(TheaterBean theater);

		void hasFav(boolean hasFav);

	}

}