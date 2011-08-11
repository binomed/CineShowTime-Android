package com.binomed.showtime.android.screen.widget.search;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.location.Location;

import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.results.IModelResults;
import com.binomed.showtime.android.screen.search.IModelSearch;

public class ModelCineShowTimeWidget implements IModelSearch, IModelResults {

	private String cityName;
	private TheaterBean theater;
	private List<TheaterBean> favList;
	private boolean nullResult;
	private boolean resetTheme;
	private Calendar lastRequestDate;
	private Location location;
	private Set<String> requestList = new HashSet<String>();
	private Set<String> requestMovieList = new HashSet<String>();
	private int day;
	private int start;
	private boolean forceSearch;
	private Map<String, TheaterBean> theaterFavList = new HashMap<String, TheaterBean>();
	private Set<Integer> groupExpand = new HashSet<Integer>();
	private NearResp nearResp;
	private String lastRequestCity;
	private String lastRequestMovie;
	private String lastRequestTheaterId;

	public ModelCineShowTimeWidget() {
		super();
	}

	@Override
	public String getCityName() {
		return cityName;
	}

	@Override
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public TheaterBean getTheater() {
		return theater;
	}

	public void setTheater(TheaterBean theater) {
		this.theater = theater;
	}

	public List<TheaterBean> getFavList() {
		return favList;
	}

	public void setFavList(List<TheaterBean> favList) {
		this.favList = favList;
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

	public Calendar getLastRequestDate() {
		return lastRequestDate;
	}

	public void setLastRequestDate(Calendar lastRequestDate) {
		this.lastRequestDate = lastRequestDate;
	}

	@Override
	public void setLocalisation(Location location) {
		this.location = location;

	}

	@Override
	public Location getLocalisation() {
		return location;
	}

	@Override
	public String getMovieName() {
		return null;
	}

	@Override
	public void setMovieName(String movieName) {
	}

	@Override
	public String getFavTheaterId() {
		return null;
	}

	@Override
	public void setFavTheaterId(String favTheaterId) {
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
	public boolean isForceResearch() {
		return forceSearch;
	}

	@Override
	public void setForceResearch(boolean forceResearch) {
		this.forceSearch = forceResearch;
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
		return groupExpand;
	}

	@Override
	public void setGroupExpanded(Set<Integer> groupExpanded) {
		this.groupExpand = groupExpanded;

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
	public Set<String> getRequestMovieList() {
		return requestMovieList;
	}

	@Override
	public void setRequestMovieList(Set<String> requestMovieList) {
		this.requestMovieList = requestMovieList;
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
