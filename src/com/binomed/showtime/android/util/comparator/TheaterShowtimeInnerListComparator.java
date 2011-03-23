package com.binomed.showtime.android.util.comparator;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

public class TheaterShowtimeInnerListComparator implements Comparator<Entry<String, List<Long>>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Entry<String, List<Long>> entry0, Entry<String, List<Long>> entry1) {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		long minDiff0 = -1;
		long minDiff1 = -1;
		long minDiffTemp = 0;
		int result = 0;

		for (Long time : entry0.getValue()) {
			minDiffTemp = (time - currentTime);
			if ((minDiffTemp < (minDiff0) || (minDiff0 == -1)) && (minDiffTemp > 0)) {
				minDiff0 = minDiffTemp;
			}
		}
		for (Long time : entry1.getValue()) {
			minDiffTemp = (time - currentTime);
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

}
