package com.binomed.showtime.android.movieactivity;

import java.text.MessageFormat;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.binomed.showtime.android.R;
import com.binomed.showtime.android.activity.AndShowTimePreferencesActivity;
import com.binomed.showtime.android.adapter.view.ProjectionListAdapter;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.handler.ServiceCallBackMovie;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.AndShowtimeRequestManage;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.TheaterBean;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class AndShowTimeMovieActivity extends Activity {

	private static final String TAG = "MovieActivity"; //$NON-NLS-1$

	private static final int MENU_PREF = Menu.FIRST;
	private static final int MENU_OPEN_MAPS = Menu.FIRST + 1;
	private static final int MENU_VIDEO = Menu.FIRST + 2;
	private static final int MENU_CALL = Menu.FIRST + 3;
	private static final int ITEM_TRANSLATE = Menu.FIRST + 4;
	private static final int ITEM_SEND_SMS = Menu.FIRST + 5;
	private static final int ITEM_SEND_MAIL = Menu.FIRST + 6;

	private static final Integer REQUEST_PREF = 1;

	private TextView movieTitle;
	private TextView movieRate;
	private TextView movieDuration;
	private TextView movieDirector;
	private TextView movieActor;
	private TextView movieStyle;
	private TextView theaterTitle;
	private ImageView summaryMoviePoster;
	private TextView moviePlot;
	private TextView movieWebLinks;
	private ListView movieProjectionTimeList;
	// private Button btnImdb;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.and_showtime_movie);

		controler = ControlerMovieActivity.getInstance();
		model = controler.getModel();
		listener = new ListenerMovieActivity(controler, model, this);

		// controler.bindService();

		String movieId = getIntent().getStringExtra(ParamIntent.MOVIE_ID);
		String theaterId = getIntent().getStringExtra(ParamIntent.THEATER_ID);

		// Init star img
		bitmapRateOff = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_off);
		bitmapRateHalf = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_half);
		bitmapRateOn = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_on);

		MovieBean movie = BeanManagerFactory.getMovieForId(movieId);
		model.setMovie(movie);

		if (theaterId != null) {
			TheaterBean theater = BeanManagerFactory.getTheaterForId(theaterId);
			model.setTheater(theater);
		}

		createTabs();
		initViews();
		initlisteners();
		initMenus();
		controler.registerView(this);

		try {
			fillBasicInformations(movie);

			if (controler.isServiceRunning()) {
				openDialog();
			}

			if (movie.getImdbId() == null) {
				controler.searchMovieDetail(movie);
			} else {
				fillViews(movie);
			}
		} catch (Exception e) {
			Log.e(TAG, "error on create", e); //$NON-NLS-1$
		}

	}

	/**
	 * 
	 */
	private void initViews() {
		summaryMoviePoster = (ImageView) findViewById(R.id.moviePoster);
		movieTitle = (TextView) findViewById(R.id.movieTitle);
		movieDuration = (TextView) findViewById(R.id.movieDuration);
		moviePlot = (TextView) findViewById(R.id.moviePlot);
		theaterTitle = (TextView) findViewById(R.id.movieTheaterTitle);
		movieProjectionTimeList = (ListView) findViewById(R.id.movieListProjection);
		// movieDirector = (TextView) findViewById(R.id.movieDirector);
		// movieActor = (TextView) findViewById(R.id.movieActor);
		// movieStyle = (TextView) findViewById(R.id.movieGenre);
		// movieRate = (TextView) findViewById(R.id.movieRate);
		// btnImdb = (Button) findViewById(R.id.movieBtnImdb);

		// sumRate1 = (ImageView) findViewById(R.id.movieImgRate1);
		// sumRate2 = (ImageView) findViewById(R.id.movieImgRate2);
		// sumRate3 = (ImageView) findViewById(R.id.movieImgRate3);
		// sumRate4 = (ImageView) findViewById(R.id.movieImgRate4);
		// sumRate5 = (ImageView) findViewById(R.id.movieImgRate5);
		// sumRate6 = (ImageView) findViewById(R.id.movieImgRate6);
		// sumRate7 = (ImageView) findViewById(R.id.movieImgRate7);
		// sumRate8 = (ImageView) findViewById(R.id.movieImgRate8);
		// sumRate9 = (ImageView) findViewById(R.id.movieImgRate9);
		// sumRate10 = (ImageView) findViewById(R.id.movieImgRate10);
	}

	private void initlisteners() {
		// btnImdb.setOnClickListener(listener);
	}

	private void initMenus() {
		registerForContextMenu(moviePlot);
		registerForContextMenu(movieProjectionTimeList);
	}

	private void createTabs() {
		try {
			TabHost tabs = (TabHost) this.findViewById(R.id.movieTabhost);
			tabs.setup();

			TabHost.TabSpec tabSummary = tabs.newTabSpec("Summary");
			tabSummary.setContent(R.id.movieTab_summary);
			tabSummary.setIndicator(getResources().getString(R.string.tabSummary), getResources().getDrawable(android.R.drawable.ic_dialog_info));

			TabHost.TabSpec tabProjection = tabs.newTabSpec("Projection");
			tabProjection.setContent(R.id.movieTab_projections);
			tabProjection.setIndicator(getResources().getString(R.string.tabProjection), getResources().getDrawable(R.drawable.ic_dialog_time));

			tabs.addTab(tabSummary);
			tabs.addTab(tabProjection);
			tabs.setCurrentTab(model.getLastTab());
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
			// controler.unbindService();

			try {
				fillViews(movie);
			} catch (Exception e) {
				Log.e(TAG, "exception ", e);
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

		movieTitle.setText(Html.fromHtml( //
				new StringBuilder("<b>") //$NON-NLS-1$
						.append(getResources().getString(R.string.txtTitle)).append(" ") //$NON-NLS-1$
						.append("</b>") //$NON-NLS-1$
						.append(movie.getMovieName())//
						.toString()));

		movieDuration.setText(Html.fromHtml(new StringBuilder("<b>") //$NON-NLS-1$
				.append(getResources().getString(R.string.txtDuration)).append(" ") //$NON-NLS-1$
				.append("</b>") //$NON-NLS-1$
				.append(AndShowtimeDateNumberUtil.showMovieTimeLength(this, movie))//
				.toString()));

		List<Long> projectionList = null;
		TheaterBean theater = model.getTheater();
		if (theater != null) {
			theaterTitle.setText(theater.getTheaterName());
			projectionList = theater.getMovieMap().get(movie.getId());
		}
		ProjectionListAdapter adapter = new ProjectionListAdapter(AndShowTimeMovieActivity.this, movie, projectionList);
		movieProjectionTimeList.setAdapter(adapter);
	}

	/**
	 * @param movie
	 * @throws Exception
	 */
	private void fillViews(MovieBean movie) throws Exception {
		if ((movie.getUrlImg() != null)) {
			AndShowtimeRequestManage.completeMovieDetailStream(movie);
		}

		// if (btnImdb == null && (movie.getImdbId() != null) && (movie.getImdbId().length() != 0)) {
		// btnImdb = (Button) findViewById(R.id.movieBtnImdb);
		// btnImdb.setOnClickListener(listener);
		//
		// }
		// if (btnImdb != null) {
		// btnImdb.setClickable((movie.getImdbId() != null) && (movie.getImdbId().length() != 0));
		// }
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
			movieRate = (TextView) findViewById(R.id.movieRate);
		}
		if (movieRate != null) {
			String rate = " / 10";
			if (movie.getRate() != null) {
				rate = String.valueOf(movie.getRate()) + rate;
			} else {
				rate = "-" + rate;
			}
			movieRate.setText(Html.fromHtml(new StringBuilder("<b>") //$NON-NLS-1$
					.append(getResources().getString(R.string.txtRate)).append(" ") //$NON-NLS-1$
					.append("</b>") //$NON-NLS-1$
					.append(rate)//
					.toString()));

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
			movieStyle = (TextView) findViewById(R.id.movieGenre);
		}
		if (movieStyle != null) {
			if (style != null && style.length() != 0) {
				movieStyle.setText(//
						Html.fromHtml(new StringBuilder("<b>") //$NON-NLS-1$
								.append(getResources().getString(R.string.txtGenre)).append(" ") //$NON-NLS-1$
								.append("</b>") //$NON-NLS-1$
								.append(style.replaceAll("\\|", ", "))//
								.toString())//
						);
			}
		}

		String directorList = movie.getDirectorList();
		if (movieDirector == null && directorList != null && directorList.length() > 0) {
			movieDirector = (TextView) findViewById(R.id.movieDirector);
		}
		if (movieDirector != null) {
			if (directorList != null && directorList.length() > 0) {
				movieDirector.setText(//
						Html.fromHtml(new StringBuilder("<b>") //$NON-NLS-1$
								.append(getResources().getString(R.string.txtDirector)).append(" ") //$NON-NLS-1$
								.append("</b>") //$NON-NLS-1$
								.append(directorList.replaceAll("\\|", ", "))//
								.toString())//
						);
			}
		}

		String actorList = movie.getActorList();
		if (movieActor == null && actorList != null && actorList.length() > 0) {
			movieActor = (TextView) findViewById(R.id.movieActor);
		}
		if (movieActor != null) {
			StringBuffer actorBuffer = new StringBuffer();
			if (actorList != null && actorList.length() > 0) {
				boolean first = true;
				String[] actorArray = actorList.split("\\|");
				for (int i = 0; i < 3; i++) {
					String actor = actorArray[i];
					if (first) {
						first = false;
					} else {
						actorBuffer.append(", "); //$NON-NLS-1$
					}
					actorBuffer.append(actor);
				}
				if (actorArray.length > 3) {
					actorBuffer.append(", ...");
				}
				// for (String actor : actorList) {
				// if (first) {
				// first = false;
				// } else {
				//					actorBuffer.append(", "); //$NON-NLS-1$
				// }
				// actorBuffer.append(actor);
				// }
				movieActor.setText(//
						Html.fromHtml(new StringBuilder("<b>") //$NON-NLS-1$
								.append(getResources().getString(R.string.txtActor)).append(" ") //$NON-NLS-1$
								.append("</b>") //$NON-NLS-1$
								.append(actorBuffer.toString())//
								.toString())//
						);
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
		if (movie.getImgStream() != null) {
			Drawable drawablePoster = Drawable.createFromStream(movie.getImgStream(), "src");
			summaryMoviePoster.setImageDrawable(drawablePoster);
		} else {
			summaryMoviePoster.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_poster));
		}
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
		menu.add(0, MENU_PREF, 0, R.string.menuPreferences).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_OPEN_MAPS, 0, R.string.openMapsMenuItem).setIcon(android.R.drawable.ic_menu_mapmode);
		menu.add(0, MENU_VIDEO, 0, R.string.openYoutubeMenuItem).setIcon(R.drawable.ic_menu_play_clip);
		menu.add(0, MENU_CALL, 0, R.string.menuCall).setIcon(android.R.drawable.ic_menu_call);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PREF: {
			Intent launchPreferencesIntent = new Intent().setClass(this, AndShowTimePreferencesActivity.class);

			// Make it a subactivity so we know when it returns
			startActivityForResult(launchPreferencesIntent, REQUEST_PREF);
			return true;
		}
		case MENU_OPEN_MAPS: {
			startActivity(IntentShowtime.createMapsIntent(model.getTheater()));
			return true;
		}
		case MENU_CALL: {
			startActivity(IntentShowtime.createCallIntent(model.getTheater()));
			return true;
		}
		case MENU_VIDEO: {
			startActivity(IntentShowtime.createYoutubeIntent(model.getMovie()));
			return true;
		}
		case ITEM_TRANSLATE: {
			try {
				moviePlot.setText(controler.translateDesc());
			} catch (Exception e) {
				Log.e(TAG, "error while translating", e); //$NON-NLS-1$
			}
			return true;
		}
		case ITEM_SEND_SMS: {
			try {

				MovieBean movie = model.getMovie();
				TheaterBean theater = model.getTheater();
				long showtime = theater.getMovieMap().get(movie.getId()).get(item.getGroupId());

				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.putExtra("sms_body", MessageFormat.format(getResources().getString(R.string.smsContent) // //$NON-NLS-1$
						, movie.getMovieName() //
						, AndShowtimeDateNumberUtil.getDayString(this, showtime) //
						, AndShowtimeDateNumberUtil.showMovieTime(this, showtime) //
						, theater.getTheaterName()));
				sendIntent.setType("vnd.android-dir/mms-sms"); //$NON-NLS-1$
				startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.chooseIntentSms)));

			} catch (Exception e) {
				Log.e(TAG, "error while translating", e); //$NON-NLS-1$
			}
			return true;
		}
		case ITEM_SEND_MAIL: {
			try {

				MovieBean movie = model.getMovie();
				TheaterBean theater = model.getTheater();
				long showtime = theater.getMovieMap().get(movie.getId()).get(item.getGroupId());
				// String[] mailto = { "jean.francois.garreay@gmail.com" };
				// Create a new Intent to send messages
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setType("text/html"); //$NON-NLS-1$
				// sendIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, MessageFormat.format(getResources().getString(R.string.mailSubject), movie.getMovieName()));
				sendIntent.putExtra(Intent.EXTRA_TEXT,// 
						MessageFormat.format(getResources().getString(R.string.mailContent) //
								, movie.getMovieName() //
								, AndShowtimeDateNumberUtil.getDayString(this, showtime) //
								, AndShowtimeDateNumberUtil.showMovieTime(this, showtime) //
								, theater.getTheaterName()));
				startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.chooseIntentMail)));
			} catch (Exception e) {
				Log.e(TAG, "error while translating", e); //$NON-NLS-1$
			}
			return true;
		}
		default:
			break;
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
		MovieBean movie = model.getMovie();
		switch (v.getId()) {
		case R.id.moviePlot:
			if (movie.isImdbDesrciption() && movie.getDescription() != null) {
				menu.add(0, ITEM_TRANSLATE, 0, R.string.menuTranslate);

			}

			break;
		case R.id.movieListProjection:
			AdapterContextMenuInfo contexMenuInfo = (AdapterContextMenuInfo) menuInfo;

			TheaterBean theater = model.getTheater();
			long showtime = theater.getMovieMap().get(movie.getId()).get(contexMenuInfo.position);
			long minTime = AndShowtimeDateNumberUtil.getMinTime(theater.getMovieMap().get(movie.getId()));
			if (minTime != -1 && showtime >= minTime) {

				menu.add(contexMenuInfo.position, ITEM_SEND_SMS, 0, R.string.menuSms);
				menu.add(contexMenuInfo.position, ITEM_SEND_MAIL, 0, R.string.menuMail);
			}
			break;

		default:
			break;
		}
	}

}
