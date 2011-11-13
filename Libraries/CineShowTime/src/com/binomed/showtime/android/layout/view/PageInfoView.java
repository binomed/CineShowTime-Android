/*
 * Copyright (C) 2011 Binomed (http://blog.binomed.fr)
 *
 * Licensed under the Eclipse Public License - v 1.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC 
 * LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM 
 * CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 */
package com.binomed.showtime.android.layout.view;

import pl.polidea.coverflow.CoverFlow;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.adapter.view.CoverFlowTrailerAdapter;
import com.binomed.showtime.android.adapter.view.GalleryTrailerAdapter;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.YoutubeBean;
import com.binomed.showtime.android.screen.movie.IModelMovie;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;
import com.binomed.showtime.android.util.CineShowtimeRequestManage;
import com.binomed.showtime.android.util.images.ImageDownloader;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class PageInfoView extends LinearLayout implements OnItemClickListener, OnLongClickListener, OnItemSelectedListener {

	private static final String TAG = "CineShowTime-PageInfoView";

	private TextView movieTitle, txtMovieTitle;
	private TextView movieRate, txtMovieRate;
	private TextView movieDuration, txtMovieDuration;
	private TextView movieDirector, txtMovieDirector;
	private TextView movieActor, txtMovieActor;
	private TextView movieStyle, txtMovieStyle;
	private ImageView summaryMoviePoster;
	private TextView moviePlot;
	private TextView movieWebLinks;
	private LinearLayout videoSeparator;
	// private Gallery movieGalleryTrailer;
	private CoverFlow movieGalleryTrailer;
	private TextView movieGalleryTrailerSelect;

	protected GalleryTrailerAdapter trailerAdapter;

	private ImageView sumRate1, sumRate2, sumRate3, sumRate4, sumRate5, sumRate6, sumRate7, sumRate8, sumRate9, sumRate10;
	private Bitmap bitmapRateOff;
	private Bitmap bitmapRateHalf;
	private Bitmap bitmapRateOn;

	private IModelMovie model;
	private GoogleAnalyticsTracker tracker;
	private ImageDownloader imageDownloader;
	private CallBack callBack;

	public PageInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PageInfoView(Context context) {
		super(context);
		init();
	}

	public void changeData(IModelMovie model, GoogleAnalyticsTracker tracker, CallBack callBack) {
		this.model = model;
		this.tracker = tracker;
		this.callBack = callBack;
	}

	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.tab_movie_info, this);

		// Init star img
		bitmapRateOff = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_off);
		bitmapRateHalf = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_half);
		bitmapRateOn = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_on);

		imageDownloader = new ImageDownloader();

		initView();
		initListeners();
	}

	private void initView() {
		summaryMoviePoster = (ImageView) findViewById(R.id.moviePoster);
		// txtMovieTitle = (TextView) findViewById(R.id.txtMovieTitle);
		movieTitle = (TextView) findViewById(R.id.movieTitle);
		txtMovieDuration = (TextView) findViewById(R.id.txtMovieDuration);
		movieDuration = (TextView) findViewById(R.id.movieDuration);
		moviePlot = (TextView) findViewById(R.id.moviePlot);
		// movieGalleryTrailer = (Gallery) findViewById(R.id.gallery_trailer);
		movieGalleryTrailer = (CoverFlow) findViewById(R.id.gallery_trailer);
		movieGalleryTrailer.setImageWidth(getContext().getResources().getDimension(R.dimen.cstTrailerWidth));
		movieGalleryTrailer.setImageHeight(getContext().getResources().getDimension(R.dimen.cstTrailerHeight));
		movieGalleryTrailerSelect = (TextView) findViewById(R.id.gallery_trailer_select);
		videoSeparator = (LinearLayout) findViewById(R.id.videosSeparator);

		movieWebLinks = (TextView) findViewById(R.id.movieWebLinks);
		txtMovieRate = (TextView) findViewById(R.id.txtMovieRate);
		movieRate = (TextView) findViewById(R.id.movieRate);

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

		txtMovieStyle = (TextView) findViewById(R.id.txtMovieGenre);
		movieStyle = (TextView) findViewById(R.id.movieGenre);

		txtMovieDirector = (TextView) findViewById(R.id.txtMovieDirector);
		movieDirector = (TextView) findViewById(R.id.movieDirector);

		txtMovieActor = (TextView) findViewById(R.id.txtMovieActor);
		movieActor = (TextView) findViewById(R.id.movieActor);

	}

	private void initListeners() {
		movieGalleryTrailer.setOnItemClickListener(this);
		movieGalleryTrailer.setOnItemSelectedListener(this);
		moviePlot.setOnLongClickListener(this);

	}

	/**
	 * @param movie
	 */
	public void fillBasicInformations(MovieBean movie) {

		movieTitle.setText(movie.getMovieName());

		txtMovieDuration.setText(getResources().getString(R.string.txtDuration));
		movieDuration.setText(CineShowtimeDateNumberUtil.showMovieTimeLength(getContext(), movie));

	}

	/**
	 * @param movie
	 * @throws Exception
	 */
	public void fillViews(MovieBean movie) throws Exception {

		fillBasicInformations(movie);

		if ((movie.getUrlImg() != null)) {
			try {
				CineShowtimeRequestManage.completeMovieDetailStream(movie);
			} catch (Exception e) {
				Log.w(TAG, "Error getting movie img : ", e);
			}
		}

		if (((movie.getImdbId() != null) && (movie.getImdbId().length() != 0)) //

				|| ((movie.getUrlWikipedia() != null) && (movie.getUrlWikipedia().length() != 0)) //
		) {
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

		if ((movie.getRate() != null)) {
			String rate = " / 10";
			if (movie.getRate() != null) {
				rate = String.valueOf(movie.getRate()) + rate;
			} else {
				rate = "-" + rate;
			}
			txtMovieRate.setText(getResources().getString(R.string.txtRate));
			movieRate.setText(rate);

			fillRateImg(movie.getRate());
		}

		String style = movie.getStyle();
		if ((style != null) && (style.length() != 0)) {
			if ((style != null) && (style.length() != 0)) {
				txtMovieStyle.setText(getResources().getString(R.string.txtGenre));
				movieStyle.setText(style.replaceAll("\\|", ", "));
			}
		}

		String directorList = movie.getDirectorList();
		if ((directorList != null) && (directorList.length() > 0)) {
			if ((directorList != null) && (directorList.length() > 0)) {
				txtMovieDirector.setText(getResources().getString(R.string.txtDirector));
				movieDirector.setText(directorList.replaceAll("\\|", ", "));
			}
		}

		String actorList = movie.getActorList();
		if ((actorList != null) && (actorList.length() > 0)) {
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

		// boolean checkboxPreference;
		// SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		// checkboxPreference = prefs.getBoolean(this.getResources().getString(R.string.preference_lang_key_auto_translate), false);
		// TODO a remettre quand j'aurais une api de traduction

		if ((movie.getDescription() != null)) {
			String descTlt = movie.getDescription();
			// if (checkboxPreference) {
			// descTlt = Translate.translate(movie.getDescription(), Language.ENGLISH, Language.FRENCH);
			// movie.setTrDescription(descTlt);
			// callBack.fillDB();
			// model.setTranslate(true);
			// } else {
			model.setTranslate(false);
			// }
			moviePlot.setText(descTlt);
		} else {
			moviePlot.setText(getResources().getString(R.string.noSummary));
		}
		if (movie.getUrlImg() != null) {
			imageDownloader.download(movie.getUrlImg(), summaryMoviePoster, getContext());
		} else {
			summaryMoviePoster.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_poster));
		}

		if ((movie.getYoutubeVideos() != null) && !movie.getYoutubeVideos().isEmpty()) {
			// this.movieGalleryTrailer.setAdapter(new GalleryTrailerAdapter(getContext(), movie.getYoutubeVideos(), imageDownloader));
			this.movieGalleryTrailer.setAdapter(new CoverFlowTrailerAdapter(getContext(), movie.getYoutubeVideos(), imageDownloader));
			this.movieGalleryTrailer.setSelection(movie.getYoutubeVideos().size() / 2, true);
			this.movieGalleryTrailer.setVisibility(View.VISIBLE);
			this.movieGalleryTrailerSelect.setVisibility(View.VISIBLE);
			this.videoSeparator.setVisibility(View.VISIBLE);
		} else {
			this.movieGalleryTrailer.setVisibility(View.GONE);
			this.videoSeparator.setVisibility(View.GONE);
			this.movieGalleryTrailerSelect.setVisibility(View.GONE);
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

	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		YoutubeBean trailer = model.getMovie().getYoutubeVideos().get(groupPosition);
		tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_MOVIE // Category
				, CineShowtimeCst.ANALYTICS_ACTION_OPEN // Action
				, CineShowtimeCst.ANALYTICS_LABEL_MOVIE_ACTIONS_TRAILER // Label
				, 0 // Value
		);
		getContext().startActivity(IntentShowtime.createTrailerIntent(trailer));

	}

	@Override
	public boolean onLongClick(View v) {
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
		// TODO
		// Part for selection
		// TODO
		// switch (item.getItemId()) {
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
		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> adpater, View view, int groupPosition, long id) {
		movieGalleryTrailerSelect.setText(model.getMovie().getYoutubeVideos().get(groupPosition).getVideoName());
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public interface CallBack {

		void fillDB();

	}

}
