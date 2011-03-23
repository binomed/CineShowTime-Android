package com.binomed.showtime.android.searchmovieactivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.location.Location;

import com.binomed.showtime.android.util.localisation.IModelLocalisation;

public class ModelSearchMovieActivity implements IModelLocalisation {

	private String cityName;
	private String movieName;

	private String favTheaterId;

	private List<String> voiceCityList;
	private List<String> voiceMovieList;

	private Set<String> requestNearList;
	private Set<String> requestMovieList;

	private Location localisation;
	// private Location localisationSearch;

	private int day;

	public ModelSearchMovieActivity() {
		super();
		requestNearList = new HashSet<String>();
		requestMovieList = new HashSet<String>();
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

	// public Location getLocalisationSearch() {
	// return localisationSearch;
	// }
	//
	// public void setLocalisationSearch(Location localisationSearch) {
	// this.localisationSearch = localisationSearch;
	// }

	public String getFavTheaterId() {
		return favTheaterId;
	}

	public void setFavTheaterId(String favTheaterId) {
		this.favTheaterId = favTheaterId;
	}

	public Set<String> getNearRequestList() {
		return requestNearList;
	}

	public void setNearRequestList(Set<String> requestList) {
		this.requestNearList = requestList;
	}

	public Set<String> getRequestMovieList() {
		return requestMovieList;
	}

	public void setRequestMovieList(Set<String> requestMovieList) {
		this.requestMovieList = requestMovieList;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public List<String> getVoiceCityList() {
		return voiceCityList;
	}

	public void setVoiceCityList(List<String> voiceCityList) {
		this.voiceCityList = voiceCityList;
	}

	public List<String> getVoiceMovieList() {
		return voiceMovieList;
	}

	public void setVoiceMovieList(List<String> voiceMovieList) {
		this.voiceMovieList = voiceMovieList;
	}

}
