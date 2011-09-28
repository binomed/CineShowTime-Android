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
	private static final int FIELD_FORMAT_24 = 3;
	private static final int FIELD_FORMAT_12 = 4;
	private static final int FIELD_END = -1;

	private Long showtime;

	private String lang;

	private String reservationLink;

	private String format24;
	private String format12;

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

	public String getFormat24() {
		return format24;
	}

	public void setFormat24(String format24) {
		this.format24 = format24;
	}

	public String getFormat12() {
		return format12;
	}

	public void setFormat12(String format12) {
		this.format12 = format12;
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
			case FIELD_FORMAT_24: {
				setFormat24(readString(parcel));
				break;
			}
			case FIELD_FORMAT_12: {
				setFormat12(readString(parcel));
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
		if (getFormat24() != null) {
			dest.writeInt(FIELD_FORMAT_24);
			writeString(dest, getFormat24());
		}
		if (getFormat12() != null) {
			dest.writeInt(FIELD_FORMAT_12);
			writeString(dest, getFormat12());
		}
		dest.writeInt(FIELD_END);

	}

}
