package com.binomed.showtime.android.screen.results;

import java.util.Map;
import java.util.Set;

import com.binomed.showtime.android.model.NearResp;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.util.activity.ICineShowTimeActivityHelperModel;
import com.binomed.showtime.android.util.localisation.IModelLocalisation;

public interface IModelResults extends ICineShowTimeActivityHelperModel, IModelLocalisation {

	String getCityName();

	void setCityName(String cityName);

	String getMovieName();

	void setMovieName(String movieName);

	String getFavTheaterId();

	void setFavTheaterId(String favTheaterId);

	Set<String> getRequestList();

	void setRequestList(Set<String> requestList);

	int getDay();

	void setDay(int day);

	int getStart();

	void setStart(int start);

	boolean isForceResearch();

	void setForceResearch(boolean forceResearch);

	Map<String, TheaterBean> getTheaterFavList();

	void setTheaterFavList(Map<String, TheaterBean> theaterFavList);

	Set<Integer> getGroupExpanded();

	void setGroupExpanded(Set<Integer> groupExpanded);

	NearResp getNearResp();

	void setNearResp(NearResp nearResp);

}
