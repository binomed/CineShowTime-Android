package com.binomed.showtime.android.util.comparator;

import com.binomed.showtime.android.model.TheaterBean;

public class TheaterDistanceComparator implements CineShowtimeComparator<TheaterBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(TheaterBean theater0, TheaterBean theater1) {
		int result = 0;
		if (theater0 != null && theater1 != null) {
			if (theater0.getPlace() != null && theater1.getPlace() != null //
					&& theater0.getPlace().getDistance() != null && theater1.getPlace().getDistance() != null) {
				if (theater0.getPlace().getDistance() > theater1.getPlace().getDistance()) {
					result = 1;
				} else if (theater0.getPlace().getDistance() < theater1.getPlace().getDistance()) {
					result = -1;
				}
			} else if (theater0.getPlace() != null && theater0.getPlace().getDistance() != null) {
				result = -1;
			} else if (theater0.getPlace() != null && theater0.getPlace().getDistance() == null) {
				result = 1;
			} else {
				result = 1;
			}
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
		return COMPARATOR_THEATER_DISTANCE;
	}

}
