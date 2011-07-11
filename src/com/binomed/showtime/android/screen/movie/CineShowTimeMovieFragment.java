package com.binomed.showtime.android.screen.movie;

import java.net.URLDecoder;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.binomed.showtime.android.adapter.view.GalleryTrailerAdapter;
import com.binomed.showtime.android.adapter.view.ProjectionListAdapter;
import com.binomed.showtime.android.adapter.view.ReviewListAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.handler.MovieCallBackMovie;
import com.binomed.showtime.android.handler.ServiceCallBackMovie;
import com.binomed.showtime.android.layout.view.GalleryTrailerView;
import com.binomed.showtime.android.layout.view.ListPopupWindow;
import com.binomed.showtime.android.layout.view.ProjectionView;
import com.binomed.showtime.android.model.LocalisationBean;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.service.CineShowDBGlobalService;
import com.binomed.showtime.android.util.CineShowTimeEncodingUtil;
import com.binomed.showtime.android.util.CineShowTimeMenuUtil;
import com.binomed.showtime.android.util.CineShowtimeDB2AndShowtimeBeans;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.CineShowtimeRequestManage;
import com.binomed.showtime.android.util.activity.IFragmentCineShowTimeInteraction;
import com.binomed.showtime.android.util.images.ImageDownloader;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class CineShowTimeMovieFragment extends Fragment //
		// public class CineShowTimeMovieActivity extends Activity //
		implements OnItemClickListener //
		, OnTabChangeListener //
		, OnTouchListener //
		, OnClickListener //
