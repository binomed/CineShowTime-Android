package com.binomed.showtime.android.util.comparator;

import com.binomed.showtime.android.model.MovieBean;

public class MovieNameComparatorFromId implements CineShowtimeComparator<MovieBean> {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.util.comparator.AndShowtimeComparator#getType()
	 */
	@Override
	public int getType() {
		return COMPARATOR_MOVIE_ID;
	}

}
