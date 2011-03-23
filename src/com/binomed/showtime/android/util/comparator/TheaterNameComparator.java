package com.binomed.showtime.android.util.comparator;

import java.util.Comparator;

import com.binomed.showtime.beans.TheaterBean;

public class TheaterNameComparator implements Comparator<TheaterBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(TheaterBean theater0, TheaterBean theater1) {
		int result = 0;
		if (theater0 != null && theater1 != null) {
			result = theater0.getTheaterName().compareTo(theater1.getTheaterName());
		} else if (theater0 != null) {
			result = 1;
		} else {
			result = -1;
		}
		return result;
	}

}
