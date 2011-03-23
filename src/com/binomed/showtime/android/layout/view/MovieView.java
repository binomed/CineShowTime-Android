package com.binomed.showtime.android.layout.view;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.android.cst.AndShowtimeCst;
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
		StringBuilder movieListStr = new StringBuilder("");

		boolean first = true;
		List<Long> projectionList = theaterBean.getMovieMap().get(movieBean.getId());
		int passedShowtime;
		long minTime = AndShowtimeDateNumberUtil.getMinTime(projectionList);
		for (long projectionTime : projectionList) {
			if (!first) {
				movieListStr.append(" | ");
			} else {
				first = false;
			}
			passedShowtime = AndShowtimeDateNumberUtil.getPositionTime(projectionTime, minTime);
			switch (passedShowtime) {
			case 0:
				movieListStr.append("<FONT COLOR=\"").append(AndShowtimeCst.COLOR_WHITE).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
						.append("<b>").append(AndShowtimeDateNumberUtil.showMovieTime(getContext(), projectionTime)).append("</b>") //$NON-NLS-1$//$NON-NLS-2$
						.append("</FONT>"); //$NON-NLS-1$
				break;
			case 1:
				movieListStr.append(AndShowtimeDateNumberUtil.showMovieTime(getContext(), projectionTime));
				break;
			case -1:
				movieListStr.append("<FONT COLOR=\"").append(AndShowtimeCst.COLOR_GREY).append("\">") //$NON-NLS-1$//$NON-NLS-2$
						.append("<i>").append(AndShowtimeDateNumberUtil.showMovieTime(getContext(), projectionTime)).append("</i>") //$NON-NLS-1$ //$NON-NLS-2$
						.append("</FONT>"); //$NON-NLS-1$
				break;
			default:
				break;
			}
		}
		movieListStr.append("\n");

		movieList.setText(Html.fromHtml(movieListStr.toString()));
	}
}
