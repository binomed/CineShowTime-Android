/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
		CineShowTimeWidgetHelper.updateWidget(context, null, null);
	}

}
