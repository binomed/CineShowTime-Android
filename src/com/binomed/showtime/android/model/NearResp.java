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
package com.binomed.showtime.android.model;

import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class NearResp extends AbstractModel implements Parcelable {

	public static final Parcelable.Creator<NearResp> CREATOR = new Creator<NearResp>() {

		@Override
		public NearResp[] newArray(int size) {
			return new NearResp[size];
		}

		@Override
		public NearResp createFromParcel(Parcel source) {
			return new NearResp(source);
		}
	};

	public NearResp() {
		super();
	}

	public NearResp(Parcel parcel) {
		this();
		readFromParcel(parcel);
	}

	private static final int FIELD_CITY_NAME = 0;
	private static final int FIELD_HAS_MORE_RESULTS = 1;
	private static final int FIELD_NEAR_RESP = 2;
	private static final int FIELD_MAP_MOVIES = 3;
	private static final int FIELD_THEATER_LIST = 4;
	private static final int FIELD_END = -1;

	private String cityName;

	private boolean hasMoreResults;

	private boolean nearResp;

	private Map<String, MovieBean> mapMovies;

	private List<TheaterBean> theaterList;

	public Map<String, MovieBean> getMapMovies() {
		return mapMovies;
	}

	public void setMapMovies(Map<String, MovieBean> mapMovies) {
		this.mapMovies = mapMovies;
	}

	public List<TheaterBean> getTheaterList() {
		return theaterList;
	}

	public void setTheaterList(List<TheaterBean> theaterList) {
		this.theaterList = theaterList;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public boolean isHasMoreResults() {
		return hasMoreResults;
	}

	public void setHasMoreResults(boolean hasMoreResults) {
		this.hasMoreResults = hasMoreResults;
	}

	public boolean isNearResp() {
		return nearResp;
	}

	public void setNearResp(boolean nearResp) {
		this.nearResp = nearResp;
	}

	private void readFromParcel(Parcel parcel) {
		boolean end = false;
		int code = 0;
		while (!end) {
			code = parcel.readInt();
			switch (code) {
			case FIELD_CITY_NAME: {
				setCityName(readString(parcel));
				break;
			}
			case FIELD_HAS_MORE_RESULTS: {
				setHasMoreResults(readBoolean(parcel));
				break;
			}
			case FIELD_MAP_MOVIES: {
				setMapMovies(readMap(parcel, MovieBean.class));
				break;
			}
			case FIELD_NEAR_RESP: {
				setNearResp(readBoolean(parcel));
				break;
			}
			case FIELD_THEATER_LIST: {
				setTheaterList(readList(parcel, TheaterBean.class));
				break;
			}
			case FIELD_END: {
				end = true;
				break;
			}
			default:
				break;
			}
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (getCityName() != null) {
			dest.writeInt(FIELD_CITY_NAME);
			writeString(dest, getCityName());
		}
		dest.writeInt(FIELD_HAS_MORE_RESULTS);
		writeBoolean(dest, isHasMoreResults());
		if (getMapMovies() != null) {
			dest.writeInt(FIELD_MAP_MOVIES);
			writeMap(dest, getMapMovies(), flags);
		}
		dest.writeInt(FIELD_NEAR_RESP);
		writeBoolean(dest, isNearResp());
		if (getTheaterList() != null) {
			dest.writeInt(FIELD_THEATER_LIST);
			writeList(dest, getTheaterList(), new TheaterBean[getTheaterList().size()], TheaterBean.class, flags);
		}
		dest.writeInt(FIELD_END);

	}

}
