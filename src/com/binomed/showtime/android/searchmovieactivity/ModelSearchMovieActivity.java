package com.binomed.showtime.android.searchmovieactivity;

import java.util.HashSet;
import java.util.Set;

import android.location.Location;

public class ModelSearchMovieActivity {

	private String cityName;
	private String movieName;

	private String favTheaterId;

	private Set<String> requestNearList;
	private Set<String> requestMovieList;

	private Location gpsLocalisation;
	private Location localisationSearch;

	private int day;

	public ModelSearchMovieActivity() {
		super();
		requestNearList = new HashSet<String>();
		requestMovieList = new HashSet<String>();
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Location getGpsLocalisation() {
		return gpsLocalisation;
	}

	public void setGpsLocalisation(Location gpsLocalisation) {
		this.gpsLocalisation = gpsLocalisation;
	}

	public Location getLocalisationSearch() {
		return localisationSearch;
	}

	public void setLocalisationSearch(Location localisationSearch) {
		this.localisationSearch = localisationSearch;
	}

	public String getFavTheaterId() {
		return favTheaterId;
	}

	public void setFavTheaterId(String favTheaterId) {
		this.favTheaterId = favTheaterId;
	}

	public Set<String> getNearRequestList() {
		return requestNearList;
	}

	public void setNearRequestList(Set<String> requestList) {
		this.requestNearList = requestList;
	}

	public Set<String> getRequestMovieList() {
		return requestMovieList;
	}

	public void setRequestMovieList(Set<String> requestMovieList) {
		this.requestMovieList = requestMovieList;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

}
