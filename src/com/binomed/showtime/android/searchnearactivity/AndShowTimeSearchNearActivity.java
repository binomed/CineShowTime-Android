package com.binomed.showtime.android.searchnearactivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.TheaterAndMovieListAdapter;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.handler.ServiceCallBackNear;
import com.binomed.showtime.android.layout.dialogs.fav.FavDialog;
import com.binomed.showtime.android.layout.dialogs.sort.ListDialog;
import com.binomed.showtime.android.layout.view.MovieView;
import com.binomed.showtime.android.util.AndShowTimeLayoutUtils;
import com.binomed.showtime.android.util.AndShowTimeMenuUtil;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.LocationUtils;
import com.binomed.showtime.android.util.LocationUtils.ProviderEnum;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;
import com.binomed.showtime.cst.HttpParamsCst;

public class AndShowTimeSearchNearActivity extends Activity {

	private static final int MENU_FAV = Menu.FIRST;
	private static final int MENU_SORT = Menu.FIRST + 1;
	private static final int OPEN_MAP = Menu.FIRST + 2;
	private static final int OPEN_MAP_DIRECTION = Menu.FIRST + 3;
	private static final int OPEN_YOUTUBE = Menu.FIRST + 4;
	private static final int CALL_THEATER = Menu.FIRST + 5;
	private static final int ADD_FAV = Menu.FIRST + 6;
	private static final int MENU_PREF = Menu.FIRST + 7;

	protected static final int ID_SORT = 1;
	protected static final int ID_VOICE = ID_SORT + 1;

	protected static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	public static final Integer ACTIVITY_OPEN_MOVIE = 0;

	private static final String TAG = "NearActivity"; //$NON-NLS-1$

	protected AutoCompleteTextView fieldCityName;
	protected Button searchButton;
	protected ImageButton speechButton;
	protected CheckBox checkButtonLocalisation;
	protected ExpandableListView resultList;
	protected ProgressDialog progressDialog;
	protected TheaterAndMovieListAdapter adapter = null;
	protected Spinner spinnerChooseDay;
	protected ImageView gpsImgView;

	private ControlerSearchNearActivity controler;
	private ListenerSearchNearActivity listener;
	private ModelSearchNearActivity model;

	protected Comparator<TheaterBean> comparator;

	protected Bitmap bitmapGpsOn;
	protected Bitmap bitmapGpsOff;

	private ProviderEnum provider;
	private boolean checkboxPreference, locationListener;
	private SharedPreferences prefs;

