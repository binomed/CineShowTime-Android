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
		if ((theater0 != null) && (theater1 != null)) {
			if ((theater0.getPlace() != null) && (theater1.getPlace() != null //
					) && (theater0.getPlace().getDistance() != null) && (theater1.getPlace().getDistance() != null)) {
				if (theater0.getPlace().getDistance() > theater1.getPlace().getDistance()) {
					result = 1;
				} else if (theater0.getPlace().getDistance() < theater1.getPlace().getDistance()) {
					result = -1;
				}
			} else if ((theater0.getPlace() != null) && (theater0.getPlace().getDistance() != null)) {
				result = -1;
			} else if ((theater0.getPlace() != null) && (theater0.getPlace().getDistance() == null)) {
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
