package com.binomed.showtime.android.widget.one;

import java.util.List;

import android.location.Location;

import com.binomed.showtime.android.util.localisation.IModelLocalisation;
import com.binomed.showtime.beans.TheaterBean;

public class ModelAndShowTimeWidget implements IModelLocalisation {

	private String cityName;
	private TheaterBean theater;
	private Location localisation;
	private List<TheaterBean> favList;

	public ModelAndShowTimeWidget() {
		super();
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Location getLocalisation() {
		return localisation;
	}

	public void setLocalisation(Location gpsLocalisation) {
		this.localisation = gpsLocalisation;
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

}
