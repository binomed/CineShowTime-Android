package com.binomed.showtime.android.widget.one;

import java.util.HashMap;
import java.util.Map;

import android.location.Location;

import com.binomed.showtime.android.util.localisation.IModelLocalisation;
import com.binomed.showtime.beans.TheaterBean;

public class ModelResultsWidgetActivity implements IModelLocalisation {

	private String cityName;
	private String favTheaterId;
	private int start;
	private Map<String, TheaterBean> theaterFavList;
	// private Location gpsLocalisation;
	private Location localisationSearch;

	public ModelResultsWidgetActivity() {
		super();
		theaterFavList = new HashMap<String, TheaterBean>();
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
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

	public Map<String, TheaterBean> getTheaterFavList() {
		return theaterFavList;
	}

	public void setTheaterFavList(Map<String, TheaterBean> theaterFavList) {
		this.theaterFavList = theaterFavList;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

}
