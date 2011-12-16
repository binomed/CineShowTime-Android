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
package com.binomed.showtime.android.util.activity;

import android.content.Context;
import android.content.SharedPreferences;

import com.binomed.showtime.android.adapter.db.CineShowtimeDbAdapter;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public interface IFragmentCineShowTimeInteraction<M extends ICineShowTimeActivityHelperModel> {

	M getModelActivity();

	GoogleAnalyticsTracker getTracker();

	SharedPreferences getPrefs();

	CineShowtimeDbAdapter getMDbHelper();

	void closeDialog();

	void openDialog();

	void openErrorDialog(int errorMsg);

	boolean isWithAdds();

	void refreshResultsIntent();

	Context getMainContext();

}
