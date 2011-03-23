package com.binomed.showtime.android.widget.search;

import java.util.List;

import android.location.Location;

import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.localisation.IModelLocalisation;

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

	@Override
	public Location getLocalisation() {
		return localisation;
	}

	@Override
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
