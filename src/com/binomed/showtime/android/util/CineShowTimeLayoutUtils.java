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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.CineShowtimeCst;
import com.binomed.showtime.android.layout.view.AutoCompleteTextWithSpeech;
import com.binomed.showtime.android.util.localisation.IListenerLocalisationUtilCallBack;
import com.binomed.showtime.android.util.localisation.IModelLocalisation;
import com.binomed.showtime.android.util.localisation.LocalisationManagement;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public final class CineShowTimeLayoutUtils {

	private static final String TAG = "AndShowTimeLayoutUtils"; //$NON-NLS-1$

	public static void manageVisibiltyFieldSpeech(Context context, ImageButton button, AutoCompleteTextView text, int idRightof, int idLeftOf, int idBelow) {
		// Manage speech button just if package present on device
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			button.setVisibility(View.GONE);

			if ((text != null) && (Integer.valueOf(Build.VERSION.SDK) <= 3)) {
				// Manage specificity for version before 4
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				if (idRightof != -1) {
					params.addRule(RelativeLayout.RIGHT_OF, idRightof);
				}
				if (idLeftOf != -1) {
					params.addRule(RelativeLayout.LEFT_OF, idLeftOf);
				}
				if (idBelow != -1) {
					params.addRule(RelativeLayout.BELOW, idBelow);
				}
				text.setSingleLine(true);
				text.setLayoutParams(params);
			}
		}
	}

	public static IListenerLocalisationUtilCallBack manageLocationManagement(Context context, GoogleAnalyticsTracker tracker, ImageView imageGps, AutoCompleteTextWithSpeech textSearch, IModelLocalisation model) {
		LocalisationManagement callBack = new LocalisationManagement(context, tracker, imageGps, textSearch, model);
		// callBack.setContext(context);
		// callBack.setImageGps(imageGps);
		// callBack.setChckBoxGps(chckBoxGps);
		// callBack.setTextSearch(textSearch);
		// callBack.setModel(model);
		// callBack.initLocalisationManager();
		return callBack;

	}

	public static InputStream manageFile(String urlImg, String fileName) {
		InputStream stream = null;
		try {
			File root = Environment.getExternalStorageDirectory();
			if (root.canWrite()) {
				if (fileName == null) {
					fileName = urlImg.substring(urlImg.lastIndexOf("/"), urlImg.length());
				}

				File posterFile = new File(root, new StringBuilder(CineShowtimeCst.FOLDER_POSTER).append(fileName).toString());
				posterFile.getParentFile().mkdirs();
				if (posterFile.exists()) {
					Log.i(TAG, "img existe");
					stream = new FileInputStream(posterFile);
				} else {
					Log.i(TAG, "img existe pas : lancement de la requete");
					HttpGet getMethod = new HttpGet();
					getMethod.setURI(new URI(urlImg));
					HttpResponse res = new DefaultHttpClient().execute(getMethod);

					FileOutputStream fileOutPut = new FileOutputStream(posterFile);
					InputStream inputStream = res.getEntity().getContent();
					byte[] tempon = new byte[10240];

					while (true) {
						int nRead = inputStream.read(tempon, 0, tempon.length);
						if (nRead <= 0) {
							break;
						}
						fileOutPut.write(tempon, 0, nRead);
					}
					fileOutPut.close();

					stream = new FileInputStream(posterFile);
					// movie.setImgStream();
				}

			} else {
				Log.i(TAG, "SD not accessible : " + urlImg);
				HttpGet getMethod = CineShowtimeFactory.getHttpGet();
				getMethod.setURI(new URI(urlImg));
				HttpResponse res = CineShowtimeFactory.getHttpClient().execute(getMethod);

				stream = res.getEntity().getContent();
			}
		} catch (IOException e) {
			Log.e(TAG, "Could not write file " + e); //$NON-NLS-1$
		} catch (URISyntaxException e) {
			Log.e(TAG, "Could not write file " + e); //$NON-NLS-1$
		}

		return stream;
	}

	public static InputStream existFile(String urlImg, String fileName) {
		InputStream stream = null;
		try {
			File root = Environment.getExternalStorageDirectory();
			if (fileName == null) {
				fileName = urlImg.substring(urlImg.lastIndexOf("/"), urlImg.length());
			}

			File posterFile = new File(root, new StringBuilder(CineShowtimeCst.FOLDER_POSTER).append(fileName).toString());
			posterFile.getParentFile().mkdirs();
			if (posterFile.exists()) {
				Log.i(TAG, "img existe");
				stream = new FileInputStream(posterFile);
			}

		} catch (IOException e) {
			Log.e(TAG, "Could not write file " + e.getMessage()); //$NON-NLS-1$
		}

		return stream;
	}

	/**
	 * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
	 */

	public static void changeToTheme(Activity activity, Intent originalIntent) {
		activity.finish();
		Intent newIntent = new Intent(activity, activity.getClass());
		newIntent.replaceExtras(originalIntent);
		activity.startActivity(newIntent);
	}

	/** Set the theme of the activity, according to the configuration. */
	public static void onActivityCreateSetTheme(Activity activity, SharedPreferences pref) {
		String defaultTheme = activity.getResources().getString(R.string.preference_gen_default_theme);
		String theme = pref.getString(activity.getResources().getString(R.string.preference_gen_key_theme), defaultTheme);
		if (theme.equals(defaultTheme)) {
			activity.setTheme(R.style.Theme_Dark_Night);
		} else {
			activity.setTheme(R.style.Theme_Shine_the_lite);
		}

	}

	public static String getColorSubMainInfo(boolean blackTheme) {
		return blackTheme ? CineShowtimeCst.COLOR_SUB_MAIN_INFO_DARK : CineShowtimeCst.COLOR_SUB_MAIN_INFO_LIGHT;
	}

	public static String getColorTimeOrDistance(boolean blackTheme) {
		return blackTheme ? CineShowtimeCst.COLOR_TIME_OR_DISTANCE_DARK : CineShowtimeCst.COLOR_TIME_OR_DISTANCE_LIGHT;
	}

	public static String getColorLang(boolean blackTheme) {
		return blackTheme ? CineShowtimeCst.COLOR_LANG_DARK : CineShowtimeCst.COLOR_LANG_LIGHT;

	}

	public static String getColorPassedShowTime(boolean blackTheme) {
		return blackTheme ? CineShowtimeCst.COLOR_PASSED_SHOWTIME_DARK : CineShowtimeCst.COLOR_PASSED_SHOWTIME_LIGHT;

	}

	public static String getColorNearestShowTime(boolean blackTheme) {
		return blackTheme ? CineShowtimeCst.COLOR_NEAREST_SHOWTIME_DARK : CineShowtimeCst.COLOR_NEAREST_SHOWTIME_LIGHT;

	}

	public static String getColorNextShowTime(boolean blackTheme) {
		return blackTheme ? CineShowtimeCst.COLOR_NEXT_SHOWTIME_DARK : CineShowtimeCst.COLOR_NEAREST_SHOWTIME_LIGHT;

	}

	public static boolean isPreferenceAnalyticsPresent(SharedPreferences pref) {
		boolean result = false;
		result = pref.getInt(CineShowtimeCst.PREF_KEY_ANALYTICS, -1) != -1;
		return result;
	}

	public static boolean isAnalyticsAthorized(SharedPreferences pref) {
		return pref.getInt(CineShowtimeCst.PREF_KEY_ANALYTICS, 0) == 1;
	}

	public static void setPreferenceAnalytics(SharedPreferences pref, boolean authorized) {
		pref.edit().putInt(CineShowtimeCst.PREF_KEY_ANALYTICS, authorized ? 1 : 0);
		pref.edit().commit();
	}
}
