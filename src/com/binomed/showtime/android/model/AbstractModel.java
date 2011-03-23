package com.binomed.showtime.android.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Parcel;
import android.os.Parcelable;

public class AbstractModel {

	private static final String NULL_STR = "NULL";
	private static final int NULL_NUMBER = -1;

	protected String readString(Parcel parcel) {
		String res = parcel.readString();
		return NULL_STR.equals(res) ? null : res;
	}

	protected float readFloat(Parcel parcel) {
		float res = parcel.readFloat();
		return (NULL_NUMBER == res) ? null : res;
	}

	protected int readInt(Parcel parcel) {
		int res = parcel.readInt();
		return (NULL_NUMBER == res) ? null : res;
	}

	protected long readLong(Parcel parcel) {
		long res = parcel.readLong();
		return (NULL_NUMBER == res) ? null : res;
	}

	protected double readDouble(Parcel parcel) {
		double res = parcel.readDouble();
		return (NULL_NUMBER == res) ? null : res;
	}

	protected boolean readBoolean(Parcel parcel) {
		return parcel.readInt() == 1;
	}

	protected <T> T readParcel(Parcel parcel, Class<T> clazz) {
		T value = null;
		try {
			value = (T) parcel.readParcelable(clazz.getClassLoader());
		} catch (ClassCastException e) {
		}
		return value;
	}

	protected <T> List<T> readList(Parcel parcel, Class<T> clazz) {
		T[] valueArray = (T[]) parcel.readParcelableArray(clazz.getClassLoader());
		return Arrays.asList(valueArray);
	}

	protected <T extends Parcelable> Map<String, T> readMap(Parcel parcel, Class<T> clazz) {
		int nbElements = parcel.readInt();
		Map<String, T> valueMap = new HashMap<String, T>();
		String key = null;
		T value = null;
		for (int i = 0; i < nbElements; i++) {
			key = parcel.readString();
			value = parcel.readParcelable(clazz.getClassLoader());
			valueMap.put(key, value);
		}
		return valueMap;
	}

	protected <T extends Parcelable> Map<String, List<T>> readMapList(Parcel parcel, Class<T> clazz) {
		int nbElements = parcel.readInt();
		Map<String, List<T>> valueMap = new HashMap<String, List<T>>();
		String key = null;
		List<T> value = null;
		for (int i = 0; i < nbElements; i++) {
			key = parcel.readString();
			value = (List<T>) Arrays.asList(parcel.readParcelableArray(clazz.getClassLoader()));
			valueMap.put(key, value);
		}
		return valueMap;
	}

	protected void writeString(Parcel dest, String value) {
		dest.writeString(value != null ? value : NULL_STR);
	}

	protected void writeFloat(Parcel dest, Float value) {
		dest.writeFloat(value != null ? value : NULL_NUMBER);
	}

	protected void writeInt(Parcel dest, Integer value) {
		dest.writeInt(value != null ? value : NULL_NUMBER);
	}

	protected void writeLong(Parcel dest, Long value) {
		dest.writeLong(value != null ? value : NULL_NUMBER);
	}

	protected void writeDouble(Parcel dest, Double value) {
		dest.writeDouble(value != null ? value : NULL_NUMBER);
	}

	protected void writeBoolean(Parcel dest, Boolean value) {
		dest.writeInt(value != null && value ? 1 : 0);
	}

	protected <T extends Parcelable> void writeList(Parcel dest, List<T> valueList, T[] filledArray, Class<T> clazz, int flags) {
		T[] valueArray = null;
		valueArray = (T[]) valueList.toArray(filledArray);
		dest.writeParcelableArray(valueArray, flags);
	}

	protected <T extends Parcelable> void writeMap(Parcel dest, Map<String, T> valueMap, int flags) {
		dest.writeInt(valueMap != null ? valueMap.size() : 0);
		if (valueMap != null) {
			for (Entry<String, T> entry : valueMap.entrySet()) {
				dest.writeString(entry.getKey());
				dest.writeParcelable(entry.getValue(), flags);
			}
		}
	}

	protected <T extends Parcelable> void writeParcelable(Parcel dest, T value, int flags) {
		if (value == null) {
			dest.writeParcelable(new NullObject(), flags);
		} else {
			dest.writeParcelable(value, flags);
		}
	}

}
