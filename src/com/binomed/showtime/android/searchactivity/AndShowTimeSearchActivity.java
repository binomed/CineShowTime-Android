package com.binomed.showtime.android.searchactivity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.util.AndShowTimeLayoutUtils;
import com.binomed.showtime.android.util.AndShowTimeMenuUtil;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.localisation.IListenerLocalisationUtilCallBack;

public class AndShowTimeSearchActivity extends Activity {

	private static final int MENU_PREF = Menu.FIRST;

	private static final String TAG = "SearchActivity"; //$NON-NLS-1$

	protected AutoCompleteTextView fieldCityName, fieldMovieName;
	protected Button searchButton;
	protected Spinner spinnerChooseDay;
	protected ImageView gpsImgView;

	private ControlerSearchActivity controler;
	private ListenerSearchActivity listener;
	private ModelSearchActivity model;

	protected IListenerLocalisationUtilCallBack localisationCallBack;

	private SharedPreferences prefs;

	protected EditText getFieldName() {
		return fieldCityName;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		AndShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
		Log.i(TAG, "onCreate"); //$NON-NLS-1$
		setContentView(R.layout.activity_search);

		controler = ControlerSearchActivity.getInstance();
		model = controler.getModelNearActivity();
		listener = new ListenerSearchActivity(this, controler, model);

		initViews();
		controler.registerView(this);

		display();

		initResults();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy"); //$NON-NLS-1$
		controler.closeDB();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (localisationCallBack != null) {
			localisationCallBack.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume"); //$NON-NLS-1$
		initListeners();
		initViewsState();

		if (localisationCallBack != null) {
			localisationCallBack.onResume();
		}
	}

	private void initResults() {
		Intent intentResult = new Intent();
		intentResult.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, model.isResetTheme());
		intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, model.isNullResult());
		setResult(AndShowtimeCst.ACTIVITY_RESULT_SEARCH_ACTIVITY, intentResult);
	}

	/**
	 * init the view of activity
	 */
	private void initViews() {

		gpsImgView = (ImageView) findViewById(R.id.searchImgGps);
		searchButton = (Button) findViewById(R.id.searchBtnSearch);
		fieldCityName = (AutoCompleteTextView) findViewById(R.id.searchCityName);
		fieldMovieName = (AutoCompleteTextView) findViewById(R.id.searchMovieName);
		spinnerChooseDay = (Spinner) findViewById(R.id.searchSpinner);

		// manageCallBack
		localisationCallBack = AndShowTimeLayoutUtils.manageLocationManagement(this, gpsImgView, fieldCityName, model);
	}

	private void initViewsState() {

		fillAutoField();

		ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this //
				, R.layout.view_spinner_item//
				, AndShowtimeDateNumberUtil.getSpinnerDaysValues(AndShowTimeSearchActivity.this)//
		);
		adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerChooseDay.setAdapter(adapterSpinner);

	}

	protected void fillAutoField() {
		if (fieldCityName != null) {
			ArrayAdapter<String> adapterII = new ArrayAdapter<String>( //
					this //
					, android.R.layout.simple_dropdown_item_1line //
					, new ArrayList<String>(model.getRequestList()) //
			);
			fieldCityName.setAdapter(adapterII);
		}
	}

	private void initListeners() {
		searchButton.setOnClickListener(listener);
		spinnerChooseDay.setOnItemSelectedListener(listener);

	}

	protected void display() {

		if (model.getLastRequestCity() != null) {
			fieldCityName.setText(model.getLastRequestCity());
		}
		if (model.getLastRequestMovie() != null) {
			fieldMovieName.setText(model.getLastRequestMovie());
		}
	}

	/*
	 * ---------
	 * 
	 * MENU
	 * 
	 * ------
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Log.i(TAG, "onCreateOptionsMenu"); //$NON-NLS-1$
		AndShowTimeMenuUtil.createMenu(menu, MENU_PREF, 3);
		return true;
	}

	;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.i(TAG, "onMenuItemSelected"); //$NON-NLS-1$
		if (AndShowTimeMenuUtil.onMenuItemSelect(this, MENU_PREF, item.getItemId())) {
			if (localisationCallBack != null) {
				localisationCallBack.onPreferenceReturn();
			}
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