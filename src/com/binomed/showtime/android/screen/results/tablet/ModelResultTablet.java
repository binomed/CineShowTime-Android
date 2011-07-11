package com.binomed.showtime.android.screen.results.tablet;

import java.util.Map;
import java.util.Set;

import android.location.Location;

import com.binomed.showtime.android.model.MovieBean;
import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.screen.movie.IModelMovie;
import com.binomed.showtime.android.screen.movie.ModelMovieActivity;
import com.binomed.showtime.android.screen.results.IModelResults;
import com.binomed.showtime.android.screen.results.ModelResultsActivity;

public class ModelResultTablet implements IModelResultTablet {

	private IModelMovie modelMovie;
	private IModelResults modelResults;

	public ModelResultTablet() {
		super();
		modelMovie = new ModelMovieActivity();
		modelResults = new ModelResultsActivity();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#isTranslate()
	 */
	@Override
	public boolean isTranslate() {
		return modelMovie.isTranslate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#setTranslate(boolean)
	 */
	@Override
	public void setTranslate(boolean translate) {
		modelMovie.setTranslate(translate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#getLastTab()
	 */
	@Override
	public int getLastTab() {
		return modelMovie.getLastTab();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#setLastTab(int)
	 */
	@Override
	public void setLastTab(int lastTab) {
		modelMovie.setLastTab(lastTab);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#getMovie()
	 */
	@Override
	public MovieBean getMovie() {
		return modelMovie.getMovie();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#setMovie(com.binomed.showtime.android.model.MovieBean)
	 */
	@Override
	public void setMovie(MovieBean movie) {
		modelMovie.setMovie(movie);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#getTheater()
	 */
	@Override
	public TheaterBean getTheater() {
		return modelMovie.getTheater();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#setTheater(com.binomed.showtime.android.model.TheaterBean)
	 */
	@Override
	public void setTheater(TheaterBean theater) {
		modelMovie.setTheater(theater);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#getGpsLocation()
	 */
	@Override
	public Location getGpsLocation() {
		return modelMovie.getGpsLocation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#setGpsLocation(android.location.Location)
	 */
	@Override
	public void setGpsLocation(Location gpsLocation) {
		modelMovie.setGpsLocation(gpsLocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#isMapInstalled()
	 */
	@Override
	public boolean isMapInstalled() {
		return modelMovie.isMapInstalled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#setMapInstalled(boolean)
	 */
	@Override
	public void setMapInstalled(boolean mapInstalled) {
		modelMovie.setMapInstalled(mapInstalled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#isDialerInstalled()
	 */
	@Override
	public boolean isDialerInstalled() {
		return modelMovie.isDialerInstalled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#setDialerInstalled(boolean)
	 */
	@Override
	public void setDialerInstalled(boolean dialerInstalled) {
		modelMovie.setDialerInstalled(dialerInstalled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#isCalendarInstalled()
	 */
	@Override
	public boolean isCalendarInstalled() {
		return modelMovie.isCalendarInstalled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.movie.IModelMovie#setCalendarInstalled(boolean)
	 */
	@Override
	public void setCalendarInstalled(boolean calendarInstalled) {
		modelMovie.setCalendarInstalled(calendarInstalled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.util.activity.ICineShowTimeActivityHelperModel#setNullResult(boolean)
	 */
	@Override
	public void setNullResult(boolean nullResult) {
		modelMovie.setNullResult(nullResult);
	}

	@Override
	public void setResetTheme(boolean resetTheme) {
		modelMovie.setResetTheme(resetTheme);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.util.activity.ICineShowTimeActivityHelperModel#isNullResult()
	 */
	@Override
	public boolean isNullResult() {
		return modelMovie.isNullResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.util.activity.ICineShowTimeActivityHelperModel#isResetTheme()
	 */
	@Override
	public boolean isResetTheme() {
		return modelMovie.isResetTheme();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#getCityName()
	 */
	@Override
	public String getCityName() {
		return modelResults.getCityName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#setCityName(java.lang.String)
	 */
	@Override
	public void setCityName(String cityName) {
		modelResults.setCityName(cityName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#getMovieName()
	 */
	@Override
	public String getMovieName() {
		return modelResults.getMovieName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#setMovieName(java.lang.String)
	 */
	@Override
	public void setMovieName(String movieName) {
		modelResults.setMovieName(movieName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#getFavTheaterId()
	 */
	@Override
	public String getFavTheaterId() {
		return modelResults.getFavTheaterId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#setFavTheaterId(java.lang.String)
	 */
	@Override
	public void setFavTheaterId(String favTheaterId) {
		modelResults.setFavTheaterId(favTheaterId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#getRequestList()
	 */
	@Override
	public Set<String> getRequestList() {
		return modelResults.getRequestList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#setRequestList(java.util.Set)
	 */
	@Override
	public void setRequestList(Set<String> requestList) {
		modelResults.setRequestList(requestList);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#getDay()
	 */
	@Override
	public int getDay() {
		return modelResults.getDay();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#setDay(int)
	 */
	@Override
	public void setDay(int day) {
		modelResults.setDay(day);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#getStart()
	 */
	@Override
	public int getStart() {
		return modelResults.getStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#setStart(int)
	 */
	@Override
	public void setStart(int start) {
		modelResults.setStart(start);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#isForceResearch()
	 */
	@Override
	public boolean isForceResearch() {
		return modelResults.isForceResearch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#setForceResearch(boolean)
	 */
	@Override
	public void setForceResearch(boolean forceResearch) {
		modelResults.setForceResearch(forceResearch);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#getTheaterFavList()
	 */
	@Override
	public Map<String, TheaterBean> getTheaterFavList() {
		return modelResults.getTheaterFavList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#setTheaterFavList(java.util.Map)
	 */
	@Override
	public void setTheaterFavList(Map<String, TheaterBean> theaterFavList) {
		modelResults.setTheaterFavList(theaterFavList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#getGroupExpanded()
	 */
	@Override
	public Set<Integer> getGroupExpanded() {
		return modelResults.getGroupExpanded();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#setGroupExpanded(java.util.Set)
	 */
	@Override
	public void setGroupExpanded(Set<Integer> groupExpanded) {
		modelResults.setGroupExpanded(groupExpanded);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#getNearResp()
	 */
	@Override
	public NearResp getNearResp() {
		return modelResults.getNearResp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.screen.results.IModelResults#setNearResp(com.binomed.showtime.android.model.NearResp)
	 */
	@Override
	public void setNearResp(NearResp nearResp) {
		modelResults.setNearResp(nearResp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.util.localisation.IModelLocalisation#setLocalisation(android.location.Location)
	 */
	@Override
	public void setLocalisation(Location location) {
		modelResults.setLocalisation(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.util.localisation.IModelLocalisation#getLocalisation()
	 */
	@Override
	public Location getLocalisation() {
		return modelResults.getLocalisation();
	}

}
