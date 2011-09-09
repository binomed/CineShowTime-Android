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
package com.binomed.showtime.android.screen.fav;

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
