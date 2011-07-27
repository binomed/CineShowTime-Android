package com.binomed.showtime.android.screen.results.tablet;

import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
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
		CineShowTimeResultInteraction<IModelResultTablet> //
		, MovieFragmentInteraction<IModelResultTablet> //
{

	private static final int MENU_SORT = Menu.NONE;
	private static final int MENU_PREF = Menu.NONE + 1;

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
					btnExpand.setBackgroundResource(R.drawable.ic_btn_find_prev);
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
					btnExpand.setBackgroundResource(R.drawable.ic_btn_find_next);
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
					widthLeftLight = Double.valueOf(totalWidth * 0.20).intValue();
					dist = widthLeftFull - widthLeftLight;
					int widthRight = Double.valueOf(totalWidth * 0.80).intValue();

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

	@Override
	protected boolean delegateOnActionBarItemClick(ActionBarItem item, int position) {
		switch (position) {
		case MENU_SORT: {
			fragmentResult.openSortDialog();
			return true;
		}
		}
		return true;
	}

	@Override
	protected void addActionBarItems(ActionBar actionBar) {
		addActionBarItem(getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(new ActionBarDrawable(this, android.R.drawable.ic_menu_sort_by_size)), R.id.menuSort);

	}

	@Override
	protected boolean isHomeActivity() {
		return false;
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
			intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LATITUDE, (getModelActivity().getLocalisation() != null) ? getModelActivity().getLocalisation().getLatitude() : null);
			intentStartMovieActivity.putExtra(ParamIntent.ACTIVITY_MOVIE_LONGITUDE, (getModelActivity().getLocalisation() != null) ? getModelActivity().getLocalisation().getLongitude() : null);
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

	@Override
	public void onGroupClick() {
		if (portraitMode && !hideRight) {
			hideRight = true;
			extendList();
		}

	}

	@Override
	public void onChildClick() {
		if (portraitMode && hideRight) {
			hideRight = false;
			extendList();
		}

	}

	@Override
	public void onFocusListener(boolean focus) {
		if (portraitMode && !focus && hideRight) {
			hideRight = false;
			extendList();
		} else if (portraitMode && focus && !hideRight) {
			hideRight = true;
			extendList();
		}

	}

}