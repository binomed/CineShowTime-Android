package com.binomed.showtime.android.movieactivity;

import android.view.View;
import android.view.View.OnClickListener;

public class ListenerMovieActivity implements OnClickListener {

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
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.movieBtnImdb:
		// controler.openImdbBrowser();
		// break;
		// case R.id.movieActor:
		// Toast.makeText(movieActivity, "Click Done on text", Toast.LENGTH_SHORT).show();
		// break;
		default:
			break;
		}

	}

}
