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

import android.os.Parcel;
import android.os.Parcelable;

public class MovieResp extends AbstractModel implements Parcelable {

	public static final Parcelable.Creator<MovieResp> CREATOR = new Creator<MovieResp>() {

		@Override
		public MovieResp[] newArray(int size) {
			return new MovieResp[size];
		}

		@Override
		public MovieResp createFromParcel(Parcel source) {
			return new MovieResp(source);
		}
	};

	private static final int FIELD_CITY_NAME = 0;
	private static final int FIELD_HAS_MORE_RESULTS = 1;
	private static final int FIELD_MOVIE = 2;
	private static final int FIELD_THEATER_LIST = 3;
	private static final int FIELD_END = -1;

	private String cityName;

	private boolean hasMoreResults;

	private MovieBean movie;

	private List<TheaterBean> theaterList;

	public MovieBean getMovie() {
		return movie;
	}

	public void setMovie(MovieBean movie) {
		this.movie = movie;
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

	public MovieResp() {
		super();
	}

	public MovieResp(Parcel parcel) {
		this();
		readFromParcel(parcel);
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
			case FIELD_MOVIE: {
				setMovie(readParcel(parcel, MovieBean.class));
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
		if (getMovie() != null) {
			dest.writeInt(FIELD_MOVIE);
			writeParcelable(dest, getMovie(), flags);
		}
		if (getTheaterList() != null) {
			dest.writeInt(FIELD_THEATER_LIST);
			writeList(dest, getTheaterList(), new TheaterBean[getTheaterList().size()], TheaterBean.class, flags);
		}
		dest.writeInt(FIELD_END);

	}

}
