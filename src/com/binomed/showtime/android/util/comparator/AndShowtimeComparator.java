package com.binomed.showtime.android.util.comparator;

import java.util.Comparator;

public interface AndShowtimeComparator<T> extends Comparator<T> {

	static final int COMPARATOR_THEATER_NAME = 0;
	static final int COMPARATOR_THEATER_DISTANCE = 1;
	static final int COMPARATOR_THEATER_SHOWTIME = 2;
	static final int COMPARATOR_THEATER_SHOWTIME_INNER_LIST = 3;
	static final int COMPARATOR_MOVIE_NAME = 4;
	static final int COMPARATOR_MOVIE_ID = 5;

	/**
	 * @return type of comparator
	 */
	int getType();

}
