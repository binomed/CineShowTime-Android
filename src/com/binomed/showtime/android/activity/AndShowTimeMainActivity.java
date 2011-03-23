package com.binomed.showtime.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.binomed.showtime.android.R;
import com.binomed.showtime.android.searchmovieactivity.AndShowTimeSearchMovieActivity;
import com.binomed.showtime.android.searchnearactivity.AndShowTimeSearchNearActivity;
import com.binomed.showtime.android.service.AndShowCleanFileService;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.LocationUtils;

public class AndShowTimeMainActivity extends Activity {

	private static final Integer ACTIVITY_NEAR = 0;
	private static final Integer ACTIVITY_MOVIE = 1;
	private static final int MENU_PREF = Menu.FIRST;

	private static final Integer REQUEST_PREF = 1;

	private Context mainContext;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.and_showtime);

		mainContext = this;

		AndShowtimeFactory.initGeocoder(this);

		if (!BeanManagerFactory.isFirstOpen()) {
			LocationUtils.checkGPSLocation(AndShowTimeMainActivity.this);

			Intent intentCleanFileService = new Intent(AndShowTimeMainActivity.this, AndShowCleanFileService.class);
			startService(intentCleanFileService);

			BeanManagerFactory.setFirstOpen();
		}

		ImageView logoImg = (ImageView) findViewById(R.id.logoImg);
		logoImg.setImageResource(R.drawable.logo);

		// Watch for button clicks.
		Button buttonSearchNear = (Button) findViewById(R.id.mainBtnSearchNear);
		buttonSearchNear.setOnClickListener(mStartListener);
		Button buttonSearchMovie = (Button) findViewById(R.id.mainBtnSearchMovie);
		buttonSearchMovie.setOnClickListener(mStartListener);
	}

	private OnClickListener mStartListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.mainBtnSearchNear: {
				Intent intentStartNearActivity = new Intent(mainContext, AndShowTimeSearchNearActivity.class);

				startActivityForResult(intentStartNearActivity, ACTIVITY_NEAR);
				break;
			}
			case R.id.mainBtnSearchMovie: {
				Intent intentStartMovieActivity = new Intent(mainContext, AndShowTimeSearchMovieActivity.class);

				startActivityForResult(intentStartMovieActivity, ACTIVITY_MOVIE);
				break;
			}
			default:
				break;
			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_PREF, 0, R.string.menuPreferences).setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PREF: {
			Intent launchPreferencesIntent = new Intent().setClass(this, AndShowTimePreferencesActivity.class);

			// Make it a subactivity so we know when it returns
			startActivityForResult(launchPreferencesIntent, REQUEST_PREF);
			return true;
		}
		}

		return super.onMenuItemSelected(featureId, item);
	}

	boolean checkboxPreference;

	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		checkboxPreference = prefs.getBoolean("checkbox_preference", false);
	}

}