package com.binomed.showtime.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProjectionBean extends AbstractModel implements Parcelable {

	public static final Parcelable.Creator<ProjectionBean> CREATOR = new Creator<ProjectionBean>() {

		@Override
		public ProjectionBean[] newArray(int size) {
			return new ProjectionBean[size];
		}

		@Override
		public ProjectionBean createFromParcel(Parcel source) {
			return new ProjectionBean(source);
		}
	};

	public ProjectionBean() {
		super();
	}

	public ProjectionBean(Parcel parcel) {
		this();
		readFromParcel(parcel);
	}

	private static final int FIELD_SHOWTIME = 0;
	private static final int FIELD_LANG = 1;
	private static final int FIELD_RESERVATION_LINK = 2;
	private static final int FIELD_END = -1;

	private Long showtime;

	private String lang;

	private String reservationLink;

	public Long getShowtime() {
		return showtime;
	}

	public void setShowtime(Long showtime) {
		this.showtime = showtime;
	}

	public String getLang() {
		return lang;
	}

	public void setSubtitle(String subtitle) {
		this.lang = subtitle;
	}

	public String getReservationLink() {
		return reservationLink;
	}

	public void setReservationLink(String reservationLink) {
		this.reservationLink = reservationLink;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	private void readFromParcel(Parcel parcel) {
		boolean end = false;
		int code = 0;
		while (!end) {
			code = parcel.readInt();
			switch (code) {
			case FIELD_LANG: {
				setSubtitle(readString(parcel));
				break;
			}
			case FIELD_RESERVATION_LINK: {
				setReservationLink(readString(parcel));
				break;
			}
			case FIELD_SHOWTIME: {
				setShowtime(readLong(parcel));
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
	public void writeToParcel(Parcel dest, int flags) {
		if (getReservationLink() != null) {
			dest.writeInt(FIELD_RESERVATION_LINK);
			writeString(dest, getReservationLink());
		}
		if (getShowtime() != null) {
			dest.writeInt(FIELD_SHOWTIME);
			writeLong(dest, getShowtime());
		}
		if (getLang() != null) {
			dest.writeInt(FIELD_LANG);
			writeString(dest, getLang());
		}
		dest.writeInt(FIELD_END);

	}

}
