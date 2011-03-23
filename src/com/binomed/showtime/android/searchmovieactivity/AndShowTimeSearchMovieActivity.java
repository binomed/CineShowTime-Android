package com.binomed.showtime.android.searchmovieactivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.MovieListAdapter;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.handler.ServiceCallBackNear;
import com.binomed.showtime.android.layout.dialogs.SortDialog;
import com.binomed.showtime.android.layout.view.MovieView;
import com.binomed.showtime.android.util.AndShowTimeMenuUtil;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.LocationUtils;
import com.binomed.showtime.android.util.LocationUtils.ProviderEnum;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.MovieResp;
import com.binomed.showtime.beans.TheaterBean;
import com.binomed.showtime.cst.SpecialChars;

public class AndShowTimeSearchMovieActivity extends Activity {

	private static final int MENU_SORT = Menu.FIRST;
	private static final int OPEN_MAP = Menu.FIRST + 1;
	private static final int OPEN_YOUTUBE = Menu.FIRST + 2;
	private static final int CALL_THEATER = Menu.FIRST + 3;
	private static final int MENU_PREF = Menu.FIRST + 5;

	public static final Integer ACTIVITY_OPEN_MOVIE = 0;

	private static final String TAG = "SearchMovieActivity"; //$NON-NLS-1$

	protected AutoCompleteTextView fieldNearName;
	protected AutoCompleteTextView fieldMovieName;
	protected CheckBox checkLocationButton;
	protected Button searchButton;
	protected ListView resultList;
	protected ProgressDialog progressDialog;
	protected MovieListAdapter adapter = null;
	protected TextView txtMovieFind, txtMovieFindDureation, movieFind, movieFindDuration;
	protected Spinner spinnerChooseDay;
	protected ImageView gpsImgView;

	private ControlerSearchMovieActivity controler;
	private ListenerSearchMovieActivity listener;
	private ModelSearchMovieActivity model;

	protected Comparator<TheaterBean> comparator;

	protected Bitmap bitmapGpsOn;
	protected Bitmap bitmapGpsOff;

	private ProviderEnum provider = null;
	private boolean checkboxPreference, locationListener;
	private SharedPreferences prefs;

	protected EditText getFieldName() {
		return fieldNearName;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate"); //$NON-NLS-1$
		setContentView(R.layout.and_showtime_search_movie);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		controler = ControlerSearchMovieActivity.getInstance();
		model = controler.getModelNearActivity();
		listener = new ListenerSearchMovieActivity(this, controler, model);

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

		model.setGpsLocalisation(checkboxPreference ? LocationUtils.getLastLocation(AndShowTimeSearchMovieActivity.this, provider) : null);

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

		gpsImgView = (ImageView) findViewById(R.id.searchMovieImgGps);
		searchButton = (Button) findViewById(R.id.searchMovieBtnSearch);
		checkLocationButton = (CheckBox) findViewById(R.id.searchMovieLocation);
		resultList = (ListView) findViewById(R.id.searchMovieListResult);
		fieldNearName = (AutoCompleteTextView) findViewById(R.id.searchMovieCityName);
		fieldMovieName = (AutoCompleteTextView) findViewById(R.id.searchMovieMovieName);
		txtMovieFind = (TextView) findViewById(R.id.searchMovieTxtMovieFind);
		txtMovieFindDureation = (TextView) findViewById(R.id.searchMovieTxtMovieFindDuration);
		movieFind = (TextView) findViewById(R.id.searchMovieMovieFind);
		movieFindDuration = (TextView) findViewById(R.id.searchMovieMovieFindDuration);
		spinnerChooseDay = (Spinner) findViewById(R.id.searchMovieSpinner);

	}

