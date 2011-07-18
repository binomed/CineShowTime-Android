package com.binomed.showtime.android.screen.results.tablet;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.movie.CineShowTimeMovieActivity;
import com.binomed.showtime.android.screen.movie.CineShowTimeMovieFragment;
import com.binomed.showtime.android.screen.movie.CineShowTimeMovieFragment.MovieFragmentInteraction;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment;
import com.binomed.showtime.android.screen.results.CineShowTimeResultsFragment.CineShowTimeResultInteraction;
import com.binomed.showtime.android.util.activity.AbstractCineShowTimeActivity;

public class CineShowTimeResultsTabletActivity extends AbstractCineShowTimeActivity<IModelResultTablet> implements // OnChildClickListener //
		// , OnGroupClickListener //
		// , OnGroupExpandListener //
		// , OnGroupCollapseListener //
		// , ListSelectionListener //
		// , OnClickListener//
		// , OnCancelListener //
		CineShowTimeResultInteraction<IModelResultTablet> //
		, MovieFragmentInteraction<IModelResultTablet> //
{

	private static final int MENU_SORT = Menu.FIRST;
	private static final int MENU_PREF = Menu.FIRST + 1;

	protected static final int ID_SORT = 1;

	private static final String TAG = "ResultsActivity"; //$NON-NLS-1$
	private static final String TRACKER_NAME = "/ResultActivity"; //$NON-NLS-1$

	private CineShowTimeResultsFragment fragmentResult;
	private CineShowTimeMovieFragment fragmentMovie;
	private CineShowTimeFrameFragment fragmentFrame;

	private Intent intentStartMovieActivity;

	// Var for portrait mode
	private boolean portraitMode;
	private ImageButton btnExpand;
	private FrameLayout frameLayout = null;
	private LinearLayout infoLayout = null;
	private final Handler mHandler = new Handler();

	private int dist = 200;
	private int delay = 500;
	private int widthLeftFull, widthLeftLight;
	private FrameLayout.LayoutParams paramsLeft, paramsRight;

	private boolean hideRight;

	// protected ExpandableListView resultList;
	// protected ProgressDialog progressDialog;
	// protected CineShowTimeExpandableListAdapter adapter = null;
	//
	// private ModelResultsActivity model;
	//
	// protected boolean movieView;
	//
	// protected Comparator<?> comparator;
	// private IServiceSearch serviceResult;
	//
	// private SharedPreferences prefs;
	// private CineShowtimeDbAdapter mDbHelper;
	// protected GoogleAnalyticsTracker tracker;

	// /** Called when the activity is first created. */
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// tracker = GoogleAnalyticsTracker.getInstance();
	// tracker.start(CineShowtimeCst.GOOGLE_ANALYTICS_ID, this);
	// tracker.trackPageView("/ResultActivity");
	// prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	// CineShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
	//		Log.i(TAG, "onCreate"); //$NON-NLS-1$
	// setContentView(R.layout.activity_result);
	//
	// model = new ModelResultsActivity();
	//
	// // We init the theater id if set
	//
	// model.setForceResearch(getIntent().getBooleanExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, true));
	// getIntent().putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, false);
	// model.setFavTheaterId(getIntent().getStringExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID));
	// model.setLocalisation(null);
	// model.setDay(getIntent().getIntExtra(ParamIntent.ACTIVITY_SEARCH_DAY, 0));
	// model.setCityName(getIntent().getStringExtra(ParamIntent.ACTIVITY_SEARCH_CITY));
	// model.setMovieName(getIntent().getStringExtra(ParamIntent.ACTIVITY_SEARCH_MOVIE_NAME));
	// Double latitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, 0);
	// Double longitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, 0);
	// if ((latitude != 0) && (longitude != 0)) {
	// Location locationTheater = new Location("GPS");
	// locationTheater.setLatitude(latitude);
	// locationTheater.setLongitude(longitude);
	// model.setLocalisation(locationTheater);
	// }
	// getIntent().putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, "");
	//
	// movieView = (model.getMovieName() != null) && (model.getMovieName().length() > 0);
	//
	// initComparator();
	// initViews();
	// initMenus();
	//
	// bindService();
	// initDB();
	//
	// initResults();
	//
	// }

	// /*
	// * (non-Javadoc)
	// *
	// * @see android.app.Activity#onDestroy()
	// */
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	//		Log.i(TAG, "onDestroy"); //$NON-NLS-1$
	// unbindService();
	// closeDB();
	// tracker.dispatch();
	// tracker.stop();
	// }
	//
	// @Override
	// protected void onPause() {
	// super.onPause();
	//		Log.i(TAG, "onPause"); //$NON-NLS-1$
	// if ((progressDialog != null) && progressDialog.isShowing()) {
	// progressDialog.dismiss();
	// }
	// }

	// @Override
	// protected void onResume() {
	// super.onResume();
	//		Log.i(TAG, "onResume"); //$NON-NLS-1$
	// initListeners();
	//
	// display();
	//
	// }

	// private void initResults() {
	// Intent intentResult = new Intent();
	// intentResult.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, model.isResetTheme());
	// intentResult.putExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, model.isNullResult());
	// setResult(CineShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY, intentResult);
	// }

	// /**
	// * init the view of activity
	// */
	// private void initViews() {
	//
	// resultList = (ExpandableListView) findViewById(R.id.resultListResult);
	//
	// // Manage Adapter
	// adapter = new CineShowTimeExpandableListAdapter(this, this);
	// }
	//
	// private void initListeners() {
	// resultList.setOnChildClickListener(this);
	// resultList.setOnGroupClickListener(this);
	// resultList.setOnGroupExpandListener(this);
	// resultList.setOnGroupCollapseListener(this);
	// }
	//
	// private void initMenus() {
	// registerForContextMenu(resultList);
	// }
	//
	// private void initComparator() {
	// String sort = prefs.getString(this.getResources().getString(R.string.preference_sort_key_sort_theater) //
	// , this.getResources().getString(R.string.preference_sort_default_sort_theater));
	// String[] values = getResources().getStringArray(R.array.sort_theaters_values_code);
	// int code = 0;
	// for (int i = 0; i < values.length; i++) {
	// if (values[i].equals(sort)) {
	// code = i;
	// break;
	// }
	// }
	//
	// if (!movieView) {
	// switch (code) {
	// case 0:
	// comparator = CineShowtimeFactory.getTheaterNameComparator();
	// break;
	// case 1:
	// comparator = CineShowtimeFactory.getTheaterDistanceComparator();
	// break;
	// case 2:
	// comparator = CineShowtimeFactory.getTheaterShowtimeComparator();
	// break;
	// default:
	// comparator = null;
	// break;
	// }
	// } else {
	// comparator = CineShowtimeFactory.getMovieNameComparator();
	//
	// }
	//
	// }
	//
	// protected void display() {
	// if (isServiceRunning()) {
	// openDialog();
	// } else {
	// NearResp nearResp = model.getNearResp();
	// if (nearResp != null) {
	// boolean error = false;
	// List<TheaterBean> theaterList = nearResp.getTheaterList();
	// if ((theaterList != null) && (theaterList.size() == 1)) {
	// TheaterBean errorTheater = theaterList.get(0);
	// if (errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_WRONG_DATE))//
	// || errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_WRONG_PLACE)) //
	// || errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_NO_DATA)) //
	// || errorTheater.getId().equals(String.valueOf(HttpParamsCst.ERROR_CUSTOM_MESSAGE)) //
	// ) {
	// error = true;
	// switch (Integer.valueOf(errorTheater.getId())) {
	// case HttpParamsCst.ERROR_WRONG_DATE:
	// errorTheater.setTheaterName(getResources().getString(R.string.msgNoDateMatch));
	// break;
	// case HttpParamsCst.ERROR_WRONG_PLACE:
	// errorTheater.setTheaterName(getResources().getString(R.string.msgNoPlaceMatch));
	// break;
	// case HttpParamsCst.ERROR_NO_DATA:
	// errorTheater.setTheaterName(getResources().getString(R.string.msgNoResultRetryLater));
	// break;
	// case HttpParamsCst.ERROR_CUSTOM_MESSAGE:
	// // Nothing to do special the custom message is in theaterTitle
	// break;
	//
	// default:
	// break;
	// }
	// }
	// } else if ((theaterList == null) || (theaterList.size() == 0)) {
	// error = true;
	// TheaterBean theaterZeroResp = new TheaterBean();
	// theaterZeroResp.setId(String.valueOf(HttpParamsCst.ERROR_NO_DATA));
	// theaterZeroResp.setTheaterName(getResources().getString(R.string.msgNoResultRetryLater));
	// if (theaterList == null) {
	// theaterList = new ArrayList<TheaterBean>();
	// nearResp.setTheaterList(theaterList);
	// }
	// theaterList.add(theaterZeroResp);
	// }
	// adapter.setTheaterList(nearResp, model.getTheaterFavList(), (CineShowtimeComparator<?>) comparator);
	// resultList.setAdapter(adapter);
	// if ((theaterList.size() == 1) && !error) {
	// resultList.expandGroup(0);
	// } else {
	// for (int i : model.getGroupExpanded()) {
	// resultList.expandGroup(i);
	// }
	// }
	// if ((nearResp != null) && (nearResp.getCityName() != null) && (nearResp.getCityName().length() > 0)) {
	// model.setCityName(nearResp.getCityName());
	// }
	// }
	// }
	//
	// }
	//
	// protected void changeComparator(CineShowtimeComparator<?> comparator) {
	// this.comparator = comparator;
	// movieView = comparator.getType() == comparator.COMPARATOR_MOVIE_NAME;
	// adapter.changeSort(comparator);
	// resultList.setAdapter(adapter);
	// }
	//
	// protected void launchNearService() throws UnsupportedEncodingException {
	// openDialog();
	//
	// launchSearchService();
	// }
	//
	// /**
	// *
	// */
	// protected void openDialog() {
	// progressDialog = ProgressDialog.show(CineShowTimeResultsTabletActivity.this, //
	// CineShowTimeResultsTabletActivity.this.getResources().getString(R.string.searchNearProgressTitle)//
	// , CineShowTimeResultsTabletActivity.this.getResources().getString(R.string.searchNearProgressMsg) //
	// , true, true, this);
	// }
	//
	// /**
	// * The call back message handler
	// */
	// public ServiceCallBackSearch m_callbackHandler = new ServiceCallBackSearch() {
	//
	// @Override
	// public void handleInputRecived() {
	//
	// try {
	// initResults();
	// display();
	// } catch (Exception e) {
	// Log.e(TAG, "Error during display", e);
	// }
	// if (progressDialog != null) {
	// progressDialog.dismiss();
	// }
	//
	// }
	//
	// };

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
		Log.i(TAG, "onCreateOptionsMenu"); //$NON-NLS-1$
		menu.add(0, MENU_SORT, 2, R.string.menuSort).setIcon(android.R.drawable.ic_menu_sort_by_size);
		super.onCreateOptionsMenu(menu);
		// CineShowTimeMenuUtil.createMenu(menu, MENU_PREF, 3);
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
		boolean menuItemSetlect = super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
		case MENU_SORT: {
			fragmentResult.openSortDialog();
			return true;
		}
		}
		return menuItemSetlect;
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// if (data != null) {
	// model.setNullResult(data.getBooleanExtra(ParamIntent.ACTIVITY_SEARCH_NULL_RESULT, false));
	// model.setResetTheme(data.getBooleanExtra(ParamIntent.PREFERENCE_RESULT_THEME, false));
	// } else {
	// model.setResetTheme(false);
	// model.setNullResult(false);
	// }
	//
	// initResults();
	//
	// if (requestCode == CineShowtimeCst.ACTIVITY_RESULT_PREFERENCES) {
	// adapter.changePreferences();
	//
	// }
	//
	// if (model.isResetTheme()) {
	// CineShowTimeLayoutUtils.changeToTheme(this, getIntent());
	// }
	//
	// }
	//
	// /*
	// *
	// * Events
	// */
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see android.widget.ExpandableListView.OnChildClickListener#onChildClick(android .widget.ExpandableListView, android.view.View, int, int, long)
	// */
	// @Override
	// public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
	//
	// ObjectSubView subView = (ObjectSubView) v;
	// TheaterBean theater = subView.getTheaterBean();
	// MovieBean movie = subView.getMovieBean();
	// tracker.trackEvent("Open", "Film", "Open from list", 0);
	// tracker.dispatch();
	// openMovieActivity(movie, theater);
	// return false;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see android.widget.ExpandableListView.OnGroupClickListener#onGroupClick(android .widget.ExpandableListView, android.view.View, int, long)
	// */
	// @Override
	// public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
	// switch (parent.getId()) {
	// case R.id.resultListResult: {
	// if ((model.getNearResp() != null) && (model.getNearResp().getTheaterList() != null)) {
	// int theaterListSize = model.getNearResp().getTheaterList().size();
	// if (theaterListSize == groupPosition) {
	// model.setStart(model.getStart() + 10);
	// try {
	// tracker.trackEvent("Resultats", "Click", "Search more theaters", 0);
	// launchNearService();
	// } catch (UnsupportedEncodingException e) {
	// // TODO
	// }
	// }
	// }
	// break;
	// }
	//
	// default:
	// break;
	// }
	// return false;
	// }
	//
	// @Override
	// public void sortSelected(int sourceID, int sortKey) {
	// tracker.trackEvent("Sort", "Click", "Click search Btn", sortKey);
	//
	// sourceLabel: switch (sourceID) {
	// case CineShowTimeResultsTabletActivity.ID_SORT: {
	// CineShowtimeComparator<?> comparator = null;
	// sortLabel: switch (sortKey) {
	// case CineShowtimeCst.SORT_THEATER_NAME:
	// comparator = CineShowtimeFactory.getTheaterNameComparator();
	// break sortLabel;
	// case CineShowtimeCst.SORT_THEATER_DISTANCE:
	// comparator = CineShowtimeFactory.getTheaterDistanceComparator();
	// break sortLabel;
	// case CineShowtimeCst.SORT_SHOWTIME:
	// comparator = CineShowtimeFactory.getTheaterShowtimeComparator();
	// break sortLabel;
	// case CineShowtimeCst.SORT_MOVIE_NAME:
	// comparator = CineShowtimeFactory.getMovieNameComparator();
	// break sortLabel;
	//
	// default:
	// comparator = CineShowtimeFactory.getTheaterNameComparator();
	// break sortLabel;
	// }
	// changeComparator(comparator);
	// break sourceLabel;
	// }
	// default:
	// break sourceLabel;
	// }
	//
	// }
	//
	// @Override
	// public void onClick(View v) {
	// ImageView imageViewFav = (ImageView) v;
	//
	// ObjectMasterView objectMasterView = (ObjectMasterView) imageViewFav.getParent().getParent();
	//
	// boolean isFav = objectMasterView.isFav();
	// TheaterBean theaterBean = objectMasterView.getTheaterBean();
	// if (isFav) {
	// tracker.trackEvent("Favoris", "Delete", "Delete from Results", 0);
	// removeFavorite(theaterBean);
	// } else {
	// tracker.trackEvent("Favoris", "Add", "Add from Results", 0);
	// addFavorite(theaterBean);
	// }
	// objectMasterView.toggleFav();
	//
	// }
	//
	// @Override
	// public void onGroupExpand(int groupPosition) {
	// model.getGroupExpanded().add(groupPosition);
	// }
	//
	// @Override
	// public void onGroupCollapse(int groupPosition) {
	// model.getGroupExpanded().remove(groupPosition);
	// }

	// @Override
	// public void onCancel(DialogInterface dialog) {
	// try {
	// serviceResult.cancelService();
	// } catch (RemoteException e) {
	// Log.e(TAG, "Error cancel service", e);
	// }
	// Intent intentResultService = new Intent(this, CineShowTimeResultsService.class);
	// stopService(intentResultService);
	// finish();
	// }

	// /*
	// *
	// * ACTIVITIES
	// */
	//
	// public void openMovieActivity(MovieBean movie, TheaterBean theater) {
	// if (movie != null) {
	// Intent intentStartMovieActivity = new Intent(this, CineShowTimeMovieActivity.class);
	//
	// intentStartMovieActivity.putExtra(ParamIntent.MOVIE_ID, movie.getId());
	// intentStartMovieActivity.putExtra(ParamIntent.MOVIE, movie);
	// intentStartMovieActivity.putExtra(ParamIntent.THEATER_ID, theater.getId());
	// intentStartMovieActivity.putExtra(ParamIntent.THEATER, theater);
	// intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LATITUDE, (model.getLocalisation() != null) ? model.getLocalisation().getLatitude() : null);
	// intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, (model.getLocalisation() != null) ? model.getLocalisation().getLongitude() : null);
	// StringBuilder place = new StringBuilder();
	// if (theater != null) {
	// if (theater.getPlace() != null) {
	// if ((theater.getPlace().getCityName() != null //
	// )
	// && (theater.getPlace().getCityName().length() > 0)) {
	// place.append(theater.getPlace().getCityName());
	// }
	// if ((theater.getPlace().getPostalCityNumber() != null //
	// )
	// && (theater.getPlace().getPostalCityNumber().length() > 0)) {
	// place.append(" ").append(theater.getPlace().getPostalCityNumber());
	// }
	// if ((theater.getPlace().getCountryNameCode() != null //
	// )
	// && (theater.getPlace().getCountryNameCode().length() > 0 //
	// ) && (place.length() > 0)) {
	//						place.append(", ").append(theater.getPlace().getCountryNameCode()); //$NON-NLS-1$
	// }
	// if (place.length() == 0) {
	// place.append(theater.getPlace().getSearchQuery());
	// }
	//
	// }
	// }
	// intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_NEAR, place.toString());
	// startActivityForResult(intentStartMovieActivity, CineShowtimeCst.ACTIVITY_RESULT_MOVIE_ACTIVITY);
	// }
	// }

	// public void launchSearchService() throws UnsupportedEncodingException {
	//
	// Location gpsLocation = model.getLocalisation();
	// String cityName = model.getCityName();
	// String movieName = model.getMovieName();
	// String theaterId = model.getFavTheaterId();
	// int day = model.getDay();
	// int start = model.getStart();
	//
	// if (mDbHelper.isOpen()) {
	// mDbHelper.createNearRequest(cityName //
	// , (gpsLocation != null) ? gpsLocation.getLatitude() : null //
	// , (gpsLocation != null) ? gpsLocation.getLongitude() : null //
	// , theaterId//
	// );
	// }
	// if ((cityName != null) && (cityName.length() > 0)) {
	// model.getRequestList().add(cityName);
	// }
	//
	// CineShowtimeFactory.initGeocoder(this);
	// Intent intentResultService = new Intent(this, CineShowTimeResultsService.class);
	//
	// intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
	// intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
	// intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, CineShowTimeEncodingUtil.getEncoding()) : cityName));
	// intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_MOVIE_NAME, ((movieName != null) ? URLEncoder.encode(movieName, CineShowTimeEncodingUtil.getEncoding()) : movieName));
	// intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_THEATER_ID, theaterId);
	// intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_DAY, day);
	// intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_START, start);
	//
	// startService(intentResultService);
	// }

	/*
	 * 
	 * DB
	 */

	// public void initDB() {
	//
	// try {
	// // openDB();
	//
	// boolean rerunService = model.isForceResearch();
	// if (mDbHelper.isOpen()) {
	//
	// List<TheaterBean> theaterFav = CineShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);
	// if (theaterFav != null) {
	// Map<String, TheaterBean> theaterFavList = new HashMap<String, TheaterBean>();
	// for (TheaterBean theater : theaterFav) {
	// theaterFavList.put(theater.getId(), theater);
	// }
	// model.setTheaterFavList(theaterFavList);
	// }
	// }
	// if (rerunService) {
	// try {
	// launchNearService();
	// } catch (UnsupportedEncodingException e) {
	//					Log.e(TAG, "error while rerun service", e); //$NON-NLS-1$
	// }
	// } else {
	//
	// NearResp nearResp = model.getNearResp();
	// if (nearResp == null) {
	// nearResp = new NearResp();
	// if (mDbHelper.isOpen()) {
	// nearResp.setTheaterList(CineShowtimeDB2AndShowtimeBeans.extractTheaterList(mDbHelper));
	// if ((nearResp.getTheaterList() != null) && !nearResp.getTheaterList().isEmpty()) {
	// nearResp.setMapMovies(CineShowtimeDB2AndShowtimeBeans.extractMovies(mDbHelper, nearResp.getTheaterList()));
	//							Log.i(TAG, "Datas found"); //$NON-NLS-1$
	// } else {
	//							Log.i(TAG, "No datas founds"); //$NON-NLS-1$
	// }
	// }
	// model.setNearResp(nearResp);
	// }
	//
	// if (nearResp != null) {
	// // We manage particular case of fav theaterResults :
	// // if previous result was just favorite => we have to relaunch the request
	// // if previous result was a full results => we have to filtered the results
	// if ((model.getTheaterFavList() != null) && !model.getTheaterFavList().isEmpty()) {
	// if ((nearResp.getTheaterList().size() == 1) && (model.getFavTheaterId() == null)) {
	// thLabel: for (String thFavId : model.getTheaterFavList().keySet()) {
	// for (TheaterBean thTmp : nearResp.getTheaterList()) {
	// if (thTmp.getId().equals(thFavId)) {
	// try {
	// launchNearService();
	// break thLabel;
	// } catch (UnsupportedEncodingException e) {
	//											Log.e(TAG, "error while rerun service", e); //$NON-NLS-1$
	// }
	// }
	// }
	// }
	// } else if ((model.getFavTheaterId() != null) && (nearResp.getTheaterList().size() > 0)) {
	// List<TheaterBean> filteredTheaterBean = new ArrayList<TheaterBean>();
	// for (TheaterBean thTmp : nearResp.getTheaterList()) {
	// if (thTmp.getId().equals(model.getFavTheaterId())) {
	// filteredTheaterBean.add(thTmp);
	// break;
	// }
	// }
	// Map<String, MovieBean> mapMovieFiltered = new HashMap<String, MovieBean>();
	// String theaterFavId = model.getFavTheaterId();
	// List<String> theaterIdListTmp = new ArrayList<String>();
	// theaterIdListTmp.add(theaterFavId);
	// for (Entry<String, MovieBean> entryMovieTmp : nearResp.getMapMovies().entrySet()) {
	// if (entryMovieTmp.getValue().getTheaterList() != null) {
	// if (entryMovieTmp.getValue().getTheaterList().contains(theaterFavId)) {
	// entryMovieTmp.getValue().setTheaterList(theaterIdListTmp);
	// mapMovieFiltered.put(entryMovieTmp.getKey(), entryMovieTmp.getValue());
	// }
	// }
	// }
	//
	// nearResp.setTheaterList(filteredTheaterBean);
	// nearResp.setMapMovies(mapMovieFiltered);
	// }
	// }
	//
	// }
	// }
	// } catch (SQLException e) {
	//			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
	// }
	// }
	//
	// @Override
	// public void closeDB() {
	// try {
	// if (mDbHelper.isOpen()) {
	//				Log.i(TAG, "Close DB"); //$NON-NLS-1$
	// mDbHelper.close();
	// }
	// } catch (Exception e) {
	//			Log.e(TAG, "error onDestroy of movie Activity", e); //$NON-NLS-1$
	// }
	// }
	//
	// public void addFavorite(TheaterBean theaterBean) {
	// try {
	// if (LocationUtils.isEmptyLocation(theaterBean.getPlace())) {
	// LocalisationBean place = theaterBean.getPlace();
	// if (place == null) {
	// place = new LocalisationBean();
	// theaterBean.setPlace(place);
	// }
	// place.setCityName(model.getCityName());
	// }
	// model.getTheaterFavList().put(theaterBean.getId(), theaterBean);
	// Intent service = new Intent(this, CineShowDBGlobalService.class);
	// service.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_FAV_WRITE);
	// service.putExtra(ParamIntent.SERVICE_DB_DATA, theaterBean);
	// startService(service);
	// } catch (Exception e) {
	// Log.e(TAG, "error putting data into data base", e);
	// }
	//
	// }
	//
	// public void removeFavorite(TheaterBean theaterBean) {
	// try {
	// model.getTheaterFavList().remove(theaterBean);
	// Intent service = new Intent(this, CineShowDBGlobalService.class);
	// service.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_FAV_DELETE);
	// service.putExtra(ParamIntent.SERVICE_DB_DATA, theaterBean);
	// startService(service);
	// } catch (Exception e) {
	// Log.e(TAG, "error removing theater from fav", e);
	// }
	//
	// }
	//
	// public List<TheaterBean> getFavTheater() {
	// List<TheaterBean> theaterList = CineShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);
	//
	// return theaterList;
	// }
	//
	// /*
	// *
	// * CALL BACK SERVICE
	// */
	//
	// public void bindService() {
	// bindService(new Intent(this, CineShowTimeResultsService.class), mConnection, Context.BIND_AUTO_CREATE);
	// }
	//
	// public void unbindService() {
	// try {
	// serviceResult.unregisterCallback(m_callback);
	// unbindService(mConnection);
	// } catch (Exception e) {
	// Log.e(TAG, "error while unbinding service", e);
	// }
	// }
	//
	// /**
	// * The service connection inteface with our binded service {@link http ://code .google.com/android/reference/android/content/ServiceConnection.html}
	// */
	// private ServiceConnection mConnection = new ServiceConnection() {
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// serviceResult = IServiceSearch.Stub.asInterface(service);
	//
	// try {
	// serviceResult.registerCallback(m_callback);
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	//
	// }
	//
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	//
	// }
	//
	// };
	//
	// protected boolean isServiceRunning() {
	// if (serviceResult != null) {
	// try {
	// return serviceResult.isServiceRunning();
	// } catch (RemoteException e) {
	//				Log.e(TAG, "Eror during checking service", e); //$NON-NLS-1$
	// }
	// }
	// return false;
	// }
	//
	// /**
	// * The callback object that will return from the service
	// */
	// private ICallbackSearch m_callback = new ICallbackSearch.Stub() {
	//
	// @Override
	// public void finish() throws RemoteException {
	//
	// Location gpsLocation = model.getLocalisation();
	// String cityName = model.getCityName();
	// String movieName = model.getMovieName();
	// String theaterId = model.getFavTheaterId();
	//
	// NearResp nearResp = serviceResult.getNearResp();
	// if (model.getStart() > 0) {
	// // We have to complete the result with previous entries
	// NearResp lastNearResp = model.getNearResp();
	// if (lastNearResp != null) {
	// lastNearResp.setHasMoreResults(nearResp.isHasMoreResults());
	// lastNearResp.getTheaterList().addAll(nearResp.getTheaterList());
	// for (String movieId : nearResp.getMapMovies().keySet()) {
	// if (!lastNearResp.getMapMovies().containsKey(movieId)) {
	// lastNearResp.getMapMovies().put(movieId, nearResp.getMapMovies().get(movieId));
	// }
	// }
	// }
	// nearResp = lastNearResp;
	// }
	// model.setNearResp(nearResp);
	//
	// if (mDbHelper.isOpen()) {
	// mDbHelper.createMovieRequest(cityName //
	// , movieName //
	// , (gpsLocation != null) ? gpsLocation.getLatitude() : null //
	// , (gpsLocation != null) ? gpsLocation.getLongitude() : null //
	// , theaterId//
	// , nearResp == null //
	// );
	// }
	//
	// model.setNullResult(nearResp == null);
	// m_callbackHandler.sendInputRecieved();
	//
	// }
	//
	// @Override
	// public void finishLocation(String theaterId) throws RemoteException {
	//
	// if ((model.getNearResp() != null) && (model.getNearResp().getTheaterList() != null)) {
	// for (TheaterBean theaterBean : model.getNearResp().getTheaterList()) {
	// if (theaterId.equals(theaterBean.getId())) {
	// LocalisationBean localisation = serviceResult.getLocalisation(theaterId);
	// theaterBean.setPlace(localisation);
	// // TODO
	// break;
	// }
	// }
	// }
	//
	// }
	//
	// };

	private void extendList() {

		if (hideRight) {
			// We move the info to the right
			TranslateAnimation animation = new TranslateAnimation(infoLayout.getLeft(), infoLayout.getLeft() + dist, 0, 0);
			animation = new TranslateAnimation(0, dist, 0, 0);
			animation.setStartOffset(0);// layoutRight.getLeft());
			animation.setDuration(delay);
			animation.setFillAfter(true);
			infoLayout.startAnimation(animation);

			fragmentResult.changeAdapter(true);

			paramsLeft.width = widthLeftFull;
			fragmentResult.getView().setLayoutParams(paramsLeft);

			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					btnExpand.setImageResource(R.drawable.ic_btn_find_prev);
				}
			}, delay + 200);
		} else {
			// We move the info to initial position
			TranslateAnimation animation = new TranslateAnimation(infoLayout.getLeft() + dist, infoLayout.getLeft(), 0, 0);
			animation = new TranslateAnimation(dist, 0, 0, 0);
			animation.setStartOffset(0);// layoutRight.getLeft());
			animation.setDuration(500);
			animation.setFillAfter(true);
			infoLayout.startAnimation(animation);

			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					btnExpand.setImageResource(R.drawable.ic_btn_find_next);
					paramsLeft.width = widthLeftLight;
					fragmentResult.getView().setLayoutParams(paramsLeft);
					fragmentResult.changeAdapter(false);
				}
			}, delay + 200);
		}
	}

	/*
	 * OverRide methods
	 */

	@Override
	protected int getMenuKey() {
		return MENU_PREF;
	}

	@Override
	protected int getLayout() {
		return R.layout.activity_result;
	}

	@Override
	protected String getTrackerName() {
		return TRACKER_NAME;
	}

	@Override
	protected String getTAG() {
		return TAG;
	}

	@Override
	protected void initContentView() {
		fragmentResult = (CineShowTimeResultsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentResults);
		fragmentFrame = new CineShowTimeFrameFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.fragmentInfo, fragmentFrame).commit();
		// fragmentMovie = (CineShowTimeMovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentInfo);

		// We check if we're in portrait mode in order to manage specific expand
		Configuration conf = getResources().getConfiguration();
		portraitMode = conf.orientation == Configuration.ORIENTATION_PORTRAIT;
		if (portraitMode) {
			hideRight = false;
			btnExpand = (ImageButton) findViewById(R.id.btnExpand);
			frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
			infoLayout = (LinearLayout) findViewById(R.id.fragmentInfo);
			frameLayout.setVisibility(View.INVISIBLE);

			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					paramsLeft = (android.widget.FrameLayout.LayoutParams) fragmentResult.getView().getLayoutParams();
					paramsRight = (android.widget.FrameLayout.LayoutParams) infoLayout.getLayoutParams();

					int totalWidth = frameLayout.getWidth();
					widthLeftFull = Double.valueOf(totalWidth * 0.50).intValue();
					widthLeftLight = Double.valueOf(totalWidth * 0.30).intValue();
					dist = widthLeftFull - widthLeftLight;
					int widthRight = Double.valueOf(totalWidth * 0.70).intValue();

					paramsLeft.width = widthLeftLight;
					paramsRight.width = widthRight;
					paramsRight.gravity = Gravity.RIGHT;

					fragmentResult.getView().setLayoutParams(paramsLeft);
					infoLayout.setLayoutParams(paramsRight);
					frameLayout.setVisibility(View.VISIBLE);
				}
			}, delay);

			btnExpand.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					hideRight = !hideRight;
					extendList();
					fragmentResult.requestFocus();

				}
			});

			fragmentResult.setOnFocusListener(new View.OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean focus) {
					if (!focus && hideRight) {
						hideRight = false;
						extendList();
					} else if (focus && !hideRight) {
						hideRight = true;
						extendList();
					}
				}
			});
		}
	}

	@Override
	protected IModelResultTablet getModel() {
		return new ModelResultTablet();
	}

	@Override
	protected void doOnCancel() {
		fragmentResult.onCancel();
	}

	@Override
	protected void doChangeFromPref() {
		fragmentResult.changePreferences();

	}

	@Override
	protected int getDialogTitle() {
		return R.string.searchNearProgressTitle;
	}

	@Override
	protected int getDialogMsg() {
		return R.string.searchNearProgressMsg;
	}

	/*
	 * 
	 * Fragment Result interaction
	 */

	@Override
	public void openMovieScreen(MovieBean movie, TheaterBean theater) {
		if (movie != null) {
			intentStartMovieActivity = new Intent(this, CineShowTimeMovieActivity.class);

			intentStartMovieActivity.putExtra(ParamIntent.MOVIE_ID, movie.getId());
			intentStartMovieActivity.putExtra(ParamIntent.MOVIE, movie);
			intentStartMovieActivity.putExtra(ParamIntent.THEATER_ID, theater.getId());
			intentStartMovieActivity.putExtra(ParamIntent.THEATER, theater);
			intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LATITUDE, (model.getLocalisation() != null) ? model.getLocalisation().getLatitude() : null);
			intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, (model.getLocalisation() != null) ? model.getLocalisation().getLongitude() : null);
			StringBuilder place = new StringBuilder();
			if (theater != null) {
				if (theater.getPlace() != null) {
					if ((theater.getPlace().getCityName() != null //
							)
							&& (theater.getPlace().getCityName().length() > 0)) {
						place.append(theater.getPlace().getCityName());
					}
					if ((theater.getPlace().getPostalCityNumber() != null //
							)
							&& (theater.getPlace().getPostalCityNumber().length() > 0)) {
						place.append(" ").append(theater.getPlace().getPostalCityNumber());
					}
					if ((theater.getPlace().getCountryNameCode() != null //
							)
							&& (theater.getPlace().getCountryNameCode().length() > 0 //
							) && (place.length() > 0)) {
						place.append(", ").append(theater.getPlace().getCountryNameCode()); //$NON-NLS-1$
					}
					if (place.length() == 0) {
						place.append(theater.getPlace().getSearchQuery());
					}

				}
			}
			intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_NEAR, place.toString());
			// startActivityForResult(intentStartMovieActivity, CineShowtimeCst.ACTIVITY_RESULT_MOVIE_ACTIVITY);
			fragmentMovie = new CineShowTimeMovieFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.fragmentInfo, fragmentMovie).commit();
		}

	}

	/*
	 * Fragment Movie interaction
	 */

	@Override
	public Intent getIntentMovie() {
		return intentStartMovieActivity;
	}

}