package com.binomed.showtime.android.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Parcel;
import android.os.Parcelable;

public class TheaterBean extends AbstractModel implements Parcelable {

	public static final Parcelable.Creator<TheaterBean> CREATOR = new Creator<TheaterBean>() {

		@Override
		public TheaterBean[] newArray(int size) {
			return new TheaterBean[size];
		}

		@Override
		public TheaterBean createFromParcel(Parcel source) {
			return new TheaterBean(source);
		}
	};

	private static final int FIELD_ID = 0;
	private static final int FIELD_THEATER_NAME = 1;
	private static final int FIELD_PHONE_NUMBER = 2;
	private static final int FIELD_PLACE = 3;
	private static final int FIELD_MOVIE_MAP = 4;
	private static final int FIELD_END = -1;

	private String id;

	private String theaterName;

	private String phoneNumber;

	private LocalisationBean place;

	private Map<String, List<ProjectionBean>> movieMap;

	public TheaterBean() {
		super();
	}

	public TheaterBean(Parcel parcel) {
		this();
		readFromParcel(parcel);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTheaterName() {
		return theaterName;
	}

	public void setTheaterName(String theaterName) {
		this.theaterName = theaterName;
	}

	public Map<String, List<ProjectionBean>> getMovieMap() {
		return movieMap;
	}

	public void setMovieMap(Map<String, List<ProjectionBean>> movieMap) {
		this.movieMap = movieMap;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public LocalisationBean getPlace() {
		return place;
	}

	public void setPlace(LocalisationBean place) {
		this.place = place;
	}

	private void readFromParcel(Parcel parcel) {
		boolean end = false;
		int code = 0;
		while (!end) {
			code = parcel.readInt();
			switch (code) {
			case FIELD_ID: {
				setId(readString(parcel));
				break;
			}
			case FIELD_MOVIE_MAP: {
				setMovieMap(readMapList(parcel, ProjectionBean.class));
				break;
			}
			case FIELD_PHONE_NUMBER: {
				setPhoneNumber(readString(parcel));
				break;
			}
			case FIELD_PLACE: {
				setPlace(readParcel(parcel, LocalisationBean.class));
				break;
			}
			case FIELD_THEATER_NAME: {
				setTheaterName(readString(parcel));
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
		if (getId() != null) {
			writeInt(dest, FIELD_ID);
			writeString(dest, getId());
		}
		if (getMovieMap() != null) {
			writeInt(dest, FIELD_MOVIE_MAP);
			dest.writeInt(getMovieMap() != null ? getMovieMap().size() : 0);
			for (Entry<String, List<ProjectionBean>> entry : getMovieMap().entrySet()) {
				dest.writeString(entry.getKey());
				dest.writeParcelableArray(entry.getValue().toArray(new ProjectionBean[entry.getValue().size()]), flags);
			}
		}
		if (getPhoneNumber() != null) {
			writeInt(dest, FIELD_PHONE_NUMBER);
			writeString(dest, getPhoneNumber());
		}
		if (getPlace() != null) {
			writeInt(dest, FIELD_PLACE);
			writeParcelable(dest, getPlace(), flags);
		}
		if (getTheaterName() != null) {
			writeInt(dest, FIELD_THEATER_NAME);
			writeString(dest, getTheaterName());
		}
		writeInt(dest, FIELD_END);

	}

}
