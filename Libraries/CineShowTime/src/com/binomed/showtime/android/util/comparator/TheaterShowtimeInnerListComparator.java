/*
 * Copyright (C) 2011 Binomed (http://blog.binomed.fr)
 *
 * Licensed under the Eclipse Public License - v 1.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC 
 * LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM 
 * CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 */
package com.binomed.showtime.android.util.comparator;

import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;

import com.binomed.showtime.android.model.ProjectionBean;

public class TheaterShowtimeInnerListComparator implements CineShowtimeComparator<Entry<String, List<ProjectionBean>>> {

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
			if (((minDiffTemp < (minDiff0)) || (minDiff0 == -1)) && (minDiffTemp > 0)) {
				minDiff0 = minDiffTemp;
			}
		}
		for (ProjectionBean time : entry1.getValue()) {
			minDiffTemp = (time.getShowtime() - currentTime);
			if (((minDiffTemp < (minDiff1)) || (minDiff1 == -1)) && (minDiffTemp > 0)) {
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
