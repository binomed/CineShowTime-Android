package com.binomed.showtime.android.movieactivity;

import android.location.Location;

import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.activity.ICineShowTimeActivityHelperModel;

public class ModelMovieActivity implements ICineShowTimeActivityHelperModel {

	private int lastTab = 0;
	private boolean translate = false;
	private MovieBean movie;
	private TheaterBean theater;
	private Location gpsLocation;
	private boolean resetTheme = false;
	private boolean mapInstalled;
	private boolean dialerInstalled;
	private boolean calendarInstalled;

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

	@Override
	public boolean isResetTheme() {
		return resetTheme;
	}

	@Override
	public void setResetTheme(boolean resetTheme) {
		this.resetTheme = resetTheme;
	}

	public boolean isMapInstalled() {
		return mapInstalled;
	}

	public void setMapInstalled(boolean mapInstalled) {
		this.mapInstalled = mapInstalled;
	}

	public boolean isDialerInstalled() {
		return dialerInstalled;
	}

	public void setDialerInstalled(boolean dialerInstalled) {
		this.dialerInstalled = dialerInstalled;
	}

	public boolean isCalendarInstalled() {
		return calendarInstalled;
	}

	public void setCalendarInstalled(boolean calendarInstalled) {
		this.calendarInstalled = calendarInstalled;
	}

	@Override
	public void setNullResult(boolean nullResult) {
		// nothing to do
	}

	@Override
	public boolean isNullResult() {
		// nothing to do
		return false;
	}

}
