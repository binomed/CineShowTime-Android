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

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.service.CineShowDBGlobalService;
import com.binomed.showtime.android.util.CineShowtimeFactory;

/**
 * Define a simple widget that shows the Wiktionary "Word of the day." To build an update we spawn a background {@link Service} to perform the API queries.
 */
public class CineShowtimeWidget extends AppWidgetProvider {

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		for (int widgetId : appWidgetIds) {
			// We fill db
			Intent intentWidgetDb = new Intent(context, CineShowDBGlobalService.class);
			intentWidgetDb.putExtra(ParamIntent.SERVICE_DB_TYPE, CineShowtimeCst.DB_TYPE_WIDGET_DELETE);
			intentWidgetDb.putExtra(ParamIntent.SERVICE_DB_DATA, widgetId);
			context.startService(intentWidgetDb);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		CineShowtimeFactory.initGeocoder(context);
		for (int widgetId : appWidgetIds) {
			CineShowTimeWidgetHelper.updateWidget(context, null, null, widgetId);
		}
	}

}
