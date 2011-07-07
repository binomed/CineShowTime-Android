package com.binomed.showtime.android.activity;

import java.util.Calendar;

import com.binomed.showtime.android.util.activity.ICineShowTimeActivityHelperModel;

public class ModelMainFragment implements ICineShowTimeActivityHelperModel {

	// TODO à virer
	// private List<TheaterBean> favList;
	private Calendar lastRequestDate;
	private boolean nullResult;
	private boolean resetTheme;

	public ModelMainFragment() {
		super();
		nullResult = false;
		resetTheme = false;
	}

	// public List<TheaterBean> getFavList() {
	// return favList;
	// }
	//
	// public void setFavList(List<TheaterBean> favList) {
	// this.favList = favList;
	// }
	//
	public Calendar getLastRequestDate() {
		return lastRequestDate;
	}

	public void setLastRequestDate(Calendar lastRequestDate) {
		this.lastRequestDate = lastRequestDate;
	}

	@Override
	public boolean isNullResult() {
		return nullResult;
	}

	@Override
	public void setNullResult(boolean nullResult) {
		this.nullResult = nullResult;
	}

	@Override
	public boolean isResetTheme() {
		return resetTheme;
	}

	@Override
	public void setResetTheme(boolean resetTheme) {
		this.resetTheme = resetTheme;
	}

}
