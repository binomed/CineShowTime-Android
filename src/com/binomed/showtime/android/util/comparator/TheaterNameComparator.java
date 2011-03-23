package com.binomed.showtime.android.util.comparator;

import com.binomed.showtime.android.model.TheaterBean;

public class TheaterNameComparator implements CineShowtimeComparator<TheaterBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(TheaterBean theater0, TheaterBean theater1) {
		int result = 0;
		if (theater0 != null //
				&& theater1 != null //
				&& theater0.getTheaterName() != null //
				&& theater1.getTheaterName() != null) {
			result = theater0.getTheaterName().compareTo(theater1.getTheaterName());
		} else if (theater0 != null) {
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
		return COMPARATOR_THEATER_NAME;
	}

}
