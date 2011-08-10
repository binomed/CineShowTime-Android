package com.binomed.showtime.android.screen.movie;

import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.adapter.view.MoviePagedAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.handler.MovieCallBackMovie;
import com.binomed.showtime.android.handler.ServiceCallBackMovie;
import com.binomed.showtime.android.layout.view.PageInfoView.CallBack;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.service.CineShowDBGlobalService;
import com.binomed.showtime.android.util.CineShowTimeMenuUtil;
import com.binomed.showtime.android.util.CineShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.activity.IFragmentCineShowTimeInteraction;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class CineShowTimeMovieFragment extends Fragment //
		implements OnTabChangeListener //
		, OnPagedViewChangeListener //
		, CallBack//
{

	private static final String TAG = "MovieActivity"; //$NON-NLS-1$

	private TabHost tabHost;
	private TabWidget tabWidget;
	protected PagedView moviePagedView;
	private MoviePagedAdapter moviePagedAdapter;

	private View mainView;

	/**
	 * The invoked service
	 */
	private IModelMovie model;
	private GoogleAnalyticsTracker tracker;

	private MovieFragmentInteraction<? extends IModelMovie> interaction;

	/*
	 * attributes
	 */

	private float oldTouchValue;
	protected boolean desactivListener = false;
	protected int lastTab = 0, previousTab = 0;

	private IServiceMovie serviceMovie;
	private CineShowtimeDbAdapter mDbHelper;

	class RunnableWithTab implements Runnable {

		private int tabIndex;

		private RunnableWithTab(int tabIndex) {
			super();
			this.tabIndex = tabIndex;
		}

		@Override
		public void run() {
			innerCallBack.handleInputRecived(tabIndex);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragment_movie, container, false);

		model = interaction.getModelActivity();
		tracker = interaction.getTracker();

		model.setMapInstalled(CineShowTimeMenuUtil.isMapsInstalled(getActivity().getPackageManager()));
		model.setDialerInstalled(CineShowTimeMenuUtil.isDialerInstalled(getActivity().getPackageManager()));
		model.setCalendarInstalled(CineShowTimeMenuUtil.isCalendarInstalled(getActivity().getPackageManager()));

		createTabs(mainView);
		initViews(mainView);
		initlisteners();
		bindService();

		return mainView;
	}

	@Override
	public void onAttach(Activity activity) {
		interaction = (MovieFragmentInteraction) activity;
		super.onAttach(activity);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			unbindService();
		} catch (Exception e) {
			Log.e(TAG, "Error during unbinding service");
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		Intent intent = interaction.getIntentMovie();
		String movieId = null;
		String theaterId = null;
		boolean fromWidget = intent.getBooleanExtra(ParamIntent.ACTIVITY_MOVIE_FROM_WIDGET, false);
		String near = intent.getStringExtra(ParamIntent.ACTIVITY_MOVIE_NEAR);
		Log.i(TAG, "From Widget : " + fromWidget);
		MovieBean movie = null;
		TheaterBean theater = null;
		// if (fromWidget) {
		//
		// Object[] currentMovie = extractCurrentMovie();
		// if (currentMovie != null) {
		// theater = (TheaterBean) currentMovie[0];
		// movie = (MovieBean) currentMovie[1];
		// }
		// } else {
		movieId = intent.getStringExtra(ParamIntent.MOVIE_ID);
		movie = intent.getParcelableExtra(ParamIntent.MOVIE);
		theaterId = intent.getStringExtra(ParamIntent.THEATER_ID);
		theater = intent.getParcelableExtra(ParamIntent.THEATER);
		double latitude = intent.getDoubleExtra(ParamIntent.ACTIVITY_MOVIE_LATITUDE, 0);
		double longitude = intent.getDoubleExtra(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, 0);
		if ((latitude != 0) && (longitude != 0)) {
			Location gpsLocation = new Location("GPS"); //$NON-NLS-1$
			gpsLocation.setLatitude(latitude);
			gpsLocation.setLongitude(longitude);
			model.setGpsLocation(gpsLocation);
		} else {
			model.setGpsLocation(null);
		}
		// }
		Log.i(TAG, "Movie ID : " + movieId);

		model.setMovie(movie);
		moviePagedAdapter.changeData(movie, getActivity(), model, tracker, this);
		moviePagedAdapter.notifyDataSetChanged();

		if (theaterId != null) {
			model.setTheater(theater);
		}

		moviePagedAdapter.manageViewVisibility();

		try {
			moviePagedAdapter.fillBasicInformations(movie);

			if (isServiceRunning()) {
				interaction.openDialog();
			}

			if (movie.getImdbId() == null) {
				tracker.trackEvent("Movie", "Search", "Search data from database", 0);
				searchMovieDetail(movie, near);
			} else {
				tracker.trackEvent("Movie", "Reuse", "Reuse data from database", 0);
				moviePagedAdapter.fillViews(movie);
			}
		} catch (Exception e) {
			Log.e(TAG, "error on create", e); //$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	private void initViews(View mainView) {

		moviePagedView = (PagedView) mainView.findViewById(R.id.paged_view);
		moviePagedAdapter = new MoviePagedAdapter();
		moviePagedView.setAdapter(moviePagedAdapter);

	}

	private void initlisteners() {
		tabHost.setOnTabChangedListener(this);
		moviePagedView.setOnPageChangeListener(this);
	}

	private void createTabs(View mainView) {
		try {
			tabHost = (TabHost) mainView.findViewById(android.R.id.tabhost);
			tabWidget = (TabWidget) mainView.findViewById(android.R.id.tabs);
			// tabHost = getTabHost();
			// tabWidget = getTabWidget();
			tabHost.setup();

			Intent intentEmptyActivity = new Intent(getActivity(), EmptyActivity.class);

			TabHost.TabSpec tabSummary = tabHost.newTabSpec("Summary");
			// tabSummary.setContent(intentEmptyActivity);
			tabSummary.setContent(new TabHost.TabContentFactory() {

				@Override
				public View createTabContent(String arg0) {
					TextView view = new TextView(getActivity());
					return view;
				}
			});
			tabSummary.setIndicator(getResources().getString(R.string.movieLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_info));

			TabHost.TabSpec tabProjection = tabHost.newTabSpec("Projection");
			// tabProjection.setContent(intentEmptyActivity);
			tabProjection.setContent(new TabHost.TabContentFactory() {

				@Override
				public View createTabContent(String arg0) {
					TextView view = new TextView(getActivity());
					return view;
				}
			});
			tabProjection.setIndicator(getResources().getString(R.string.showtimeLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_showtimes));

			TabHost.TabSpec tabReviews = tabHost.newTabSpec("Review");
			// tabReviews.setContent(intentEmptyActivity);
			tabReviews.setContent(new TabHost.TabContentFactory() {

				@Override
				public View createTabContent(String arg0) {
					TextView view = new TextView(getActivity());
					return view;
				}
			});
			tabReviews.setIndicator(getResources().getString(R.string.rateLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_review));

			tabHost.addTab(tabSummary);
			tabHost.addTab(tabProjection);
			tabHost.addTab(tabReviews);
			// tabHost.setCurrentTab(model.getLastTab());
		} catch (Exception e1) {
			Log.e(TAG, "error while init Movie acitivty", e1); //$NON-NLS-1$
		}
	}

	/**
	 * The call back message handler
	 */
	public ServiceCallBackMovie m_callbackHandler = new ServiceCallBackMovie() {

		@Override
		public void handleInputRecived(String idMovie) {
			interaction.closeDialog();

			fillDB();

			try {
				moviePagedAdapter.fillViews(model.getMovie());
			} catch (Exception e) {
				Log.e(TAG, "exception ", e);
			}

		}

	};

	public MovieCallBackMovie innerCallBack = new MovieCallBackMovie() {

		@Override
		public void handleInputRecived(int tabIndex) {
			try {
				moviePagedAdapter.fillViews(model.getMovie());
			} catch (Exception e) {
				Log.e(TAG, "error during filling", e);
			}

		}
	};

	/*
	 * 
	 * 
	 * Event Part
	 */

	@Override
	public void onTabChanged(String tabId) {
		Log.i("ListenerMovieActivity", "Change Tab : " + tabId + ", " + desactivListener);
		try {
			if (!desactivListener) {
				previousTab = lastTab;
				if (tabId.equals("Summary")) {
					tracker.trackEvent("Action", "Click", "Go to tab Info", 0);
					// movieFlipper.setInAnimation(AnimationHelper.inFromLeftAnimation());
					// movieFlipper.setOutAnimation(AnimationHelper.outToRightAnimation());
					if (lastTab == 2) {
						moviePagedView.smoothScrollToPage(0);
					} else {
						moviePagedView.smoothScrollToPrevious();
					}
					lastTab = 0;
				} else if (tabId.equals("Projection")) {
					tracker.trackEvent("Action", "Click", "Go to tab projections", 0);
					if (lastTab == 0) {
						moviePagedView.smoothScrollToNext();
						// movieFlipper.setInAnimation(AnimationHelper.inFromRightAnimation());
						// movieFlipper.setOutAnimation(AnimationHelper.outToLeftAnimation());
					} else {
						moviePagedView.smoothScrollToPrevious();
						// movieFlipper.setInAnimation(AnimationHelper.inFromLeftAnimation());
						// movieFlipper.setOutAnimation(AnimationHelper.outToRightAnimation());
					}
					lastTab = 1;
				} else if (tabId.equals("Review")) {
					tracker.trackEvent("Action", "Click", "Go to tab reviews", 0);
					// movieFlipper.setInAnimation(AnimationHelper.inFromRightAnimation());
					// movieFlipper.setOutAnimation(AnimationHelper.outToLeftAnimation());
					if (lastTab == 0) {
						moviePagedView.smoothScrollToPage(2);
					} else {
						moviePagedView.smoothScrollToNext();

					}
					lastTab = 2;
				}
			}
		} catch (Exception e) {
			Log.e("ListenerMovieActivity", "error during change of tab", e);
		}

	}

	@Override
	public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {
		try {
			if (previousPage != previousTab && newPage != lastTab) {
				desactivListener = true;
				tabHost.setCurrentTab(newPage);
				lastTab = tabHost.getCurrentTab();
				// if ((newPage > previousPage) && (tabHost.getCurrentTab() <= 1)) {
				// tracker.trackEvent("Action", "Slide", "Use slide on movie screen", 0);
				// // movieFlipper.setInAnimation(AnimationHelper.inFromRightAnimation());
				// // movieFlipper.setOutAnimation(AnimationHelper.outToLeftAnimation());
				// } else if ((newPage < previousPage) && (tabHost.getCurrentTab() >= 1)) {
				// tracker.trackEvent("Action", "Slide", "Use slide on movie screen", 0);
				// // movieFlipper.setInAnimation(AnimationHelper.inFromLeftAnimation());
				// // movieFlipper.setOutAnimation(AnimationHelper.outToRightAnimation());
				// tabHost.setCurrentTab(tabHost.getCurrentTab() - 1);
				// lastTab = tabHost.getCurrentTab();
				// }
				desactivListener = false;
			}
		} catch (Exception e) {
			Log.e(TAG, "error during managing ActionUp", e);
		}

	}

	@Override
	public void onStartTracking(PagedView pagedView) {
		// nothing to do

	}

	@Override
	public void onStopTracking(PagedView pagedView) {
		// nothing to do

	}

	public void onCancel() {
		try {
			serviceMovie.cancelService();
		} catch (RemoteException e) {
			Log.e(TAG, "Error cancel service", e);
		}
		Intent intentMovieService = new Intent(getActivity(), CineShowTimeMovieService.class);
		getActivity().stopService(intentMovieService);
		// finish(); TODO
	}

	/*
	 * 
	 * DB
	 */

	public void initDB() {

		try {
			mDbHelper = new CineShowtimeDbAdapter(getActivity());
			mDbHelper.open();

		} catch (SQLException e) {
			Log.e(TAG, "error during opening data base", e); //$NON-NLS-1$
		}
	}

	public void closeDB() {
		try {
			if (mDbHelper != null) {
				mDbHelper.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "error onDestroy of movie Activity", e);
		}
	}

	@Override
	public void fillDB() {
		Intent intentUpdateMovie = new Intent(getActivity(), CineShowDBGlobalService.class);
		intentUpdateMovie.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_MOVIE_WRITE);
		intentUpdateMovie.putExtra(ParamIntent.SERVICE_DB_DATA, model.getMovie());
		getActivity().startService(intentUpdateMovie);

	}

	public Object[] extractCurrentMovie() {
		initDB();
		Object[] result = CineShowtimeDB2AndShowtimeBeans.extractCurrentWidgetMovie(mDbHelper);
		closeDB();
		return result;
	}

	/*
	 * SERVICE
	 */

	public void bindService() {
		getActivity().bindService(new Intent(getActivity(), CineShowTimeMovieService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() {
		try {
			serviceMovie.unregisterCallback(m_callback);
			getActivity().unbindService(mConnection);
		} catch (Exception e) {
			Log.e(TAG, "error while unbinding service", e);
		}
	}

	protected boolean isServiceRunning() {
		if (serviceMovie != null) {
			try {
				return serviceMovie.isServiceRunning();
			} catch (RemoteException e) {
				Log.e(TAG, "Error during checking service", e); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * The service connection inteface with our binded service {@link http ://code .google.com/android/reference/android/content/ServiceConnection.html}
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceMovie = IServiceMovie.Stub.asInterface(service);

			try {
				serviceMovie.registerCallback(m_callback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	/**
	 * The callback object that will return from the service
	 */
	private ICallbackMovie m_callback = new ICallbackMovie.Stub() {

		@Override
		public void finish(String idMovie) throws RemoteException {
			model.setMovie(serviceMovie.getMovie(idMovie));
			m_callbackHandler.sendInputRecieved(idMovie);

		}

	};

	public void searchMovieDetail(MovieBean movie, String near) throws Exception {

		interaction.openDialog();

		boolean checkboxPreference;
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		checkboxPreference = prefs.getBoolean("checkbox_preference", false);

		Intent intentMovieService = new Intent(getActivity(), CineShowTimeMovieService.class);

		intentMovieService.putExtra(ParamIntent.SERVICE_MOVIE_ID, movie.getId());
		intentMovieService.putExtra(ParamIntent.SERVICE_MOVIE, movie);
		intentMovieService.putExtra(ParamIntent.SERVICE_MOVIE_NEAR, near);
		intentMovieService.putExtra(ParamIntent.SERVICE_MOVIE_TRANSLATE, checkboxPreference);

		getActivity().startService(intentMovieService);

	}

	/*
	 * Intents
	 */

	public void openImdbBrowser() {
		startActivity(IntentShowtime.createImdbBrowserIntent(model.getMovie()));
	}

	// TODO a remettre quand j'aurais une api de traduction
	// public String translateDesc() throws Exception {
	// MovieBean movie = model.getMovie();
	// String descTlt = movie.getDescription();
	// if (!model.isTranslate()) {
	// descTlt = movie.getTrDescription();
	// if ((descTlt == null) //
	// || (descTlt.length() == 0)) {
	// Language convertLanguage = CineShowTimeEncodingUtil.convertLocaleToLanguage();
	// if (!convertLanguage.equals(Language.ENGLISH)) {
	// descTlt = Translate.translate(movie.getDescription(), Language.ENGLISH, convertLanguage);
	// movie.setTrDescription(descTlt);
	// fillDB();
	// }
	// }
	// model.setTranslate(true);
	// } else {
	// model.setTranslate(false);
	// }
	// return descTlt;
	// }

	/*
	 * Interactions
	 */

	public void changePreferences() {
		moviePagedAdapter.changePreferences();
	}

	public interface MovieFragmentInteraction<M extends IModelMovie> extends IFragmentCineShowTimeInteraction<M> {

		Intent getIntentMovie();

	}

}
