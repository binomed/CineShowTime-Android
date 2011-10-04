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

public class ReviewBean extends AbstractModel implements Parcelable {

	public static final Parcelable.Creator<ReviewBean> CREATOR = new Creator<ReviewBean>() {

		@Override
		public ReviewBean[] newArray(int size) {
			return new ReviewBean[size];
		}

		@Override
		public ReviewBean createFromParcel(Parcel source) {
			return new ReviewBean(source);
		}
	};

	public ReviewBean() {
		super();
	}

	public ReviewBean(Parcel parcel) {
		this();
		readFromParcel(parcel);
	}

	private static final int FIELD_SOURCE = 0;
	private static final int FIELD_AUTHOR = 1;
	private static final int FIELD_REVIEW = 2;
	private static final int FIELD_RATE = 3;
	private static final int FIELD_URL_REVIEW = 4;
	private static final int FIELD_END = -1;

	private String source;

	private String author;

	private String review;

	private Float rate;

	private String urlReview;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public String getUrlReview() {
		return urlReview;
	}

	public void setUrlReview(String urlReview) {
		this.urlReview = urlReview;
	}

	private void readFromParcel(Parcel parcel) {
		boolean end = false;
		int code = 0;
		while (!end) {
			code = parcel.readInt();
			switch (code) {
			case FIELD_AUTHOR: {
				setAuthor(readString(parcel));
				break;
			}
			case FIELD_RATE: {
				setRate(readFloat(parcel));
				break;
			}
			case FIELD_REVIEW: {
				setReview(readString(parcel));
				break;
			}
			case FIELD_SOURCE: {
				setSource(readString(parcel));
				break;
			}
			case FIELD_URL_REVIEW: {
				setUrlReview(readString(parcel));
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
		if (getAuthor() != null) {
			writeInt(dest, FIELD_AUTHOR);
			writeString(dest, getAuthor());
		}
		if (getRate() != null) {
			writeInt(dest, FIELD_RATE);
			writeFloat(dest, getRate());
		}
		if (getSource() != null) {
			writeInt(dest, FIELD_SOURCE);
			writeString(dest, getSource());
		}
		if (getReview() != null) {
			writeInt(dest, FIELD_REVIEW);
			writeString(dest, getReview());
		}
		if (getUrlReview() != null) {
			writeInt(dest, FIELD_URL_REVIEW);
			writeString(dest, getUrlReview());
		}
		writeInt(dest, FIELD_END);

	}

}
