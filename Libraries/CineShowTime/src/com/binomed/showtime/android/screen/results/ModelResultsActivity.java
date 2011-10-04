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
package com.binomed.showtime.android.screen.results;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.location.Location;

import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;

public class ModelResultsActivity implements IModelResults {

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
		return localisationSearch;
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
	public boolean isForceResearch() {
		return forceResearch;
	}

	@Override
	public void setForceResearch(boolean forceResearch) {
		this.forceResearch = forceResearch;
	}

	@Override
	public Map<String, TheaterBean> getTheaterFavList() {
		return theaterFavList;
	}

	@Override
	public void setTheaterFavList(Map<String, TheaterBean> theaterFavList) {
		this.theaterFavList = theaterFavList;
	}

	@Override
	public Set<Integer> getGroupExpanded() {
		return groupExpanded;
	}

	@Override
	public void setGroupExpanded(Set<Integer> groupExpanded) {
		this.groupExpanded = groupExpanded;
	}

	@Override
	public NearResp getNearResp() {
		return nearResp;
	}

	@Override
	public void setNearResp(NearResp nearResp) {
		this.nearResp = nearResp;
	}

}
