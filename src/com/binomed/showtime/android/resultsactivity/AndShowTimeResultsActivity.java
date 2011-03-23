package com.binomed.showtime.android.resultsactivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.CineShowTimeExpandableListAdapter;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.handler.ServiceCallBackSearch;
import com.binomed.showtime.android.layout.dialogs.sort.ListDialog;
import com.binomed.showtime.android.util.AndShowTimeLayoutUtils;
import com.binomed.showtime.android.util.AndShowTimeMenuUtil;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.comparator.AndShowtimeComparator;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;
import com.binomed.showtime.cst.HttpParamsCst;

public class AndShowTimeResultsActivity extends Activity {

	private static final int MENU_SORT = Menu.FIRST;
	private static final int MENU_PREF = Menu.FIRST + 1;

	protected static final int ID_SORT = 1;

	private static final String TAG = "ResultsActivity"; //$NON-NLS-1$

	protected ExpandableListView resultList;
	protected ProgressDialog progressDialog;
	protected CineShowTimeExpandableListAdapter adapter = null;

	private ControlerResultsActivity controler;
	private ListenerResultsActivity listener;
	private ModelResultsActivity model;

	protected boolean movieView;

	protected Comparator<?> comparator;

	private SharedPreferences prefs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		AndShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
		Log.i(TAG, "onCreate"); //$NON-NLS-1$
		setContentView(R.layout.activity_results);

		controler = ControlerResultsActivity.getInstance();
		model = controler.getModelResultsActivity();
		listener = new ListenerResultsActivity(this, controler, model);

		// We init the theater id if set

