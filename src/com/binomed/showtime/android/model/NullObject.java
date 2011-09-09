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
