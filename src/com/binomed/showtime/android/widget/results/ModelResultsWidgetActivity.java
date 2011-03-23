package com.binomed.showtime.android.widget.results;

import java.util.HashMap;
import java.util.Map;

import android.location.Location;

import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.localisation.IModelLocalisation;

public class ModelResultsWidgetActivity implements IModelLocalisation {

	private String cityName;
	private String favTheaterId;
	private int start;
	private Map<String, TheaterBean> theaterFavList;
	// private Location gpsLocalisation;
	private Location localisationSearch;
	private NearResp nearResp;

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

	public NearResp getNearResp() {
		return nearResp;
	}

	public void setNearResp(NearResp nearResp) {
		this.nearResp = nearResp;
	}

}
