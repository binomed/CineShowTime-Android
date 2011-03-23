package com.binomed.showtime.android.searchnearactivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import com.binomed.showtime.android.R;
import com.binomed.showtime.android.activity.AndShowTimePreferencesActivity;
import com.binomed.showtime.android.adapter.view.TheaterAndMovieListAdapter;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.handler.ServiceCallBackNear;
import com.binomed.showtime.android.layout.dialogs.SortDialog;
import com.binomed.showtime.android.layout.view.MovieView;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.LocationUtils;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;

public class AndShowTimeSearchNearActivity extends Activity {

	private static final int MENU_PREF = Menu.FIRST;
	private static final int MENU_FAV = Menu.FIRST + 1;
	private static final int MENU_SORT = Menu.FIRST + 2;
	private static final int OPEN_MAP = Menu.FIRST + 3;
	private static final int OPEN_YOUTUBE = Menu.FIRST + 4;
	private static final int CALL_THEATER = Menu.FIRST + 5;
	private static final int ADD_FAV = Menu.FIRST + 6;

	private static final Integer REQUEST_PREF = 1;
	public static final Integer ACTIVITY_OPEN_MOVIE = 0;

	private static final String TAG = "NearActivity"; //$NON-NLS-1$

	protected AutoCompleteTextView fieldCityName;
	protected Button searchButton;
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

	protected EditText getFieldName() {
		return fieldCityName;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate"); //$NON-NLS-1$
		setContentView(R.layout.and_showtime_search_near);

		controler = ControlerSearchNearActivity.getInstance();
		model = controler.getModelNearActivity();
		listener = new ListenerSearchNearActivity(this, controler, model);

		initComparator();
		initViews();
		initListeners();
		initMenus();

		model.setGpsLocalisation(LocationUtils.getLastLocation(AndShowTimeSearchNearActivity.this));

		controler.registerView(this);
		// controler.bindService();
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
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		removeListeners();
		controler.unbindService();
		controler.closeDB();
	}

	/**
	 * init the view of activity
	 */
	private void initViews() {

		// bitmapGpsOn = BitmapFactory.decodeResource(getResources(), R.drawable.stat_sys_gps_on);
		// bitmapGpsOff = BitmapFactory.decodeResource(getResources(), R.drawable.stat_sys_gps_acquiring);
		bitmapGpsOn = BitmapFactory.decodeResource(getResources(), R.drawable.gps_activ);
		bitmapGpsOff = BitmapFactory.decodeResource(getResources(), R.drawable.gps_not_activ);

		gpsImgView = (ImageView) findViewById(R.id.searchNearImgGps);
		searchButton = (Button) findViewById(R.id.searchNearBtnSearch);
		checkButtonLocalisation = (CheckBox) findViewById(R.id.searchNearLocation);
		resultList = (ExpandableListView) findViewById(R.id.searchNearListResult);
		fieldCityName = (AutoCompleteTextView) findViewById(R.id.searchNearCityName);
		spinnerChooseDay = (Spinner) findViewById(R.id.searchNearSpinner);

		gpsImgView.setImageBitmap(bitmapGpsOff);
		checkButtonLocalisation.setChecked(false);
		checkButtonLocalisation.setEnabled(LocationUtils.isGPSEnabled(AndShowTimeSearchNearActivity.this));

		fillAutoField();

		ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this //
				, android.R.layout.simple_spinner_item//
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
		LocationUtils.registerListener(AndShowTimeSearchNearActivity.this, listener);
		spinnerChooseDay.setOnItemSelectedListener(listener);

	}

	private void removeListeners() {
		LocationUtils.unRegisterListener(AndShowTimeSearchNearActivity.this, listener);
	}

	private void initMenus() {
		registerForContextMenu(resultList);
	}

	private void initComparator() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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

	protected void display() {

		if (controler.isServiceRunning()) {
			openDialog();
		} else {
			NearResp nearResp = BeanManagerFactory.getNearResp();
			if (nearResp != null) {
				adapter = new TheaterAndMovieListAdapter(AndShowTimeSearchNearActivity.this, BeanManagerFactory.getNearResp(), comparator);// TODO gérer tri par défaut
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
		menu.add(0, MENU_FAV, 0, R.string.menuFav).setIcon(R.drawable.ic_menu_star);
		menu.add(0, MENU_SORT, 0, R.string.menuSort).setIcon(android.R.drawable.ic_menu_sort_by_size);
		menu.add(0, MENU_PREF, 0, R.string.menuPreferences).setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.i(TAG, "onMenuItemSelected"); //$NON-NLS-1$
		switch (item.getItemId()) {
		case MENU_PREF: {
			Intent launchPreferencesIntent = new Intent().setClass(this, AndShowTimePreferencesActivity.class);

			// Make it a subactivity so we know when it returns
			startActivityForResult(launchPreferencesIntent, REQUEST_PREF);
			return true;
		}
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
			}
			return true;
		}
		case MENU_SORT: {
			SortDialog dialog = new SortDialog(//
					AndShowTimeSearchNearActivity.this //
					, listener //
					, R.array.sort_theaters_values //
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
		int groupId = Long.valueOf(info.id).intValue();
		int itemId = (MovieView.class == targetView.getClass()) ? OPEN_YOUTUBE : OPEN_MAP;
		int menuStr = (MovieView.class == targetView.getClass()) ? R.string.openYoutubeMenuItem : R.string.openMapsMenuItem;
		int icon = (MovieView.class == targetView.getClass()) ? R.drawable.ic_menu_play_clip : android.R.drawable.ic_dialog_map;
		menu.add(groupId, itemId, 0, menuStr).setIcon(icon);
		if (OPEN_MAP == itemId) {
			Object selectItem = resultList.getItemAtPosition(groupId);
			if ((selectItem.getClass() == TheaterBean.class) // 
					&& (((TheaterBean) selectItem).getPhoneNumber() != null) //  
					&& (((TheaterBean) selectItem).getPhoneNumber().length() != 0) //  
			) {
				menu.add(groupId, CALL_THEATER, 0, R.string.menuCall).setIcon(android.R.drawable.ic_menu_call);
			}
			menu.add(groupId, ADD_FAV, 0, R.string.addFav).setIcon(R.drawable.ic_menu_star);
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

}