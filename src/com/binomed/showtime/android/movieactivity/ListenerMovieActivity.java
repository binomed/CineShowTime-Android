package com.binomed.showtime.android.movieactivity;

import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.layout.view.GalleryTrailerView;
import com.binomed.showtime.android.layout.view.ListPopupWindow;
import com.binomed.showtime.android.layout.view.ProjectionView;
import com.binomed.showtime.android.util.BeanManagerFactory;

public class ListenerMovieActivity implements OnItemClickListener, OnTabChangeListener, OnTouchListener, OnClickListener {

	private ControlerMovieActivity controler;
	private ModelMovieActivity model;
	private AndShowTimeMovieActivity movieActivity;

	public ListenerMovieActivity(ControlerMovieActivity controler, ModelMovieActivity model, AndShowTimeMovieActivity movieActivity) {
		super();
		this.controler = controler;
		this.model = model;
		this.movieActivity = movieActivity;
	}

	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		if (view instanceof GalleryTrailerView) {
			GalleryTrailerView trailer = (GalleryTrailerView) view;
			movieActivity.startActivity(IntentShowtime.createTrailerIntent(trailer.getYoutubeBean()));
		}

	}

	@Override
	public void onTabChanged(String tabId) {
		Log.i("ListenerMovieActivity", "Change Tab : " + tabId + ", " + movieActivity.desactivListener);
		try {
			if (!movieActivity.desactivListener) {
				if (tabId.equals("Summary")) {
					// movieActivity.fillViews(model.getMovie(), false);
					movieActivity.movieFlipper.setInAnimation(AnimationHelper.inFromLeftAnimation());
					movieActivity.movieFlipper.setOutAnimation(AnimationHelper.outToRightAnimation());
					movieActivity.movieFlipper.showPrevious();
					if (movieActivity.lastTab == 2) {
						movieActivity.tabShowtimes.setVisibility(View.INVISIBLE);
						movieActivity.movieFlipper.showPrevious();
						// movieActivity.tabShowtimes.setVisibility(View.VISIBLE);
					}
					movieActivity.lastTab = 0;
				} else if (tabId.equals("Projection")) {
					// movieActivity.fillViews(model.getMovie(), false);
					if (movieActivity.lastTab == 0) {
						movieActivity.movieFlipper.setInAnimation(AnimationHelper.inFromRightAnimation());
						movieActivity.movieFlipper.setOutAnimation(AnimationHelper.outToLeftAnimation());
						movieActivity.movieFlipper.showNext();
					} else {
						movieActivity.movieFlipper.setInAnimation(AnimationHelper.inFromLeftAnimation());
						movieActivity.movieFlipper.setOutAnimation(AnimationHelper.outToRightAnimation());
						movieActivity.movieFlipper.showPrevious();
					}
					movieActivity.lastTab = 1;
				} else if (tabId.equals("Review")) {
					// movieActivity.fillViews(model.getMovie(), false);
					movieActivity.movieFlipper.setInAnimation(AnimationHelper.inFromRightAnimation());
					movieActivity.movieFlipper.setOutAnimation(AnimationHelper.outToLeftAnimation());
					movieActivity.movieFlipper.showNext();
					if (movieActivity.lastTab == 0) {
						movieActivity.tabShowtimes.setVisibility(View.INVISIBLE);
						movieActivity.movieFlipper.showNext();
						// movieActivity.tabShowtimes.setVisibility(View.VISIBLE);
					}
					movieActivity.lastTab = 2;
				}
			}
		} catch (Exception e) {
			Log.e("ListenerMovieActivity", "error during change of tab", e);
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		movieActivity.manageMotionEvent(event);
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_projection_button:
			ImageButton imageBtn = (ImageButton) v;
			ProjectionView parentView = (ProjectionView) imageBtn.getParent().getParent();

			ListPopupWindow popupWindow = new ListPopupWindow(v, movieActivity, model.getTheater(), model.getMovie(), parentView.getProjectionBean());
			// popupWindow.showLikeQuickAction(0, -30);
			// popupWindow.showLikePopDownMenu(0, -100);
			popupWindow.loadView();
			// Log.i("ListenerMovieActivity", "Rect : " + popupWindow.getSize() + ", list : " + popupWindow.getSizeList());
			popupWindow.showLikePopDownMenu(0, -(popupWindow.getOptions().size() * 40));
			break;
		case R.id.movieBtnMap:
			if (BeanManagerFactory.isMapsInstalled(movieActivity.getPackageManager())) {
				movieActivity.startActivity(IntentShowtime.createMapsIntent(model.getTheater()));
			} else {
				movieActivity.startActivity(IntentShowtime.createMapsIntentBrowser(model.getTheater()));

			}
			break;
		case R.id.movieBtnDirection:
			Intent intentDirection = IntentShowtime.createMapsWithDrivingDirectionIntent(model.getTheater(), model.getGpsLocation());
			if (intentDirection != null) {
				movieActivity.startActivity(intentDirection);
			}
			break;
		case R.id.movieBtnCall:
			movieActivity.startActivity(IntentShowtime.createCallIntent(model.getTheater()));
			break;
		default:
			break;
		}

	}
}
