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
		if ((movie0 != null) && (movie1 != null)) {
			if ((movie0.getMovieName() != null) && (movie1.getMovieName() != null)) {
				result = movie0.getMovieName().compareTo(movie1.getMovieName());
			} else if (movie0.getMovieName() != null) {
				result = 1;
			} else {
				result = -1;
			}
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
