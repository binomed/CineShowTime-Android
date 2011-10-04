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
package com.binomed.showtime.android.screen.widget.results;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.location.Location;

import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.results.IModelResults;

public class ModelResultsWidgetActivity implements IModelResults {

	private String cityName;
	private String favTheaterId;
	private int start;
	private Map<String, TheaterBean> theaterFavList;
	// private Location gpsLocalisation;
	private Location localisationSearch;
	private NearResp nearResp;
	private boolean nullResult;
	private boolean resetTheme;
	private Set<String> requestList;
	private Set<Integer> groupExpand;
	private int day;
	private boolean forceResearch;

	public ModelResultsWidgetActivity() {
		super();
		theaterFavList = new HashMap<String, TheaterBean>();
		requestList = new HashSet<String>();
		groupExpand = new HashSet<Integer>();
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
	public Map<String, TheaterBean> getTheaterFavList() {
		return theaterFavList;
	}

	@Override
	public void setTheaterFavList(Map<String, TheaterBean> theaterFavList) {
		this.theaterFavList = theaterFavList;
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
	public NearResp getNearResp() {
		return nearResp;
	}

	@Override
	public void setNearResp(NearResp nearResp) {
		this.nearResp = nearResp;
	}

	@Override
	public void setNullResult(boolean nullResult) {
		this.nullResult = nullResult;

	}

	@Override
	public void setResetTheme(boolean resetTheme) {
		this.resetTheme = resetTheme;

	}

	@Override
	public boolean isNullResult() {
		return nullResult;
	}

	@Override
	public boolean isResetTheme() {
		return resetTheme;
	}

	@Override
	public String getMovieName() {
		return null;
	}

	@Override
	public void setMovieName(String movieName) {

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
	public boolean isForceResearch() {
		return forceResearch;
	}

	@Override
	public void setForceResearch(boolean forceResearch) {
		this.forceResearch = forceResearch;

	}

	@Override
	public Set<Integer> getGroupExpanded() {
		return groupExpand;
	}

	@Override
	public void setGroupExpanded(Set<Integer> groupExpanded) {
		this.groupExpand = groupExpanded;

	}

}
