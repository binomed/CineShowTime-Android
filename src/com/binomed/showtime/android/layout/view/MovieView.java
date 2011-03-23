package com.binomed.showtime.android.layout.view;

import java.util.List;

import android.content.Context;
import android.text.Spanned;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.TheaterBean;

public class MovieView extends LinearLayout {

	private TextView movieTitle;

	private TextView movieList;

	private MovieBean movieBean;

	private TheaterBean theaterBean;

	public MovieBean getMovieBean() {
		return movieBean;
	}

	public TheaterBean getTheaterBean() {
		return theaterBean;
	}

	public MovieView(Context context) {
		super(context);
		this.setOrientation(VERTICAL);

		// Here we build the child views in code. They could also have
		// been specified in an XML file.

		movieTitle = new TextView(context);
		addView(movieTitle, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		movieList = new TextView(context);
		addView(movieList, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	}

	public void setMovie(MovieBean movieBean, TheaterBean theaterBean) {
		this.movieBean = movieBean;
		this.theaterBean = theaterBean;

		movieTitle.setText(new StringBuilder(movieBean.getMovieName()) //
				.append(" : ").append(AndShowtimeDateNumberUtil.showMovieTimeLength(getContext(), movieBean))//
				.toString()//
				);

		List<Long> projectionList = theaterBean.getMovieMap().get(movieBean.getId());
		Spanned movieListStr = AndShowtimeDateNumberUtil.getMovieViewStr(movieBean.getId(), projectionList, getContext());

		movieList.setText(movieListStr);
	}
}
