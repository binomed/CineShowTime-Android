package com.binomed.showtime.android.activity;

import java.util.Calendar;
import java.util.List;

import com.binomed.showtime.beans.TheaterBean;

public class ModelMainActivity {

	private List<TheaterBean> favList;
	private Calendar lastRequestDate;
	private boolean nullResult;
	private boolean resetTheme;

	public ModelMainActivity() {
		super();
		nullResult = false;
		resetTheme = false;
	}

	public List<TheaterBean> getFavList() {
		return favList;
	}

	public void setFavList(List<TheaterBean> favList) {
		this.favList = favList;
	}

	public Calendar getLastRequestDate() {
		return lastRequestDate;
	}

	public void setLastRequestDate(Calendar lastRequestDate) {
		this.lastRequestDate = lastRequestDate;
	}

	public boolean isNullResult() {
		return nullResult;
	}

	public void setNullResult(boolean nullResult) {
		this.nullResult = nullResult;
	}

	public boolean isResetTheme() {
		return resetTheme;
	}

	public void setResetTheme(boolean resetTheme) {
		this.resetTheme = resetTheme;
	}

}
