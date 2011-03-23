package com.binomed.showtime.android.activity;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.adapter.view.TheaterFavMainListAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.layout.dialogs.last.LastChangeDialog;
import com.binomed.showtime.android.layout.view.TheaterFavView;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.resultsactivity.CineShowTimeResultsActivity;
import com.binomed.showtime.android.searchactivity.CineShowTimeSearchActivity;
import com.binomed.showtime.android.service.CineShowCleanFileService;
import com.binomed.showtime.android.service.CineShowDBGlobalService;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.android.util.CineShowTimeLayoutUtils;
import com.binomed.showtime.android.util.CineShowTimeMenuUtil;
import com.binomed.showtime.android.util.CineShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class CineShowTimeMainActivity extends Activity implements OnClickListener //
		, OnItemClickListener //
{

	private static final String TAG = "AndShowTimeMainActivity"; //$NON-NLS-1$

	private static final int MENU_PREF = Menu.FIRST;

	private Context mainContext;
	private ModelMainActivity model;
	private Button buttonSearchNear;
	private ListView theaterFavList;
	private TheaterFavMainListAdapter adapter;
	private CineShowtimeDbAdapter mDbHelper;

	private GoogleAnalyticsTracker tracker;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start(CineShowtimeCst.GOOGLE_ANALYTICS_ID, this);
		tracker.trackPageView("/MainActivity");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		CineShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
		setContentView(R.layout.activity_main);

		mainContext = this;

		CineShowtimeFactory.initGeocoder(this);

		Intent intentCleanFileService = new Intent(CineShowTimeMainActivity.this, CineShowCleanFileService.class);
		startService(intentCleanFileService);

		initViews();
		initListeners();

		this.model = new ModelMainActivity();

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
		closeDB();
		tracker.dispatch();
		tracker.stop();
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

		List<TheaterBean> favList = getFavTheater();

		if ((favList == null) || (favList.size() == 0)) {
			favList = new ArrayList<TheaterBean>();
			TheaterBean thTmp = new TheaterBean();
			thTmp.setId("0");
			thTmp.setTheaterName(getResources().getString(R.string.msgNoDFav));

			favList.add(thTmp);
		}

		model.setFavList(favList);

		adapter = new TheaterFavMainListAdapter(mainContext, favList, this);

		this.theaterFavList.setAdapter(adapter);
	}

	private void initResults() {
		Intent intentResult = new Intent();
		intentResult.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, model.isResetTheme());
		intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, model.isNullResult());
		setResult(CineShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY, intentResult);
	}

	/**
	 * Init views objects
	 */
	private void initViews() {

		// Watch for button clicks.
		buttonSearchNear = (Button) findViewById(R.id.mainBtnSearchNear);
		theaterFavList = (ListView) findViewById(R.id.mainFavList);
	}

	/**
	 * Init listener
	 */

	private void initListeners() {
		buttonSearchNear.setOnClickListener(this);
		theaterFavList.setOnItemClickListener(this);
	}

	private void display() {
		if (this.showLastChange()) {
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
		tracker.trackEvent("Menu", "Open", "Consult menu from main activity", 0);
		CineShowTimeMenuUtil.createMenu(menu, MENU_PREF, 0);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (CineShowTimeMenuUtil.onMenuItemSelect(this, tracker, MENU_PREF, item.getItemId())) {
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
			CineShowTimeLayoutUtils.changeToTheme(this, getIntent());
		}
	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public void openSearchActivity(TheaterBean theater) {
		Intent intentStartNearActivity = new Intent(this, CineShowTimeSearchActivity.class);
		Bundle extras = new Bundle();
		if (theater != null) {
			extras.putString(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, theater.getId());
			intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, theater.getId());
			if (theater.getPlace() != null) {
				LocalisationBean localisation = theater.getPlace();
				if ((localisation.getLatitude() != null) && (localisation.getLongitude() != null)) {
					extras.putDouble(ParamIntent.ACTIVITY_SEARCH_LATITUDE, localisation.getLatitude());
					extras.putDouble(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, localisation.getLongitude());
					intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, localisation.getLatitude());
					intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, localisation.getLongitude());
				}
				// else {
				StringBuilder place = new StringBuilder();
				if ((theater.getPlace().getCityName() != null //
						)
						&& (theater.getPlace().getCityName().length() > 0)) {
					place.append(theater.getPlace().getCityName());
				}
				if ((theater.getPlace().getCountryNameCode() != null //
						)
						&& (theater.getPlace().getCountryNameCode().length() > 0 //
						) && (place.length() > 0)) {
					place.append(", ").append(theater.getPlace().getCountryNameCode()); //$NON-NLS-1$
				}
				extras.putString(ParamIntent.ACTIVITY_SEARCH_CITY, place.toString());
				intentStartNearActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, place.toString());
				// }
			}
		} else {
			extras.putString(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, null);
		}
		intentStartNearActivity.replaceExtras(extras);
		startActivityForResult(intentStartNearActivity, CineShowtimeCst.ACTIVITY_RESULT_SEARCH_ACTIVITY);
	}

	public void openResultsActivity(TheaterBean theaterBean) {
		openDB();

		try {
			String cityName = theaterBean.getPlace().getCityName();
			if (theaterBean.getPlace().getCountryNameCode() != null) {
				cityName += ", " + theaterBean.getPlace().getCountryNameCode();
			}
			String theaterId = theaterBean.getId();
			boolean forceRequest = false;

			Calendar today = Calendar.getInstance();
			Calendar calendarLastRequest = model.getLastRequestDate();
			if (calendarLastRequest != null) {
				int yearToday = today.get(Calendar.YEAR);
				int monthToday = today.get(Calendar.MONTH);
				int dayToday = today.get(Calendar.DAY_OF_MONTH);
				int yearLast = calendarLastRequest.get(Calendar.YEAR);
				int monthLast = calendarLastRequest.get(Calendar.MONTH);
				int dayLast = calendarLastRequest.get(Calendar.DAY_OF_MONTH);
				if ((yearToday != yearLast) //
						|| (monthToday != monthLast) //
						|| (dayToday != dayLast) //
				) {//

					forceRequest = true;
				} else {
					Cursor cursorInResults = null;
					try {
						if (mDbHelper.isOpen()) {
							cursorInResults = mDbHelper.fetchInResults(theaterBean);
							forceRequest = !cursorInResults.moveToFirst();
						}
					} finally {
						if (cursorInResults != null) {
							cursorInResults.close();
						}
					}
				}
			} else {
				forceRequest = true;
			}

			model.setLastRequestDate(today);

			CineShowtimeFactory.initGeocoder(this);
			Intent intentResultActivity = new Intent(this, CineShowTimeResultsActivity.class);

			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, CineShowTimeEncodingUtil.getEncoding()) : cityName));
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, theaterId);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_DAY, 0);
			intentResultActivity.putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, forceRequest);
			startActivityForResult(intentResultActivity, CineShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY);

		} catch (Exception e) {
			Log.e(TAG, "Error during open results activity", e);
		} finally {
			closeDB();
		}

	}

	/*
	 * 
	 * EVENT Part
	 */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mainBtnSearchNear: {
			tracker.trackEvent("Button", "Click", "Click search Btn", 0);
			tracker.dispatch();
			openSearchActivity(null);
			break;
		}
		case R.id.favItemDelete: {
			tracker.trackEvent("Favoris", "Delete", "Delete from main activity", 0);
			TheaterFavView thFavView = (TheaterFavView) v.getParent().getParent();
			TheaterBean thTmp = thFavView.getTheaterBean();
			Intent intentRemoveTh = new Intent(this, CineShowDBGlobalService.class);
			intentRemoveTh.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_FAV_DELETE);
			intentRemoveTh.putExtra(ParamIntent.SERVICE_DB_DATA, thTmp);
			startService(intentRemoveTh);
			model.getFavList().remove(thTmp);
			if (model.getFavList().size() == 0) {
				TheaterBean thEmtpy = new TheaterBean();
				thEmtpy.setId("0");
				thEmtpy.setTheaterName(getResources().getString(R.string.msgNoDFav));
				model.getFavList().add(thEmtpy);
			}
			adapter.notifyDataSetChanged();
			break;
		}
		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		tracker.trackEvent("Open", "Favoris", "Open from main activity", 0);
		tracker.dispatch();
		// Sinon on ouvre la page résultats
		TheaterBean theater = model.getFavList().get(groupPosition);
		openResultsActivity(theater);
	}

	/*
	 * 
	 * DataBase
	 */

	public void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new CineShowtimeDbAdapter(this);
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			openDB();

			if (mDbHelper.isOpen()) {
				// else we just look at previous request in order to check it's time
				Cursor cursorLastResult = mDbHelper.fetchLastMovieRequest();
				if (cursorLastResult.moveToFirst()) {
					Calendar calendarLastRequest = Calendar.getInstance();
					long timeLastRequest = cursorLastResult.getLong(cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_TIME));
					calendarLastRequest.setTimeInMillis(timeLastRequest);

					model.setLastRequestDate(calendarLastRequest);
					model.setNullResult(cursorLastResult.getShort(cursorLastResult.getColumnIndex(CineShowtimeDbAdapter.KEY_MOVIE_REQUEST_NULL_RESULT)) == 1);
				}
				cursorLastResult.close();
			}
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		} finally {
			closeDB();
		}
	}

	/**
	 * @return
	 */
	public boolean showLastChange() {
		openDB();
		boolean result = false;
		int versionCode = -1;
		if (mDbHelper.isOpen()) {
			Cursor cursorLastChange = mDbHelper.fetchLastChange();
			try {
				PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
				versionCode = pi.versionCode;
			} catch (Exception e) {
				Log.e(TAG, "Error getting package for activity", e); //$NON-NLS-1$
			}
			if (cursorLastChange != null) {
				try {
					if (cursorLastChange.moveToFirst()) {
						int columnIndex = cursorLastChange.getColumnIndex(CineShowtimeDbAdapter.KEY_LAST_CHANGE_VERSION);
						int codeVersion = cursorLastChange.getInt(columnIndex);

						result = codeVersion != versionCode;

					} else {
						result = true;
					}
				} finally {
					cursorLastChange.close();
					closeDB();
				}
			} else {
				result = true;
			}
		}

		if (result) {
			Intent intentService = new Intent(this, CineShowDBGlobalService.class);
			intentService.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_LAST_CHANGE_WRITE);
			intentService.putExtra(ParamIntent.SERVICE_DB_VAL_VERSION_CODE, versionCode);
			startService(intentService);
		}

		return result;
	}

	/**
	 * @return
	 */
	public List<TheaterBean> getFavTheater() {
		openDB();
		List<TheaterBean> theaterList = null;
		try {
			theaterList = CineShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);
		} catch (Exception e) {
			Log.e(TAG, "Error during getting fav", e);
		} finally {
			closeDB();
		}

		return theaterList;
	}

	/**
	 * 
	 */
	public void closeDB() {
		try {
			if (mDbHelper.isOpen()) {
				Log.i(TAG, "Close DB"); //$NON-NLS-1$
				mDbHelper.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "error onDestroy of movie Activity", e); //$NON-NLS-1$
		}
	}

	public void removeFavorite(TheaterBean theaterBean) {
		try {
			if (mDbHelper.isOpen()) {
				mDbHelper.deleteFavorite(theaterBean.getId());
			}
		} catch (Exception e) {
			Log.e(TAG, "error removing theater from fav", e);
		}

	}

}