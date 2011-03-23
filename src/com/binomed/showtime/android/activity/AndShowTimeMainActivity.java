package com.binomed.showtime.android.activity;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.ListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.TheaterFavMainListAdapter;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.layout.dialogs.last.LastChangeDialog;
import com.binomed.showtime.android.service.AndShowCleanFileService;
import com.binomed.showtime.android.util.AndShowTimeLayoutUtils;
import com.binomed.showtime.android.util.AndShowTimeMenuUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.localisation.LocationUtils;
import com.binomed.showtime.android.util.localisation.LocationUtils.ProviderEnum;
import com.binomed.showtime.beans.TheaterBean;

public class AndShowTimeMainActivity extends Activity {

	private static final String TAG = "AndShowTimeMainActivity"; //$NON-NLS-1$

	private static final int MENU_PREF = Menu.FIRST;

	private Context mainContext;
	private ControlerMainActivity controler;
	protected ModelMainActivity model;
	private ListenerMainActivity listener;
	private boolean checkboxPreference, shineALightTheme;
	private ImageView logoImg;
	private Button buttonSearchNear;
	private ListView theaterFavList;
	protected TheaterFavMainListAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		AndShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
		setContentView(R.layout.activity_main);

		mainContext = this;

		AndShowtimeFactory.initGeocoder(this);

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
		this.model = controler.getModel();

		display();

		initResults();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		List<TheaterBean> favList = controler.getFavTheater();

		if ((favList == null) || (favList.size() == 0)) {
			favList = new ArrayList<TheaterBean>();
			TheaterBean thTmp = new TheaterBean();
			thTmp.setId("0");
			thTmp.setTheaterName(getResources().getString(R.string.msgNoDFav));

			favList.add(thTmp);
		}

		model.setFavList(favList);

		adapter = new TheaterFavMainListAdapter(mainContext, favList, listener);

		this.theaterFavList.setAdapter(adapter);
	}

	private void initResults() {
		Intent intentResult = new Intent();
		intentResult.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, model.isResetTheme());
		intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, model.isNullResult());
		setResult(AndShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY, intentResult);
	}

	/**
	 * Init views objects
	 */
	private void initViews() {

		logoImg = (ImageView) findViewById(R.id.logoImg);
		// logoImg.setImageResource(R.drawable.logo);

		// Watch for button clicks.
		buttonSearchNear = (Button) findViewById(R.id.mainBtnSearchNear);
		theaterFavList = (ListView) findViewById(R.id.mainFavList);
	}

	/**
	 * Init listener
	 */

	private void initListeners() {
		buttonSearchNear.setOnClickListener(listener);
		theaterFavList.setOnItemClickListener(listener);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			model.setNullResult(data.getBooleanExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, false));
			model.setResetTheme(data.getBooleanExtra(ParamIntent.PREFERENCE_RESULT_THEME, false));
		} else {
			model.setResetTheme(false);
			model.setNullResult(false);
		}

		initResults();

		if (model.isResetTheme()) {
			AndShowTimeLayoutUtils.changeToTheme(this, getIntent());
		}
	}

}