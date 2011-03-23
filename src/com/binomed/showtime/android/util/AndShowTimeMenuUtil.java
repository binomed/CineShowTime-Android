package com.binomed.showtime.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.view.Menu;

import com.binomed.showtime.android.R;
import com.binomed.showtime.android.activity.AndShowTimePreferencesActivity;
import com.binomed.showtime.android.cst.IntentShowtime;

public final class AndShowTimeMenuUtil {

	private static final Integer REQUEST_PREF = 1;

	public static void createMenu(Menu menu, int idItemPref) {
		menu.add(0, idItemPref, 0, R.string.menuPreferences).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, idItemPref + 1, 0, R.string.menuAbout).setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, idItemPref + 2, 0, R.string.menuHelp).setIcon(android.R.drawable.ic_menu_help);
	}

	public static boolean onMenuItemSelect(Activity activity, int idItemPref, int idItemSelected) {
		final int MENU_PREF = idItemPref;
		final int MENU_ABOUT = idItemPref + 1;
		final int MENU_HELP = idItemPref + 2;

		if (idItemSelected == MENU_PREF) {
			Intent launchPreferencesIntent = new Intent().setClass(activity, AndShowTimePreferencesActivity.class);

			// Make it a subactivity so we know when it returns
			activity.startActivityForResult(launchPreferencesIntent, REQUEST_PREF);
			return true;
		} else if (idItemSelected == MENU_ABOUT) {
			AlertDialog.Builder aboutDialog = new AlertDialog.Builder(activity);
			try {
				PackageInfo pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
				aboutDialog.setMessage(new StringBuilder() //
						.append(activity.getResources().getString(R.string.msgVersionCode)).append(pi.versionCode).append("\n") // //$NON-NLS-1$
						.append(activity.getResources().getString(R.string.msgVersionName)).append(pi.versionName) //
						);
			} catch (Exception e) {
			}
			aboutDialog.setCancelable(false);
			aboutDialog.setIcon(android.R.drawable.ic_menu_info_details);
			aboutDialog.setTitle(R.string.msgVersionTitle);
			aboutDialog.setNeutralButton(R.string.btnClose, null);

			// aboutDialog.create();
			aboutDialog.show();
			return true;
		} else if (idItemSelected == MENU_HELP) {
			Intent launchPreferencesIntent = IntentShowtime.createHelpAndShowTime();

			// Make it a subactivity so we know when it returns
			activity.startActivityForResult(launchPreferencesIntent, 0);
			return true;
		}
		return false;
	}

}
