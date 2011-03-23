package com.binomed.showtime.android.objects;

public class DBActionObject {

	private int type;

	private Object data;

	public DBActionObject(int type, Object data) {
		super();
		this.type = type;
		this.data = data;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
