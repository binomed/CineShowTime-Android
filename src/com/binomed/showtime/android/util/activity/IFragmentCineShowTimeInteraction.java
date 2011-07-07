package com.binomed.showtime.android.util.activity;

import android.content.SharedPreferences;

import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public interface IFragmentCineShowTimeInteraction<M extends ICineShowTimeActivityHelperModel> {

	M getModelActivity();

	GoogleAnalyticsTracker getTracker();

	SharedPreferences getPrefs();

	CineShowtimeDbAdapter getMDbHelper();

}
