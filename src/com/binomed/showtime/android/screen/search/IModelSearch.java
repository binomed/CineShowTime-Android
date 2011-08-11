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
