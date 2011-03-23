package com.binomed.showtime.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LocalisationBean extends AbstractModel implements Parcelable {

	public static final Parcelable.Creator<LocalisationBean> CREATOR = new Creator<LocalisationBean>() {

		@Override
		public LocalisationBean[] newArray(int size) {
			return new LocalisationBean[size];
		}

		@Override
		public LocalisationBean createFromParcel(Parcel source) {
			return new LocalisationBean(source);
		}
	};

	private static final int FIELD_DISTANCE = 0;
	private static final int FIELD_DISTANCE_TIME = 1;
	private static final int FIELD_LATITUDE = 2;
	private static final int FIELD_LONGITUDE = 3;
	private static final int FIELD_SEARCH_QUERY = 4;
	private static final int FIELD_COUNTRY_NAME = 5;
	private static final int FIELD_COUNTRY_NAME_CODE = 6;
	private static final int FIELD_CITY_NAME = 7;
	private static final int FIELD_POSTAL_CITY_NAME = 8;
	private static final int FIELD_END = -1;

	private Float distance;
	private Long distanceTime;
	private Double latitude;
	private Double longitude;
	private String searchQuery;
	private String countryName;
	private String countryNameCode;
	private String cityName;
	private String postalCityNumber;

	public LocalisationBean() {
		super();
	}

	public LocalisationBean(Parcel parcel) {
		this();
		readFromParcel(parcel);
	}

	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}

	public Long getDistanceTime() {
		return distanceTime;
	}

	public void setDistanceTime(Long distanceTime) {
		this.distanceTime = distanceTime;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryNameCode() {
		return countryNameCode;
	}

	public void setCountryNameCode(String countryNameCode) {
		this.countryNameCode = countryNameCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getPostalCityNumber() {
		return postalCityNumber;
	}

	public void setPostalCityNumber(String postalCityNumber) {
		this.postalCityNumber = postalCityNumber;
	}

	@Override
	public String toString() {
		return cityName + ", " + postalCityNumber + " " + countryName; //$NON-NLS-1$//$NON-NLS-2$
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
			case FIELD_COUNTRY_NAME: {
				setCountryName(readString(parcel));
				break;
			}
			case FIELD_COUNTRY_NAME_CODE: {
				setCountryNameCode(readString(parcel));
				break;
			}
			case FIELD_DISTANCE: {
				setDistance(readFloat(parcel));
				break;
			}
			case FIELD_DISTANCE_TIME: {
				setDistanceTime(readLong(parcel));
				break;
			}
			case FIELD_LATITUDE: {
				setLatitude(readDouble(parcel));
				break;
			}
			case FIELD_LONGITUDE: {
				setLongitude(readDouble(parcel));
				break;
			}
			case FIELD_POSTAL_CITY_NAME: {
				setPostalCityNumber(readString(parcel));
				break;
			}
			case FIELD_SEARCH_QUERY: {
				setSearchQuery(readString(parcel));
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
			writeInt(dest, FIELD_CITY_NAME);
			writeString(dest, getCityName());
		}
		if (getCountryName() != null) {
			writeInt(dest, FIELD_COUNTRY_NAME);
			writeString(dest, getCountryName());
		}
		if (getCountryNameCode() != null) {
			writeInt(dest, FIELD_COUNTRY_NAME_CODE);
			writeString(dest, getCountryNameCode());
		}
		if (getDistance() != null) {
			writeInt(dest, FIELD_DISTANCE);
			writeFloat(dest, getDistance());
		}
		if (getDistanceTime() != null) {
			writeInt(dest, FIELD_DISTANCE_TIME);
			writeLong(dest, getDistanceTime());
		}
		if (getLatitude() != null) {
			writeInt(dest, FIELD_LATITUDE);
			writeDouble(dest, getLatitude());
		}
		if (getLongitude() != null) {
			writeInt(dest, FIELD_LONGITUDE);
			writeDouble(dest, getLongitude());
		}
		if (getPostalCityNumber() != null) {
			writeInt(dest, FIELD_POSTAL_CITY_NAME);
			writeString(dest, getPostalCityNumber());
		}
		if (getSearchQuery() != null) {
			writeInt(dest, FIELD_SEARCH_QUERY);
			writeString(dest, getSearchQuery());
		}
		writeInt(dest, FIELD_END);

	}
}
