package com.binomed.showtime.android.movieactivity;

import java.net.URLDecoder;
import java.util.List;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.GalleryTrailerAdapter;
import com.binomed.showtime.android.adapter.view.ProjectionListAdapter;
import com.binomed.showtime.android.adapter.view.ReviewListAdapter;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.handler.MovieCallBackMovie;
import com.binomed.showtime.android.handler.ServiceCallBackMovie;
import com.binomed.showtime.android.util.AndShowTimeEncodingUtil;
import com.binomed.showtime.android.util.AndShowTimeLayoutUtils;
import com.binomed.showtime.android.util.AndShowTimeMenuUtil;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.AndShowtimeRequestManage;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.images.ImageDownloader;
import com.binomed.showtime.beans.LocalisationBean;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.ProjectionBean;
import com.binomed.showtime.beans.TheaterBean;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

//public class AndShowTimeMovieActivity extends Activity {
public class AndShowTimeMovieActivity extends TabActivity {

	private static final String TAG = "MovieActivity"; //$NON-NLS-1$

	private static final int ITEM_TRANSLATE = Menu.FIRST + 2;

	private TabHost tabHost;
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

	private Bitmap bitmapRateOff;
	private Bitmap bitmapRateHalf;
	private Bitmap bitmapRateOn;

	private ProgressDialog progressDialog;
	/**
	 * The invoked service
	 */
	private ControlerMovieActivity controler;
	private ListenerMovieActivity listener;
	private ModelMovieActivity model;

	/*
	 * attributes
	 */
	private long minTime;
	private boolean distanceTime;
	private boolean homePress;

	private float oldTouchValue;
	protected boolean desactivListener = false;
	protected int lastTab = 0;

	// private DrawableManager drawableManager;
	private ImageDownloader imageDownloader;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		AndShowTimeLayoutUtils.onActivityCreateSetTheme(this, prefs);
		setContentView(R.layout.activity_movie);