		model.setForceResearch(getIntent().getBooleanExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, true));
		getIntent().putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, false);
		model.setFavTheaterId(getIntent().getStringExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID));
		model.setLocalisation(null);
		model.setDay(getIntent().getIntExtra(ParamIntent.ACTIVITY_SEARCH_DAY, 0));
		model.setCityName(getIntent().getStringExtra(ParamIntent.ACTIVITY_SEARCH_CITY));
		model.setMovieName(getIntent().getStringExtra(ParamIntent.ACTIVITY_SEARCH_MOVIE_NAME));
		Double latitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, 0);
		Double longitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, 0);
		if (latitude != 0 && longitude != 0) {
			Location locationTheater = new Location("GPS");
			locationTheater.setLatitude(latitude);
			locationTheater.setLongitude(longitude);
			model.setLocalisation(locationTheater);
		}
		getIntent().putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, "");

		movieView = model.getMovieName() != null && model.getMovieName().length() > 0;

		initComparator();
		initViews();
		initMenus();

		controler.registerView(this);

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
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume"); //$NON-NLS-1$
		initListeners();

		display();

	}

	private void initResults() {
		Intent intentResult = new Intent();
		intentResult.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, model.isResetTheme());
		intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, model.isNullResult());
		setResult(AndShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY, intentResult);
	}

	/**
	 * init the view of activity
	 */
	private void initViews() {

		resultList = (ExpandableListView) findViewById(R.id.resultListResult);

		// Manage Adapter
		adapter = new CineShowTimeExpandableListAdapter(this, listener);
	}

	private void initListeners() {
		resultList.setOnChildClickListener(listener);
		resultList.setOnGroupClickListener(listener);
		resultList.setOnGroupExpandListener(listener);
		resultList.setOnGroupCollapseListener(listener);
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

		if (!movieView) {
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
		} else {
			comparator = AndShowtimeFactory.getMovieNameComparator();

		}

	}

	protected void display() {
		if (controler.isServiceRunning()) {
			openDialog();
		} else {
			NearResp nearResp = BeanManagerFactory.getNearResp();
			if (nearResp != null) {
				boolean error = false;
				List<TheaterBean> theaterList = nearResp.getTheaterList();
				if (theaterList != null && theaterList.size() == 1) {
					TheaterBean errorTheater = theaterList.get(0);
					if (errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_WRONG_DATE))//
							|| errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_WRONG_PLACE)) //
							|| errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_NO_DATA)) //
							|| errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_CUSTOM_MESSAGE)) //
					) {
						error = true;
						switch (Integer.valueOf(errorTheater.getId())) {
						case HttpParamsCst.ERROR_WRONG_DATE:
							errorTheater.setTheaterName(getResources().getString(R.string.msgNoDateMatch));
							break;
						case HttpParamsCst.ERROR_WRONG_PLACE:
							errorTheater.setTheaterName(getResources().getString(R.string.msgNoPlaceMatch));
							break;
						case HttpParamsCst.ERROR_NO_DATA:
							errorTheater.setTheaterName(getResources().getString(R.string.msgNoResultRetryLater));
							break;
						case HttpParamsCst.ERROR_CUSTOM_MESSAGE:
							// Nothing to do special the custom message is in theaterTitle
							break;

						default:
							break;
						}
					}
				} else if (theaterList == null || theaterList.size() == 0) {
					error = true;
					TheaterBean theaterZeroResp = new TheaterBean();
					theaterZeroResp.setId(String.valueOf(HttpParamsCst.ERROR_NO_DATA));
					theaterZeroResp.setTheaterName(getResources().getString(R.string.msgNoResultRetryLater));
					if (theaterList == null) {
						theaterList = new ArrayList<TheaterBean>();
						nearResp.setTheaterList(theaterList);
					}
					theaterList.add(theaterZeroResp);
				}
				adapter.setTheaterList(nearResp, model.getTheaterFavList(), (AndShowtimeComparator<?>) comparator);
				resultList.setAdapter(adapter);
				if (theaterList.size() == 1 && !error) {
					resultList.expandGroup(0);
				} else {
					for (int i : model.getGroupExpanded()) {
						resultList.expandGroup(i);
					}
				}
				if ((nearResp != null) && (nearResp.getCityName() != null) && (nearResp.getCityName().length() > 0)) {
					model.setCityName(nearResp.getCityName());
				}
			}
		}

	}

	protected void changeComparator(AndShowtimeComparator<?> comparator) {
		this.comparator = comparator;
		movieView = comparator.getType() == comparator.COMPARATOR_MOVIE_NAME;
		adapter.changeSort(comparator);
		resultList.setAdapter(adapter);
	}

	protected void launchNearService() throws UnsupportedEncodingException {
		openDialog();

		controler.launchSearchService();
	}

	/**
	 * 
	 */
	protected void openDialog() {
		progressDialog = ProgressDialog.show(AndShowTimeResultsActivity.this, //
				AndShowTimeResultsActivity.this.getResources().getString(R.string.searchNearProgressTitle)//
				, AndShowTimeResultsActivity.this.getResources().getString(R.string.searchNearProgressMsg) //
				, true, false);
	}

	/**
	 * The call back message handler
	 */
	public ServiceCallBackSearch m_callbackHandler = new ServiceCallBackSearch() {

		@Override
		public void handleInputRecived() {

			try {
				initResults();
				display();
			} catch (Exception e) {
				Log.e(TAG, "Error during display", e);
			}
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
			adapter.changePreferences();
			return true;
		}
		switch (item.getItemId()) {
		case MENU_SORT: {
			ListDialog dialog = new ListDialog(//
					AndShowTimeResultsActivity.this //
					, listener //
					, R.array.sort_theaters_values //
					, ID_SORT //
			);
			dialog.setTitle(AndShowTimeResultsActivity.this.getResources().getString(R.string.sortDialogTitle));
			dialog.setFeatureDrawableResource(featureId, android.R.drawable.ic_menu_sort_by_size);
			dialog.show();

			return true;
		}
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

		if (requestCode == AndShowtimeCst.ACTIVITY_RESULT_PREFERENCES) {
			adapter.changePreferences();

		}

		if (model.isResetTheme()) {
			AndShowTimeLayoutUtils.changeToTheme(this, getIntent());
		}

	}

}