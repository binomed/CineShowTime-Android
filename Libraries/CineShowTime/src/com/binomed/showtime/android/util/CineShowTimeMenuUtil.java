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
package com.binomed.showtime.android.util;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.Menu;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;

public final class CineShowTimeMenuUtil {

	public static void createMenu(Menu menu, int idItemPref, int order) {
		menu.addSubMenu(0, idItemPref, order, R.string.menuPreferences).setIcon(android.R.drawable.ic_menu_preferences);
		menu.addSubMenu(0, idItemPref + 1, order + 1, R.string.menuAbout).setIcon(android.R.drawable.ic_menu_info_details);
		menu.addSubMenu(0, idItemPref + 2, order + 2, R.string.menuHelp).setIcon(android.R.drawable.ic_menu_help);
	}

	public static boolean isMapsInstalled(PackageManager packageManager) {
		boolean result = true;
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, 0);
		for (ResolveInfo info : infos) {
			result = CineShowtimeCst.MAPS_PACKAGE.equals(info.activityInfo.packageName) //
					&& CineShowtimeCst.MAPS_NAME.equals(info.activityInfo.name);
			if (result) {
				break;
			}
		}

		return result;
	}

	public static boolean isDialerInstalled(PackageManager packageManager) {
		boolean result = true;
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, 0);
		for (ResolveInfo info : infos) {
			result = CineShowtimeCst.CONTACTS_PACKAGE.equals(info.activityInfo.packageName) //
					&& CineShowtimeCst.CONTACTS_NAME.equals(info.activityInfo.name);
			if (result) {
				break;
			}
		}
		if (result) {
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:00000000"));
			infos = packageManager.queryIntentActivities(intent, 0);
			result = infos.size() > 0;
		}

		return result;
	}

	public static boolean isCalendarInstalled(PackageManager packageManager) {
		boolean result = true;
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, 0);
		for (ResolveInfo info : infos) {
			result = (CineShowtimeCst.CALENDAR_PACKAGE.equals(info.activityInfo.packageName) //
					&& CineShowtimeCst.CALENDAR_NAME.equals(info.activityInfo.name)) //
					|| (CineShowtimeCst.CALENDAR_PACKAGE_OLD.equals(info.activityInfo.packageName) //
					&& CineShowtimeCst.CALENDAR_NAME_OLD.equals(info.activityInfo.name)) //
			;
			if (result) {
				break;
			}
		}

		return result;
	}

	public static boolean manageResult(Activity activity, int requestCode, int resultCode, Intent intent) {
		boolean resultCatch = false;
		if (requestCode == CineShowtimeCst.ACTIVITY_RESULT_PREFERENCES) {
			switch (resultCode) {
			case CineShowtimeCst.RESULT_PREF_WITH_NEW_THEME:
				resultCatch = true;
				Intent originalIntent = activity.getIntent();
				originalIntent.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, true);
				CineShowTimeLayoutUtils.changeToTheme(activity, originalIntent);

				break;
			default:
				break;
			}
		}
		return resultCatch;
	}

}
