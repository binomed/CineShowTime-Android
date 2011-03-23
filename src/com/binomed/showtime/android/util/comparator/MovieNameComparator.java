package com.binomed.showtime.android.util.comparator;

import java.util.Comparator;

import com.binomed.showtime.beans.MovieBean;

public class MovieNameComparator implements Comparator<MovieBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(MovieBean movie0, MovieBean movie1) {
		int result = 0;
		if (movie0 != null && movie1 != null) {
			result = movie0.getMovieName().compareTo(movie1.getMovieName());
		} else if (movie0 != null) {
			result = 1;
		} else {
			result = -1;
		}
		return result;
	}

}