	protected EditText getFieldName() {
		return fieldCityName;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate"); //$NON-NLS-1$
		setContentView(R.layout.and_showtime_search_near);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		controler = ControlerSearchNearActivity.getInstance();
		model = controler.getModelNearActivity();
		listener = new ListenerSearchNearActivity(this, controler, model);

		// We init the theater id if set

		String theaterId = getIntent().getStringExtra(ParamIntent.ACTIVITY_NEAR_THEATER_ID);
		getIntent().putExtra(ParamIntent.ACTIVITY_NEAR_THEATER_ID, "");
		if (theaterId != null && theaterId.length() > 0) {
			model.setForceResearch(true);
			model.setFavTheaterId(theaterId);
			model.setLocalisationSearch(null);
			model.setCityName(null);
			Double latitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_NEAR_LATITUDE, 0);
			Double longitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_NEAR_LONGITUDE, 0);
			String cityName = getIntent().getStringExtra(ParamIntent.ACTIVITY_NEAR_CITY_NAME);
			if (latitude != 0 && longitude != 0) {
				Location locationTheater = new Location("GPS");
				locationTheater.setLatitude(latitude);
				locationTheater.setLongitude(longitude);
				model.setLocalisationSearch(locationTheater);
			} else {
				if (cityName != null) {
					model.setCityName(cityName);
				}
			}
		} else {
			model.setForceResearch(false);
		}

		initComparator();
		initViews();
		initMenus();

		controler.registerView(this);
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
		controler.unbindService();
		controler.closeDB();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause"); //$NON-NLS-1$
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		removeListenersLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume"); //$NON-NLS-1$
		initProvider();
		initListeners();
		initViewsState();

		model.setGpsLocalisation(checkboxPreference ? LocationUtils.getLastLocation(AndShowTimeSearchNearActivity.this, provider) : null);

		display();

	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart"); //$NON-NLS-1$
		super.onRestart();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstance"); //$NON-NLS-1$
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, "onSaveInstance"); //$NON-NLS-1$
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart"); //$NON-NLS-1$
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop"); //$NON-NLS-1$
		super.onStop();
	}

	/**
	 * init the view of activity
	 */
	private void initViews() {

		bitmapGpsOn = BitmapFactory.decodeResource(getResources(), R.drawable.gps_activ);
		bitmapGpsOff = BitmapFactory.decodeResource(getResources(), R.drawable.gps_not_activ);

		gpsImgView = (ImageView) findViewById(R.id.searchNearImgGps);
		searchButton = (Button) findViewById(R.id.searchNearBtnSearch);
		checkButtonLocalisation = (CheckBox) findViewById(R.id.searchNearLocation);
		resultList = (ExpandableListView) findViewById(R.id.searchNearListResult);
		fieldCityName = (AutoCompleteTextView) findViewById(R.id.searchNearCityName);
		spinnerChooseDay = (Spinner) findViewById(R.id.searchNearSpinner);

		speechButton = (ImageButton) findViewById(R.id.searchNearBtnSpeech);
		// Manage speech button just if package present on device
		AndShowTimeLayoutUtils.manageVisibiltyFieldSpeech(this, speechButton, fieldCityName, R.id.searchNearTxtCityName, R.id.searchNearLocation, -1);

	}

	private void initViewsState() {

		gpsImgView.setImageBitmap(bitmapGpsOff);
		checkButtonLocalisation.setChecked(false);
		checkButtonLocalisation.setEnabled(LocationUtils.isLocalisationEnabled(AndShowTimeSearchNearActivity.this, provider));

		// Check to see if a recognition activity is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
			// speechButton.setBackgroundResource(android.R.drawable.ic_btn_speak_now);
			speechButton.setOnClickListener(listener);
		}

		fillAutoField();

		ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this //
				, R.layout.and_showtime_spinner_item//
				, AndShowtimeDateNumberUtil.getSpinnerDaysValues(AndShowTimeSearchNearActivity.this)//
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
		checkButtonLocalisation.setOnClickListener(listener);
		resultList.setOnChildClickListener(listener);
		resultList.setOnGroupClickListener(listener);
		spinnerChooseDay.setOnItemSelectedListener(listener);

	}

	protected void initListenersLocation() {
		if (checkboxPreference) {
			locationListener = true;
			LocationUtils.registerLocalisationListener(AndShowTimeSearchNearActivity.this, provider, listener);
		}

	}

	protected void removeListenersLocation() {
		locationListener = false;
		LocationUtils.unRegisterListener(AndShowTimeSearchNearActivity.this, listener);
	}

	private void initMenus() {
		registerForContextMenu(resultList);
	}

	private void initComparator() {
		String sort = prefs.getString(this.getResources().getString(R.string.preference_sort_key_sort_theater) //
				, this.getResources().getString(R.string.preference_sort_default_sort_theater));
		String[] values = getResources().getStringArray(R.array.sort_theaters_values_code);
		int code = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(sort)) {
				code = i;
				break;
			}
		}
		switch (code) {
		case 0:
			comparator = AndShowtimeFactory.getTheaterNameComparator();
			break;
		case 1:
			comparator = AndShowtimeFactory.getTheaterDistanceComparator();
			break;
		case 2:
			comparator = AndShowtimeFactory.getTheaterShowtimeComparator();
			break;
		default:
			comparator = null;
			break;
		}

	}

	private void initProvider() {
		provider = LocationUtils.getProvider(prefs, this);
		checkboxPreference = prefs.getBoolean(getResources().getString(R.string.preference_loc_key_enable_localisation), true);

	}

	protected void display() {

		if (controler.isServiceRunning()) {
			openDialog();
		} else {
			NearResp nearResp = BeanManagerFactory.getNearResp();
			if (nearResp != null) {
				List<TheaterBean> theaterList = nearResp.getTheaterList();
				if (theaterList != null && theaterList.size() == 1) {
					TheaterBean errorTheater = theaterList.get(0);
					if (errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_WRONG_DATE))//
							|| errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_WRONG_PLACE)) //
					) {
						switch (Integer.valueOf(errorTheater.getId())) {
						case HttpParamsCst.ERROR_WRONG_DATE:
							errorTheater.setTheaterName(getResources().getString(R.string.msgNoDateMatch));
							break;
						case HttpParamsCst.ERROR_WRONG_PLACE:
							errorTheater.setTheaterName(getResources().getString(R.string.msgNoPlaceMatch));
							break;

						default:
							break;
						}
					}
				}
				adapter = new TheaterAndMovieListAdapter(AndShowTimeSearchNearActivity.this, nearResp, comparator);
				resultList.setAdapter(adapter);
				if ((nearResp != null) && (nearResp.getCityName() != null) && (nearResp.getCityName().length() > 0)) {
					model.setCityName(nearResp.getCityName());
				}
				fieldCityName.setText(model.getCityName());
			}
		}
	}

	protected void launchNearService() throws UnsupportedEncodingException {
		openDialog();

		controler.launchNearService();
	}

	/**
	 * 
	 */
	protected void openDialog() {
		progressDialog = ProgressDialog.show(AndShowTimeSearchNearActivity.this, //
				AndShowTimeSearchNearActivity.this.getResources().getString(R.string.searchNearProgressTitle)//
				, AndShowTimeSearchNearActivity.this.getResources().getString(R.string.searchNearProgressMsg) //
				, true, false);
	}

	/**
	 * The call back message handler
	 */
	public ServiceCallBackNear m_callbackHandler = new ServiceCallBackNear() {

		@Override
		public void handleInputRecived() {

			display();
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

		}

	};

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
		menu.add(0, MENU_FAV, 1, R.string.menuFav).setIcon(R.drawable.ic_menu_star);
		menu.add(0, MENU_SORT, 2, R.string.menuSort).setIcon(android.R.drawable.ic_menu_sort_by_size);
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
			checkboxPreference = prefs.getBoolean(getResources().getString(R.string.preference_loc_key_enable_localisation), true);
			if (checkboxPreference && checkButtonLocalisation.isChecked() && !locationListener) {
				initListenersLocation();
			} else {
				removeListenersLocation();
			}
			return true;
		}
		switch (item.getItemId()) {
		case MENU_FAV: {
			List<TheaterBean> theaterList = controler.getFavTheater();
			if (!theaterList.isEmpty()) {
				FavDialog dialog = new FavDialog(//
						AndShowTimeSearchNearActivity.this //
						, listener //
						, theaterList//
				);
				dialog.setTitle(AndShowTimeSearchNearActivity.this.getResources().getString(R.string.dialogBookmarkTitle));
				dialog.setFeatureDrawableResource(featureId, android.R.drawable.ic_input_get);
				dialog.show();
			} else {
				Toast.makeText(this, R.string.msgNoDFav, Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		case MENU_SORT: {
			ListDialog dialog = new ListDialog(//
					AndShowTimeSearchNearActivity.this //
					, listener //
					, R.array.sort_theaters_values //
					, ID_VOICE //
			);
			dialog.setTitle(AndShowTimeSearchNearActivity.this.getResources().getString(R.string.sortDialogTitle));
			dialog.setFeatureDrawableResource(featureId, android.R.drawable.ic_menu_sort_by_size);
			dialog.show();

			return true;
		}
		}

		return super.onMenuItemSelected(featureId, item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		Log.i(TAG, "onCreateContextMenu"); //$NON-NLS-1$
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
		View targetView = info.targetView;
		boolean isMovieView = (MovieView.class == targetView.getClass());
		int groupId = Long.valueOf(info.id).intValue();
		Object selectItem = resultList.getItemAtPosition(groupId);
		if (!isMovieView) {
			if (((TheaterBean) selectItem).getId().equals(String.valueOf(HttpParamsCst.ERROR_WRONG_DATE)) //
					|| ((TheaterBean) selectItem).getId().equals(String.valueOf(HttpParamsCst.ERROR_WRONG_PLACE))) {
				return;
			} else {
				if ((selectItem.getClass() == TheaterBean.class) // 
						&& (((TheaterBean) selectItem).getPhoneNumber() != null) //  
						&& (((TheaterBean) selectItem).getPhoneNumber().length() != 0) //  
				) {
					menu.add(groupId, CALL_THEATER, 1, R.string.menuCall).setIcon(android.R.drawable.ic_menu_call);
				}
				menu.add(groupId, ADD_FAV, 1, R.string.addFav).setIcon(R.drawable.ic_menu_star);
				if ((selectItem.getClass() == TheaterBean.class) // 
						&& (((TheaterBean) selectItem) != null) //
						&& (model.getGpsLocalisation() != null)) {
					menu.add(groupId, OPEN_MAP_DIRECTION, 2, R.string.openMapsDriveMenuItem).setIcon(android.R.drawable.ic_menu_directions);
				}
			}
		}
		int itemId = isMovieView ? OPEN_YOUTUBE : OPEN_MAP;
		int menuStr = isMovieView ? R.string.openYoutubeMenuItem : R.string.openMapsMenuItem;
		int icon = isMovieView ? R.drawable.ic_menu_play_clip : android.R.drawable.ic_dialog_map;
		if ((isMovieView //
				&& (selectItem.getClass() == TheaterBean.class) // 
		&& (((TheaterBean) selectItem) != null) //
				)
				|| !isMovieView) {
			menu.add(groupId, itemId, 0, menuStr).setIcon(icon);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i(TAG, "onContextItmSelected"); //$NON-NLS-1$
		switch (item.getItemId()) {
		case OPEN_MAP: {
			Object selectItem = resultList.getItemAtPosition(item.getGroupId());
			if (selectItem.getClass() == TheaterBean.class) {
				TheaterBean theater = (TheaterBean) selectItem;
				startActivity(IntentShowtime.createMapsIntent(theater));
			}
			return true;
		}
		case OPEN_MAP_DIRECTION: {
			Object selectItem = resultList.getItemAtPosition(item.getGroupId());
			if (selectItem.getClass() == TheaterBean.class) {
				TheaterBean theater = (TheaterBean) selectItem;
				Intent intentDirection = IntentShowtime.createMapsWithDrivingDirectionIntent(theater, model.getGpsLocalisation());
				if (intentDirection != null) {
					startActivity(intentDirection);
				}
			}
			return true;
		}
		case ADD_FAV: {
			Object selectItem = resultList.getItemAtPosition(item.getGroupId());
			if (selectItem.getClass() == TheaterBean.class) {
				TheaterBean theater = (TheaterBean) selectItem;
				controler.addFavorite(theater);
			}
			return true;
		}
		case CALL_THEATER: {
			Object selectItem = resultList.getItemAtPosition(item.getGroupId());
			if (selectItem.getClass() == TheaterBean.class) {
				TheaterBean theater = (TheaterBean) selectItem;
				startActivity(IntentShowtime.createCallIntent(theater));
			}
			return true;
		}
		case OPEN_YOUTUBE: {
			ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
			MovieView targetView = (MovieView) info.targetView;
			MovieBean movie = targetView.getMovieBean();
			startActivity(IntentShowtime.createYoutubeIntent(movie));
			return true;
		}
		}
		return super.onContextItemSelected(item);
	}

	/*
	 * 
	 * Activity and Service Results
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (matches != null && matches.size() > 0) {
				model.setVoiceCityList(matches);
				ListDialog dialog = new ListDialog(//
						AndShowTimeSearchNearActivity.this //
						, listener //
						, matches //
						, ID_VOICE//
				);
				dialog.setTitle(AndShowTimeSearchNearActivity.this.getResources().getString(R.string.msgSpeecRecognition));
				dialog.show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}