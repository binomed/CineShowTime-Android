package com.binomed.showtime.android.widget;

import java.util.List;

import android.location.Location;

import com.binomed.showtime.beans.TheaterBean;

public class ModelAndShowTimeWidget {

	private String cityName;
	private List<TheaterBean> theaterResultList;
	private TheaterBean theater;
	private Location gpsLocalisation;
	private Location localisationSearch;

	public ModelAndShowTimeWidget() {
		super();
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

	public TheaterBean getTheater() {
		return theater;
	}

	public void setTheater(TheaterBean theater) {
		this.theater = theater;
	}

	public List<TheaterBean> getTheaterResultList() {
		return theaterResultList;
	}

	public void setTheaterResultList(List<TheaterBean> theaterResultList) {
		this.theaterResultList = theaterResultList;
	}

}
