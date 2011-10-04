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
package com.binomed.showtime.android.screen.search;

import java.util.Set;

import com.binomed.showtime.android.util.activity.ICineShowTimeActivityHelperModel;
import com.binomed.showtime.android.util.localisation.IModelLocalisation;

public interface IModelSearch extends IModelLocalisation, ICineShowTimeActivityHelperModel {

	String getCityName();

	void setCityName(String cityName);

	String getMovieName();

	void setMovieName(String movieName);

	String getFavTheaterId();

	void setFavTheaterId(String favTheaterId);

	Set<String> getRequestList();

	void setRequestList(Set<String> requestList);

	Set<String> getRequestMovieList();

	void setRequestMovieList(Set<String> requestMovieList);

	int getDay();

	void setDay(int day);

	int getStart();

	void setStart(int start);

	boolean isForceResearch();

	void setForceResearch(boolean forceResearch);

	String getLastRequestCity();

	void setLastRequestCity(String lastRequestCity);

	String getLastRequestMovie();

	void setLastRequestMovie(String lastRequestMovie);

	String getLastRequestTheaterId();

	void setLastRequestTheaterId(String lastRequestTheaterId);

}