	private void initViewsState() {

		gpsImgView.setImageBitmap(bitmapGpsOff);
		checkLocationButton.setChecked(false);
		checkLocationButton.setEnabled(LocationUtils.isLocalisationEnabled(AndShowTimeSearchMovieActivity.this, provider));

		fillAutoFields();

		ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this //
				, R.layout.and_showtime_spinner_item//
				, AndShowtimeDateNumberUtil.getSpinnerDaysValues(AndShowTimeSearchMovieActivity.this)//
		);
		adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerChooseDay.setAdapter(adapterSpinner);
	}

	protected void fillAutoFields() {
		ArrayAdapter<String> adapterNear = new ArrayAdapter<String>( //
				this //
				, android.R.layout.simple_dropdown_item_1line //
				, new ArrayList<String>(model.getNearRequestList()) //
		);
		fieldNearName.setAdapter(adapterNear);
		ArrayAdapter<String> adapterMovie = new ArrayAdapter<String>( //
				this //
				, android.R.layout.simple_dropdown_item_1line //
				, new ArrayList<String>(model.getRequestMovieList()) //
		);
		fieldMovieName.setAdapter(adapterMovie);
	}

	private void initListeners() {
		searchButton.setOnClickListener(listener);
		checkLocationButton.setOnClickListener(listener);
		resultList.setOnItemClickListener(listener);
		spinnerChooseDay.setOnItemSelectedListener(listener);
	}

	protected void initListenersLocation() {
		if (checkboxPreference) {
			locationListener = true;
			LocationUtils.registerLocalisationListener(AndShowTimeSearchMovieActivity.this, provider, listener);
		}

	}

	protected void removeListenersLocation() {
		locationListener = false;
		LocationUtils.unRegisterListener(AndShowTimeSearchMovieActivity.this, listener);
	}

	private void initMenus() {
		registerForContextMenu(resultList);
	}

	private void initComparator() {
		String sort = prefs.getString(this.getResources().getString(R.string.preference_sort_key_sort_movie)//
				, this.getResources().getString(R.string.preference_sort_default_sort_movie));
		String[] values = getResources().getStringArray(R.array.sort_movies_values_code);
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

			MovieResp movieResp = BeanManagerFactory.getMovieResp();
			if (movieResp != null) {
				MovieBean movie = movieResp.getMovie();
				if (movie != null) {
					adapter = new MovieListAdapter(AndShowTimeSearchMovieActivity.this, movieResp.getTheaterList(), comparator);
					model.setCityName(movieResp.getCityName());
					fieldNearName.setText(movieResp.getCityName());

					txtMovieFind.setText(AndShowTimeSearchMovieActivity.this.getResources().getString(R.string.txtMovieFind));
					txtMovieFindDureation.setText(AndShowTimeSearchMovieActivity.this.getResources().getString(R.string.txtMovieFindDuration));

					movieFind.setText(movie.getMovieName());
					movieFindDuration.setText(AndShowtimeDateNumberUtil.showMovieTimeLength(AndShowTimeSearchMovieActivity.this, movie));
				} else {
					adapter = new MovieListAdapter(AndShowTimeSearchMovieActivity.this, new ArrayList<TheaterBean>(), comparator);
					txtMovieFind.setText(AndShowTimeSearchMovieActivity.this.getResources().getString(R.string.txtMovieFindValueNotFound));
					txtMovieFindDureation.setText(SpecialChars.EMPTY);
					movieFind.setText(SpecialChars.EMPTY);
					movieFindDuration.setText(SpecialChars.EMPTY);
				}
			}
			resultList.setAdapter(adapter);
		}
	}

	protected void launchMovieService() throws UnsupportedEncodingException {
		openDialog();

		controler.launchMovieService();
	}

	/**
	 * 
	 */
	protected void openDialog() {
		progressDialog = ProgressDialog.show(AndShowTimeSearchMovieActivity.this, //
				AndShowTimeSearchMovieActivity.this.getResources().getString(R.string.searchMovieProgressTitle)//
				, AndShowTimeSearchMovieActivity.this.getResources().getString(R.string.searchMovieProgressMsg) //
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
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		Log.i(TAG, "onCreateContextMenu"); //$NON-NLS-1$
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int groupId = Long.valueOf(info.id).intValue();
		int itemId = OPEN_MAP;
		int menuStr = R.string.openMapsMenuItem;
		int icon = android.R.drawable.ic_dialog_map;
		menu.add(groupId, itemId, 0, menuStr).setIcon(icon);
		Object selectItem = resultList.getItemAtPosition(groupId);
		if ((selectItem.getClass() == TheaterBean.class) // 
				&& (((TheaterBean) selectItem).getPhoneNumber() != null) //  
				&& (((TheaterBean) selectItem).getPhoneNumber().length() != 0) //  
		) {
			menu.add(groupId, CALL_THEATER, 0, R.string.menuCall).setIcon(android.R.drawable.ic_menu_call);
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
				if (theater.getPlace() != null) {
					startActivity(IntentShowtime.createMapsIntent(theater));
				}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_SORT, 0, R.string.menuSort).setIcon(android.R.drawable.ic_menu_sort_by_size);
		AndShowTimeMenuUtil.createMenu(menu, MENU_PREF);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (AndShowTimeMenuUtil.onMenuItemSelect(this, MENU_PREF, item.getItemId())) {
			checkboxPreference = prefs.getBoolean(getResources().getString(R.string.preference_loc_key_enable_localisation), true);
			if (checkboxPreference && checkLocationButton.isChecked() && !locationListener) {
				initListenersLocation();
			} else {
				removeListenersLocation();
			}
			return true;
		}
		switch (item.getItemId()) {
		case MENU_SORT: {
			SortDialog dialog = new SortDialog(//
					AndShowTimeSearchMovieActivity.this //
					, listener //
					, R.array.sort_movies_values //
			);
			dialog.setTitle(AndShowTimeSearchMovieActivity.this.getResources().getString(R.string.sortDialogTitle));
			dialog.setFeatureDrawableResource(featureId, android.R.drawable.ic_menu_sort_by_size);
			dialog.show();
			return true;
		}
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);

	}

}