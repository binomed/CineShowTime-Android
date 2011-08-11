package com.binomed.showtime.android.screen.main;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.location.Location;

import com.binomed.showtime.android.screen.search.IModelSearch;

public class ModelMainFragment implements IModelSearch {

	private Calendar lastRequestDate;
	private boolean nullResult;
	private boolean resetTheme;

	private String cityName;
	private String movieName;
	private String favTheaterId;
	private Set<String> requestList;
	private Set<String> requestMovieList;
	private Location localisationSearch;
	private String lastRequestCity;
	private String lastRequestMovie;
	private String lastRequestTheaterId;
	private int day;
	private int start;
	private boolean forceResearch;

	public ModelMainFragment() {
		super();
		nullResult = false;
		resetTheme = false;
		requestList = new HashSet<String>();
		requestMovieList = new HashSet<String>();
	}

	public Calendar getLastRequestDate() {
		return lastRequestDate;
	}

	public void setLastRequestDate(Calendar lastRequestDate) {
		this.lastRequestDate = lastRequestDate;
	}

	@Override
	public boolean isNullResult() {
		return nullResult;
	}

	@Override
	public void setNullResult(boolean nullResult) {
		this.nullResult = nullResult;
	}

	@Override
	public boolean isResetTheme() {
		return resetTheme;
	}

	@Override
	public void setResetTheme(boolean resetTheme) {
		this.resetTheme = resetTheme;
	}

	@Override
	public String getCityName() {
		return cityName;
	}

	@Override
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Override
	public String getMovieName() {
		return movieName;
	}

	@Override
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	@Override
	public Location getLocalisation() {
		return this.localisationSearch;
	}

	@Override
	public void setLocalisation(Location localisationSearch) {
		this.localisationSearch = localisationSearch;
	}

	@Override
	public String getFavTheaterId() {
		return favTheaterId;
	}

	@Override
	public void setFavTheaterId(String favTheaterId) {
		this.favTheaterId = favTheaterId;
	}

	@Override
	public Set<String> getRequestList() {
		return requestList;
	}

	@Override
	public void setRequestList(Set<String> requestList) {
		this.requestList = requestList;
	}

	@Override
	public Set<String> getRequestMovieList() {
		return requestMovieList;
	}

	@Override
	public void setRequestMovieList(Set<String> requestMovieList) {
		this.requestMovieList = requestMovieList;
	}

	@Override
	public int getDay() {
		return day;
	}

	@Override
	public void setDay(int day) {
		this.day = day;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public void setStart(int start) {
		this.start = start;
	}

	@Override
	public boolean isForceResearch() {
		return forceResearch;
	}

	@Override
	public void setForceResearch(boolean forceResearch) {
		this.forceResearch = forceResearch;
	}

	@Override
	public String getLastRequestCity() {
		return lastRequestCity;
	}

	@Override
	public void setLastRequestCity(String lastRequestCity) {
		this.lastRequestCity = lastRequestCity;
	}

	@Override
	public String getLastRequestMovie() {
		return lastRequestMovie;
	}

	@Override
	public void setLastRequestMovie(String lastRequestMovie) {
		this.lastRequestMovie = lastRequestMovie;
	}

	@Override
	public String getLastRequestTheaterId() {
		return lastRequestTheaterId;
	}

	@Override
	public void setLastRequestTheaterId(String lastRequestTheaterId) {
		this.lastRequestTheaterId = lastRequestTheaterId;
	}
}
