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
package com.binomed.showtime.android.widget;

import com.binomed.showtime.android.cst.CineShowtimeCst;

import android.content.Intent;

public class CineShowTimeWidgetServiceRight extends CineShowTimeWidgetService {

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		tracker.trackEvent(CineShowtimeCst.ANALYTICS_CATEGORY_WIDGET // Category
				, CineShowtimeCst.ANALYTICS_ACTION_INTERACTION // Action
				, CineShowtimeCst.ANALYTICS_VALUE_WIDGET_REFRESH // Label
				, 0 // Value
		);
		tracker.dispatch();
		tracker.stop();
	}

	
	
}
