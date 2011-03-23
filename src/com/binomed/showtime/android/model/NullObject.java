package com.binomed.showtime.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NullObject implements Parcelable {

	public static final Parcelable.Creator<NullObject> CREATOR = new Creator<NullObject>() {

		@Override
		public NullObject[] newArray(int size) {
			return new NullObject[size];
		}

		@Override
		public NullObject createFromParcel(Parcel source) {
			return new NullObject(source);
		}
	};

	public NullObject() {
		super();
	}

	public NullObject(Parcel parcel) {
		this();
		readFromParcel(parcel);
	}

	private void readFromParcel(Parcel parcel) {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	}

}
