package com.binomed.showtime.android.fav;

import java.util.List;

import com.binomed.showtime.android.model.TheaterBean;

public class ModelFavFragment {

	private List<TheaterBean> favList;

	// private Calendar lastRequestDate; TODO à virer
	// private boolean nullResult;
	// private boolean resetTheme;

	public ModelFavFragment() {
		super();
		// nullResult = false;
		// resetTheme = false;
	}

	public List<TheaterBean> getFavList() {
		return favList;
	}

	public void setFavList(List<TheaterBean> favList) {
		this.favList = favList;
	}

	// public Calendar getLastRequestDate() {
	// return lastRequestDate;
	// }
	//
	// public void setLastRequestDate(Calendar lastRequestDate) {
	// this.lastRequestDate = lastRequestDate;
	// }
	//
	// public boolean isNullResult() {
	// return nullResult;
	// }
	//
	// public void setNullResult(boolean nullResult) {
	// this.nullResult = nullResult;
	// }
	//
	// public boolean isResetTheme() {
	// return resetTheme;
	// }
	//
	// public void setResetTheme(boolean resetTheme) {
	// this.resetTheme = resetTheme;
	// }

}
