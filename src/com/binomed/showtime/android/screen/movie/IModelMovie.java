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
package com.binomed.showtime.android.screen.movie;

import android.location.Location;

import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.activity.ICineShowTimeActivityHelperModel;

public interface IModelMovie extends ICineShowTimeActivityHelperModel {

	boolean isTranslate();

	void setTranslate(boolean translate);

	int getLastTab();

	void setLastTab(int lastTab);

	MovieBean getMovie();

	void setMovie(MovieBean movie);

	TheaterBean getTheater();

	void setTheater(TheaterBean theater);

	Location getGpsLocation();

	void setGpsLocation(Location gpsLocation);

	boolean isMapInstalled();

	void setMapInstalled(boolean mapInstalled);

	boolean isDialerInstalled();

	void setDialerInstalled(boolean dialerInstalled);

	boolean isCalendarInstalled();

	void setCalendarInstalled(boolean calendarInstalled);

}