// , OnCancelListener //
{

	private static final String TAG = "MovieActivity"; //$NON-NLS-1$

	private static final int ITEM_TRANSLATE = Menu.FIRST + 2;
	private static final int MENU_PREF = Menu.FIRST;

	private TabHost tabHost;
	private TabWidget tabWidget;
	private TextView movieTitle, txtMovieTitle;
	private TextView movieRate, txtMovieRate;
	private TextView movieDuration, txtMovieDuration;
	private TextView movieDirector, txtMovieDirector;
	private TextView movieActor, txtMovieActor;
	private TextView movieStyle, txtMovieStyle;
	private TextView theaterTitle, theaterAddress;
	private ImageView summaryMoviePoster;
	private TextView moviePlot;
	private TextView movieWebLinks;
	private ListView movieProjectionTimeList;
	private ListView movieReviewsList;
	private Gallery movieGalleryTrailer;
	protected ViewFlipper movieFlipper;
	private ScrollView movieTabInfoScrollView;
	protected RelativeLayout tabShowtimes;
	private ImageButton movieBtnMap, movieBtnDirection, movieBtnCall;

	protected GalleryTrailerAdapter trailerAdapter;
	protected ProjectionListAdapter projectionAdapter;

	private ImageView sumRate1, sumRate2, sumRate3, sumRate4, sumRate5, sumRate6, sumRate7, sumRate8, sumRate9, sumRate10;

	private View mainView;

	private Bitmap bitmapRateOff;
	private Bitmap bitmapRateHalf;
	private Bitmap bitmapRateOn;

	// private ProgressDialog progressDialog;
	/**
	 * The invoked service
	 */
	private IModelMovie model;
	protected GoogleAnalyticsTracker tracker;

	private MovieFragmentInteraction<? extends IModelMovie> interaction;

	/*
	 * attributes
	 */
	// private long minTime;
	private boolean distanceTime;
	// private boolean homePress;

	private float oldTouchValue;
	protected boolean desactivListener = false;
	protected int lastTab = 0;

	// private DrawableManager drawableManager;
	private ImageDownloader imageDownloader;

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
		// super.onCreate(savedInstanceState);
		// tracker = GoogleAnalyticsTracker.getInstance();
		// tracker.start(CineShowtimeCst.GOOGLE_ANALYTICS_ID, getActivity());
		// tracker.trackPageView("/MovieActivity");
		// SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		// CineShowTimeLayoutUtils.onActivityCreateSetTheme(getActivity(), prefs);
		// setContentView(R.layout.activity_movie);
		mainView = inflater.inflate(R.layout.fragment_movie, container, false);

		model = interaction.getModelActivity();
		tracker = interaction.getTracker();
		// Init star img
		bitmapRateOff = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_off);
		bitmapRateHalf = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_half);
		bitmapRateOn = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_on);

		model.setMapInstalled(CineShowTimeMenuUtil.isMapsInstalled(getActivity().getPackageManager()));
		model.setDialerInstalled(CineShowTimeMenuUtil.isDialerInstalled(getActivity().getPackageManager()));
		model.setCalendarInstalled(CineShowTimeMenuUtil.isCalendarInstalled(getActivity().getPackageManager()));

		createTabs(mainView);
		initViews(mainView);
		initlisteners();
		initMenus();
		bindService();

		// initResults(); TODO
		return mainView;
	}

	// TODO
	// private void initResults() {
	// Intent intentResult = new Intent();
	// intentResult.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, model.isResetTheme());
	// setResult(CineShowtimeCst.ACTIVITY_RESULT_RESULT_ACTIVITY, intentResult);
	// }

	@Override
	public void onAttach(Activity activity) {
		interaction = (MovieFragmentInteraction) activity;
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		super.onResume();

		// homePress = false;

		Intent intent = interaction.getIntentMovie();
		String movieId = null;
		String theaterId = null;
		boolean fromWidget = intent.getBooleanExtra(ParamIntent.ACTIVITY_MOVIE_FROM_WIDGET, false);
		String near = intent.getStringExtra(ParamIntent.ACTIVITY_MOVIE_NEAR);
		Log.i(TAG, "From Widget : " + fromWidget);
		MovieBean movie = null;
		TheaterBean theater = null;
		if (fromWidget) {

			Object[] currentMovie = extractCurrentMovie();
			if (currentMovie != null) {
				theater = (TheaterBean) currentMovie[0];
				movie = (MovieBean) currentMovie[1];
			}
		} else {
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
		}
		Log.i(TAG, "Movie ID : " + movieId);

		model.setMovie(movie);

		if (theaterId != null) {
			model.setTheater(theater);
		}

		manageViewVisibility();

		try {
			fillBasicInformations(movie);

			if (isServiceRunning()) {
				interaction.openDialog();
				// openDialog();
			}

			if (movie.getImdbId() == null) {
				tracker.trackEvent("Movie", "Search", "Search data from database", 0);
				searchMovieDetail(movie, near);
			} else {
				tracker.trackEvent("Movie", "Reuse", "Reuse data from database", 0);
				fillViews(mainView, movie, false);
			}
		} catch (Exception e) {
			Log.e(TAG, "error on create", e); //$NON-NLS-1$
		}
	}

	private void manageViewVisibility() {
		if ((model.getTheater() == null) //
		) {
			movieBtnMap.setEnabled(false);
		}
		if ((model.getGpsLocation() == null) //
				|| !model.isMapInstalled()//
		) {
			movieBtnDirection.setEnabled(false);
		}
		if (!model.isDialerInstalled() //
				&& ((model.getTheater() == null) || (model.getTheater().getPhoneNumber() == null))) {
			movieBtnCall.setEnabled(false);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
		// if (homePress) {
		// finish();
		// }
	}

	// TODO
	// /*
	// * (non-Javadoc)
	// *
	// * @see android.app.Activity#onDestroy()
	// */
	// @Override
	// public void finish() {
	// boolean resetTheme = getActivity().getIntent() != null ? getActivity().getIntent().getBooleanExtra(ParamIntent.PREFERENCE_RESULT_THEME, false) : false;
	// if (resetTheme) {
	// setResult(CineShowtimeCst.RESULT_PREF_WITH_NEW_THEME);
	// }
	// super.finish();
	// }

	@Override
	public void onStop() {
		Log.i(TAG, "onStop : ");
		super.onStop();
	}

	// TODO
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// Log.i(TAG, "onKeyDown : " + (keyCode == KeyEvent.KEYCODE_HOME));
	// homePress = (keyCode == KeyEvent.KEYCODE_HOME);
	// return super.onKeyDown(keyCode, event);
	// }
	//
	// @Override
	// public boolean onTouchEvent(MotionEvent touchEvent) {
	// manageMotionEvent(touchEvent);
	// return false;
	// }

	protected void manageMotionEvent(MotionEvent touchEvent) {
		final float x = touchEvent.getX();
		switch (touchEvent.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			Log.i(TAG, "onTouchEventDown");
			oldTouchValue = x;
			break;
		}
		case MotionEvent.ACTION_UP: {
			Log.i(TAG, "onTouchEventUp");
			float currentX = touchEvent.getX();
			float diff = Math.abs(currentX - oldTouchValue);
			if (diff > (tabWidget.getWidth() / 2)) {
				Log.i("MainActivity4", "oldTouch: " + oldTouchValue + "; currentX : " + currentX + "; tabHos : " + tabHost.getCurrentTab());
				try {
					if ((oldTouchValue > currentX) && (tabHost.getCurrentTab() <= 1)) {
						tracker.trackEvent("Action", "Slide", "Use slide on movie screen", 0);
						// fillViews(model.getMovie(), false);
						movieFlipper.setInAnimation(AnimationHelper.inFromRightAnimation());
						movieFlipper.setOutAnimation(AnimationHelper.outToLeftAnimation());
						movieFlipper.showNext();
						desactivListener = true;
						tabHost.setCurrentTab(tabHost.getCurrentTab() + 1);
						lastTab = tabHost.getCurrentTab();
						desactivListener = false;
					} else if ((oldTouchValue < currentX) && (tabHost.getCurrentTab() >= 1)) {
						tracker.trackEvent("Action", "Slide", "Use slide on movie screen", 0);
						// fillViews(model.getMovie(), false);
						movieFlipper.setInAnimation(AnimationHelper.inFromLeftAnimation());
						movieFlipper.setOutAnimation(AnimationHelper.outToRightAnimation());
						movieFlipper.showPrevious();
						desactivListener = true;
						tabHost.setCurrentTab(tabHost.getCurrentTab() - 1);
						lastTab = tabHost.getCurrentTab();
						desactivListener = false;
					}
				} catch (Exception e) {
					Log.e(TAG, "error during managing ActionUp", e);
				}
			}
			break;
		}
		}
	}

	/**
	 * 
	 */
	private void initViews(View mainView) {

		movieFlipper = (ViewFlipper) mainView.findViewById(R.id.movieFlipper);

		summaryMoviePoster = (ImageView) mainView.findViewById(R.id.moviePoster);
		// txtMovieTitle = (TextView) findViewById(R.id.txtMovieTitle);
		movieTitle = (TextView) mainView.findViewById(R.id.movieTitle);
		txtMovieDuration = (TextView) mainView.findViewById(R.id.txtMovieDuration);
		movieDuration = (TextView) mainView.findViewById(R.id.movieDuration);
		moviePlot = (TextView) mainView.findViewById(R.id.moviePlot);
		theaterTitle = (TextView) mainView.findViewById(R.id.movieTheaterTitle);
		theaterAddress = (TextView) mainView.findViewById(R.id.movieTheaterAddress);
		movieProjectionTimeList = (ListView) mainView.findViewById(R.id.movieListProjection);
		movieReviewsList = (ListView) mainView.findViewById(R.id.movieListReview);
		movieGalleryTrailer = (Gallery) mainView.findViewById(R.id.gallery_trailer);

		movieTabInfoScrollView = (ScrollView) mainView.findViewById(R.id.movieTab_summary);

		tabShowtimes = (RelativeLayout) mainView.findViewById(R.id.Projection);

		movieBtnMap = (ImageButton) mainView.findViewById(R.id.movieBtnMap);
		movieBtnDirection = (ImageButton) mainView.findViewById(R.id.movieBtnDirection);
		movieBtnCall = (ImageButton) mainView.findViewById(R.id.movieBtnCall);

		// drawableManager = new DrawableManager();
		imageDownloader = new ImageDownloader();
	}

	private void initlisteners() {
		movieGalleryTrailer.setOnItemClickListener(this);
		tabHost.setOnTabChangedListener(this);

		movieTabInfoScrollView.setOnTouchListener(this);
		movieProjectionTimeList.setOnTouchListener(this);
		movieReviewsList.setOnTouchListener(this);

		movieBtnMap.setOnClickListener(this);
		movieBtnDirection.setOnClickListener(this);
		movieBtnCall.setOnClickListener(this);
	}

	private void initMenus() {
		registerForContextMenu(moviePlot);
		registerForContextMenu(movieProjectionTimeList);
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
			// tabSummary.setContent(R.id.movieTab_summary);
			// tabSummary.setContent(intentEmptyActivity);
			tabSummary.setContent(new TabHost.TabContentFactory() {

				@Override
				public View createTabContent(String arg0) {
					TextView view = new TextView(getActivity());
					view.setText("Un texte");
					return view;
				}
			});
			// tabSummary.setIndicator(new TabInfoView(this, getResources().getString(R.string.movieLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_info)));
			tabSummary.setIndicator(getResources().getString(R.string.movieLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_info));

			TabHost.TabSpec tabProjection = tabHost.newTabSpec("Projection");
			// tabProjection.setContent(R.id.movieTab_projections);
			// tabProjection.setContent(intentEmptyActivity);
			// tabProjection.setContent(intentEmptyActivity);
			tabProjection.setContent(new TabHost.TabContentFactory() {

				@Override
				public View createTabContent(String arg0) {
					TextView view = new TextView(getActivity());
					view.setText("Un texte");
					return view;
				}
			});
			// tabSummary.setIndicator(new TabInfoView(this, getResources().getString(R.string.showtimeLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_showtimes)));
			tabProjection.setIndicator(getResources().getString(R.string.showtimeLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_showtimes));

			TabHost.TabSpec tabReviews = tabHost.newTabSpec("Review");
			// tabReviews.setContent(R.id.movieTab_reviews);
			// tabReviews.setContent(intentEmptyActivity);
			// tabReviews.setContent(intentEmptyActivity);
			tabReviews.setContent(new TabHost.TabContentFactory() {

				@Override
				public View createTabContent(String arg0) {
					TextView view = new TextView(getActivity());
					view.setText("Un texte");
					return view;
				}
			});
			// tabSummary.setIndicator(new TabInfoView(this, getResources().getString(R.string.rateLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_review)));
			tabReviews.setIndicator(getResources().getString(R.string.rateLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_review));

			tabHost.addTab(tabSummary);
			tabHost.addTab(tabProjection);
			tabHost.addTab(tabReviews);
			// tabHost.setCurrentTab(model.getLastTab());
		} catch (Exception e1) {
			Log.e(TAG, "error while init Movie acitivty", e1); //$NON-NLS-1$
		}
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see android.app.Activity#onDestroy()
	// */
	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// if ((progressDialog != null) && progressDialog.isShowing()) {
	// progressDialog.dismiss();
	// }
	// unbindService();
	// tracker.dispatch();
	// tracker.stop();
	// // controler.closeDB();
	// }

	/**
	 * The call back message handler
	 */
	public ServiceCallBackMovie m_callbackHandler = new ServiceCallBackMovie() {

		@Override
		public void handleInputRecived(String idMovie) {
			interaction.closeDialog();
			// if (progressDialog != null) {
			// progressDialog.dismiss();
			// }

			fillDB();

			try {
				fillViews(mainView, model.getMovie(), false);
			} catch (Exception e) {
				Log.e(TAG, "exception ", e);
			}

		}

	};

	public MovieCallBackMovie innerCallBack = new MovieCallBackMovie() {

		@Override
		public void handleInputRecived(int tabIndex) {
			try {
				fillViews(mainView, model.getMovie(), true);
			} catch (Exception e) {
				Log.e(TAG, "error during filling", e);
			}

		}
	};

	// public void openDialog() {
	//
	// progressDialog = ProgressDialog.show(getActivity(),
	// //
	// getResources().getString(R.string.movieProgressTitle)//
	// , getResources().getString(R.string.movieProgressMsg) //
	// , true, true, this);
	// }

	/**
	 * @param movie
	 */
	private void fillBasicInformations(MovieBean movie) {

		movieTitle.setText(movie.getMovieName());

		txtMovieDuration.setText(getResources().getString(R.string.txtDuration));
		movieDuration.setText(CineShowtimeDateNumberUtil.showMovieTimeLength(getActivity(), movie));

	}

	/**
	 * @param movie
	 * @throws Exception
	 */
	protected void fillViews(View mainView, MovieBean movie, boolean withAdapter) throws Exception {

		Log.i(TAG, "FillViews : " + withAdapter);
		if ((movie.getUrlImg() != null)) {
			CineShowtimeRequestManage.completeMovieDetailStream(movie);
		}

		if (((movieWebLinks == null //
				) && ((movie.getImdbId() != null) && (movie.getImdbId().length() != 0)) //
				)
				|| ((movie.getUrlWikipedia() != null) && (movie.getUrlWikipedia().length() != 0)) //
		) {
			movieWebLinks = (TextView) mainView.findViewById(R.id.movieWebLinks);
		}
		if (movieWebLinks != null) {
			StringBuffer linkBuffer = new StringBuffer();
			if ((movie.getImdbId() != null) && (movie.getImdbId().length() != 0)) {
				linkBuffer.append("<A HREF=\"http://www.imdb.com/title/tt").append(movie.getImdbId()).append("\">Imdb</A>"); //$NON-NLS-1$//$NON-NLS-2$
			}
			if ((movie.getUrlWikipedia() != null) && (movie.getUrlWikipedia().length() != 0)) {
				if (linkBuffer.length() > 0) {
					linkBuffer.append(", "); //$NON-NLS-1$
				}
				linkBuffer.append("<A HREF=\"").append(movie.getUrlWikipedia()).append("\">Wikipedia</A>"); //$NON-NLS-1$//$NON-NLS-2$
			}

			movieWebLinks.setText(Html.fromHtml(linkBuffer.toString()));
			movieWebLinks.setMovementMethod(LinkMovementMethod.getInstance());
		}

		if ((movieRate == null) && (movie.getRate() != null)) {
			txtMovieRate = (TextView) mainView.findViewById(R.id.txtMovieRate);
			movieRate = (TextView) mainView.findViewById(R.id.movieRate);
		}
		if (movieRate != null) {
			String rate = " / 10";
			if (movie.getRate() != null) {
				rate = String.valueOf(movie.getRate()) + rate;
			} else {
				rate = "-" + rate;
			}
			txtMovieRate.setText(getResources().getString(R.string.txtRate));
			movieRate.setText(rate);

			sumRate1 = (ImageView) mainView.findViewById(R.id.movieImgRate1);
			sumRate2 = (ImageView) mainView.findViewById(R.id.movieImgRate2);
			sumRate3 = (ImageView) mainView.findViewById(R.id.movieImgRate3);
			sumRate4 = (ImageView) mainView.findViewById(R.id.movieImgRate4);
			sumRate5 = (ImageView) mainView.findViewById(R.id.movieImgRate5);
			sumRate6 = (ImageView) mainView.findViewById(R.id.movieImgRate6);
			sumRate7 = (ImageView) mainView.findViewById(R.id.movieImgRate7);
			sumRate8 = (ImageView) mainView.findViewById(R.id.movieImgRate8);
			sumRate9 = (ImageView) mainView.findViewById(R.id.movieImgRate9);
			sumRate10 = (ImageView) mainView.findViewById(R.id.movieImgRate10);
			fillRateImg(movie.getRate());
		}

		String style = movie.getStyle();
		if ((movieStyle == null) && (style != null) && (style.length() != 0)) {
			txtMovieStyle = (TextView) mainView.findViewById(R.id.txtMovieGenre);
			movieStyle = (TextView) mainView.findViewById(R.id.movieGenre);
		}
		if (movieStyle != null) {
			if ((style != null) && (style.length() != 0)) {
				txtMovieStyle.setText(getResources().getString(R.string.txtGenre));
				movieStyle.setText(style.replaceAll("\\|", ", "));
			}
		}

		String directorList = movie.getDirectorList();
		if ((movieDirector == null) && (directorList != null) && (directorList.length() > 0)) {
			txtMovieDirector = (TextView) mainView.findViewById(R.id.txtMovieDirector);
			movieDirector = (TextView) mainView.findViewById(R.id.movieDirector);
		}
		if (movieDirector != null) {
			if ((directorList != null) && (directorList.length() > 0)) {
				txtMovieDirector.setText(getResources().getString(R.string.txtDirector));
				movieDirector.setText(directorList.replaceAll("\\|", ", "));
			}
		}

		String actorList = movie.getActorList();
		if ((movieActor == null) && (actorList != null) && (actorList.length() > 0)) {
			txtMovieActor = (TextView) mainView.findViewById(R.id.txtMovieActor);
			movieActor = (TextView) mainView.findViewById(R.id.movieActor);
		}
		if (movieActor != null) {
			StringBuffer actorBuffer = new StringBuffer();
			if ((actorList != null) && (actorList.length() > 0)) {
				boolean first = true;
				String[] actorArray = actorList.split("\\|");
				for (int i = 0; i < Math.min(3, actorArray.length); i++) {
					String actor = actorArray[i];
					if (first) {
						first = false;
					} else {
						actorBuffer.append(", "); //$NON-NLS-1$
					}
					actorBuffer.append(actor);
				}
				if (actorArray.length > Math.min(3, actorArray.length)) {
					actorBuffer.append(", ...");
				}
				txtMovieActor.setText(getResources().getString(R.string.txtActor));
				movieActor.setText(actorBuffer.toString());
			}
		}

		boolean checkboxPreference;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		checkboxPreference = prefs.getBoolean(this.getResources().getString(R.string.preference_lang_key_auto_translate), false);

		if ((movie.getDescription() != null)) {
			String descTlt = movie.getDescription();
			if (checkboxPreference) {
				descTlt = Translate.translate(movie.getDescription(), Language.ENGLISH, Language.FRENCH);
				movie.setTrDescription(descTlt);
				fillDB();
				model.setTranslate(true);
			} else {
				model.setTranslate(false);
			}
			moviePlot.setText(descTlt);
		} else {
			moviePlot.setText(getResources().getString(R.string.noSummary));
		}
		if (movie.getUrlImg() != null) {
			imageDownloader.download(movie.getUrlImg(), summaryMoviePoster, getActivity());
		} else {
			summaryMoviePoster.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_poster));
		}

		TheaterBean theater = model.getTheater();
		if (theater != null) {
			theaterTitle.setText(theater.getTheaterName());

			LocalisationBean place = theater.getPlace();
			if (place != null) {
				try {
					theaterAddress.setText(URLDecoder.decode(place.getSearchQuery(), CineShowTimeEncodingUtil.getEncoding()));
				} catch (Exception e) {
					Log.e(TAG, "error decoding address", e);
				}
			}
		}

		if ((movie.getYoutubeVideos() != null) && !movie.getYoutubeVideos().isEmpty()) {
			this.movieGalleryTrailer.setAdapter(new GalleryTrailerAdapter(getActivity(), movie.getYoutubeVideos(), imageDownloader));
		}
		List<ProjectionBean> projectionList = null;
		distanceTime = prefs.getBoolean(this.getResources().getString(R.string.preference_loc_key_time_direction)//
				, false);
		Long distanceTimeLong = null;
		if (theater != null) {
			projectionList = theater.getMovieMap().get(movie.getId());
			if (distanceTime && (theater.getPlace() != null)) {
				distanceTimeLong = theater.getPlace().getDistanceTime();
			}
		}

		projectionAdapter = new ProjectionListAdapter(getActivity()//
				, movie //
				, projectionList //
				, CineShowtimeDateNumberUtil.getMinTime(projectionList, distanceTimeLong) //
				, this//
		);
		movieProjectionTimeList.setAdapter(projectionAdapter);
		this.movieReviewsList.setAdapter(new ReviewListAdapter(getActivity(), movie.getReviews()));
	}

	/**
	 * @param rate
	 */
	private void fillRateImg(Double rate) {

		int rate1 = R.drawable.rate_star_small_off;
		int rate2 = R.drawable.rate_star_small_off;
		int rate3 = R.drawable.rate_star_small_off;
		int rate4 = R.drawable.rate_star_small_off;
		int rate5 = R.drawable.rate_star_small_off;
		int rate6 = R.drawable.rate_star_small_off;
		int rate7 = R.drawable.rate_star_small_off;
		int rate8 = R.drawable.rate_star_small_off;
		int rate9 = R.drawable.rate_star_small_off;
		int rate10 = R.drawable.rate_star_small_off;
		if (rate != null) {
			switch (rate.intValue()) {
			case 10:
				rate10 = R.drawable.rate_star_small_on;
			case 9:
				rate9 = R.drawable.rate_star_small_on;
				if ((rate > 9.5) && (rate < 10)) {
					rate10 = R.drawable.rate_star_small_half;
				}
			case 8:
				rate8 = R.drawable.rate_star_small_on;
				if ((rate > 8.5) && (rate < 9)) {
					rate9 = R.drawable.rate_star_small_half;
				}
			case 7:
				rate7 = R.drawable.rate_star_small_on;
				if ((rate > 7.5) && (rate < 8)) {
					rate8 = R.drawable.rate_star_small_half;
				}
			case 6:
				rate6 = R.drawable.rate_star_small_on;
				if ((rate > 6.5) && (rate < 7)) {
					rate7 = R.drawable.rate_star_small_half;
				}
			case 5:
				rate5 = R.drawable.rate_star_small_on;
				if ((rate > 5.5) && (rate < 6)) {
					rate6 = R.drawable.rate_star_small_half;
				}
			case 4:
				rate4 = R.drawable.rate_star_small_on;
				if ((rate > 4.5) && (rate < 5)) {
					rate5 = R.drawable.rate_star_small_half;
				}
			case 3:
				rate3 = R.drawable.rate_star_small_on;
				if ((rate > 3.5) && (rate < 4)) {
					rate4 = R.drawable.rate_star_small_half;
				}
			case 2:
				rate2 = R.drawable.rate_star_small_on;
				if ((rate > 2.5) && (rate < 3)) {
					rate3 = R.drawable.rate_star_small_half;
				}
			case 1:
				rate1 = R.drawable.rate_star_small_on;
				if ((rate > 1.5) && (rate < 2)) {
					rate2 = R.drawable.rate_star_small_half;
				}
			case 0:
				if ((rate > 0.5) && (rate < 1)) {
					rate1 = R.drawable.rate_star_small_half;
				}
			default:
				break;
			}
		}

		sumRate1.setImageBitmap(getImg(rate1));
		sumRate2.setImageBitmap(getImg(rate2));
		sumRate3.setImageBitmap(getImg(rate3));
		sumRate4.setImageBitmap(getImg(rate4));
		sumRate5.setImageBitmap(getImg(rate5));
		sumRate6.setImageBitmap(getImg(rate6));
		sumRate7.setImageBitmap(getImg(rate7));
		sumRate8.setImageBitmap(getImg(rate8));
		sumRate9.setImageBitmap(getImg(rate9));
		sumRate10.setImageBitmap(getImg(rate10));

	}

	/**
	 * @param rate
	 * @return
	 */
	private Bitmap getImg(int rate) {
		if (rate == R.drawable.rate_star_small_off) {
			return bitmapRateOff;
		} else if (rate == R.drawable.rate_star_small_half) {
			return bitmapRateHalf;
		} else {
			return bitmapRateOn;
		}
	}

	/*
	 * MENU
	 */
	// TODO
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// Log.i(TAG, "onCreateOptionMenu : ");
	// tracker.trackEvent("Menu", "Consult", "Consult menu from movie activity", 0);
	// CineShowTimeMenuUtil.createMenu(menu, MENU_PREF, 3);
	// super.onCreateOptionsMenu(menu);
	// // menu.add(0, MENU_VIDEO, 2, R.string.openYoutubeMenuItem).setIcon(R.drawable.ic_menu_play_clip);
	// return true;
	// }
	//
	// @Override
	// public boolean onMenuItemSelected(int featureId, MenuItem item) {
	// Log.i(TAG, "onMenuItemSelect : " + item.getItemId());
	// if (CineShowTimeMenuUtil.onMenuItemSelect(this, tracker, MENU_PREF, item.getItemId())) {
	// projectionAdapter.changePreferences();
	// return true;
	// }
	// // switch (item.getItemId()) {
	// // case MENU_VIDEO: {
	// // startActivity(IntentShowtime.createYoutubeIntent(model.getMovie()));
	// // return true;
	// // }
	// // case ITEM_TRANSLATE: {
	// // try {
	// // moviePlot.setText(controler.translateDesc());
	// // } catch (Exception e) {
	//		//				Log.e(TAG, "error while translating", e); //$NON-NLS-1$
	// // }
	// // return true;
	// // }
	// // default:
	// // break;
	// // }
	//
	// return super.onMenuItemSelected(featureId, item);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	// */
	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	// super.onCreateContextMenu(menu, v, menuInfo);
	// MovieBean movie = model.getMovie();
	// switch (v.getId()) {
	// case R.id.moviePlot:
	// if (movie.isImdbDesrciption() && (movie.getDescription() != null)) {
	// menu.add(0, ITEM_TRANSLATE, 0, R.string.menuTranslate);
	//
	// }
	//
	// break;
	//
	// default:
	// break;
	// }
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// Log.i(TAG, "onOptionsItemSelected : " + item.getItemId());
	// if (CineShowTimeMenuUtil.onMenuItemSelect(this, tracker, MENU_PREF, item.getItemId())) {
	// projectionAdapter.changePreferences();
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }
	//
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// if (data != null) {
	// model.setResetTheme(data.getBooleanExtra(ParamIntent.PREFERENCE_RESULT_THEME, false));
	// } else {
	// model.setResetTheme(false);
	// }
	//
	// initResults();
	//
	// if (requestCode == CineShowtimeCst.ACTIVITY_RESULT_PREFERENCES && projectionAdapter != null) {
	// projectionAdapter.changePreferences();
	//
	// }
	//
	// if (model.isResetTheme()) {
	// CineShowTimeLayoutUtils.changeToTheme(this, getIntent());
	// }
	// //
	// // if (requestCode == AndShowtimeCst.ACTIVITY_RESULT_PREFERENCES) {
	// // projectionAdapter.changePreferences();
	// // if (AndShowTimeMenuUtil.manageResult(this, requestCode, resultCode, data)) {
	// // return;
	// // }
	// // }
	//
	// }

	/*
	 * 
	 * 
	 * Event Part
	 */

	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		if (view instanceof GalleryTrailerView) {
			GalleryTrailerView trailer = (GalleryTrailerView) view;
			tracker.trackEvent("Open", "Click", "Open trailer", 0);
			tracker.dispatch();
			startActivity(IntentShowtime.createTrailerIntent(trailer.getYoutubeBean()));
		}

	}

	@Override
	public void onTabChanged(String tabId) {
		Log.i("ListenerMovieActivity", "Change Tab : " + tabId + ", " + desactivListener);
		try {
			if (!desactivListener) {
				if (tabId.equals("Summary")) {
					tracker.trackEvent("Action", "Click", "Go to tab Info", 0);
					// movieActivity.fillViews(model.getMovie(), false);
					movieFlipper.setInAnimation(AnimationHelper.inFromLeftAnimation());
					movieFlipper.setOutAnimation(AnimationHelper.outToRightAnimation());
					movieFlipper.showPrevious();
					if (lastTab == 2) {
						tabShowtimes.setVisibility(View.INVISIBLE);
						movieFlipper.showPrevious();
						// movieActivity.tabShowtimes.setVisibility(View.VISIBLE);
					}
					lastTab = 0;
				} else if (tabId.equals("Projection")) {
					tracker.trackEvent("Action", "Click", "Go to tab projections", 0);
					// movieActivity.fillViews(model.getMovie(), false);
					if (lastTab == 0) {
						movieFlipper.setInAnimation(AnimationHelper.inFromRightAnimation());
						movieFlipper.setOutAnimation(AnimationHelper.outToLeftAnimation());
						movieFlipper.showNext();
					} else {
						movieFlipper.setInAnimation(AnimationHelper.inFromLeftAnimation());
						movieFlipper.setOutAnimation(AnimationHelper.outToRightAnimation());
						movieFlipper.showPrevious();
					}
					lastTab = 1;
				} else if (tabId.equals("Review")) {
					tracker.trackEvent("Action", "Click", "Go to tab reviews", 0);
					// movieActivity.fillViews(model.getMovie(), false);
					movieFlipper.setInAnimation(AnimationHelper.inFromRightAnimation());
					movieFlipper.setOutAnimation(AnimationHelper.outToLeftAnimation());
					movieFlipper.showNext();
					if (lastTab == 0) {
						tabShowtimes.setVisibility(View.INVISIBLE);
						movieFlipper.showNext();
						// movieActivity.tabShowtimes.setVisibility(View.VISIBLE);
					}
					lastTab = 2;
				}
			}
		} catch (Exception e) {
			Log.e("ListenerMovieActivity", "error during change of tab", e);
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		manageMotionEvent(event);
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_projection_button:
			tracker.trackEvent("Action", "Click", "Use popup button", 0);
			ImageButton imageBtn = (ImageButton) v;
			ProjectionView parentView = (ProjectionView) imageBtn.getParent().getParent();

			ListPopupWindow popupWindow = new ListPopupWindow(v, getActivity(), model.getTheater(), model.getMovie(), parentView.getProjectionBean(), model.isCalendarInstalled());
			// popupWindow.showLikeQuickAction(0, -30);
			// popupWindow.showLikePopDownMenu(0, -100);
			popupWindow.loadView();
			// Log.i("ListenerMovieActivity", "Rect : " + popupWindow.getSize() + ", list : " + popupWindow.getSizeList());
			popupWindow.showLikePopDownMenu(0, -(popupWindow.getOptions().size() * 40));
			break;
		case R.id.movieBtnMap:
			tracker.trackEvent("Action", "Click", "Use map button", 0);
			tracker.dispatch();
			if (model.isMapInstalled()) {
				startActivity(IntentShowtime.createMapsIntent(model.getTheater()));
			} else {
				startActivity(IntentShowtime.createMapsIntentBrowser(model.getTheater()));

			}
			break;
		case R.id.movieBtnDirection:
			tracker.trackEvent("Action", "Click", "Use map navigation button", 0);
			tracker.dispatch();
			Intent intentDirection = IntentShowtime.createMapsWithDrivingDirectionIntent(model.getTheater(), model.getGpsLocation());
			if (intentDirection != null) {
				startActivity(intentDirection);
			}
			break;
		case R.id.movieBtnCall:
			tracker.trackEvent("Action", "Click", "Use call button", 0);
			tracker.dispatch();
			startActivity(IntentShowtime.createCallIntent(model.getTheater()));
			break;
		default:
			break;
		}

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

		// bindService();

		// openDialog();
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

	public String translateDesc() throws Exception {
		MovieBean movie = model.getMovie();
		String descTlt = movie.getDescription();
		if (!model.isTranslate()) {
			descTlt = movie.getTrDescription();
			if ((descTlt == null) //
					|| (descTlt.length() == 0)) {
				Language convertLanguage = CineShowTimeEncodingUtil.convertLocaleToLanguage();
				if (!convertLanguage.equals(Language.ENGLISH)) {
					descTlt = Translate.translate(movie.getDescription(), Language.ENGLISH, convertLanguage);
					movie.setTrDescription(descTlt);
					fillDB();
				}
			}
			model.setTranslate(true);
		} else {
			model.setTranslate(false);
		}
		return descTlt;
	}

	/*
	 * Intents
	 */

	public void openImdbBrowser() {
		startActivity(IntentShowtime.createImdbBrowserIntent(model.getMovie()));
	}

	/*
	 * Interactions
	 */

	public void changePreferences() {
		projectionAdapter.changePreferences();
	}

	public interface MovieFragmentInteraction<M extends IModelMovie> extends IFragmentCineShowTimeInteraction<M> {

		Intent getIntentMovie();

	}

}
