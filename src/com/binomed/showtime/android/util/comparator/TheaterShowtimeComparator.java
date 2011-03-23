package com.binomed.showtime.android.util.comparator;

import java.util.Calendar;
import java.util.List;

import com.binomed.showtime.android.model.ProjectionBean;
import com.binomed.showtime.android.model.TheaterBean;

public class TheaterShowtimeComparator implements CineShowtimeComparator<TheaterBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(TheaterBean theater0, TheaterBean theater1) {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		long minDiff0 = -1;
		long minDiff1 = -1;
		long minDiffTemp = 0;
		int result = 0;
		List<ProjectionBean> movieShowTimeList = null;
		if (theater0 != null && theater1 != null //
				&& theater0.getMovieMap() != null && theater1.getMovieMap() != null) {
			for (String movieId : theater0.getMovieMap().keySet()) {
				movieShowTimeList = theater0.getMovieMap().get(movieId);
				for (ProjectionBean time : movieShowTimeList) {
					minDiffTemp = (time.getShowtime() - currentTime);
					if ((minDiffTemp < (minDiff0) || (minDiff0 == -1)) && (minDiffTemp > 0)) {
						minDiff0 = minDiffTemp;
					}
				}
			}
			for (String movieId : theater1.getMovieMap().keySet()) {
				movieShowTimeList = theater1.getMovieMap().get(movieId);
				for (ProjectionBean time : movieShowTimeList) {
					minDiffTemp = (time.getShowtime() - currentTime);
					if ((minDiffTemp < (minDiff1) || (minDiff1 == -1)) && (minDiffTemp > 0)) {
						minDiff1 = minDiffTemp;
					}
				}
			}

			if ((minDiff0 == -1) && (minDiff1 != -1)) {
				minDiff0 = minDiff1 + 1;
			} else if ((minDiff1 == -1) && (minDiff0 != -1)) {
				minDiff1 = minDiff0 + 1;
			}
			if (minDiff0 > minDiff1) {
				result = 1;
			} else if (minDiff0 < minDiff1) {
				result = -1;
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
		return COMPARATOR_THEATER_SHOWTIME;
	}

}
