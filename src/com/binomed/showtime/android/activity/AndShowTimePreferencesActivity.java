package com.binomed.showtime.android.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.binomed.showtime.R;

public class AndShowTimePreferencesActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.and_showtime_preferences);
	}

}