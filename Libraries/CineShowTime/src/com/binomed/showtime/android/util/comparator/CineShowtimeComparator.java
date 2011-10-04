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

import java.util.Comparator;

public interface CineShowtimeComparator<T> extends Comparator<T> {

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
