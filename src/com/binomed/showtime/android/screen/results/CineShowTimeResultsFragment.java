package com.binomed.showtime.android.screen.results;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.SQLException;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.adapter.view.CineShowTimeExpandableListAdapter;
import com.binomed.showtime.android.adapter.view.CineShowTimeNonExpandableListAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.handler.ServiceCallBackSearch;
import com.binomed.showtime.android.layout.dialogs.sort.ListDialog;
import com.binomed.showtime.android.layout.dialogs.sort.ListSelectionListener;
import com.binomed.showtime.android.layout.view.ObjectMasterView;
import com.binomed.showtime.android.layout.view.ObjectSubView;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.service.CineShowDBGlobalService;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.android.util.CineShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.CineShowtimeFactory;
import com.binomed.showtime.android.util.activity.IFragmentCineShowTimeInteraction;
import com.binomed.showtime.android.util.comparator.CineShowtimeComparator;
import com.binomed.showtime.android.util.localisation.LocationUtils;
import com.binomed.showtime.cst.HttpParamsCst;

public class CineShowTimeResultsFragment extends Fragment implements OnChildClickListener //
		, OnGroupClickListener //
		, OnItemClickListener //
		, OnGroupExpandListener //
		, OnGroupCollapseListener //
		, OnFocusChangeListener //
		, ListSelectionListener //
		, OnClickListener//
		, OnCancelListener //
{

	protected static final int ID_SORT = 1;

	private static final String TAG = "ResultsActivity"; //$NON-NLS-1$

	protected ExpandableListView resultList;
	protected ListView resultListNonExpandable;
	protected CineShowTimeExpandableListAdapter adapter = null;
	protected CineShowTimeNonExpandableListAdapter adapterNonExpendable = null;
	private CineShowTimeResultInteraction<? extends IModelResults> interaction;

	protected boolean movieView;

	protected Comparator<?> comparator;
	private IServiceSearch serviceResult;

	private IModelResults model;
	private CineShowtimeDbAdapter mDbHelper;
	private View mainView;

	private boolean nonExpendable = false;
	private Intent intent;

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragment_results, container, false);
		initViews(mainView);
		initMenus();

		bindService();
		initDB();
		return mainView;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// We init the theater id if set
		model = interaction.getModelActivity();
		mDbHelper = interaction.getMDbHelper();

		if (intent == null) {
			intent = getActivity().getIntent();
		}

		// if ((savedInstanceState != null) && savedInstanceState.getBoolean(ParamIntent.BUNDLE_SAVE, false)) {
		// model.setNearResp((NearResp) savedInstanceState.getParcelable(ParamIntent.NEAR_RESP));
		// }

		model.setForceResearch(intent.getBooleanExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, true));
		intent.putExtra(ParamIntent.ACTIVITY_SEARCH_FORCE_REQUEST, false);
		model.setFavTheaterId(intent.getStringExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID));
		model.setLocalisation(null);
		model.setDay(intent.getIntExtra(ParamIntent.ACTIVITY_SEARCH_DAY, 0));
		model.setCityName(intent.getStringExtra(ParamIntent.ACTIVITY_SEARCH_CITY));
		model.setMovieName(intent.getStringExtra(ParamIntent.ACTIVITY_SEARCH_MOVIE_NAME));
		Double latitude = intent.getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LATITUDE, 0);
		Double longitude = intent.getDoubleExtra(ParamIntent.ACTIVITY_SEARCH_LONGITUDE, 0);
		if ((latitude != 0) && (longitude != 0)) {
			Location locationTheater = new Location("GPS");
			locationTheater.setLatitude(latitude);
			locationTheater.setLongitude(longitude);
			model.setLocalisation(locationTheater);
		}
		intent.putExtra(ParamIntent.ACTIVITY_SEARCH_THEATER_ID, "");

		movieView = (model.getMovieName() != null) && (model.getMovieName().length() > 0);

		initComparator();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		interaction = (CineShowTimeResultInteraction) activity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy"); //$NON-NLS-1$
		unbindService();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "onResume"); //$NON-NLS-1$
		initListeners();

		display();

	}

	// @Override
	// public void onSaveInstanceState(Bundle outState) {
	// if (!isServiceRunning()) {
	// NearResp nearResp = model.getNearResp();
	// outState.putBoolean(ParamIntent.BUNDLE_SAVE, true);
	// outState.putParcelable(ParamIntent.NEAR_RESP, nearResp);
	// }
	// super.onSaveInstanceState(outState);
	// }

	/**
	 * init the view of activity
	 */
	private void initViews(View mainView) {

		resultList = (ExpandableListView) mainView.findViewById(R.id.resultListResult);
		resultListNonExpandable = (ListView) mainView.findViewById(R.id.resultListResultNonExpandable);

		resultList.setVisibility(nonExpendable ? View.GONE : View.VISIBLE);
		resultListNonExpandable.setVisibility(nonExpendable ? View.VISIBLE : View.GONE);

		// Manage Adapter
		adapter = new CineShowTimeExpandableListAdapter(getActivity(), this);
		adapterNonExpendable = new CineShowTimeNonExpandableListAdapter(getActivity(), this);
	}

	private void initListeners() {
		resultListNonExpandable.setOnItemClickListener(this);
		resultList.setOnChildClickListener(this);
		resultList.setOnGroupClickListener(this);
		resultList.setOnGroupExpandListener(this);
		resultList.setOnGroupCollapseListener(this);
		resultList.setOnFocusChangeListener(this);

	}

	private void initMenus() {
		registerForContextMenu(resultList);
	}

	private void initComparator() {
		String sort = interaction.getPrefs().getString(this.getResources().getString(R.string.preference_sort_key_sort_theater) //
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
				comparator = CineShowtimeFactory.getTheaterNameComparator();
				break;
			case 1:
				comparator = CineShowtimeFactory.getTheaterDistanceComparator();
				break;
			case 2:
				comparator = CineShowtimeFactory.getTheaterShowtimeComparator();
				break;
			default:
				comparator = null;
				break;
			}
		} else {
			comparator = CineShowtimeFactory.getMovieNameComparator();

		}

	}

	protected void display() {
		if (isServiceRunning()) {
			interaction.openDialog();
		} else {
			NearResp nearResp = model.getNearResp();
			if (nearResp != null) {
				boolean error = false;
				List<TheaterBean> theaterList = nearResp.getTheaterList();
				if ((theaterList != null) && (theaterList.size() == 1)) {
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
				} else if ((theaterList == null) || (theaterList.size() == 0)) {
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
				adapter.setTheaterList(nearResp, model.getTheaterFavList(), (CineShowtimeComparator<?>) comparator);
				if (nonExpendable) {
					adapterNonExpendable.setTheaterList(nearResp, model.getTheaterFavList(), (CineShowtimeComparator<?>) comparator);
					resultListNonExpandable.setAdapter(adapterNonExpendable);
				} else {
					resultList.setAdapter(adapter);

				}
				if ((theaterList.size() == 1) && !error) {
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

	protected void changeComparator(CineShowtimeComparator<?> comparator) {
		this.comparator = comparator;
		movieView = comparator.getType() == comparator.COMPARATOR_MOVIE_NAME;
		adapter.changeSort(comparator);
		if (!nonExpendable) {
			resultList.setAdapter(adapter);
		}
	}

	protected void launchNearService() throws UnsupportedEncodingException {
		interaction.openDialog();

		launchSearchService();
	}

	/**
	 * The call back message handler
	 */
	public ServiceCallBackSearch m_callbackHandler = new ServiceCallBackSearch() {

		@Override
		public void handleInputRecived() {

			try {
				unregisterCallBack();
				display();
			} catch (Exception e) {
				Log.e(TAG, "Error during display", e);
			}
			interaction.closeDialog();

		}

	};

	/*
	 * 
	 * Events
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListView.OnChildClickListener#onChildClick(android .widget.ExpandableListView, android.view.View, int, int, long)
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

		ObjectSubView subView = (ObjectSubView) v;
		TheaterBean theater = subView.getTheaterBean();
		MovieBean movie = subView.getMovieBean();
		interaction.getTracker().trackEvent("Open", "Film", "Open from list", 0);
		interaction.getTracker().dispatch();
		// openMovieActivity(movie, theater);
		interaction.openMovieScreen(movie, theater);
		interaction.onChildClick();
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListView.OnGroupClickListener#onGroupClick(android .widget.ExpandableListView, android.view.View, int, long)
	 */
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		switch (parent.getId()) {
		case R.id.resultListResult: {
			if ((model.getNearResp() != null) && (model.getNearResp().getTheaterList() != null)) {
				int theaterListSize = model.getNearResp().getTheaterList().size();
				if (theaterListSize == groupPosition) {
					model.setStart(model.getStart() + 10);
					try {
						interaction.getTracker().trackEvent("Resultats", "Click", "Search more theaters", 0);
						launchNearService();
					} catch (UnsupportedEncodingException e) {
						// TODO
					}
				} else {
					interaction.onTheaterClick(model.getNearResp().getTheaterList().get(groupPosition));
				}
			}
			interaction.onGroupClick();
			break;
		}

		default:
			break;
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		switch (arg0.getId()) {
		case R.id.resultListResultNonExpandable: {
			if ((model.getNearResp() != null) && (model.getNearResp().getTheaterList() != null)) {
				int theaterListSize = model.getNearResp().getTheaterList().size();
				if (theaterListSize == position) {
					model.setStart(model.getStart() + 10);
					try {
						interaction.getTracker().trackEvent("Resultats", "Click", "Search more theaters", 0);
						launchNearService();
					} catch (UnsupportedEncodingException e) {
						// TODO
					}
				} else {
					interaction.onTheaterClick(model.getNearResp().getTheaterList().get(position));
				}
			}
			break;
		}

		default:
			break;
		}

	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		interaction.onFocusListener(arg1);
	}

	@Override
	public void sortSelected(int sourceID, int sortKey) {
		interaction.getTracker().trackEvent("Sort", "Click", "Click search Btn", sortKey);

		sourceLabel: switch (sourceID) {
		case CineShowTimeResultsFragment.ID_SORT: {
			CineShowtimeComparator<?> comparator = null;
			sortLabel: switch (sortKey) {
			case CineShowtimeCst.SORT_THEATER_NAME:
				comparator = CineShowtimeFactory.getTheaterNameComparator();
				break sortLabel;
			case CineShowtimeCst.SORT_THEATER_DISTANCE:
				comparator = CineShowtimeFactory.getTheaterDistanceComparator();
				break sortLabel;
			case CineShowtimeCst.SORT_SHOWTIME:
				comparator = CineShowtimeFactory.getTheaterShowtimeComparator();
				break sortLabel;
			case CineShowtimeCst.SORT_MOVIE_NAME:
				comparator = CineShowtimeFactory.getMovieNameComparator();
				break sortLabel;

			default:
				comparator = CineShowtimeFactory.getTheaterNameComparator();
				break sortLabel;
			}
			changeComparator(comparator);
			break sourceLabel;
		}
		default:
			break sourceLabel;
		}

	}

	@Override
	public void onClick(View v) {
		ImageView imageViewFav = (ImageView) v;

		ObjectMasterView objectMasterView = (ObjectMasterView) imageViewFav.getParent().getParent();

		boolean isFav = objectMasterView.isFav();
		TheaterBean theaterBean = objectMasterView.getTheaterBean();
		if (isFav) {
			interaction.getTracker().trackEvent("Favoris", "Delete", "Delete from Results", 0);
			removeFavorite(theaterBean);
		} else {
			interaction.getTracker().trackEvent("Favoris", "Add", "Add from Results", 0);
			addFavorite(theaterBean);
		}
		objectMasterView.toggleFav();

	}

	@Override
	public void onGroupExpand(int groupPosition) {
		model.getGroupExpanded().add(groupPosition);
	}

	@Override
	public void onGroupCollapse(int groupPosition) {
		model.getGroupExpanded().remove(groupPosition);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		try {
			serviceResult.cancelService();
		} catch (RemoteException e) {
			Log.e(TAG, "Error cancel service", e);
		}
		Intent intentResultService = new Intent(getActivity(), CineShowTimeResultsService.class);
		getActivity().stopService(intentResultService);
		// finish(); TODO
	}

	/*
	 * 
	 * ACTIVITIES
	 */

	public void setNonExpendable(boolean nonExpandable) {
		this.nonExpendable = nonExpandable;
	}

	public void setIntentResult(Intent intent) {
		this.intent = intent;
	}

	public void launchSearchService() throws UnsupportedEncodingException {

		Location gpsLocation = model.getLocalisation();
		String cityName = model.getCityName();
		String movieName = model.getMovieName();
		String theaterId = model.getFavTheaterId();
		int day = model.getDay();
		int start = model.getStart();

		if (mDbHelper.isOpen()) {
			mDbHelper.createNearRequest(cityName //
					, (gpsLocation != null) ? gpsLocation.getLatitude() : null //
					, (gpsLocation != null) ? gpsLocation.getLongitude() : null //
					, theaterId//
					);
		}
		if ((cityName != null) && (cityName.length() > 0)) {
			model.getRequestList().add(cityName);
		}

		CineShowtimeFactory.initGeocoder(getActivity());
		Intent intentResultService = new Intent(getActivity(), CineShowTimeResultsService.class);

		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_LATITUDE, (gpsLocation != null) ? gpsLocation.getLatitude() : null);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_LONGITUDE, (gpsLocation != null) ? gpsLocation.getLongitude() : null);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_CITY, ((cityName != null) ? URLEncoder.encode(cityName, CineShowTimeEncodingUtil.getEncoding()) : cityName));
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_MOVIE_NAME, ((movieName != null) ? URLEncoder.encode(movieName, CineShowTimeEncodingUtil.getEncoding()) : movieName));
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_THEATER_ID, theaterId);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_DAY, day);
		intentResultService.putExtra(ParamIntent.SERVICE_SEARCH_START, start);

		getActivity().startService(intentResultService);
	}

	/*
	 * 
	 * DB
	 */

	public void openDB() {

		try {
			Log.i(TAG, "openDB"); //$NON-NLS-1$
			mDbHelper = new CineShowtimeDbAdapter(getActivity());
			mDbHelper.open();
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void initDB() {

		try {
			// openDB();

			boolean rerunService = model.isForceResearch();
			if (mDbHelper.isOpen()) {

				List<TheaterBean> theaterFav = CineShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);
				if (theaterFav != null) {
					Map<String, TheaterBean> theaterFavList = new HashMap<String, TheaterBean>();
					for (TheaterBean theater : theaterFav) {
						theaterFavList.put(theater.getId(), theater);
					}
					model.setTheaterFavList(theaterFavList);
				}
			}
			if (rerunService) {
				try {
					launchNearService();
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "error while rerun service", e); //$NON-NLS-1$
				}
			} else {

				NearResp nearResp = model.getNearResp();
				if (nearResp == null) {
					nearResp = new NearResp();
					if (mDbHelper.isOpen()) {
						nearResp.setTheaterList(CineShowtimeDB2AndShowtimeBeans.extractTheaterList(mDbHelper));
						if ((nearResp.getTheaterList() != null) && !nearResp.getTheaterList().isEmpty()) {
							nearResp.setMapMovies(CineShowtimeDB2AndShowtimeBeans.extractMovies(mDbHelper, nearResp.getTheaterList()));
							Log.i(TAG, "Datas found"); //$NON-NLS-1$
						} else {
							Log.i(TAG, "No datas founds"); //$NON-NLS-1$
						}
					}
					model.setNearResp(nearResp);
				}

				if (nearResp != null) {
					// We manage particular case of fav theaterResults :
					// if previous result was just favorite => we have to relaunch the request
					// if previous result was a full results => we have to filtered the results
					if ((model.getTheaterFavList() != null) && !model.getTheaterFavList().isEmpty()) {
						if ((nearResp.getTheaterList().size() == 1) && (model.getFavTheaterId() == null)) {
							thLabel: for (String thFavId : model.getTheaterFavList().keySet()) {
								for (TheaterBean thTmp : nearResp.getTheaterList()) {
									if (thTmp.getId().equals(thFavId)) {
										try {
											launchNearService();
											break thLabel;
										} catch (UnsupportedEncodingException e) {
											Log.e(TAG, "error while rerun service", e); //$NON-NLS-1$
										}
									}
								}
							}
						} else if ((model.getFavTheaterId() != null) && (nearResp.getTheaterList().size() > 0)) {
							List<TheaterBean> filteredTheaterBean = new ArrayList<TheaterBean>();
							for (TheaterBean thTmp : nearResp.getTheaterList()) {
								if (thTmp.getId().equals(model.getFavTheaterId())) {
									filteredTheaterBean.add(thTmp);
									break;
								}
							}
							Map<String, MovieBean> mapMovieFiltered = new HashMap<String, MovieBean>();
							String theaterFavId = model.getFavTheaterId();
							List<String> theaterIdListTmp = new ArrayList<String>();
							theaterIdListTmp.add(theaterFavId);
							for (Entry<String, MovieBean> entryMovieTmp : nearResp.getMapMovies().entrySet()) {
								if (entryMovieTmp.getValue().getTheaterList() != null) {
									if (entryMovieTmp.getValue().getTheaterList().contains(theaterFavId)) {
										entryMovieTmp.getValue().setTheaterList(theaterIdListTmp);
										mapMovieFiltered.put(entryMovieTmp.getKey(), entryMovieTmp.getValue());
									}
								}
							}

							nearResp.setTheaterList(filteredTheaterBean);
							nearResp.setMapMovies(mapMovieFiltered);
						}
					}

				}
			}
		} catch (SQLException e) {
			Log.e(TAG, "error during getting fetching informations", e); //$NON-NLS-1$
		}
	}

	public void addFavorite(TheaterBean theaterBean) {
		try {
			if (LocationUtils.isEmptyLocation(theaterBean.getPlace())) {
				LocalisationBean place = theaterBean.getPlace();
				if (place == null) {
					place = new LocalisationBean();
					theaterBean.setPlace(place);
				}
				place.setCityName(model.getCityName());
			}
			model.getTheaterFavList().put(theaterBean.getId(), theaterBean);
			Intent service = new Intent(getActivity(), CineShowDBGlobalService.class);
			service.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_FAV_WRITE);
			service.putExtra(ParamIntent.SERVICE_DB_DATA, theaterBean);
			getActivity().startService(service);
		} catch (Exception e) {
			Log.e(TAG, "error putting data into data base", e);
		}

	}

	public void removeFavorite(TheaterBean theaterBean) {
		try {
			model.getTheaterFavList().remove(theaterBean);
			Intent service = new Intent(getActivity(), CineShowDBGlobalService.class);
			service.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_FAV_DELETE);
			service.putExtra(ParamIntent.SERVICE_DB_DATA, theaterBean);
			getActivity().startService(service);
		} catch (Exception e) {
			Log.e(TAG, "error removing theater from fav", e);
		}

	}

	public List<TheaterBean> getFavTheater() {
		List<TheaterBean> theaterList = CineShowtimeDB2AndShowtimeBeans.extractFavTheaterList(mDbHelper);

		return theaterList;
	}

	/*
	 * 
	 * CALL BACK SERVICE
	 */

	private void bindService() {
		getActivity().bindService(new Intent(getActivity(), CineShowTimeResultsService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	private void unbindService() {
		try {
			unregisterCallBack();
			getActivity().unbindService(mConnection);
		} catch (Exception e) {
			Log.e(TAG, "error while unbinding service", e);
		}
	}

	private void unregisterCallBack() {
		try {
			if (serviceResult != null) {
				serviceResult.unregisterCallback(m_callback);
			}
		} catch (Exception e) {
			Log.e(TAG, "error while unbinding service", e);
		}
	}

	/**
	 * The service connection inteface with our binded service {@link http ://code .google.com/android/reference/android/content/ServiceConnection.html}
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceResult = IServiceSearch.Stub.asInterface(service);
			try {
				if (serviceResult != null) {
					serviceResult.registerCallback(m_callback);
				}
			} catch (Exception e) {
				Log.e(TAG, "error while unbinding service", e);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	protected boolean isServiceRunning() {
		if (serviceResult != null) {
			try {
				return serviceResult.isServiceRunning();
			} catch (RemoteException e) {
				Log.e(TAG, "Eror during checking service", e); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * The callback object that will return from the service
	 */
	private ICallbackSearch m_callback = new ICallbackSearch.Stub() {

		@Override
		public void finish() throws RemoteException {

			Location gpsLocation = model.getLocalisation();
			String cityName = model.getCityName();
			String movieName = model.getMovieName();
			String theaterId = model.getFavTheaterId();

			NearResp nearResp = serviceResult.getNearResp();
			if (model.getStart() > 0) {
				// We have to complete the result with previous entries
				NearResp lastNearResp = model.getNearResp();
				if (lastNearResp != null) {
					lastNearResp.setHasMoreResults(nearResp.isHasMoreResults());
					lastNearResp.getTheaterList().addAll(nearResp.getTheaterList());
					for (String movieId : nearResp.getMapMovies().keySet()) {
						if (!lastNearResp.getMapMovies().containsKey(movieId)) {
							lastNearResp.getMapMovies().put(movieId, nearResp.getMapMovies().get(movieId));
						}
					}
				}
				nearResp = lastNearResp;
			}
			model.setNearResp(nearResp);

			if (mDbHelper.isOpen()) {
				mDbHelper.createMovieRequest(cityName //
						, movieName //
						, (gpsLocation != null) ? gpsLocation.getLatitude() : null //
						, (gpsLocation != null) ? gpsLocation.getLongitude() : null //
						, theaterId//
						, nearResp == null //
						);
			}

			model.setNullResult(nearResp == null);
			m_callbackHandler.sendInputRecieved();

		}

		@Override
		public void finishLocation(String theaterId) throws RemoteException {

			if ((model.getNearResp() != null) && (model.getNearResp().getTheaterList() != null)) {
				for (TheaterBean theaterBean : model.getNearResp().getTheaterList()) {
					if (theaterId.equals(theaterBean.getId())) {
						LocalisationBean localisation = serviceResult.getLocalisation(theaterId);
						theaterBean.setPlace(localisation);
						// TODO
						break;
					}
				}
			}

		}

	};

	public interface CineShowTimeResultInteraction<M extends IModelResults> extends IFragmentCineShowTimeInteraction<M> {

		void openMovieScreen(MovieBean movie, TheaterBean theater);

		void onGroupClick();

		void onTheaterClick(TheaterBean theater);

		void onChildClick();

		void onFocusListener(boolean focus);

	}

	/*
	 * 
	 * Fragment interaction methods
	 */

	public void changePreferences() {
		adapter.changePreferences();
	}

	public void openSortDialog() {
		ListDialog dialog = new ListDialog(//
				getActivity() // Context
				, this // ListSelectionListener
				, R.array.sort_theaters_values //
				, ID_SORT //
		);
		dialog.setTitle(getActivity().getResources().getString(R.string.sortDialogTitle));
		dialog.setFeatureDrawableResource(0, android.R.drawable.ic_menu_sort_by_size);
		dialog.show();

	}

	public void onCancel() {
		try {
			serviceResult.cancelService();
		} catch (RemoteException e) {
			Log.e(TAG, "Error cancel service", e);
		}
		Intent intentResultService = new Intent(getActivity(), CineShowTimeResultsService.class);
		getActivity().stopService(intentResultService);
	}

	public void changeAdapter(boolean full) {
		adapter.setLightFormat(!full);
		adapter.notifyDataSetChanged();
	}

	public void requestFocus() {
		resultList.requestFocus();
	}

}