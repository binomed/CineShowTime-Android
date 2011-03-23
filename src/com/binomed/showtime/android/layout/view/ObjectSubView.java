package com.binomed.showtime.android.layout.view;

import java.util.List;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.CineShowtimeDateNumberUtil;

public class ObjectSubView extends LinearLayout {

	private TextView movieTitle;

	private TextView movieList;

	private MovieBean movieBean;

	private TheaterBean theaterBean;

	private boolean kmUnit;

	public MovieBean getMovieBean() {
		return movieBean;
	}

	public TheaterBean getTheaterBean() {
		return theaterBean;
	}

	public ObjectSubView(Context context, boolean kmUnit) {
		super(context);
		this.setOrientation(VERTICAL);
		this.kmUnit = kmUnit;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_expandable_child_item, this);
		// Here we build the child views in code. They could also have
		// been specified in an XML file.

		movieTitle = (TextView) this.findViewById(R.id.object_sub_name);
		movieList = (TextView) this.findViewById(R.id.object_sub_subcontent_name);
	}

	public void setMovie(MovieBean movieBean, TheaterBean theaterBean, boolean distanceTime, boolean movieView, boolean blackTheme, boolean format24) {
		this.movieBean = movieBean;
		this.theaterBean = theaterBean;

		if (movieBean != null && theaterBean != null) {
			if (!movieView) {
				// movieTitle.setText(new StringBuilder(movieBean.getMovieName()) //
				// .append(" : ").append(AndShowtimeDateNumberUtil.showMovieTimeLength(getContext(), movieBean))//
				// .toString()//
				// );
				movieTitle.setText(CineShowtimeDateNumberUtil.getMovieNameViewStr(movieBean, getContext(), blackTheme));
			} else {
				// StringBuilder strTheater = new StringBuilder(theaterBean.getTheaterName()); //
				// if ((theaterBean != null) && (theaterBean.getPlace() != null) && theaterBean.getPlace().getDistance() != null) {
				// strTheater.append(" : ").append(AndShowtimeDateNumberUtil.showDistance(theaterBean.getPlace().getDistance(), !kmUnit));//
				// }
				// movieTitle.setText(strTheater.toString()//
				// );
				movieTitle.setText(CineShowtimeDateNumberUtil.getTheaterNameViewStr(theaterBean, kmUnit, blackTheme));

			}

			List<ProjectionBean> projectionList = theaterBean.getMovieMap().get(movieBean.getId());
			Long distanceTimeLong = null;
			if (distanceTime && theaterBean != null && theaterBean.getPlace() != null) {
				distanceTimeLong = theaterBean.getPlace().getDistanceTime();
			}
			Spanned movieListStr = CineShowtimeDateNumberUtil.getMovieViewStr(movieBean.getId(), theaterBean.getId(), projectionList, getContext(), distanceTimeLong, blackTheme, format24);

			movieList.setText(movieListStr);
		} else {
			movieTitle.setText("");
			movieList.setText("");
		}
	}
}
