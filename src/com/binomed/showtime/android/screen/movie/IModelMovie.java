package com.binomed.showtime.android.screen.movie;

import android.location.Location;

import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.activity.ICineShowTimeActivityHelperModel;

public interface IModelMovie extends ICineShowTimeActivityHelperModel {

	boolean isTranslate();

	void setTranslate(boolean translate);

	int getLastTab();

	void setLastTab(int lastTab);

	MovieBean getMovie();

	void setMovie(MovieBean movie);

	TheaterBean getTheater();

	void setTheater(TheaterBean theater);

	Location getGpsLocation();

	void setGpsLocation(Location gpsLocation);

	boolean isMapInstalled();

	void setMapInstalled(boolean mapInstalled);

	boolean isDialerInstalled();

	void setDialerInstalled(boolean dialerInstalled);

	boolean isCalendarInstalled();

	void setCalendarInstalled(boolean calendarInstalled);

}
