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
import com.binomed.showtime.android.activity.AndShowTimePreferencesActivity;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.IntentShowtime;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.layout.view.AboutView;

public final class AndShowTimeMenuUtil {

	public static void createMenu(Menu menu, int idItemPref, int order) {
		menu.addSubMenu(0, idItemPref, order, R.string.menuPreferences).setIcon(android.R.drawable.ic_menu_preferences);
		menu.addSubMenu(0, idItemPref + 1, order + 1, R.string.menuAbout).setIcon(android.R.drawable.ic_menu_info_details);
		menu.addSubMenu(0, idItemPref + 2, order + 2, R.string.menuHelp).setIcon(android.R.drawable.ic_menu_help);
	}

	public static boolean onMenuItemSelect(Activity activity, int idItemPref, int idItemSelected) {
		final int MENU_PREF = idItemPref;
		final int MENU_ABOUT = idItemPref + 1;
		final int MENU_HELP = idItemPref + 2;

		if (idItemSelected == MENU_PREF) {
			Intent launchPreferencesIntent = new Intent().setClass(activity, AndShowTimePreferencesActivity.class);

			// Make it a subactivity so we know when it returns
			activity.startActivityForResult(launchPreferencesIntent, AndShowtimeCst.ACTIVITY_RESULT_PREFERENCES);
			return true;
		} else if (idItemSelected == MENU_ABOUT) {
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
			result = AndShowtimeCst.MAPS_PACKAGE.equals(info.activityInfo.packageName) //
					&& AndShowtimeCst.MAPS_NAME.equals(info.activityInfo.name);
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
			result = AndShowtimeCst.CONTACTS_PACKAGE.equals(info.activityInfo.packageName) //
					&& AndShowtimeCst.CONTACTS_NAME.equals(info.activityInfo.name);
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
			result = (AndShowtimeCst.CALENDAR_PACKAGE.equals(info.activityInfo.packageName) //
					&& AndShowtimeCst.CALENDAR_NAME.equals(info.activityInfo.name)) //
					|| (AndShowtimeCst.CALENDAR_PACKAGE_OLD.equals(info.activityInfo.packageName) //
					&& AndShowtimeCst.CALENDAR_NAME_OLD.equals(info.activityInfo.name)) //
			;
			if (result) {
				break;
			}
		}

		return result;
	}

	public static boolean manageResult(Activity activity, int requestCode, int resultCode, Intent intent) {
		boolean resultCatch = false;
		if (requestCode == AndShowtimeCst.ACTIVITY_RESULT_PREFERENCES) {
			switch (resultCode) {
			case AndShowtimeCst.RESULT_PREF_WITH_NEW_THEME:
				resultCatch = true;
				Intent originalIntent = activity.getIntent();
				originalIntent.putExtra(ParamIntent.PREFERENCE_RESULT_THEME, true);
				AndShowTimeLayoutUtils.changeToTheme(activity, originalIntent);

				break;
			default:
				break;
			}
		}
		return resultCatch;
	}

}
