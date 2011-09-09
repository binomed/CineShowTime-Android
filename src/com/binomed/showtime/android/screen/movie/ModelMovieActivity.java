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
package com.binomed.showtime.android.screen.movie;

import android.location.Location;

import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;

public class ModelMovieActivity implements IModelMovie {

	private int lastTab = 0;
	private boolean translate = false;
	private MovieBean movie;
	private TheaterBean theater;
	private Location gpsLocation;
	private boolean resetTheme = false;
	private boolean mapInstalled;
	private boolean dialerInstalled;
	private boolean calendarInstalled;

	@Override
	public boolean isTranslate() {
		return translate;
	}

	@Override
	public void setTranslate(boolean translate) {
		this.translate = translate;
	}

	@Override
	public int getLastTab() {
		return lastTab;
	}

	@Override
	public void setLastTab(int lastTab) {
		this.lastTab = lastTab;
	}

	@Override
	public MovieBean getMovie() {
		return movie;
	}

	@Override
	public void setMovie(MovieBean movie) {
		this.movie = movie;
	}

	@Override
	public TheaterBean getTheater() {
		return theater;
	}

	@Override
	public void setTheater(TheaterBean theater) {
		this.theater = theater;
	}

	@Override
	public Location getGpsLocation() {
		return gpsLocation;
	}

	@Override
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

	@Override
	public boolean isMapInstalled() {
		return mapInstalled;
	}

	@Override
	public void setMapInstalled(boolean mapInstalled) {
		this.mapInstalled = mapInstalled;
	}

	@Override
	public boolean isDialerInstalled() {
		return dialerInstalled;
	}

	@Override
	public void setDialerInstalled(boolean dialerInstalled) {
		this.dialerInstalled = dialerInstalled;
	}

	@Override
	public boolean isCalendarInstalled() {
		return calendarInstalled;
	}

	@Override
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
