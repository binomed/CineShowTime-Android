package com.binomed.showtime.android.resultsactivity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.location.Location;

import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.localisation.IModelLocalisation;

public class ModelResultsActivity implements IModelLocalisation {

	private String cityName;
	private String movieName;
	private String favTheaterId;
	private Set<String> requestList;
	private Map<String, TheaterBean> theaterFavList;
	private Set<Integer> groupExpanded;
	// private Location gpsLocalisation;
	private Location localisationSearch;
	private int day;
	private int start;
	private boolean forceResearch;
	private boolean nullResult;
	private boolean resetTheme;
	private NearResp nearResp;

	public ModelResultsActivity() {
		super();
		requestList = new HashSet<String>();
		groupExpanded = new HashSet<Integer>();
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

	@Override
	public Location getLocalisation() {
		return localisationSearch;
	}

	@Override
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

	public Map<String, TheaterBean> getTheaterFavList() {
		return theaterFavList;
	}

	public void setTheaterFavList(Map<String, TheaterBean> theaterFavList) {
		this.theaterFavList = theaterFavList;
	}

	public Set<Integer> getGroupExpanded() {
		return groupExpanded;
	}

	public void setGroupExpanded(Set<Integer> groupExpanded) {
		this.groupExpanded = groupExpanded;
	}

	public NearResp getNearResp() {
		return nearResp;
	}

	public void setNearResp(NearResp nearResp) {
		this.nearResp = nearResp;
	}

}