		controler = ControlerMovieActivity.getInstance();
		model = controler.getModel();
		listener = new ListenerMovieActivity(controler, model, this);
		// Init star img
		bitmapRateOff = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_off);
		bitmapRateHalf = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_half);
		bitmapRateOn = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_on);

		createTabs();
		initViews();
		initlisteners();
		initMenus();
		controler.registerView(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		homePress = false;

		String movieId = null;
		String theaterId = null;
		boolean fromWidget = getIntent().getBooleanExtra(ParamIntent.ACTIVITY_MOVIE_FROM_WIDGET, false);
		String near = getIntent().getStringExtra(ParamIntent.ACTIVITY_MOVIE_NEAR);
		Log.i(TAG, "From Widget : " + fromWidget);
		if (fromWidget) {

			Object[] currentMovie = controler.extractCurrentMovie();
			if (currentMovie != null) {
				TheaterBean theaterTmp = (TheaterBean) currentMovie[0];
				MovieBean movieTmp = (MovieBean) currentMovie[1];

				theaterId = (theaterTmp != null) ? theaterTmp.getId() : null;
				movieId = (movieTmp != null) ? movieTmp.getId() : null;
			}
		} else {
			movieId = getIntent().getStringExtra(ParamIntent.MOVIE_ID);

			theaterId = getIntent().getStringExtra(ParamIntent.THEATER_ID);
			double latitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_MOVIE_LATITUDE, 0);
			double longitude = getIntent().getDoubleExtra(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, 0);
			if (latitude != 0 && longitude != 0) {
				Location gpsLocation = new Location("GPS"); //$NON-NLS-1$
				gpsLocation.setLatitude(latitude);
				gpsLocation.setLongitude(longitude);
				model.setGpsLocation(gpsLocation);
			} else {
				model.setGpsLocation(null);
			}
		}
		Log.i(TAG, "Movie ID : " + movieId);

		MovieBean movie = BeanManagerFactory.getMovieForId(movieId);
		model.setMovie(movie);

		if (theaterId != null) {
			TheaterBean theater = BeanManagerFactory.getTheaterForId(theaterId);
			model.setTheater(theater);
		}

		manageViewVisibility();

		try {
			fillBasicInformations(movie);

			if (controler.isServiceRunning()) {
				openDialog();
			}

			if (movie.getImdbId() == null) {
				controler.searchMovieDetail(movie, near);
			} else {
				fillViews(movie, false);
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
				|| !BeanManagerFactory.isMapsInstalled(getPackageManager())//
		) {
			movieBtnDirection.setEnabled(false);
		}
		if (!BeanManagerFactory.isDialerInstalled(getPackageManager())) {
			movieBtnCall.setEnabled(false);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
		// if (homePress) {
		// finish();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void finish() {
		boolean resetTheme = getIntent() != null ? getIntent().getBooleanExtra(ParamIntent.PREFERENCE_RESULT_THEME, false) : false;
		if (resetTheme) {
			setResult(AndShowtimeCst.RESULT_PREF_WITH_NEW_THEME);
		}
		super.finish();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop : ");
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "onKeyDown : " + (keyCode == KeyEvent.KEYCODE_HOME));
		homePress = (keyCode == KeyEvent.KEYCODE_HOME);
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent touchEvent) {
		manageMotionEvent(touchEvent);
		return false;
	}

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
			if (diff > (getTabWidget().getWidth() / 2)) {
				Log.i("MainActivity4", "oldTouch: " + oldTouchValue + "; currentX : " + currentX + "; tabHos : " + tabHost.getCurrentTab());
				try {
					if (oldTouchValue > currentX && tabHost.getCurrentTab() <= 1) {
						// fillViews(model.getMovie(), false);
						movieFlipper.setInAnimation(AnimationHelper.inFromRightAnimation());
						movieFlipper.setOutAnimation(AnimationHelper.outToLeftAnimation());
						movieFlipper.showNext();
						desactivListener = true;
						tabHost.setCurrentTab(tabHost.getCurrentTab() + 1);
						lastTab = tabHost.getCurrentTab();
						desactivListener = false;
					} else if (oldTouchValue < currentX && tabHost.getCurrentTab() >= 1) {
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
	private void initViews() {

		movieFlipper = (ViewFlipper) findViewById(R.id.movieFlipper);

		summaryMoviePoster = (ImageView) findViewById(R.id.moviePoster);
		// txtMovieTitle = (TextView) findViewById(R.id.txtMovieTitle);
		movieTitle = (TextView) findViewById(R.id.movieTitle);
		txtMovieDuration = (TextView) findViewById(R.id.txtMovieDuration);
		movieDuration = (TextView) findViewById(R.id.movieDuration);
		moviePlot = (TextView) findViewById(R.id.moviePlot);
		theaterTitle = (TextView) findViewById(R.id.movieTheaterTitle);
		theaterAddress = (TextView) findViewById(R.id.movieTheaterAddress);
		movieProjectionTimeList = (ListView) findViewById(R.id.movieListProjection);
		movieReviewsList = (ListView) findViewById(R.id.movieListReview);
		movieGalleryTrailer = (Gallery) findViewById(R.id.gallery_trailer);

		movieTabInfoScrollView = (ScrollView) findViewById(R.id.movieTab_summary);

		tabShowtimes = (RelativeLayout) findViewById(R.id.Projection);

		movieBtnMap = (ImageButton) findViewById(R.id.movieBtnMap);
		movieBtnDirection = (ImageButton) findViewById(R.id.movieBtnDirection);
		movieBtnCall = (ImageButton) findViewById(R.id.movieBtnCall);

		// drawableManager = new DrawableManager();
		imageDownloader = new ImageDownloader();
	}

	private void initlisteners() {
		movieGalleryTrailer.setOnItemClickListener(listener);
		tabHost.setOnTabChangedListener(listener);

		movieTabInfoScrollView.setOnTouchListener(listener);
		movieProjectionTimeList.setOnTouchListener(listener);
		movieReviewsList.setOnTouchListener(listener);

		movieBtnMap.setOnClickListener(listener);
		movieBtnDirection.setOnClickListener(listener);
		movieBtnCall.setOnClickListener(listener);
	}

	private void initMenus() {
		registerForContextMenu(moviePlot);
		registerForContextMenu(movieProjectionTimeList);
	}

	private void createTabs() {
		try {
			// tabHost = (TabHost) this.findViewById(R.id.movieTabhost);
			tabHost = getTabHost();
			// tabHost.setup();

			Intent intentEmptyActivity = new Intent(AndShowTimeMovieActivity.this, EmptyActivity.class);

			TabHost.TabSpec tabSummary = tabHost.newTabSpec("Summary");
			// tabSummary.setContent(R.id.movieTab_summary);
			tabSummary.setContent(intentEmptyActivity);
			// tabSummary.setIndicator(new TabInfoView(this, getResources().getString(R.string.movieLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_info)));
			tabSummary.setIndicator(getResources().getString(R.string.movieLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_info));

			TabHost.TabSpec tabProjection = tabHost.newTabSpec("Projection");
			// tabProjection.setContent(R.id.movieTab_projections);
			tabProjection.setContent(intentEmptyActivity);
			// tabSummary.setIndicator(new TabInfoView(this, getResources().getString(R.string.showtimeLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_showtimes)));
			tabProjection.setIndicator(getResources().getString(R.string.showtimeLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_showtimes));

			TabHost.TabSpec tabReviews = tabHost.newTabSpec("Review");
			// tabReviews.setContent(R.id.movieTab_reviews);
			tabReviews.setContent(intentEmptyActivity);
			// tabSummary.setIndicator(new TabInfoView(this, getResources().getString(R.string.rateLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_review)));
			tabReviews.setIndicator(getResources().getString(R.string.rateLabel).toUpperCase(), getResources().getDrawable(R.drawable.tab_review));

			tabHost.addTab(tabSummary);
			tabHost.addTab(tabProjection);
			tabHost.addTab(tabReviews);
			tabHost.setCurrentTab(model.getLastTab());
		} catch (Exception e1) {
			Log.e(TAG, "error while init Movie acitivty", e1); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		controler.unbindService();
		// controler.closeDB();
	}

	/**
	 * The call back message handler
	 */
	public ServiceCallBackMovie m_callbackHandler = new ServiceCallBackMovie() {

		@Override
		public void handleInputRecived(String idMovie) {

			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			MovieBean movie = BeanManagerFactory.getMovieForId(idMovie);
			model.setMovie(movie);

			controler.fillDB();

			try {
				fillViews(movie, false);
			} catch (Exception e) {
				Log.e(TAG, "exception ", e);
			}

		}

	};

	public MovieCallBackMovie innerCallBack = new MovieCallBackMovie() {

		@Override
		public void handleInputRecived(int tabIndex) {
			try {
				fillViews(model.getMovie(), true);
			} catch (Exception e) {
				Log.e(TAG, "error during filling", e);
			}

		}
	};

	public void openDialog() {
		progressDialog = ProgressDialog.show(AndShowTimeMovieActivity.this,
		//
				AndShowTimeMovieActivity.this.getResources().getString(R.string.movieProgressTitle)//
				, AndShowTimeMovieActivity.this.getResources().getString(R.string.movieProgressMsg) //
				, true, false);
	}

	/**
	 * @param movie
	 */
	private void fillBasicInformations(MovieBean movie) {

		movieTitle.setText(movie.getMovieName());

		txtMovieDuration.setText(getResources().getString(R.string.txtDuration));
		movieDuration.setText(AndShowtimeDateNumberUtil.showMovieTimeLength(this, movie));

	}

	/**
	 * @param movie
	 * @throws Exception
	 */
	protected void fillViews(MovieBean movie, boolean withAdapter) throws Exception {

		Log.i(TAG, "FillViews : " + withAdapter);
		if ((movie.getUrlImg() != null)) {
			AndShowtimeRequestManage.completeMovieDetailStream(movie);
		}

		if (movieWebLinks == null //
				&& ((movie.getImdbId() != null) && (movie.getImdbId().length() != 0)) //
				|| ((movie.getUrlWikipedia() != null) && (movie.getUrlWikipedia().length() != 0)) //
		) {
			movieWebLinks = (TextView) findViewById(R.id.movieWebLinks);
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

		if (movieRate == null && movie.getRate() != null) {
			txtMovieRate = (TextView) findViewById(R.id.txtMovieRate);
			movieRate = (TextView) findViewById(R.id.movieRate);
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

			sumRate1 = (ImageView) findViewById(R.id.movieImgRate1);
			sumRate2 = (ImageView) findViewById(R.id.movieImgRate2);
			sumRate3 = (ImageView) findViewById(R.id.movieImgRate3);
			sumRate4 = (ImageView) findViewById(R.id.movieImgRate4);
			sumRate5 = (ImageView) findViewById(R.id.movieImgRate5);
			sumRate6 = (ImageView) findViewById(R.id.movieImgRate6);
			sumRate7 = (ImageView) findViewById(R.id.movieImgRate7);
			sumRate8 = (ImageView) findViewById(R.id.movieImgRate8);
			sumRate9 = (ImageView) findViewById(R.id.movieImgRate9);
			sumRate10 = (ImageView) findViewById(R.id.movieImgRate10);
			fillRateImg(movie.getRate());
		}

		String style = movie.getStyle();
		if (movieStyle == null && style != null && style.length() != 0) {
			txtMovieStyle = (TextView) findViewById(R.id.txtMovieGenre);
			movieStyle = (TextView) findViewById(R.id.movieGenre);
		}
		if (movieStyle != null) {
			if (style != null && style.length() != 0) {
				txtMovieStyle.setText(getResources().getString(R.string.txtGenre));
				movieStyle.setText(style.replaceAll("\\|", ", "));
			}
		}

		String directorList = movie.getDirectorList();
		if (movieDirector == null && directorList != null && directorList.length() > 0) {
			txtMovieDirector = (TextView) findViewById(R.id.txtMovieDirector);
			movieDirector = (TextView) findViewById(R.id.movieDirector);
		}
		if (movieDirector != null) {
			if (directorList != null && directorList.length() > 0) {
				txtMovieDirector.setText(getResources().getString(R.string.txtDirector));
				movieDirector.setText(directorList.replaceAll("\\|", ", "));
			}
		}

		String actorList = movie.getActorList();
		if (movieActor == null && actorList != null && actorList.length() > 0) {
			txtMovieActor = (TextView) findViewById(R.id.txtMovieActor);
			movieActor = (TextView) findViewById(R.id.movieActor);
		}
		if (movieActor != null) {
			StringBuffer actorBuffer = new StringBuffer();
			if (actorList != null && actorList.length() > 0) {
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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		checkboxPreference = prefs.getBoolean(this.getResources().getString(R.string.preference_lang_key_auto_translate), false);

		if ((movie.getDescription() != null)) {
			String descTlt = movie.getDescription();
			if (checkboxPreference) {
				descTlt = Translate.translate(movie.getDescription(), Language.ENGLISH, Language.FRENCH);
				movie.setTrDescription(descTlt);
				controler.fillDB();
				model.setTranslate(true);
			} else {
				model.setTranslate(false);
			}
			moviePlot.setText(descTlt);
		} else {
			moviePlot.setText(getResources().getString(R.string.noSummary));
		}
		if (movie.getUrlImg() != null) {
			imageDownloader.download(movie.getUrlImg(), summaryMoviePoster);
		} else {
			summaryMoviePoster.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_poster));
		}

		TheaterBean theater = model.getTheater();
		if (theater != null) {
			theaterTitle.setText(theater.getTheaterName());

			LocalisationBean place = theater.getPlace();
			if (place != null) {
				try {
					theaterAddress.setText(URLDecoder.decode(place.getSearchQuery(), AndShowTimeEncodingUtil.getEncoding()));
				} catch (Exception e) {
					Log.e(TAG, "error decoding address", e);
				}
			}
		}

		if (movie.getYoutubeVideos() != null && !movie.getYoutubeVideos().isEmpty()) {
			this.movieGalleryTrailer.setAdapter(new GalleryTrailerAdapter(this, movie.getYoutubeVideos(), imageDownloader));
		}
		List<ProjectionBean> projectionList = null;
		distanceTime = prefs.getBoolean(this.getResources().getString(R.string.preference_loc_key_time_direction)//
				, false);
		Long distanceTimeLong = null;
		if (theater != null) {
			projectionList = theater.getMovieMap().get(movie.getId());
			if (distanceTime && theater.getPlace() != null) {
				distanceTimeLong = theater.getPlace().getDistanceTime();
			}
		}

		projectionAdapter = new ProjectionListAdapter(AndShowTimeMovieActivity.this //
				, movie //
				, projectionList //
				, AndShowtimeDateNumberUtil.getMinTime(projectionList, distanceTimeLong) //
				, listener//
		);
		movieProjectionTimeList.setAdapter(projectionAdapter);
		this.movieReviewsList.setAdapter(new ReviewListAdapter(this, movie.getReviews()));
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
				if (rate > 9.5 && rate < 10) {
					rate10 = R.drawable.rate_star_small_half;
				}
			case 8:
				rate8 = R.drawable.rate_star_small_on;
				if (rate > 8.5 && rate < 9) {
					rate9 = R.drawable.rate_star_small_half;
				}
			case 7:
				rate7 = R.drawable.rate_star_small_on;
				if (rate > 7.5 && rate < 8) {
					rate8 = R.drawable.rate_star_small_half;
				}
			case 6:
				rate6 = R.drawable.rate_star_small_on;
				if (rate > 6.5 && rate < 7) {
					rate7 = R.drawable.rate_star_small_half;
				}
			case 5:
				rate5 = R.drawable.rate_star_small_on;
				if (rate > 5.5 && rate < 6) {
					rate6 = R.drawable.rate_star_small_half;
				}
			case 4:
				rate4 = R.drawable.rate_star_small_on;
				if (rate > 4.5 && rate < 5) {
					rate5 = R.drawable.rate_star_small_half;
				}
			case 3:
				rate3 = R.drawable.rate_star_small_on;
				if (rate > 3.5 && rate < 4) {
					rate4 = R.drawable.rate_star_small_half;
				}
			case 2:
				rate2 = R.drawable.rate_star_small_on;
				if (rate > 2.5 && rate < 3) {
					rate3 = R.drawable.rate_star_small_half;
				}
			case 1:
				rate1 = R.drawable.rate_star_small_on;
				if (rate > 1.5 && rate < 2) {
					rate2 = R.drawable.rate_star_small_half;
				}
			case 0:
				if (rate > 0.5 && rate < 1) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Log.i(TAG, "onCreateOptionMenu : ");
		// menu.add(0, MENU_VIDEO, 2, R.string.openYoutubeMenuItem).setIcon(R.drawable.ic_menu_play_clip);
		// AndShowTimeMenuUtil.createMenu(menu, MENU_PREF, 3);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.i(TAG, "onMenuItemSelect : " + item.getItemId());
		// if (AndShowTimeMenuUtil.onMenuItemSelect(this, MENU_PREF, item.getItemId())) {
		// projectionAdapter.changePreferences();
		// return true;
		// }
		// switch (item.getItemId()) {
		// case MENU_VIDEO: {
		// startActivity(IntentShowtime.createYoutubeIntent(model.getMovie()));
		// return true;
		// }
		// case ITEM_TRANSLATE: {
		// try {
		// moviePlot.setText(controler.translateDesc());
		// } catch (Exception e) {
		//				Log.e(TAG, "error while translating", e); //$NON-NLS-1$
		// }
		// return true;
		// }
		// default:
		// break;
		// }

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
		MovieBean movie = model.getMovie();
		switch (v.getId()) {
		case R.id.moviePlot:
			if (movie.isImdbDesrciption() && movie.getDescription() != null) {
				menu.add(0, ITEM_TRANSLATE, 0, R.string.menuTranslate);

			}

			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == AndShowtimeCst.ACTIVITY_RESULT_PREFERENCES) {
			projectionAdapter.changePreferences();
			if (AndShowTimeMenuUtil.manageResult(this, requestCode, resultCode, data)) {
				return;
			}
		}

	}

}
