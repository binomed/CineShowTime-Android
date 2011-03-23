package com.binomed.showtime.android.searchactivity;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.location.Location;

import com.binomed.showtime.android.util.localisation.IModelLocalisation;

public class ModelSearchActivity implements IModelLocalisation {

	private String cityName;
	private String movieName;
	private String favTheaterId;
	private Set<String> requestList;
	private Set<String> requestMovieList;
	private Location localisationSearch;
	private String lastRequestCity;
	private String lastRequestMovie;
	private String lastRequestTheaterId;
	private Calendar lastRequestDate;
	private int day;
	private int start;
	private boolean forceResearch;
	private boolean nullResult;
	private boolean resetTheme;

	public ModelSearchActivity() {
		super();
		requestList = new HashSet<String>();
		requestMovieList = new HashSet<String>();
		nullResult = false;
		resetTheme = false;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public Location getLocalisation() {
		return localisationSearch;
	}

	public void setLocalisation(Location localisationSearch) {
		this.localisationSearch = localisationSearch;
	}

	public String getFavTheaterId() {
		return favTheaterId;
	}

	public void setFavTheaterId(String favTheaterId) {
		this.favTheaterId = favTheaterId;
	}

	public Set<String> getRequestList() {
		return requestList;
	}

	public void setRequestList(Set<String> requestList) {
		this.requestList = requestList;
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

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public boolean isNullResult() {
		return nullResult;
	}

	public void setNullResult(boolean nullResult) {
		this.nullResult = nullResult;
	}

	public boolean isResetTheme() {
		return resetTheme;
	}

	public void setResetTheme(boolean resetTheme) {
		this.resetTheme = resetTheme;
	}

	public boolean isForceResearch() {
		return forceResearch;
	}

	public void setForceResearch(boolean forceResearch) {
		this.forceResearch = forceResearch;
	}

	public String getLastRequestCity() {
		return lastRequestCity;
	}

	public void setLastRequestCity(String lastRequestCity) {
		this.lastRequestCity = lastRequestCity;
	}

	public String getLastRequestMovie() {
		return lastRequestMovie;
	}

	public void setLastRequestMovie(String lastRequestMovie) {
		this.lastRequestMovie = lastRequestMovie;
	}

	public Calendar getLastRequestDate() {
		return lastRequestDate;
	}

	public void setLastRequestDate(Calendar lastRequestDate) {
		this.lastRequestDate = lastRequestDate;
	}

	public String getLastRequestTheaterId() {
		return lastRequestTheaterId;
	}

	public void setLastRequestTheaterId(String lastRequestTheaterId) {
		this.lastRequestTheaterId = lastRequestTheaterId;
	}

}
