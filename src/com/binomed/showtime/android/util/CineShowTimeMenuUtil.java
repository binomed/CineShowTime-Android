package com.binomed.showtime.android.util;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.Html;
import android.view.Menu;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.layout.view.AboutView;
import com.binomed.showtime.android.screen.pref.CineShowTimePreferencesActivity;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public final class CineShowTimeMenuUtil {

	public static void createMenu(Menu menu, int idItemPref, int order) {
		menu.addSubMenu(0, idItemPref, order, R.string.menuPreferences).setIcon(android.R.drawable.ic_menu_preferences);
		menu.addSubMenu(0, idItemPref + 1, order + 1, R.string.menuAbout).setIcon(android.R.drawable.ic_menu_info_details);
		menu.addSubMenu(0, idItemPref + 2, order + 2, R.string.menuHelp).setIcon(android.R.drawable.ic_menu_help);
	}

	public static boolean onMenuItemSelect(Activity activity, GoogleAnalyticsTracker tracker, int idItemPref, int idItemSelected) {
		final int MENU_PREF = idItemPref;
		final int MENU_ABOUT = idItemPref + 1;
		final int MENU_HELP = idItemPref + 2;

		if (idItemSelected == MENU_PREF) {
			tracker.trackEvent("Open", "Click", "Open preferences from " + activity.getClass().getCanonicalName(), 0);
			Intent launchPreferencesIntent = new Intent().setClass(activity, CineShowTimePreferencesActivity.class);

			// Make it a subactivity so we know when it returns
			activity.startActivityForResult(launchPreferencesIntent, CineShowtimeCst.ACTIVITY_RESULT_PREFERENCES);
			return true;
		} else if (idItemSelected == MENU_ABOUT) {
			tracker.trackEvent("Open", "Click", "Open about from " + activity.getClass().getCanonicalName(), 0);
			AlertDialog.Builder aboutDialog = new AlertDialog.Builder(activity);
			try {
				PackageInfo pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
				aboutDialog.setTitle(Html.fromHtml(new StringBuilder() //
						.append("CineShowTime ").append(pi.versionName).append("<br>") // //$NON-NLS-1$ //$NON-NLS-2$
						.toString()));
			} catch (Exception e) {
			}
			aboutDialog.setCancelable(false);
			aboutDialog.setIcon(R.drawable.icon);
			aboutDialog.setNeutralButton(R.string.btnClose, null);

			AboutView aboutView = new AboutView(activity);
			aboutDialog.setView(aboutView);

			// aboutDialog.create();
			aboutDialog.show();
			return true;
		} else if (idItemSelected == MENU_HELP) {
			tracker.trackEvent("Open", "Click", "Open help from " + activity.getClass().getCanonicalName(), 0);
			Intent launchPreferencesIntent = IntentShowtime.createHelpAndShowTime(activity);

			// Make it a subactivity so we know when it returns
			activity.startActivityForResult(launchPreferencesIntent, 0);
			return true;
		}
		return false;
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
