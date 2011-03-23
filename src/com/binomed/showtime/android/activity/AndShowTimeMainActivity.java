package com.binomed.showtime.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.dialogs.last.LastChangeDialog;
import com.binomed.showtime.android.service.AndShowCleanFileService;
import com.binomed.showtime.android.util.AndShowTimeMenuUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.localisation.LocationUtils;
import com.binomed.showtime.android.util.localisation.LocationUtils.ProviderEnum;

public class AndShowTimeMainActivity extends Activity {

	private static final String TAG = "AndShowTimeMainActivity"; //$NON-NLS-1$

	protected static final Integer ACTIVITY_NEAR = 0;
	protected static final Integer ACTIVITY_MOVIE = 1;
	private static final int MENU_PREF = Menu.FIRST;
	private static final int MENU_ABOUT = Menu.FIRST + 1;
	private static final int MENU_HELP = Menu.FIRST + 2;

	private static final Integer REQUEST_PREF = 1;

	private Context mainContext;
	private ControlerMainActivity controler;
	private ListenerMainActivity listener;
	private boolean checkboxPreference;
	private ImageView logoImg;
	private Button buttonSearchNear;
	private Button buttonSearchMovie;
	private Button buttonTheatersFav;

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
				ProviderEnum provider = LocationUtils.getProvider(prefs, this);
				switch (provider) {
				case GPS_PROVIDER:
				case GSM_PROVIDER:
					// LocationUtils.checkProviderLocation(AndShowTimeMainActivity.this, provider);
					break;
				default:
					break;
				}

				Intent intentCleanFileService = new Intent(AndShowTimeMainActivity.this, AndShowCleanFileService.class);
				startService(intentCleanFileService);

				BeanManagerFactory.setFirstOpen();
			}
		}

		controler = ControlerMainActivity.getInstance();
		listener = new ListenerMainActivity(controler, this);

		initViews();
		initListeners();

		controler.registerView(this);

		display();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy"); //$NON-NLS-1$
		controler.closeDB();
	}

	/*
	 * 
	 * Init Views
	 */

	/**
	 * Init views objects
	 */
	private void initViews() {

		logoImg = (ImageView) findViewById(R.id.logoImg);
		logoImg.setImageResource(R.drawable.logo);

		// Watch for button clicks.
		buttonSearchNear = (Button) findViewById(R.id.mainBtnSearchNear);
		buttonSearchMovie = (Button) findViewById(R.id.mainBtnSearchMovie);
		buttonTheatersFav = (Button) findViewById(R.id.mainBtnTheaterFav);
	}

	/**
	 * Init listener
	 */
	private void initListeners() {
		buttonSearchNear.setOnClickListener(listener);
		buttonSearchMovie.setOnClickListener(listener);
		buttonTheatersFav.setOnClickListener(listener);

	}

	private void display() {
		if (this.controler.showLastChange()) {
			LastChangeDialog dialog = new LastChangeDialog(this);
			dialog.show();
		}

	}

	/*
	 * 
	 * 
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		AndShowTimeMenuUtil.createMenu(menu, MENU_PREF, 0);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (AndShowTimeMenuUtil.onMenuItemSelect(this, MENU_PREF, item.getItemId())) {
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

}