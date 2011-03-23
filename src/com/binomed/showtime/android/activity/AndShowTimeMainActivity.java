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

import com.binomed.showtime.R;
import com.binomed.showtime.android.searchmovieactivity.AndShowTimeSearchMovieActivity;
import com.binomed.showtime.android.searchnearactivity.AndShowTimeSearchNearActivity;
import com.binomed.showtime.android.service.AndShowCleanFileService;
import com.binomed.showtime.android.util.AndShowTimeMenuUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.LocationUtils;

public class AndShowTimeMainActivity extends Activity {

	private static final Integer ACTIVITY_NEAR = 0;
	private static final Integer ACTIVITY_MOVIE = 1;
	private static final int MENU_PREF = Menu.FIRST;
	private static final int MENU_ABOUT = Menu.FIRST + 1;
	private static final int MENU_HELP = Menu.FIRST + 2;

	private static final Integer REQUEST_PREF = 1;

	private Context mainContext;
	private boolean checkboxPreference;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.and_showtime);

		mainContext = this;

		AndShowtimeFactory.initGeocoder(this);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		checkboxPreference = prefs.getBoolean(getResources().getString(R.string.preference_loc_key_enable_localisation), true);

		if (checkboxPreference) {

			if (!BeanManagerFactory.isFirstOpen()) {
				LocationUtils.checkProviderLocation(AndShowTimeMainActivity.this, LocationUtils.getProvider(prefs, this));

				Intent intentCleanFileService = new Intent(AndShowTimeMainActivity.this, AndShowCleanFileService.class);
				startService(intentCleanFileService);

				BeanManagerFactory.setFirstOpen();
			}
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
		AndShowTimeMenuUtil.createMenu(menu, MENU_PREF);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (AndShowTimeMenuUtil.onMenuItemSelect(this, MENU_PREF, item.getItemId())) {
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

}