package com.binomed.showtime.android.searchnearactivity;

import java.util.HashSet;
import java.util.Set;

import android.location.Location;

public class ModelSearchNearActivity {

	private String cityName;
	private String favTheaterId;
	private Set<String> requestList;
	private Location gpsLocalisation;
	private Location localisationSearch;
	private int day;
	private int start;

	public ModelSearchNearActivity() {
		super();
		requestList = new HashSet<String>();
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

	public Set<String> getRequestList() {
		return requestList;
	}

	public void setRequestList(Set<String> requestList) {
		this.requestList = requestList;
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

}
