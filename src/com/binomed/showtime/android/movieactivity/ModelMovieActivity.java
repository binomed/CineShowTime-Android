package com.binomed.showtime.android.movieactivity;

import android.location.Location;

import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.TheaterBean;

public class ModelMovieActivity {

	private int lastTab = 0;

	private boolean translate = false;

	private MovieBean movie;

	private TheaterBean theater;

	private Location gpsLocation;

	public boolean isTranslate() {
		return translate;
	}

	public void setTranslate(boolean translate) {
		this.translate = translate;
	}

	public int getLastTab() {
		return lastTab;
	}

	public void setLastTab(int lastTab) {
		this.lastTab = lastTab;
	}

	public MovieBean getMovie() {
		return movie;
	}

	public void setMovie(MovieBean movie) {
		this.movie = movie;
	}

	public TheaterBean getTheater() {
		return theater;
	}

	public void setTheater(TheaterBean theater) {
		this.theater = theater;
	}

	public Location getGpsLocation() {
		return gpsLocation;
	}

	public void setGpsLocation(Location gpsLocation) {
		this.gpsLocation = gpsLocation;
	}

}
