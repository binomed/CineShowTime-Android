package com.binomed.showtime.android.util.comparator;

import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;

import com.binomed.showtime.beans.ProjectionBean;

public class TheaterShowtimeInnerListComparator implements AndShowtimeComparator<Entry<String, List<ProjectionBean>>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Entry<String, List<ProjectionBean>> entry0, Entry<String, List<ProjectionBean>> entry1) {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		long minDiff0 = -1;
		long minDiff1 = -1;
		long minDiffTemp = 0;
		int result = 0;

		for (ProjectionBean time : entry0.getValue()) {
			minDiffTemp = (time.getShowtime() - currentTime);
			if ((minDiffTemp < (minDiff0) || (minDiff0 == -1)) && (minDiffTemp > 0)) {
				minDiff0 = minDiffTemp;
			}
		}
		for (ProjectionBean time : entry1.getValue()) {
			minDiffTemp = (time.getShowtime() - currentTime);
			if ((minDiffTemp < (minDiff1) || (minDiff1 == -1)) && (minDiffTemp > 0)) {
				minDiff1 = minDiffTemp;
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

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.util.comparator.AndShowtimeComparator#getType()
	 */
	@Override
	public int getType() {
		return COMPARATOR_THEATER_SHOWTIME_INNER_LIST;
	}

}
